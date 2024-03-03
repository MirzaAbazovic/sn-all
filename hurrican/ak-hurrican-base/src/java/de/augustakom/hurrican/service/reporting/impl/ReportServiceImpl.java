/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.02.2007 12:16:41
 */
package de.augustakom.hurrican.service.reporting.impl;

import java.io.*;
import java.util.*;
import javax.annotation.*;
import javax.jms.*;
import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;
import com.lowagie.text.pdf.PdfCopyFields;
import com.lowagie.text.pdf.PdfReader;
import net.sf.jooreports.templates.DocumentTemplate;
import net.sf.jooreports.templates.ZippedDocumentTemplate;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import de.augustakom.authentication.model.AKApplication;
import de.augustakom.authentication.model.AKRole;
import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.AKApplicationService;
import de.augustakom.authentication.service.AKAuthenticationServiceNames;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.service.iface.IServiceCommand;
import de.augustakom.common.service.iface.IServiceLocator;
import de.augustakom.common.service.iface.IServiceLocatorNames;
import de.augustakom.common.service.locator.ServiceLocatorRegistry;
import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.file.FileTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.annotation.ReportingTxRequired;
import de.augustakom.hurrican.dao.reporting.PrinterDAO;
import de.augustakom.hurrican.dao.reporting.Report2TechLsDAO;
import de.augustakom.hurrican.dao.reporting.ReportDAO;
import de.augustakom.hurrican.dao.reporting.ReportDataDAO;
import de.augustakom.hurrican.dao.reporting.ReportPaperFormatDAO;
import de.augustakom.hurrican.dao.reporting.ReportReasonDAO;
import de.augustakom.hurrican.dao.reporting.ReportRequestDAO;
import de.augustakom.hurrican.dao.reporting.ReportTemplateDAO;
import de.augustakom.hurrican.dao.reporting.TxtBausteinDAO;
import de.augustakom.hurrican.dao.reporting.TxtBausteinGruppeDAO;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.command.ServiceCommand;
import de.augustakom.hurrican.model.reporting.Printer;
import de.augustakom.hurrican.model.reporting.Report;
import de.augustakom.hurrican.model.reporting.Report2UserRole;
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
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.ChainService;
import de.augustakom.hurrican.service.reporting.ReportConfigService;
import de.augustakom.hurrican.service.reporting.ReportService;
import de.augustakom.hurrican.service.reporting.impl.command.AbstractReportCommand;
import de.mnet.common.service.locator.ServiceLocator;

/**
 * Implementierung des Report-Service mit Funktionen für die Erzeugung von Reports.
 *
 *
 */
@ReportingTxRequired
public class ReportServiceImpl extends DefaultReportService implements ReportService {

    private static final Logger LOGGER = Logger.getLogger(ReportServiceImpl.class);

    private ReportDAO reportDAO;
    private ReportDataDAO reportDataDAO;
    private ReportRequestDAO reportRequestDAO;
    private ReportTemplateDAO reportTemplateDAO;
    private TxtBausteinDAO txtBausteinDAO;
    private TxtBausteinGruppeDAO txtBausteinGruppeDAO;
    private ReportPaperFormatDAO reportPaperFormatDAO;
    private Report2TechLsDAO report2TechLsDAO;
    private ReportReasonDAO reportReasonDAO;
    private PrinterDAO printerDAO;
    private JmsTemplate jmsTemplateToSendReportRequests;

    private String reportPathOutput;
    private String reportPathTemp;
    private String reportPathTemplates;

    @Resource(name = "de.augustakom.authentication.service.AKApplicationService")
    private AKApplicationService applicationService;

    @Autowired
    private ServiceLocator serviceLocator;

    @Override
    public void saveReportData(ReportData toSave) throws StoreException {
        if (toSave == null) {
            throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
        }
        try {
            reportDataDAO.store(toSave);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void saveReportRequest(ReportRequest toSave) throws StoreException {
        if (toSave == null) {
            throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
        }
        try {
            // Prüfe Daten
            Report rep = findReport(toSave.getRepId());
            if (rep == null) {
                throw new ReportException("Keine Report-Id zugeordnet!");
            }
            if ((rep.getType().longValue() == Report.REPORT_TYPE_KUNDE.longValue())
                    && (toSave.getKundeNo() == null)) {
                throw new ReportException("Ein Kundenreport muss eine Kundennummer enthalten!");
            }
            if ((rep.getType().longValue() == Report.REPORT_TYPE_AUFTRAG.longValue())
                    && ((toSave.getKundeNo() == null) || (toSave.getAuftragId() == null))) {
                throw new ReportException("Ein Auftragsreport muss Kunden- und Auftragsnummer enthalten!");
            }

            reportRequestDAO.store(toSave);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Report findReport(Long id) throws FindException {
        try {
            return reportDAO.findById(id, Report.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    /**
     * Funktion ermittelt alle Daten für einen bestimmten ReportRequest
     */
    private Map<String, Object> findReportData4ReportRequest(ReportRequest request) throws FindException {
        if (request == null) {
            return null;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            ChainService cs = (ChainService) getCCService(ChainService.class);
            List<ServiceCommand> cmds = cs.findServiceCommands4Reference(request.getRepId(), Report.class, null);

            for (ServiceCommand cmd : cmds) {
                IServiceCommand serviceCmd = serviceLocator.getCmdBean(cmd.getClassName());
                if (serviceCmd == null) {
                    throw new FindException("Fuer das definierte ReportCommand " + cmd.getName() +
                            " konnte kein Objekt geladen werden!");
                }
                serviceCmd.prepare(AbstractReportCommand.AUFTRAG_ID, request.getAuftragId());
                serviceCmd.prepare(AbstractReportCommand.REQUEST_ID, request.getId());
                serviceCmd.prepare(AbstractReportCommand.ORDER_NO_ORIG, request.getOrderNoOrig());
                serviceCmd.prepare(AbstractReportCommand.KUNDE_NO_ORIG, request.getKundeNo());
                serviceCmd.prepare(AbstractReportCommand.USER_LOGINNAME, request.getRequestFrom());
                Object result = serviceCmd.execute();

                if (result instanceof Map<?, ?>) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> mapResult = (Map<String, Object>) result;
                    map = addMap(map, mapResult);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
        return map;
    }

    @Override
    public ReportRequest findReportRequest(Long id) throws FindException {
        try {
            return reportRequestDAO.findById(id, ReportRequest.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public ReportPaperFormat findReportPaperFormat(Long id) throws FindException {
        if (id == null) {
            return null;
        }
        try {
            return reportPaperFormatDAO.findById(id, ReportPaperFormat.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public String[] findTrayName4ReportPrinter(Long reportId, String printerName) throws FindException {
        if ((reportId == null) || StringUtils.isEmpty(printerName)) {
            return null;
        }
        try {
            String[] result = new String[2];
            Report rep = reportDAO.findById(reportId, Report.class);
            if (rep != null) {
                result[0] = reportPaperFormatDAO.findTrayName4Report(printerName, rep.getFirstPage());
                result[1] = reportPaperFormatDAO.findTrayName4Report(printerName, rep.getSecondPage());
            }
            return result;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<ReportPaperFormat> findAllReportPaperFormat() throws FindException {
        try {
            return reportPaperFormatDAO.findAll(ReportPaperFormat.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    /**
     * Funktion wandelt die Report-Daten in der HashMap ins Datenbank-Format um und speichert die Datensätze in der DB
     */
    private void saveReportValues(Long requestId, Map<String, Object> values) throws StoreException {
        Map<String, Object> values1 = values;
        if (values1 == null) {
            return;
        }
        values1 = prepareMap(values1);
        try {
            for (Map.Entry<String, Object> entry : values1.entrySet()) {
                String key = entry.getKey();
                Object obj = entry.getValue();
                // Neuen Datensatz anlegen
                ReportData rd = new ReportData();
                rd.setRequestId(requestId);
                rd.setKeyName(key);
                if (obj != null) {
                    if (obj instanceof String) {
                        rd.setKeyValue((String) obj);
                    }
                    else if (obj instanceof Number) {
                        rd.setKeyValue(obj.toString());
                    }
                }
                saveReportData(rd);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR);
        }
    }

    /**
     * Funktion erzeugt eine HashMap mit einer flachen Struktur, indem alle Listen und HashMaps entfernet werden.
     */
    private Map<String, Object> prepareMap(Map<String, Object> map) {
        if (map == null) {
            return null;
        }
        Map<String, Object> result = new HashMap<String, Object>();

        // Durchlaufe alle Keys der HasMap
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object obj = entry.getValue();
            if (obj != null) {

                // Falls Objekt eine Liste, dann erzeuge neuen Key,
                // der die Position innerhalb der Liste wiedergibt
                // und fügen diesen Datensatz der HasMap hinzu.
                if (obj instanceof List<?>) {
                    List<?> l = (List<?>) obj;
                    for (int i = 0; i < l.size(); i++) {
                        Object element = l.get(i);
                        if (element != null) {

                            // Durchlaufe weitere HashMaps rekursiv.
                            if (element instanceof Map<?, ?>) {
                                @SuppressWarnings("unchecked")
                                Map<String, Object> hm = prepareMap((Map<String, Object>) element);

                                for (Map.Entry<String, Object> hmEntry : hm.entrySet()) {
                                    String aktKey = hmEntry.getKey();
                                    String newKey = key + "_" + i + "." + aktKey;
                                    Object hmValue = hmEntry.getValue();

                                    result.put(newKey, hmValue);
                                }
                            }
                            else {
                                result.put(key + "_" + i, element);
                            }
                        }
                    }
                }
                else if (obj instanceof Map<?, ?>) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> hm = prepareMap((Map<String, Object>) obj);

                    for (Map.Entry<String, Object> hmEntry : hm.entrySet()) {
                        String aktKey = hmEntry.getKey();
                        String newKey = key + "." + aktKey;
                        Object hmValue = hmEntry.getValue();
                        result.put(newKey, hmValue);
                    }
                }
                else {
                    result.put(key, obj);
                }
            }
            else {
                result.put(key, null);
            }
        }
        return result;
    }

    @Override
    public Map<String, Object> createMaps(Map<String, Object> map) {
        if (map == null) {
            return null;
        }

        Map<String, Object> result = new HashMap<String, Object>();

        // Durchlaufe alle Keys der HashMap
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            // Datensatz gehört zu einer Liste
            if (StringUtils.contains(key, '.')) {
                String strBeforeDot = StringUtils.substringBefore(key, ".");
                String strAfterDot = StringUtils.substringAfter(key, ".");
                // Falls Key einen Unterstrich enthält, dann erzeuge Listenindex
                if (StringUtils.contains(strBeforeDot, '_')) {
                    String strBeforeUnderline = StringUtils.substringBeforeLast(strBeforeDot, "_");
                    String strAfterUnderline = StringUtils.substringAfterLast(strBeforeDot, "_");
                    if ((strBeforeUnderline != null) && (strAfterUnderline != null)) {
                        int pos = Integer.parseInt(strAfterUnderline);
                        result = insertMap(result, strBeforeUnderline, pos);

                        ((Map<String, Object>) ((List<?>) result.get(strBeforeUnderline)).get(pos)).put(strAfterDot,
                                value);
                    }
                }
                else {
                    if (!result.containsKey(strBeforeDot)) {
                        result.put(strBeforeDot, new HashMap<String, Object>());
                    }
                    if (result.get(strBeforeDot) instanceof Map<?, ?>) {
                        ((Map<String, Object>) result.get(strBeforeDot)).put(strAfterDot, value);
                    }
                }
            }
            else {
                result.put(key, value);
            }
        }
        result = testNewMaps(result);
        return result;
    }

    /**
     * Hilfsfunktion für createMaps zum Anlegen einer HashMap und Vorbelegen von Listenelementen
     */
    private Map<String, Object> insertMap(Map<String, Object> map, String key, int pos) {
        // Falls key in Map noch nicht existiert, lege diesen an
        if (!map.containsKey(key)) {
            map.put(key, new ArrayList<Object>());
        }
        // Stelle sicher dass es sich beim eingefügten Objekt um eine Liste handelt
        if (!(map.get(key) instanceof List<?>)) {
            map.put(key, new ArrayList<Object>());
        }
        // Falls Liste noch nicht genügend Elemente enthält, dann füge Platzhalter mit Wert null ein
        if (((List<?>) map.get(key)).size() < (pos + 1)) {
            int anzahl = (pos + 1) - ((List<?>) map.get(key)).size();

            for (int i = 0; i < anzahl; i++) {
                ((List<?>) map.get(key)).add(null);
            }
        }
        // Füge leere HashMap an der betreffenden Stelle in der Liste ein
        if (!(((List<?>) map.get(key)).get(pos) instanceof Map<?, ?>)) {
            ((List<Object>) map.get(key)).set(pos, new HashMap<Object, Object>());
        }
        return map;
    }

    /**
     * Hilfsfunktion für createMaps um neu angelegte HashMaps rekursiv zu durchlaufen
     */
    private Map<String, Object> testNewMaps(Map<String, Object> map) {
        if (map == null) {
            return null;
        }

        Map<String, Object> result = new HashMap<String, Object>();

        // Durchlaufe alle keys der HashMap und
        // falls das zugehörige Element eine HashMap ist, überprüfe dessen Struktur
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            if (entry.getValue() instanceof Map<?, ?>) {
                result.put(key, createMaps((Map<String, Object>) entry.getValue()));
            }
            else {
                result.put(key, entry.getValue());
            }
        }
        return result;
    }

    /**
     * Funktion merged zwei HashMaps. Falls ein Key doppelt vorkommt, wird das Prefix "ErrorKey_" vorangestellt.
     */
    private Map<String, Object> addMap(Map<String, Object> map, Map<String, Object> toAdd) {
        Map<String, Object> map1 = map;
        if (toAdd == null) {
            return map1;
        }
        if (map1 == null) {
            map1 = new HashMap<String, Object>();
        }

        for (Map.Entry<String, Object> entry : toAdd.entrySet()) {
            String key = entry.getKey();
            if (map1.containsKey(entry.getKey())) {
                key = "ErrorKey_" + key;
                if (map1.containsKey(key)) {
                    continue;
                }
            }
            map1.put(key, entry.getValue());
        }
        return map1;
    }

    @Override
    public void generateReport(Long requestId, OpenOfficeConnection connection) throws ReportException {
        if (requestId == null) {
            throw new ReportException(ReportException.INVALID_PARAMETER);
        }

        File reportTmp = null;
        FileOutputStream outputStream = null;
        try {
            // Ermittle Daten für den Report-Request
            ReportRequest request = findReportRequest(requestId);
            if (request == null) {
                throw new ReportException(ReportException._UNEXPECTED_ERROR);
            }
            Map<String, Object> values = findReportData4ReportRequest(request);

            saveReportValues(requestId, values);

            // Wandle Null-Werte in HashMap in Leerstrings um
            values = deleteNullValues(values);

            // Report-Vorlage ermitteln
            ReportTemplate repTemp = findReportTemplate4Report(request.getRepId());
            if (repTemp == null) {
                throw new ReportException(ReportException._UNEXPECTED_ERROR);
            }
            String reportTemplate = reportPathTemplates + repTemp.getReportId() + File.separator
                    + repTemp.getFilename();

            // Erzeuge Report als OpenOffice-Dokument mit JooReports
            // 1. Durchlauf
            DocumentTemplate template = new ZippedDocumentTemplate(new File(reportTemplate));
            String tmpFile = reportPathTemp + "Report-ReqID" + requestId + "-tmp.odt";
            outputStream = new FileOutputStream(tmpFile);
            template.createDocument(values, outputStream);
            // 2. Durchlauf (Platzhalter in Text-Bausteinen werden in diesem Schritt ersetzt)
            reportTmp = new File(tmpFile);
            template = new ZippedDocumentTemplate(reportTmp);
            String reportFile = reportPathTemp + "Report-ReqID" + requestId + "-"
                    + Calendar.getInstance().getTimeInMillis() + ".odt";
            template.createDocument(values, new FileOutputStream(reportFile));

            // Umwandlung von OpenOffice in Dateiformat wie in ReportRequest.requestType angegeben
            String format = (request.getRequestType() == null) ? "pdf" : request.getRequestType();
            String pdfFile = convertDocument(reportFile, format, connection);

            // File in Output-Ordner kopieren
            String outputFile = reportPathOutput + StringUtils.substringAfterLast(pdfFile, File.separator);
            File output = new File(outputFile);

            // Kopiere Datei und lösche temporäre Datei
            FileTools.copyFile(new File(pdfFile), output, true);

            // Erzeugungsdatum und Filename in ReportRequest schreiben
            if (output.exists()) {
                request.setFile(StringUtils.substringAfterLast(pdfFile, File.separator));
                request.setRequestFinishedAt(DateTools.getActualSQLDate());
                request.setError(null);
                saveReportRequest(request);
            }
            else {
                throw new ReportException(ReportException._UNEXPECTED_ERROR);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            setError4Request(requestId, e);
            throw new ReportException(e.getMessage(), e);
        }
        finally {
            try {
                FileTools.closeStreamSilent(outputStream);
                FileTools.deleteFile(reportTmp);
            }
            catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Funktion schreibt Fehlermeldung in ReportRequest-Datensatz
     */
    private void setError4Request(Long requestId, Exception error) throws RuntimeException {
        try {
            if ((requestId != null) && (error != null)) {
                ReportRequest request = findReportRequest(requestId);
                if (request != null) {
                    // Erzeuge String aus Exception
                    StringBuilder erStr = new StringBuilder();
                    // Falls bereits eine Eintrag im Error-Feld vorhanden,
                    // wird neue Fehlermeldung an diese angehängt.
                    if (StringUtils.isNotBlank(request.getError())) {
                        erStr.append("(" + request.getError() + ") - ");
                    }
                    erStr.append(error.getMessage());
                    Throwable cause = error.getCause();
                    int i = 1;
                    while ((cause != null) && (i < 10)) {
                        erStr.append("\n").append(cause.getMessage());
                        cause = cause.getCause();
                        i++;
                    }
                    request.setError(StringUtils.substring(erStr.toString(), 0, 65000));
                    saveReportRequest(request);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public String convertDocument(String file, String format, OpenOfficeConnection connection) throws ReportException {
        if ((file == null) || (format == null)) {
            return null;
        }

        File inputFile = null;
        File localInputFile = null;
        try {
            // Konvertiere Datei
            inputFile = new File(file);
            if (!inputFile.exists()) {
                return null;
            }

            // Basis-File auf lokales Verzeichnis kopieren
            localInputFile = new File(System.getProperty("user.home"), inputFile.getName());
            FileTools.copyFile(inputFile, localInputFile, false);

            String destFile = StringUtils.substringBeforeLast(file, ".") + "." + format;
            File outputFile = new File(destFile);

            // Konvertierung ueber lokale Files vornehmen, da OO sonst Fehler verursacht
            // (Fehlermeldung: URL seems to be an unsupported one)
            File localOutputFile = new File(System.getProperty("user.home"), outputFile.getName());
            DocumentConverter converter = new OpenOfficeDocumentConverter(connection);
            converter.convert(localInputFile, localOutputFile);

            FileTools.copyFile(localOutputFile, outputFile, true);

            // Falls Konvertierung erfolgreich, gebe Dateinamen zurück
            return destFile;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ReportException(ReportException._UNEXPECTED_ERROR, e);
        }
        finally {
            try {
                FileTools.deleteFile(inputFile);
                FileTools.deleteFile(localInputFile);
            }
            catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public ReportTemplate findReportTemplateById(Long id) throws FindException {
        try {
            return reportTemplateDAO.findById(id, ReportTemplate.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public ReportTemplate findReportTemplate4Report(Long reportId) throws FindException {
        try {
            return reportTemplateDAO.findTemplate4Report(reportId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Long createReportRequest(Long reportId, Long sessionId, Long kundeNoOrig, Long orderNoOrig, Long auftragId,
            Integer buendelNo, Long reasonId, Boolean sendMsg) throws ReportException {
        try {
            ReportRequest request = new ReportRequest();
            request.setAuftragId(auftragId);
            request.setKundeNo(kundeNoOrig);
            request.setOrderNoOrig(orderNoOrig);
            request.setRequestAt(DateTools.getActualSQLDate());
            request.setBuendelNo(buendelNo);
            request.setPrintReasonId(reasonId);

            // Aktuellen User ermitteln
            IServiceLocator authSL = ServiceLocatorRegistry.instance().getServiceLocator(
                    IServiceLocatorNames.AUTHENTICATION_SERVICE);
            AKUserService userService = authSL.getService(AKAuthenticationServiceNames.USER_SERVICE,
                    AKUserService.class, null);
            AKUser user = userService.findUserBySessionId(sessionId);

            request.setRequestFrom(((user != null) && (user.getLoginName() != null)) ? user.getLoginName() : "");
            request.setRequestType("pdf");
            request.setRepId(reportId);

            // Report-Request speichern
            saveReportRequest(request);

            // Report-Server benachrichtigen
            if (request.getId() != null) {
                if (sendMsg) {
                    sendReportRequest(request, sessionId);
                }
                return request.getId();
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ReportException("Fehler bei Anlegen einer Report-Anfrage", e);
        }
        return null;
    }

    @Override
    public void sendReportRequest(final ReportRequest request, final Long sessionId) throws ReportException {
        if (request == null) {
            return;
        }
        jmsTemplateToSendReportRequests.send(new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                MapMessage message = session.createMapMessage();
                message.setLong(MSG_KEY_REQUEST_ID, request.getId());
                message.setLong(MSG_KEY_SESSION_ID, sessionId);
                return message;
            }
        });
    }

    @Override
    public List<Report> findAvailableReports(Long repType, AuftragDaten auftragDaten, Long sessionId)
            throws FindException {
        try {
            List<Report> result = new ArrayList<Report>();

            if (auftragDaten == null) {
                List<Report> reports = reportDAO.findReportsByProdStati(repType, null, null);
                if (CollectionTools.isNotEmpty(reports)) {
                    for (Report rep : reports) {
                        // Pruefe User-Zuordnung
                        if (testReportUser(rep, sessionId)) {
                            result.add(rep);
                        }
                    }
                }
            }
            else {
                // Ermittle alle Reports anhand des Produkts und des Status
                List<Report> reports = reportDAO.findReportsByProdStati(repType, auftragDaten.getProdId(),
                        auftragDaten.getStatusId());
                if (CollectionTools.isNotEmpty(reports)) {
                    for (Report rep : reports) {
                        // Überprüfe, ob zusätzlich für das Produkt eine techn. Leistung zugeordnet ist
                        // und prüfe User-Zuordnung
                        if (testReportTechLs(rep, auftragDaten) && testReportUser(rep, sessionId)) {
                            result.add(rep);
                        }
                    }
                }
            }
            return (CollectionTools.isNotEmpty(result)) ? result : null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    /*
     * Funktion prüft die Zuordnung von techn. Leistungen zum aktuellen Report
     */
    private Boolean testReportTechLs(Report report, AuftragDaten auftragDaten) throws FindException {
        try {
            // Überprüfe, ob zusätzlich für das Produkt eine techn. Leistung zugeordnet ist
            List<Long> techLsIds = report2TechLsDAO.findTechLsIds4ReportProdukt(report.getId(),
                    auftragDaten.getProdId());
            // Falls Keine techn. Leistung zugeordnet, dann ist Report verfügbar
            if (CollectionTools.isEmpty(techLsIds)) {
                return Boolean.TRUE;
            }
            // Überprüfe, ob Auftrag die benötigte techn. Leistung enthält
            CCLeistungsService ls = (CCLeistungsService) getCCService(CCLeistungsService.class);
            List<TechLeistung> ls4Auftrag = ls.findTechLeistungen4Auftrag(auftragDaten.getAuftragId(), null, true);
            Map<Long, TechLeistung> map = new HashMap<Long, TechLeistung>();
            CollectionMapConverter.convert2Map(ls4Auftrag, map, "getId", null);
            for (Long lsId : techLsIds) {
                if (map.containsKey(lsId)) {
                    return Boolean.TRUE;
                }
            }
            return Boolean.FALSE;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(e.getMessage(), e);
        }
    }

    /*
     * Funktion testet die User-Zuordnung für den aktuellen Report
     */
    private Boolean testReportUser(Report report, Long sessionId) throws FindException {
        try {
            // Überprüfe, ob dem Report eine best. Benutzer-Rolle zugeordnet ist.
            ReportConfigService rs = (ReportConfigService) getReportService(ReportConfigService.class);
            List<Report2UserRole> roles = rs.findUserRoles4Report(report.getId());
            // Falls keine User-Rolle zugeordnet, dann ist Report verfuegbar
            if (CollectionTools.isEmpty(roles)) {
                return Boolean.TRUE;
            }
            // Überpruefe, ob aktueller Benutzer die entsprechende Rolle besitzt.
            AKUser user = userService.findUserBySessionId(sessionId);
            if (user != null) {
                AKApplication app = applicationService.findByName("Hurrican");
                // Ermittle Benutzer-Rollen
                List<AKRole> userRoles = userService.getRoles(user.getId(), app.getId());

                Map<Long, Report2UserRole> map = new HashMap<Long, Report2UserRole>();
                CollectionMapConverter.convert2Map(roles, map, "getRoleId", null);
                for (AKRole akRole : userRoles) {
                    if (map.containsKey(akRole.getId())) {
                        return Boolean.TRUE;
                    }
                }
            }
            return Boolean.FALSE;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(e.getMessage(), e);
        }
    }

    /**
     * @see de.augustakom.hurrican.service.reporting.ReportService#downloadReport(java.lang.Long, java.lang.String)
     */
    @Override
    public String downloadReport(Long requestId, String tmpPath) throws FindException {
        if ((requestId == null) || (tmpPath == null)) {
            throw new FindException(FindException.EMPTY_FIND_PARAMETER);
        }
        ReportRequest request = findReportRequest(requestId);
        if ((request == null) || StringUtils.isBlank(request.getFilePathForCurrentPlatform())) {
            throw new FindException("Kein File vorhanden!");
        }
        String filename = StringUtils.substringAfterLast(request.getFilePathForCurrentPlatform(), File.separator);
        String tmpFileName = (StringUtils.isNotBlank(filename)) ? tmpPath + filename : tmpPath
                + request.getFilePathForCurrentPlatform();

        try {
            String reportFileName = reportPathOutput + request.getFilePathForCurrentPlatform();
            FileTools.copyFile(new File(reportFileName), new File(tmpFileName), false);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException("Datei kann nicht heruntergeladen werden");
        }
        return tmpFileName;
    }

    /**
     * @see de.augustakom.hurrican.service.reporting.ReportService#getPDFBundled(java.util.List, java.lang.String,
     * java.lang.String)
     */
    @Override
    public List<String> getPDFBundled(List<ReportRequest> requests, String tmpPath, String serienbriefFile)
            throws ReportException {
        if (CollectionTools.isEmpty(requests) || (tmpPath == null)) {
            return null;
        }
        try {
            PdfCopyFields pdfCopy = new PdfCopyFields(new FileOutputStream(serienbriefFile));
            List<String> result = new ArrayList<String>();
            // Lade alle Reports einzeln herunter und fasse alle PDFs zusammen
            for (ReportRequest request : requests) {
                File pdf = null;
                if (StringUtils.isNotBlank(request.getFilePathForCurrentPlatform())) {
                    String filename = StringUtils.substringAfterLast(request.getFilePathForCurrentPlatform(),
                            File.separator);
                    String tmpFileName = (StringUtils.isNotBlank(filename)) ? tmpPath + filename : tmpPath
                            + request.getFilePathForCurrentPlatform();
                    pdf = new File(tmpFileName);

                    FileTools
                            .copyFile(new File(reportPathOutput + request.getFilePathForCurrentPlatform()), pdf, false);

                    if (pdf.canRead()) {
                        result.add(tmpFileName);
                        PdfReader reader = new PdfReader(tmpFileName);
                        pdfCopy.addDocument(reader);
                        reader.close();
                    }
                }
            }
            pdfCopy.close();
            return result;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ReportException(ReportException._UNEXPECTED_ERROR, e);
        }
    }

    /**
     * @see de.augustakom.hurrican.service.reporting.ReportService#getPDF4BuendelNo(java.lang.Integer, java.lang.String)
     */
    @Override
    public List<String> getPDF4BuendelNo(Integer buendelNo, String tmpPath, String serienbriefFile)
            throws ReportException {
        if ((buendelNo == null) || (tmpPath == null)) {
            return null;
        }
        try {
            // Ermittle alle Reports, die zu einer Bündelnr. gehören
            List<ReportRequest> requests = findAllRequests4BuendelNo(buendelNo);
            if (CollectionTools.isNotEmpty(requests)) {
                PdfCopyFields pdfCopy = new PdfCopyFields(new FileOutputStream(serienbriefFile));
                List<String> result = new ArrayList<String>();

                // Lade alle Reports einzeln herunter und fasse alle PDFs zusammen
                for (ReportRequest request : requests) {
                    File pdf = null;
                    if (StringUtils.isNotBlank(request.getFilePathForCurrentPlatform())) {
                        String filename = StringUtils.substringAfterLast(request.getFilePathForCurrentPlatform(),
                                File.separator);
                        String tmpFileName = (StringUtils.isNotBlank(filename)) ? tmpPath + filename : tmpPath
                                + request.getFilePathForCurrentPlatform();
                        pdf = new File(tmpFileName);

                        FileTools.copyFile(new File(reportPathOutput + request.getFilePathForCurrentPlatform()), pdf,
                                false);

                        if (pdf.canRead()) {
                            result.add(tmpFileName);
                            PdfReader reader = new PdfReader(tmpFileName);
                            pdfCopy.addDocument(reader);
                            reader.close();
                        }
                    }
                }
                pdfCopy.close();
                return result;
            }
            return null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ReportException(ReportException._UNEXPECTED_ERROR, e);
        }
    }

    /*
     * Funktion ändert in einer HashMap alle null-Wert in Leerstrings. Diese Umwandlung ist für die Reporterzeugung
     * nötig, da dort keine null-Werte verarbeitet werden können
     */
    private Map<String, Object> deleteNullValues(Map<String, Object> map) {
        if (map == null) {
            return null;
        }
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object obj = entry.getValue();
            if (obj == null) {
                map.put(key, "");
            }
            if (obj instanceof Date) {
                map.put(key, DateTools.formatDate((Date) obj, DateTools.PATTERN_DAY_MONTH_YEAR));
            }
            if (obj instanceof List<?>) {
                List<Object> neu = deleteNullValues((List<Object>) entry.getValue());
                map.put(key, neu);
            }
            if (obj instanceof Map<?, ?>) {
                Map<String, Object> neu = deleteNullValues((Map<String, Object>) entry.getValue());
                map.put(key, neu);
            }
        }
        return map;
    }

    /*
     * Hilfsdatei für deleteNullValues(List)
     */
    private List<Object> deleteNullValues(List<Object> list) {
        for (int i = 0; i < list.size(); i++) {
            Object obj = list.get(i);
            if (obj == null) {
                list.set(i, "");
            }
            if (obj instanceof List<?>) {
                List<Object> neu = deleteNullValues((List<Object>) obj);
                list.set(i, neu);
            }
            if (obj instanceof Map<?, ?>) {
                Map<String, Object> neu = deleteNullValues((Map<String, Object>) obj);
                list.set(i, neu);
            }
        }
        return list;
    }

    @Override
    public List<ReportRequestView> findReportRequest4Auftrag(Long kundeNoOrig, Long auftragId, Date filterBeginn,
            Date filterEnde) throws FindException {
        try {
            return reportRequestDAO.findAllRequestsByKundeNoAndAuftragId(kundeNoOrig, auftragId, filterBeginn,
                    filterEnde);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<TxtBaustein> findTxtBausteine4Gruppe(Long gruppeId) throws FindException {
        List<TxtBaustein> result;
        try {
            result = txtBausteinDAO.findAllValid4TxtBausteinGruppe(gruppeId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
        return (CollectionTools.isNotEmpty(result)) ? result : null;
    }

    @Override
    public List<String> findReportDataByKey(Long requestId, String key) throws FindException {
        try {
            ReportData example = new ReportData();
            example.setRequestId(requestId);
            example.setKeyName(key);

            List<ReportData> list = reportDataDAO.queryByExample(example, ReportData.class);
            if (CollectionTools.isNotEmpty(list)) {
                List<String> result = new ArrayList<String>();
                for (ReportData data : list) {
                    result.add(data.getKeyValue());
                }
                return result;
            }
            return null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void deleteOldReportData(Integer days) throws DeleteException {
        if (days == null) {
            throw new DeleteException(DeleteException.INVALID_PARAMETERS);
        }

        try {
            List<Long> list = reportRequestDAO.findReportData2Delete(days);

            if (CollectionTools.isNotEmpty(list)) {
                for (Long requestId : list) {
                    if (requestId != null) {
                        reportDataDAO.deleteData4Request(requestId);
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new DeleteException(DeleteException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void deleteOldReports(Integer days, Boolean archive) throws ReportException {
        if ((days == null) || (archive == null)) {
            throw new ReportException(ReportException.INVALID_PARAMETER);
        }

        try {
            // Suche alle zu löschenden Reports
            List<ReportRequest> list = reportRequestDAO.findReports2Delete(days);

            // Ermittle Output-Pfad

            // Prüfe ob Reports vorhanden
            if (CollectionTools.isNotEmpty(list)) {
                String archivName = null;
                // Falls archiviert werden soll, lege Verzeichnis an
                if (archive) {
                    archivName = ReportService.REPORT_ARCHIV + File.separator
                            + DateTools.formatDate(new Date(), DateTools.PATTERN_YEAR_MONTH_DAY);
                    File archiv = new File(reportPathOutput + archivName);
                    boolean created = archiv.mkdirs();
                    if (!created) {
                        throw new IOException("Archiv directory not created: " + archiv.getAbsolutePath());
                    }
                }

                // Durchlaufe alle ermittelten Reports
                for (ReportRequest request : list) {
                    String filename = request.getFilePathForCurrentPlatform();
                    if (StringUtils.isNotBlank(filename)) {
                        File report = new File(reportPathOutput + filename);
                        try {
                            if (archive && report.canRead()) {
                                // Kopiere Report ins Archiv
                                FileTools.copyFile(report, new File(reportPathOutput + archivName + File.separator
                                        + filename), true);
                                // Ändere Filename in ReportRequest-Objekt
                                request.setFile(archivName + File.separator + filename);
                                saveReportRequest(request);
                            }
                            // Falls File existiert und Schreibrechte vorhanden sind, lösche File
                            else if (report.exists() && report.canWrite()) {
                                FileTools.deleteFile(report);
                                // Lösche Filename in ReportRequest-Objekt
                                request.setFile(null);
                                saveReportRequest(request);
                            }
                        }
                        catch (Exception e) {
                            LOGGER.error(e);
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ReportException(ReportException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<TxtBausteinGruppe> findAllTxtBausteinGruppen4Report(Long reportId) throws FindException {
        try {
            return txtBausteinGruppeDAO.findAll4Report(reportId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<ReportRequest> findAllRequests4BuendelNo(Integer buendelNo) throws FindException {
        if (buendelNo == null) {
            return null;
        }
        try {
            ReportRequest example = new ReportRequest();
            example.setBuendelNo(buendelNo);

            return reportRequestDAO.queryByExample(
                    example, ReportRequest.class, new String[] { "id" }, null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Integer findNewReportBuendelNo() throws FindException {
        return reportRequestDAO.findNewBuendelNo();
    }

    @Override
    public Boolean testSerienbriefReady(Integer buendelNo) throws FindException {
        try {
            return CollectionTools.isEmpty(reportRequestDAO.findReportsNotReady4BuendelNo(buendelNo));
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<ReportRequestView> findAllSerienbriefe() throws FindException {
        try {
            return reportRequestDAO.findAllSerienbriefe();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<ReportData> findAllReportDataByRequestId(Long requestId) throws FindException {
        try {
            ReportData example = new ReportData();
            example.setRequestId(requestId);

            return reportDataDAO.queryByExample(example, ReportData.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<ReportRequest> findReportRequests4ReportGruppe(Report rep, Long kundeNoOrig, Long auftragId)
            throws FindException {
        if ((rep == null) || (kundeNoOrig == null)) {
            return null;
        }
        try {
            List<ReportRequest> list = reportRequestDAO.findAllRequests4Reportgruppe(rep, kundeNoOrig, auftragId);

            return (CollectionTools.isNotEmpty(list)) ? list : null;
        }
        catch (Exception e) {
            throw new FindException(e);
        }
    }

    @Override
    public Boolean testReportReprint(Report rep, Long kundeNoOrig, AuftragDaten ad) throws FindException {
        try {
            List<ReportRequest> list = findReportRequests4ReportGruppe(rep, kundeNoOrig,
                    (ad != null) ? ad.getAuftragId() : null);

            return (CollectionTools.isEmpty(list)) ? Boolean.FALSE : Boolean.TRUE;
        }
        catch (Exception e) {
            throw new FindException(e);
        }
    }

    @Override
    public List<ReportReason> findReportReasons(Boolean reprintBeforeArchive) throws FindException {
        try {
            if ((reprintBeforeArchive != null) && reprintBeforeArchive.booleanValue()) {
                return reportReasonDAO.findAll(ReportReason.class);
            }
            ReportReason example = new ReportReason();
            example.setOnlyNotArchived(Boolean.FALSE);

            return reportReasonDAO.queryByExample(example, ReportReason.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Boolean testReportReadable(ReportRequest request) throws ReportException {
        if (request == null) {
            return Boolean.FALSE;
        }
        try {
            if (StringUtils.isBlank(request.getFilePathForCurrentPlatform())) {
                return Boolean.FALSE;
            }
            File report = new File(reportPathOutput + request.getFilePathForCurrentPlatform());
            return report.canRead();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new ReportException(e.getMessage(), e);
        }

    }

    @Override
    public void saveReportParameter(Long requestId, String parameterKey, String parameterValue) throws StoreException {
        try {
            ReportData data = new ReportData();
            data.setKeyName(parameterKey);
            data.setKeyValue(parameterValue);
            data.setRequestId(requestId);
            saveReportData(data);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(e.getMessage(), e);
        }
    }

    @Override
    public String findReportParameter(Long requestId, String parameterKey) throws FindException {
        try {
            List<String> list = findReportDataByKey(requestId, parameterKey);
            return (CollectionTools.isNotEmpty(list)) ? list.get(0) : null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(e.getMessage(), e);
        }
    }

    @Override
    public Printer findPrinterByName(String name) throws FindException {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        List<Printer> printers = printerDAO.findPrintersByName(name);
        if ((printers != null) && (printers.size() > 1)) {
            throw new FindException(FindException.INVALID_RESULT_SIZE,
                    new Object[] { Integer.valueOf(1), Integer.valueOf(printers.size()) });
        }
        return ((printers != null) && (printers.size() == 1)) ? printers.get(0) : null;
    }

    @Override
    public boolean isReportForAuftragAvailable(Long reportId, Long auftragId, Long sessionId) throws FindException,
            ServiceNotFoundException {
        CCAuftragService auftragService = (CCAuftragService) getCCService(CCAuftragService.class);
        AuftragDaten auftragDaten = auftragService.findAuftragDatenByAuftragId(auftragId);
        List<Report> availableReportsForAuftrag = findAvailableReports(Report.REPORT_TYPE_AUFTRAG, auftragDaten,
                sessionId);
        if (availableReportsForAuftrag != null) {
            for (Report report : availableReportsForAuftrag) {
                if (Report.REPORT_INSTALLATIONSAUFTRAG_ENDSTELLE_B.equals(report.getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    public void setReportReasonDAO(ReportReasonDAO reportReasonDAO) {
        this.reportReasonDAO = reportReasonDAO;
    }

    public void setPrinterDAO(PrinterDAO printerDAO) {
        this.printerDAO = printerDAO;
    }

    public void setReportDAO(ReportDAO reportDAO) {
        this.reportDAO = reportDAO;
    }

    public void setReportDataDAO(ReportDataDAO reportDataDAO) {
        this.reportDataDAO = reportDataDAO;
    }

    public void setReportRequestDAO(ReportRequestDAO reportRequestDAO) {
        this.reportRequestDAO = reportRequestDAO;
    }

    public void setReportTemplateDAO(ReportTemplateDAO reportTemplateDAO) {
        this.reportTemplateDAO = reportTemplateDAO;
    }

    public void setTxtBausteinDAO(TxtBausteinDAO reportTxtBausteinDAO) {
        this.txtBausteinDAO = reportTxtBausteinDAO;
    }

    public void setTxtBausteinGruppeDAO(TxtBausteinGruppeDAO txtBausteinGruppeDAO) {
        this.txtBausteinGruppeDAO = txtBausteinGruppeDAO;
    }

    public void setReportPaperFormatDAO(ReportPaperFormatDAO reportPaperFormatDAO) {
        this.reportPaperFormatDAO = reportPaperFormatDAO;
    }

    public void setReport2TechLsDAO(Report2TechLsDAO report2TechLsDAO) {
        this.report2TechLsDAO = report2TechLsDAO;
    }

    public void setJmsTemplateToSendReportRequests(JmsTemplate jmsTemplateToSendReportRequests) {
        this.jmsTemplateToSendReportRequests = jmsTemplateToSendReportRequests;
    }

    public void setReportPathOutput(String reportPathOutput) {
        this.reportPathOutput = reportPathOutput;
    }

    public void setReportPathTemp(String reportPathTemp) {
        this.reportPathTemp = reportPathTemp;
    }

    public void setReportPathTemplates(String reportPathTemplates) {
        this.reportPathTemplates = reportPathTemplates;
    }

}
