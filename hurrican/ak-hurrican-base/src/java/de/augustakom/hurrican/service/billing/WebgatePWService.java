/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.02.2005 11:58:54
 */
package de.augustakom.hurrican.service.billing;

import de.augustakom.hurrican.model.billing.WebgatePW;
import de.augustakom.hurrican.service.base.exceptions.FindException;


/**
 * Service fuer die Suche nach e-Rechnungs-Daten aus dem Billing-System.
 *
 *
 */
public interface WebgatePWService extends IBillingService {

    /**
     * Liefert das erste WebgatePW-Objekt für einen Kunden
     *
     * @param kundeNo Kundennummer
     * @return Objekt des Typs <code>WebgatePW</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public WebgatePW findFirstWebgatePW4Kunde(Long kundeNo) throws FindException;

    /**
     * Sucht nach eRechnungsdaten fuer RechnungsinfoNo
     *
     * @param rInfoNo RechnungInfo__NO aus Mistral
     * @return Liste mit Objekten des Typs <code>WebgatePW</code>.
     * @throws FindException
     */
    public WebgatePW findWebgatePW4RInfo(Long rInfoNo) throws FindException;

}


