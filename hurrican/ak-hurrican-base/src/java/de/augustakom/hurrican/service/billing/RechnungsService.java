/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.01.2006 11:39:00
 */
package de.augustakom.hurrican.service.billing;

import java.util.*;
import net.sf.jasperreports.engine.JasperPrint;

import de.augustakom.common.tools.reports.AKReportException;
import de.augustakom.hurrican.model.billing.ArchPrintSet;
import de.augustakom.hurrican.model.billing.BLZ;
import de.augustakom.hurrican.model.billing.Finanz;
import de.augustakom.hurrican.model.billing.RInfo;
import de.augustakom.hurrican.model.billing.view.BillRunView;
import de.augustakom.hurrican.model.billing.view.RInfo2KundeView;
import de.augustakom.hurrican.model.shared.view.RInfoAdresseView;
import de.augustakom.hurrican.model.shared.view.RInfoQuery;
import de.augustakom.hurrican.service.base.exceptions.FindException;


/**
 * Service-Interface fuer Rechnungs-Operationen auf dem Billing-System.
 *
 *
 */
public interface RechnungsService extends IBillingService {

    /**
     * Ermittelt RInfo-Kunden Views fuer einen bestimmten Abrechnungsmonat.
     *
     * @param billRunId Id des Bill-Runs
     * @param year      Abrechnungsjahr
     * @param month     Abrechnungsmonat
     * @return Liste mit Objekten des Typs <code>RInfo2KundeView</code>
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt
     */
    public List<RInfo2KundeView> findRInfo2KundeViews(Long billRunId, String year, String month) throws FindException;

    /**
     * Ermittelt die SAP-Debitorennummer, unter der eine bestimmte Rechnung im ScanView-Archiv hinterlegt ist. <br> Bei
     * Kunden mit Reseller AugustaKom/AllgaeuKom ist dies die R_INFO__NO, bei Kunden mit Reseller M-net die
     * R_INFO.EXT_DEBITOR_ID
     *
     * @param billId Rechnungsnummer zu der die SAP-Nummer fuer das Archiv ermittelt werden soll.
     * @param year   Rechnungsjahr
     * @param month  Rechnungsmonat
     * @return Debitorennummer, unter der die Rechnung im Archiv hinterlegt ist.
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt
     *
     */
    public RInfo2KundeView findRInfo2KundeView4BillId(String billId, String year, String month) throws FindException;

    /**
     * Sucht nach einer bestimmten RInfo ueber die eindeutige ID.
     *
     * @param rinfoNo
     * @return RInfo-Objekt
     * @throws FindException wenn bei der Ermittlung der RInfo ein Fehler auftritt.
     */
    public RInfo findRInfo(Long rinfoNo) throws FindException;

    /**
     * Liefert eine bestimmte Finanz-Information
     *
     * @param finanzNo
     * @return Finanz-Objekt zu der Finanz-No
     * @throws FindException wenn bei der Ermittlung der Finanz ein Fehler auftritt
     *
     */
    public Finanz findFinanz(Long finanzNo) throws FindException;

    /**
     * Liefert zu einer Bankleitzahl den Namen der Bank.
     *
     * @param blz
     * @return BLZ-Objekt
     * @throws FindException wenn bei der Ermittlung der BLZ ein Fehler auftritt
     *
     */
    public BLZ findBLZ(Long blz) throws FindException;

    /**
     * Ermittelt das Rechnungsdatum fuer einen bestimmten Abrechnungszeitraum.
     *
     * @param billRunId Id des gesuchten BillRuns
     * @return Rechnungsdatum fuer den definierten Abrechnungszeitraum
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     *
     */
    public Date findInvoiceDate(Long billRunId) throws FindException;

    /**
     * Ermittelt eine Liste mit allen ArchPrintSets.
     *
     * @return Liste mit Objekten des Typs <code>ArchPrintSet</code>
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     *
     */
    public List<ArchPrintSet> findArchPrintSets() throws FindException;

    /**
     * Ermittelt die Anzahl Seiten und Rechnungen fuer eine bestimmte File-Gruppe (z.B. "%Normalversand.02%")
     *
     * @param printSetNo ID des zu verwendenden PrintSets
     * @param groupName  Name, ueber den die DB-Eintraege gruppiert werden koennen.
     * @return Array mit der Seiten- und Rechnungszahl fuer diese Gruppe. <br> Index 0: Seitenzahl <br> Index 1:
     * Rechnungszahl
     * @throws FindException
     *
     */
    public Integer[] sumPagesAndBills(Long printSetNo, String groupName) throws FindException;

    /**
     * Erstellt fuer den angegebenen Rechnungsmonat eine Uebersicht der zu druckenden Seiten- und Rechnungszahlen.
     *
     * @param printSetNo ID des PrintSets, dessen Details gedruckt werden sollen
     * @param billCycle  Name des betroffenen Bill-Cycles
     * @return JasperPrint-Objekt
     * @throws AKReportException wenn bei der Erstellung ein Fehler auftritt.
     *
     */
    public JasperPrint reportPrintStatistic(Long printSetNo, String billCycle) throws AKReportException;

    /**
     * Funktion ermittelt alle RInfos für einen Kunden
     *
     * @param kundeNo Kundennummer
     * @return Liste mit allen RInfos des Kunden
     * @throws FindException
     *
     */
    public List<RInfo> findRInfos4KundeNo(Long kundeNo) throws FindException;

    /**
     * Findet Kundendaten anhand von Rechnungsdaten fuer Suchmaske.
     *
     * @param query Query mit Rechnungsdaten
     * @return Liste mit Views
     * @throws FindException
     *
     */
    public List<RInfoAdresseView> findKundeByRInfoQuery(RInfoQuery query) throws FindException;

    /**
     * Ermittelt Daten zu den Rechnungslaeufen
     *
     * @return Liste mit BillRunView-Objekten
     * @throws FindException Falls ein Fehler auftritt
     *
     */
    public List<BillRunView> findBillRunViews() throws FindException;
}


