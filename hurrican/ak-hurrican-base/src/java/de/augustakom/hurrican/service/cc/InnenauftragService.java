/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.01.2007 14:11:12
 */
package de.augustakom.hurrican.service.cc;

import java.io.*;
import java.util.*;
import javax.annotation.*;
import net.sf.jasperreports.engine.JasperPrint;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.tools.reports.AKReportException;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.model.cc.Lager;
import de.augustakom.hurrican.model.cc.innenauftrag.IA;
import de.augustakom.hurrican.model.cc.innenauftrag.IABudget;
import de.augustakom.hurrican.model.cc.innenauftrag.IAMaterial;
import de.augustakom.hurrican.model.cc.innenauftrag.IAMaterialEntnahme;
import de.augustakom.hurrican.model.cc.innenauftrag.IAMaterialEntnahmeArtikel;
import de.augustakom.hurrican.model.cc.innenauftrag.IaLevel1;
import de.augustakom.hurrican.model.cc.innenauftrag.IaLevel3;
import de.augustakom.hurrican.model.cc.innenauftrag.IaLevel5;
import de.augustakom.hurrican.model.shared.view.InnenauftragQuery;
import de.augustakom.hurrican.model.shared.view.InnenauftragView;
import de.augustakom.hurrican.service.base.exceptions.DeleteException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;


/**
 * Service-Definition fuer die Verwaltung von Innenauftraegen. <br> Die Innenauftraege werden fuer die Realisierung von
 * hochpreisigen Produkten (z.B. Connect, PMXer) benoetigt. Zu einem Innenauftrag wird in SAP ein Budget eingestellt.
 * Jede Investition (Material), die fuer solch einen Auftrag anfaellt, wird dem IA zugeordnet.
 *
 *
 */
 public interface InnenauftragService extends ICCService {

    /**
     * Legt einen neuen Innenauftrag fuer den Auftrag mit ID <code>auftragId</code> an.
     *
     * @param auftragId     Auftrags-ID
     * @param iaNummer      Kennzeichen fuer den Innenauftrag
     * @param projectLeadId Projektleiter-ID für den Innenauftrag
     * @param kostenstelle  Kostenstelle für den Innenauftrag
     * @throws StoreException      wenn bei der Anlage ein Fehler auftritt.
     * @throws ValidationException wenn die IA-Nummer ein ungueltiges Format besitzt.
     *
     */
     IA createIA(Long auftragId, String iaNummer, Long projectLeadId, String kostenstelle) throws StoreException, ValidationException;

    /**
     * Speichert den angegebenen Innenauftrag ohne Pruefung ab.
     *
     * @param toSave zu speicherndes Objekt
     * @throws StoreException wenn beim Speichern ein Fehler auftritt.
     *
     */
     void saveIA(IA toSave) throws StoreException;

    /**
     * Ermittelt den Innenauftrag zu einem Auftrag.
     *
     * @param auftragId Auftrags-ID
     * @return Objekt vom Typ <code>IA</code>.
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt oder mehr als ein Innenauftrag zu dem Auftrag
     *                       existiert.
     *
     */
     IA findIA4Auftrag(Long auftragId) throws FindException;

    /**
     * Ermittelt den Innenauftrag zu einem Rangierungsauftrag.
     *
     * @param raId ID des Rangierungs-Auftrags
     * @return Objekt vom Typ <code>IA</code>.
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     *
     */
     IA findIA4RangierungsAuftrag(Long raId) throws FindException;

    /**
     * Ermittelt den Innenauftrag ueber dessen Id.
     *
     * @param iaId ID des gesuchten Innenauftrags
     * @return Objekt vom Typ <code>IA</code>.
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     *
     */
     IA findIA(Long iaId) throws FindException;

    /**
     * Ermittelt Innenauftraege (und interne Auftraege) ueber das Query-Objekt und gibt relevante Informationen zu den
     * zugehoerigen Auftraegen zurueck.
     *
     * @param query Query-Objekt mit den Suchparametern
     * @return Liste mit Objekten des Typs <code>InnenauftragView</code>
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     *
     */
     List<InnenauftragView> findIAViews(InnenauftragQuery query) throws FindException;

    /**
     * Speichert das angegebene Budget ab.
     *
     * @param toSave    zu speicherndes Budget
     * @param sessionId Id der User-Session
     * @throws StoreException wenn beim Speichern ein Fehler auftritt.
     *
     */
     void saveBudget(IABudget toSave, Long sessionId) throws StoreException;

    /**
     * Ermittelt alle Budgets, die einem bestimmten Innenauftrag zugeordnet sind. Die Ermittlung erfolgt aufsteigend
     * nach der ID.
     *
     * @param iaId ID des Innenauftrags
     * @return Liste mit Objekten des Typs <code>IABudget</code>
     * @throws FindException wenn bei der Ermittlung der Daten ein Fehler auftritt.
     *
     */
     List<IABudget> findBudgets4IA(Long iaId) throws FindException;

    /**
     * Ermittelt das Budget mit der angegebenen ID.
     *
     * @param budgetId Budget-ID
     * @return Objekt vom Typ <code>IABudget</code> oder <code>null</code>
     * @throws FindException wenn bei der Ermittlung der Daten ein Fehler auftritt.
     *
     */
     IABudget findBudget(Long budgetId) throws FindException;

    /**
     * Ermittelt, ob zu einem Auftrag noch ein offenes Budget vorhanden ist.
     *
     * @param auftragId ID des Auftrags, zu dem geprueft werden soll, ob ein offenes Budget existiert.
     * @return true wenn der Auftrag noch ein offenes Budget besitzt
     * @throws FindException wenn bei der Ermittlung der Daten ein Fehler auftritt.
     *
     */
     boolean hasOpenBudget(Long auftragId) throws FindException;

    /**
     * Speichert die angegebene Materialentnahme.
     *
     * @param toSave    zu speicherndes Objekt
     * @param sessionId Session-ID des Users
     * @throws StoreException      wenn beim Speichern ein Fehler auftritt.
     * @throws ValidationException wenn das zu speichernde Objekt ungueltige Werte besitzt.
     *
     */
     void saveMaterialEntnahme(IAMaterialEntnahme toSave, Long sessionId) throws StoreException;

    /**
     * Ermittelt alle Materialentnahmen zu einerm bestimmten Budget.
     *
     * @param budgetId die Budget-ID
     * @return Liste mit Objekten des Typs <code>IAMaterialEntnahme</code>
     * @throws FindException wenn bei der Ermittlung der Daten ein Fehler auftritt.
     *
     */
     List<IAMaterialEntnahme> findMaterialEntnahmen4Budget(Long budgetId) throws FindException;

    /**
     * Ermittelt eine Materialentnahme ueber deren ID.
     *
     * @param id ID der gesuchten Materialentnahme.
     * @return Objekt vom Typ <code>IAMaterialEntnahme</code> oder <code>null</code>.
     * @throws FindException wenn bei der Ermittlung der Daten ein Fehler auftritt.
     *
     */
     IAMaterialEntnahme findMaterialEntnahme(Long id) throws FindException;

    /**
     * Speichert das angegebene Objekt.
     *
     * @param toSave zu speicherndes Objekt.
     * @throws StoreException wenn beim Speichern ein Fehler auftritt.
     *
     */
     void saveMaterialEntnahmeArtikel(IAMaterialEntnahmeArtikel toSave) throws StoreException;

    /**
     * Ermittelt alle Artikel, die einer Materialentnahme zugeordnet sind.
     *
     * @param matEntnahmeId ID der Materialentnahme
     * @return Liste mit Objekten des Typs <code>IAMaterialEntnahmeArtikel</code>
     * @throws FindException wenn bei der Ermittlung der Daten ein Fehler auftritt.
     *
     */
     List<IAMaterialEntnahmeArtikel> findArtikel4MatEntnahme(Long matEntnahmeId) throws FindException;

    /**
     * Ermittelt die Gesamtliste der angelegten Materialien.
     *
     * @return Liste mit Objekten des Typs <code>IAMaterialliste</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     *
     */
     List<IAMaterial> findIAMaterialien() throws FindException;

    /**
     * Ermittelt zu einer Materialnummer das Material. Es wird davon ausgegangen, dass die Materialnummer immer
     * eindeutig ist. Werden dennoch mehrere Objekte gefunden, wird eine Exception generiert.
     *
     * @param materialNr Materialnummer
     * @return Objekt vom Typ <code>IAMaterial</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     *
     */
     IAMaterial findMaterial(String materialNr) throws FindException;

    /**
     * Ermittelt alle vorhandenen Lager.
     *
     * @return Liste mit Objekten des Typs <code>Lager</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     *
     */
     List<Lager> findLager() throws FindException;

    /**
     * Ermittelt ein Lager ueber dessen ID.
     *
     * @param lagerId ID des gesuchten Lagers.
     * @return Objekt vom Typ <code>Lager</code> oder <code>null</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     *
     */
     Lager findLager(Long lagerId) throws FindException;

    /**
     * Erstellt den Budget-Report fuer das angegebene Budget (=Innenauftrag).
     *
     * @param budgetId Budget-ID
     * @return JasperPrint-Objekt
     * @throws FindException     wenn bei der Datenermittlung ein Fehler auftritt.
     * @throws AKReportException wenn der Report nicht erzeugt werden konnte.
     *
     */
     JasperPrint printBudget(Long budgetId) throws FindException, AKReportException;

    /**
     * Erstellt den Materialentnahme-Report.
     *
     * @param matEntnahmeId ID der zu druckenden Materialentnahme
     * @return JasperPrint-Objekt
     * @throws FindException     wenn bei der Datenermittlung ein Fehler auftritt.
     * @throws AKReportException wenn der Report nicht erzeugt werden konnte.
     *
     */
     JasperPrint printMaterialentnahme(Long matEntnahmeId) throws FindException, AKReportException;

    /**
     * Importiert die SAP-Materialliste. <br> Wichtig: vor dem Import wird alle aktuellen Datensaetze aus der
     * entsprechenden Tabelle geloescht!
     *
     * @param file das zu importierende File. Als Trennzeichen wird ";" erwartet.
     * @return Anzahl der importierten Materialien
     * @throws StoreException wenn die Materialliste nicht eingelesen werden konnte.
     *
     */
     int importMaterialliste(File file) throws StoreException;


    /**
     * Ermittelt alle {@link IaLevel1} Entitaeten.
     * @return
     * @throws FindException
     */
     List<IaLevel1> findIaLevels() throws FindException;

    @Nonnull
    List<IaLevel1> findIaLevelsForUser(final @Nonnull AKUser akUser);


    /**
     * Speichert den angegebenen {@link IaLevel1}.
     * @param toSave
     * @throws StoreException
     */
     void saveIaLevel(IaLevel1 toSave) throws StoreException;

    /**
     * Loescht den angegebenen {@link IaLevel1}
     * @param toDelete
     * @throws DeleteException
     */
     void deleteIaLevel(IaLevel1 toDelete) throws DeleteException;

    /**
     * Sucht das passende Projekt (Standardprojekt oder Grossprojekt) fuer den gegebenen Innenauftrag. Diese Methode
     * macht nur fuer den Bereich Produktion und Dokumentation (Lock Mode) Sinn.
     * @param innenauftrag der Innenauftrag, fuer den n Budgets schon eingestellt sind
     * @param budget das aktuelle (noch nicht persistierte) Budget, welches zu den restlichen (persistierten)
     *               hinzuaddiert werden muss
     */
    IaLevel3 findLevel3Projekt4PD(IA innenauftrag, float budget) throws FindException;

    /**
     * Sucht fuer Level 3 das zugehoerige Grossprojekt. Diese Methode macht nur fuer den Bereich Produktion und
     * Dokumentation (Lock Mode) Sinn.
     */
    IaLevel3 findLevel3Grossprojekt4PD() throws FindException;

    /**
     * Sucht in einer Liste von {@link IaLevel5} nach einem Element mit dem angegebenen Namen
     *
     * @param iaLevel5s         zu durchsuchende List
     * @param taifunProduktName zu suchender Name
     * @return das gefunde {@link IaLevel5} - Element, andernfalls {@code null}
     */
    @Nullable
    IaLevel5 getLevel5ByTaifunProduktName(@Nonnull final List<IaLevel5> iaLevel5s, final @Nullable String taifunProduktName);

    String fetchInnenAuftragKostenstelle(Long auftragId);
}


