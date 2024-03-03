/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.01.2006 11:36:09
 */
package de.augustakom.hurrican.dao.billing;

import java.util.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.hurrican.model.billing.RInfo;
import de.augustakom.hurrican.model.billing.view.RInfo2KundeView;
import de.augustakom.hurrican.model.shared.view.RInfoAdresseView;
import de.augustakom.hurrican.model.shared.view.RInfoQuery;


/**
 * DAO-Interface fuer die Abfrage von RInfo-Daten.
 *
 *
 */
public interface RInfoDAO extends FindAllDAO, FindDAO, ByExampleDAO {

    /**
     * Ermittelt RInfo-Kunden Views fuer einen bestimmten Abrechnungsmonat.
     *
     * @param billRunId Id des Bill-Runs
     * @param year      Rechnungsjahr
     * @param month     Rechnungsmonat
     * @return Liste mit Objekten des Typs <code>RInfo2KundeView</code>
     *
     */
    public List<RInfo2KundeView> findRInfo2KundeViews(Long billRunId, String year, String month);

    /**
     * Ermittelt eine RInfo-Kunden View zu einer bestimmten Rechnungsnummer.
     *
     * @param billId        Rechnungsnummer
     * @param billingStream Name des Billing-Streams (z.B. 'RTB' oder 'MLB')
     * @param year          Rechnungsjahr
     * @param month         Rechnungsmonat
     * @return Objekt vom Typ <code>RInfo2KundeView</code>
     *
     */
    public RInfo2KundeView findRInfo2KundeView4BillId(String billId, String year, String month);

    /**
     * Ermittelt das Rechnungsdatum fuer einen bestimmten Abrechnungszeitraum.
     *
     * @param billRunId Id des gesuchten BillRuns
     * @return Rechnungsdatum fuer den definierten Abrechnungszeitraum
     *
     */
    public Date findInvoiceDate(Long billRunId);

    /**
     * Liefert eine Liste aller RInfos zu einer internen Kundennummer
     *
     * @param kundeNo interne Kundennummer
     * @return
     *
     */
    public List<RInfo> findRInfo4KundeNo(Long kundeNo);

    /**
     * Liefert Kundendaten anhand der RInfoQuery
     *
     * @param query
     * @return
     *
     */
    public List<RInfoAdresseView> findKundenByRInfoQuery(RInfoQuery query);

}


