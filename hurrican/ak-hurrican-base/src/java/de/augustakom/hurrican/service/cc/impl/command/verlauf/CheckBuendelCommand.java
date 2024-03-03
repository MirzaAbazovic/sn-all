/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.03.2007 09:12:49
 */
package de.augustakom.hurrican.service.cc.impl.command.verlauf;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.service.base.ServiceCommandResult;


/**
 * Command-Klasse prueft, ob ein Auftrag, dessen Produkt als 'braucht Buendel' definiert ist, auch wirklich eine
 * Buendelnummer besitzt.
 *
 *
 */
@Component("de.augustakom.hurrican.service.cc.impl.command.verlauf.CheckBuendelCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CheckBuendelCommand extends AbstractVerlaufCheckCommand {

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.AbstractServiceCommand#execute()
     */
    public Object execute() throws Exception {
        if (getAuftragDatenTx(getAuftragId()).getBuendelNr() == null) {
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    "Dem Auftrag ist keine Buendel-Nr zugeordnet!", getClass());
        }

        return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_OK, null, getClass());
    }

}


