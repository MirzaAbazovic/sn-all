/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.05.2014 07:31
 */
package de.augustakom.hurrican.service.cc.impl;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;

import java.util.*;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragTechnik2EndstelleBuilder;
import de.augustakom.hurrican.model.cc.DSLAMProfileBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTGruppeBuilder;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.model.cc.HVTTechnik;
import de.augustakom.hurrican.model.cc.HVTTechnikBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppeBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppenTypBuilder;
import de.augustakom.hurrican.model.cc.HWDslamBuilder;
import de.augustakom.hurrican.model.cc.HWMduBuilder;
import de.augustakom.hurrican.model.cc.HWOltBuilder;
import de.augustakom.hurrican.model.cc.HWOntBuilder;
import de.augustakom.hurrican.model.cc.RangierungBuilder;
import de.augustakom.hurrican.model.cc.ReferenceBuilder;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnungBuilder;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSPBITData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSVlanData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.accessdevice.CpsAccessDevice;
import de.augustakom.hurrican.model.cc.cps.serviceorder.accessdevice.CpsEndpointDevice;
import de.augustakom.hurrican.model.cc.cps.serviceorder.accessdevice.CpsItem;
import de.augustakom.hurrican.model.cc.cps.serviceorder.accessdevice.CpsLayer2Config;
import de.augustakom.hurrican.model.cc.cps.serviceorder.accessdevice.CpsNetworkDevice;
import de.augustakom.hurrican.model.cc.fttx.A10NspBuilder;
import de.augustakom.hurrican.model.cc.fttx.A10NspPortBuilder;
import de.augustakom.hurrican.model.cc.fttx.Auftrag2EkpFrameContractBuilder;
import de.augustakom.hurrican.model.cc.fttx.CvlanServiceTyp;
import de.augustakom.hurrican.model.cc.fttx.EkpFrameContractBuilder;
import de.augustakom.hurrican.model.cc.fttx.EqVlan;
import de.augustakom.hurrican.model.cc.fttx.EqVlanBuilder;
import de.augustakom.hurrican.model.cc.hardware.HWDslam;
import de.augustakom.hurrican.model.cc.hardware.HWMdu;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.DSLAMService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.service.cc.ReferenceService;
import de.augustakom.hurrican.service.cc.fttx.EkpFrameContractService;
import de.augustakom.hurrican.service.cc.fttx.VlanService;
import de.augustakom.hurrican.service.cc.impl.command.cps.CpsFacade;
import de.augustakom.hurrican.service.cc.impl.command.cps.PbitHelper;

@Test(groups = { BaseTest.UNIT })
public class CpsGetDataServiceImplGetFtthDataTest {

    public static final String NETWORK_DEVICE_MANUFACTURER = "testmanufacturer";
    public static final String ACCESS_DEVICE_MANUFACTURER = "HUAWEI";
    public static final String NETWORK_DEVICE_NAME = "Testgeraet";
    public static final String PORT = "1-1-2-1-003";
    public static final String HARDWARE_MODEL = "O-0881V-P";
    public static final String TECH_ID = "MUC-PFEUF-010";
    public static final String ENDPOINT_DEVICE_NAME = "ONT-xxx";
    public static final String ENDPOINT_DEVICE_SERIAL_NO = "ALCLF8A19C5B";
    public static final String ENDPOINT_DEVICE_STANDORT_TYP = "FTTH";
    public static final String ENDPOINT_PORT_HWEQN = "8-1";
    public static final String ENDPOINT_PORT_TYPE = "ETH";
    public static final int DOWNSTREAM = 50000;
    public static final int UPSTREAM = 5000;
    public static final String PORT_NAME = "WV10048354--Jiang";
    public static final String TARGET_MARGIN_DOWN = "6";
    public static final String TARGET_MARGIN_UP = "5";
    public static final long AUFTRAG_ID = 9871234L;
    private static final String EKP_ID = "QSC";
    private static final String FRAME_CONTRACT_ID = "QSC-001";
    private static final String A10NSP = "A10-NSP";
    private static final String VBZ = "DEU.MNET.000001";

    List<CPSPBITData> pbits;

    CPSPBITData voipPbit;

    HWOntBuilder ontRack = createOntRack();
    HWBaugruppeBuilder ontBaugruppe = new HWBaugruppeBuilder().withId(987L).withRackBuilder(ontRack);
    EquipmentBuilder ontPort;

    HWOltBuilder oltRack = createOltRack();
    HWBaugruppeBuilder oltBaugruppe = new HWBaugruppeBuilder().withId(789L).withRackBuilder(oltRack);

    private AuftragDatenBuilder auftragDaten = createAuftragDaten();
    private EkpFrameContractBuilder ekpFrameContract = createEkpFrameContract();
    private Auftrag2EkpFrameContractBuilder auftrag2EkpFrameContract = createAuftrag2EkpFrameContract();
    private A10NspBuilder a10Nsp = createA10Nsp();
    private VerbindungsBezeichnungBuilder vbz = createVbzBuilder();
    private A10NspPortBuilder a10NspPort = createA10NspPort();

    private static long ENDPOINT_STANDORT_TYP_REF_ID = 4321L;

    private HVTStandortBuilder ftthStandort;
    private EndstelleBuilder endstelleBuilder;

    private EquipmentBuilder createOntPort(final HWBaugruppeBuilder bgBuilder) {
        return new EquipmentBuilder()
                .withId(1234L)
                .withHwEQN(ENDPOINT_PORT_HWEQN)
                .withBaugruppeBuilder(bgBuilder);
    }

    private HWOntBuilder createOntRack() {
        return new HWOntBuilder()
                .withId(1928L)
                .withRackTyp(HWRack.RACK_TYPE_ONT)
                .withOntType(HARDWARE_MODEL)
                .withHwProducerBuilder(new HVTTechnikBuilder().toHuawei().withCpsName(ACCESS_DEVICE_MANUFACTURER))
                .withGeraeteBez(ENDPOINT_DEVICE_NAME)
                .withSerialNo(ENDPOINT_DEVICE_SERIAL_NO)
                .withOltFrame("1")
                .withOltSubrack("1")
                .withOltSlot("2")
                .withOltGPONPort("1")
                .withOltGPONId("003");
    }

    private HWOltBuilder createOltRack() {
        return new HWOltBuilder()
                .withId(8291L)
                .withRackTyp(HWRack.RACK_TYPE_OLT)
                .withGeraeteBez(NETWORK_DEVICE_NAME);
    }

    @InjectMocks
    private CPSGetDataServiceImpl cut = new CPSGetDataServiceImpl();

    @Mock
    private HVTService hvtService;
    @Mock
    private ReferenceService referenceService;
    @Mock
    private HWService hwService;
    @Mock
    private DSLAMService dslamService;
    @Mock
    private EndstellenService endstellenService;
    @Mock
    private RangierungsService rangierungsService;
    @Mock
    private PbitHelper pbitHelper;
    @Mock
    private CpsFacade cpsFacade;
    @Mock
    private VlanService vlanService;
    @Mock
    private CCAuftragService auftragService;
    @Mock
    private EkpFrameContractService ekpFrameContractService;

    @BeforeMethod
    public void setUp() throws Exception {
        initMocks(this);

        pbits = Lists.newArrayList();

        ontRack = createOntRack();
        ontRack.get();
        ontBaugruppe = new HWBaugruppeBuilder().withId(987L).withRackBuilder(ontRack)
                .withBaugruppenTypBuilder(new HWBaugruppenTypBuilder().setOntEthValues());
        ontPort = createOntPort(ontBaugruppe);

        oltRack = createOltRack();
        oltBaugruppe = new HWBaugruppeBuilder().withId(789L).withRackBuilder(oltRack);

        voipPbit = new CPSPBITData();
        voipPbit.limit = 12;
        voipPbit.service = "VOIP";
        pbits.add(voipPbit);

        final EqVlan hsiVlan = new EqVlanBuilder()
                .withRandomId()
                .withCvlanTyp(CvlanServiceTyp.HSI)
                .build();

        final DSLAMProfileBuilder profile = createProfile();

        when(hwService.findRackById(ontRack.get().getOltRackId())).thenReturn(oltRack.get());

        when(hwService.findBaugruppe(eq(ontPort.get().getHwBaugruppenId()))).thenReturn(ontBaugruppe.get());
        when(hwService.findRackForBaugruppe(eq(ontBaugruppe.getId()))).thenReturn(ontRack.get());

        when(hvtService.findHVTGruppe4Standort(anyLong())).thenReturn(createEndpointDeviceStandort());
        when(hvtService.findHVTStandort(anyLong())).thenReturn(new HVTStandortBuilder().withStandortTypRefId(ENDPOINT_STANDORT_TYP_REF_ID).build());
        when(referenceService.findReference(eq(ENDPOINT_STANDORT_TYP_REF_ID))).thenReturn(new ReferenceBuilder().withStrValue(ENDPOINT_DEVICE_STANDORT_TYP).build());

        when(hvtService.findHVTTechnik(anyLong())).thenAnswer(new Answer<HVTTechnik>() {
            @Override
            public HVTTechnik answer(InvocationOnMock invocationOnMock) throws Throwable {
                Long producerId = (Long) invocationOnMock.getArguments()[0];
                if (producerId != null && producerId.equals(ontRack.get().getHwProducer())) {
                    return ontRack.getHwProducerBuilder().get();
                }
                return createHvtTechnik();
            }
        });

        when(dslamService.findDslamProfile4AuftragOrCalculateDefault(eq(AUFTRAG_ID), org.mockito.Matchers.any(Date.class)))
                .thenReturn(profile.get());

        ftthStandort = createFtthStandort();
        endstelleBuilder = createEndstelle(ftthStandort);
        when(endstellenService.findEndstelle4Auftrag(AUFTRAG_ID, Endstelle.ENDSTELLEN_TYP_B))
                .thenReturn(endstelleBuilder.build());
        when(hvtService.findHVTStandort(endstelleBuilder.get().getHvtIdStandort())).thenReturn(ftthStandort.get());

        when(rangierungsService.findRangierung(endstelleBuilder.get().getRangierId())).thenReturn(endstelleBuilder.getRangierungBuilder().get());
        when(rangierungsService.findEquipment(endstelleBuilder.getRangierungBuilder().get().getEqInId())).thenReturn(ontPort.get());

        when(pbitHelper.getVoipPbitIfNecessary(eq(profile.get()), eq(AUFTRAG_ID), org.mockito.Matchers.any(Date.class))).thenReturn(voipPbit);

        when(vlanService.findEqVlans(eq(ontPort.getId()), org.mockito.Matchers.any(Date.class))).thenReturn(Lists.newArrayList(hsiVlan));
        when(cpsFacade.findFttxPort(endstelleBuilder.get())).thenReturn(ontPort.get());
        when(cpsFacade.findBaugruppeByPort(ontPort.get())).thenReturn(ontBaugruppe.get());
        when(cpsFacade.getEndstelleBWithStandortId(AUFTRAG_ID)).thenReturn(endstelleBuilder.build());
        when(cpsFacade.findRackByPort(ontPort.get())).thenReturn(ontRack.get());
    }

    private EndstelleBuilder createEndstelle(HVTStandortBuilder ftthStandort) {
        return new EndstelleBuilder()
                .withRandomId()
                .withEndstelleGruppeBuilder(new AuftragTechnik2EndstelleBuilder().withRandomId())
                .withRangierungBuilder(new RangierungBuilder().withRandomId().withEqInBuilder(ontPort))
                .withHvtStandortBuilder(ftthStandort);
    }

    private HVTTechnik createHvtTechnik() {
        return new HVTTechnikBuilder()
                .withRandomId()
                .withHersteller(NETWORK_DEVICE_MANUFACTURER)
                .withCpsName(NETWORK_DEVICE_MANUFACTURER)
                .build();
    }

    @Test
    public void testGetFtthData() {
        final CpsAccessDevice result = cut.getFtthData(AUFTRAG_ID, new Date(), false, PORT_NAME);
        assertThat(result.getItems(), hasSize(1));
    }

    private HVTStandortBuilder createFtthStandort() {
        return new HVTStandortBuilder()
                .withRandomId()
                .withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_FTTH);
    }

    private HVTGruppe createEndpointDeviceStandort() {
        return new HVTGruppeBuilder()
                .withRandomId()
                .withOrtsteil(TECH_ID)
                .build();
    }

    @Test
    public void testExpectNetworkDevice() throws Exception {
        final CpsNetworkDevice cpsNetworkDevice = getNetworkDeviceFromFtthData(HWRack.RACK_TYPE_OLT);

        assertThat(cpsNetworkDevice, notNullValue(CpsNetworkDevice.class));
        assertThat(cpsNetworkDevice.getManufacturer(), equalTo(NETWORK_DEVICE_MANUFACTURER));
        assertThat(cpsNetworkDevice.getName(), equalTo(NETWORK_DEVICE_NAME));
        assertThat(cpsNetworkDevice.getPort(), equalTo(PORT));
        assertThat(cpsNetworkDevice.getPortType(), equalTo("GPON"));
        assertThat(cpsNetworkDevice.getHardwareModel(), nullValue(String.class));
    }

    private CpsNetworkDevice getNetworkDeviceFromFtthData(final String rackType) throws Exception {
        final CpsItem cpsItem = getItemFromFtthData(rackType);
        return cpsItem.getNetworkDevice();
    }

    private CpsEndpointDevice getEndpointDeviceFromFtthData(final String rackType) throws Exception {
        return getEndpointDeviceFromFtthData(rackType, false);
    }

    private CpsEndpointDevice getEndpointDeviceFromFtthData(final String rackType, boolean iadConfigured) throws Exception {
        return getItemFromFtthData(rackType, iadConfigured).getEndpointDevice();
    }


    private CpsItem getItemFromFtthData(String rackType) throws Exception {
        return getItemFromFtthData(rackType, false);
    }

    private CpsItem getItemFromFtthData(String rackType, boolean iadConfigured) throws Exception {
        switch (rackType) {
            case HWRack.RACK_TYPE_DSLAM: {
                when(hwService.findRackById(ontRack.get().getOltRackId())).thenReturn(createDslam());
                break;
            }
            case HWRack.RACK_TYPE_MDU: {
                when(hwService.findRackById(ontRack.get().getOltRackId())).thenReturn(createMduRack());
                break;
            }
        }
        final CpsAccessDevice cpsAccessDevice = cut.getFtthData(AUFTRAG_ID, new Date(), iadConfigured, PORT_NAME);
        return Iterables.getOnlyElement(cpsAccessDevice.getItems());
    }

    private HWMdu createMduRack() {
        return new HWMduBuilder()
                .withRackTyp(HWRack.RACK_TYPE_MDU)
                .withRandomId()
                .build();
    }

    private DSLAMProfileBuilder createProfile() {
        return new DSLAMProfileBuilder()
                .withRandomId()
                .withBandwidth(DOWNSTREAM, UPSTREAM)
                .withTmDown(Integer.valueOf(TARGET_MARGIN_DOWN))
                .withTmUp(Integer.valueOf(TARGET_MARGIN_UP));
    }

    private HWDslam createDslam() {
        return new HWDslamBuilder().withRandomId().withRackTyp(HWRack.RACK_TYPE_DSLAM).build();
    }

    @Test
    public void testWhenRackTypeOltThenExpectNetworkDeviceTypeOlt() throws Exception {
        final CpsNetworkDevice cpsNetworkDevice = getNetworkDeviceFromFtthData(HWRack.RACK_TYPE_OLT);
        assertThat(cpsNetworkDevice.getType(), equalTo("OLT"));
    }

    @Test
    public void testWhenRackTypeDslamThenExpectNetworkDeviceTypeGslam() throws Exception {
        final CpsNetworkDevice cpsNetworkDevice = getNetworkDeviceFromFtthData(HWRack.RACK_TYPE_DSLAM);
        assertThat(cpsNetworkDevice.getType(), equalTo("GSLAM"));
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testWhenRackNotForFtthThenThrowException() throws Exception {
        getNetworkDeviceFromFtthData(HWRack.RACK_TYPE_MDU);
    }

    @Test
    public void testExpectEndpointDevice() throws Exception {
        final CpsEndpointDevice cpsEndpointDevice = getEndpointDeviceFromFtthData(HWRack.RACK_TYPE_OLT);
        assertThat(cpsEndpointDevice, notNullValue(CpsEndpointDevice.class));
        assertThat(cpsEndpointDevice.getType(), equalTo("ONT"));
        assertThat(cpsEndpointDevice.getHardwareModel(), equalTo(HARDWARE_MODEL));
        assertThat(cpsEndpointDevice.getManufacturer(), equalTo(ACCESS_DEVICE_MANUFACTURER));
        assertThat(cpsEndpointDevice.getTechId(), equalTo(TECH_ID));
        assertThat(cpsEndpointDevice.getName(), equalTo(ENDPOINT_DEVICE_NAME));
        assertThat(cpsEndpointDevice.getSerialNo(), equalTo(ENDPOINT_DEVICE_SERIAL_NO));
        assertThat(cpsEndpointDevice.getLocationType(), equalTo(ENDPOINT_DEVICE_STANDORT_TYP));
        assertThat(cpsEndpointDevice.getKvzNr(), nullValue(String.class)); //nur fuer FTTC relevant bzw. auf Port gesetzt
        assertThat(cpsEndpointDevice.getCardType(), nullValue(String.class)); //-> ATM/EFM fuer Dslam-Ports, fuer FTTH immer null
        assertThat(cpsEndpointDevice.getPort(), equalTo(ENDPOINT_PORT_HWEQN));
        assertThat(cpsEndpointDevice.getPortType(), equalTo(ENDPOINT_PORT_TYPE));
        assertThat(cpsEndpointDevice.getDownstream(), equalTo("50000"));
        assertThat(cpsEndpointDevice.getUpstream(), equalTo("5000"));
        assertThat(cpsEndpointDevice.getPortName(), equalTo(PORT_NAME));
        assertThat(cpsEndpointDevice.getTargetMarginDown(), equalTo(TARGET_MARGIN_DOWN));
        assertThat(cpsEndpointDevice.getTargetMarginUp(), equalTo(TARGET_MARGIN_UP));
        assertThat(cpsEndpointDevice.getFastpath(), nullValue(String.class));
        assertThat(cpsEndpointDevice.getInterleavingDelay(), nullValue(String.class)); // ist in hurrican nicht gepflegt (gehoert zu DslamProfile)
        assertThat(cpsEndpointDevice.getImpulseNoiseProtection(), nullValue(String.class)); // ist in hurrican nicht gepflegt (gehoert zu DslamProfile)
        assertThat(cpsEndpointDevice.getTransferModeOverride(), nullValue(String.class)); // bisher nur bei dslam relevant (wenn gesetzt dann 'ADSL')
        assertThat(cpsEndpointDevice.getWires(), nullValue(String.class)); //bei Kupferleitung noetig (bisher bei DSLAM immer konstant 2)
        assertThat(cpsEndpointDevice.getLineType(), nullValue(String.class)); //UETV (H13, ...), nur auf DTAG-Ports gesetzt -> gibts bei FTTH nicht
    }

    @Test
    public void testExpectedUpAndDownstreamWhenIad() throws Exception {
        final CpsEndpointDevice cpsEndpointDevice = getEndpointDeviceFromFtthData(HWRack.RACK_TYPE_OLT, true);
        assertThat(cpsEndpointDevice.getDownstream(), equalTo(Long.toString(DOWNSTREAM)));
        assertThat(cpsEndpointDevice.getUpstream(), equalTo(Long.toString(UPSTREAM)));
    }

    private CpsLayer2Config getLayerTwoConfigFromFtthData() throws Exception {
        return getItemFromFtthData(HWRack.RACK_TYPE_OLT).getLayer2Config();
    }

    @Test
    public void testLayer2ConfigExists() throws Exception {
        assertThat(getLayerTwoConfigFromFtthData(), notNullValue(CpsLayer2Config.class));
        assertThat(getLayerTwoConfigFromFtthData().getVlans(), notNullValue(List.class));
        assertThat(getLayerTwoConfigFromFtthData().getPbits(), notNullValue(List.class));
    }

    @Test
    public void testVoipPbitIsSet() throws Exception {
        final List<CPSPBITData> pbits = getLayerTwoConfigFromFtthData().getPbits();
        assertThat(pbits, hasSize(1));
        final CPSPBITData pbit = Iterables.getOnlyElement(pbits);
        assertThat(pbit.service, equalTo(voipPbit.service));
        assertThat(pbit.limit, equalTo(voipPbit.limit));
    }

    @Test
    public void testVlansAreSet() throws Exception {
        final List<CPSVlanData> vlans = getLayerTwoConfigFromFtthData().getVlans();
        assertThat(vlans, hasSize(1));
        final CPSVlanData vlan = vlans.get(0);
        assertThat(vlan.getCvlan(), equalTo(vlans.get(0).getCvlan()));
        assertThat(vlan.getSvlan(), equalTo(vlans.get(0).getSvlan()));
        assertThat(vlan.getService(), equalTo(vlans.get(0).getService()));
        assertThat(vlan.getSvlanBackbone(), equalTo(vlans.get(0).getSvlanBackbone()));
        assertThat(vlan.getType(), equalTo(vlans.get(0).getType()));
    }

    @Test
    public void testGetFtthDataWithWholesaleProduct() throws FindException {
        final Date when = new Date();

        final CpsAccessDevice result = cut.getFtthData(AUFTRAG_ID, when, false, PORT_NAME);
        assertThat(result.getItems(), hasSize(1));
    }

    private AuftragDatenBuilder createAuftragDaten() {
        return new AuftragDatenBuilder().withAuftragId(AUFTRAG_ID).withProdId(600L)
                .setPersist(false);
    }

    private EkpFrameContractBuilder createEkpFrameContract() {
        return new EkpFrameContractBuilder().setPersist(false)
                .withEkpId(EKP_ID).withFrameContractId(FRAME_CONTRACT_ID);
    }

    private Auftrag2EkpFrameContractBuilder createAuftrag2EkpFrameContract() {
        return new Auftrag2EkpFrameContractBuilder()
                .withEkpFrameContractBuilder(ekpFrameContract).setPersist(false);
    }

    private A10NspBuilder createA10Nsp() {
        return new A10NspBuilder().withName(A10NSP).setPersist(false);
    }

    private VerbindungsBezeichnungBuilder createVbzBuilder() {
        return new VerbindungsBezeichnungBuilder().withVbz(VBZ).setPersist(false);
    }

    private A10NspPortBuilder createA10NspPort() {
        return new A10NspPortBuilder().setPersist(false).withA10NspBuilder(a10Nsp)
                .withVbzBuilder(vbz);
    }
}
