/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.02.2007 12:16:17
 */
package de.augustakom.hurrican.service.reporting;

import java.util.*;

import de.augustakom.authentication.model.AKRole;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.command.ServiceCommand;
import de.augustakom.hurrican.model.reporting.Report;
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

/**
 * Interface fuer Report-Config-Service. Service beinhaltet ueberwiegend Funktionen fuer die Konfiguration der Reports.
 *
 *
 */

public interface ReportConfigService extends IReportService {

    public static final String REPORT_TYPE_JASPER = "Jasper";

    /**
     * Funktion speichert ein ReportTemplate-Objekt
     *
     * @param toSave zu speicherndes Objekt
     * @throws StoreException
     *
     */
    public void saveReportTemplate(ReportTemplate toSave) throws StoreException;

    /**
     * Funktion speichert ein neues ReportTemplate-Objekt, d.h. es wird zusaetzlich das Template auf den Server geladen
     * und das aktuelle Template beendet
     *
     * @param toSave zu speicherndes Objekt
     * @throws StoreException
     *
     */
    public void saveNewReportTemplate(ReportTemplate toSave) throws StoreException;

    /**
     * Funktion liefert alle Report-Templates fuer einen Report.
     *
     * @param reportId Id des Reports
     * @return Liste aller gefundenen ReportTemplates
     * @throws FindException
     *
     */
    public List<ReportTemplate> findAllReportTemplates4Report(Long reportId) throws FindException;

    /**
     * Funktion liefert alle Report2ProdViews fuer einen Report
     *
     * @param reportId ID des Reports
     * @return Liste mit Report2ProdViews
     * @throws FindException
     *
     */
    public List<Report2ProdView> findReport2ProdView4Report(Long reportId) throws FindException;

    /**
     * Funktion liefert alle Report2TechLsViews fuer einen Report
     *
     * @param reportId Id des Reports
     * @return Liste mit Report2TechLsViews
     * @throws FindException
     *
     */
    public List<Report2TechLsView> findReport2TechLsView4Report(Long reportId) throws FindException;

    /**
     * Funktion liefert alle zugeordneten Produkte zu einem Report
     *
     * @param reportId Id des Reports
     * @return Liste mit dem Report zugeordneten Produkten
     * @throws FindException
     *
     */
    public List<Produkt> findProdukte4Report(Long reportId) throws FindException;

    /**
     * Funktion speichert eine Liste mit Report-Produkt Zuordnungen
     *
     * @param views    zu speichernde Views
     * @param reportId Id des Reports, dem die Produkte zugeordnet werden sollen
     * @throws StoreException
     *
     */
    public void saveReport2ProdView(List<Report2ProdView> views, Long reportId) throws StoreException;

    /**
     * Funktion speichert eine Liste mit Report zu Techn. Leistung Zuordnungen
     *
     * @param views    zu speichernde View
     * @param reportId Id des Reports, dem die techn. Leistungen zugeordnet werden sollen.
     * @throws StoreException
     *
     */
    public void saveReport2TechLsView(List<Report2TechLsView> views, Long reportId) throws StoreException;

    /**
     * Speichert einen TextBaustein
     *
     * @param toSave zu speicherndes Objekt
     * @throws StoreException
     *
     */
    public void saveTxtBaustein(TxtBaustein toSave) throws StoreException;

    /**
     * Funktion speichert die Zuordnung zwischen Text-Baustein-Gruppen und Reports
     *
     * @param reportId Id des Reports
     * @param gruppen  Liste mit Text-Baustein-Gruppen
     * @throws StoreException
     *
     */
    public void saveTxtBausteinGruppen4Report(Long reportId, List<TxtBausteinGruppe> gruppen) throws StoreException;

    /**
     * Loescht einen bestimmten TextBaustein anhand der ID
     *
     * @param id Id des zu loeschenden Text-Bausteins
     * @throws DeleteException
     *
     */
    public void deleteTxtBausteinById(Long id) throws DeleteException;

    /**
     * Liefert alle Txt-Bausteine
     *
     * @return Liste mit allen verfuegbaren Textbausteinen
     * @throws FindException
     *
     */
    public List<TxtBaustein> findAllTxtBausteine() throws FindException;

    /**
     * Liefert fuer jeden Txt-Baustein die letzte Historisierung
     *
     * @return Liste mit Text-Bausteinen
     * @throws FindException
     *
     */
    public List<TxtBaustein> findAllNewTxtBausteine() throws FindException;

    /**
     * Liefert fuer eine ID_Orig alle historisierten Txt-Bausteine
     *
     * @return liste mit Textbausteinen
     * @throws FindException
     *
     */
    public List<TxtBaustein> findAllTxtBausteine4IdOrig(Long idOrig) throws FindException;

    /**
     * Liefert alle gueltigen Text-Bausteine
     *
     * @return Liste mit gueltigen Text-Bausteinen
     * @throws FindException
     *
     */
    public List<TxtBaustein> findAllValidTxtBausteine() throws FindException;

    /**
     * Funktion speichert ein Report-Objekt
     *
     * @param toSave zu speicherndes Objekt
     * @throws StoreException
     *
     */
    public void saveReport(Report toSave) throws StoreException;

    /**
     * Funktion liefert alle aktiven Report-Vorlagen
     *
     * @return Liste mit Report-Objekten
     * @throws FindException
     *
     */
    public List<Report> findReports() throws FindException;

    /**
     * Funktion liefert alle Report-Commands
     *
     * @return Liste mit Report-Commands
     * @throws FindException
     *
     */
    public List<ServiceCommand> findReportCommands() throws FindException;

    /**
     * Funktion kopiert eine Report-Vorlage nach path
     *
     * @param template Vorlage, die kopiert werden soll
     * @param path     Zielpfad
     * @throws ReportException
     *
     */
    public void downloadFile(ReportTemplate template, String path) throws ReportException;

    /**
     * Funktion liefert alle Text-Baustein-Gruppen
     *
     * @return Liste mit Textbaustein-Gruppen
     * @throws FindException
     *
     */
    public List<TxtBausteinGruppe> findAllTxtBausteinGruppen() throws FindException;

    /**
     * Funktion speichert Text-Baustein-Gruppe
     *
     * @param toStore zu speicherndes Objekt
     * @throws StoreException
     *
     */
    public void saveTxtBausteinGruppe(TxtBausteinGruppe toStore) throws StoreException;

    /**
     * Funktion loescht eine TextBaustein-Gruppe anhand der Id
     *
     * @param id Id der Textbaustein-Gruppe, die geloescht werden soll
     * @throws DeleteException
     *
     */
    public void deleteTxtBausteinGruppeById(Long id) throws DeleteException;

    /**
     * Funktion liefert die Id aller Text-Baustein-Gruppen, denen ein Text-Baustein zugeordnet ist.
     *
     * @param bausteinId Id des Textbausteins fuer den eine Zuordnung gesucht wird
     * @return Liste mit zugeordneten Textbaustein-Gruppen
     * @throws FindException
     *
     */
    public List<TxtBausteinGruppe> findTxtBausteinGruppen4TxtBaustein(Long bausteinIdOrig) throws FindException;

    /**
     * Funktion speichert die Zuordnung von mehreren Text-Bausteinen zu einer Gruppe. Zuordnungen zu dieser Gruppe
     * werden zuerst geloescht.
     *
     * @param bausteinGruppeId Id der Textbaustein-Gruppe
     * @param bausteine        Textbausteine, die der Gruppe zugeordnet werden sollen
     * @throws StoreException
     *
     */
    public void saveTxtBausteine2GruppeZuordnung(Long bausteinGruppeId, List<TxtBaustein> bausteine) throws StoreException;

    /**
     * Funktion speichert ein TxtBaustein2Gruppe-Objekt
     *
     * @param toStore zu speicherndes Objekt
     * @throws StoreException
     *
     */
    public void saveTxtBaustein2Gruppe(TxtBaustein2Gruppe toStore) throws StoreException;

    /**
     * Funktion loescht alle Zuordnungen von Textbaustein-Gruppen zu einem best. Report
     *
     * @param reportId Id des Reports
     * @throws DeleteException
     *
     */
    public void deleteAllTxtBausteinGruppen4Report(Long reportId) throws DeleteException;

    /**
     * Funktion sucht Referenzen auf eine best. Text-Baustein-Gruppe (Reports und Textbausteine). Noetig um eine Gruppe
     * loeschen zu koennen.
     *
     * @param gruppeId Id der Textbaustein-Gruppe
     * @return TRUE falls noch Referenzen vorhanden sind.
     * @throws FindException
     *
     */
    public Boolean testReferences4TxtBausteinGruppe(Long gruppeId) throws FindException;

    /**
     * Funktion loescht alle Zuordnungen von Text-Bausteinen zu einer best. Textbaustein-Gruppen
     *
     * @param bausteinGruppeId Id der Textbaustein-Gruppe
     * @throws DeleteException
     *
     */
    public void deleteTxtBausteine2GruppeZuordnung(Long bausteinGruppeId) throws DeleteException;


    /**
     * Funktion liefert alle Benutzer-Rollen fuer einen bestimmten Report
     *
     * @param reportId Id des Reports
     * @return Liste mit Report2UserRole-Objekten
     * @throws FindException
     *
     */
    public List<Report2UserRole> findUserRoles4Report(Long reportId) throws FindException;

    /**
     * Funktion speichert die UserRollen-Report-Zuordnung
     *
     * @param id    ID des Reports
     * @param roles Liste mit Benutzerrollen
     * @throws StoreException
     *
     */
    public void saveRoles4Report(Long id, List<AKRole> roles) throws StoreException;

    /**
     * Funktion liefert alle Reportgruppen
     *
     * @return Liste mit Reportgruppen
     * @throws FindException
     *
     */
    public List<ReportGruppe> findAllReportGroups() throws FindException;

    /**
     * Funktion testet ob der Report-Gruppe einzelne Reports zugeordnet sind. Noetig beim Loeschen einer Report-Gruppe.
     *
     * @param gruppeId
     * @return TRUE falls Referenzen vorhanden sind
     * @throws FindException
     *
     */
    public Boolean testReferences4ReportGroup(Long groupId) throws FindException;

    /**
     * Funktion speichert ein ReportGroup-Objekt
     *
     * @param toStore zu speicherndes Objekt
     * @throws StoreException
     *
     */
    public void saveReportGroup(ReportGruppe toStore) throws StoreException;

    /**
     * Funktion loescht eine Reportgruppe anhand der Id
     *
     * @param groupId Id der zu loeschenden Reportgruppe
     * @throws DeleteException
     *
     */
    public void deleteReportGroupById(Long groupId) throws DeleteException;

}

