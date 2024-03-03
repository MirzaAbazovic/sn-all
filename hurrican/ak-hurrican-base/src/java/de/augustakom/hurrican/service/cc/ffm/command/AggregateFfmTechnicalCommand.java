/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.09.14
 */
package de.augustakom.hurrican.service.cc.ffm.command;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.exceptions.FFMServiceException;
import de.mnet.esb.cdm.resource.workforceservice.v1.OrderTechnicalParams;
import de.mnet.esb.cdm.resource.workforceservice.v1.WorkforceOrder;

// @formatter:off
/**
 * Command-Klasse, um in eine {@link WorkforceOrder} ein leeres {@link OrderTechnicalParams} Objekt einzutragen.
 * (Notwendig, damit die anderen Aggregators eine Basis haben, um ihre Sub-Elemente einzutragen.
 */
// @formatter:on
@Component("de.augustakom.hurrican.service.cc.ffm.command.AggregateFfmTechnicalCommand")
@Scope("prototype")
public class AggregateFfmTechnicalCommand extends AbstractFfmCommand {

    private static final Logger LOGGER = Logger.getLogger(AggregateFfmTechnicalCommand.class);

    @Override
    public Object execute() throws Exception {
        try {
            if (getWorkforceOrder().getDescription() == null) {
                throw new FFMServiceException("Workforce order has no description to add the technical parameters!");
            }
            getWorkforceOrder().getDescription().setTechParams(new OrderTechnicalParams());

            return ServiceCommandResult.createCmdResult(
                    ServiceCommandResult.CHECK_STATUS_OK, null, this.getClass());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    "Error loading FFM TechnicalOrderParams Common Data: " + e.getMessage(), this.getClass());
        }
    }

}
