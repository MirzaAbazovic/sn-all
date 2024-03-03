/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.12.2004 10:57:00
 */
package de.augustakom.hurrican.service.cc.impl.command;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;
import de.augustakom.hurrican.model.cc.VerlaufStatus;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.BAService;


/**
 * Command-Klasse, um eine Projektierung fuer die zentrale Dispo anzulegen.
 *
 *
 */
public class CreateProjektierungForZentraleDispoCommand extends BaseCreateProjektierungCommand {

    private static final Logger LOGGER = Logger.getLogger(CreateProjektierungForZentraleDispoCommand.class);

    @Override
    protected void createProjektierung() throws StoreException {
        try {
            BAService baService = getCCService(BAService.class);
            Verlauf verlauf = createNewVerlauf(VerlaufStatus.BEI_ZENTRALER_DISPO);

            // Verlauf an die AM und die zentrale Dispo verteilen
            List<VerlaufAbteilung> vas = baService.createVerlaufAbteilungForZentraleDispo(verlauf.getId(), sessionId);

            syncWithAuftragData(verlauf, vas);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Beim Anlegen der Projektierung ist ein nicht erwarteter Fehler aufgetreten! Bitte prüfen.", e);
        }
    }

}


