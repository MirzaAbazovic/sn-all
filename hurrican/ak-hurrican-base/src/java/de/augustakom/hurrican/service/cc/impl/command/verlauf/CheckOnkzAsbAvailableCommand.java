/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.11.13 13:54
 */
package de.augustakom.hurrican.service.cc.impl.command.verlauf;

import javax.annotation.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.common.tools.lang.Pair;

@Component("de.augustakom.hurrican.service.cc.impl.command.verlauf.CheckOnkzAsbAvailableCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CheckOnkzAsbAvailableCommand extends AbstractVerlaufCheckCommand {
    private static final Logger LOGGER = Logger.getLogger(CheckOnkzAsbAvailableCommand.class);

    @Resource(name = "de.augustakom.hurrican.service.cc.CCAuftragService")
    protected CCAuftragService auftragService;

    @Override
    public Object execute() throws Exception {
        try {
            Pair<String, Integer> checkThis = auftragService.findOnkzAsb4Auftrag(getAuftragId());
            if (checkThis != null) {
                if (checkThis.getFirst() == null && checkThis.getSecond() == null) {
                    return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                            "ONKZ und ASB sind nicht gesetzt!", getClass());
                }
                else if (checkThis.getFirst() == null) {
                    return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                            "ONKZ ist nicht gesetzt!", getClass());
                }
                else if (checkThis.getSecond() == null) {
                    return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                            "ASB ist nicht gesetzt!", getClass());
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    e.getMessage(), getClass());
        }

        return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_OK, null, getClass());
    }
}
