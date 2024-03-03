/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.02.2007 12:16:41
 */
package de.augustakom.hurrican.service.reporting.impl;

import java.io.*;
import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.authentication.model.AKRole;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.file.FileTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.annotation.ReportingTxRequired;
import de.augustakom.hurrican.dao.reporting.Report2ProdDAO;
import de.augustakom.hurrican.dao.reporting.Report2ProdStatiDAO;
import de.augustakom.hurrican.dao.reporting.Report2TechLsDAO;
import de.augustakom.hurrican.dao.reporting.Report2TxtBausteinGruppeDAO;
import de.augustakom.hurrican.dao.reporting.Report2UserRoleDAO;
import de.augustakom.hurrican.dao.reporting.ReportDAO;
import de.augustakom.hurrican.dao.reporting.ReportGruppeDAO;
import de.augustakom.hurrican.dao.reporting.ReportReasonDAO;
import de.augustakom.hurrican.dao.reporting.ReportTemplateDAO;
import de.augustakom.hurrican.dao.reporting.TxtBaustein2GruppeDAO;
import de.augustakom.hurrican.dao.reporting.TxtBausteinDAO;
import de.augustakom.hurrican.dao.reporting.TxtBausteinGruppeDAO;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.command.ServiceCommand;
import de.augustakom.hurrican.model.reporting.Report;
import de.augustakom.hurrican.model.reporting.Report2Prod;
import de.augustakom.hurrican.model.reporting.Report2ProdStati;
import de.augustakom.hurrican.model.reporting.Report2TechLs;
import de.augustakom.hurrican.model.reporting.Report2TxtBausteinGruppe;
import de.augustakom.hurrican.model.reporting.Report2UserRole;
import de.augustakom.hurrican.model.reporting.ReportGruppe;
import de.augustakom.hurrican.model.reporting.ReportTemplate;
import de.augustakom.hurrican.model.reporting.TxtBaustein;
import de.augustakom.hurrican.model.reporting.TxtBaustein2Gruppe;
import de.augustakom.hurrican.model.reporting.TxtBausteinGruppe;
import de.augustakom.hurrican.model.reporting.view.Report2ProdView;
import de.augustakom.hurrican.model.reporting.view.Report2TechLsView;
import de.augustakom.hurrican.service.base.exceptions.DeleteException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.ReportException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.ChainService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.reporting.ReportConfigService;
import de.augustakom.hurrican.service.reporting.ReportService;
import de.augustakom.hurrican.validation.reporting.ReportValidator;

/**
 * Implementierung für ReportConfig-Service mit Funktionen für die Report-Konfiguration.
 *
 *
 */
@ReportingTxRequired
public class ReportConfigServiceImpl extends DefaultReportService implements ReportConfigService {

    private static final Logger LOGGER = Logger.getLogger(ReportConfigServiceImpl.class);

    private ReportDAO reportDAO;
    private Report2ProdDAO report2ProdDAO;
    private Report2ProdStatiDAO report2ProdStatiDAO;
    private Report2TechLsDAO report2TechLsDAO;
    private ReportTemplateDAO reportTemplateDAO;
    private TxtBausteinDAO txtBausteinDAO;
    private Report2TxtBausteinGruppeDAO report2TxtBausteinGruppeDAO;
    private TxtBausteinGruppeDAO txtBausteinGruppeDAO;
    private TxtBaustein2GruppeDAO txtBaustein2GruppeDAO;
    private Report2UserRoleDAO report2UserRoleDAO;
    private ReportGruppeDAO reportGruppeDAO;
    private ReportReasonDAO reportReasonDAO;
    private ReportValidator reportValidator;
    private String reportPathTemplates;

    @Override
    public void saveTxtBaustein(TxtBaustein toSave) throws StoreException {
        try {
            getTxtBausteinDAO().store(toSave);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<TxtBaustein> findAllTxtBausteine() throws FindException {
        try {
            return getTxtBausteinDAO().findAll(TxtBaustein.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR);
        }
    }

    @Override
    public List<TxtBaustein> findAllNewTxtBausteine() throws FindException {
        try {
            return getTxtBausteinDAO().findAllNewTxtBausteine();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR);
        }
    }

    @Override
    public List<TxtBaustein> findAllTxtBausteine4IdOrig(Long idOrig) throws FindException {
        if (idOrig == null) {
            return null;
        }
        try {
            TxtBaustein example = new TxtBaustein();
            example.setIdOrig(idOrig);

            return getTxtBausteinDAO().queryByExample(
                    example, TxtBaustein.class, new String[] { "id" }, null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR);
        }
    }

    @Override
    public List<TxtBaustein> findAllValidTxtBausteine() throws FindException {
        try {
            return getTxtBausteinDAO().findAllValidTxtBausteine();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR);
        }
    }

    @Override
    public void saveTxtBausteinGruppen4Report(Long reportId, List<TxtBausteinGruppe> gruppen) throws StoreException {
        if (gruppen == null) {
            throw new StoreException(StoreException._UNEXPECTED_ERROR);
        }
        try {
            // Lösche alte Zuordnung
            deleteAllTxtBausteinGruppen4Report(reportId);

            // Falls Liste mit neuer Zuordnung leer ist, breche ab
            if (CollectionTools.isEmpty(gruppen)) {
                return;
            }

            // Speichere neue Zuordnung
            long i = 1;
            for (TxtBausteinGruppe gruppe : gruppen) {
                if (gruppe != null) {
                    Report2TxtBausteinGruppe rep2Txt = new Report2TxtBausteinGruppe();
                    rep2Txt.setReportId(reportId);
                    rep2Txt.setTxtBausteinGruppeId(gruppe.getId());
                    rep2Txt.setOrderNo(i++);
                    getReport2TxtBausteinGruppeDAO().store(rep2Txt);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR);
        }
    }

    @Override
    public void deleteTxtBausteinById(Long id) throws DeleteException {
        if (id == null) {
            throw new DeleteException(DeleteException.INVALID_PARAMETERS);
        }
        try {
            getTxtBausteinDAO().deleteById(id);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new DeleteException(DeleteException.DELETE_FAILED, e);
        }
    }

    @Override
    public void saveReport2ProdView(List<Report2ProdView> views, Long reportId) throws StoreException {
        if (reportId == null) {
            return;
        }
        try {
            // Lösche bestehende Zuordnungen
            deleteReport2Prod4Report(reportId);

            if (CollectionTools.isEmpty(views)) {
                return;
            }
            // Speichere neue Zuordnung
            for (int i = 0; i < views.size(); i++) {
                Report2ProdView view = views.get(i);
                if (view != null) {
                    Report2Prod rep2Prod = getReport2ProdDAO().findReport2ProdByIds(view.getReportId(), view.getProduktId());
                    if (rep2Prod == null) {
                        rep2Prod = new Report2Prod();
                        rep2Prod.setProduktId(view.getProduktId());
                        rep2Prod.setReportId(view.getReportId());

                        getReport2ProdDAO().store(rep2Prod);
                    }
                    if (rep2Prod.getId() != null) {
                        Report2ProdStati status = getReport2ProdStatiDAO().findReport2ProdStatiByIds(rep2Prod.getId(), view.getStatusId());
                        if (status == null) {
                            status = new Report2ProdStati();
                            status.setReport2ProdId(rep2Prod.getId());
                            status.setStatusId(view.getStatusId());

                            getReport2ProdStatiDAO().store(status);
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void saveReport2TechLsView(List<Report2TechLsView> views, Long reportId) throws StoreException {
        if (reportId == null) {
            return;
        }
        try {
            // Lösche bestehende Zuordnungen
            deleteReport2TechLs4Report(reportId);

            if (CollectionTools.isEmpty(views)) {
                return;
            }
            // Speichere neue Zuordnung
            for (Report2TechLsView view : views) {
                if (view != null) {
                    // Ermittle Report2Produkt Zuordnung
                    Report2Prod rep2Prod = getReport2ProdDAO().findReport2ProdByIds(view.getReportId(), view.getProduktId());

                    if (rep2Prod.getId() != null) {
                        // Ermittle ob Zuordnung bereits existiert
                        Report2TechLs techLs = getReport2TechLsDAO().findReport2TechLsByIds(rep2Prod.getId(), view.getTechLsId());
                        if (techLs == null) {
                            techLs = new Report2TechLs();
                            techLs.setReport2ProdId(rep2Prod.getId());
                            techLs.setTechLsId(view.getTechLsId());

                            getReport2TechLsDAO().store(techLs);
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    /*
     * Funktion löscht alle Report-Produkt Zuordnungen für einen best. Report
     */
    private void deleteReport2Prod4Report(Long reportId) throws DeleteException {
        try {
            List<Report2Prod> report2Prods = findReport2Prod4Report(reportId);
            if (CollectionTools.isNotEmpty(report2Prods)) {
                // Lösche Status-Zuordnung zu Report/Produkt
                for (Report2Prod rep2Prod : report2Prods) {
                    if (rep2Prod != null) {
                        getReport2ProdStatiDAO().deleteReport2ProdStati(rep2Prod.getId());
                        // Lösche Zuordnung der techn Leistung zu einem Report/Produkt
                        getReport2TechLsDAO().deleteReport2TechLs(rep2Prod.getId());
                    }
                }
            }
            // Lösche Produktzuordnungen für einen Report.
            getReport2ProdDAO().deleteProds4Report(reportId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new DeleteException(DeleteException._UNEXPECTED_ERROR, e);
        }
    }

    /*
     * Funktion löscht alle Report-TechLs Zuordnungen für einen best. Report
     */
    private void deleteReport2TechLs4Report(Long reportId) throws DeleteException {
        try {
            List<Report2Prod> report2Prods = findReport2Prod4Report(reportId);
            if (CollectionTools.isNotEmpty(report2Prods)) {
                // Lösche Techn. Leistungs-Zuordnung zu Report/Produkt
                for (Report2Prod rep2Prod : report2Prods) {
                    if (rep2Prod != null) {
                        getReport2TechLsDAO().deleteReport2TechLs(rep2Prod.getId());
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
    public List<Report2ProdView> findReport2ProdView4Report(Long reportId) throws FindException {
        if (reportId == null) {
            return null;
        }
        try {
            return getReport2ProdDAO().findReport2ProdView4Report(reportId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    /*
     * Liefert alle Report2Prod- Datensätze für eine Report
     */
    private List<Report2Prod> findReport2Prod4Report(Long reportId) throws FindException {
        if (reportId == null) {
            return null;
        }
        try {
            Report2Prod example = new Report2Prod();
            example.setReportId(reportId);

            return getReport2ProdDAO().queryByExample(example, Report2Prod.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Report2TechLsView> findReport2TechLsView4Report(Long reportId) throws FindException {
        List<Report2TechLsView> result = new ArrayList<Report2TechLsView>();

        try {
            List<Report2Prod> report2Prods = findReport2Prod4Report(reportId);
            if (CollectionTools.isNotEmpty(report2Prods)) {
                for (Report2Prod rep2Prod : report2Prods) {
                    if (rep2Prod != null) {
                        Report2TechLs ex = new Report2TechLs();
                        ex.setReport2ProdId(rep2Prod.getId());

                        List<Report2TechLs> x = getReport2TechLsDAO()
                                .queryByExample(ex, Report2TechLs.class);
                        if (CollectionTools.isNotEmpty(x)) {
                            for (Report2TechLs rep2TechLs : x) {
                                if (rep2TechLs != null) {
                                    Report2TechLsView view = new Report2TechLsView();

                                    // Report-Daten setzen
                                    ReportService rs = (ReportService) getReportService(ReportService.class);
                                    Report rep = rs.findReport(reportId);
                                    view.setReportName(rep.getName());
                                    view.setReportId(reportId);

                                    // Produkt-Daten setzen
                                    ProduktService ps = (ProduktService) getCCService(ProduktService.class);
                                    Produkt produkt = ps.findProdukt(rep2Prod.getProduktId());
                                    view.setProduktId(rep2Prod.getProduktId());
                                    view.setProduktName(produkt.getAnschlussart());

                                    // Daten der techn. Leistung setzen
                                    CCLeistungsService ls = (CCLeistungsService) getCCService(CCLeistungsService.class);
                                    TechLeistung techLs = ls.findTechLeistung(rep2TechLs.getTechLsId());
                                    view.setTechLsId(rep2TechLs.getTechLsId());
                                    view.setTechLsName(techLs.getName());

                                    result.add(view);
                                }
                            }
                        }
                    }
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
    public List<Produkt> findProdukte4Report(Long reportId) throws FindException {
        List<Produkt> result = new ArrayList<Produkt>();

        try {
            List<Report2Prod> report2Prods = findReport2Prod4Report(reportId);
            if (CollectionTools.isNotEmpty(report2Prods)) {
                for (Report2Prod rep2Prod : report2Prods) {
                    if (rep2Prod != null) {
                        // Produkt-Daten setzen
                        ProduktService ps = (ProduktService) getCCService(ProduktService.class);
                        Produkt produkt = ps.findProdukt(rep2Prod.getProduktId());
                        result.add(produkt);
                    }
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
    public List<ReportTemplate> findAllReportTemplates4Report(Long reportId) throws FindException {
        try {
            ReportTemplate example = new ReportTemplate();
            example.setReportId(reportId);

            return getReportTemplateDAO().queryByExample(example, ReportTemplate.class, new String[] { ReportTemplate.ID }, null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void saveReport(Report toSave) throws StoreException {
        if (toSave == null) { throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE); }
        try {
            ValidationException ve = new ValidationException(toSave, "Report");
            getReportValidator().validate(toSave, ve);
            if (ve.hasErrors()) {
                throw ve;
            }

            getReportDAO().store(toSave);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Report> findReports() throws FindException {
        try {
            return getReportDAO().findActiveReports();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void saveNewReportTemplate(ReportTemplate toSave) throws StoreException {
        if (toSave == null) { throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE); }
        try {
            // Lege für jeden Report ein neues Verzeichnis an
            String pathName = getPathName4Template(toSave);
            // Speichere Template auf Server
            String newFilename = StringUtils.substringAfterLast(toSave.getFilenameOrig(), File.separator);
            newFilename = StringUtils.substringBeforeLast(newFilename, ".")
                    + "-" + Calendar.getInstance().getTimeInMillis()
                    + "." + StringUtils.substringAfterLast(newFilename, ".");

            FileTools.copyFile(new File(toSave.getFilenameOrig()), new File(pathName + newFilename), false);

            // Setze neue Dateinamen
            toSave.setFilename(newFilename);
            toSave.setFilenameOrig(StringUtils.substringAfterLast(toSave.getFilenameOrig(), File.separator));

            // Beende aktuelles Template
            List<ReportTemplate> templates = findAllReportTemplates4Report(toSave.getReportId());
            Date neu = DateTools.changeDate(toSave.getGueltigVon(), Calendar.DAY_OF_MONTH, -1);
            if (CollectionTools.isNotEmpty(templates)) {
                for (int i = 0; i < templates.size(); i++) {
                    ReportTemplate temp = templates.get(i);
                    if ((temp.getGueltigBis() == null) || temp.getGueltigBis().after(toSave.getGueltigVon())) {
                        temp.setGueltigBis(neu);
                        saveReportTemplate(temp);
                    }
                }
            }

            // Speichere aktuelles Template
            getReportTemplateDAO().store(toSave);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    /*
     * Funktion ermittelt Pfadnamen der Report-Templates
     */
    private String getPathName4Template(ReportTemplate template) throws RuntimeException {
        try {
            // Ermittle Standard-Templatepfad
            String templatePath = reportPathTemplates + template.getReportId() + File.separator;

            // Erstelle neues Verzeichnis
            File path = new File(templatePath);
            if (!path.isDirectory()) {
                boolean created = path.mkdir();
                if (!created) {
                    throw new IOException("Directory not created: " + templatePath);
                }
            }

            return templatePath;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException("Fehler bei der Ermittlung des Pfadnamens für ein Template", e);
        }
    }

    @Override
    public void saveReportTemplate(ReportTemplate toSave) throws StoreException {
        if (toSave == null) { throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE); }
        try {
            //Speichere aktuelles Template
            getReportTemplateDAO().store(toSave);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<ServiceCommand> findReportCommands() throws FindException {
        try {
            ChainService chainService = (ChainService) getCCService(ChainService.class);
            return chainService.findServiceCommands(ServiceCommand.COMMAND_TYPE_REPORT);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void downloadFile(ReportTemplate template, String path) throws ReportException {
        try {
            String tempFile = reportPathTemplates + template.getReportId() + File.separator + template.getFilename();

            FileTools.copyFile(new File(tempFile), new File(path), false);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ReportException("Fehler beim Download des Reports", e);
        }
    }

    @Override
    public List<TxtBausteinGruppe> findAllTxtBausteinGruppen() throws FindException {
        try {
            return getTxtBausteinGruppeDAO().findAll(TxtBausteinGruppe.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void saveTxtBausteinGruppe(TxtBausteinGruppe toStore) throws StoreException {
        try {
            getTxtBausteinGruppeDAO().store(toStore);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void deleteTxtBausteinGruppeById(Long id) throws DeleteException {
        if (id == null) {
            throw new DeleteException(DeleteException.INVALID_PARAMETERS);
        }
        try {
            getTxtBausteinGruppeDAO().deleteById(id);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new DeleteException(DeleteException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<TxtBausteinGruppe> findTxtBausteinGruppen4TxtBaustein(Long bausteinIdOrig) throws FindException {
        if (bausteinIdOrig == null) {
            throw new FindException(FindException.EMPTY_FIND_PARAMETER);
        }
        try {
            return getTxtBausteinGruppeDAO().findAll4TxtBaustein(bausteinIdOrig);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void deleteTxtBausteine2GruppeZuordnung(Long bausteinGruppeId) throws DeleteException {
        if (bausteinGruppeId == null) {
            throw new DeleteException(DeleteException.INVALID_PARAMETERS);
        }
        try {
            getTxtBaustein2GruppeDAO().deleteByBausteinGruppeId(bausteinGruppeId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new DeleteException(DeleteException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void saveTxtBausteine2GruppeZuordnung(Long bausteinGruppeId, List<TxtBaustein> bausteine) throws StoreException {
        if ((bausteine == null) || (bausteinGruppeId == null)) {
            throw new StoreException(StoreException._UNEXPECTED_ERROR);
        }
        try {
            // Lösche bestehende Zuordnung
            deleteTxtBausteine2GruppeZuordnung(bausteinGruppeId);

            // Aktuelle Zuordnung speichern
            if (CollectionTools.isNotEmpty(bausteine)) {
                for (TxtBaustein baustein : bausteine) {
                    if (baustein != null) {
                        TxtBaustein2Gruppe toStore = new TxtBaustein2Gruppe();
                        toStore.setTxtBausteinGruppeId(bausteinGruppeId);
                        toStore.setTxtBausteinIdOrig(baustein.getIdOrig());

                        saveTxtBaustein2Gruppe(toStore);
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void saveTxtBaustein2Gruppe(TxtBaustein2Gruppe toStore) throws StoreException {
        try {
            getTxtBaustein2GruppeDAO().store(toStore);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void deleteAllTxtBausteinGruppen4Report(Long reportId) throws DeleteException {
        if (reportId == null) {
            throw new DeleteException(DeleteException.INVALID_PARAMETERS);
        }
        try {
            getReport2TxtBausteinGruppeDAO().deleteAllTxtBausteinGruppen4Report(reportId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new DeleteException(DeleteException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Boolean testReferences4TxtBausteinGruppe(Long gruppeId) throws FindException {
        try {
            // Teste Referenzen auf Reports
            Report2TxtBausteinGruppe example = new Report2TxtBausteinGruppe();
            example.setTxtBausteinGruppeId(gruppeId);

            List<Report2TxtBausteinGruppe> list = getReport2TxtBausteinGruppeDAO().queryByExample(example, Report2TxtBausteinGruppe.class);
            if (CollectionTools.isNotEmpty(list)) {
                return Boolean.TRUE;
            }

            // Teste Referenzen auf Text-Bausteine
            TxtBaustein2Gruppe example2 = new TxtBaustein2Gruppe();
            example2.setTxtBausteinGruppeId(gruppeId);

            List<TxtBaustein2Gruppe> list2 = getTxtBaustein2GruppeDAO().queryByExample(example2, TxtBaustein2Gruppe.class);

            if (CollectionTools.isNotEmpty(list2)) {
                return Boolean.TRUE;
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
        return Boolean.FALSE;
    }

    @Override
    public List<Report2UserRole> findUserRoles4Report(Long reportId) throws FindException {
        try {
            Report2UserRole example = new Report2UserRole();
            example.setReportId(reportId);

            return getReport2UserRoleDAO().queryByExample(example, Report2UserRole.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void saveRoles4Report(Long reportId, List<AKRole> roles) throws StoreException {
        if (reportId == null) {
            return;
        }
        try {
            // Lösche bestehende Zuordnungen
            deleteUser4Report(reportId);

            if (CollectionTools.isEmpty(roles)) {
                return;
            }
            // Speichere neue Zuordnung
            for (int i = 0; i < roles.size(); i++) {
                AKRole role = roles.get(i);
                if (role != null) {
                    Report2UserRole rep2User = new Report2UserRole();
                    rep2User.setReportId(reportId);
                    rep2User.setRoleId(role.getId());
                    getReport2UserRoleDAO().store(rep2User);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    /**
     * Funktion löscht alle Report-Benutzer-Zuordnungen für einen best. Report
     */
    private void deleteUser4Report(Long reportId) throws DeleteException {
        if (reportId == null) {
            throw new DeleteException(DeleteException.INVALID_PARAMETERS);
        }
        try {
            getReport2UserRoleDAO().deleteRoles4Report(reportId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new DeleteException(DeleteException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<ReportGruppe> findAllReportGroups() throws FindException {
        try {
            return getReportDAO().findAll(ReportGruppe.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Boolean testReferences4ReportGroup(Long groupId) throws FindException {
        try {
            // Teste Referenzen auf Reports
            Report example = new Report();
            example.setReportGruppeId(groupId);

            List<Report> list = getReportDAO().queryByExample(example, Report.class);
            if (CollectionTools.isNotEmpty(list)) {
                return Boolean.TRUE;
            }
            else {
                return Boolean.FALSE;
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void saveReportGroup(ReportGruppe toStore) throws StoreException {
        try {
            getReportGruppeDAO().store(toStore);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void deleteReportGroupById(Long groupId) throws DeleteException {
        if (groupId == null) {
            throw new DeleteException(DeleteException.INVALID_PARAMETERS);
        }
        try {
            getReportGruppeDAO().deleteById(groupId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new DeleteException(DeleteException._UNEXPECTED_ERROR, e);
        }
    }

    public Report2UserRoleDAO getReport2UserRoleDAO() {
        return report2UserRoleDAO;
    }

    public void setReport2UserRoleDAO(Report2UserRoleDAO report2UserRoleDAO) {
        this.report2UserRoleDAO = report2UserRoleDAO;
    }

    public Report2ProdDAO getReport2ProdDAO() {
        return report2ProdDAO;
    }

    public void setReport2ProdDAO(Report2ProdDAO report2ProdDAO) {
        this.report2ProdDAO = report2ProdDAO;
    }

    public Report2ProdStatiDAO getReport2ProdStatiDAO() {
        return report2ProdStatiDAO;
    }

    public void setReport2ProdStatiDAO(Report2ProdStatiDAO report2ProdStatiDAO) {
        this.report2ProdStatiDAO = report2ProdStatiDAO;
    }

    public Report2TxtBausteinGruppeDAO getReport2TxtBausteinGruppeDAO() {
        return report2TxtBausteinGruppeDAO;
    }

    public void setReport2TxtBausteinGruppeDAO(Report2TxtBausteinGruppeDAO report2TxtBausteinGruppeDAO) {
        this.report2TxtBausteinGruppeDAO = report2TxtBausteinGruppeDAO;
    }

    public ReportDAO getReportDAO() {
        return reportDAO;
    }

    public void setReportDAO(ReportDAO reportDAO) {
        this.reportDAO = reportDAO;
    }

    public ReportTemplateDAO getReportTemplateDAO() {
        return reportTemplateDAO;
    }

    public void setReportTemplateDAO(ReportTemplateDAO reportTemplateDAO) {
        this.reportTemplateDAO = reportTemplateDAO;
    }

    public TxtBaustein2GruppeDAO getTxtBaustein2GruppeDAO() {
        return txtBaustein2GruppeDAO;
    }

    public void setTxtBaustein2GruppeDAO(TxtBaustein2GruppeDAO txtBaustein2GruppeDAO) {
        this.txtBaustein2GruppeDAO = txtBaustein2GruppeDAO;
    }

    public TxtBausteinDAO getTxtBausteinDAO() {
        return txtBausteinDAO;
    }

    public void setTxtBausteinDAO(TxtBausteinDAO txtBausteinDAO) {
        this.txtBausteinDAO = txtBausteinDAO;
    }

    public TxtBausteinGruppeDAO getTxtBausteinGruppeDAO() {
        return txtBausteinGruppeDAO;
    }

    public void setTxtBausteinGruppeDAO(TxtBausteinGruppeDAO txtBausteinGruppeDAO) {
        this.txtBausteinGruppeDAO = txtBausteinGruppeDAO;
    }

    public Report2TechLsDAO getReport2TechLsDAO() {
        return report2TechLsDAO;
    }

    public void setReport2TechLsDAO(Report2TechLsDAO report2TechLsDAO) {
        this.report2TechLsDAO = report2TechLsDAO;
    }

    public ReportGruppeDAO getReportGruppeDAO() {
        return reportGruppeDAO;
    }

    public void setReportGruppeDAO(ReportGruppeDAO reportGroupDAO) {
        this.reportGruppeDAO = reportGroupDAO;
    }

    public ReportReasonDAO getReportReasonDAO() {
        return reportReasonDAO;
    }

    public void setReportReasonDAO(ReportReasonDAO reportReasonDAO) {
        this.reportReasonDAO = reportReasonDAO;
    }

    public ReportValidator getReportValidator() {
        return reportValidator;
    }

    public void setReportValidator(ReportValidator reportValidator) {
        this.reportValidator = reportValidator;
    }

    public void setReportPathTemplates(String reportPathTemplates) {
        this.reportPathTemplates = reportPathTemplates;
    }

}
