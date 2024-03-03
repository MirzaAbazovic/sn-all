/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.01.2012 16:00:21
 */
package de.augustakom.hurrican.service.cc.impl.command.cps.mvs;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import com.google.common.collect.ImmutableList;
import org.mockito.Mock;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.service.exceptions.ServiceCommandException;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.exceptions.NoDataFoundException;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.lang.RandomTools;
import de.augustakom.hurrican.model.billing.Leistung;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.billing.RufnummerBuilder;
import de.augustakom.hurrican.model.billing.view.BAuftragLeistungView;
import de.augustakom.hurrican.model.billing.view.BAuftragLeistungViewBuilder;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragMVSEnterprise;
import de.augustakom.hurrican.model.cc.AuftragMVSEnterpriseBuilder;
import de.augustakom.hurrican.model.cc.AuftragMVSSite;
import de.augustakom.hurrican.model.cc.AuftragMVSSiteBuilder;
import de.augustakom.hurrican.model.cc.CPSTransactionBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.GeoIdBuilder;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSMVSAdminAccount;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSMVSSiteData;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.billing.KundenService;
import de.augustakom.hurrican.service.cc.AvailabilityService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.MVSService;

@Test(groups = { BaseTest.UNIT })
public class CPSGetMVSSiteDataCommandTest extends AbstractCPSGetMVSDataTest {

    private Auftrag auftragMVSEp;
    private AuftragDaten auftragDatenMVSEp;
    private AuftragDaten auftragDaten;
    private AuftragMVSSite auftragMVSSite;
    private AuftragMVSEnterprise auftragMvsEP;
    private Endstelle endstelle;
    private final String onkz = "1234";
    private final Integer asb = RandomTools.createInteger();
    private final String location = "MUC-LEO-234";
    private List<Rufnummer> dns;

    @Spy
    private CPSGetMVSSiteDataCommand cut;
    @Mock
    private AvailabilityService avsMock;
    @Mock
    private EndstellenService essMock;
    @Mock
    private MVSService mvssMock;
    @Mock
    private BillingAuftragService billingMock;

    @BeforeMethod()
    void setUp() throws FindException, ServiceNotFoundException, ServiceCommandException {
        cut = new CPSGetMVSSiteDataCommand();
        initMocks(this);

        // formatter:off
        auftragMVSEp = new AuftragBuilder().setPersist(false).withRandomId().withKundeNo(RandomTools.createLong())
                .build();
        auftragDaten = new AuftragDatenBuilder().setPersist(false).withAuftragId(auftrag.getId())
                .withAuftragNoOrig(RandomTools.createLong()).build();
        endstelle = new EndstelleBuilder().setPersist(false).withGeoIdBuilder(new GeoIdBuilder().setPersist(false))
                .build();
        auftragMVSSite = spy(new AuftragMVSSiteBuilder().setPersist(false).withUserName("admin").withPassword("asdf")
                .build());
        when(auftragMVSSite.getStandortKuerzel()).thenReturn(location);

        auftragMvsEP = new AuftragMVSEnterpriseBuilder().setPersist(false).withDomain("Kunde.mpbx.de").build();
        auftragDatenMVSEp = new AuftragDatenBuilder().setPersist(false).withAuftragNoOrig(RandomTools.createLong())
                .withAuftragId(auftragMVSEp.getId()).build();
        dns = Arrays.asList(new RufnummerBuilder().setPersist(false).withOnKz(String.valueOf(onkz)).withDnBase("5678")
                .withRangeFrom("0").withRangeTo("100").build());
        // formatter:on

        cut.prepare(CPSGetMVSSiteDataCommand.KEY_AUFTRAG_DATEN, auftragDaten);
        cut.prepare(CPSGetMVSSiteDataCommand.KEY_SERVICE_ORDER_DATA, soData);
        cut.prepare(AbstractGetMVSDataCommand.KEY_CPS_TRANSACTION,
                new CPSTransactionBuilder()
                        .withEstimatedExecTime(new Date())
                        .setPersist(false)
                        .build()
        );
        stubSpy();
        configureMocks();
    }

    public CPSGetMVSSiteDataCommandTest() {

    }

    public void execute_Success() throws Exception {
        ServiceCommandResult cmdResult = (ServiceCommandResult) cut.execute();

        assertEquals(cmdResult.getCheckStatus(), ServiceCommandResult.CHECK_STATUS_OK);

        CPSMVSSiteData siteData = soData.getMvs().getSiteData();
        assertNotNull(siteData);
        assertEquals(siteData.getEnterpriseId(), cut.findEnterpriseId());
        assertEquals(siteData.getResellerId(), cut.findResellerId());
        assertEquals(siteData.getSubdomain(), cut.findSubdomain());
        assertEquals(siteData.getLocation(), cut.findLocation());
        assertEquals(siteData.getChannels(), Long.valueOf(16));

        CPSMVSAdminAccount adminAccFound = siteData.getAdminAccount();
        CPSMVSAdminAccount adminAccExp = new CPSMVSAdminAccount(cut.findUsername(), cut.findPassword(), null);
        assertEqAdminAcc(adminAccFound, adminAccExp);

        List<CPSMVSSiteData.Number> numbersFound = siteData.getNumbers().getNumbers();
        List<CPSMVSSiteData.Number> numbersExp = cut.findNumbers();
        assertEquals(numbersFound.size(), numbersExp.size());
        assertEqNumber(numbersFound.get(0), numbersExp.get(0));
    }

    private void assertEqAdminAcc(CPSMVSAdminAccount found, CPSMVSAdminAccount expected) {
        assertEquals(found.getUsername(), expected.getUsername());
        assertEquals(found.getPassword(), expected.getPassword());
        assertEquals(found.getEmail(), expected.getEmail());
    }

    private void assertEqNumber(CPSMVSSiteData.Number found, CPSMVSSiteData.Number expected) {
        Pair<String, String> adjustedBlocks = cut.adjustBlockLength(expected.getBlockStart(), expected.getBlockEnd());
        assertEquals(found.getBlockStart(), adjustedBlocks.getFirst());
        assertEquals(found.getBlockEnd(), adjustedBlocks.getSecond());
        assertEquals(found.getCountryCode(), expected.getCountryCode());
        assertEquals(found.getLac(), expected.getLac());
        assertEquals(found.getDn(), expected.getDn());
    }

    public void execute_WithException() throws Exception {
        doThrow(new RuntimeException()).when(cut).getCCService(CCAuftragService.class);
        ServiceCommandResult cmdResult = (ServiceCommandResult) cut.execute();
        assertEquals(cmdResult.getCheckStatus(), ServiceCommandResult.CHECK_STATUS_INVALID);
    }

    @Test
    public void execute_WithServiceCommandResultIsInvalid() throws FindException, ServiceNotFoundException,
            ServiceCommandException {
        doThrow(new HurricanServiceCommandException()).when(cut).findEnterpriseId();
        ServiceCommandResult result = (ServiceCommandResult) cut.execute();
        assertTrue(!result.isOk());
    }

    public void findNumbers_Success() throws FindException, ServiceCommandException, ServiceNotFoundException {
        List<CPSMVSSiteData.Number> numbersFound = cut.findNumbers();
        CPSMVSSiteData.Number numExp = new CPSMVSSiteData.Number();
        numExp.transferDNData(dns.get(0));
        assertEquals(numbersFound.size(), 1);
        assertEqNumber(numbersFound.get(0), numExp);
    }

    @Test(expectedExceptions = { HurricanServiceCommandException.class })
    public void findNumbers_NoNumbersFound() throws Exception {
        doReturn(Collections.emptyList()).when(cut).getActiveDNs(auftragDaten.getAuftragNoOrig());
        cut.findNumbers();
    }

    @Test(expectedExceptions = { HurricanServiceCommandException.class })
    public void findSubdomain_DomainNotFound() throws FindException,
            ServiceNotFoundException, ServiceCommandException {
        when(mvssMock.findEnterpriseForSiteAuftragId(eq(auftrag.getId()))).thenThrow(new NoDataFoundException());
        cut.findSubdomain();
    }

    public void findPassword_Success() throws FindException, ServiceNotFoundException, ServiceCommandException {
        final String pwFound = cut.findPassword();
        assertEquals(pwFound, auftragMVSSite.getPassword());
    }

    @Test(expectedExceptions = { HurricanServiceCommandException.class })
    public void findPassword_SiteAuftragNotFound() throws FindException, NoDataFoundException,
            ServiceNotFoundException, ServiceCommandException {
        when(mvssMock.findMvsSite4Auftrag(eq(auftrag.getId()), eq(true))).thenReturn(null);
        cut.findPassword();
    }

    public void findUsername() throws FindException, ServiceNotFoundException, ServiceCommandException {
        final String username = cut.findUsername();
        assertEquals(username, auftragMVSSite.getUserName());
    }

    @Test(expectedExceptions = { HurricanServiceCommandException.class })
    public void findUsername_SiteAuftragNotFound() throws FindException,
            ServiceNotFoundException, ServiceCommandException {
        when(mvssMock.findMvsSite4Auftrag(eq(auftrag.getId()), eq(true))).thenReturn(null);
        cut.findUsername();
    }

    public void findEnterpriseId_Success() throws FindException,
            ServiceNotFoundException, ServiceCommandException {
        Long epId = cut.findEnterpriseId();
        assertEquals(epId, auftragDatenMVSEp.getAuftragNoOrig());
    }

    @Test(expectedExceptions = { HurricanServiceCommandException.class })
    public void findEnterpriseId_EpIdNotFound() throws FindException,
            ServiceNotFoundException, ServiceCommandException {
        when(asMock.findAuftragDatenByAuftragIdTx(eq(auftragMVSEp.getId()))).thenReturn(null);
        cut.findEnterpriseId();
    }

    public void findAsbAndOnkz_Success() throws FindException,
            ServiceNotFoundException, ServiceCommandException {
        Pair<Integer, String> asbAndOnkz = cut.findAsbAndOnkz();
        assertEquals(asbAndOnkz.getFirst(), asb);
        assertEquals(asbAndOnkz.getSecond(), onkz);
    }

    @DataProvider(name = "findAsbAndOrOnkzNotFoundDP")
    public Object[][] findAsbAndOrOnkzNotFoundDP() throws FindException, HurricanServiceCommandException,
            ServiceNotFoundException {
        final Integer randomInteger = RandomTools.createInteger();
        return new Object[][] { { new Pair<Integer, String>(null, "asdf") },
                { new Pair<Integer, String>(randomInteger, null) }, { null } };
    }

    @Test(expectedExceptions = { HurricanServiceCommandException.class }, dataProvider = "findAsbAndOrOnkzNotFoundDP")
    public void findAsbAndOnkz_AsbOrOnkzNotFound(Pair<Integer, String> asbAndOnkz)
            throws FindException, ServiceNotFoundException, ServiceCommandException {
        when(avsMock.findAsbAndOnKzForGeoId(anyLong())).thenReturn(asbAndOnkz);
        cut.findAsbAndOnkz();
    }

    public void findLocation_Success() throws FindException,
            ServiceNotFoundException, ServiceCommandException {
        assertEquals(cut.findLocation(), location);
    }

    public void findEmptyLocation() throws FindException,
            ServiceNotFoundException, ServiceCommandException {
        when(auftragMVSSite.getStandortKuerzel()).thenReturn(null);
        assertEquals(cut.findLocation(), null);
    }

    @Test(expectedExceptions = { FindException.class })
    public void findLocation_VentoException() throws Exception {
        when(mvssMock.findMvsSite4Auftrag(eq(auftrag.getId()), eq(true))).thenThrow(new FindException());
        cut.findLocation();
    }

    private void stubSpy() throws ServiceNotFoundException, FindException, ServiceCommandException {
        doReturn(asMock).when(cut).getCCService(CCAuftragService.class);
        doReturn(auftragMVSEp.getId()).when(cut).getCCAuftragIdMVSEp();
        doReturn(ksMock).when(cut).getBillingService(KundenService.class);
        doReturn(essMock).when(cut).getCCService(EndstellenService.class);
        doReturn(avsMock).when(cut).getCCService(AvailabilityService.class);
        doReturn(mvssMock).when(cut).getCCService(MVSService.class);
        doReturn(billingMock).when(cut).getBillingService(BillingAuftragService.class);
        doReturn(dns).when(cut).getActiveDNs(auftragDaten.getAuftragNoOrig());
    }

    private void configureMocks() throws FindException, ServiceNotFoundException {
        when(asMock.findAuftragById(eq(auftrag.getAuftragId()))).thenReturn(auftrag);
        when(asMock.findAuftragById(eq(auftragMVSEp.getId()))).thenReturn(auftragMVSEp);
        when(ksMock.findKunde(eq(auftragMVSEp.getKundeNo()))).thenReturn(kunde);
        when(asMock.findAuftragDatenByAuftragIdTx(eq(auftragMVSEp.getId()))).thenReturn(auftragDatenMVSEp);
        when(essMock.findEndstelle4Auftrag(eq(auftrag.getId()), eq(Endstelle.ENDSTELLEN_TYP_B))).thenReturn(endstelle);
        when(avsMock.findAsbAndOnKzForGeoId(eq(endstelle.getGeoId()))).thenReturn(new Pair<Integer, String>(asb, onkz));
        when(mvssMock.findMvsSite4Auftrag(eq(auftrag.getId()), eq(true))).thenReturn(auftragMVSSite);
        when(mvssMock.findEnterpriseForSiteAuftragId(eq(auftrag.getId()))).thenReturn(auftragMvsEP);

        BAuftragLeistungView activeView = new BAuftragLeistungViewBuilder()
                .withExternMiscNo(Leistung.EXT_MISC_NO_MVS_LIZENZPAKET)
                .withAuftragPosGueltigVon(Date.from(LocalDate.now().minusDays(10).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .withAuftragPosGueltigBis(null)
                .withMenge(Long.valueOf(15))
                .build();
        BAuftragLeistungView inactiveView = new BAuftragLeistungViewBuilder()
                .withExternMiscNo(Leistung.EXT_MISC_NO_MVS_FAX2MAIL)
                .withAuftragPosGueltigVon(Date.from(LocalDate.now().minusDays(10).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .withAuftragPosGueltigBis(Date.from(LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .withMenge(Long.valueOf(1))
                .build();
        BAuftragLeistungView tomorrowActiveView = new BAuftragLeistungViewBuilder()
                .withExternMiscNo(Leistung.EXT_MISC_NO_MVS_MOBILE_CLIENT)
                .withAuftragPosGueltigVon(Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .withAuftragPosGueltigBis(null)
                .withMenge(Long.valueOf(1))
                .build();
        BAuftragLeistungView channelsBeforeToday = new BAuftragLeistungViewBuilder()
                .withExternMiscNo(Leistung.EXT_MISC_NO_MVS_CHANNELS)
                .withAuftragPosGueltigVon(Date.from(LocalDate.now().minusDays(10).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .withAuftragPosGueltigBis(Date.from(LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .withMenge(Long.valueOf(2))
                .build();
        BAuftragLeistungView channelsToday1 = new BAuftragLeistungViewBuilder()
                .withExternMiscNo(Leistung.EXT_MISC_NO_MVS_CHANNELS)
                .withAuftragPosGueltigVon(Date.from(LocalDate.now().minusDays(10).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .withAuftragPosGueltigBis(null)
                .withMenge(Long.valueOf(3))
                .build();
        BAuftragLeistungView channelsToday2 = new BAuftragLeistungViewBuilder()
                .withExternMiscNo(Leistung.EXT_MISC_NO_MVS_CHANNELS)
                .withAuftragPosGueltigVon(Date.from(LocalDate.now().minusDays(10).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .withAuftragPosGueltigBis(null)
                .withMenge(Long.valueOf(5))
                .build();
        BAuftragLeistungView channelsAfterTomorrow = new BAuftragLeistungViewBuilder()
                .withExternMiscNo(Leistung.EXT_MISC_NO_MVS_CHANNELS)
                .withAuftragPosGueltigVon(Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .withAuftragPosGueltigBis(null)
                .withMenge(Long.valueOf(7))
                .build();

        List<BAuftragLeistungView> leistungen = ImmutableList.of(activeView, inactiveView, tomorrowActiveView,
                channelsBeforeToday, channelsToday1, channelsToday2, channelsAfterTomorrow);
        when(
                billingMock.findAuftragLeistungViewsByExtProdNoAndExtMiscNOs(any(Long.class),
                        eq(Produkt.PROD_ID_MVS_SITE), anyCollectionOf(Long.class))
        )
                .thenReturn(leistungen);
    }
}
