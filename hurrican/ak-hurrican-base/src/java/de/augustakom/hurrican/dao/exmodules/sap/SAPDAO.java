/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.08.2007 13:58:47
 */
package de.augustakom.hurrican.dao.exmodules.sap;

import java.util.*;

import de.augustakom.hurrican.model.exmodules.sap.SAPBankverbindung;
import de.augustakom.hurrican.model.exmodules.sap.SAPBuchungssatz;
import de.augustakom.hurrican.model.exmodules.sap.SAPSaldo;

/**
 * DAO-Interface fuer den Zugriff auf SAP.
 *
 *
 */
public interface SAPDAO extends SAPConnectionData {


    /**
     * Funktion liefert alle ausgeglichenen Posten zu einem bestimmten Debitor
     *
     * @param debNo
     * @return
     *
     */
    public List<SAPBuchungssatz> findAusgeglichenePosten(String debNo);

    /**
     * Funktion liefert alle offenen Posten zu einem Debitor
     *
     * @param debNo
     * @return
     *
     */
    public List<SAPBuchungssatz> findOffenePosten(String debNo);

    /**
     * Funktion liefert alle Buchungsdatensätze für einen Debitor
     *
     * @param debNo
     * @return
     *
     */
    public List<SAPBuchungssatz> findBuchungssaetze(String debNo);

    /**
     * Funktion liefert zu einem Debitor die entsprechende Mahnstufe
     *
     * @param debNo
     * @return
     *
     */
    public Integer findMahnstufe(String debNo);

    /**
     * Funktion liefer die SAP-Bankverbindung zu einem best. Debitor
     *
     * @param debNo
     * @return
     *
     */
    public SAPBankverbindung findBankverbindung(String debNo);

    /**
     * Funktion liefert den Saldo für einen best. Debitor
     *
     * @param debNo
     * @return
     *
     */
    public SAPSaldo findAktSaldo(String debNo);

}



