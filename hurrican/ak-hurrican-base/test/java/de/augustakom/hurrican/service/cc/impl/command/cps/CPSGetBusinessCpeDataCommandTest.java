/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.12.2010 09:07:27
 */
package de.augustakom.hurrican.service.cc.impl.command.cps;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.CPSTransactionBuilder;
import de.augustakom.hurrican.model.cc.EG2Auftrag;
import de.augustakom.hurrican.model.cc.EG2AuftragBuilder;
import de.augustakom.hurrican.model.cc.EGBuilder;
import de.augustakom.hurrican.model.cc.EGConfig;
import de.augustakom.hurrican.model.cc.EGConfigBuilder;
import de.augustakom.hurrican.model.cc.EGTypeBuilder;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSBusinessCpeData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSServiceOrderData;
import de.augustakom.hurrican.model.cc.view.EG2AuftragView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.EndgeraeteService;

/**
 * TestNG Test fuer {@link CPSGetBusinessCpeDataCommand}
 */
@Test(groups = { BaseTest.UNIT })
public class CPSGetBusinessCpeDataCommandTest extends BaseTest {

    private static final String SERIAL_NO = "SrNo123456";
    private static final String MODELL_BEZ = "Modell-Bez.";
    private static final String EG_HERSTELLER = "EG-Hersteller";

    private CPSGetBusinessCpeDataCommand cut;
    private EndgeraeteService endgeraeteServiceMock;
    private CCLeistungsService leistungsServiceMock;
    private CPSServiceOrderData cpsServiceOrderData = new CPSServiceOrderData();

    private List<EG2AuftragView> eg2AuftragViews;
    private EG2AuftragView eg2AuftragView;
    private EG2Auftrag expectedEG2Auftrag;
    private EGConfig egConfig;

    private TechLeistung techLsVoip;

    @SuppressWarnings("unused")
    @BeforeMethod
    private void prepareTest() throws FindException {
        cut = spy(new CPSGetBusinessCpeDataCommand());

        AuftragBuilder auftragBuilder = new AuftragBuilder().withRandomId();
        CPSTransaction cpsTx = new CPSTransactionBuilder().withAuftragBuilder(auftragBuilder).setPersist(false).build();
        cut.prepare(CPSGetBusinessCpeDataCommand.KEY_CPS_TRANSACTION, cpsTx);
        cut.prepare(CPSGetBusinessCpeDataCommand.KEY_SERVICE_ORDER_DATA, cpsServiceOrderData);

        endgeraeteServiceMock = mock(EndgeraeteService.class);
        leistungsServiceMock = mock(CCLeistungsService.class);
        cut.setEndgeraeteService(endgeraeteServiceMock);
        cut.setLeistungsService(leistungsServiceMock);

        expectedEG2Auftrag = new EG2AuftragBuilder().withRandomId().withAuftragBuilder(auftragBuilder)
                .withEgBuilder(new EGBuilder()).setPersist(false).build();

        // +configuration
        // +CPS allowed
        // +active
        eg2AuftragView = new EG2AuftragView();
        eg2AuftragView.setHasConfiguration(Boolean.TRUE);
        eg2AuftragView.setCpsProvisioning(Boolean.TRUE);
        eg2AuftragView.setEg2AuftragId(expectedEG2Auftrag.getId());
        eg2AuftragViews = new ArrayList<EG2AuftragView>();
        eg2AuftragViews.add(eg2AuftragView);
        eg2AuftragViews.add(eg2AuftragView);

        // +configuration
        // -CPS allowed
        // +active
        EG2AuftragView eg2AuftragViewWithoutCps = new EG2AuftragView();
        eg2AuftragViewWithoutCps.setHasConfiguration(Boolean.TRUE);
        eg2AuftragViews.add(eg2AuftragViewWithoutCps);

        // -configuration
        // -CPS allowed
        // +active
        EG2AuftragView eg2AuftragViewWithoutConfig = new EG2AuftragView();
        eg2AuftragViewWithoutConfig.setHasConfiguration(Boolean.FALSE);
        eg2AuftragViews.add(eg2AuftragViewWithoutConfig);

        // +configuration
        // +CPS allowed
        // -active
        EG2AuftragView eg2AuftragViewDeactivated = new EG2AuftragView();
        eg2AuftragViewDeactivated.setHasConfiguration(Boolean.TRUE);
        eg2AuftragViewDeactivated.setCpsProvisioning(Boolean.TRUE);
        eg2AuftragViewDeactivated.setDeactivated(Boolean.TRUE);
        eg2AuftragViews.add(eg2AuftragViewDeactivated);

        EGTypeBuilder egTypeBuilder = new EGTypeBuilder().withHersteller(EG_HERSTELLER).withModell(MODELL_BEZ)
                .setPersist(false);

        egConfig = new EGConfigBuilder().withEGTypeBuilder(egTypeBuilder).withSerialNumber(SERIAL_NO).setPersist(false)
                .build();

        techLsVoip = new TechLeistung();
        techLsVoip.setId(TechLeistung.ID_VOIP_MGA);
        techLsVoip.setStrValue("ISDN");
        techLsVoip.setTyp(TechLeistung.TYP_VOIP);
    }

    private void stubEGServiceMock() throws FindException {
        when(endgeraeteServiceMock.findEG2AuftragViews(any(Long.class))).thenReturn(eg2AuftragViews);
        when(endgeraeteServiceMock.findEGConfig(eg2AuftragView.getEg2AuftragId())).thenReturn(egConfig);
        when(endgeraeteServiceMock.findEG2AuftragById(eg2AuftragView.getEg2AuftragId())).thenReturn(expectedEG2Auftrag);

        TechLeistung ls = new TechLeistung();
        ls.setTyp("TEST");
        ls.setStrValue("TEST");

        List<TechLeistung> techLs = new ArrayList<TechLeistung>();
        techLs.add(techLsVoip);
        techLs.add(ls);

        when(leistungsServiceMock.findTechLeistungen4Auftrag(anyLong(), eq(TechLeistung.TYP_VOIP), eq(Boolean.TRUE)))
                .thenReturn(techLs);
    }

    public void testFindEgConfiguration() throws FindException, HurricanServiceCommandException {
        stubEGServiceMock();

        Pair<EGConfig, EG2Auftrag> result = cut.findEgConfigurations().get(0);
        assertNotNull(result, "EG-Konfig nicht gefunden!");
        assertNotNull(result.getFirst(), "EGConfig not defined!");
        assertNotNull(result.getSecond(), "EG2Auftrag not defined!");
    }

    @Test(expectedExceptions = HurricanServiceCommandException.class)
    public void testFindEgConfigurationInvalidConfig() throws FindException, HurricanServiceCommandException {
        EG2AuftragView secondConfig = new EG2AuftragView();
        secondConfig.setHasConfiguration(Boolean.TRUE);
        secondConfig.setCpsProvisioning(Boolean.TRUE);
        eg2AuftragViews.add(secondConfig);

        stubEGServiceMock();
        cut.findEgConfigurations();
    }

    @Test(expectedExceptions = HurricanServiceCommandException.class)
    public void testFindEgConfigurationInvalidEGCount() throws FindException, HurricanServiceCommandException {
        EG2AuftragView secondConfig = new EG2AuftragView();
        secondConfig.setHasConfiguration(Boolean.TRUE);
        secondConfig.setCpsProvisioning(Boolean.TRUE);
        eg2AuftragView.setEg2AuftragId(expectedEG2Auftrag.getId());
        eg2AuftragViews.add(secondConfig);

        EG2AuftragView thirdConfig = new EG2AuftragView();
        thirdConfig.setHasConfiguration(Boolean.TRUE);
        thirdConfig.setCpsProvisioning(Boolean.TRUE);
        eg2AuftragView.setEg2AuftragId(expectedEG2Auftrag.getId());
        eg2AuftragViews.add(thirdConfig);

        stubEGServiceMock();
        cut.findEgConfigurations();
    }

    public void testExecute() throws Exception {
        stubEGServiceMock();
        doReturn(true).when(cut).businessCpeNecessary();

        Object result = cut.execute();
        assertTrue((result instanceof ServiceCommandResult), "Command returned other object as expected!");
        assertNotEmpty(cpsServiceOrderData.getCustomerPremisesEquipments(),
                "No CUSTOMER_PREMISES_EQUPIMENT data found!");
        assertEquals(cpsServiceOrderData.getCustomerPremisesEquipments().size(), 2);
        CPSBusinessCpeData cpe = cpsServiceOrderData.getCustomerPremisesEquipments().get(0);
        assertEquals(cpe.getManufacturer(), EG_HERSTELLER);
        assertEquals(cpe.getType(), MODELL_BEZ);
        assertEquals(cpe.getSerialNo(), SERIAL_NO);
        assertEquals(cpe.getVoiceType(), techLsVoip.getStrValue());
    }

    @DataProvider(name = "businessCpeNecessaryDataProvider")
    public Object[][] deviceNecessaryDataProvider() {
        TechLeistung a = new TechLeistung();
        a.setId(Long.valueOf(1L));
        TechLeistung b = new TechLeistung();
        b.setId(Long.valueOf(2L));
        TechLeistung cpe = new TechLeistung();
        cpe.setId(TechLeistung.ID_BUSINESS_CPE);

        // @formatter:off
        return new Object[][] {
                { Collections.emptyList(), false },
                { xconnList(a)             , false },
                { xconnList(a, b)          , false },
                { xconnList(cpe)           , true },
                { xconnList(a, cpe)        , true },
        };
        // @formatter:on
    }

    List<TechLeistung> xconnList(TechLeistung... in) {
        List<TechLeistung> out = new ArrayList<>(in.length);
        Collections.addAll(out, in);
        return out;
    }

    @Test(dataProvider = "businessCpeNecessaryDataProvider")
    public void testBusinessCpeNecessary(List<TechLeistung> xconnLeistungen, boolean expectedResult) throws FindException {
        when(
                leistungsServiceMock.findTechLeistungen4Auftrag(any(Long.class), eq(TechLeistung.TYP_CROSS_CONNECTION),
                        any(Date.class))
        ).thenReturn(xconnLeistungen);
        doReturn(new CPSTransaction()).when(cut).getCPSTransaction();

        boolean businessCpeNecessary = cut.businessCpeNecessary();
        assertEquals(businessCpeNecessary, expectedResult);
    }

}
