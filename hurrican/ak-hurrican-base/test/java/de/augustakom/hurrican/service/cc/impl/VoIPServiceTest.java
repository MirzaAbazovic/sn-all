/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.06.2011 16:14:42
 */
package de.augustakom.hurrican.service.cc.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.apache.log4j.Logger;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.dao.cc.AuftragVoIPDNDAO;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragVoIP;
import de.augustakom.hurrican.model.cc.AuftragVoIPDN;
import de.augustakom.hurrican.model.cc.AuftragVoIPDN2EGPort;
import de.augustakom.hurrican.model.cc.AuftragVoIPDNBuilder;
import de.augustakom.hurrican.model.cc.EndgeraetPort;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.ReferenceBuilder;
import de.augustakom.hurrican.model.cc.VoipDn2DnBlockView;
import de.augustakom.hurrican.model.cc.VoipDnBlock;
import de.augustakom.hurrican.model.cc.VoipDnPlan;
import de.augustakom.hurrican.model.cc.VoipDnPlanBuilder;
import de.augustakom.hurrican.model.cc.VoipDnPlanView;
import de.augustakom.hurrican.model.cc.view.AuftragVoipDNViewBuilder;
import de.augustakom.hurrican.model.cc.view.AuftragVoipDNViewBuilder.SelectedPortsBuilder;
import de.augustakom.hurrican.model.shared.view.voip.AuftragVoipDNView;
import de.augustakom.hurrican.model.shared.view.voip.BlockDNView;
import de.augustakom.hurrican.model.shared.view.voip.SelectedPortsView;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.VoIPService;

/**
 * Service Tests zur Validierung der {@code VoIPServiceImpl}
 */
@Test(groups = BaseTest.SERVICE)
public class VoIPServiceTest extends AbstractHurricanBaseServiceTest {

    private static final Logger LOGGER = Logger.getLogger(VoIPServiceTest.class);

    /**
     * System under test
     */
    private VoIPService sut;

    private AuftragVoIPDNDAO auftragVoIPDnDao;

    @BeforeMethod
    public void setup() {
        sut = getCCService(VoIPService.class);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateVoIP4Auftrag() {
        try {
            AuftragVoIP result = sut.createVoIP4Auftrag((long) 193009, getSessionId());

            Assert.assertNotNull(result, "Es wurde kein VoIP-Zusatz generiert!");
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testFindVoIP4Auftrag() {
        try {
            Long auftragId = (long) 123456;

            sut.createVoIP4Auftrag(auftragId, getSessionId());
            AuftragVoIP result = sut.findVoIP4Auftrag(auftragId);

            Assert.assertNotNull(result, "Es wurde kein VoIP-Zusatz fuer den Auftrag gefunden: " + auftragId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testCreateVoIPDN4Auftrag() {
        try {
            AuftragVoIPDN result = sut.createVoIPDN4Auftrag((long) 124951, (long) 193012, "abcd");

            Assert.assertNotNull(result, "Es wurde kein VoIPDN-Zusatz generiert!");
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            Assert.fail(e.getMessage());
        }
    }

    @DataProvider
    public Object[][] voipLoginDatenProvider() {
        return new Object[][] {
                { new VoipDnBlock("0", "10", true), new VoipDnBlock("0", "10", true), "login", "ngn.m-online.net", "+498912345610", "login", "+498912345610" },
                { new VoipDnBlock("0", "10", false), new VoipDnBlock("1", "10", false), "login", "ngn.m-online.net", "+498912345600", "+498912345600", "+49891234561" },
                { new VoipDnBlock("0", "10", true), new VoipDnBlock("5", "10", true), "login", "ngn.m-online.net", "+49891234560", "+49891234565", "+49891234565" },
                { new VoipDnBlock("0", "10", false), new VoipDnBlock("5", "10", true), "login", "ngn.m-online.net", "+49891234560", "+49891234565", "+49891234565" },
                { new VoipDnBlock("0", "10", false), new VoipDnBlock("0", "10", true), "login", "ngn.m-online.net", "+498912345610", "+49891234560", "+49891234560" },
                { new VoipDnBlock("0", "10", true), new VoipDnBlock("0", "10", false), "login", "ngn.m-online.net", "+49891234560", "+498912345600", "+49891234560" }
        };
    }

    @Test(dataProvider = "voipLoginDatenProvider")
    public void createVoIPLoginDaten(VoipDnBlock activeBlock, VoipDnBlock newBlock,
            String activeSipLogin, String activeSipDomain, String activeSipHauptrufnummer,
            String expectedSipLogin, String expectedSipHauptrufnummer) throws StoreException {
        try {
            AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class);

            VoipDnPlan veryOldPlan = new VoipDnPlanBuilder()
                    .withVoipDnBlocks(Collections.singletonList(new VoipDnBlock("0", "10", true)))
                    .withGueltigAb(Date.from(LocalDate.now().minusYears(1).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                    .withSipLogin("veryOld@" + activeSipDomain)
                    .withSipHauptrufnummer("+491234567")
                    .build();

            VoipDnPlan activePlan = new VoipDnPlanBuilder()
                    .withVoipDnBlocks(Collections.singletonList(activeBlock))
                    .withGueltigAb(Date.from(LocalDate.now().minusWeeks(1).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                    .withSipLogin(activeSipLogin + "@" + activeSipDomain)
                    .withSipHauptrufnummer(activeSipHauptrufnummer)
                    .build();

            VoipDnPlan newPlan = new VoipDnPlanBuilder()
                    .withVoipDnBlocks(Collections.singletonList(newBlock))
                    .withGueltigAb(Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                    .build();

            AuftragVoIPDN auftragVoIPDN = new AuftragVoIPDNBuilder().withAuftragBuilder(auftragBuilder)
                    .withDnNoOrig(3456789L)
                    .withRufnummernplaene(Arrays.asList(veryOldPlan, activePlan, newPlan))
                    .withSipDomain(getBuilder(ReferenceBuilder.class)
                            .withStrValue(activeSipDomain)
                            .withType(Reference.REF_TYPE_SIP_DOMAIN_TYPE)
                            .build())
                    .build();

            sut.saveAuftragVoIPDN(auftragVoIPDN);

            VoipDnPlan plan = auftragVoIPDN.getActiveRufnummernplan(Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
            Assert.assertNotNull(plan, String.format("Missing voipDn plan for voipDn: %s", auftragVoIPDN.getDnNoOrig()));

            sut.createVoIPLoginDaten(plan, activePlan, "089", "123456", "00", auftragVoIPDN.getSipDomain().getStrValue());

            Assert.assertEquals(plan.getSipLogin(), expectedSipLogin + "@" + activeSipDomain);
            Assert.assertEquals(plan.getSipHauptrufnummer(), expectedSipHauptrufnummer);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void createVoIPLoginDatenNoActivePlan() {
        try {
            AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class);

            VoipDnPlan newPlan = new VoipDnPlanBuilder()
                    .withVoipDnBlocks(Collections.singletonList(new VoipDnBlock("0", "10", false)))
                    .withGueltigAb(Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                    .build();

            AuftragVoIPDN auftragVoIPDN = new AuftragVoIPDNBuilder().withAuftragBuilder(auftragBuilder)
                    .withDnNoOrig(3456789L)
                    .withRufnummernplaene(Arrays.asList(newPlan))
                    .withSipDomain(getBuilder(ReferenceBuilder.class)
                            .withStrValue("ngn.m-online.net")
                            .withType(Reference.REF_TYPE_SIP_DOMAIN_TYPE)
                            .build())
                    .build();
            sut.saveAuftragVoIPDN(auftragVoIPDN);

            VoipDnPlan plan = auftragVoIPDN.getActiveRufnummernplan(Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
            Assert.assertNotNull(plan, String.format("Missing voipDn plan for voipDn: %s", auftragVoIPDN.getDnNoOrig()));

            sut.createVoIPLoginDaten(plan, null, "089", "123456", "00", auftragVoIPDN.getSipDomain().getStrValue());

            Assert.assertEquals(plan.getSipLogin(), "+498912345600@ngn.m-online.net");
            Assert.assertEquals(plan.getSipHauptrufnummer(), "+49891234560");
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void createVoIPLoginDatenNoOverwrite() {
        try {
            AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class);

            VoipDnPlan activePlan = new VoipDnPlanBuilder()
                    .withVoipDnBlocks(Collections.singletonList(new VoipDnBlock("0", "10", false)))
                    .withGueltigAb(Date.from(LocalDate.now().minusWeeks(1).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                    .withSipLogin("+498912345600@ngn.m-online.net")
                    .withSipHauptrufnummer("+49891234560")
                    .build();

            VoipDnPlan newPlan = new VoipDnPlanBuilder()
                    .withVoipDnBlocks(Collections.singletonList(new VoipDnBlock("0", "10", false)))
                    .withGueltigAb(Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                    .withSipLogin("login@ngn.m-online.net")
                    .withSipHauptrufnummer("+498912345601")
                    .build();

            AuftragVoIPDN auftragVoIPDN = new AuftragVoIPDNBuilder().withAuftragBuilder(auftragBuilder)
                    .withDnNoOrig(3456789L)
                    .withRufnummernplaene(Arrays.asList(activePlan, newPlan))
                    .withSipDomain(getBuilder(ReferenceBuilder.class)
                            .withStrValue("ngn.m-online.net")
                            .withType(Reference.REF_TYPE_SIP_DOMAIN_TYPE)
                            .build())
                    .build();
            sut.saveAuftragVoIPDN(auftragVoIPDN);

            VoipDnPlan plan = auftragVoIPDN.getActiveRufnummernplan(Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
            Assert.assertNotNull(plan, String.format("Missing voipDn plan for voipDn: %s", auftragVoIPDN.getDnNoOrig()));

            sut.createVoIPLoginDaten(plan, activePlan, "089", "123456", "00", auftragVoIPDN.getSipDomain().getStrValue());

            Assert.assertEquals(plan.getSipLogin(), "login@ngn.m-online.net");
            Assert.assertEquals(plan.getSipHauptrufnummer(), "+498912345601");
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void saveAuftragVoIPDN_GoodCase() throws StoreException {
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class);
        AuftragVoIPDN toSave = new AuftragVoIPDNBuilder().withAuftragBuilder(auftragBuilder)
                .withDnNoOrig(12341234L).build();
        sut.saveAuftragVoIPDN(toSave);
        final Long id = toSave.getId();
        assertNotNull(id);
    }

    @Test
    public void testFindVoIPDN4AuftragDn() throws FindException {
        Long dnNoOrig = (long) 914761;
        AuftragVoIPDNBuilder auftragVoIPDNBuilder = getBuilder(AuftragVoIPDNBuilder.class);
        AuftragVoIPDN auftragVoIPDN = auftragVoIPDNBuilder.withAuftragBuilder(getBuilder(AuftragBuilder.class))
                .withDnNoOrig(dnNoOrig).build();

        AuftragVoIPDN result = sut.findByAuftragIDDN(auftragVoIPDN.getAuftragId(), dnNoOrig);
        Assert.assertNotNull(result, "Es wurde kein VoIPDN-Zusatz fuer den Auftrag und die DN gefunden: "
                + auftragVoIPDN.getAuftragId() + ", " + dnNoOrig);
    }

    @Test
    public void testSavePortAssignmentsOnVoIPDNs_success() throws Exception {
        final Integer selectedPortNumber = 1;
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class);
        AuftragVoIPDN auftragVoIpDn = getBuilder(AuftragVoIPDNBuilder.class)
                .withAuftragBuilder(auftragBuilder)
                .withDnNoOrig(1234L)
                .build();
        @SuppressWarnings("unused")
        AuftragVoipDNView viewWithPorts = new AuftragVoipDNViewBuilder()
                .withAuftragId(auftragVoIpDn.getAuftragId())
                .withDnNoOrig(auftragVoIpDn.getDnNoOrig())
                .withSipDomain(getBuilder(ReferenceBuilder.class)
                        .withStrValue("ngn.m-online.net")
                        .withType(Reference.REF_TYPE_SIP_DOMAIN_TYPE)
                        .build())
                .addSelectedPorts(new SelectedPortsBuilder(new boolean[1], new Date(), DateTools.getHurricanEndDate())
                        .withPortNrSelected(1))
                .withSipPassword("asdf5")
                .build();

        sut.saveAuftragVoIPDNs(Arrays.asList(viewWithPorts));

        final List<AuftragVoIPDN2EGPort> auftragVoIPDN2EGPorts = getAuftragVoIPDNDAO()
                .findByProperty(AuftragVoIPDN2EGPort.class, "auftragVoipDnId", auftragVoIpDn.getId());

        assertEquals(Iterables.getOnlyElement(auftragVoIPDN2EGPorts).getEgPort().getNumber(), selectedPortNumber);
    }

    @Test
    public void testSavePortAssignmentsOnVoIPDNs_Fails() throws Exception {
        final AuftragVoIPDN auftragVoIpDn = getBuilder(AuftragVoIPDNBuilder.class)
                .withAuftragBuilder(getBuilder(AuftragBuilder.class))
                .withDnNoOrig(815L)
                .build();

        AuftragVoipDNView viewWithPorts = new AuftragVoipDNView();
        viewWithPorts.setSipDomain(null);
        viewWithPorts.setAuftragId(auftragVoIpDn.getAuftragId());
        viewWithPorts.setDnNoOrig(auftragVoIpDn.getDnNoOrig());

        try {
            sut.saveAuftragVoIPDNs(Arrays.asList(viewWithPorts));
            Assert.fail("expected StoreException because of missing sip-domain");
        }
        catch (StoreException e) {
            assertTrue(e.getMessage().contains("SIP-Domäne"));
        }
    }

    @DataProvider(name = "hauprufnrToPortAssignment")
    public Object[][] validateHauprufnrToPortAssignmentDataProvider() {
        return new Object[][] {
                new Object[] {
                        Collections.emptyList(), true },
                new Object[] {
                        // Keine Ports konfiguriert -> OK
                        Arrays.asList(new AuftragVoipDNViewBuilder()
                                .withMainNumber(true)
                                .addSelectedPorts(
                                        new SelectedPortsBuilder(Collections.<AuftragVoIPDN2EGPort>emptyList(),
                                                new Date(), DateTools.getHurricanEndDate(), 0)
                                ).build()),
                        true },
                new Object[] {
                        // Eine Hauptrufnummer einem Port zugeordnet -> OK
                        Arrays.asList(new AuftragVoipDNViewBuilder()
                                .withMainNumber(true)
                                .addSelectedPorts(new SelectedPortsBuilder(new boolean[1], new Date(),
                                        DateTools.getHurricanEndDate())
                                        .withPortNrSelected(1))
                                .build()),
                        true },
                new Object[] {
                        // Eine Hauptrufnummer als Block einem Port zugeordnet -> OK (kommt das vor?)
                        Arrays.asList(new AuftragVoipDNViewBuilder()
                                .withBlock(new BlockDNView("test", "test", "test"))
                                .withMainNumber(true)
                                .addSelectedPorts(
                                        new SelectedPortsBuilder(new boolean[1], new Date(), DateTools
                                                .getHurricanEndDate())
                                                .withPortNrSelected(1)
                                )
                                .build()),
                        true },
                new Object[] {
                        // Zwei Hauptrufnummern einem Port zugeordnet -> BAD
                        Arrays.asList(
                                new AuftragVoipDNViewBuilder()
                                        .withMainNumber(true)
                                        .addSelectedPorts(
                                                new SelectedPortsBuilder(new boolean[1], new Date(), DateTools
                                                        .getHurricanEndDate())
                                                        .withPortNrSelected(1)
                                        )
                                        .build(),
                                new AuftragVoipDNViewBuilder()
                                        .withMainNumber(true)
                                        .addSelectedPorts(new SelectedPortsBuilder(new boolean[1], new Date(),
                                                DateTools.getHurricanEndDate())
                                                .withPortNrSelected(1))
                                        .build()
                        ),
                        false },
                new Object[] {
                        // Eine Hauptrufnummer keinem Port zugeordnet -> BAD
                        Arrays.asList(new AuftragVoipDNViewBuilder()
                                .withMainNumber(true)
                                .addSelectedPorts(
                                        new SelectedPortsBuilder(new boolean[1], new Date(),
                                                DateTools.getHurricanEndDate())
                                )
                                .build()),
                        false } };
    }

    @Test(dataProvider = "hauprufnrToPortAssignment")
    public void testValidateHauprufnrToPortAssignment(Collection<AuftragVoipDNView> auftragVoipDnViews, boolean expected) {
        assertEquals(sut.validateHauprufnrToPortAssignment(auftragVoipDnViews), expected);
    }

    @DataProvider(name = "rufnrCountToPortAssignment")
    public Object[][] validateRufnrCountToPortAssignmenDataProvider() {
        final int countAuftragVoipDNViews = EndgeraetPort.getMaxDNsPerDefaultPort() + 1;

        final List<AuftragVoipDNView> moreThanMaxAuftragVoipDnsPerPort =
                Lists.newArrayListWithCapacity(countAuftragVoipDNViews);

        for (int i = 0; i < countAuftragVoipDNViews; i++) {
            moreThanMaxAuftragVoipDnsPerPort.add(
                    new AuftragVoipDNViewBuilder()
                            .addSelectedPorts(new SelectedPortsBuilder(
                                    new boolean[1], new Date(), DateTools.getHurricanEndDate())
                                    .withPortNrSelected(1))
                            .build()
            );
        }

        return new Object[][] {
                new Object[] {
                        Collections.emptyList(), true },
                new Object[] {
                        // Keine Ports konfiguriert -> OK
                        Arrays.asList(new AuftragVoipDNViewBuilder().addSelectedPorts(
                                new SelectedPortsBuilder(new boolean[1], new Date(),
                                        DateTools.getHurricanEndDate())
                        )
                                .build()),
                        true },
                new Object[] {
                        // Ein Port, Port zugeordnet -> OK
                        Arrays.asList(new AuftragVoipDNViewBuilder().addSelectedPorts(new SelectedPortsBuilder(
                                new boolean[1], new Date(), DateTools.getHurricanEndDate())
                                .withPortNrSelected(1))
                                .build()),
                        true },
                new Object[] {
                        // Ein Port, mehr Zuordnungen als erlaubt -> BAD
                        moreThanMaxAuftragVoipDnsPerPort,
                        false } };
    }

    @Test(dataProvider = "rufnrCountToPortAssignment")
    public void testValidateRufnrCountToPortAssignment(Collection<AuftragVoipDNView> auftragVoipDnViews,
            boolean expected) {
        assertEquals(sut.validateRufnrCountToPortAssignment(auftragVoipDnViews), expected);
    }

    @DataProvider(name = "portToDNAssignment")
    public Object[][] validatePortToRufnrAssignmenDataProvider() {
        return new Object[][] {
                new Object[] {
                        Collections.emptyList(), true },
                new Object[] {
                        // Keine Ports konfiguriert -> OK
                        Arrays.asList(new AuftragVoipDNViewBuilder().addSelectedPorts(
                                new SelectedPortsBuilder(new boolean[0], new Date(), DateTools.getHurricanEndDate()))
                                .build()), true },
                new Object[] {
                        // Ein Port, Port zugeordnet -> OK
                        Arrays.asList(new AuftragVoipDNViewBuilder().addSelectedPorts(
                                new SelectedPortsBuilder(new boolean[1], new Date(), DateTools.getHurricanEndDate())
                                        .withPortNrSelected(1)
                        )
                                .build()),
                        true },
                new Object[] {
                        // Ein Port, nicht zugeordnet -> BAD
                        Arrays.asList(new AuftragVoipDNViewBuilder()
                                .addSelectedPorts(new SelectedPortsBuilder(new boolean[1], new Date(),
                                        DateTools.getHurricanEndDate()))
                                .build()),
                        false } };
    }

    @Test(dataProvider = "portToDNAssignment")
    public void testValidatePortToDNAssignment(Collection<AuftragVoipDNView> views, boolean expected) {
        assertEquals(sut.validatePortToDNAssignment(views), expected);
    }

    @Test(expectedExceptions = IllegalArgumentException.class, groups = { BaseTest.SERVICE })
    public void checkDNBlocks_withDNsAndBlocks() throws IllegalArgumentException {
        List<AuftragVoipDNView> views = new ArrayList<>();
        AuftragVoipDNView view1 = new AuftragVoipDNViewBuilder()
                .withBlock(new BlockDNView("a", "b", "c"))
                .build();
        AuftragVoipDNView view2 = new AuftragVoipDNViewBuilder()
                .build();
        views.add(view1);
        views.add(view2);
        sut.checkDNBlocks(views);
    }

    @Test
    public void checkDNBlocks_withDNsOnly() throws IllegalArgumentException {
        List<AuftragVoipDNView> views = new ArrayList<>();
        AuftragVoipDNView view1 = new AuftragVoipDNViewBuilder()
                .build();
        AuftragVoipDNView view2 = new AuftragVoipDNViewBuilder()
                .build();
        views.add(view1);
        views.add(view2);
        assertEquals(sut.checkDNBlocks(views), false);
    }

    @Test
    public void checkDNBlocks_withBlocksOnly() throws IllegalArgumentException {
        List<AuftragVoipDNView> views = new ArrayList<>();
        AuftragVoipDNView view1 = new AuftragVoipDNViewBuilder()
                .withBlock(new BlockDNView("a", "a", "a"))
                .build();
        AuftragVoipDNView view2 = new AuftragVoipDNViewBuilder()
                .withBlock(new BlockDNView("b", "b", "b"))
                .build();
        views.add(view1);
        views.add(view2);
        assertEquals(sut.checkDNBlocks(views), true);
    }

    @Test
    public void validatePortAssignment_withEmptyList() {
        List<AuftragVoipDNView> views = new ArrayList<>();
        assertTrue(sut.validatePortAssignment(views).isEmpty());
    }

    @Test
    void validatePortAssignment_withBlocksOnly_failed() {
        List<AuftragVoipDNView> views = new ArrayList<>();
        AuftragVoipDNView view1 = new AuftragVoipDNViewBuilder()
                .withBlock(new BlockDNView("a", "a", "a"))
                .addSelectedPorts(new SelectedPortsBuilder(new boolean[1], new Date(), DateTools.getHurricanEndDate()))
                .build();
        views.add(view1);
        assertEquals(sut.validatePortAssignment(views).getAKMessages().size(), 1);
    }

    @Test
    void validatePortAssignment_withRufnummer_failed() {
        List<AuftragVoipDNView> views = new ArrayList<>();
        AuftragVoipDNView view1 = new AuftragVoipDNViewBuilder()
                .withMainNumber(true)
                .addSelectedPorts(new SelectedPortsBuilder(new boolean[1], new Date(), DateTools.getHurricanEndDate()))
                .build();
        views.add(view1);
        assertEquals(sut.validatePortAssignment(views).getAKMessages().size(), 2);
    }

    @DataProvider
    Object[][] portzuordnungEindeutigDataProvider() {
        final Date today = new Date(), yesterday = Date.from(LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                tomorrow = Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        return new Object[][] {
                {
                        ImmutableList.of(
                                new SelectedPortsBuilder(new boolean[2], yesterday, tomorrow)
                                        .withPortNrSelected(1)
                                        .build(),
                                new SelectedPortsBuilder(new boolean[2], today, DateTools.getHurricanEndDate())
                                        .withPortNrSelected(2)
                                        .build()
                        ),
                        false
                },
                {
                        ImmutableList.of(
                                new SelectedPortsBuilder(new boolean[2], today, DateTools.getHurricanEndDate())
                                        .withPortNrSelected(1)
                                        .build(),
                                new SelectedPortsBuilder(new boolean[2], yesterday, tomorrow)
                                        .withPortNrSelected(2)
                                        .build()
                        ),
                        false
                },
                {
                        ImmutableList.of(
                                new SelectedPortsBuilder(new boolean[2], today, today)
                                        .withPortNrSelected(1)
                                        .build(),
                                new SelectedPortsBuilder(new boolean[2], today, DateTools.getHurricanEndDate())
                                        .withPortNrSelected(2)
                                        .build()
                        ),
                        true
                },
                {
                        ImmutableList.of(
                                new SelectedPortsBuilder(new boolean[3], yesterday, today)
                                        .withPortNrSelected(1)
                                        .build(),
                                new SelectedPortsBuilder(new boolean[3], tomorrow, DateTools.getHurricanEndDate())
                                        .withPortNrSelected(2)
                                        .build(),
                                new SelectedPortsBuilder(new boolean[3], today, tomorrow)
                                        .withPortNrSelected(3)
                                        .build()
                        ),
                        true
                },
        };
    }

    @Test(dataProvider = "portzuordnungEindeutigDataProvider")
    public void testPortzuordnungEindeutig(final List<SelectedPortsView> selectedPortsViews,
            final boolean expectedResult) {
        final AuftragVoipDNView auftragVoipDNView = new AuftragVoipDNView();
        selectedPortsViews.forEach(auftragVoipDNView::addSelectedPort);
        assertThat(sut.validatePortzuordnungEindeutig(ImmutableList.of(auftragVoipDNView)), equalTo(expectedResult));
    }

    private AuftragVoIPDNDAO getAuftragVoIPDNDAO() {
        if (auftragVoIPDnDao == null) {
            auftragVoIPDnDao = (AuftragVoIPDNDAO) getBean("auftragVoIPDNDAO");
        }
        return auftragVoIPDnDao;
    }

    @Test
    public void testUpdateRufnummernplaene_UpdateExisting() throws StoreException {
        final VoipDnPlan voipDnPlan = new VoipDnPlanBuilder()
                .withVoipDnBlocks(Lists.newArrayList(new VoipDnBlock("1", "100", false)))
                .build();
        final AuftragVoIPDN auftragVoIPDN = getBuilder(AuftragVoIPDNBuilder.class)
                .withRufnummernplaene(Lists.newArrayList(voipDnPlan))
                .build();
        final VoipDnPlan voipDnPlanNeu = new VoipDnPlanBuilder()
                .withId(voipDnPlan.getId())
                .withVoipDnBlocks(Lists.newArrayList(new VoipDnBlock("1", "100", false)))
                .build();
        final AuftragVoipDNView auftragVoipDNView = new AuftragVoipDNViewBuilder()
                .withVoipDnPlanViews(Lists.newArrayList(new VoipDnPlanView("", "", voipDnPlanNeu)))
                .build();
        auftragVoipDNView.getVoipDnPlanViewsDesc().get(0)
                .addVoipDn2BlockView(new VoipDn2DnBlockView("", "", new VoipDnBlock("0", null, true)));
        sut.updateRufnummernplaene(auftragVoipDNView, auftragVoIPDN, false);
        assertThat(voipDnPlan.getVoipDnBlocks(), hasSize(2));
    }

    @Test
    public void testUpdateRufnummernplaene_DeleteRemoved() throws StoreException {
        final VoipDnPlan voipDnPlan = new VoipDnPlanBuilder()
                .withRandomId()
                .withGueltigAb(new Date())
                .build();
        final VoipDnPlan voipDnPlanToRemove1 = new VoipDnPlanBuilder()
                .withRandomId()
                .withGueltigAb(new Date())
                .build();
        final VoipDnPlan voipDnPlanToRemove2 = new VoipDnPlanBuilder()
                .withRandomId()
                .withGueltigAb(Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .build();
        final VoipDnPlan voipDnPlanNotToRemove = new VoipDnPlanBuilder()
                .withRandomId()
                .withGueltigAb(Date.from(LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .build();

        final AuftragVoIPDN auftragVoIPDN = new AuftragVoIPDNBuilder()
                .withRandomId()
                .withRufnummernplaene(
                        Lists.newArrayList(voipDnPlan, voipDnPlanToRemove1, voipDnPlanToRemove2, voipDnPlanNotToRemove))
                .build();

        final AuftragVoipDNView auftragVoipDNView = new AuftragVoipDNViewBuilder()
                .withVoipDnPlanViews(Lists.newArrayList(new VoipDnPlanView("", "", voipDnPlan)))
                .build();

        sut.updateRufnummernplaene(auftragVoipDNView, auftragVoIPDN, false);
        assertThat(auftragVoIPDN.getRufnummernplaene(), hasSize(2));
    }
}
