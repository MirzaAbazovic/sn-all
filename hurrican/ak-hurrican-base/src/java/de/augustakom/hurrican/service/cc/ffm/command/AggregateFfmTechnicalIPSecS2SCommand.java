/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.02.2015
 */
package de.augustakom.hurrican.service.cc.ffm.command;

import javax.annotation.*;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.model.builder.cdm.workforceservice.FfmHelper;
import de.augustakom.hurrican.model.builder.cdm.workforceservice.v1.IPSecS2SBuilder;
import de.augustakom.hurrican.model.cc.IPSecSite2Site;
import de.augustakom.hurrican.service.cc.IPSecService;
import de.mnet.esb.cdm.resource.workforceservice.v1.OrderTechnicalParams;
import de.mnet.esb.cdm.resource.workforceservice.v1.WorkforceOrder;

/**
 * Command-Klasse, die fuer die 'OrderTechnicalParams' Daten einer FFM {@link WorkforceOrder} folgende Daten aggregiert:
 * <br/>
 * <ul>
 *     <li>IPSecS2S</li>
 * </ul>
 */
// @formatter:on
@Component("de.augustakom.hurrican.service.cc.ffm.command.AggregateFfmTechnicalIPSecS2SCommand")
@Scope("prototype")
public class AggregateFfmTechnicalIPSecS2SCommand extends AbstractFfmCommand {

    private static final Logger LOGGER = Logger.getLogger(AggregateFfmTechnicalIPSecS2SCommand.class);

    @Resource(name = "de.augustakom.hurrican.service.cc.IPSecService")
    private IPSecService ipSecService;

    @Override
    public Object execute() throws Exception {
        try {
            checkThatWorkforceOrderHasTechnicalParams();

            IPSecSite2Site ipSecSite2Site = ipSecService.findIPSecSiteToSite(getAuftragId());
            if (ipSecSite2Site != null) {
                OrderTechnicalParams.IPSecS2S cdmIPSecS2S = new IPSecS2SBuilder()
                        .withHostName(ipSecSite2Site.getHostname())
                        .withHostNamePassive(ipSecSite2Site.getHostnamePassive())
                        .withWanIP(ipSecSite2Site.getVirtualWanIp())
                        .withWanGateway(ipSecSite2Site.getWanGateway())
                        .withWanSubnet(ipSecSite2Site.getVirtualWanSubmask())
                        .withLoopbackIp(ipSecSite2Site.getLoopbackIp())
                        .withLoopbackIpPassive(ipSecSite2Site.getLoopbackIpPassive())
                        .withVirtualLanIp(ipSecSite2Site.getVirtualLanIp())
                        .withNetToEncrypt(ipSecSite2Site.getVirtualLan2Scramble())
                        .withVirtualLanSubnet(ipSecSite2Site.getVirtualLanSubmask())
                        .withDialInNo(ipSecSite2Site.getIsdnDialInNumber())
                        .withSplitTunnel(FfmHelper.convertBoolean(ipSecSite2Site.getSplitTunnel()))
                        .withPreSharedKey(FfmHelper.convertBoolean(ipSecSite2Site.getHasPresharedKey()))
                        .withCertificate(FfmHelper.convertBoolean(ipSecSite2Site.getHasCertificate()))
                        .withDescription(ipSecSite2Site.getDescription())
                        .withCarrier(ipSecSite2Site.getAccessCarrier())
                        .withBandwidth(ipSecSite2Site.getAccessBandwidth())
                        .withType(ipSecSite2Site.getAccessType())
                        .withAuftragsNr(ipSecSite2Site.getAccessAuftragNr())
                        .build();

                getWorkforceOrder().getDescription().getTechParams().setIPSecS2S(cdmIPSecS2S);
            }

            return ServiceCommandResult.createCmdResult(
                    ServiceCommandResult.CHECK_STATUS_OK, null, this.getClass());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    "Error loading FFM TechnicalOrderParams IPSecS2S Data: " + e.getMessage(), this.getClass());
        }
    }

}
