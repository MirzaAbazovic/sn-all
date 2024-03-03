/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.07.2007 11:45:31
 */
package de.augustakom.hurrican.service.cc.impl.command.tal;

import org.apache.log4j.Logger;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Endstelle;


/**
 * Command-Klasse um zu pruefen, ob fuer die TAL-Bestellung eine Standortadresse vorhanden ist. <br> Wird keine
 * Standortadresse gefunden, wird ein negatives Result generiert.
 */
public class CheckAPAddressTALCommand extends AbstractTALCheckCommand {

    private static final Logger LOGGER = Logger.getLogger(CheckAPAddressTALCommand.class);

    @Override
    public Object execute() throws Exception {
        try {
            Carrierbestellung cb = getCarrierbestellung();
            Long auftragId = (Long) getPreparedValue(KEY_AUFTRAG_ID);
            Endstelle es = getEndstelle(auftragId, cb.getCb2EsId());
            if (es == null) {
                return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                        "Die Endstelle konnte nicht ermittelt werden!", getClass());
            }

            if (getAPAddress(es, auftragId) == null) {
                return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                        "Die Standortadresse konnte nicht ermittelt werden!", getClass());
            }

            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_OK, null, getClass());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    "Fehler bei der Ueberpruefung der Standortadresse: " + e.getMessage(), getClass());
        }
    }

}
