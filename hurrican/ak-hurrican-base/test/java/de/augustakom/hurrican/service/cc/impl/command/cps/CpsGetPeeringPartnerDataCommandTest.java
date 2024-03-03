package de.augustakom.hurrican.service.cc.impl.command.cps;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import java.util.stream.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.dao.cc.Auftrag2PeeringPartnerDAO;
import de.augustakom.hurrican.model.cc.AddressTypeEnum;
import de.augustakom.hurrican.model.cc.Auftrag2PeeringPartner;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.CPSTransactionBuilder;
import de.augustakom.hurrican.model.cc.IPAddress;
import de.augustakom.hurrican.model.cc.IPAddressBuilder;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSServiceOrderData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CpsSbcIp;
import de.augustakom.hurrican.model.cc.sip.SipPeeringPartner;
import de.augustakom.hurrican.model.cc.sip.SipPeeringPartnerBuilder;
import de.augustakom.hurrican.model.cc.sip.SipSbcIpSet;
import de.augustakom.hurrican.model.cc.sip.SipSbcIpSetBuilder;
import de.augustakom.hurrican.service.cc.SipPeeringPartnerService;

@Test(groups = BaseTest.UNIT)
public class CpsGetPeeringPartnerDataCommandTest extends BaseTest {

    @Mock
    Auftrag2PeeringPartnerDAO auftrag2PeeringPartnerDAO;
    @Mock
    SipPeeringPartnerService sipPeeringPartnerService;

    @Spy
    @InjectMocks
    CpsGetPeeringPartnerDataCommand cut;

    final CPSTransaction cpsTx = new CPSTransactionBuilder()
                                .withRandomId()
                                .withEstimatedExecTime(new Date())
                                .build();

    final AuftragDaten auftragDaten = new AuftragDatenBuilder()
                                        .withRandomId()
                                        .withRandomAuftragId()
                                        .build();


    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testExecute() throws Exception {
        final CPSServiceOrderData soData = new CPSServiceOrderData();
        final IPAddress ip1 = new IPAddressBuilder().withRandomId().withAddress("2001:a60:90e7:ffff::1/64").build();
        final IPAddress ip2 = new IPAddressBuilder().withRandomId().withAddress("192.168.2.1").withAddressType(AddressTypeEnum.IPV4).build();
        final IPAddress ip3 = new IPAddressBuilder().withRandomId().withAddress("2001:a60:90dc::/48").build();
        final IPAddress ip4 = new IPAddressBuilder().withRandomId().withAddress("::/64").build();
        final SipSbcIpSet validSbcIpSet1 = new SipSbcIpSetBuilder()
                .withRandomId()
                .withGueltigAb(new Date())
                .withSbcIps(Lists.newArrayList(ip1, ip2))
                .build();
        final SipSbcIpSet validSbcIpSet2 = new SipSbcIpSetBuilder()
                .withRandomId()
                .withGueltigAb(Date.from(LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .withSbcIps(Lists.newArrayList(ip3))
                .build();
        final SipSbcIpSet invalidSbcIpSet = new SipSbcIpSetBuilder()
                .withRandomId()
                .withGueltigAb(Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .withSbcIps(Lists.newArrayList(ip4))
                .build();

        final SipPeeringPartner peeringPartner1 = new SipPeeringPartnerBuilder()
                .withRandomId()
                .withSbcIpSets(Lists.newArrayList(validSbcIpSet1, invalidSbcIpSet))
                .build();
        final SipPeeringPartner peeringPartner2 = new SipPeeringPartnerBuilder()
                .withRandomId()
                .withSbcIpSets(Lists.newArrayList(validSbcIpSet2))
                .build();
        final Auftrag2PeeringPartner auftrag2PeeringPartner1 = new Auftrag2PeeringPartner();
        auftrag2PeeringPartner1.setPeeringPartnerId(peeringPartner1.getId());
        final Auftrag2PeeringPartner auftrag2PeeringPartner2 = new Auftrag2PeeringPartner();
        auftrag2PeeringPartner1.setPeeringPartnerId(peeringPartner2.getId());


        doReturn(cpsTx).when(cut).getCPSTransaction();
        doReturn(auftragDaten).when(cut).getAuftragDaten();
        doReturn(soData).when(cut).getServiceOrderData();

        when(auftrag2PeeringPartnerDAO
                .findValidAuftrag2PeeringPartner(auftragDaten.getAuftragId(), cpsTx.getEstimatedExecTime()))
                .thenReturn(ImmutableList.of(auftrag2PeeringPartner1, auftrag2PeeringPartner2));
        when(sipPeeringPartnerService
                .findPeeringPartnerById(auftrag2PeeringPartner1.getPeeringPartnerId())).thenReturn(peeringPartner1);
        when(sipPeeringPartnerService
                .findPeeringPartnerById(auftrag2PeeringPartner2.getPeeringPartnerId())).thenReturn(peeringPartner2);

        final ServiceCommandResult result = (ServiceCommandResult) cut.execute();
        assertEquals(result.getCheckStatus(), ServiceCommandResult.CHECK_STATUS_OK);
        assertThat(soData.getPeeringPartner(), hasSize(3));
        assertThat(adressesFromCpsSbcIps(soData), containsInAnyOrder("2001:a60:90e7:ffff:0:0:0:1", "192.168.2.1", "2001:a60:90dc:0:0:0:0:0"));
        assertThat(getCpsSbcIpByAddress(soData, "2001:a60:90e7:ffff:0:0:0:1").netmask, equalTo(64));
        assertThat(getCpsSbcIpByAddress(soData, "2001:a60:90e7:ffff:0:0:0:1").addressType, equalTo("IPV6"));
        assertThat(getCpsSbcIpByAddress(soData, "192.168.2.1").netmask, equalTo(32));
        assertThat(getCpsSbcIpByAddress(soData, "192.168.2.1").addressType, equalTo("IPV4"));
        assertThat(getCpsSbcIpByAddress(soData, "2001:a60:90dc:0:0:0:0:0").netmask, equalTo(48));
        assertTrue(soData.getPeeringPartner().stream().allMatch(sbcIp -> sbcIp.layer2Priority == 5));
    }

    private CpsSbcIp getCpsSbcIpByAddress(final CPSServiceOrderData soData, final String address) {
        return soData.getPeeringPartner().stream().filter(cpsSbcIp -> cpsSbcIp.address.equals(address)).findFirst().get();
    }

    private List<String> adressesFromCpsSbcIps(CPSServiceOrderData soData) {
        return soData.getPeeringPartner().stream()
                .map(ip -> ip.address)
                .collect(Collectors.toList());
    }
}
