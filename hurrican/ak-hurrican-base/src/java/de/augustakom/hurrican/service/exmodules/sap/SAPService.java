/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.08.2007 14:10:57
 */
package de.augustakom.hurrican.service.exmodules.sap;

import java.util.*;

import de.augustakom.hurrican.model.exmodules.sap.SAPBankverbindung;
import de.augustakom.hurrican.model.exmodules.sap.SAPBuchungssatz;
import de.augustakom.hurrican.model.exmodules.sap.SAPSaldo;
import de.augustakom.hurrican.service.base.exceptions.FindException;


/**
 * Interface fuer die Datenzugriff auf SAP
 *
 *
 */
public interface SAPService extends ISAPService {

    /**
     * Funktion liefert alle ausgeglichenen Posten zu einem Debitor
     *
     * @param debNo Debitorennummer
     * @return Liste mit SAP-Buchungssaetzen
     * @throws FindException
     *
     */
    public List<SAPBuchungssatz> findAusgeglichenePosten(String debNo) throws FindException;

    /**
     * Funktion liefert alle offenen Posten zu einem Debitor
     *
     * @param debNo Debitorennummer
     * @return Liste mit SAP-Buchungssaetzen
     * @throws FindException
     *
     */
    public List<SAPBuchungssatz> findOffenePosten(String debNo) throws FindException;

    /**
     * Funktion liefert alle Buchungsdatensaetze für einen Debitor
     *
     * @param debNo Debitorennummer
     * @return Liste mit SAP-Buchungssätzen
     * @throws FindException
     *
     */
    public List<SAPBuchungssatz> findBuchungssaetze(String debNo) throws FindException;

    /**
     * Funktion liefert zu einem Debitor die entsprechende Mahnstufe
     *
     * @param debNo Debitorennummer
     * @return Manhstufe des Debitors als Integer-Wert
     * @throws FindException
     *
     */
    public Integer findMahnstufe(String debNo) throws FindException;

    /**
     * Funktion liefer die SAP-Bankverbindung zu einem best. Debitor
     *
     * @param debNo Debitorennummer
     * @return SAPBankverbindung-Objekt
     * @throws FindException
     *
     */
    public SAPBankverbindung findBankverbindung(String debNo) throws FindException;

    /**
     * Funktion liefert den Saldo für einen best. Debitor
     *
     * @param debNo Debitorennummer
     * @return SAPSaldo-Objekt
     * @throws FindException
     *
     */
    public SAPSaldo findAktSaldo(String debNo) throws FindException;
}
