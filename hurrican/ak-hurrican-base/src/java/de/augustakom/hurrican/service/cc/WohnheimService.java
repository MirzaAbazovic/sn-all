/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.08.2004 09:22:22
 */
package de.augustakom.hurrican.service.cc;

import java.util.*;

import de.augustakom.hurrican.model.cc.Wohnheim;
import de.augustakom.hurrican.service.base.exceptions.FindException;


/**
 * Service-Interface fuer ein Wohnheim.
 *
 *
 */
public interface WohnheimService extends ICCService {

    /**
     * Sucht nach einem Wohnheim-Eintrag ueber die (original) Kundennummer.
     *
     * @param kNoOrig (original) Kundennummer des gesuchten Wohnheims.
     * @return Liste mit Objekten des Typs Wohnheim oder <code>null</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public List<Wohnheim> findByKundeNoOrig(Long kNoOrig) throws FindException;

    /**
     * Sucht nach einem Wohnheim ueber die VerbindungsBezeichnung (Leitungsnummer)
     *
     * @param verbindungsBezeichnung (VerbindungsBezeichnung) Leitungsnummer
     * @return das Wohnheim, dem die VerbindungsBezeichnung zugeordnet ist.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public Wohnheim findByVbz(String vbz) throws FindException;
}


