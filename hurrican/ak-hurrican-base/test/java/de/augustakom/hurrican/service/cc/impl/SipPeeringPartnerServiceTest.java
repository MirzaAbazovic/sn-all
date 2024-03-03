package de.augustakom.hurrican.service.cc.impl;

import static org.testng.Assert.*;

import java.util.*;
import javax.annotation.*;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.AddressTypeEnum;
import de.augustakom.hurrican.model.cc.IPAddress;
import de.augustakom.hurrican.model.cc.IPAddressBuilder;
import de.augustakom.hurrican.model.cc.sip.SipPeeringPartner;
import de.augustakom.hurrican.model.cc.sip.SipPeeringPartnerBuilder;
import de.augustakom.hurrican.model.cc.sip.SipSbcIpSet;
import de.augustakom.hurrican.model.cc.sip.SipSbcIpSetBuilder;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.cc.SipPeeringPartnerService;

/**
 * SipPeeringPartnerServiceTest
 */
@Test(groups = { BaseTest.SERVICE })
public class SipPeeringPartnerServiceTest extends AbstractHurricanBaseServiceTest {

    @Resource(name = "de.augustakom.hurrican.service.cc.SipPeeringPartnerService")
    private SipPeeringPartnerService cut;

    private SipPeeringPartner createSipPP() {
        IPAddress ipAddressV41 = getBuilder(IPAddressBuilder.class).withAddress("192.168.1.1")
                .withAddressType(AddressTypeEnum.IPV4).setPersist(false).build();
        IPAddress ipAddressV42 = getBuilder(IPAddressBuilder.class).withAddress("192.168.1.2")
                .withAddressType(AddressTypeEnum.IPV4).setPersist(false).build();
        IPAddress ipAddressV43 = getBuilder(IPAddressBuilder.class).withAddress("192.168.1.3")
                .withAddressType(AddressTypeEnum.IPV4).setPersist(false).build();
        SipSbcIpSet sipSbcIpSet1 = getBuilder(SipSbcIpSetBuilder.class).withSbcIps(Arrays.asList(ipAddressV41, ipAddressV42))
                .setPersist(false).build();
        SipSbcIpSet sipSbcIpSet2 = getBuilder(SipSbcIpSetBuilder.class).withSbcIps(Arrays.asList(ipAddressV43))
                .setPersist(false).build();
        SipPeeringPartner sipPeeringPartner = getBuilder(SipPeeringPartnerBuilder.class).withIsActive(true)
                .withSbcIpSets(Arrays.asList(sipSbcIpSet1, sipSbcIpSet2)).withName("PeeringTest").build();
        return sipPeeringPartner;
    }

    public void testFindPeeringPartnerById() {
        SipPeeringPartner sipPeeringPartner = createSipPP();
        SipPeeringPartner sipPPEntity = cut.findPeeringPartnerById(sipPeeringPartner.getId());
        assertNotNull(sipPPEntity);
        assertTrue(sipPPEntity.getId().equals(sipPeeringPartner.getId()));
        List<SipSbcIpSet> sbcIpSets = sipPeeringPartner.getSbcIpSets();
        assertEquals(sbcIpSets.size(), 2);
        assertRegExMatch(String.valueOf(sbcIpSets.get(0).getSbcIps().size()), "1|2");
        assertRegExMatch(String.valueOf(sbcIpSets.get(1).getSbcIps().size()), "1|2");
        for (SipSbcIpSet sbcIpSet : sbcIpSets) {
            for (IPAddress ipAddress : sbcIpSet.getSbcIps()) {
                assertTrue(ipAddress.getAddress().startsWith("192.168.1."));
                assertTrue(ipAddress.isIPV4());
            }
        }
    }

}
