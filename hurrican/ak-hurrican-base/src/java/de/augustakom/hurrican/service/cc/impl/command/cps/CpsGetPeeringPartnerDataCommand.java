/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.02.2015 08:30
 */
package de.augustakom.hurrican.service.cc.impl.command.cps;

import java.util.*;
import java.util.stream.*;
import javax.annotation.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.annotation.CcTxMandatory;
import de.augustakom.hurrican.dao.cc.Auftrag2PeeringPartnerDAO;
import de.augustakom.hurrican.model.cc.Auftrag2PeeringPartner;
import de.augustakom.hurrican.model.cc.IPAddress;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CpsSbcIp;
import de.augustakom.hurrican.model.cc.sip.SipPeeringPartner;
import de.augustakom.hurrican.model.cc.sip.SipSbcIpSet;
import de.augustakom.hurrican.service.cc.SipPeeringPartnerService;

/**
 *
 */
public class CpsGetPeeringPartnerDataCommand extends AbstractCPSDataCommand {

    private static final Logger LOGGER = Logger.getLogger(CpsGetPeeringPartnerDataCommand.class);

    @Autowired
    private Auftrag2PeeringPartnerDAO auftrag2PeeringPartnerDAO;

    @Resource(name = "de.augustakom.hurrican.service.cc.SipPeeringPartnerService")
    private SipPeeringPartnerService sipPeeringPartnerService;

    @Override
    @CcTxMandatory
    public Object execute() throws Exception {
        try {
            final Date estimatedExecTime = getCPSTransaction().getEstimatedExecTime();
            final Stream<SipPeeringPartner> sipPeeringPartner = findSipPeeringPartner(estimatedExecTime);
            final Stream<IPAddress> ipsFromValidIpSets = findValidSbcIpAddresses(estimatedExecTime, sipPeeringPartner);
            final List<CpsSbcIp> peeringPartner = transform(ipsFromValidIpSets);

            getServiceOrderData().setPeeringPartner(peeringPartner.isEmpty() ? null : peeringPartner);

            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_OK,
                    null, this.getClass());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    "Error loading Peering-Partner-Data: " + e.getMessage(), this.getClass());
        }
    }

    private List<CpsSbcIp> transform(final Stream<IPAddress> ipsFromValidIpSets) {
        return ipsFromValidIpSets
                .map(ip -> buildCpsSbcIp(ip))
                .collect(Collectors.toList());
    }

    private Stream<IPAddress> findValidSbcIpAddresses(final Date estimatedExecTime, final Stream<SipPeeringPartner> sipPeeringPartner) {
        final Stream<SipSbcIpSet> sbcSets = sipPeeringPartner
                .map(pp -> pp.getCurrentSbcIpSetAt(estimatedExecTime))
                .filter(option -> option.isPresent())
                .map(option -> option.get());

        return sbcSets.flatMap((SipSbcIpSet sbcSet) -> sbcSet.getSbcIps().stream());
    }

    private Stream<SipPeeringPartner> findSipPeeringPartner(final Date estimatedExecTime) {
        final List<Auftrag2PeeringPartner> a2pps =
                auftrag2PeeringPartnerDAO.findValidAuftrag2PeeringPartner(getAuftragDaten().getAuftragId(),
                        estimatedExecTime);
        return a2pps.stream().map((Auftrag2PeeringPartner a2pp) ->
                sipPeeringPartnerService.findPeeringPartnerById(a2pp.getPeeringPartnerId()));
    }

    private CpsSbcIp buildCpsSbcIp(final IPAddress ip) {
        return new CpsSbcIp.CpsSbcIpBuilder()
                .withAddress(ip.getAddressWithoutPrefix())
                .withAddressType(getAddressType(ip))
                .withNetmask(ip.getPrefixLength())
                .build();
    }

    private String getAddressType(final IPAddress ip) {
        final String addressType;
        if (ip.isIPV4()) {
            addressType = "IPV4";
        }
        else if (ip.isIPV6()) {
            addressType = "IPV6";
        }
        else
            throw new RuntimeException(String.format("IP mit id %s is weder eine V4- noch eine V6-Adresse", ip.getId()));
        return addressType;
    }
}
