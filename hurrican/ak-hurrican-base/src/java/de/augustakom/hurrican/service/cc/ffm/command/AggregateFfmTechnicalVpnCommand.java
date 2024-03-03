/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.02.2015
 */
package de.augustakom.hurrican.service.cc.ffm.command;

// @formatter:off
import javax.annotation.*;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.model.builder.cdm.workforceservice.v1.VPNBuilder;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.VPN;
import de.augustakom.hurrican.model.cc.VPNKonfiguration;
import de.augustakom.hurrican.service.cc.ReferenceService;
import de.augustakom.hurrican.service.cc.VPNService;
import de.mnet.esb.cdm.resource.workforceservice.v1.OrderTechnicalParams;
import de.mnet.esb.cdm.resource.workforceservice.v1.WorkforceOrder;

/**
 * Command-Klasse, die fuer die 'OrderTechnicalParams' Daten einer FFM {@link WorkforceOrder} folgende Daten aggregiert:
 * <br/>
 * <ul>
 *     <li>vpn</li>
 *     <ul>
 *         <li>vplsId</li>
 *         <li>vpnName</li>
 *         <li>vpnTyp</li>
 *         <li>vpnId</li>
 *         <li>einwahl</li>
 *         <li>...</li>
 *     </ul>
 * </ul>
 */
// @formatter:on
@Component("de.augustakom.hurrican.service.cc.ffm.command.AggregateFfmTechnicalVpnCommand")
@Scope("prototype")
public class AggregateFfmTechnicalVpnCommand extends AbstractFfmCommand {

    private static final Logger LOGGER = Logger.getLogger(AggregateFfmTechnicalVpnCommand.class);

    @Resource(name = "de.augustakom.hurrican.service.cc.VPNService")
    private VPNService vpnService;
    @Resource(name = "de.augustakom.hurrican.service.cc.ReferenceService")
    private ReferenceService referenceService;

    @Override
    public Object execute() throws Exception {
        try {
            checkThatWorkforceOrderHasTechnicalParams();

            VPN vpn = vpnService.findVPNByAuftragId(getAuftragId());
            if (vpn != null) {
                VPNKonfiguration vpnKonfiguration = vpnService.findVPNKonfiguration4Auftrag(getAuftragId());
                Reference vpnType = referenceService.findReference(vpn.getVpnType());

                OrderTechnicalParams.VPN cdmVpn = new VPNBuilder()
                        .withVpnId(String.format("%s", vpn.getVpnNr()))
                        .withVpnName(vpn.getVpnName())
                        .withEinwahl(vpn.getEinwahl())
                        .withVpnTyp((vpnType != null) ? vpnType.getStrValue() : null)
                        .withVplsId((vpnKonfiguration != null) ? vpnKonfiguration.getVplsId() : null)
                        .build();

                getWorkforceOrder().getDescription().getTechParams().setVPN(cdmVpn);
            }

            return ServiceCommandResult.createCmdResult(
                    ServiceCommandResult.CHECK_STATUS_OK, null, this.getClass());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    "Error loading FFM TechnicalOrderParams VPN Data: " + e.getMessage(), this.getClass());
        }
    }

}
