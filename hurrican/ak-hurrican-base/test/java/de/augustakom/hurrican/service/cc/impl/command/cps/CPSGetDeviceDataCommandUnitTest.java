/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.03.2012 06:50:41
 */
package de.augustakom.hurrican.service.cc.impl.command.cps;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import com.google.common.collect.Lists;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.service.exceptions.ServiceCommandException;
import de.augustakom.hurrican.model.billing.Device;
import de.augustakom.hurrican.model.billing.DeviceBuilder;
import de.augustakom.hurrican.model.cc.Anschlussart;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.CPSTransactionBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppeBuilder;
import de.augustakom.hurrican.model.cc.HWDslamBuilder;
import de.augustakom.hurrican.model.cc.HWMduBuilder;
import de.augustakom.hurrican.model.cc.HWOntBuilder;
import de.augustakom.hurrican.model.cc.PhysikTyp;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.ProduktBuilder;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.RangierungBuilder;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.TechLeistungBuilder;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.DeviceService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.mnet.common.tools.DateConverterUtils;

@Test(groups = BaseTest.UNIT)
public class CPSGetDeviceDataCommandUnitTest extends BaseTest {

    @Mock
    protected RangierungsService rangierungsService;
    @Mock
    protected EndstellenService endstellenService;
    @Mock
    protected HVTService hvtService;
    @Mock
    protected ProduktService produktService;
    @Spy
    @InjectMocks
    private CPSGetDeviceDataCommand cut;
    @Mock
    private DeviceService deviceServiceMock;
    @Mock
    private CCLeistungsService ccLeistungsServiceMock;
    @Mock
    private HWService hwService;

    private CPSTransaction cpsTx;

    private AuftragDaten auftragDaten;
    private Endstelle esB;
    private Equipment eqIn;

    @BeforeMethod
    public void setUp() throws Exception {
        cut = new CPSGetDeviceDataCommand();
        MockitoAnnotations.initMocks(this);

        final AuftragBuilder auftragBuilder = new AuftragBuilder().withRandomId();
        auftragDaten = new AuftragDatenBuilder().withAuftragBuilder(auftragBuilder).setPersist(false)
                .build();

        esB = new EndstelleBuilder().withRandomId().build();
        eqIn = new EquipmentBuilder().withRandomId().withBaugruppeBuilder(new HWBaugruppeBuilder().withRandomId()).build();

        cpsTx = new CPSTransactionBuilder().withAuftragBuilder(auftragBuilder).setPersist(false).build();
        doReturn(cpsTx).when(cut).getCPSTransaction();
        doReturn(auftragDaten).when(cut).getAuftragDaten();

        when(endstellenService.findEndstelle4Auftrag(auftragDaten.getAuftragId(), Endstelle.ENDSTELLEN_TYP_B)).thenReturn(esB);
        when(rangierungsService.findEquipment4Endstelle(esB, false, false)).thenReturn(eqIn);
        when(hwService.findRackForBaugruppe(eqIn.getHwBaugruppenId())).thenReturn(new HWOntBuilder().withRandomId().build());
    }

    @DataProvider(name = "executeAndCheckErrorHandlingOnDevicesDataProvider")
    public Object[][] executeAndCheckErrorHandlingOnDevicesDataProvider() {
        final Date validFrom = DateConverterUtils.asDate(LocalDate.now().minusYears(50));
        Device device = new DeviceBuilder().setPersist(false).withValidFrom(validFrom).build();

        // @formatter:off
        return new Object[][] {
                { Arrays.asList(device), Boolean.TRUE, 1, true },  // Devices vorhanden + notwendig  --> createDeviceData wird aufgerufen --> Result OK
                { Arrays.asList(), Boolean.TRUE, 0, true },       // keine Devices, aber notwendig  --> createDeviceData wird nicht aufgerufen --> Result INVALD
                { null, Boolean.TRUE, 0, true },                  // wie zuvor, aber statt leere Liste mit NULL
                { Arrays.asList(), Boolean.FALSE, 0, true },       // keine Devices, nicht notwendig --> createDeviceData wird nicht aufgerufen --> Result OK
                { null, Boolean.FALSE, 0, true },                  // wie zuvor, aber statt leere Liste mit NULL
        };
        // @formatter:on
    }

    @Test(dataProvider = "executeAndCheckErrorHandlingOnDevicesDataProvider")
    public void executeAndCheckErrorHandlingOnDevices(List<Device> devices, Boolean deviceNecessary,
            int createDeviceDataCallCount, boolean expectedResult) throws Exception {
        when(deviceServiceMock.findDevices4Auftrag(any(Long.class), any(String.class), any(String.class))).thenReturn(
                devices);
        doReturn(deviceNecessary).when(cut).deviceNecessary();

        if (createDeviceDataCallCount > 0) {
            doReturn(ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_OK, null, null)).when(cut)
                    .createDeviceData(devices);
        }

        ServiceCommandResult result = (ServiceCommandResult) cut.execute();
        assertEquals(result.isOk(), expectedResult);

        verify(cut, times(createDeviceDataCallCount)).createDeviceData(devices);
    }

    @DataProvider(name = "deviceNecessaryWhenFtthAndNoCpeLeistungDataProvider")
    Object[][] deviceNecessaryWhenFtthAndNoCpeLeistungDataProvider() {
        return new Object[][] {
                //ENDG   FTTH   FTTB   Produkt-ID                      PhysikTyp-ID                   erwartetes Ergebnis
                { true , true , false, Produkt.PROD_ID_FTTX_TELEFONIE, PhysikTyp.PHYSIKTYP_FTTH_ETH , true  },
                { false, true , false, Produkt.PROD_ID_FTTX_TELEFONIE, PhysikTyp.PHYSIKTYP_FTTH_POTS, false },
                { false, false, true , Produkt.PROD_ID_FTTX_TELEFONIE, PhysikTyp.PHYSIKTYP_FTTB_VDSL, true  },
                { false, false, true , Produkt.PROD_ID_FTTX_TELEFONIE, PhysikTyp.PHYSIKTYP_FTTB_POTS, false },

                { false, false, false, Produkt.PROD_ID_DSL_VOIP, PhysikTyp.PHYSIKTYP_ADSL2P, true },

                { true , true , false, Produkt.PROD_ID_MNET_KABEL_TV, PhysikTyp.PHYSIKTYP_FTTH_RF,   true },
                { false, true , false, Produkt.PROD_ID_MNET_KABEL_TV, PhysikTyp.PHYSIKTYP_FTTH_RF,   true },
                { false, false, true , Produkt.PROD_ID_MNET_KABEL_TV, PhysikTyp.PHYSIKTYP_FTTB_RF,   false },

                { true , true , false, Produkt.PROD_ID_FTTX_DSL_FON, PhysikTyp.PHYSIKTYP_FTTH_ETH , true },
                { false, true , false, Produkt.PROD_ID_FTTX_DSL_FON, PhysikTyp.PHYSIKTYP_FTTH_ETH , true },
                { false, false, true , Produkt.PROD_ID_FTTX_DSL_FON, PhysikTyp.PHYSIKTYP_FTTB_VDSL, true },
        };
    }

    @Test(dataProvider = "deviceNecessaryWhenFtthAndNoCpeLeistungDataProvider")
    public void deviceNecessaryWhenFtthAndNoCpeLeistung(boolean findTechLs, boolean isFtth, boolean isFttb, long prodId,
            Long physikTypId, boolean expectedResult)
            throws FindException, ServiceCommandException {
        // @formatter:off
        when(ccLeistungsServiceMock.deviceNecessary(cpsTx.getAuftragId(), cpsTx.getEstimatedExecTime())).thenReturn(true);
        // @formatter:on

        final Endstelle esB = new EndstelleBuilder().withRandomId().withAnschlussart(
                (isFtth) ? Anschlussart.ANSCHLUSSART_FTTH :
                        (isFttb) ? Anschlussart.ANSCHLUSSART_FTTB : Anschlussart.ANSCHLUSSART_KVZ
        ).build();
        final TechLeistung techLs = new TechLeistungBuilder().withRandomId().build();
        final HVTStandort hvtStandort = new HVTStandortBuilder().withRandomId().withStandortTypRefId(
                (isFtth) ? HVTStandort.HVT_STANDORT_TYP_FTTH :
                        (isFttb) ? HVTStandort.HVT_STANDORT_TYP_FTTB : HVTStandort.HVT_STANDORT_TYP_FTTC_KVZ
        ).build();
        Rangierung rangierung = new RangierungBuilder().withPhysikTypId(physikTypId).build();

        when(endstellenService.findEndstelle4Auftrag(cpsTx.getAuftragId(), Endstelle.ENDSTELLEN_TYP_B)).thenReturn(esB);
        if (findTechLs) {
            when(ccLeistungsServiceMock.findTechLeistungen4Auftrag(cpsTx.getAuftragId(), TechLeistung.TYP_ENDGERAET,
                    cpsTx.getEstimatedExecTime())).thenReturn(Lists.newArrayList(techLs));
        }
        when(hvtService.findHVTStandort(esB.getHvtIdStandort())).thenReturn(hvtStandort);
        when(produktService.findProdukt4Auftrag(anyLong())).thenReturn(new ProduktBuilder().withId(prodId).build());
        when(endstellenService.findEndstelle4Auftrag(auftragDaten.getAuftragId(), Endstelle.ENDSTELLEN_TYP_B)).thenReturn(esB);
        when(rangierungsService.findEquipment4Endstelle(esB, false, false)).thenReturn(eqIn);
        when(rangierungsService.findRangierungenTxWithoutExplicitFlush(auftragDaten.getAuftragId(), Endstelle.ENDSTELLEN_TYP_B))
                .thenReturn(new Rangierung[] {rangierung});

        if (isFttb) {
            when(hwService.findRackForBaugruppe(eqIn.getHwBaugruppenId())).thenReturn(new HWMduBuilder().withRandomId().build());
        }
        else if (!isFtth) {
            when(hwService.findRackForBaugruppe(eqIn.getHwBaugruppenId())).thenReturn(new HWDslamBuilder().withRandomId().build());
        }

        assertEquals(cut.deviceNecessary(), expectedResult);
    }
}
