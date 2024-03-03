/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.02.2007 12:16:17
 */
package de.augustakom.hurrican.service.reporting;

import java.util.*;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.reporting.Printer;
import de.augustakom.hurrican.model.reporting.Report;
import de.augustakom.hurrican.model.reporting.ReportData;
import de.augustakom.hurrican.model.reporting.ReportPaperFormat;
import de.augustakom.hurrican.model.reporting.ReportReason;
import de.augustakom.hurrican.model.reporting.ReportRequest;
import de.augustakom.hurrican.model.reporting.ReportTemplate;
import de.augustakom.hurrican.model.reporting.TxtBaustein;
import de.augustakom.hurrican.model.reporting.TxtBausteinGruppe;
import de.augustakom.hurrican.model.reporting.view.ReportRequestView;
import de.augustakom.hurrican.service.base.exceptions.DeleteException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.ReportException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;

/**
 * Interface für Report-Service. Dieser Service beinhaltet überwiegend Funktionen für die Erzeugung von Reports.
 *
 *
 */

public interface ReportService extends IReportService {

    public static final String MSG_KEY_REQUEST_ID = "requestId";
    public static final String MSG_KEY_SESSION_ID = "sessionId";
    public static final String MSG_KEY_STATUS = "status";
    public static final String MSG_KEY_ERROR_TEXT = "error.text";
    public static final String MSG_KEY_RECEIVER = "receiver";
    public static final Integer MSG_STATUS_OK = 1;
    public static final Integer MSG_STATUS_ERROR = 0;

    public static final String REPORT_ARCHIV = "Archiv";
    public static final String REPORT_TMP_PATH = System.getProperty("java.io.tmpdir");


    /**
     * Funktion speichert ein ReportData-Objekt
     *
     * @param toSave zu speicherndes Objekt
     * @throws StoreException
     */
    public void saveReportData(ReportData toSave) throws StoreException;

    /**
     * Funktion speichert ein ReportRequest-Objekt
     *
     * @param toSave zu speicherndes Objekt
     * @throws StoreException
     */
    public void saveReportRequest(ReportRequest toSave) throws StoreException;

    /**
     * Funktion liefert ein Report-Objekt anhand der Id
     *
     * @param Id des gesuchten Objekts
     * @return Report-Objekt
     * @throws FindException
     */
    public Report findReport(Long Id) throws FindException;

    /**
     * Liefert ein ReportRequest anhand der Id
     *
     * @param id des gesuchten Objekts
     * @return ReportRequest-Objekt
     * @throws FindException
     */
    public ReportRequest findReportRequest(Long id) throws FindException;

    /**
     * Funktion liefert ein Papierformat anhand der id
     *
     * @param id des gesuchten Objekts
     * @return ReportPaperFormat-Objekt
     * @throws FindException
     */
    public ReportPaperFormat findReportPaperFormat(Long id) throws FindException;

    /**
     * Funktion liefert die Namen der Papierschächte eines best. Druckers für einen Report
     *
     * @param reportId    Id des Reports
     * @param printerName Druckername
     * @return Array mit TrayNamen für Seite1 und Seite2
     * @throws FindException
     */
    public String[] findTrayName4ReportPrinter(Long reportId, String printerName) throws FindException;

    /**
     * Funktion liefert alle Papierformate
     *
     * @return Liste mit allen ReportPaperFormat-Objekten
     * @throws FindException
     */
    public List<ReportPaperFormat> findAllReportPaperFormat() throws FindException;

    /**
     * Funktion liefert ein Report-Template anhand der Id
     *
     * @param id des gesuchten Objekts
     * @return ReportTemplate
     * @throws FindException
     */
    public ReportTemplate findReportTemplateById(Long id) throws FindException;

    /**
     * Funktion liefert das aktuelle Report-Template für einen Report.
     *
     * @param reportid ID des Reports
     * @return zum Report gehörendes, aktuelles Template
     * @throws FindException
     */
    public ReportTemplate findReportTemplate4Report(Long reportId) throws FindException;

    /**
     * Funktion erzeugt einen PDF-Report anhand eines ReportRequests.
     *
     * @param requestId            Id des ReportRequests
     * @param OpenOfficeConnection Verbindung zu OpenOffice
     * @throws ReportException
     */
    public void generateReport(Long requestId, OpenOfficeConnection connection) throws ReportException;

    /**
     * Funktion konvertiert ein Dokument mit Hilfe von OpenOffice
     *
     * @param file                 Dateiname der zu konvertierenden Datei
     * @param format               Format der Zieldatei (z.B. "pdf" oder "doc")
     * @param OpenOfficeConnection Verbindung zu OpenOffice
     * @return Dateiname der erzeugten Datei, null falls Fehler
     * @throws ReportException
     */
    public String convertDocument(String file, String format, OpenOfficeConnection connection) throws ReportException;

    /**
     * Funktion stößt die Report-Erstellung an, indem ein ReportRequest erstellt wird und dem Report-Server eine
     * Nachricht zur Report-Erstellung übergeben wird.
     *
     * @param reportId    Id des Reports
     * @param sessionId   SessionId des akt. Benutzers
     * @param kundeNoOrig Kundennummer fuer welchen Kunden der Report erstellt werden soll
     * @param orderNoOrig Auftragsnummer (Taifun) fuer die der Report erstellt werden soll
     * @param auftragId   Auftragsnummer (Hurrican) fuer die der Report erstellt werden soll
     * @param buendelNo   Buendelnummer um mehrere Reports zu einem Serienbrief zu buendeln
     * @param reasonId    Grund eines wiederholten Drucks
     * @param sendMsg     Falls true wird die Reporterstellung sofort gestartet
     * @return ID des erstellten Report-Requests
     * @throws ReportException
     */
    public Long createReportRequest(Long reportId, Long sessionId, Long kundeNoOrig, Long orderNoOrig, Long auftragId, Integer buendelNo, Long reasonId, Boolean sendMsg) throws ReportException;

    /**
     * Funktion liefert alle Reports, die einem Produkt in einem best. Status zugeordnet sind
     *
     * @param repType      Typ des Reports (Auftrags- oder Kundenreport)
     * @param auftragDaten Daten des Auftrags
     * @param sessionId    Id der aktuellen Session
     * @return
     * @throws FindException
     */
    public List<Report> findAvailableReports(Long repType, AuftragDaten aufragDaten, Long sessionId) throws FindException;

    /**
     * Funktion lädt eine Report auf den Client herunter.
     *
     * @param requestId Id des ReportRequests
     * @param tmpPath   temporaerer Pfad zum speichern des Reports
     * @return Pfad des Reports auf dem Client
     * @throws FindException
     */
    public String downloadReport(Long requestId, String tmpPath) throws FindException;

    /**
     * Findet alle ReportRequests zu einem bestimmten Auftrag oder Kunden
     *
     * @param kundeNoOrig  Kundennummer
     * @param auftragId    AuftragsId (Hurrican)
     * @param filterBeginn Filterparameter
     * @param filterEnde   Filterparameter
     * @return Liste von Views mit allen gefundenen Reports
     * @throws FindException
     */
    public List<ReportRequestView> findReportRequest4Auftrag(Long kundeNoOrig, Long auftragId, Date filterBeginn, Date filterEnde) throws FindException;

    /**
     * Findet alle Text-Bausteine zu einer Baustein-Gruppe
     *
     * @param gruppeId Id der Textbausteingruppe
     * @return Liste aller gefundenen Textbausteine
     * @throws FindException
     */
    public List<TxtBaustein> findTxtBausteine4Gruppe(Long gruppeId) throws FindException;

    /**
     * Liefert den Inhalt aller ReportData-Datensatz anhand der RequestId und des Keys.
     *
     * @param requestId ID des ReportRequests
     * @param key       Key der gesuchten Report-Daten
     * @return Liste mit allen gesuchten Daten
     * @throws FindException
     */
    public List<String> findReportDataByKey(Long requestId, String key) throws FindException;

    /**
     * Schickt eine Report-Anfrage an den JMS um den Reportserver zu starten
     *
     * @param request   ID des ReportRequests
     * @param sessionId ID der aktuellen Session
     * @throws ReportException
     */
    public void sendReportRequest(ReportRequest request, Long sessionId) throws ReportException;

    /**
     * Funktion löscht alle ReportData-Datensätze deren Report-Erstellung mehr als days-Tage zurückliegt
     *
     * @param days
     * @throws DeleteException
     */
    public void deleteOldReportData(Integer days) throws DeleteException;

    /**
     * Funktion löscht alle Reports, die älter als days-Tage sind. Die Reports können auch archiviert werden, indem sie
     * in ein anderes Verzeichnis kopiert werden.
     *
     * @param days
     * @param archive Falls true werden die Reports vor Löschung in einem anderen Verzeichnis archiviert
     * @throws ReportException
     */
    public void deleteOldReports(Integer days, Boolean archive) throws ReportException;

    /**
     * Funktion liefert eine Liste mit Text-Baustein-Gruppen, die einem best. Report zugeordnet sind.
     *
     * @param reportId ID des Reports
     * @return Liste aller dem Report zugeordneten Textbausteingruppen
     * @throws FindException
     */
    public List<TxtBausteinGruppe> findAllTxtBausteinGruppen4Report(Long reportId) throws FindException;

    /**
     * Funktion liefert neue Bündelnummer, d.h. max(buendelNo) + 1
     *
     * @return neue Buendelnummer
     * @throws FindException
     */
    public Integer findNewReportBuendelNo() throws FindException;

    /**
     * Funktion liefert alle requests zu einer Bündelnummer
     *
     * @param buendelNo
     * @return Liste aller gesuchten ReportRequests
     * @throws FindException
     */
    public List<ReportRequest> findAllRequests4BuendelNo(Integer buendelNo) throws FindException;

    /**
     * Funktion fasst alle PDFs der übergebenen Requests zu einem File zusammen
     *
     * @param requests        Alle Reports, die gebündelt werden sollen
     * @param tmpPath         temporärer Pfad, in dem alle einzelnen PDFs und das Gesamt-PDF abgelegt wird
     * @param serienbriefFile Name des zusammengefassten PDF-Files
     * @return Dateinamen aller einzelnen PDFs
     * @throws ReportException
     */
    public List<String> getPDFBundled(List<ReportRequest> requests, String tmpPath, String serienbriefFile) throws ReportException;

    /**
     * Funktion fasst alle PDFs einer BündelNo zusammen
     *
     * @param buendelNo       aller Requests, die zusammengefasst werden sollen
     * @param tmpPath         temporaerer Pfad zur Ablage der PDFs
     * @param serienbriefFile Name des zusammengefassten PDf-Files
     * @return Dateinamen aller einzelnen PDFs
     * @throws ReportException
     */
    public List<String> getPDF4BuendelNo(Integer buendelNo, String tmpPath, String serienbriefFile) throws ReportException;

    /**
     * Funktion prüft, ob für einen Serienbrief alle Einzel-Reports erstellt wurden.
     *
     * @param buendelNo des Serienbriefs
     * @return True, falls bereits alle Reports erzeugt wurden
     * @throws FindException
     */
    public Boolean testSerienbriefReady(Integer buendelNo) throws FindException;

    /**
     * Funktion liefert alle Serienbriefe
     *
     * @return View aller Serienbriefe
     * @throws FindException
     */
    public List<ReportRequestView> findAllSerienbriefe() throws FindException;

    /**
     * Funktion liefert alle ReportData-Datensätze, die einem Request zugeorndet sind.
     *
     * @param requestId ID des ReportRequests
     * @return Liste aller zum Request gehoerenden ReportData-Objekten
     * @throws FindException
     */
    public List<ReportData> findAllReportDataByRequestId(Long requestId) throws FindException;

    /**
     * Funktion testet, ob von derselben Reportgruppe bereits ein Report für den Auftrag/Kunden erstellt wurde.
     *
     * @param rep         Report-Objekt
     * @param kundeNoOrig Kundennummer fuer die ein Report gedruckt werden soll
     * @param ad          Auftragdaten des Auftrags fuer den ein Report gedruckt werden soll
     * @return TRUE falls von derselben Reportgruppe bereits ein Report gedruckt wurde
     * @throws FindException
     */
    public Boolean testReportReprint(Report rep, Long kundeNoOrig, AuftragDaten ad) throws FindException;

    /**
     * Liefert alle ReportRequests einer bestimmten Reportgruppe
     *
     * @param rep         Report-Objekt
     * @param kundeNoOrig Kundennummer fuer die ein Report gedruckt werden soll
     * @param ad          Auftragdaten des Auftrags fuer den ein Report gedruckt werden soll
     * @return Liste aller ReportRequests derselben Reportgruppe
     * @throws FindException
     */
    public List<ReportRequest> findReportRequests4ReportGruppe(Report rep, Long kundeNoOrig, Long auftragId) throws FindException;

    /**
     * Funktion liefert alle Report-Reason-Objekte, eventl. eingeschränkt nach Reasons, die nach der Archivierung des
     * vorhergehenden Reports nicht mehr relevant sind
     *
     * @param reprintBeforeArchive
     * @return Liste aller ReportReason-Objekte oder falls reportBeforeArchive = False nur alle Gründe, die nach der
     * Archivierung noch relevant sind
     * @throws FindException
     */
    public List<ReportReason> findReportReasons(Boolean reprintBeforeArchive) throws FindException;

    /**
     * Funktionen erzeugt aus der flachen Struktur, wie die Report-Daten in der DB gespeichert sind, wieder die
     * Original-Struktur mit Listen und HashMaps. Gegenstück zur Funktion prepareMap.
     *
     * @param map HashMap in flacher Struktur
     * @return Aufbereitete HashMap zur Übergabe an JOOReport-API
     */
    public Map<String, Object> createMaps(Map<String, Object> map);

    /**
     * Funktion testet, ob vom ReportServer ein File erzeugt wurde und dieses lesbar ist.
     *
     * @param request zu testender Report-Request
     * @return True falls Report vorhanden und lesbar
     * @throws ReportException
     */
    public Boolean testReportReadable(ReportRequest request) throws ReportException;

    /**
     * Speichert einen zusätzlichen Parameter für die Report-Erstellung in der Tabelle t_report_data
     *
     * @param requestId      Id des ReportRequests
     * @param parameterKey   Key des Parameters
     * @param parameterValue Parameterwert
     * @throws StoreException
     */
    public void saveReportParameter(Long requestId, String parameterKey, String parameterValue) throws StoreException;

    /**
     * Liefert einen zusätzlichen Parammeter für die Report-Erstellung, falls vorhanden.
     *
     * @param requestId    Id des ReportRequests
     * @param parameterKey Key des Parameters
     * @return Parameterwert, falls vorhanden sonst null
     * @throws FindException
     */
    public String findReportParameter(Long requestId, String parameterKey) throws FindException;

    /**
     * Liefert ein Printer-Objekt anhand des Druckernamens
     *
     * @param name Druckername
     * @return Printer-Objekt
     * @throws FindException
     */
    public Printer findPrinterByName(String name) throws FindException;

    /**
     * Prueft ob fuer den gegebenen Auftrag der Report mit der reportId moeglich ist
     *
     * @throws FindException
     * @throws ServiceNotFoundException
     */
    public boolean isReportForAuftragAvailable(Long reportId, Long auftragId, Long sessionId) throws FindException, ServiceNotFoundException;
}

