/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.12.2004 10:57:00
 */
package de.augustakom.hurrican.service.cc.impl.command;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;
import de.augustakom.hurrican.model.cc.VerlaufStatus;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.BAService;


/**
 * Command-Klasse, um eine Projektierung fuer einen Auftrag anzulegen.
 *
 *
 */
public class CreateProjektierungCommand extends BaseCreateProjektierungCommand {

    private static final Logger LOGGER = Logger.getLogger(CreateProjektierungCommand.class);

    @Override
    protected void createProjektierung() throws StoreException {
        try {
            BAService bas = getCCService(BAService.class);
            Verlauf verlauf = createNewVerlauf(VerlaufStatus.BEI_DISPO);

            // Verlauf an die Abteilungen verteilen
            List<Long> abtIds = new ArrayList<Long>();
            abtIds.add(Abteilung.AM);
            abtIds.add((produkt.getVerteilungDurch() != null) ? produkt.getVerteilungDurch() : Abteilung.DISPO);
            vpnCheck(auftragTechnik, abtIds);
            List<VerlaufAbteilung> vas = bas.baErstellen(verlauf.getId(), abtIds, null, sessionId);

            syncWithAuftragData(verlauf, vas);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Beim Anlegen der Projektierung ist ein nicht erwarteter Fehler aufgetreten! Bitte prüfen.", e);
        }
    }


}


