/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.03.2007 09:13:46
 */
package de.augustakom.hurrican.service.cc.impl.command.verlauf;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.hurrican.model.billing.RInfo;
import de.augustakom.hurrican.service.billing.RechnungsService;


/**
 * Command-Klasse prueft, ob der Bill-Channel (Rechnungskanal) des Auftrags auf 'Maxi' gesetzt ist.
 *
 *
 */
@Component("de.augustakom.hurrican.service.cc.impl.command.verlauf.CheckBillChannelMaxiCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CheckBillChannelMaxiCommand extends AbstractVerlaufCheckCommand {

    private static final Logger LOGGER = Logger.getLogger(CheckBillChannelMaxiCommand.class);

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.AbstractServiceCommand#execute()
     */
    public Object execute() throws Exception {
        try {
            RechnungsService res = (RechnungsService) getBillingService(RechnungsService.class);
            RInfo rinfo = res.findRInfo(getBillingAuftrag().getRechInfoNoOrig());
            if (rinfo == null) {
                return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                        "Es wurde keine RInfo fuer den Auftrag gefunden!", getClass());
            }
            else if (!BooleanTools.nullToFalse(rinfo.getInvMaxi())) {
                return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                        "Der Rechnungskanal fuer den Auftrag ist ungueltig (INV_MAXI=0).", getClass());
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    "Bei der Ueberpruefung des Rechnungskanals ist ein nicht erwarteter Fehler aufgetreten: " +
                            e.getMessage(), getClass()
            );
        }

        return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_OK, null, getClass());
    }

}


