/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.09.14
 */
package de.augustakom.hurrican.service.cc.ffm.command;

import static org.mockito.Mockito.*;

import java.util.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.dao.cc.ffm.FfmDAO;
import de.augustakom.hurrican.exceptions.FFMServiceException;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlass;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlassBuilder;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.ProduktBuilder;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.TechLeistungBuilder;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnungBuilder;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.enums.FfmTyp;
import de.augustakom.hurrican.model.cc.ffm.FfmProductMapping;
import de.augustakom.hurrican.model.cc.ffm.FfmProductMappingBuilder;
import de.augustakom.hurrican.model.cc.ffm.FfmQualification;
import de.augustakom.hurrican.model.cc.ffm.FfmQualificationBuilder;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.OEService;
import de.augustakom.hurrican.service.cc.BAConfigService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.ffm.FFMService;
import de.mnet.esb.cdm.resource.workforceservice.v1.WorkforceService;

@Test(groups = BaseTest.UNIT)
public class AggregateFfmHeaderIdsCommandTest extends AbstractAggregateFfmCommandTest {

    private static final String DISPLAY_ID_PATTERN = "%s-[0-9]{4}-[0-9]{2}-[0-9]{2}-[0-9]{2}:[0-9]{2}:[0-9]{2}.[0-9]{3}";

    @Mock
    private CCAuftragService ccAuftragService;
    @Mock
    private PhysikService physikService;
    @Mock
    private EndstellenService endstellenService;
    @Mock
    private HVTService hvtService;
    @Mock
    private WorkforceService workforceService;
    @Mock
    private FfmDAO ffmDAO;
    @Mock
    private ProduktService produktService;
    @Mock
    private OEService oeService;
    @Mock
    private FFMService ffmService;
    @Mock
    private BAConfigService baConfigService;

    @InjectMocks
    @Spy
    private AggregateFfmHeaderIdsCommand testling;

    @BeforeMethod
    public void setUp() {
        testling = new AggregateFfmHeaderIdsCommand();
        prepareFfmCommand(testling);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testExecuteFailure() throws Exception {
        doThrow(new FFMServiceException("failure")).when(testling).createFfmUniqueId();

        Object result = testling.execute();

        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof ServiceCommandResult);
        Assert.assertFalse(((ServiceCommandResult) result).isOk());
    }

    @Test
    public void testExecute() throws Exception {
        doReturn("HUR_uniqueId").when(testling).createFfmUniqueId();
        doReturn("DisplayId").when(testling).createFfmDisplayId();
        doReturn("CustomerOrderId").when(testling).getCustomerOrder();
        doReturn("Produkt").when(testling).getProduktName();
        BAVerlaufAnlass baVerlaufAnlass =
                new BAVerlaufAnlassBuilder()
                        .setPersist(false)
                        .withName("Anschlussuebernahme")
                        .build();
        doReturn(Optional.of(baVerlaufAnlass)).when(testling).getBauauftragAnlass();

        Object result = testling.execute();

        verify(testling).getBauauftragAnlass();
        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof ServiceCommandResult);
        Assert.assertTrue(((ServiceCommandResult) result).isOk());

        Assert.assertEquals(workforceOrder.getId(), "HUR_uniqueId");
        Assert.assertEquals(workforceOrder.getDisplayId(), "DisplayId");
        Assert.assertEquals(workforceOrder.getCustomerOrderId(), "CustomerOrderId");
        Assert.assertEquals(workforceOrder.getType(), "Produkt");
        Assert.assertEquals(workforceOrder.getActivityType(), ffmProductMapping.getFfmActivityType());
        Assert.assertEquals(workforceOrder.getActivitySubtype(), baVerlaufAnlass.getName());
        Assert.assertEquals(new Integer(workforceOrder.getPlannedDuration().intValue()), ffmProductMapping.getFfmPlannedDuration());
        Assert.assertNotNull(workforceOrder.getQualification());
        Assert.assertEquals(workforceOrder.getQualification().size(), 0);
    }

    @Test
    public void testAdditionalDurationFromQualifications() throws Exception {
        doReturn("HUR_uniqueId").when(testling).createFfmUniqueId();
        doReturn("DisplayId").when(testling).createFfmDisplayId();
        doReturn("CustomerOrderId").when(testling).getCustomerOrder();
        doReturn("Produkt").when(testling).getProduktName();
        doReturn(Optional.empty()).when(testling).getBauauftragAnlass();

        FfmQualification duplicateQualification = new FfmQualificationBuilder()
                .withQualification("duplicate-qualification")
                .withAdditionalDuration(5)
                .setPersist(false).build();

        List<TechLeistung> techLeistungen = new ArrayList<>();
        techLeistungen.add(new TechLeistungBuilder().build());
        techLeistungen.add(new TechLeistungBuilder().build());
        testling.prepare(AbstractFfmCommand.KEY_TECH_LEISTUNGEN, techLeistungen);

        List<FfmQualification> leistungQualifications = new ArrayList<>();
        leistungQualifications.add(new FfmQualificationBuilder().withAdditionalDuration(-80).build());
        leistungQualifications.add(new FfmQualificationBuilder().withAdditionalDuration(50).build());
        leistungQualifications.add(duplicateQualification);
        when(ffmService.getFfmQualifications(techLeistungen)).thenReturn(leistungQualifications);

        List<FfmQualification> productQualifications = new ArrayList<>();
        productQualifications.add(new FfmQualificationBuilder().build());
        productQualifications.add(new FfmQualificationBuilder().withAdditionalDuration(100).build());
        productQualifications.add(duplicateQualification);
        when(ffmService.getFfmQualifications(auftragDaten)).thenReturn(productQualifications);

        List<FfmQualification> hvtStandortQualifications = new ArrayList<>();
        hvtStandortQualifications.add(new FfmQualificationBuilder().withAdditionalDuration(10).build());
        hvtStandortQualifications.add(duplicateQualification);
        when(ffmService.getFfmQualifications(hvtStandort)).thenReturn(hvtStandortQualifications);

        Object result = testling.execute();

        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof ServiceCommandResult);
        Assert.assertTrue(((ServiceCommandResult) result).isOk());

        Assert.assertEquals(workforceOrder.getId(), "HUR_uniqueId");
        Assert.assertEquals(workforceOrder.getDisplayId(), "DisplayId");
        Assert.assertEquals(workforceOrder.getCustomerOrderId(), "CustomerOrderId");
        Assert.assertEquals(workforceOrder.getType(), "Produkt");
        Assert.assertEquals(workforceOrder.getActivityType(), ffmProductMapping.getFfmActivityType());
        Assert.assertEquals(workforceOrder.getPlannedDuration().intValue(), (ffmProductMapping.getFfmPlannedDuration() + 85));
        Assert.assertNotNull(workforceOrder.getQualification());
        Assert.assertEquals(workforceOrder.getQualification().size(), 6);

        verify(ffmService, times(2)).getFfmQualifications(techLeistungen);
        verify(testling).getBauauftragAnlass();
        verify(baConfigService, never()).findBAVerlaufAnlass(anyLong());
    }

    @Test
    public void testCreateFfmDisplayIdWithTaifunId() throws FindException {
        AuftragDaten auftragDaten = new AuftragDatenBuilder()
                .withAuftragNoOrig(123456L)
                .setPersist(false).build();

        doReturn(auftragDaten).when(testling).getAuftragDaten();

        String result = testling.createFfmDisplayId();
        System.out.println(result);
        Assert.assertTrue(result.matches(String.format(DISPLAY_ID_PATTERN, auftragDaten.getAuftragNoOrig())));
    }

    @Test
    public void testCreateFfmDisplayIdWithoutTaifunId() throws FindException {
        AuftragDaten auftragDaten = new AuftragDatenBuilder()
                .withRandomAuftragId()
                .withAuftragNoOrig(null)
                .setPersist(false).build();

        VerbindungsBezeichnung vbz = new VerbindungsBezeichnungBuilder()
                .withVbz("IA10471992")
                .setPersist(false).build();

        doReturn(auftragDaten).when(testling).getAuftragDaten();
        when(physikService.findVerbindungsBezeichnungByAuftragId(auftragDaten.getAuftragId())).thenReturn(vbz);

        String result = testling.createFfmDisplayId();
        Assert.assertTrue(result.matches(String.format(DISPLAY_ID_PATTERN, vbz.getVbz())));

        verify(physikService).findVerbindungsBezeichnungByAuftragId(auftragDaten.getAuftragId());
    }


    @Test(expectedExceptions = FindException.class)
    public void testCreateFfmDisplayIdNoCustomerOrderId() throws FindException {
        Verlauf bauauftrag = new Verlauf();
        bauauftrag.setAuftragId(1L);

        auftragDaten.setAuftragNoOrig(null);
        when(physikService.findVerbindungsBezeichnungByAuftragId(bauauftrag.getAuftragId())).thenReturn(null);

        testling.createFfmDisplayId();
    }

    @Test
    public void testCreateFfmUniqueId() {
        String result = testling.createFfmUniqueId();
        Assert.assertTrue(result.startsWith("HUR_"));
        Assert.assertTrue(result.length() <= 100);
    }

    @Test
    public void testGetProduktNameTaifun() throws FindException {
        AuftragDaten auftragDaten = new AuftragDatenBuilder().withAuftragNoOrig(9L).setPersist(false).build();
        doReturn(auftragDaten).when(testling).getAuftragDaten();
        when(oeService.findProduktName4Auftrag(auftragDaten.getAuftragNoOrig())).thenReturn("TAI-Produkt");

        Assert.assertEquals(testling.getProduktName(), "TAI-Produkt");
    }

    @Test
    public void testGetProduktNameHurrican() throws FindException {
        AuftragDaten auftragDaten = new AuftragDatenBuilder().withAuftragNoOrig(null).setPersist(false).build();
        doReturn(auftragDaten).when(testling).getAuftragDaten();

        Produkt produkt = new ProduktBuilder().withAnschlussart("HUR-Produkt").setPersist(false).build();
        when(produktService.findProdukt4Auftrag(auftragDaten.getAuftragId())).thenReturn(produkt);

        Assert.assertEquals(testling.getProduktName(), "HUR-Produkt");
    }


    @DataProvider(name = "getPlannedDurationsDP")
    public Object[][] getPlannedDurationsDP() {
        return new Object[][] {
                { new FfmProductMappingBuilder().withBaFfmTyp(FfmTyp.NEU).withFfmPlannedDuration(100).build(), 150 },
                { new FfmProductMappingBuilder().withBaFfmTyp(FfmTyp.KUENDIGUNG).withFfmPlannedDuration(100).build(), 100 },
                { new FfmProductMappingBuilder().withBaFfmTyp(FfmTyp.ENTSTOERUNG).withFfmPlannedDuration(100).build(), 100 },
        };
    }


    @Test(dataProvider = "getPlannedDurationsDP")
    public void testGetPlannedDurations(FfmProductMapping ffmProductMappingToUse, int expected) {
        doReturn(ffmProductMappingToUse).when(testling).getFfmProductMapping();
        when(ffmService.getFfmQualifications(any(AuftragDaten.class)))
                .thenReturn(Arrays.asList(new FfmQualificationBuilder().withAdditionalDuration(50).build()));

        int duration = testling.getPlannedDuration();
        Assert.assertEquals(duration, expected);
    }


}
