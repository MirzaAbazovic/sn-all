/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.01.2012 09:01:56
 */
package de.augustakom.hurrican.service.cc.impl.command.cps.mvs;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import org.mockito.Mock;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.RandomTools;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.billing.Leistung;
import de.augustakom.hurrican.model.billing.view.BAuftragLeistungView;
import de.augustakom.hurrican.model.billing.view.BAuftragLeistungViewBuilder;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragMVSEnterprise;
import de.augustakom.hurrican.model.cc.AuftragMVSEnterpriseBuilder;
import de.augustakom.hurrican.model.cc.CPSTransactionBuilder;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSMVSAdminAccount;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSMVSData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSMVSEnterpriseData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSMVSEnterpriseLicences;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.billing.KundenService;
import de.augustakom.hurrican.service.cc.CCAuftragService;

/**
 *
 */
@Test(groups = { BaseTest.UNIT })
public class CPSGetMVSEnterpriseDataCommandTest extends AbstractCPSGetMVSDataTest {

    @Spy
    private CPSGetMVSEnterpriseDataCommand cut = new CPSGetMVSEnterpriseDataCommand();

    @Mock
    private BillingAuftragService billingAuftragServiceMock;

    private final AuftragMVSEnterprise auftragMvsEP;
    private final AuftragMVSEnterprise auftragMvsEPNull;

    private final Long auftragNoOrig = RandomTools.createLong();

    private final List<BAuftragLeistungView> billingAuftragLeistungViews = new ArrayList<>();
    private final Long positionMenge = 2L;
    private final String serviceValueParam = "3";

    public CPSGetMVSEnterpriseDataCommandTest() throws InstantiationException, IllegalAccessException {
        super();
        //@formatter:off
        auftragMvsEP = new AuftragMVSEnterpriseBuilder().setPersist(false)
                          .withDomain("asdf")
                          .withUserName("admin")
                          .withPassword("1234fdsa765")
                          .build();

        auftragMvsEPNull = new AuftragMVSEnterpriseBuilder().setPersist(false)
                          .withDomain(null)
                          .withUserName(null)
                          .withPassword(null)
                          .withMail(null)
                          .build();
        //@formatter:on
        for (Long extMiscNo : Leistung.getExtMiscNos4Mvs()) {
            BAuftragLeistungView bAuftragLeistungView = BAuftragLeistungView.class.newInstance(); //no standard / simple constructor available
            bAuftragLeistungView.setExternMiscNo(extMiscNo);
            bAuftragLeistungView.setMenge(positionMenge);
            bAuftragLeistungView.setAuftragPosGueltigVon(Date.from(LocalDate.now().minusDays(10).atStartOfDay(ZoneId.systemDefault()).toInstant()));
            if (extMiscNo.equals(Leistung.EXT_MISC_NO_MVS_LIZENZPAKET)) {
                bAuftragLeistungView.setServiceValueParam(serviceValueParam);
            }
            billingAuftragLeistungViews.add(bAuftragLeistungView);
        }
    }

    @BeforeMethod
    void setUp() {
        cut = new CPSGetMVSEnterpriseDataCommand();
        initMocks(this);
        //@formatter:off
        cut.prepare(AbstractGetMVSDataCommand.KEY_AUFTRAG_DATEN,
                new AuftragDatenBuilder().setPersist(false)
                    .withAuftragId(auftrag.getAuftragId())
                    .withAuftragNoOrig(auftragNoOrig)
                    .build());
        cut.prepare(AbstractGetMVSDataCommand.KEY_CPS_TRANSACTION,
                new CPSTransactionBuilder()
                    .withEstimatedExecTime(new Date())
                    .setPersist(false)
                    .build());
        //@formatter:on
        cut.prepare(AbstractGetMVSDataCommand.KEY_SERVICE_ORDER_DATA, soData);
    }

    void stubSpyAndMocks(AuftragMVSEnterprise localAuftragMvsEP, Kunde localKunde,
            List<BAuftragLeistungView> billingAuftragLeistungViews) throws FindException, ServiceNotFoundException {
        doReturn(auftrag.getId()).when(cut).getCCAuftragIdMVSEp();
        doReturn(localAuftragMvsEP).when(cut).getAuftragMVSEnterprise(); //
        doReturn(asMock).when(cut).getCCService(CCAuftragService.class);
        doReturn(ksMock).when(cut).getBillingService(KundenService.class);
        doReturn(billingAuftragServiceMock).when(cut).getBillingService(BillingAuftragService.class);
        when(asMock.findAuftragById(auftrag.getAuftragId())).thenReturn(auftrag);
        when(ksMock.findKunde(auftrag.getKundeNo())).thenReturn(localKunde);
        when(
                billingAuftragServiceMock.findAuftragLeistungViewsByExtProdNoAndExtMiscNOs(eq(auftragNoOrig),
                        eq(Long.valueOf(Produkt.PROD_ID_MVS_ENTERPRISE)),
                        eq(Arrays.asList(Leistung.getExtMiscNos4Mvs())))
        ).thenReturn(
                billingAuftragLeistungViews);
    }

    @Test
    public void findResellerId() throws ServiceNotFoundException, FindException, HurricanServiceCommandException {
        stubSpyAndMocks(auftragMvsEP, kunde, billingAuftragLeistungViews);
        assertEquals(cut.findResellerId(), kunde.getResellerKundeNo());
    }

    @Test(expectedExceptions = { HurricanServiceCommandException.class })
    public void findResellerId_withoutKunde() throws ServiceNotFoundException, FindException,
            HurricanServiceCommandException {
        stubSpyAndMocks(auftragMvsEP, null, billingAuftragLeistungViews);
        cut.findResellerId();
    }

    @Test
    public void findDomain() throws ServiceNotFoundException, FindException, HurricanServiceCommandException {
        stubSpyAndMocks(auftragMvsEP, kunde, billingAuftragLeistungViews);
        assertEquals(cut.findDomain(), auftragMvsEP.getDomain());
    }

    @Test(expectedExceptions = { HurricanServiceCommandException.class })
    public void findDomain_DomainIsNull() throws ServiceNotFoundException, FindException,
            HurricanServiceCommandException {
        stubSpyAndMocks(auftragMvsEPNull, kunde, billingAuftragLeistungViews);
        cut.findDomain();
    }

    @Test
    public void findAdminAcc() throws ServiceNotFoundException, FindException, HurricanServiceCommandException {
        stubSpyAndMocks(auftragMvsEP, kunde, billingAuftragLeistungViews);
        CPSMVSAdminAccount adminAcc = cut.findAdminAcc();
        assertEquals(adminAcc.getUsername(), auftragMvsEP.getUserName());
        assertEquals(adminAcc.getPassword(), auftragMvsEP.getPassword());
        assertEquals(adminAcc.getEmail(), auftragMvsEP.getMail());
    }

    @Test(expectedExceptions = { HurricanServiceCommandException.class })
    public void findAdminAcc_withNullValues() throws ServiceNotFoundException, FindException,
            HurricanServiceCommandException {
        stubSpyAndMocks(auftragMvsEPNull, kunde, billingAuftragLeistungViews);
        cut.findAdminAcc();
    }

    @Test
    public void execute_success() throws Exception {
        stubSpyAndMocks(auftragMvsEP, kunde, billingAuftragLeistungViews);
        ServiceCommandResult cmdResult = (ServiceCommandResult) cut.execute();
        CPSMVSData mvsData = soData.getMvs();
        CPSMVSEnterpriseData mvsEpData = mvsData.getEnterpriseData();
        CPSMVSAdminAccount adminAcc = mvsEpData.getAdminAccount();
        CPSMVSAdminAccount adminAccExp = cut.findAdminAcc();
        assertEquals(cmdResult.getCheckStatus(), ServiceCommandResult.CHECK_STATUS_OK);
        assertEquals(mvsEpData.getDomain(), cut.findDomain());
        assertEquals(mvsEpData.getResellerId(), cut.findResellerId());
        assertEquals(adminAcc.getEmail(), adminAccExp.getEmail());
        assertEquals(adminAcc.getPassword(), adminAccExp.getPassword());
        assertEquals(adminAcc.getUsername(), adminAccExp.getUsername());
    }

    @Test
    public void execute_withException() throws Exception {
        stubSpyAndMocks(auftragMvsEP, kunde, billingAuftragLeistungViews);
        RuntimeException exc = new RuntimeException("testmsg");
        doThrow(exc).when(cut).findResellerId();
        ServiceCommandResult cmdResult = (ServiceCommandResult) cut.execute();
        assertEquals(cmdResult.getCheckStatus(), ServiceCommandResult.CHECK_STATUS_INVALID);
        assertEquals(cmdResult.getMessage(), exc.getMessage());
    }

    @Test
    public void findLicences_success() throws Exception {
        stubSpyAndMocks(auftragMvsEP, kunde, billingAuftragLeistungViews);
        CPSMVSEnterpriseLicences result = cut.findLicences();
        assertEquals(result.getAttendantConsole(), positionMenge);
        assertEquals(result.getFaxToMail(), positionMenge);
        assertEquals(result.getIvr(), positionMenge);
        assertEquals(result.getMobileClient(), positionMenge);
        assertEquals(result.getThreeWayConference(), positionMenge);
        assertEquals(result.getLines(), Long.valueOf(positionMenge * Long.valueOf(serviceValueParam)));
        verify(billingAuftragServiceMock, times(1)).findAuftragLeistungViewsByExtProdNoAndExtMiscNOs(eq(auftragNoOrig),
                eq(Long.valueOf(Produkt.PROD_ID_MVS_ENTERPRISE)),
                eq(Arrays.asList(Leistung.getExtMiscNos4Mvs())));
    }

    @Test(expectedExceptions = { HurricanServiceCommandException.class })
    public void findLicences_NoLizenzpaketConfigured() throws Exception {
        List<BAuftragLeistungView> auftragLeistungViewsWithoutLizenzpaketPosition = new ArrayList<BAuftragLeistungView>();
        for (BAuftragLeistungView auftragPosition : billingAuftragLeistungViews) {
            if (NumberTools.notEqual(auftragPosition.getExternMiscNo(),
                    Leistung.EXT_MISC_NO_MVS_LIZENZPAKET)) {
                auftragLeistungViewsWithoutLizenzpaketPosition.add(auftragPosition);
            }
        }
        stubSpyAndMocks(auftragMvsEP, kunde, auftragLeistungViewsWithoutLizenzpaketPosition);
        cut.findLicences();
    }


    @Test
    public void findLicenceWithInActiveService() throws Exception {
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

        stubSpyAndMocks(auftragMvsEP, kunde, Arrays.asList(activeView, inactiveView, tomorrowActiveView));
        CPSMVSEnterpriseLicences licenses = cut.findLicences();
        assertNull(licenses.getFaxToMail());
        assertNull(licenses.getMobileClient());
        assertThat(licenses.getLines(), equalTo(activeView.getMenge()));
    }

}
