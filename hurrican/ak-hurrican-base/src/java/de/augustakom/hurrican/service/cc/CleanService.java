/*
 * Copyright (c) 2007 - M-net Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.01.2007 13:58:17
 */
package de.augustakom.hurrican.service.cc;

import de.augustakom.common.tools.messages.AKWarnings;


/**
 * Service fuer die Datenpflege und das saeubern der Datenbestaende
 *
 *
 */
public interface CleanService extends ICCService {

    /**
     * Ueberprueft die Tabelle t_leistung_dn auf nicht mehr gueltige Rufnummernleistungen
     */
    public AKWarnings cleanLeistungDn();

}


