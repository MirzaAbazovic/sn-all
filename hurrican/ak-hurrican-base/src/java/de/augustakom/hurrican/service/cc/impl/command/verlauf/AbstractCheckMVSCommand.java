/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.03.2007 09:13:46
 */
package de.augustakom.hurrican.service.cc.impl.command.verlauf;

import org.apache.log4j.Logger;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.model.cc.AuftragMVS;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.MVSService;

/**
 * Abstrakte Command-Klasse prueft, ob MVS Auftrag vorhanden ist. Muss hier nicht validiert werden, da dies beim
 * Speichern passiert.
 */
public abstract class AbstractCheckMVSCommand extends AbstractVerlaufCheckCommand {

    private static final Logger LOGGER = Logger.getLogger(AbstractCheckMVSCommand.class);

    @Override
    public Object execute() throws Exception {
        try {
            MVSService mvsService = getCCService(MVSService.class);
            AuftragMVS auftragMVS = findAuftragMVS(mvsService);

            if (auftragMVS == null) {
                return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                        "Es wurden keine MVS Daten fuer den Auftrag gefunden!", getClass());
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    "Bei der Ueberpruefung des MVS Auftrags ist ein nicht erwarteter Fehler aufgetreten: " +
                            e.getMessage(), getClass()
            );
        }

        return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_OK, null, getClass());
    }

    protected abstract AuftragMVS findAuftragMVS(MVSService mvsService) throws FindException;

}
