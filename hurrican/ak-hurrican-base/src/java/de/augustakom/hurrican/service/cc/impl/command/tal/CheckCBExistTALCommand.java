/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.07.2007 16:08:37
 */
package de.augustakom.hurrican.service.cc.impl.command.tal;

import org.apache.log4j.Logger;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.model.cc.Carrierbestellung;


/**
 * Command prueft, ob die Carrierbestellung zu einem el. TAL-Bestellungsvorgang ermittelt werden kann.
 *
 *
 */
public class CheckCBExistTALCommand extends AbstractTALCommand {

    private static final Logger LOGGER = Logger.getLogger(CheckCBExistTALCommand.class);

    /**
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanServiceCommand#execute()
     */
    public Object execute() throws Exception {
        try {
            Carrierbestellung cb = getCarrierbestellung();
            if (cb == null) {
                return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                        "Zugehoerige Carrierbestellung konnte nicht ermittelt werden!", getClass());
            }

            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_OK, null, getClass());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    "Fehler bei der Ueberpruefung der Carrierbestellungsdaten: " + e.getMessage(), getClass());
        }
    }

}


