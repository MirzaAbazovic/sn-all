/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.02.2007 08:55:59
 */
package de.augustakom.hurrican.dao.reporting;

import java.util.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.reporting.Report;
import de.augustakom.hurrican.model.reporting.ReportRequest;
import de.augustakom.hurrican.model.reporting.view.ReportRequestView;

public interface ReportRequestDAO extends FindDAO, FindAllDAO, StoreDAO, ByExampleDAO {

    /**
     * Findet alle Report-Requests anhand der KundeNo und/oder AuftragId
     *
     * @param KundeNo      Kundennummer
     * @param auftragId    Auftragsnummer
     * @param filterBeginn Datum fuer Filterung
     * @param filterEnde   Datum fuer Filterung
     * @return Liste mit ReportRequestViews
     *
     */
    public List<ReportRequestView> findAllRequestsByKundeNoAndAuftragId(Long kundeNo, Long auftragId, Date filterBeginn, Date filterEnde);

    /**
     * Funktion findet alle ReportRequests, die aelter als days-Tage sind und das Feld files nicht leer ist. Funktion
     * dient dem Scheduler zum Loeschen der erzeugten Reports vom Filesystem.
     *
     * @param days
     * @return Liste mit ReportRequest-Objekten
     *
     */
    public List<ReportRequest> findReports2Delete(Integer days);

    /**
     * Funktion findet alle ReportRequests (nur die Id), die aelter als days-Tage sind. Die Funktion dient dem Scheduler
     * zum Loeschen der ReportData-Datensätze.
     *
     * @param days
     * @return Liste mit ReportRequest-IDs
     *
     */
    public List<Long> findReportData2Delete(Integer days);

    /**
     * Funktion ermittelt max(buendelNo) + 1
     *
     * @return Naechste freie Buendelnummer
     *
     */
    public Integer findNewBuendelNo();

    /**
     * Funktion liefert alle Reports einer Buendelnr., die noch nicht erstellt wurden, d.h. wenn Ergebniss leer ist,
     * wurde Serienbrief vollstaendig erstellt.
     *
     * @param buendelNo Zu pruefende Buendelnummer
     * @return Alle ReportRequest-Objekt, bei denen kein Report erzeugt wurde.
     *
     */
    public List<ReportRequest> findReportsNotReady4BuendelNo(Integer buendelNo);

    /**
     * Funktion liefert alle Serienbriefe
     *
     * @return Liste mit ReportRequestViews
     *
     */
    public List<ReportRequestView> findAllSerienbriefe();

    /**
     * Funktion ermittelt alle Reports, die bereits für eine bestimmte Reportgruppe erstellt wurden. Falls beim
     * uebergebenen Report keine Reportgruppe gesetzt ist wird anhand des Reports geprueft, ob dieser bereits gedruckt
     * wurde.
     *
     * @param rep       Report, fuer den die Report-Gruppe ermittelt und geprueft werden soll
     * @param kundeNo   Kundennummer
     * @param auftragId Auftragsnummer
     * @return Liste mit ReportRequests, der bereits gedruckten Reports.
     *
     */
    public List<ReportRequest> findAllRequests4Reportgruppe(Report rep, Long kundeNo, Long auftragId);

}
