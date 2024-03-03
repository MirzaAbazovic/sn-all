/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.08.2010
 */
package de.augustakom.hurrican.service.cc;

import de.augustakom.hurrican.model.cc.view.FTTXKundendatenView;
import de.augustakom.hurrican.service.base.exceptions.FindException;

/**
 * Service, um Informationen an Command zu uebergeben.
 */
public interface FTTXInfoService extends ICCService {

    enum FttxPortStatus {
        unbekannt,
        belegt,
        frei,
        reserviert,
        defekt,
        freigabebereit,
        gesperrt;
    }

    /**
     * Funktion liefert Kundendaten für Command. Referenziert wird der Auftrag über den Wohnungs-Stift oder die ONT-ID
     *
     * @throws FindException
     */
    void getKundendaten4Command(FTTXKundendatenView view) throws FindException;

}
