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
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.billing.BAuftrag;


/**
 * Ueberprueft, ob der Billing-Auftrag aktiviert ist.
 *
 *
 */
@Component("de.augustakom.hurrican.service.cc.impl.command.verlauf.CheckBillingOrderActivatedCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CheckBillingOrderActivatedCommand extends AbstractVerlaufCheckCommand {

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.AbstractServiceCommand#execute()
     */
    public Object execute() throws Exception {
        if (getBillingAuftrag() == null) {
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    "Die Taifun-Auftragsdaten wurden nicht uebergeben. Auftragsstatus kann nicht geprueft werden.",
                    getClass());
        }

        Integer astatus = getBillingAuftrag().getAstatus();
        if (!NumberTools.equal(astatus, BAuftrag.STATUS_FREIGEGEBEN) &&
                !NumberTools.equal(astatus, BAuftrag.STATUS_GEKUENDIGT) &&
                !NumberTools.equal(astatus, BAuftrag.STATUS_FREIGABEBEREIT)) {
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    "Der Taifun-Auftrag ist noch nicht abgeschlossen.", getClass());
        }

        return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_OK, null, getClass());
    }

}


