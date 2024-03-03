package de.augustakom.hurrican.service.cc.impl.logindata;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.dao.cc.AuftragTechnikDAO;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.billing.BAuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.AuftragVoIP;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.IntAccount;
import de.augustakom.hurrican.model.cc.IpMode;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.hardware.HWSwitch;
import de.augustakom.hurrican.model.cc.hardware.HWSwitchType;
import de.augustakom.hurrican.model.shared.view.voip.AuftragVoipDNView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.billing.OEService;
import de.augustakom.hurrican.service.cc.AccountService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.ReferenceService;
import de.augustakom.hurrican.service.cc.VoIPService;
import de.augustakom.hurrican.service.cc.impl.logindata.model.LoginData;
import de.augustakom.hurrican.service.cc.impl.logindata.model.LoginDataActiveOrderNotFoundException;
import de.augustakom.hurrican.service.cc.impl.logindata.model.LoginDataImsOrderException;
import de.augustakom.hurrican.service.cc.impl.logindata.model.LoginDataNotUniqueOrderException;
import de.augustakom.hurrican.service.cc.impl.logindata.model.LoginDataNotValidProductException;
import de.augustakom.hurrican.service.cc.impl.logindata.model.internet.LoginDataInternet;
import de.augustakom.hurrican.service.cc.impl.logindata.model.voip.LoginDataVoip;
import de.augustakom.hurrican.service.cc.impl.logindata.model.voip.LoginDataVoipDn;

/**
 * UT for {@link LoginDataService}
 */
@Test(groups = { BaseTest.UNIT })
public class LoginDataServiceTest {

    @Mock
    private CCAuftragService ccAuftragService;
    @Mock
    private AuftragTechnikDAO auftragTechnikDAO;
    @Mock
    private OEService oeService;
    @Mock
    private ReferenceService referenceService;
    @Mock
    private AccountService accountService;
    @Mock
    private CCLeistungsService ccLeistungsService;
    @Mock
    private ProduktService produktService;
    @Mock
    private HVTService hvtService;
    @Mock
    private EndstellenService endstellenService;
    @Mock
    private VoIPService voIPService;
    @Mock
    private BillingAuftragService billingAuftragService;

    @InjectMocks
    @Spy
    private LoginDataService loginDataService;
    @InjectMocks
    @Spy
    private LoginDataInternetGatherer loginDataInternetGatherer;
    @InjectMocks
    @Spy
    private LoginDataVoipGatherer loginDataVoipGatherer;

    @BeforeMethod
    public void setUp() {
        loginDataService = new LoginDataService();
        loginDataInternetGatherer = new LoginDataInternetGatherer();
        loginDataVoipGatherer = new LoginDataVoipGatherer();
        initMocks(this);
    }

    @Test
    public void testGetSingleAuftrag_Ok() throws Exception {
        final AuftragDaten adMock = getAuftragBuilder();
        when(adMock.isAuftragActiveAndInBetrieb()).thenReturn(true);
        final List<AuftragDaten> auftragDatenList = Collections.singletonList(adMock);
        when(ccAuftragService.findAuftragDaten4OrderNoOrig(anyLong())).thenReturn(auftragDatenList);
        final AuftragDaten auftrag = loginDataService.getSingleAuftrag(anyLong());
        assertNotNull(auftrag);
    }

    @Test
    public void testGetSingleAuftragAfterFilter_Ok() throws Exception {
        final AuftragDaten adMock1 = getAuftragBuilder();
        final AuftragDaten adMock2 = getAuftragBuilder();
        adMock2.setAuftragId(9999998L);
        adMock2.setProdId(Produkt.PROD_ID_ISDN_TK);
        when(adMock1.isAuftragActiveAndInBetrieb()).thenReturn(true);
        when(adMock2.isAuftragActiveAndInBetrieb()).thenReturn(true);
        final List<AuftragDaten> auftragDatenList = Arrays.asList(adMock1,adMock2);
        when(ccAuftragService.findAuftragDaten4OrderNoOrig(anyLong())).thenReturn(auftragDatenList);
        final AuftragDaten auftrag = loginDataService.getSingleAuftrag(anyLong());
        assertNotNull(auftrag);
    }

    @Test
    public void testGetLoginDataOK() throws Exception {
        IntAccount intAccount = new IntAccount();
        intAccount.setAccount("ppp-user");
        intAccount.setPasswort("ppp-passwort");
        String pppUserRealmSuffix = "@mdsl.mnet-online.de";
        AuftragDaten auftragDaten = getAuftragBuilder();
        prepareTestGetLoginDataOK(auftragDaten);
        prepareTestGetLoginDataInternetOK(intAccount,pppUserRealmSuffix,auftragDaten);
        prepareTestGetLoginDataVoipOK();

        final LoginDataInternet loginDataInternetExpected =
                new LoginDataInternet(intAccount.getAccount(), pppUserRealmSuffix, intAccount.getPasswort(), IpMode.DUAL_STACK, 0, 5, 40, null, "aftr.prod.m-online.net", 1, 32);
        final LoginDataVoip loginDataVoipExpected =
                new LoginDataVoip(Collections.singletonList(new LoginDataVoipDn("+49821999999", "biz.m-call.de", "x5faeg")));

        final LoginData loginData = loginDataService.getLoginData(9999999L);

        assertEquals(loginData.getLoginDataInternet(),loginDataInternetExpected);
        assertEquals(loginData.getLoginDataVoip(),loginDataVoipExpected);
    }

    @Test(expectedExceptions = LoginDataActiveOrderNotFoundException.class)
    public void testGetSingleAuftrag_Inactive() throws Exception {
        final AuftragDaten adMock = getAuftragBuilder();
        when(adMock.isAuftragActiveAndInBetrieb()).thenReturn(false);
        final List<AuftragDaten> auftragDatenList = Collections.singletonList(adMock);
        when(ccAuftragService.findAuftragDaten4OrderNoOrig(anyLong())).thenReturn(auftragDatenList);
        loginDataService.getSingleAuftrag(anyLong());
    }

    @Test (expectedExceptions = LoginDataNotUniqueOrderException.class)
    public void testGetFilteredAuftrag() throws Exception {
        final AuftragDaten adMock1 = getAuftragBuilder();
        final AuftragDaten adMock2 = getAuftragBuilder();
        adMock2.setAuftragId(9999998L);
        adMock2.setProdId(Produkt.PROD_ID_PREMIUM_DSL_ISDN);
        when(adMock1.isAuftragActiveAndInBetrieb()).thenReturn(true);
        when(adMock2.isAuftragActiveAndInBetrieb()).thenReturn(true);
        final List<AuftragDaten> auftragDatenList = Arrays.asList(adMock1,adMock2);
        when(ccAuftragService.findAuftragDaten4OrderNoOrig(anyLong())).thenReturn(auftragDatenList);
        final AuftragDaten auftrag = loginDataService.getSingleAuftrag(anyLong());
        assertNotNull(auftrag);
    }

    @Test(expectedExceptions = LoginDataImsOrderException.class)
    public void testGetLoginData_Ims() throws Exception {
        final AuftragDaten adMock = getAuftragBuilder();
        when(adMock.isAuftragActiveAndInBetrieb()).thenReturn(true);
        Reference reference = new Reference();
        reference.setStrValue("3400");
        reference.setType(Reference.REF_TYPE_PROVIDED_OE_NO);
        when(referenceService.findReferencesByType(Reference.REF_TYPE_PROVIDED_OE_NO, false)).thenReturn(Collections.singletonList(reference));
        when(oeService.findVaterOeNoOrig4Auftrag(anyLong())).thenReturn(3400L);
        final List<AuftragDaten> auftragDatenList = Collections.singletonList(getAuftragBuilder());
        when(ccAuftragService.findAuftragDaten4OrderNoOrig(anyLong())).thenReturn(auftragDatenList);

        final AuftragTechnik auftragTechnik = spy(new AuftragTechnik());
        final HWSwitch hwSwitch = spy(new HWSwitch());
        hwSwitch.setType(HWSwitchType.IMS);
        auftragTechnik.setHwSwitch(hwSwitch);
        when(auftragTechnikDAO.findByAuftragId(anyLong())).thenReturn(auftragTechnik);
        when(ccAuftragService.getSwitchKennung4Auftrag(anyLong())).thenReturn(hwSwitch);
        loginDataService.getLoginData(anyLong());
    }

    @Test(expectedExceptions = LoginDataNotValidProductException.class)
    public void testGetLoginNotValidProductException() throws Exception {
        Reference reference = new Reference();
        reference.setStrValue("3400");
        reference.setType(Reference.REF_TYPE_PROVIDED_OE_NO);

        final AuftragDaten adMock = getAuftragBuilder();
        when(adMock.isAuftragActiveAndInBetrieb()).thenReturn(true);
        final List<AuftragDaten> auftragDatenList = Collections.singletonList(adMock);
        when(ccAuftragService.findAuftragDaten4OrderNoOrig(anyLong())).thenReturn(auftragDatenList);

        when(referenceService.findReferencesByType(Reference.REF_TYPE_PROVIDED_OE_NO, false)).thenReturn(Collections.singletonList(reference));
        when(oeService.findVaterOeNoOrig4Auftrag(anyLong())).thenReturn(2000L);
        loginDataService.getLoginData(anyLong());
    }

    @Test
    public void testGetLoginData_NoImsCheck() throws Exception {
        final Long auftragId = 1L;

        final AuftragDaten auftragDaten = getAuftragBuilder();
        when(auftragDaten.isAuftragActiveAndInBetrieb()).thenReturn(true);

        IntAccount intAccount = new IntAccount();
        intAccount.setAccount("ppp-user");
        intAccount.setPasswort("ppp-passwort");
        String pppUserRealmSuffix = "mdsl.mnet-online.de";
        prepareTestGetLoginDataOK(auftragDaten);
        prepareTestGetLoginDataInternetOK(intAccount,pppUserRealmSuffix, auftragDaten);

        Reference referenceIms = new Reference();
        referenceIms.setStrValue("3402");
        referenceIms.setType(Reference.REF_NO_IMS_CHECK_4_OE_NO_TYPE);
        when(referenceService.findReferencesByType(Reference.REF_NO_IMS_CHECK_4_OE_NO_TYPE, false)).thenReturn(Collections.singletonList(referenceIms));

        Reference referenceProvidedOe = new Reference();
        referenceProvidedOe.setStrValue("3400");
        referenceProvidedOe.setType(Reference.REF_TYPE_PROVIDED_OE_NO);
        when(referenceService.findReferencesByType(Reference.REF_TYPE_PROVIDED_OE_NO, false)).thenReturn(Collections.singletonList(referenceProvidedOe));

        when(oeService.findVaterOeNoOrig4Auftrag(anyLong())).thenReturn(3400L);
        final List<AuftragDaten> auftragDatenList = Collections.singletonList(getAuftragBuilder());
        when(ccAuftragService.findAuftragDaten4OrderNoOrig(anyLong())).thenReturn(auftragDatenList);
        BAuftrag bAuftrag = new BAuftragBuilder().withOeNoOrig(3402L).withAuftragNo(auftragId).build();
        when(billingAuftragService.findAuftrag(anyLong())).thenReturn(bAuftrag);

        final AuftragTechnik auftragTechnik = new AuftragTechnikBuilder().withAuftragId(auftragId).build();
        final HWSwitch hwSwitch = spy(new HWSwitch());
        hwSwitch.setType(HWSwitchType.IMS);
        auftragTechnik.setHwSwitch(hwSwitch);
        when(auftragTechnikDAO.findByAuftragId(anyLong())).thenReturn(auftragTechnik);
        when(ccAuftragService.getSwitchKennung4Auftrag(anyLong())).thenReturn(hwSwitch);
        loginDataService.getLoginData(anyLong());
    }

    @Test(expectedExceptions = LoginDataImsOrderException.class)
    public void testGetLoginData_NoImsCheckException() throws Exception {
        final AuftragDaten adMock = getAuftragBuilder();
        when(adMock.isAuftragActiveAndInBetrieb()).thenReturn(true);
        Reference reference = new Reference();
        reference.setStrValue("3402");
        reference.setType(Reference.REF_NO_IMS_CHECK_4_OE_NO_TYPE);
        when(referenceService.findReferencesByType(Reference.REF_NO_IMS_CHECK_4_OE_NO_TYPE, false)).thenReturn(Collections.singletonList(reference));
        when(oeService.findVaterOeNoOrig4Auftrag(anyLong())).thenReturn(3400L);
        final List<AuftragDaten> auftragDatenList = Collections.singletonList(getAuftragBuilder());
        when(ccAuftragService.findAuftragDaten4OrderNoOrig(anyLong())).thenReturn(auftragDatenList);
        BAuftrag bAuftrag = new BAuftragBuilder().withOeNoOrig(3000L).withAuftragNo(1L).build();
        when(billingAuftragService.findAuftrag(anyLong())).thenReturn(bAuftrag);

        final AuftragTechnik auftragTechnik = spy(new AuftragTechnik());
        final HWSwitch hwSwitch = spy(new HWSwitch());
        hwSwitch.setType(HWSwitchType.IMS);
        auftragTechnik.setHwSwitch(hwSwitch);
        when(auftragTechnikDAO.findByAuftragId(anyLong())).thenReturn(auftragTechnik);
        when(ccAuftragService.getSwitchKennung4Auftrag(anyLong())).thenReturn(hwSwitch);
        loginDataService.getLoginData(anyLong());
    }


    private void prepareTestGetLoginDataOK(AuftragDaten auftragDaten) throws FindException {
        Reference referenceVaterOe = new Reference();
        referenceVaterOe.setStrValue("3400");
        referenceVaterOe.setType(Reference.REF_TYPE_PROVIDED_OE_NO);

        final AuftragTechnik auftragTechnik = spy(new AuftragTechnik());
        final HWSwitch hwSwitch = spy(new HWSwitch());
        hwSwitch.setType(HWSwitchType.NSP);
        auftragTechnik.setAuftragId(9999999L);
        auftragTechnik.setHwSwitch(hwSwitch);
        auftragTechnik.setIntAccountId(689899L);

        when(auftragDaten.isAuftragActiveAndInBetrieb()).thenReturn(true);
        when(referenceService.findReferencesByType(Reference.REF_TYPE_PROVIDED_OE_NO, false)).thenReturn(Collections.singletonList(referenceVaterOe));
        when(oeService.findVaterOeNoOrig4Auftrag(anyLong())).thenReturn(3400L);
        when(auftragTechnikDAO.findByAuftragId(anyLong())).thenReturn(auftragTechnik);
        when(ccAuftragService.getSwitchKennung4Auftrag(anyLong())).thenReturn(hwSwitch);
    }

    private void prepareTestGetLoginDataInternetOK(IntAccount intAccount,String pppUserRealmSuffix, AuftragDaten auftragDaten) throws FindException  {
        Reference referenceHVT = new Reference();
        referenceHVT.setId(11000L);
        referenceHVT.setStrValue("HVT");
        referenceHVT.setType("REF_TYPE_STANDORT_TYP");

        HVTStandort hvtStandort = new HVTStandort();
        hvtStandort.setStandortTypRefId(11000L);

        Produkt produkt = new Produkt();
        produkt.setAftrAddress("aftr.prod.m-online.net");
        produkt.setPbitDaten(0);
        produkt.setPbitVoip(5);

        Endstelle endstelle = new Endstelle();
        endstelle.setHvtIdStandort(11000L);

        List<AuftragDaten> auftragDatenList = new ArrayList<>();
        auftragDatenList.add(auftragDaten);

        when(ccAuftragService.findAuftragDaten4OrderNoOrig(anyLong())).thenReturn(auftragDatenList);
        when(ccLeistungsService.queryIPMode(anyLong(), any(LocalDate.class))).thenReturn(IpMode.DUAL_STACK);
        when(referenceService.findReference(Reference.REF_TYPE_STANDORT_TYP, "HVT")).thenReturn(referenceHVT);
        when(accountService.findIntAccountById(anyLong())).thenReturn(intAccount);
        when(accountService.getAccountRealm(anyLong())).thenReturn(pppUserRealmSuffix);
        when(produktService.findProdukt(anyLong())).thenReturn(produkt);
        when(endstellenService.findEndstelle4Auftrag(anyLong(), anyString())).thenReturn(endstelle);
        when(hvtService.findHVTStandort(anyLong())).thenReturn(hvtStandort);
    }

    private void prepareTestGetLoginDataVoipOK() throws FindException {
        Reference referenceSipDomain = new Reference();
        referenceSipDomain.setId(22346L);
        referenceSipDomain.setStrValue("biz.m-call.de");
        referenceSipDomain.setType("SIP_DOMAIN_TYPE");

        AuftragVoIP auftragVoIP = new AuftragVoIP();
        auftragVoIP.setIsActive(true);

        AuftragVoipDNView auftragVoipDNView = new AuftragVoipDNView();
        auftragVoipDNView.setOnKz("0821");
        auftragVoipDNView.setDnBase("999999");
        auftragVoipDNView.setSipDomain(referenceSipDomain);
        auftragVoipDNView.setSipPassword("x5faeg");
        auftragVoipDNView.setGueltigBis(DateTools.getHurricanEndDate());

        List<AuftragVoipDNView> auftragVoipDNViewList = new ArrayList<>();
        auftragVoipDNViewList.add(auftragVoipDNView);

        when(voIPService.findVoIP4Auftrag(anyLong())).thenReturn(auftragVoIP);
        when(voIPService.findVoIPDNView(anyLong())).thenReturn(auftragVoipDNViewList);
        when(voIPService.generateSipHauptrufnummer(anyString(),anyString(),anyString())).thenReturn("+49821999999");
    }

    private AuftragDaten  getAuftragBuilder() {
        final AuftragDaten adMock = spy(new AuftragDaten());
        adMock.setAuftragId(9999999L);
        adMock.setProdId(Produkt.PROD_ID_DSL_VOIP);
        return adMock;
    }
}
