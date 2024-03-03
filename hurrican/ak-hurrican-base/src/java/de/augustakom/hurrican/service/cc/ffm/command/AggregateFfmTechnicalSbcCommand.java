/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.02.2015
 */
package de.augustakom.hurrican.service.cc.ffm.command;

import java.util.*;
import java.util.stream.*;
import javax.annotation.*;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.model.builder.cdm.workforceservice.v1.SBCAddressBuilder;
import de.augustakom.hurrican.model.cc.IPAddress;
import de.augustakom.hurrican.model.cc.sip.SipPeeringPartner;
import de.augustakom.hurrican.model.cc.sip.SipSbcIpSet;
import de.augustakom.hurrican.service.cc.SipPeeringPartnerService;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.esb.cdm.resource.workforceservice.v1.OrderTechnicalParams;
import de.mnet.esb.cdm.resource.workforceservice.v1.WorkforceOrder;

// @formatter:off
/**
 * Command-Klasse, die fuer die 'OrderTechnicalParams' Daten einer FFM {@link WorkforceOrder} folgende Daten aggregiert:
 * <br/>
 * <ul>
 *     <li>SBC</li>
 * </ul>
 */
// @formatter:on
@Component("de.augustakom.hurrican.service.cc.ffm.command.AggregateFfmTechnicalSbcCommand")
@Scope("prototype")
public class AggregateFfmTechnicalSbcCommand extends AbstractFfmCommand {

    private static final Logger LOGGER = Logger.getLogger(AggregateFfmTechnicalCpeCommand.class);

    @Resource(name = "de.augustakom.hurrican.service.cc.SipPeeringPartnerService")
    private SipPeeringPartnerService sipPeeringPartnerService;

    @Override
    public Object execute() throws Exception {
        try {
            checkThatWorkforceOrderHasTechnicalParams();

            SipPeeringPartner peeringPartner =
                    sipPeeringPartnerService.findPeeringPartner4Auftrag(getAuftragId(),
                            DateConverterUtils.asDate(getReferenceDate()));
            if (peeringPartner != null) {
                Optional<SipSbcIpSet> sbcIpSet = peeringPartner.getCurrentSbcIpSetAt(
                        DateConverterUtils.asDate(getReferenceDate()));

                if (sbcIpSet.isPresent()) {
                    getWorkforceOrder().getDescription().getTechParams().getSBCAddress()
                            .addAll(convert(sbcIpSet.get().getSbcIps()));
                }
            }

            return ServiceCommandResult.createCmdResult(
                    ServiceCommandResult.CHECK_STATUS_OK, null, this.getClass());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    "Error loading FFM TechnicalOrderParams SBC Data: " + e.getMessage(), this.getClass());
        }
    }

    List<OrderTechnicalParams.SBCAddress> convert(List<IPAddress> ipAddresses) {
        if (ipAddresses == null) {
            return null;
        }
        return ipAddresses
                .stream()
                .map(
                        ip ->
                                new SBCAddressBuilder()
                                        .withAddress(ip.getAddress())
                                        .withAddressType(ip.getIpType().mnemonic)
                                        .build()
                )
                .collect(Collectors.toList());
    }

}
