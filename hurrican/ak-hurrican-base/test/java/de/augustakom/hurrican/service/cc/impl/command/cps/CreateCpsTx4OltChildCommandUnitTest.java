/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.05.2014
 */
package de.augustakom.hurrican.service.cc.impl.command.cps;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.CPSTransactionBuilder;
import de.augustakom.hurrican.model.cc.HVTGruppeBuilder;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.model.cc.HVTTechnikBuilder;
import de.augustakom.hurrican.model.cc.HWDslamBuilder;
import de.augustakom.hurrican.model.cc.HWOltBuilder;
import de.augustakom.hurrican.model.cc.HWOntBuilder;
import de.augustakom.hurrican.model.cc.ReferenceBuilder;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.serviceorder.accessdevice.CpsResourceOrder;
import de.augustakom.hurrican.model.cc.hardware.HWOlt;
import de.augustakom.hurrican.model.cc.hardware.HWOltChild;
import de.augustakom.hurrican.model.cc.hardware.HWOnt;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CPSGetDataService;
import de.augustakom.hurrican.service.cc.CPSService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.ReferenceService;
import de.augustakom.hurrican.service.cc.fttx.EkpFrameContractService;
import de.augustakom.hurrican.service.cc.impl.CPSGetDataServiceImpl;

/**
 *
 */
@Test(groups = { BaseTest.UNIT })
public class CreateCpsTx4OltChildCommandUnitTest extends BaseTest {

    private static final Long WHOLESALE_PRODUCT_ID = 600L;

    @InjectMocks
    CreateCpsTx4OltChildCommand cut;

    @InjectMocks
    CPSGetDataService cpsGetDataService;

    @Mock
    protected CPSService cpsService;
    @Mock
    private HVTService hvtService;
    @Mock
    private HWService hwService;
    @Mock
    private ReferenceService referenceService;
    @Mock
    private EkpFrameContractService ekpFrameContractService;

    private HWOnt ontRack;
    private HWRack networkRack;

    @BeforeMethod
    public void setUp() throws FindException {
        cut = spy(new CreateCpsTx4OltChildCommand());
        cpsGetDataService = new CPSGetDataServiceImpl();
        cut.setCpsGetDataService(cpsGetDataService);
        XStreamMarshaller xmlMarshaller = new XStreamMarshaller();
        xmlMarshaller.setAnnotatedClasses(CpsResourceOrder.class);
        cut.setXmlMarshaller(xmlMarshaller);
        MockitoAnnotations.initMocks(this);
    }

    @DataProvider
    Object[][] createCpsTx4OntDataProvider() {
        return new Object[][] {
                { new HWOltBuilder().init().withGeraeteBez("OLT-123").withRandomId().setPersist(false), null },
                { null, new HWDslamBuilder().init().withGeraeteBez("OLT-123").withRandomId().setPersist(false) },
        };
    }

    @Test(dataProvider = "createCpsTx4OntDataProvider")
    public void testCreateCPSTx4Ont(final HWOltBuilder hwOltBuilder, final HWDslamBuilder hwDslamBuilder) throws Exception {
        initOntRackAndNetworkRack(hwOltBuilder, hwDslamBuilder);
        mockServices();

        CPSTransaction cpsTx = cut.execute();

        assertNotNull(cpsTx);
        assertEquals(new String(cpsTx.getServiceOrderData()), String.format("<RESOURCE_ORDER>\n"
                + "  <ACCESS_DEVICE>\n"
                + "    <ITEM>\n"
                + "      <DEACTIVATE>0</DEACTIVATE>\n"
                + "      <NETWORK_DEVICE>\n"
                + "        <TYPE>%s</TYPE>\n"
                + "        <MANUFACTURER>Huawei</MANUFACTURER>\n"
                + "        <NAME>OLT-123</NAME>\n"
                + "        <PORT>00-01-02-03-04</PORT>\n"
                + "        <PORT_TYPE>GPON</PORT_TYPE>\n"
                + "      </NETWORK_DEVICE>\n"
                + "      <ENDPOINT_DEVICE>\n"
                + "        <TYPE>ONT</TYPE>\n"
                + "        <HARDWARE_MODEL>O-123-T</HARDWARE_MODEL>\n"
                + "        <MANUFACTURER>Huawei</MANUFACTURER>\n"
                + "        <NAME>ONT-789</NAME>\n"
                + "        <TECH_ID>AGB-XYZ</TECH_ID>\n"
                + "        <SERIAL_NO>32303131EA757E01</SERIAL_NO>\n"
                + "        <LOCATION_TYPE>FTTH</LOCATION_TYPE>\n"
                + "      </ENDPOINT_DEVICE>\n"
                + "    </ITEM>\n"
                + "  </ACCESS_DEVICE>\n"
                + "</RESOURCE_ORDER>", (networkRack instanceof HWOlt) ? "OLT" : "GSLAM"));
    }

    @Test(dataProvider = "createCpsTx4OntDataProvider")
    public void testCreateCPSTx4OntAndWholesaleProduct(final HWOltBuilder hwOltBuilder, final HWDslamBuilder hwDslamBuilder) throws Exception {
        initOntRackAndNetworkRack(hwOltBuilder, hwDslamBuilder);
        mockServices();

        final AuftragDaten auftragDaten = new AuftragDatenBuilder().withRandomAuftragId().withProdId(WHOLESALE_PRODUCT_ID)
                .setPersist(false).build();
        final CPSTransaction cpsTransaction = new CPSTransactionBuilder().withRandomId().withEstimatedExecTime(new Date())
                .setPersist(false).build();

        doReturn(cpsTransaction).when(cut).getCPSTransaction();
        doReturn(auftragDaten).when(cut).getAuftragDaten();

        final CPSTransaction cpsTx = cut.execute();

        assertNotNull(cpsTx);
        assertEquals(new String(cpsTx.getServiceOrderData()), String.format("<RESOURCE_ORDER>\n"
                + "  <ACCESS_DEVICE>\n"
                + "    <ITEM>\n"
                + "      <DEACTIVATE>0</DEACTIVATE>\n"
                + "      <NETWORK_DEVICE>\n"
                + "        <TYPE>%s</TYPE>\n"
                + "        <MANUFACTURER>Huawei</MANUFACTURER>\n"
                + "        <NAME>OLT-123</NAME>\n"
                + "        <PORT>00-01-02-03-04</PORT>\n"
                + "        <PORT_TYPE>GPON</PORT_TYPE>\n"
                + "      </NETWORK_DEVICE>\n"
                + "      <ENDPOINT_DEVICE>\n"
                + "        <TYPE>ONT</TYPE>\n"
                + "        <HARDWARE_MODEL>O-123-T</HARDWARE_MODEL>\n"
                + "        <MANUFACTURER>Huawei</MANUFACTURER>\n"
                + "        <NAME>ONT-789</NAME>\n"
                + "        <TECH_ID>AGB-XYZ</TECH_ID>\n"
                + "        <SERIAL_NO>32303131EA757E01</SERIAL_NO>\n"
                + "        <LOCATION_TYPE>FTTH</LOCATION_TYPE>\n"
                + "      </ENDPOINT_DEVICE>\n"
                + "    </ITEM>\n"
                + "  </ACCESS_DEVICE>\n"
                + "</RESOURCE_ORDER>", (networkRack instanceof HWOlt) ? "OLT" : "GSLAM"));
    }


    @DataProvider
    Object[][] getTxSourceDP() {
        return new Object[][] {
                { HWRack.RACK_TYPE_MDU, true, CPSTransaction.TX_SOURCE_HURRICAN_MDU },
                { HWRack.RACK_TYPE_ONT, true, CPSTransaction.TX_SOURCE_HURRICAN_ONT },
                { HWRack.RACK_TYPE_DPO, true, CPSTransaction.TX_SOURCE_HURRICAN_DPO },
                { HWRack.RACK_TYPE_DPU, true, CPSTransaction.TX_SOURCE_HURRICAN_DPU },
                { "Something Else", false, null },
                { null, false, null },
        };
    }

    @Test(dataProvider = "getTxSourceDP")
    public void testGetTxSource(String rackType, boolean expectedResult, Long expectedTxSource) {
        HWOltChild hwOltChild = Mockito.mock(HWOltChild.class);
        when(hwOltChild.getRackTyp()).thenReturn(rackType);

        try {
            Long txSource = cut.getTxSource(hwOltChild);
            Assert.assertTrue(expectedResult);
            assertEquals(txSource, expectedTxSource);
        }
        catch (IllegalStateException e) {
            Assert.assertFalse(expectedResult);
        }
    }

    private void initOntRackAndNetworkRack(final HWOltBuilder hwOltBuilder, final HWDslamBuilder hwDslamBuilder) {
        if (hwOltBuilder != null) {
            networkRack = hwOltBuilder.get();
            ontRack = new HWOntBuilder().init().withRandomId().withGeraeteBez("ONT-789")
                    .withSerialNo("32303131EA757E01")
                    .withHWRackOltBuilder(hwOltBuilder).setPersist(false).get();
        }
        else if (hwDslamBuilder != null) {
            networkRack = hwDslamBuilder.get();
            ontRack = new HWOntBuilder().init().withRandomId().withGeraeteBez("ONT-789")
                    .withSerialNo("32303131EA757E01")
                    .setPersist(false).get();
            ontRack.setOltRackId(networkRack.getId());
        }
        else {
            throw new RuntimeException("Fehler in den Testdaten: olt oder gslam/dslam muss gesetzt sein");
        }
    }

    private void mockServices() throws Exception {
        when(hwService.findRackById(eq(ontRack.getId()))).thenReturn(ontRack);
        when(hwService.findRackById(eq(networkRack.getId()))).thenReturn(networkRack);
        when(hvtService.findHVTTechnik(anyLong())).thenReturn(
                new HVTTechnikBuilder().toHuawei().setPersist(false).get());
        HVTStandortBuilder hvtStandortBuilder = new HVTStandortBuilder().init()
                .withHvtGruppeBuilder(new HVTGruppeBuilder().init().withOrtsteil("AGB-XYZ").setPersist(false))
                .withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_FTTH)
                .setPersist(false);
        when(hvtService.findHVTStandort(anyLong())).thenReturn(hvtStandortBuilder.get());
        when(hvtService.findHVTGruppe4Standort(anyLong())).thenReturn(hvtStandortBuilder.getHvtGruppeBuilder().get());
        when(referenceService.findReference(anyLong())).thenReturn(
                new ReferenceBuilder().init().withStrValue("FTTH").setPersist(false).build());

        cut.prepare(CreateCpsTx4OltChildCommand.KEY_RACK_ID, ontRack.getId());
        cut.prepare(CreateCpsTx4OltChildCommand.KEY_SERVICE_ORDER_TYPE, CPSTransaction.SERVICE_ORDER_TYPE_CREATE_DEVICE);
    }

}
