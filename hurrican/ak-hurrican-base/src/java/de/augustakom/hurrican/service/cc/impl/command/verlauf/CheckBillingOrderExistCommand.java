/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.03.2007 16:39:01
 */
package de.augustakom.hurrican.service.cc.impl.command.verlauf;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.service.base.ServiceCommandResult;


/**
 * Ueberprueft, ob der Billing-Auftrag vorhanden ist.
 *
 *
 */
@Component("de.augustakom.hurrican.service.cc.impl.command.verlauf.CheckBillingOrderExistCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CheckBillingOrderExistCommand extends AbstractVerlaufCheckCommand {

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.AbstractServiceCommand#execute()
     */
    public Object execute() throws Exception {
        if (getBillingAuftrag() == null) {
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    "Die Taifun-Auftragsdaten wurden nicht uebergeben. Auftragsstatus kann nicht geprueft werden.",
                    getClass());
        }

        return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_OK, null, getClass());
    }

}


