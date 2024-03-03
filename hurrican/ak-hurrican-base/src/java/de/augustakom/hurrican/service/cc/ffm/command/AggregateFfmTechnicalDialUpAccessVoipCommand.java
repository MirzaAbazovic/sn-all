/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.06.2015
 */
package de.augustakom.hurrican.service.cc.ffm.command;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.model.builder.cdm.workforceservice.v1.DialUpAccessVoIPBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.service.cc.impl.helper.VoIpDataHelper;

/**
 *
 */
@Component("de.augustakom.hurrican.service.cc.ffm.command.AggregateFfmTechnicalDialUpAccessVoipCommand")
@Scope("prototype")
public class AggregateFfmTechnicalDialUpAccessVoipCommand extends AbstractFfmCommand {

    private static final Logger LOGGER = Logger.getLogger(AggregateFfmTechnicalDialUpAccessCommand.class);

    @Override
    public Object execute() throws Exception {
        try {
            checkThatWorkforceOrderHasTechnicalParams();
            AuftragDaten auftragDaten = getAuftragDaten();
            getWorkforceOrder().getDescription().getTechParams().setDialUpAccessVoIP(
                    new DialUpAccessVoIPBuilder()
                            .withAccount(VoIpDataHelper.getPppoeUser(auftragDaten))
                            .withPassword(VoIpDataHelper.PPPOE_PW)
                            .build()
            );

            return ServiceCommandResult.createCmdResult(
                    ServiceCommandResult.CHECK_STATUS_OK, null, this.getClass());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    "Error loading FFM TechnicalOrderParams DialUpAccess Data: " + e.getMessage(), this.getClass());
        }
    }

}
