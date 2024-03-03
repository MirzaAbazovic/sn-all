/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.06.2007 13:37:26
 */
package de.augustakom.hurrican.service.cc.impl.command.leistung;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;


/**
 * Command-Klasse, um die VoIP-Daten auf einem Auftrag zu kuendigen.
 */
@CcTxRequired
@Component("de.augustakom.hurrican.service.cc.impl.command.leistung.CancelVoIPCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CancelVoIPCommand extends AbstractVoIPCommand {

    private static final Logger LOGGER = Logger.getLogger(CancelVoIPCommand.class);

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.AbstractServiceCommand#execute()
     */
    @Override
    public Object execute() throws Exception {
        try {
            cancelVoIP();
            return ServiceCommandResult.createCmdResult(
                    ServiceCommandResult.CHECK_STATUS_OK, null, getClass());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(e.getMessage(), e);
        }
    }

}


