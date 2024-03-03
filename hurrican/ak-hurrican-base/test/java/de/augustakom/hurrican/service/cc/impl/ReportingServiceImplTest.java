package de.augustakom.hurrican.service.cc.impl;

import static org.testng.Assert.*;

import java.util.*;
import com.google.common.collect.Sets;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.dao.cc.VrrpPriority;
import de.augustakom.hurrican.model.cc.AddressTypeEnum;
import de.augustakom.hurrican.model.cc.Ansprechpartner;
import de.augustakom.hurrican.model.cc.AnsprechpartnerBuilder;
import de.augustakom.hurrican.model.cc.Auftrag2TechLeistungBuilder;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragHousing;
import de.augustakom.hurrican.model.cc.AuftragHousingBuilder;
import de.augustakom.hurrican.model.cc.AuftragHousingKey;
import de.augustakom.hurrican.model.cc.AuftragHousingKeyBuilder;
import de.augustakom.hurrican.model.cc.AuftragMVSEnterpriseBuilder;
import de.augustakom.hurrican.model.cc.AuftragMVSSiteBuilder;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.CCAddress;
import de.augustakom.hurrican.model.cc.CCAddressBuilder;
import de.augustakom.hurrican.model.cc.CarrierbestellungBuilder;
import de.augustakom.hurrican.model.cc.EG2AuftragBuilder;
import de.augustakom.hurrican.model.cc.EGBuilder;
import de.augustakom.hurrican.model.cc.EGConfig;
import de.augustakom.hurrican.model.cc.EGConfigBuilder;
import de.augustakom.hurrican.model.cc.EGTypeBuilder;
import de.augustakom.hurrican.model.cc.EndgeraetIp;
import de.augustakom.hurrican.model.cc.EndgeraetIp.AddressType;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.EndstelleConnect;
import de.augustakom.hurrican.model.cc.EndstelleConnect.ESEinstellung;
import de.augustakom.hurrican.model.cc.EndstelleConnect.ESRouterInfo;
import de.augustakom.hurrican.model.cc.EndstelleConnect.ESSchnittstelle;
import de.augustakom.hurrican.model.cc.EndstelleConnectBuilder;
import de.augustakom.hurrican.model.cc.IPAddress;
import de.augustakom.hurrican.model.cc.IPAddressBuilder;
import de.augustakom.hurrican.model.cc.IPSecClient2SiteToken;
import de.augustakom.hurrican.model.cc.IPSecClient2SiteTokenBuilder;
import de.augustakom.hurrican.model.cc.IPSecSite2SiteBuilder;
import de.augustakom.hurrican.model.cc.IntAccountBuilder;
import de.augustakom.hurrican.model.cc.PortForwarding;
import de.augustakom.hurrican.model.cc.PortForwarding.TransportProtocolType;
import de.augustakom.hurrican.model.cc.PortforwardingBuilder;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.ProduktBuilder;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.ReferenceBuilder;
import de.augustakom.hurrican.model.cc.Routing;
import de.augustakom.hurrican.model.cc.ServiceVertrag;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnungBuilder;
import de.augustakom.hurrican.model.cc.housing.HousingBuilding;
import de.augustakom.hurrican.model.cc.housing.HousingBuildingBuilder;
import de.augustakom.hurrican.model.cc.housing.HousingFloor;
import de.augustakom.hurrican.model.cc.housing.HousingFloorBuilder;
import de.augustakom.hurrican.model.cc.housing.HousingParcel;
import de.augustakom.hurrican.model.cc.housing.HousingParcelBuilder;
import de.augustakom.hurrican.model.cc.housing.HousingRoom;
import de.augustakom.hurrican.model.cc.housing.HousingRoomBuilder;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.cc.ReportingService;

@Test(groups = BaseTest.SERVICE)
public class ReportingServiceImplTest extends AbstractHurricanBaseServiceTest {

    private static final String IP_PURPOSE_TYPE = "ip-purpose-type";
    private static final Long BILLING_AUFTRAG_NO_FOR_TESTING = 66666666L;

    private ReportingService reportingService;

    @BeforeMethod
    public void setupSut() {
        reportingService = getCCService(ReportingService.class);
    }

    @DataProvider(name = "getIpAddressDataProvider")
    public Object[][] getIpAddressDataProvider() {
        // @formatter:off
        return new Object[][] {
            new Object[]{AddressTypeEnum.IPV4,        "83.1.1.0/24",                               false, "83.1.1.0"},
            new Object[]{AddressTypeEnum.IPV4,        "83.1.1.0/24",                               true,  "83.1.1.0 (ip-purpose-type)"},
            new Object[]{AddressTypeEnum.IPV4_prefix, "83.1.1.0/24",                               true,  "83.1.1.0/24 (ip-purpose-type)"},
            new Object[]{AddressTypeEnum.IPV4_prefix, "83.1.1.0/24",                               false, "83.1.1.0/24"},
            new Object[]{AddressTypeEnum.IPV6_full,   "2001:0db8:85a3:08d3:1319:8a2e:0370:7344/1", false, "2001:0db8:85a3:08d3:1319:8a2e:0370:7344"},
            new Object[]{AddressTypeEnum.IPV6_full,   "2001:0db8:85a3:08d3:1319:8a2e:0370:7344/1", true,  "2001:0db8:85a3:08d3:1319:8a2e:0370:7344 (ip-purpose-type)"},
            new Object[]{AddressTypeEnum.IPV6_prefix, "2001:0db8:85a3::7344/1",                    true,  "2001:0db8:85a3::7344/1 (ip-purpose-type)"},
            new Object[]{AddressTypeEnum.IPV6_prefix, "2001:0db8:85a3::7344/1",                    false, "2001:0db8:85a3::7344/1"},
        };
        // @formatter:on
    }

    @Test(dataProvider = "getIpAddressDataProvider")
    public void testGetIpAddress(AddressTypeEnum ipType, String ip, boolean withPurpose, String expectedResult) {
        Reference purposeRef = getBuilder(ReferenceBuilder.class).withStrValue(IP_PURPOSE_TYPE).withRandomId().build();

        getBuilder(IPAddressBuilder.class)
                .withAddressType(ipType)
                .withAddress(ip)
                .withBillingOrderNumber(BILLING_AUFTRAG_NO_FOR_TESTING)
                .withNetId(1L)
                .withPurpose((withPurpose) ? purposeRef : null)
                .build();

        getBuilder(AuftragDatenBuilder.class).withAuftragNoOrig(BILLING_AUFTRAG_NO_FOR_TESTING).build();

        String value = reportingService.getValue(BILLING_AUFTRAG_NO_FOR_TESTING, "ip_address");
        assertEquals(value, expectedResult);
    }


    /**
     * Testmethode fuer {@link ReportingServiceImpl#getValue(Long, String)}.
     */
    public void testGetRouting_WithIPv4() {
        IPAddress ipAddress = getBuilder(IPAddressBuilder.class)
                .withAddressType(AddressTypeEnum.IPV4)
                .withAddress("83.1.1.0/24")
                .build();
        Set<Routing> routings = new HashSet<>();
        Routing routing = new Routing();
        routing.setDestinationAdressRef(ipAddress);
        routing.setNextHop("127.0.0.1");
        routings.add(routing);
        IPAddress ipAddress2 = getBuilder(IPAddressBuilder.class)
                .withAddressType(AddressTypeEnum.IPV4)
                .withAddress("183.111.111.0")
                .build();
        Routing routing2 = new Routing();
        routing2.setDestinationAdressRef(ipAddress2);
        routing2.setNextHop("127.127.127.127");
        routings.add(routing2);
        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class).withAuftragNoOrig(BILLING_AUFTRAG_NO_FOR_TESTING);
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class).withAuftragDatenBuilder(auftragDatenBuilder);
        getBuilder(EG2AuftragBuilder.class).withAuftragBuilder(auftragBuilder).withRoutings(routings).build();

        String value = reportingService.getValue(BILLING_AUFTRAG_NO_FOR_TESTING, "CPE.ROUTING");

        String sb = String.format("IP-Netz:     183.111.111.0                                     %n") +
                String.format("Gateway:     127.127.127.127                                   %n") +
                String.format("===============================================================%n") +
                String.format("IP-Netz:     83.1.1.0/24                                       %n") +
                String.format("Gateway:     127.0.0.1                                         %n");
        assertEquals(value, sb);
    }

    /**
     * Testmethode fuer {@link ReportingServiceImpl#getValue(Long, String)}.
     */
    public void testGetRouting_WithIPv6AndIpv4() {
        IPAddress firstIpAddress = getBuilder(IPAddressBuilder.class)
                .withAddressType(AddressTypeEnum.IPV6_prefix)
                .withAddress("2000:DA01::/32")
                .build();
        Set<Routing> routings = new HashSet<>();
        Routing firstRouting = new Routing();
        firstRouting.setDestinationAdressRef(firstIpAddress);
        firstRouting.setNextHop("1001:2001:6001:FAA1:5001:9001:0002:9999");
        routings.add(firstRouting);

        IPAddress secondIpAddress = getBuilder(IPAddressBuilder.class)
                .withAddressType(AddressTypeEnum.IPV6_prefix)
                .withAddress("1234:4567:1234:ABCD:0023:163F:BB2A:0001/128")
                .build();
        Routing secondRouting = new Routing();
        secondRouting.setDestinationAdressRef(secondIpAddress);
        secondRouting.setNextHop("2000:0321::243B");
        routings.add(secondRouting);

        IPAddress thirdIpAddress = getBuilder(IPAddressBuilder.class)
                .withAddressType(AddressTypeEnum.IPV4)
                .withAddress("183.111.111.252/34")
                .build();
        Routing thirdRouting = new Routing();
        thirdRouting.setDestinationAdressRef(thirdIpAddress);
        thirdRouting.setNextHop("127.127.127.127");
        routings.add(thirdRouting);

        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class).withAuftragNoOrig(BILLING_AUFTRAG_NO_FOR_TESTING);
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class).withAuftragDatenBuilder(auftragDatenBuilder);
        getBuilder(EG2AuftragBuilder.class).withAuftragBuilder(auftragBuilder).withRoutings(routings).build();

        String value = reportingService.getValue(BILLING_AUFTRAG_NO_FOR_TESTING, "CPE.ROUTING");

        String sb = String.format("IP-Netz:     1234:4567:1234:ABCD:0023:163F:BB2A:0001/128       %n") +
                String.format("Gateway:     2000:0321::243B                                   %n") +
                String.format("===============================================================%n") +
                String.format("IP-Netz:     183.111.111.252/34                                %n") +
                String.format("Gateway:     127.127.127.127                                   %n") +
                String.format("===============================================================%n") +
                String.format("IP-Netz:     2000:DA01:0:0:0:0:0:0/32                          %n") +
                String.format("Gateway:     1001:2001:6001:FAA1:5001:9001:0002:9999           %n");
        assertEquals(value, sb);
    }

    /**
     * Testmethode fuer {@link ReportingServiceImpl#getValue(Long, String)}.
     */
    public void testPortForwardings_WithTwoIPv6() {
        PortForwarding firstIPv6PortForwarding = getBuilder(PortforwardingBuilder.class)
                .withSourceAddress(
                        getBuilder(IPAddressBuilder.class)
                                .withAddress("2000:DA01:291F:1234:0021:9001:AAD2:3FF0")
                                .withAddressType(AddressTypeEnum.IPV6_full)
                )
                .withSourcePort(22)
                .withDestinationAddress(
                        getBuilder(IPAddressBuilder.class)
                                .withAddress("2000:DA01:291F:1234:0021:9001:AAD2:3FF0")
                                .withAddressType(AddressTypeEnum.IPV6_full)
                )
                .withDestinationPort(80)
                .withTransportProtocolType(TransportProtocolType.TCP)
                .active(Boolean.TRUE)
                .build();
        PortForwarding secondIPv6PortForwarding = getBuilder(PortforwardingBuilder.class)
                .withSourceAddress(
                        getBuilder(IPAddressBuilder.class)
                                .withAddress("1000:FA01:291F:1A34:0021:0001:BB02:3FF1")
                                .withAddressType(AddressTypeEnum.IPV6_full)
                )
                .withSourcePort(443)
                .withDestinationAddress(
                        getBuilder(IPAddressBuilder.class)
                                .withAddress("2000::")
                                .withAddressType(AddressTypeEnum.IPV6_full)
                )
                .withDestinationPort(443)
                .withTransportProtocolType(TransportProtocolType.UDP)
                .active(Boolean.TRUE)
                .build();

        Set<PortForwarding> portForwardings = new HashSet<>();
        portForwardings.add(firstIPv6PortForwarding);
        portForwardings.add(secondIPv6PortForwarding);
        createEGConfigWithPortForwardings(BILLING_AUFTRAG_NO_FOR_TESTING, portForwardings);
        String value = reportingService.getValue(BILLING_AUFTRAG_NO_FOR_TESTING, "CPE.PORTFORWARDING");
        String sb = String.format("WAN-IP:      1000:FA01:291F:1A34:0021:0001:BB02:3FF1           %n") +
                String.format("WAN-PORT:    443                                               %n") +
                String.format("LAN-IP:      2000::                                            %n") +
                String.format("LAN-PORT:    443                                               %n") +
                String.format("Protokoll:   UDP                                               %n") +
                String.format("===============================================================%n") +
                String.format("WAN-IP:      2000:DA01:291F:1234:0021:9001:AAD2:3FF0           %n") +
                String.format("WAN-PORT:    22                                                %n") +
                String.format("LAN-IP:      2000:DA01:291F:1234:0021:9001:AAD2:3FF0           %n") +
                String.format("LAN-PORT:    80                                                %n") +
                String.format("Protokoll:   TCP                                               %n");
        assertEquals(value, sb);
    }

    public void testPortForwardings_WithIPv4() {
        Set<PortForwarding> portForwardings = new HashSet<>();

        IPAddress ipAddressSource1 = getBuilder(IPAddressBuilder.class)
                .withAddressType(AddressTypeEnum.IPV4)
                .withAddress("83.1.1.0")
                .build();
        IPAddress ipAddressDest1 = getBuilder(IPAddressBuilder.class)
                .withAddressType(AddressTypeEnum.IPV4)
                .withAddress("127.0.0.1")
                .build();
        PortForwarding pf1 = new PortForwarding();
        pf1.setSourceIpAddressRef(ipAddressSource1);
        pf1.setSourcePort(22);
        pf1.setDestIpAddressRef(ipAddressDest1);
        pf1.setDestPort(22);
        pf1.setTransportProtocol(PortForwarding.TransportProtocolType.TCP);
        pf1.setActive(Boolean.TRUE);

        IPAddress ipAddressSource2 = getBuilder(IPAddressBuilder.class)
                .withAddressType(AddressTypeEnum.IPV4)
                .withAddress("183.111.111.0")
                .build();
        IPAddress ipAddressDest2 = getBuilder(IPAddressBuilder.class)
                .withAddressType(AddressTypeEnum.IPV4)
                .withAddress("127.127.127.127")
                .build();
        PortForwarding pf2 = new PortForwarding();
        pf2.setSourceIpAddressRef(ipAddressSource2);
        pf2.setSourcePort(83);
        pf2.setDestIpAddressRef(ipAddressDest2);
        pf2.setDestPort(183);
        pf2.setTransportProtocol(PortForwarding.TransportProtocolType.UDP);
        pf2.setActive(Boolean.TRUE);

        IPAddress ipAddressSource3 = getBuilder(IPAddressBuilder.class)
                .withAddressType(AddressTypeEnum.IPV4)
                .withAddress("183.111.111.0")
                .build();
        IPAddress ipAddressDest3 = getBuilder(IPAddressBuilder.class)
                .withAddressType(AddressTypeEnum.IPV4)
                .withAddress("127.127.127.127")
                .build();
        PortForwarding pf3 = new PortForwarding();
        pf3.setSourceIpAddressRef(ipAddressSource3);
        pf3.setSourcePort(32222);
        pf3.setDestIpAddressRef(ipAddressDest3);
        pf3.setDestPort(61111);
        pf3.setTransportProtocol(PortForwarding.TransportProtocolType.UDP);
        pf3.setActive(Boolean.TRUE);

        portForwardings.add(pf1);
        portForwardings.add(pf2);
        portForwardings.add(pf3);

        createEGConfigWithPortForwardings(BILLING_AUFTRAG_NO_FOR_TESTING, portForwardings);

        String value = reportingService.getValue(BILLING_AUFTRAG_NO_FOR_TESTING, "CPE.PORTFORWARDING");

        String sb = String.format("WAN-IP:      83.1.1.0                                          %n") +
                String.format("WAN-PORT:    22                                                %n") +
                String.format("LAN-IP:      127.0.0.1                                         %n") +
                String.format("LAN-PORT:    22                                                %n") +
                String.format("Protokoll:   TCP                                               %n") +
                String.format("===============================================================%n") +
                String.format("WAN-IP:      183.111.111.0                                     %n") +
                String.format("WAN-PORT:    83                                                %n") +
                String.format("LAN-IP:      127.127.127.127                                   %n") +
                String.format("LAN-PORT:    183                                               %n") +
                String.format("Protokoll:   UDP                                               %n") +
                String.format("===============================================================%n") +
                String.format("WAN-IP:      183.111.111.0                                     %n") +
                String.format("WAN-PORT:    32222                                             %n") +
                String.format("LAN-IP:      127.127.127.127                                   %n") +
                String.format("LAN-PORT:    61111                                             %n") +
                String.format("Protokoll:   UDP                                               %n");
        assertEquals(value, sb);
    }

    public void testPortForwardingsWithNullValues() {
        IPAddress ipAddress = getBuilder(IPAddressBuilder.class)
                .withAddressType(AddressTypeEnum.IPV4)
                .withAddress("127.0.0.1")
                .build();
        Set<PortForwarding> portForwardings = new HashSet<>();
        PortForwarding pf1 = new PortForwarding();
        pf1.setDestIpAddressRef(ipAddress);
        pf1.setDestPort(22);
        pf1.setActive(Boolean.TRUE);
        portForwardings.add(pf1);
        createEGConfigWithPortForwardings(BILLING_AUFTRAG_NO_FOR_TESTING, portForwardings);

        String value = reportingService.getValue(BILLING_AUFTRAG_NO_FOR_TESTING, "CPE.PORTFORWARDING");

        String sb = String.format("WAN-IP:                                                        %n") +
                String.format("WAN-PORT:                                                      %n") +
                String.format("LAN-IP:      127.0.0.1                                         %n") +
                String.format("LAN-PORT:    22                                                %n") +
                String.format("Protokoll:                                                     %n");
        assertEquals(value, sb);
    }

    public void testDisabledPortForwarding() {
        IPAddress ipAddressDest = getBuilder(IPAddressBuilder.class)
                .withAddressType(AddressTypeEnum.IPV4)
                .withAddress("127.0.0.1")
                .build();
        Set<PortForwarding> portForwardings = new HashSet<>();
        PortForwarding pf1 = new PortForwarding();
        pf1.setDestIpAddressRef(ipAddressDest);
        pf1.setDestPort(22);
        pf1.setActive(Boolean.FALSE);
        portForwardings.add(pf1);
        createEGConfigWithPortForwardings(BILLING_AUFTRAG_NO_FOR_TESTING, portForwardings);

        String value = reportingService.getValue(BILLING_AUFTRAG_NO_FOR_TESTING, "CPE.PORTFORWARDING");
        assertEquals(value, "");
    }

    public void testGetCpeNat() {
        createEGConfigWithPortForwardings(BILLING_AUFTRAG_NO_FOR_TESTING, new HashSet<>());
        String value = reportingService.getValue(BILLING_AUFTRAG_NO_FOR_TESTING, "CPE.NAT");
        assertEquals(value, "Inaktiv");
    }

    public void testGetCpeHersteller() {
        createEGConfigWithPortForwardings(BILLING_AUFTRAG_NO_FOR_TESTING, new HashSet<>());
        String value = reportingService.getValue(BILLING_AUFTRAG_NO_FOR_TESTING, "CPE.HERSTELLER");
        assertEquals(value, "Thomson");
    }

    public void testGetCpeModell() {
        createEGConfigWithPortForwardings(BILLING_AUFTRAG_NO_FOR_TESTING, new HashSet<>());
        String value = reportingService.getValue(BILLING_AUFTRAG_NO_FOR_TESTING, "CPE.MODELL");
        assertEquals(value, "MyModell");
    }

    public void testGetCpeModellZusatz() {
        createEGConfigWithPortForwardings(BILLING_AUFTRAG_NO_FOR_TESTING, new HashSet<>());
        String value = reportingService.getValue(BILLING_AUFTRAG_NO_FOR_TESTING, "CPE.MODELLZUSATZ");
        assertEquals(value, "H3612");
    }

    public void testGetCpeName() {
        createEGConfigWithPortForwardings(BILLING_AUFTRAG_NO_FOR_TESTING, new HashSet<>());
        String value = reportingService.getValue(BILLING_AUFTRAG_NO_FOR_TESTING, "CPE.NAME");
        assertEquals(value, "AVM FritzBox 3170");
    }

    public void testGetDhcp() {
        createEGConfigWithPortForwardings(BILLING_AUFTRAG_NO_FOR_TESTING, new HashSet<>());
        String value = reportingService.getValue(BILLING_AUFTRAG_NO_FOR_TESTING, "CPE.DHCP");
        assertEquals(value, "Aktiv");
    }

    public void testGetServiceVertrag() {
        createEGConfigWithPortForwardings(BILLING_AUFTRAG_NO_FOR_TESTING, new HashSet<>());
        String value = reportingService.getValue(BILLING_AUFTRAG_NO_FOR_TESTING, "CPE.SERVICEVERTRAG");
        assertEquals(value, ServiceVertrag.gekauft.name());
    }

    public void testGetUnbekanntCpeData() {
        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class)
                .withAuftragNoOrig(BILLING_AUFTRAG_NO_FOR_TESTING);
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class)
                .withAuftragDatenBuilder(auftragDatenBuilder);
        EG2AuftragBuilder eg2AuftragBuilder = getBuilder(EG2AuftragBuilder.class)
                .withAuftragBuilder(auftragBuilder);
        getBuilder(EGConfigBuilder.class)
                .withNatActive(null)
                .withDhcpActive(null)
                .withEG2AuftragBuilder(eg2AuftragBuilder)
                .build();

        String value = reportingService.getValue(BILLING_AUFTRAG_NO_FOR_TESTING, "CPE.DHCP");
        assertEquals(value, "Unbekannt");

        value = reportingService.getValue(BILLING_AUFTRAG_NO_FOR_TESTING, "CPE.NAT");
        assertEquals(value, "Unbekannt");
    }


    public void testAuftragHousing() throws Exception {
        Long billingID = BILLING_AUFTRAG_NO_FOR_TESTING;
        final Long PRODUCTTYPE_AUFTRAG_HOUSING = 24L;
        String testValue;

        HousingFloorBuilder floorBuilder = getBuilder(HousingFloorBuilder.class);
        HousingRoomBuilder roomBuilder = getBuilder(HousingRoomBuilder.class);
        HousingParcelBuilder parcelBuilder = getBuilder(HousingParcelBuilder.class);

        HousingParcel parcel = parcelBuilder.build();
        HousingRoom room = roomBuilder.withParcel(parcel).build();
        HousingFloor floor = floorBuilder.withRoom(room).build();

        CCAddress address = getBuilder(CCAddressBuilder.class).build();
        HousingBuildingBuilder buildingBuilder = getBuilder(HousingBuildingBuilder.class)
                .withCCAddress(address)
                .withFloor(floor).setPersist(true);

        HousingBuilding building = buildingBuilder.build();

        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class)
                .withAuftragNoOrig(BILLING_AUFTRAG_NO_FOR_TESTING)
                .withProdBuilder(getBuilder(ProduktBuilder.class)
                        .withProduktGruppeId(PRODUCTTYPE_AUFTRAG_HOUSING));

        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class).withAuftragDatenBuilder(auftragDatenBuilder);

        AuftragHousingBuilder auftragHousingbuilder = getBuilder(AuftragHousingBuilder.class)
                .withAuftragBuilder(auftragBuilder)
                .withAuftragDatenBuilder(auftragDatenBuilder)
                .withBuilding(building)
                .withFloorId(floor.getId())
                .withRoomId(room.getId())
                .withParcelId(parcel.getId())
                .setPersist(true);

        AuftragHousing auftragHousing = auftragHousingbuilder.build();

        AuftragHousingKeyBuilder auftragHousingKeyBuilder = getBuilder(AuftragHousingKeyBuilder.class).
                withAuftragBuilder(auftragBuilder).
                withAuftragHousingBuilder(auftragHousingbuilder);

        AuftragHousingKey auftragHousingKey = auftragHousingKeyBuilder.build();

        testHousingBuilding(billingID, building);

        testHousingInfos(billingID, auftragHousing);

        testHousingStromkreis(billingID, auftragHousing);

        testTransponders(billingID, auftragHousingKey);

        testValue = reportingService.getValue(billingID, "housing.stockwerk");
        assertEquals(testValue, floor.getFloor());
        testValue = reportingService.getValue(billingID, "housing.raum");
        assertEquals(testValue, room.getRoom());
        testValue = reportingService.getValue(billingID, "housing.parzelle");
        assertEquals(testValue, parcel.getParcel());

    }

    private void testTransponders(Long billingID, AuftragHousingKey housingKey) {
        String testValue;
        testValue = reportingService.getValue(billingID, "housing.transponder.nummern");
        assertEquals(testValue, housingKey.getTransponder().getTransponderId().toString());
        testValue = reportingService.getValue(billingID, "housing.transponder.namen");
        assertEquals(testValue, housingKey.getTransponder().getCustomerFirstName() + " " + housingKey.getTransponder().getCustomerLastName());
    }

    private void testHousingInfos(Long billingID, AuftragHousing housing) {
        String testValue;
        testValue = reportingService.getValue(billingID, "housing.hoeheneinheit");
        assertEquals(testValue, housing.getRackUnits().toString());
        testValue = reportingService.getValue(billingID, "housing.schrank");
        assertEquals(testValue, housing.getRack());
    }

    private void testHousingBuilding(Long billingID, HousingBuilding building) {
        String testValue;
        testValue = reportingService.getValue(billingID, "housing.gebaeude.name");
        assertEquals(testValue, building.getBuilding());
        testValue = reportingService.getValue(billingID, "housing.gebaeude.strasse");
        assertEquals(testValue, building.getAddress().getStrasse());
        testValue = reportingService.getValue(billingID, "housing.gebaeude.hausnr");
        assertEquals(testValue, building.getAddress().getNummer());
        testValue = reportingService.getValue(billingID, "housing.gebaeude.plz");
        assertEquals(testValue, building.getAddress().getPlz());
        testValue = reportingService.getValue(billingID, "housing.gebaeude.ort");
        assertEquals(testValue, building.getAddress().getOrt());
    }

    private void testHousingStromkreis(Long billingID, AuftragHousing housing) {
        String testValue;
        testValue = reportingService.getValue(billingID, "housing.stromkreis.anzahl");
        assertEquals(testValue, housing.getElectricCircuitCount().toString());
        testValue = reportingService.getValue(billingID, "housing.stromkreis.leistung");
        assertEquals(testValue, housing.getElectricCircuitCapacity().toString());
        testValue = reportingService.getValue(billingID, "housing.stromkreis.sicherung");
        assertEquals(testValue, housing.getElectricSafeguard().toString());
        testValue = reportingService.getValue(billingID, "housing.stromkreis.zaehler1.nr");
        assertEquals(testValue, housing.getElectricCounterNumber());
        testValue = reportingService.getValue(billingID, "housing.stromkreis.zaehler1.bereitstellung");
        assertEquals(testValue, housing.getElectricCounterStart().toString());
        testValue = reportingService.getValue(billingID, "housing.stromkreis.zaehler1.kuendigung");
        assertEquals(testValue, housing.getElectricCounterEnd().toString());
        testValue = reportingService.getValue(billingID, "housing.stromkreis.zaehler2.nr");
        assertEquals(testValue, housing.getElectricCounterNumber2());
        testValue = reportingService.getValue(billingID, "housing.stromkreis.zaehler2.bereitstellung");
        assertEquals(testValue, housing.getElectricCounterStart2().toString());
        testValue = reportingService.getValue(billingID, "housing.stromkreis.zaehler2.kuendigung");
        assertEquals(testValue, housing.getElectricCounterEnd2().toString());
        testValue = reportingService.getValue(billingID, "housing.stromkreis.zaehler3.nr");
        assertEquals(testValue, housing.getElectricCounterNumber3());
        testValue = reportingService.getValue(billingID, "housing.stromkreis.zaehler3.bereitstellung");
        assertEquals(testValue, housing.getElectricCounterStart3().toString());
        testValue = reportingService.getValue(billingID, "housing.stromkreis.zaehler3.kuendigung");
        assertEquals(testValue, housing.getElectricCounterEnd3().toString());
        testValue = reportingService.getValue(billingID, "housing.stromkreis.zaehler4.nr");
        assertEquals(testValue, housing.getElectricCounterNumber4());
        testValue = reportingService.getValue(billingID, "housing.stromkreis.zaehler4.bereitstellung");
        assertEquals(testValue, housing.getElectricCounterStart4().toString());
        testValue = reportingService.getValue(billingID, "housing.stromkreis.zaehler4.kuendigung");
        assertEquals(testValue, housing.getElectricCounterEnd4().toString());
    }

    public void testGetLanIps() {
        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class).withAuftragNoOrig(BILLING_AUFTRAG_NO_FOR_TESTING);
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class).withAuftragDatenBuilder(auftragDatenBuilder);
        getBuilder(EG2AuftragBuilder.class).withAuftragBuilder(auftragBuilder)
                .withEndgeraetIps(createTestEndgeraetIps())
                .build();

        String value = reportingService.getValue(BILLING_AUFTRAG_NO_FOR_TESTING, "CPE.IP.LAN");
        assertEquals(
                value,
                "192.168.104.236/24 | 2001:a60:a004:1::/48 | 2001:a60:a004::/64 eui-64 | ip-purpose-type 0:0:1:1::/64 "
                        + "eui-64 | ip-purpose-type 0:0:1:2::/48");
    }

    public void testGetVrrpIps() {
        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class).withAuftragNoOrig(BILLING_AUFTRAG_NO_FOR_TESTING);
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class).withAuftragDatenBuilder(auftragDatenBuilder);
        EGConfig egConfig = getBuilder(EGConfigBuilder.class).withVrrpPriority(VrrpPriority.SECONDARY).build();
        getBuilder(EG2AuftragBuilder.class).withAuftragBuilder(auftragBuilder)
                .withEndgeraetIps(createTestEndgeraetIps())
                .withEgConfigs(Sets.newHashSet(egConfig))
                .build();

        String value = reportingService.getValue(BILLING_AUFTRAG_NO_FOR_TESTING, "CPE.IP.VRRP");
        assertEquals(value, "2001:a60:a004:2::/48 | 61.245.247.17/28 | secondary");
    }

    public void testHas4DrahtOptionFalse() {
        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class).withAuftragNoOrig(BILLING_AUFTRAG_NO_FOR_TESTING);
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class).withAuftragDatenBuilder(auftragDatenBuilder);
        getBuilder(EG2AuftragBuilder.class).withAuftragBuilder(auftragBuilder).build();

        String value = reportingService.getValue(BILLING_AUFTRAG_NO_FOR_TESTING, "4drahtoption");
        assertEquals(value, "Nein");
    }

    public void testHas4DrathtOptionTrue() {
        AuftragDatenBuilder auftragDatenBuilder1 = getBuilder(AuftragDatenBuilder.class).withProdId(Produkt.PROD_ID_SDSL_1024).withAuftragNoOrig(BILLING_AUFTRAG_NO_FOR_TESTING);
        AuftragBuilder auftragBuilder1 = getBuilder(AuftragBuilder.class).withAuftragDatenBuilder(auftragDatenBuilder1);
        getBuilder(EG2AuftragBuilder.class).withAuftragBuilder(auftragBuilder1).build();

        ProduktBuilder prodBuilder = getBuilder(ProduktBuilder.class).withIsVierDraht(Boolean.TRUE);
        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class).withProdBuilder(prodBuilder).withAuftragNoOrig(BILLING_AUFTRAG_NO_FOR_TESTING);
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class).withAuftragDatenBuilder(auftragDatenBuilder);
        getBuilder(EG2AuftragBuilder.class).withAuftragBuilder(auftragBuilder).build();

        String value = reportingService.getValue(BILLING_AUFTRAG_NO_FOR_TESTING, "4drahtoption");
        assertEquals(value, "Ja");
    }

    public void testGetWanIps() {
        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class).withAuftragNoOrig(BILLING_AUFTRAG_NO_FOR_TESTING);
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class).withAuftragDatenBuilder(auftragDatenBuilder);
        getBuilder(EG2AuftragBuilder.class).withAuftragBuilder(auftragBuilder).withEndgeraetIps(createTestEndgeraetIps()).build();

        String value = reportingService.getValue(BILLING_AUFTRAG_NO_FOR_TESTING, "CPE.IP.WAN");
        assertEquals(value, "192.168.0.31/24 | ip-purpose-type 0:0:3:4::/48");
    }

    public void testGetLanIpsWithoutLanIps() {
        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class).withAuftragNoOrig(BILLING_AUFTRAG_NO_FOR_TESTING);
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class).withAuftragDatenBuilder(auftragDatenBuilder);
        getBuilder(EG2AuftragBuilder.class).withAuftragBuilder(auftragBuilder).build();

        String value = reportingService.getValue(BILLING_AUFTRAG_NO_FOR_TESTING, "CPE.IP.LAN");
        assertEquals(value, "");
    }

    public void testGetEtage() {
        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class).withAuftragNoOrig(BILLING_AUFTRAG_NO_FOR_TESTING);
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class).withAuftragDatenBuilder(auftragDatenBuilder);
        getBuilder(EG2AuftragBuilder.class).withEtage("etage blubber").withAuftragBuilder(auftragBuilder).build();

        String value = reportingService.getValue(BILLING_AUFTRAG_NO_FOR_TESTING, "CPE.ETAGE");

        assertEquals(value, "etage blubber");
    }

    public void testGetRaum() {
        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class).withAuftragNoOrig(BILLING_AUFTRAG_NO_FOR_TESTING);
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class).withAuftragDatenBuilder(auftragDatenBuilder);
        getBuilder(EG2AuftragBuilder.class).withRaum("Großer Raum Sonderzeichen: %&/*-").withAuftragBuilder(auftragBuilder).build();

        String value = reportingService.getValue(BILLING_AUFTRAG_NO_FOR_TESTING, "CPE.RAUM");

        assertEquals(value, "Großer Raum Sonderzeichen: %&/*-");
    }


    public void testGetIntAccounts() throws Exception {
        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class).withAuftragNoOrig(BILLING_AUFTRAG_NO_FOR_TESTING);
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class).withAuftragDatenBuilder(auftragDatenBuilder);
        IntAccountBuilder intAccountBuilder = getBuilder(IntAccountBuilder.class).withAccount("abbc123");
        AuftragTechnikBuilder auftragTechnikBuilder = getBuilder(AuftragTechnikBuilder.class).withAuftragBuilder(auftragBuilder).withIntAccountBuilder(intAccountBuilder);
        auftragTechnikBuilder.build();

        AuftragDatenBuilder auftragDatenBuilder2 = getBuilder(AuftragDatenBuilder.class).withAuftragNoOrig(BILLING_AUFTRAG_NO_FOR_TESTING);
        AuftragBuilder auftragBuilder2 = getBuilder(AuftragBuilder.class).withAuftragDatenBuilder(auftragDatenBuilder2);
        IntAccountBuilder intAccountBuilder2 = getBuilder(IntAccountBuilder.class).withAccount("hb123");
        AuftragTechnikBuilder auftragTechnikBuilder2 = getBuilder(AuftragTechnikBuilder.class).withAuftragBuilder(auftragBuilder2).withIntAccountBuilder(intAccountBuilder2);
        auftragTechnikBuilder2.build();

        String value = reportingService.getValue(BILLING_AUFTRAG_NO_FOR_TESTING, "accounts");

        assertTrue(value.contains("abbc123"));
        assertTrue(value.contains("hb123"));
    }


    public void testGetRaumNull() {
        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class).withAuftragNoOrig(BILLING_AUFTRAG_NO_FOR_TESTING);
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class).withAuftragDatenBuilder(auftragDatenBuilder);
        getBuilder(EG2AuftragBuilder.class).withAuftragBuilder(auftragBuilder).build();

        String value = reportingService.getValue(BILLING_AUFTRAG_NO_FOR_TESTING, "CPE.RAUM");

        assertEquals(value, "");
    }

    public void getCbDaten() {
        CarrierbestellungBuilder carrierbestellungBuilder = getBuilder(CarrierbestellungBuilder.class).withKundeVorOrt(Boolean.TRUE).withLbz("lbz123").withVtrNr("vtr123").withBereitstellungAm(DateTools.createDate(2008, 11, 22));
        carrierbestellungBuilder.build();

        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class).withAuftragNoOrig(BILLING_AUFTRAG_NO_FOR_TESTING);
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class).withAuftragDatenBuilder(auftragDatenBuilder);
        AuftragTechnikBuilder auftragTechnikBuilder = getBuilder(AuftragTechnikBuilder.class).withAuftragBuilder(auftragBuilder);
        EndstelleBuilder endstelleBuilder = getBuilder(EndstelleBuilder.class).withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_A).withCb2EsBuilder(carrierbestellungBuilder.getCb2EsBuilder());
        auftragTechnikBuilder.withEndstelleBuilder(endstelleBuilder).build();

        String lbz = reportingService.getValue(BILLING_AUFTRAG_NO_FOR_TESTING, "esa.cb.lbz");
        assertEquals(lbz, "lbz123");

        String vtrnr = reportingService.getValue(BILLING_AUFTRAG_NO_FOR_TESTING, "esa.cb.vtrnr");
        assertEquals(vtrnr, "vtr123");

        String bereitstellung = reportingService.getValue(BILLING_AUFTRAG_NO_FOR_TESTING, "esa.cb.bereitstellung");
        assertEquals(bereitstellung, "22.12.2008");

        String kundevorort = reportingService.getValue(BILLING_AUFTRAG_NO_FOR_TESTING, "esa.cb.kundevorort");
        assertEquals(kundevorort, "Ja");

    }

    public void getCbDatenWithoutCarrierbestellung() {
        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class).withAuftragNoOrig(BILLING_AUFTRAG_NO_FOR_TESTING);
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class).withAuftragDatenBuilder(auftragDatenBuilder);
        AuftragTechnikBuilder auftragTechnikBuilder = getBuilder(AuftragTechnikBuilder.class).withAuftragBuilder(auftragBuilder);
        EndstelleBuilder endstelleBuilder = getBuilder(EndstelleBuilder.class).withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_A);
        auftragTechnikBuilder.withEndstelleBuilder(endstelleBuilder).build();

        String lbz = reportingService.getValue(BILLING_AUFTRAG_NO_FOR_TESTING, "esa.cb.lbz");
        assertEquals(lbz, "");

        String vtrnr = reportingService.getValue(BILLING_AUFTRAG_NO_FOR_TESTING, "esa.cb.vtrnr");
        assertEquals(vtrnr, "");

        String bereitstellung = reportingService.getValue(BILLING_AUFTRAG_NO_FOR_TESTING, "esa.cb.bereitstellung");
        assertEquals(bereitstellung, "");

        String kundevorort = reportingService.getValue(BILLING_AUFTRAG_NO_FOR_TESTING, "esa.cb.kundevorort");
        assertEquals(kundevorort, "");
    }

    public void getEndstelleADaten() {
        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class).withAuftragNoOrig(BILLING_AUFTRAG_NO_FOR_TESTING);
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class).withAuftragDatenBuilder(auftragDatenBuilder);
        AuftragTechnikBuilder auftragTechnikBuilder = getBuilder(AuftragTechnikBuilder.class).withAuftragBuilder(auftragBuilder);
        EndstelleBuilder endstelleBuilder = getBuilder(EndstelleBuilder.class).withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_A).withOrt("augsburg");

        auftragTechnikBuilder.withEndstelleBuilder(endstelleBuilder).build();

        String esaOrt = reportingService.getValue(BILLING_AUFTRAG_NO_FOR_TESTING, "esa.ort");
        assertEquals(esaOrt, "augsburg");
    }

    public void getEndstelleBDaten() {
        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class).withAuftragNoOrig(BILLING_AUFTRAG_NO_FOR_TESTING);
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class).withAuftragDatenBuilder(auftragDatenBuilder);
        AuftragTechnikBuilder auftragTechnikBuilder = getBuilder(AuftragTechnikBuilder.class).withAuftragBuilder(auftragBuilder);
        CCAddressBuilder ccAddressBuilder = getBuilder(CCAddressBuilder.class).withStrasse("Strasse").withStrasseAdd(null).withNummer("123").withHausnummerZusatz("a").withOrt("Haunstetten").withPlz("12345");

        EndstelleBuilder endstelleBuilder = getBuilder(EndstelleBuilder.class).withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B).withOrt("augsburg").withPlz("76543").withAddressBuilder(ccAddressBuilder);

        auftragTechnikBuilder.withEndstelleBuilder(endstelleBuilder).build();

        String esbOrt = reportingService.getValue(BILLING_AUFTRAG_NO_FOR_TESTING, "esb.ort");
        assertEquals(esbOrt, "Haunstetten");

        String esaOrt = reportingService.getValue(BILLING_AUFTRAG_NO_FOR_TESTING, "esa.ort");
        assertEquals(esaOrt, "");

        String esbPlz = reportingService.getValue(BILLING_AUFTRAG_NO_FOR_TESTING, "esb.plz");
        assertEquals(esbPlz, "12345");

        String esbStrasse = reportingService.getValue(BILLING_AUFTRAG_NO_FOR_TESTING, "esb.strasse");
        assertEquals(esbStrasse, "Strasse");

        String strasseAdd = reportingService.getValue(BILLING_AUFTRAG_NO_FOR_TESTING, "esb.strassezusatz");
        assertEquals(strasseAdd, "");

        String esbHausnummer = reportingService.getValue(BILLING_AUFTRAG_NO_FOR_TESTING, "esb.hausnummer");
        assertEquals(esbHausnummer, "123");

        String esbHausnummerZusatz = reportingService.getValue(BILLING_AUFTRAG_NO_FOR_TESTING,
                "esb.hausnummerZusatz");
        assertEquals(esbHausnummerZusatz, "a");
    }

    @DataProvider
    public Object[][] dataProviderGetValues4VerbindungsBezeichnung() {
        return new Object[][] {
                { Collections.singletonList("ADV18116-9999"), array("ADV18116-9999") },
                { Arrays.asList("ADV18116-7777", "ADV18116-8888", "ADV18116-9999"), array("ADV18116-7777", "ADV18116-8888", "ADV18116-9999") },

                { Arrays.asList("ADV18116-9999", "ADV18116-9999"), array("ADV18116-9999") },
                { Arrays.asList("ADV18116-9999", "ADV18116-9999", "ADV18116-9999"), array("ADV18116-9999") },
                { Arrays.asList("ADV18116-8888", "ADV18116-9999", "ADV18116-9999"), array("ADV18116-8888", "ADV18116-9999") },
        };
    }

    @Test(dataProvider = "dataProviderGetValues4VerbindungsBezeichnung")
    public void testGetValues4VerbindungsBezeichnung(List<String> vbzs, String[] expectedResult) {

        HashMap<String, VerbindungsBezeichnungBuilder> vbz2Builder = new HashMap<>();
        for (String vbz : vbzs) {
            vbz2Builder.put(vbz, getBuilder(VerbindungsBezeichnungBuilder.class).withVbz(vbz));
        }
        for (String vbz : vbzs) {
            AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class)
                    .withAuftragNoOrig(BILLING_AUFTRAG_NO_FOR_TESTING).withStatusId(AuftragStatus.IN_BETRIEB);
            AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class).withAuftragDatenBuilder(auftragDatenBuilder);
            AuftragTechnikBuilder auftragTechnikBuilder = getBuilder(AuftragTechnikBuilder.class).withAuftragBuilder(auftragBuilder);

            auftragTechnikBuilder.withVerbindungsBezeichnungBuilder(vbz2Builder.get(vbz)).build();
        }

        String result = reportingService.getValue(BILLING_AUFTRAG_NO_FOR_TESTING, "verbez");
        assertEqualsNoOrder(result.split(ReportingService.STRING_DELIMITER), expectedResult);
    }

    private void createAnsprechpartner(Ansprechpartner.Typ typ) {
        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class).withAuftragNoOrig(
                BILLING_AUFTRAG_NO_FOR_TESTING);
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class).withAuftragDatenBuilder(auftragDatenBuilder);
        CCAddressBuilder addressBuilder = getBuilder(CCAddressBuilder.class);
        ReferenceBuilder referenceBuilder = getBuilder(ReferenceBuilder.class).withId(typ.refId()).setPersist(false);
        AnsprechpartnerBuilder ansprechpartnerBuilder = getBuilder(AnsprechpartnerBuilder.class)
                .withAuftragBuilder(auftragBuilder).withAddressBuilder(addressBuilder).withType(typ)
                .withReferenceBuilder(referenceBuilder);
        ansprechpartnerBuilder.build();

    }

    public void getApName() {
        createAnsprechpartner(Ansprechpartner.Typ.TECH_SERVICE);

        String apName = reportingService.getValue(BILLING_AUFTRAG_NO_FOR_TESTING, "ap.name");
        assertEquals(apName, "Huber Sepp");
    }

    public void getApFax() {
        createAnsprechpartner(Ansprechpartner.Typ.TECH_SERVICE);

        String apFax = reportingService.getValue(BILLING_AUFTRAG_NO_FOR_TESTING, "ap.fax");
        assertEquals(apFax, "+49 89 321");
    }

    public void getApNameEsA() {
        createAnsprechpartner(Ansprechpartner.Typ.ENDSTELLE_A);

        String apPhone = reportingService.getValue(BILLING_AUFTRAG_NO_FOR_TESTING, "ap.a.phone");
        assertEquals(apPhone, "+49 89 123");
    }

    public void getApPBXEnterprise() {
        createAnsprechpartner(Ansprechpartner.Typ.PBX_ENTERPRISE_ADMINISTRATOR);
        String apPBXName = reportingService.getValue(BILLING_AUFTRAG_NO_FOR_TESTING, "ap.pbx.enterprise.email");
        assertEquals(apPBXName, "ncuhwniodvasdfhjn@mailinator.com");
    }

    public void getApPBXSite() {
        createAnsprechpartner(Ansprechpartner.Typ.PBX_SITE_ADMINISTRATOR);
        String apPBXName = reportingService.getValue(BILLING_AUFTRAG_NO_FOR_TESTING, "ap.pbx.site.mobile");
        assertEquals(apPBXName, "+49 mobil 123");
    }

    public void testIPSecS2S() {
        IPSecSite2SiteBuilder ips2sBuilder = getBuilder(IPSecSite2SiteBuilder.class);
        ips2sBuilder.build();
        Long billingAuftragId = ips2sBuilder.getAuftragBuilder().getAuftragDatenBuilder().getAuftragNoOrig();

        assertEquals(reportingService.getValue(billingAuftragId, "ipsecs2s.hostname"),
                ips2sBuilder.get().getHostname(), "Hostname (active) must be found");
        assertEquals(reportingService.getValue(billingAuftragId, "ipsecs2s.hostname.passive"),
                ips2sBuilder.get().getHostnamePassive(), "Hostname (passive) must be found");
        assertEquals(reportingService.getValue(billingAuftragId, "ipsecs2s.wanip"),
                ips2sBuilder.get().getVirtualWanIp(), "Wan IP must be found");
        assertEquals(reportingService.getValue(billingAuftragId, "ipsecs2s.wansubmask"),
                ips2sBuilder.get().getVirtualWanSubmask(), "Wan Submask must be found");
        assertEquals(reportingService.getValue(billingAuftragId, "ipsecs2s.wangateway"),
                ips2sBuilder.get().getWanGateway(), "Wan Gateway must be found");
        assertEquals(reportingService.getValue(billingAuftragId, "ipsecs2s.lanip"),
                ips2sBuilder.get().getVirtualLanIp(), "Lan IP must be found");
        assertEquals(reportingService.getValue(billingAuftragId, "ipsecs2s.lan2scramble"),
                ips2sBuilder.get().getVirtualLan2Scramble(), "Lan2Scramble Submask must be found");
        assertEquals(reportingService.getValue(billingAuftragId, "ipsecs2s.lansubmask"),
                ips2sBuilder.get().getVirtualLanSubmask(), "LAN Submask must be found");
        assertEquals(reportingService.getValue(billingAuftragId, "ipsecs2s.dialinno"),
                ips2sBuilder.get().getIsdnDialInNumber(), "Dialin No must be found");
        assertEquals(reportingService.getValue(billingAuftragId, "ipsecs2s.splittunnel"),
                "Nein", "Split tunnel must be found");
        assertEquals(reportingService.getValue(billingAuftragId, "ipsecs2s.preshared"),
                "Nein", "Preshared must be found");
        assertEquals(reportingService.getValue(billingAuftragId, "ipsecs2s.certificate"),
                "Nein", "Certificate must be found");
        assertEquals(reportingService.getValue(billingAuftragId, "ipsecs2s.loopbackip"),
                ips2sBuilder.get().getLoopbackIp(), "Loopback-IP must be found");
        assertEquals(reportingService.getValue(billingAuftragId, "ipsecs2s.loopbackip.passive"),
                ips2sBuilder.get().getLoopbackIpPassive(), "passive Loopback-IP must be found");
        assertEquals(reportingService.getValue(billingAuftragId, "ipsecs2s.access.carrier"),
                ips2sBuilder.get().getAccessCarrier(), "Carrier must be found");
        assertEquals(reportingService.getValue(billingAuftragId, "ipsecs2s.access.bandbreite"),
                ips2sBuilder.get().getAccessBandwidth(), "Bandwidth must be found");
        assertEquals(reportingService.getValue(billingAuftragId, "ipsecs2s.access.typ"),
                ips2sBuilder.get().getAccessType(), "Type must be found");
        assertEquals(reportingService.getValue(billingAuftragId, "ipsecs2s.access.auftragsnr"),
                ips2sBuilder.get().getAccessAuftragNr(), "AuftragsNr must be found");
        assertEquals(reportingService.getValue(billingAuftragId, "ipsecs2s.description"),
                ips2sBuilder.get().getDescription(), "Description must be found");
    }

    public void testIPSecC2S() {
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class);
        IPSecClient2SiteToken ipsecc2sToken = getBuilder(IPSecClient2SiteTokenBuilder.class)
                .withAuftragBuilder(auftragBuilder).build();
        Long billingAuftragId = auftragBuilder.getAuftragDatenBuilder().getAuftragNoOrig();
        flushAndClear();

        assertEquals(reportingService.getValue(billingAuftragId, "ipsecs2s.tokenserialno"),
                ipsecc2sToken.getSerialNumber(), "SerialNumber incorrect");
    }

    public void getVerbindungsBezeichnungWith4Draht() {
        AuftragDatenBuilder auftragDatenBuilder1 = getBuilder(AuftragDatenBuilder.class).withProdId(Produkt.PROD_ID_SDSL_1024).withAuftragNoOrig(BILLING_AUFTRAG_NO_FOR_TESTING);
        AuftragBuilder auftragBuilder1 = getBuilder(AuftragBuilder.class).withAuftragDatenBuilder(auftragDatenBuilder1);
        AuftragTechnikBuilder auftragTechnikBuilder1 = getBuilder(AuftragTechnikBuilder.class).withAuftragBuilder(auftragBuilder1);
        VerbindungsBezeichnungBuilder vbzBuilder1 = getBuilder(VerbindungsBezeichnungBuilder.class).withVbz("ADV18116-9999");
        auftragTechnikBuilder1.withVerbindungsBezeichnungBuilder(vbzBuilder1).build();

        ProduktBuilder prodBuilder = getBuilder(ProduktBuilder.class).withIsVierDraht(Boolean.TRUE);
        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class).withProdBuilder(prodBuilder).withAuftragNoOrig(BILLING_AUFTRAG_NO_FOR_TESTING);
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class).withAuftragDatenBuilder(auftragDatenBuilder);
        AuftragTechnikBuilder auftragTechnikBuilder = getBuilder(AuftragTechnikBuilder.class).withAuftragBuilder(auftragBuilder);
        VerbindungsBezeichnungBuilder vbzBuilder = getBuilder(VerbindungsBezeichnungBuilder.class).withVbz("ADV18116-8888");
        auftragTechnikBuilder.withVerbindungsBezeichnungBuilder(vbzBuilder).build();

        String verbez = reportingService.getValue(BILLING_AUFTRAG_NO_FOR_TESTING, "verbez");

        assertTrue(verbez.contains("ADV18116-9999"));
        assertTrue(verbez.contains("ADV18116-8888"));
    }

    public void testEnstelleConnectEndstelleDoesNotExist() {
        AuftragTechnikBuilder auftragTechnikBuilder = getBuilder(AuftragTechnikBuilder.class);
        EndstelleBuilder endstelleBuilder = getBuilder(EndstelleBuilder.class)
                .withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B)
                .withAuftragTechnikBuilder(auftragTechnikBuilder);
        getBuilder(EndstelleConnectBuilder.class).withEndstelleBuilder(endstelleBuilder)
                .withGebaude("Leuchtturm").build();
        flushAndClear();

        Long orderNo = auftragTechnikBuilder.getAuftragBuilder().getAuftragDatenBuilder().getAuftragNoOrig();
        assertValuesOfRetrievedReportingKeys(orderNo,
                new String[] {
                        "connect.gebaeude.A", "connect.etage.A", "connect.raum.A", "connect.schrank.A",
                        "connect.uebergabe.A", "connect.bandbreite.A", "connect.if.A",
                        "connect.routerinfo.A", "connect.routertyp.A", "connect.bemerkung.A",
                        "connect.default.gateway.A" },
                new String[] {
                        "", "", "", "",
                        "", "", "",
                        "", "", "",
                        "" }
        );
    }

    public void testEndstelleConnectEndstelleA() {
        AuftragTechnikBuilder auftragTechnikBuilder = getBuilder(AuftragTechnikBuilder.class);
        EndstelleBuilder endstelleBuilder = getBuilder(EndstelleBuilder.class)
                .withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_A)
                .withAuftragTechnikBuilder(auftragTechnikBuilder);
        EndstelleConnect ec = getBuilder(EndstelleConnectBuilder.class)
                .withEndstelleBuilder(endstelleBuilder)
                .withGebaude("Tower").withEtage("5").withRaum("5.17").withSchrank("Nr. 3")
                .withUebergabe("123").withBandbreite("16 Mbit/s")
                .withSchnittstelle(ESSchnittstelle._100BASEFX).withEinstellung(ESEinstellung.HALFDUPLEX)
                .withRouterInfo(ESRouterInfo.UNDEFINED).withRouterTyp("CISCO").withBemerkung("Meine längere Bemerkung")
                .withDefaultGateway("1.1.1.1")
                .build();
        flushAndClear();

        Long orderNo = auftragTechnikBuilder.getAuftragBuilder().getAuftragDatenBuilder().getAuftragNoOrig();
        assertValuesOfRetrievedReportingKeys(orderNo,
                new String[] {
                        "connect.gebaeude.A", "connect.etage.A", "connect.raum.A", "connect.schrank.A",
                        "connect.uebergabe.A", "connect.bandbreite.A", "connect.if.A",
                        "connect.routerinfo.A", "connect.routertyp.A", "connect.bemerkung.A",
                        "connect.default.gateway.A" },
                new String[] {
                        ec.getGebaeude(), ec.getEtage(), ec.getRaum(), ec.getSchrank(),
                        ec.getUebergabe(), ec.getBandbreite(), ec.getSchnittstelle().toString() + " " + ec.getEinstellung().toString(),
                        ec.getRouterinfo().toString(), ec.getRoutertyp(), ec.getBemerkung(),
                        ec.getDefaultGateway() }
        );
    }

    public void testEnstelleConnectEndstelleB() {
        AuftragTechnikBuilder auftragTechnikBuilder = getBuilder(AuftragTechnikBuilder.class);
        EndstelleBuilder endstelleBuilder = getBuilder(EndstelleBuilder.class)
                .withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B)
                .withAuftragTechnikBuilder(auftragTechnikBuilder);
        EndstelleConnect ec = getBuilder(EndstelleConnectBuilder.class)
                .withEndstelleBuilder(endstelleBuilder)
                .withGebaude("Burg").withEtage("U").withRaum("Kerker").withSchrank("Folterwerkzeuge")
                .withUebergabe("007").withBandbreite("3 bit/s")
                .withSchnittstelle(ESSchnittstelle.G703).withEinstellung(null)
                .withRouterInfo(ESRouterInfo.MIETROUTER).withRouterTyp("Modem").withBemerkung("Meine Bemerkung")
                .withDefaultGateway("1.0.0.0")
                .build();
        flushAndClear();

        Long orderNo = auftragTechnikBuilder.getAuftragBuilder().getAuftragDatenBuilder().getAuftragNoOrig();
        assertValuesOfRetrievedReportingKeys(orderNo,
                new String[] {
                        "connect.gebaeude.B", "connect.etage.B", "connect.raum.B", "connect.schrank.B",
                        "connect.uebergabe.B", "connect.bandbreite.B", "connect.if.B",
                        "connect.routerinfo.B", "connect.routertyp.B", "connect.bemerkung.B",
                        "connect.default.gateway.B" },
                new String[] {
                        ec.getGebaeude(), ec.getEtage(), ec.getRaum(), ec.getSchrank(),
                        ec.getUebergabe(), ec.getBandbreite(), ec.getSchnittstelle().toString() + " ",
                        ec.getRouterinfo().toString(), ec.getRoutertyp(), ec.getBemerkung(),
                        ec.getDefaultGateway() }
        );
    }

    /**
     * Helper to assert the retrieved values supplied by an array of keys against their corresponding expected values
     *
     * @param orderNo        original order number
     * @param reportingKeys  key to be retrieved from reporting
     * @param expectedValues values to be expected by <code>ReportingService.getValue()</code>
     */
    private void assertValuesOfRetrievedReportingKeys(Long orderNo, String[] reportingKeys, String[] expectedValues) {
        assertEquals(reportingKeys.length, expectedValues.length, "length of supplied arrays unequal");
        for (int i = 0; i < reportingKeys.length; i++) {
            String key = reportingKeys[i];
            String value = expectedValues[i];
            assertEquals(reportingService.getValue(orderNo, key), value,
                    "retrieved value for '" + key + "' incorrect");
        }
    }

    private Set<EndgeraetIp> createTestEndgeraetIps() {
        Set<EndgeraetIp> endgeraetIps = new HashSet<>();

        IPAddress ipAddress1 = getBuilder(IPAddressBuilder.class)
                .withAddressType(AddressTypeEnum.IPV4_with_prefixlength).withAddress("192.168.104.236/24").build();
        EndgeraetIp endgeraetIp1 = new EndgeraetIp();
        endgeraetIp1.setAddressType(AddressType.LAN.name());
        endgeraetIp1.setIpAddressRef(ipAddress1);

        IPAddress ipAddress2 = getBuilder(IPAddressBuilder.class)
                .withAddressType(AddressTypeEnum.IPV6_with_prefixlength).withAddress("2001:a60:a004:1::/48").build();
        EndgeraetIp endgeraetIp2 = new EndgeraetIp();
        endgeraetIp2.setAddressType(AddressType.LAN.name());
        endgeraetIp2.setIpAddressRef(ipAddress2);

        Reference purposeRef = getBuilder(ReferenceBuilder.class).withStrValue(IP_PURPOSE_TYPE).withRandomId().build();
        IPAddress ipV6Prefix = getBuilder(IPAddressBuilder.class).withAddressType(AddressTypeEnum.IPV6_prefix)
                .withPurpose(purposeRef)
                .withAddress("2001:db8::/32").build();

        IPAddress ipAddress3 = getBuilder(IPAddressBuilder.class).withAddressType(AddressTypeEnum.IPV6_full_eui64)
                .withAddress("2001:a60:a004::/64").build();
        EndgeraetIp endgeraetIp3 = new EndgeraetIp();
        endgeraetIp3.setAddressType(AddressType.LAN.name());
        endgeraetIp3.setIpAddressRef(ipAddress3);

        IPAddress ipAddress4 = getBuilder(IPAddressBuilder.class).withAddressType(AddressTypeEnum.IPV6_relative_eui64)
                .withAddress("0:0:1:1::/64").withPrefixRef(ipV6Prefix).build();
        EndgeraetIp endgeraetIp4 = new EndgeraetIp();
        endgeraetIp4.setAddressType(AddressType.LAN.name());
        endgeraetIp4.setIpAddressRef(ipAddress4);

        IPAddress ipAddress5 = getBuilder(IPAddressBuilder.class).withAddressType(AddressTypeEnum.IPV6_relative)
                .withAddress("0:0:1:2::/48").withPrefixRef(ipV6Prefix).build();
        EndgeraetIp endgeraetIp5 = new EndgeraetIp();
        endgeraetIp5.setAddressType(AddressType.LAN.name());
        endgeraetIp5.setIpAddressRef(ipAddress5);

        IPAddress ipAddress6 = getBuilder(IPAddressBuilder.class)
                .withAddressType(AddressTypeEnum.IPV4)
                .withAddress("192.168.0.31/24")
                .build();
        EndgeraetIp endgeraetIp6 = new EndgeraetIp();
        endgeraetIp6.setAddressType(AddressType.WAN.name());
        endgeraetIp6.setIpAddressRef(ipAddress6);

        IPAddress ipAddress7 = getBuilder(IPAddressBuilder.class).withAddressType(AddressTypeEnum.IPV6_relative)
                .withAddress("0:0:3:4::/48").withPrefixRef(ipV6Prefix).build();
        EndgeraetIp endgeraetIp7 = new EndgeraetIp();
        endgeraetIp7.setAddressType(AddressType.WAN.name());
        endgeraetIp7.setIpAddressRef(ipAddress7);

        IPAddress ipAddress8 = getBuilder(IPAddressBuilder.class).withAddressType(AddressTypeEnum.IPV4)
                .withAddress("61.245.247.17/28").build();
        EndgeraetIp endgeraetIp8 = new EndgeraetIp();
        endgeraetIp8.setAddressType(AddressType.LAN_VRRP);
        endgeraetIp8.setIpAddressRef(ipAddress8);

        IPAddress ipAddress9 = getBuilder(IPAddressBuilder.class).withAddressType(AddressTypeEnum.IPV6_with_prefixlength)
                .withAddress("2001:a60:a004:2::/48").build();
        EndgeraetIp endgeraetIp9 = new EndgeraetIp();
        endgeraetIp9.setAddressType(AddressType.LAN_VRRP);
        endgeraetIp9.setIpAddressRef(ipAddress9);

        endgeraetIps.add(endgeraetIp1);
        endgeraetIps.add(endgeraetIp2);
        endgeraetIps.add(endgeraetIp3);
        endgeraetIps.add(endgeraetIp4);
        endgeraetIps.add(endgeraetIp5);
        endgeraetIps.add(endgeraetIp6);
        endgeraetIps.add(endgeraetIp7);
        endgeraetIps.add(endgeraetIp8);
        endgeraetIps.add(endgeraetIp9);
        return endgeraetIps;
    }

    private void createEGConfigWithPortForwardings(Long billingAuftragNo, Set<PortForwarding> portForwardings) {
        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class).withAuftragNoOrig(billingAuftragNo);
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class).withAuftragDatenBuilder(auftragDatenBuilder);
        EGBuilder egBuilder = getBuilder(EGBuilder.class).withEgName("AVM FritzBox 3170");
        EG2AuftragBuilder eg2AuftragBuilder = getBuilder(EG2AuftragBuilder.class)
                .withAuftragBuilder(auftragBuilder)
                .withEgBuilder(egBuilder);
        EGTypeBuilder egTypeBuilder = getBuilder(EGTypeBuilder.class)
                .withHersteller("Thomson")
                .withModell("MyModell");
        getBuilder(EGConfigBuilder.class)
                .withEGTypeBuilder(egTypeBuilder)
                .withServiceVertrag(ServiceVertrag.gekauft)
                .withNatActive(Boolean.FALSE)
                .withDhcpActive(Boolean.TRUE)
                .withEG2AuftragBuilder(eg2AuftragBuilder)
                .withPortForwardings(portForwardings)
                .withModellZusatz("H3612")
                .build();
    }

    @Test
    public void testMVSEnterpriseSuccess() {
        final String ENTERPRISE_PASSWORD = "secret";
        final String ENTERPRISE_USER_NAME = "Mustermann";
        final String ENTERPRISE_MAIL = "abc.def@ghi.jk";
        final String ENTERPRISE_DOMAIN = "Kunde.mpbx.de";

        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class);
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class).withAuftragDatenBuilder(auftragDatenBuilder);
        getBuilder(AuftragMVSEnterpriseBuilder.class).withAuftragBuilder(auftragBuilder).withDomain(ENTERPRISE_DOMAIN)
                .withMail(ENTERPRISE_MAIL).withPassword(ENTERPRISE_PASSWORD).withUserName(ENTERPRISE_USER_NAME).build();

        String value = reportingService.getValue(auftragDatenBuilder.get().getAuftragNoOrig(), "mvs.enterprise.domain");
        assertEquals(value, ENTERPRISE_DOMAIN, "Domain stimmt nicht überein!");
        value = reportingService.getValue(auftragDatenBuilder.get().getAuftragNoOrig(), "mvs.enterprise.email.address");
        assertEquals(value, ENTERPRISE_MAIL, "Email Adresse stimmt nicht überein!");
        value = reportingService.getValue(auftragDatenBuilder.get().getAuftragNoOrig(), "mvs.enterprise.password");
        assertEquals(value, ENTERPRISE_PASSWORD, "Passwort stimmt nicht überein!");
        value = reportingService.getValue(auftragDatenBuilder.get().getAuftragNoOrig(), "mvs.enterprise.username");
        assertEquals(value, ENTERPRISE_USER_NAME, "Username stimmt nicht überein!");
    }

    @Test
    public void testMVSSiteSuccess() {
        final String MVS_PASSWORD = "secret";
        final String SITE_SUBDOMAIN = "Standort";
        final String MVS_USER_NAME = "Mustermann";
        final String ENTERPRISE_MAIL = "abc.def@ghi.jk";
        final String ENTERPRISE_DOMAIN = "Kunde.mpbx.de";

        AuftragDatenBuilder auftragDatenBuilderEnterprise = getBuilder(AuftragDatenBuilder.class);
        AuftragBuilder auftragBuilderEnterprise = getBuilder(AuftragBuilder.class).withAuftragDatenBuilder(
                auftragDatenBuilderEnterprise);
        AuftragDatenBuilder auftragDatenBuilderSite = getBuilder(AuftragDatenBuilder.class);
        AuftragBuilder auftragBuilderSite = getBuilder(AuftragBuilder.class).withAuftragDatenBuilder(
                auftragDatenBuilderSite);

        AuftragMVSEnterpriseBuilder auftragMVSEnterpriseBuilder = getBuilder(AuftragMVSEnterpriseBuilder.class)
                .withAuftragBuilder(auftragBuilderEnterprise).withDomain(ENTERPRISE_DOMAIN).withMail(ENTERPRISE_MAIL)
                .withPassword(MVS_PASSWORD).withUserName(MVS_USER_NAME);
        getBuilder(AuftragMVSSiteBuilder.class).withAuftragBuilder(auftragBuilderSite)
                .withAuftragMVSEnterpriseBuilder(auftragMVSEnterpriseBuilder).withSubdomain(SITE_SUBDOMAIN)
                .withPassword(MVS_PASSWORD).withUserName(MVS_USER_NAME).build();

        // @formatter:off
        String value = reportingService.getValue(auftragDatenBuilderSite.get().getAuftragNoOrig(), "mvs.site.subdomain");
        assertEquals(value, SITE_SUBDOMAIN, "Subdomain stimmt nicht überein!");
        value = reportingService.getValue(auftragDatenBuilderSite.get().getAuftragNoOrig(), "mvs.site.password");
        assertEquals(value, MVS_PASSWORD, "Passwort stimmt nicht überein!");
        value = reportingService.getValue(auftragDatenBuilderSite.get().getAuftragNoOrig(), "mvs.site.username");
        assertEquals(value, MVS_USER_NAME, "Username stimmt nicht überein!");
        // @formatter:on
        // Qualified Domains lassen sich im Service Test nicht abdecken, da
        // hierzu Taifundaten (Kunde) notwendig sind, diese aber nicht durch
        // Builder angelegt werden koennen. Abdeckung erfolgt durch MVS Unit
        // Test (getQualifiedDomain()).
    }

    @Test
    public void testIPV6Prefix() {
        Reference purposeRef = getBuilder(ReferenceBuilder.class).withStrValue(IP_PURPOSE_TYPE).withRandomId().build();

        IPAddress firstIpAddress = getBuilder(IPAddressBuilder.class).withAddressType(AddressTypeEnum.IPV6_prefix)
                .withBillingOrderNumber(BILLING_AUFTRAG_NO_FOR_TESTING).withAddress("2000:DA01::/32")
                .withNetId(1L).withPurpose(purposeRef).build();

        IPAddress secondIpAddress = getBuilder(IPAddressBuilder.class)
                .withBillingOrderNumber(BILLING_AUFTRAG_NO_FOR_TESTING).withAddressType(AddressTypeEnum.IPV6_prefix)
                .withAddress("2001:a60:b007::/48").withNetId(1L).withPurpose(purposeRef).build();

        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class).withAuftragNoOrig(
                BILLING_AUFTRAG_NO_FOR_TESTING);
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class).withAuftragDatenBuilder(auftragDatenBuilder);
        getBuilder(EG2AuftragBuilder.class).withAuftragBuilder(auftragBuilder).build();

        String value = reportingService.getValue(BILLING_AUFTRAG_NO_FOR_TESTING, "IPV6.PREFIX");

        assertTrue(value.contains(firstIpAddress.getAddress()));
        assertTrue(value.contains(secondIpAddress.getAddress()));
    }

    @Test
    public void testIPV6DynamicAddress_Success() {
        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class).withAuftragNoOrig(
                BILLING_AUFTRAG_NO_FOR_TESTING);
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class).withAuftragDatenBuilder(auftragDatenBuilder);
        getBuilder(Auftrag2TechLeistungBuilder.class).withAuftragBuilder(auftragBuilder)
                .withTechLeistungId(TechLeistung.ID_DYNAMIC_IP_V6).build();

        String value = reportingService.getValue(BILLING_AUFTRAG_NO_FOR_TESTING, "IPV6.DYNAMIC_ADDRESS");

        assertEquals(value, "ja");
    }

    @Test
    public void testIPV6DynamicAddress_Fail() {
        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class).withAuftragNoOrig(
                BILLING_AUFTRAG_NO_FOR_TESTING);
        getBuilder(AuftragBuilder.class).withAuftragDatenBuilder(auftragDatenBuilder).build();

        String value = reportingService.getValue(BILLING_AUFTRAG_NO_FOR_TESTING, "IPV6.DYNAMIC_ADDRESS");

        assertEquals(value, "nein");
    }

    @Test
    public void testInternetLogindata() {
        final String value = reportingService.getValue(2697163L, "logindata.internet");
        assertNotNull(value);
        assertTrue(value.contains("PPP-User"));
        assertTrue(value.contains("PPP-Passwort"));
        assertTrue(value.contains("IP Protokoll"));
    }

}
