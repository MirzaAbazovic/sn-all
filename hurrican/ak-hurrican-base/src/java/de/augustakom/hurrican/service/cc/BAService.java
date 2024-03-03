/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.07.2004 08:29:45
 */
package de.augustakom.hurrican.service.cc;

import java.util.*;
import javax.annotation.*;
import net.sf.jasperreports.engine.JasperPrint;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.common.tools.reports.AKReportException;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;
import de.augustakom.hurrican.model.cc.VerlaufAbteilungStatus;
import de.augustakom.hurrican.model.cc.VerlaufStatus;
import de.augustakom.hurrican.model.cc.temp.SelectAbteilung4BAModel;
import de.augustakom.hurrican.model.cc.view.AbstractBauauftragView;
import de.augustakom.hurrican.model.cc.view.CCAuftragIDsView;
import de.augustakom.hurrican.model.cc.view.ProjektierungsView;
import de.augustakom.hurrican.model.cc.view.SimpleVerlaufView;
import de.augustakom.hurrican.model.cc.view.TimeSlotHolder;
import de.augustakom.hurrican.model.cc.view.VerlaufAbteilungView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.RoleException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.mnet.esb.cdm.resource.workforceservice.v1.DeleteOrder;
import de.mnet.esb.cdm.resource.workforceservice.v1.WorkforceOrder;
import de.mnet.esb.cdm.resource.workforceservice.v1.WorkforceService;


/**
 * Service-Definition fuer die Verwaltung der elektronischen Bauauftraege.
 *
 *
 */
public interface BAService extends ICCService {

    final class TerminverschiebungException extends StoreException {
        public TerminverschiebungException(final String msg) {
            super(msg);
        }
    }

    /**
     * Laedt den TimeSlotHolder fuer einen Auftrag (aus verschiedenen Quellen, siehe TimeSlotHolder).
     *
     * @param auftragId auftragID
     * @return TimeSlotHolder
     * @throws FindException
     */
    @Nonnull TimeSlotHolder getTimeSlotHolder(Long auftragId) throws FindException;

    /**
     * Erstellt einen Verlaufs-Eintrag fuer einen Auftrag. <br>
     * Abhaengig von der Konfiguration zu Produkt/BA-Anlass wird der Bauauftrag auch gleich an die hinterlegten
     * Abteilungen weiter verteilt oder nur ein Eintrag fuer AM und Dispo/NP erstellt.
     *
     * @param parameterObject
     * @return a {@link Pair} out of {@link Verlauf} and {@link AKWarnings}, pair is never {@code null}
     * @throws StoreException wenn bei der Anlage des Verlaufs ein Fehler auftritt.
     * @throws FindException  wenn bei der Ermittlung der benoetigten Daten ein Fehler auftritt.
     */
    Pair<Verlauf, AKWarnings> createVerlauf(CreateVerlaufParameter parameterObject) throws StoreException, FindException;

    /**
     * @see {@link de.augustakom.hurrican.service.cc.BAService#createVerlauf(CreateVerlaufParameter)}
     * Ausfuehrung in eigener TX!
     */
    Pair<Verlauf, AKWarnings> createVerlaufNewTx(CreateVerlaufParameter parameterObject) throws StoreException, FindException;

    /**
     * Storniert den Verlauf mit der ID <code>verlaufId</code>. Zusaetzlich wird der Auftrags-Status zurueck gesetzt.
     * <br> Abhaengig von <code>sendMail</code> wird an die Dispo eine EMail zur Information gesendet.
     *
     * @param verlaufId ID des zu stornierenden Verlaufs
     * @param sendMail  Flag, ob an die Dispo eine EMail gesendet werden soll.
     * @param sessionId Session-ID des aktuellen Users.
     * @return Objekt vom Typ <code>AKWarnings</code> mit Warnungen, die bei der Bearbeitung aufgetreten sind oder
     * <code>null</code>.
     * @throws StoreException wenn bei der Stornierung ein Fehler auftritt.
     * @throws FindException  wenn bei der Ermittlung der benoetigten Daten ein Fehler auftritt.
     */
    AKWarnings verlaufStornieren(Long verlaufId, boolean sendMail, Long sessionId) throws StoreException, FindException;

    /**
     * Die Methode trennt einen Verlauf mit Sub-Auftraegen auf. Die in der Liste {@code orderIdsToRemove} angegebenen
     * Auftrag-IDs werden von dem angegebenen Verlauf entfernt und jeweils ein eigener Verlauf pro zu entfernendem
     * Auftrag angelegt.
     *
     * @param toSplit          der aufzutrennende Verlauf
     * @param orderIdsToRemove die Auftrag-IDs, die aus dem Verlauf heraus geloest werden sollen
     * @param sessionId        ID der User-Session
     * @return Objekt vom Typ <code>List&ltVerlauf&gt</code> mit allen extrahierten Verlaeufen (jeder extrahierte
     * Verlauf bekommt eine neu persistierte Verlauf Entität)
     * @throws StoreException
     */
    List<Verlauf> splitVerlauf(Verlauf toSplit, Set<Long> orderIdsToRemove, Long sessionId) throws StoreException;

    /**
     * Setzt den Verlauf mit der angegebenen ID auf 'beobachtet'.
     *
     * @param verlaufId Verlaufs-ID
     * @throws StoreException wenn beim Speichern ein Fehler auftritt.
     *
     */
    void observeProcess(Long verlaufId) throws StoreException;

    /**
     * Informiert die Dispo ueber eine Terminverschiebung.
     *
     * @param auftragId   ID des Auftrags, dessen Realisierungsdatum geaendert werden soll
     * @param newRealDate neues Realisierungsdatum fuer den BA
     * @param sessionId   Id der Session des Benutzers
     * @throws FindException wenn ein Fehler auftrat.
     */
    void infoDispoChangeRealDate(Long auftragId, Date newRealDate, Long sessionId) throws FindException;

    /**
     * Erstellt fuer den Auftrag mit der ID <code>auftragId</code> eine Projektierung. <br><br> Die Angabe von
     * <code>auftragIdAlt</code> ist NICHT zwingend! Wird die ID angegeben, wird in als Bemerkung fuer die Projektierung
     * die VerbindungsBezeichnung des 'alten' Auftrags aufgenommen. Dies ist z.B. bei Bandbreitenaenderungen wichtig.
     * <br>
     *
     * @param auftragId    ID des Auftrags, fuer den eine Projektierung erstellt werden soll
     * @param auftragIdAlt kann angegeben werden, wenn sich der Auftrag <code>auftragId</code> auf einen 'alten' Auftrag
     *                     bezieht (z.B. bei Bandbreitenaenderungen).
     * @param sessionId    Session-ID des aktuellen Users.
     * @throws StoreException wenn bei der Anlage der Projektierung ein Fehler auftritt.
     * @throws FindException  wenn bei der Ermittlung der benoetigten Daten ein Fehler auftritt.
     */
    void createProjektierung(Long auftragId, Long auftragIdAlt, Long sessionId,
            Set<Long> subAuftragsIds) throws StoreException, FindException;

    /**
     * Erstellt fuer den Auftrag mit der ID <code>auftragId</code> eine Projektierung fuer die zentrale Dispo. <br><br>
     * Die Angabe von <code>auftragIdAlt</code> ist NICHT zwingend! Wird die ID angegeben, wird in als Bemerkung fuer
     * die Projektierung die VerbindungsBezeichnung des 'alten' Auftrags aufgenommen. Dies ist z.B. bei
     * Bandbreitenaenderungen wichtig. <br>
     *
     * @param auftragId    ID des Auftrags, fuer den eine Projektierung erstellt werden soll
     * @param auftragIdAlt kann angegeben werden, wenn sich der Auftrag <code>auftragId</code> auf einen 'alten' Auftrag
     *                     bezieht (z.B. bei Bandbreitenaenderungen).
     * @param sessionId    Session-ID des aktuellen Users.
     * @throws StoreException wenn bei der Anlage der Projektierung ein Fehler auftritt.
     * @throws FindException  wenn bei der Ermittlung der benoetigten Daten ein Fehler auftritt.
     */
    void createProjektierungForZentraleDispo(Long auftragId, Long auftragIdAlt, Long sessionId,
            Set<Long> subAuftragsIds) throws StoreException, FindException;

    /**
     * Verteilt den Verlauf mit der ID <code>verlaufId</code> automatisch an die konfigurierten Abteilungen. <br> Die
     * Abteilungen AM und DISPO werden in dieser Methode NICHT mehr erstellt! Ihr Status wird lediglich auf 'in
     * Bearbeitung' gesetzt. Ausserdem wird das LaGesp-Flag der zugehoerigen Auftrags-Daten auf 'an Technik' gesetzt.
     *
     * @param verlaufId ID des uebergeordneten Verlaufs.
     * @param sessionId Session-ID des aktuellen Benutzers.
     * @return Liste mit den erstellten <code>VerlaufAbteilung</code> Objekten.
     * @throws StoreException wenn bei der Verteilung ein Fehler auftritt.
     * @throws RoleException  bei unzureichenden Berechtigungen
     */
    List<VerlaufAbteilung> dispoBAVerteilenAuto(Long verlaufId, Long sessionId)
            throws StoreException;

    /**
     * Verteilt den Verlauf mit der ID <code>verlaufId</code> an die angegebenen Abteilungen. <br> Sollten die
     * Abteilungen AM und DISPO angegeben sein, werden diese ignoriert! Der Status des AM- und Dispo-Verlaufs wird auf
     * 'in Bearbeitung' gestzt. Das LaGesp-Flag der zugehoerigen Auftrags-Daten wird auf 'an Technik' gesetzt.
     *
     * @param verlaufId            ID des uebergeordneten Verlaufs.
     * @param verlaufAbteilungId   ID des uebergeordneten VerlaufAbteilung.
     * @param abtIds               Model mit Abteilungen, die den Verlauf erhalten sollen
     * @param filename             Dateiname des Attachments
     * @param reportMailAttachment (optional) Installationsauftrag, der an einen externen Service-Provider per Mail
     *                             verschickt werden soll
     * @param sessionId            Session-ID des aktuellen Benutzers.
     * @return Liste mit den erstellten <code>VerlaufAbteilung</code> Objekten.
     * @throws StoreException wenn bei der Verteilung ein Fehler auftritt.
     * @throws RoleException  bei unzureichenden Berechtigungen
     */
    List<VerlaufAbteilung> dispoVerteilenManuell(Long verlaufId, Long verlaufAbteilungId,
            List<SelectAbteilung4BAModel> abtIds, List<Pair<byte[], String>> attachments, Long sessionId)
            throws StoreException;


    /**
     * Ermittelt die Abteilungen, die auf Grund der zum Bauauftrag gehoerenden Leistungen den Bauauftrag eigentlich
     * erhalten muessten, aber nicht in {@code abtIds} enthalten sind.
     *
     * @param verlaufId
     * @param abtIds    Abteilungen, an die der BA verteilt werden soll
     * @return Liste mit den Abteilung-IDs, die auf Grund der vom Bauauftrag zu realisierenden techn. Leistungen den BA
     * zusaetzlich(!) erhalten sollten.
     */
    Collection<Long> getMissingAbteilungIds(Long verlaufId, List<SelectAbteilung4BAModel> abtIds)
            throws FindException;


    /**
     * Verteilt den Verlauf an die Netzplanungen der uebergebenen Niederlassungen. Wird fuer die zentrale Dispo
     * benoetigt.
     *
     * @param verlaufId          ID des uebergeordneten Verlaufs.
     * @param parentVerlaufAbtId VerlaufAbteilung der zentralen Dispo
     * @param niederlassungsIds  Niederlassungen die die Netzplanungen uebernehmen
     * @return List der erstellten VerlaufAbteilung-Objekte
     * @throws StoreException           wenn bei der Verteilung ein Fehler auftritt.
     * @throws FindException            wenn bei der Verteilung ein Fehler auftritt.
     * @throws ServiceNotFoundException wenn bei der Verteilung ein Fehler auftritt.
     */
    List<VerlaufAbteilung> anNetzplanungenVerteilen(Long verlaufId,
            Long parentVerlaufAbtId, List<Long> niederlassungsIds, Long sessionId)
            throws StoreException, FindException, ServiceNotFoundException;

    /**
     * Versucht, den Verlauf mit der ID <code>verlaufId</code> zurueck zu rufen. (Es wird die Verteilung der Dispo
     * rueckgaengig gemacht.)
     *
     * @param verlaufId ID des Verlaufs, der zurueck gerufen werden soll
     * @param sessionId Id der Benutzersession
     * @throws StoreException wenn der Verlauf z.B. auf grund des Status nicht mehr zurueck gerufen werden kann.
     */
    void dispoVerlaufRueckruf(Long verlaufId, Long sessionId) throws StoreException;

    /**
     * Schliesst den Verlauf mit der ID <code>verlaufId</code> ab. <br> Das Inbetriebnahmedatum des zugehoerigen
     * Auftrags wird auf <code>inbetriebnahmedatum</code> gesetzt, sofern es noch nicht gefuellt war. <br> <br> In der
     * Funktion ist ein Check integriert, der prueft, ob dem Auftrag noch ein offenes Innenauftragsbudget zugeordnet
     * ist. Ist dies der Fall, wird eine <code>StoreException</code> geworfen. <br>
     *
     * @param verlaufId           ID des abzuschliessenden Verlaufs
     * @param verlaufAbteilungId  ID des uebergeordneten Abteilungs-Verlaufs
     * @param inbetriebnahmedatum Datum fuer die Inbetriebnahme (bei Projektierungen kann auch <code>null</code>
     *                            uebergeben werden.
     * @param sessionId           Session-ID des aktuellen Users.
     * @param amAbschluss         durch Angabe von <code>true</code> wird nach dem Dispo-Abschluss automatisch auch der
     *                            AM-Abschluss durchgefuehrt (mit der gleichen Session-ID).
     * @throws StoreException wenn beim Abschluss ein Fehler auftritt.
     */
    void dispoVerlaufAbschluss(Long verlaufId, Long verlaufAbteilungId,
            Date inbetriebnahmedatum, Long sessionId, Boolean amAbschluss) throws StoreException;

    /**
     * Storniert den Verlauf mit der ID <code>verlaufId</code>.
     *
     * @param verlaufId ID des zu stornierenden Verlaufs
     * @param sessionId Session-ID des aktuellen Users.
     * @return Warnings, falls Fehler auftraten
     * @throws StoreException wenn der Verlauf nicht storniert werden konnte
     */
    AKWarnings dispoVerlaufStorno(Long verlaufId, Long sessionId) throws StoreException;

    /**
     * Schliesst den Bauauftrag mit der ID <code>verlaufId</code> ab.
     *
     * @param verlaufId ID des Verlaufs, der abgeschlossen werden soll.
     * @param sessionId Session-ID des aktuellen Users.
     * @return abgeschlossener Verlauf
     * @throws StoreException wenn der Bauauftrag nicht abgeschlossen werden konnte.
     */
    Verlauf amBaAbschliessen(Long verlaufId, Long sessionId) throws StoreException;

    /**
     * Schliesst die Projektierung mit der ID <code>verlaufId</code> ab.
     *
     * @param verlaufId ID des Verlaufs bzw. der Projektierung, die abgeschlossen werden soll.
     * @param sessionId Session-ID des aktuellen Users.
     * @return abgeschlossener Verlauf
     * @throws StoreException wenn die Projektierung nicht abgeschlossen werden konnte.
     */
    Verlauf amPrAbschliessen(Long verlaufId, Long sessionId) throws StoreException;

    /**
     * Erstellt Bauauftraege fuer die angegebenen Abteilungen. <br>
     *
     * @param verlaufId ID des Verlaufs, in den weitere Abteilungen einbezogen werden sollen
     * @param abtIds    Liste mit den IDs der Abteilungen, die in den Verlauf einbezogen werden sollen.
     * @param realDate  geplantes Realisierungsdatum fuer die Abteilungen
     * @param sessionId
     * @return Liste mit den erstellten Objekten des Typs <code>VerlaufAbteilung</code>.
     * @throws StoreException wenn bei der Verteilung ein Fehler auftritt.
     */
    public List<VerlaufAbteilung> baErstellen(Long verlaufId, List<Long> abtIds, Date realDate, Long sessionId) throws StoreException;

    /**
     * Erstellt VerlaufAbteilungen-Objekte fuer die zentrale Dispo. <br>
     *
     * @param verlaufId ID des Verlaufs, in den weitere Abteilungen einbezogen werden sollen
     * @param sessionId
     * @return Liste mit den erstellten Objekten des Typs <code>VerlaufAbteilung</code>.
     * @throws StoreException wenn bei der Verteilung ein Fehler auftritt.
     */
    List<VerlaufAbteilung> createVerlaufAbteilungForZentraleDispo(Long verlaufId, Long sessionId) throws StoreException;

    /**
     * Speichert den Verlauf.
     *
     * @param toSave zu speicherndes Objekt
     * @throws StoreException wenn beim Speichern ein Fehler auftritt.
     */
    void saveVerlauf(Verlauf toSave) throws StoreException;

    /**
     * Speichert das Objekt.
     *
     * @param toSave zu speicherndes Objekt
     * @throws StoreException wenn beim Speichern ein Fehler auftritt.
     */
    void saveVerlaufAbteilung(VerlaufAbteilung toSave) throws StoreException;

    /**
     * Sucht nach einem best. Verlauf.
     *
     * @param verlaufId ID des gesuchten Verlaufs.
     * @return Instanz von <code>Verlauf</code> oder <code>null</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    Verlauf findVerlauf(Long verlaufId) throws FindException;

    /**
     * Sucht nach einem best. Verlauf in Abhängigkeit zu einer FFM Workforce Order innerhalb einer neuen Transaction.
     *
     * @param workforceOrderId WorkforceOrder ID des gesuchten Verlaufs.
     * @return Instanz von <code>Verlauf</code> oder <code>null</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt bzw. wenn kein {@link Verlauf} zu der angegebenen
     *                       {@code workforceOrderId} ermittelt werden kann.
     */
    Verlauf findVerlaufByWorkforceOrder(String workforceOrderId);

    /**
     * Sucht nach allen aktiven Verlaeufen zu einem bestimmten Datum. <br> Ueber <code>projektierungen</code> kann
     * bestimmt werden, ob nach Projektierungen (true), Bauauftraegen (false) oder nach beiden (null) Verlaufs-Typen
     * gesucht wird.
     *
     * @param realDate        Realisierungsdatum zu dem aktive Verlaeufe gesucht werden.
     * @param projektierungen Einschraenkung auf den gesuchten Verlaufs-Typ.
     * @return Liste mit Objekten des Typs <code>Verlauf</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    List<Verlauf> findActVerlaeufe(Date realDate, Boolean projektierungen) throws FindException;

    /**
     * Sucht nach <strong>allen</strong> Verlaufs-Eintraegen fuer einen best. Auftrag. Die Sortierung erfolgt absteigend
     * (aktuellster BA zuerst)!
     *
     * @param auftragId ID des Auftrags, dessen Verlaufs-Eintraege gesucht werden.
     * @return Liste mit Objekten des Typs <code>Verlauf</code> oder <code>null</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    List<Verlauf> findVerlaeufe4Auftrag(Long auftragId) throws FindException;

    /**
     * Sucht nach den wichtigsten Daten aller Verlaufs-Eintraege fuer einen best. Auftrag und gibt diese absteigend
     * (nach der ID) zurueck.
     *
     * @param auftragId ID des Auftrags
     * @return Liste mit Objekten des Typs <code>SimpleVerlaufView</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public List<SimpleVerlaufView> findSimpleVerlaufViews4Auftrag(Long auftragId) throws FindException;

    /**
     * Sucht nach einem aktiven(!) Verlauf fuer den Auftrag <code>auftragId</code>. Ueber das Flag
     * <code>projektierung</code> wird  bestimmt, ob nach einem aktiven Bauauftrag (false) oder einer Projektierung
     * (true) gesucht wird.
     *
     * @param auftragId     ID des Auftrags zu dem ein aktiver Verlauf gesucht wird
     * @param projektierung Flag, ob nach einer Projektierung oder einem Bauauftrag gesucht wird.
     * @return Instanz von <code>Verlauf</code> oder <code>null</code>.
     * @throws FindException wenn bei der Suche ein Fehler auftritt oder wenn mehr als ein aktiver Verlauf gefunden
     *                       wurde!
     */
    Verlauf findActVerlauf4Auftrag(Long auftragId, boolean projektierung) throws FindException;

    /**
     * Sucht nach dem <b>letzten</b> Verlauf fuer einen best. Auftrag. <br> Ueber das Flag <code>projektierung</code>
     * wird angegeben, ob nach der letzten Projektierung (true) oder dem letzten Bauauftrag (false) fuer den Auftrag
     * gesucht werden soll.
     *
     * @param auftragId     Auftrags-ID
     * @param projektierung Flag, ob nach der letzten Projektierung oder dem letzten Bauauftrag gesucht werden soll.
     * @return Instanz von <code>Verlauf</code>.
     */
    Verlauf findLastVerlauf4Auftrag(Long auftragId, boolean projektierung) throws FindException;

    /**
     * Sucht nach aktiven(!) Verlaeufen fuer den Auftrag <code>auftragId</code>. Maximal duerfen zwei Verlaeufe
     * zurueckgegeben werden. Fuer BA und fuer Projektierung.
     *
     * @param auftragId ID des Auftrags zu dem ein aktiver Verlauf gesucht wird
     * @return Lister der Instanzen von <code>Verlauf</code> oder <code>null</code>.
     * @throws FindException wenn bei der Suche ein Fehler auftritt oder wenn mehr als ein aktiver Verlauf pro Kategorie
     *                       (BA, Projektierung) gefunden wurde!
     */
    List<Verlauf> findAllActVerlauf4Auftrag(Long auftragId) throws FindException;

    /**
     * Sucht nach einem Abteilungs-Verlauf ueber eine ID.
     *
     * @param verlaufAbtId ID des gesuchten Abteilung-Verlaufs.
     * @return Instanz von <code>VerlaufAbteilung</code> oder <code>null</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    VerlaufAbteilung findVerlaufAbteilung(Long verlaufAbtId) throws FindException;

    /**
     * Sucht nach dem Abteilungs-Verlauf einer best. Abteilung, der einem best. Verlauf zugeordnet ist.
     *
     * @param verlaufId ID des uebergeordneten Verlaufs.
     * @param abtId     ID der Abteilung
     * @return Instanz von <code>VerlaufAbteilung</code> oder <code>null</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    VerlaufAbteilung findVerlaufAbteilung(Long verlaufId, Long abtId) throws FindException;

    /**
     * @param verlaufId
     * @param abtId
     * @param niederlassungId
     * @return
     * @throws FindException
     * @see VerlaufAbteilung findVerlaufAbteilung(Long, Long) inkl. Niederlassung
     */
    VerlaufAbteilung findVerlaufAbteilung(Long verlaufId, Long abtId, Long niederlassungId) throws FindException;

    /**
     * Sucht nach allen Abteilungs-Verlaeufen fuer einen best. Verlauf.
     *
     * @param verlaufId ID des Verlaufs.
     * @return Liste mit Objekten des Typs <code>VerlaufAbteilung</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public List<VerlaufAbteilung> findVerlaufAbteilungen(Long verlaufId) throws FindException;

    List<VerlaufAbteilung> findVerlaufAbteilungen(Long verlaufId, Long... abteilungIds) throws FindException;

    /**
     * Sucht nach allen Abteilungs-Verlaeufen fuer einen best. Verlauf und einen bestimmten uebergeordneter
     * Abteilungs-Verlauf. Falls der Abteilungs-Verlauf {@code null} ist werden nur Abteilungs-Verlaeufe ohne
     * uebergeordneten Abteilungs-Verlauf zurueck gegeben!
     *
     * @param verlaufId          ID des Verlaufs.
     * @param verlaufAbteilungId ID des uebergeordneten Abteilungs-Verlaufs, kann {@code null} sein.
     * @return Liste mit Objekten des Typs <code>VerlaufAbteilung</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    List<VerlaufAbteilung> findVerlaufAbteilungen(Long verlaufId, Long verlaufAbteilungId) throws FindException;

    /**
     * Ermittelt eine Liste mit Views aus Abteilungs- und Niederlassungs-IDs, die fuer den Auftrag mit der ID
     * <code>auftragId</code> einen Bauauftrag erhalten haben.
     *
     * @param auftragId           Auftrags-ID
     * @param ignoreProjektierung Flag, ob Projektierungen ignoriert werden sollen.
     * @param onlyTechAbts        Flag, ob nur nach technischen Abteilungen gesucht werden soll
     * @param ignoreSCT           Flag, ob die Abteilung SCT(-K) ignoriert werden soll
     * @param noticeBAVerlNeu     Flag, ob die Abteilungen der Verlaufs-Konfiguration geladen werden sollen.
     * @return Liste mit den IDs der Abteilungen, die einen Bauauftrag zu dem Auftrag erhalten haben.
     * @throws FindException
     *
     */
    List<VerlaufAbteilungView> findAffectedAbteilungViews(Long auftragId, boolean ignoreProjektierung,
            boolean onlyTechAbts, boolean ignoreSCT, boolean noticeBAVerlNeu) throws FindException;

    /**
     * Sucht nach allen Verlauf-Stati.
     *
     * @return Liste mit Objekten des Typs <code>VerlaufStatus</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    List<VerlaufStatus> findVerlaufStati() throws FindException;

    /**
     * Sucht nach allen Verlauf-Abteilung-Stati.
     *
     * @return Liste mit Objekten des Typs <code>VerlaufAbteilungStatus</code>. Nie {@code null}.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    List<VerlaufAbteilungStatus> findVerlaufAbteilungStati() throws FindException;


    /**
     * Ermittelt, ob ein Verlauf automatisch verteilt werden darf.
     *
     * @param verlauf
     * @return
     * @throws FindException
     */
    boolean isAutomaticallyDispatchable(Verlauf verlauf) throws FindException;

    /**
     * Ueberprueft, ob die Installation durch M-net/extern erfolgen soll. Dies ist der Fall, wenn: <ul> <li>dem Auftrag
     * mindestens ein Endgeraet mit Montageart AKom/M-net zugeordnet ist. <li>der Verlauf einen entsprechenden
     * Installationstyp besitzt </ul>
     *
     * @param verlaufId ID des zu pruefenden Verlaufs
     * @return true, wenn eine Installation durch M-net oder durch einen externen Dienstleister erfolgen soll
     * @throws FindException wenn bei der Ermittlung der Installationsart ein Fehler auftritt
     *
     */
    boolean hasExternInstallation(Long verlaufId) throws FindException;

    /**
     * Findet alle Aufträge die von einem bestimmten Verlauf abgedeckt werden.
     *
     * @param verlaufId ID des Verlaufs zu dem alle Aufträge ermittelt werden sollen.
     * @return Liste mit Hurrican-Auftägen
     * @throws FindException wenn bei der Suche ein Fehler auftritt
     */
    List<CCAuftragIDsView> findAuftraege4Verlauf(Long verlaufId) throws FindException;

    /* ************** Methoden fuer die Ansicht der Verlaeufe der einzelnen Abteilungen ***************** */

    /**
     * Sucht nach allen Projektierungen fuer eine bestimmte Abteilung.
     *
     * @param abtId        ID der Abteilung, deren aktuelle Projektierungen angezeigt werden sollen.
     * @param ruecklaeufer Flag, ob bei Abteilung DISPO nach Ruecklaeufern (true) oder zu verteilenden Projektierungen
     *                     (false) gesucht werden soll.
     * @return Liste mit Objekten des Typs <code>ProjektierungsView</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    List<ProjektierungsView> findProjektierungen4Abt(Long abtId, boolean ruecklaeufer)
            throws FindException;

    /**
     * Sucht nach den Verlaeufen (keine Projektierung!) fuer eine best. Abteilung. Wenn ein Realisierungstermin ({@code
     * realisierungFrom} und/oder {@code realisierungTo}) gegeben ist, wird nach allen Bauaufträgen gesucht, die einen
     * entsprechenden Realisierungstermin aufweisen. Ansonsten wird eine Liste mit allen aktiven Bauaufträgen der
     * Abteilung zurückgegeben.
     *
     * @param universalQuery   Flag definiert, ob ein abteilungs-spezifisches Query fuer die Datenermittlung verwendet
     *                         werden soll (=false) oder ein allgemeines Query (=true)
     * @param abtId            ID der Abteilung, deren aktive Verlaeufe angezeigt werden sollen.
     * @param ruecklaeufer     Flag, ob nach Ruecklaeufern gesucht werden soll (nur bei AM und DISPO!)
     * @param realisierungFrom Realisierungstermin soll später oder gleich sein oder {@code null} falls nicht zu
     *                         beachten
     * @param realisierungTo   Realisierungstermin soll früher oder gleich sein oder {@code null} falls nicht zu
     *                         beachten
     * @return Liste mit Objekten des Typs <code>AbstractVerlaufView</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    List<AbstractBauauftragView> findBAVerlaufViews4Abt(boolean universalQuery, Long abtId, boolean ruecklaeufer,
            Date realisierungFrom, Date realisierungTo) throws FindException;

    /**
     * Ermittelt alle Bauauftraege (egal ob aktiv oder abgeschlossen!) des angegebenen Kunden, deren Realisierungstermin
     * heute +/- weekCount liegen.
     *
     * @param kundeNoOrig Angabe der Kundennummer
     * @param baseDate    Basis-Datum, von dem aus zeitnahe Auftraege ermittelt werden
     * @param weekCount   Anzahl der zu beruecksichtigenden Wochen (+/-)
     * @return Liste mit Objekten des Typs <code>AbstractBauauftragView</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     *
     */
    List<AbstractBauauftragView> findBAVerlaufViews4KundeInShortTerm(Long kundeNoOrig,
            Date baseDate, int weekCount) throws FindException;

    /**
     * Der Abteilungs-Verlauf mit der ID <code>verlaufAbtId</code> wird von dem Benutzer mit der Session-ID
     * <code>sessionId</code> uebernommen.
     *
     * @param verlaufAbtId ID des Abteilungs-Verlaufs, der uebernommen werden soll
     * @param sessionId    Session-ID des Benutzers, der den Verlauf uebernimmt.
     * @return aktualisierte Verlauf-Abteilung.
     * @throws StoreException wenn bei der Uebernahme ein Fehler auftritt.
     */
    VerlaufAbteilung verlaufUebernahme(Long verlaufAbtId, Long sessionId) throws StoreException;

    /**
     * Sucht zu einem Verlauf den Abteilungs-Datensatz der Abteilung, die den Bauauftrag bzw. die Projektierung verteilt
     * hat.
     *
     * @param verlaufId Id des Verlauf
     * @return gesuchter VerlaufAbteilung-Datensatz
     */
    VerlaufAbteilung findVAOfVerteilungsAbt(Long verlaufId) throws FindException;

    /**
     * Ordnet den aktuellen Verlauf (bzw. den Abteilungs-Datensatz fuer einen Verlauf) einem bestimmten Benutzer zu.
     *
     * @param verlaufAbtId ID des Abteilungs-Verlaufs, der einem Mitarbeiter zugeordnet werden soll
     * @param user         User, dem der Verlauf zugewiesen werden soll
     * @return aktualisierte Verlauf-Abteilung.
     * @throws StoreException wenn bei der Zuweisung ein Fehler auftritt.
     */
    VerlaufAbteilung assignVerlauf(Long verlaufAbtId, AKUser user) throws StoreException;

    /**
     * Setzt den angegebenen Abteilungs-Verlauf auf erledigt. <br> Achtung: die Methode ist nur fuer Bauauftraege, nicht
     * fuer Projektierungen gedacht! Haben alle technischen(!) Abteilungen den Verlauf zum vorgegebenen Termin
     * abgeschlossen, wird der ganze Verlauf auf erledigt und der Auftrag auf 'in Betrieb' bzw. 'Auftrag gekuendigt'
     * gesetzt - ohne Bauauftrags-Ruecklaeufer fuer Dispo/AM. Sollte einer der nachfolgenden Faelle auftreten, wird ein
     * BA-Ruecklaeufer erstellt: <br> <ul> <li>Produkt ist als 'Ruecklaeufer' konfiguriert <li>Verlauf ist als
     * Ueberwachung markiert <li>Realisierungstermin min. einer Abteilung weicht vom Vorgabetermin ab <li>Auftrag ist
     * einem VPN zugeordnet </ul>
     *
     * @param va               der abzuschliessende Abteilungs-Verlauf
     * @param bearbeiter       Bearbeiter, der den BA erledigt hat
     * @param bemerkung        Bemerkung zum BA
     * @param realDate         Realisierungsdatum
     * @param sessionId        Session-ID des aktuellen Users.
     * @param zusatzAufwand    Zusatzaufwand der bei Realisierung anfiel
     * @param notPossible      gesetztzes Flag (TRUE) gibt an, dass der Verlauf von der Abteilung nicht realisiert
     *                         werden kann
     * @param notPossibleRefId Referenz-ID auf einen Grund der definiert, wieso der Verlauf von der Abteilung nicht
     *                         abgeschlossen werden kann
     * @return der gespeicherte VerlaufAbteilungs-Datensatz
     * @throws StoreException wenn beim Abschluss ein Fehler auftritt.
     *
     */
    VerlaufAbteilung finishVerlauf4Abteilung(VerlaufAbteilung va, String bearbeiter,
            String bemerkung, Date realDate, Long sessionId, Long zusatzAufwand,
            Boolean notPossible, Long notPossibleRefId) throws StoreException;

    /**
     * Sonder-Variante von {@link BAService#finishVerlauf4Abteilung(VerlaufAbteilung, String, String, Date, Long, Long,
     * Boolean, Long)} die neben dem 'normalem' BA-Abschluss auch noch {@link WorkforceService#deleteOrder(DeleteOrder)}
     * aufruft, um eine in FFM platzierte {@link WorkforceOrder} zu loeschen.
     *
     * @param va
     * @param bearbeiter
     * @param bemerkung
     * @param realDate
     * @param sessionId
     * @param zusatzAufwand
     * @param notPossible
     * @param notPossibleRefId
     * @return
     * @throws StoreException
     */
    VerlaufAbteilung finishVerlauf4FFMWithDeleteOrder(VerlaufAbteilung va, String bearbeiter,
            String bemerkung, Date realDate, Long sessionId, Long zusatzAufwand,
            Boolean notPossible, Long notPossibleRefId) throws StoreException;

    /**
     * Ermittelt, ob es sich bei der Physik des Verlauf-Auftrags um z.B. eine Anschlussuebernahme oder DSL-Kreuzung etc.
     * handelt. <br> Aus der ermittelten Information wird ein String erstellt, der folgendem Schema entspricht:
     * Typ_der_Physikuebernahme + von/nach + Auftrag-ID_B <br> Bsp.: Anschlussuebernahme nach 123456 <br>
     *
     * @param verlauf Verlauf zu dem eine Physik-Information ermittelt werden soll.
     * @return Physik-Information zu dem Verlauf
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    String getPhysikInfo4Verlauf(Verlauf verlauf) throws FindException;

    /**
     * Erstellt einen Verlaufs-Report fuer den Auftrag mit der ID <code>auftragId</code>. (Es wird nach der aktuellsten
     * Projektierung fuer den Auftrag gesucht!)
     *
     * @param auftragId     Auftrags-ID
     * @param sessionId     Session-ID des Users
     * @param projektierung Flag, ob eine Projektierung oder ein Bauauftrag gedruckt werden soll.
     * @param compact
     * @return JasperPrint-Objekt, das den Report darstellt.
     * @throws AKReportException wenn beim Erstellen des Reports ein Fehler auftritt.
     * @throws FindException     wenn beim Ermitteln des letzten Verlaufs (Projektierung od. Bauauftrag) ein Fehler
     *                           auftritt oder kein Verlauf gefunden werden konnte.
     */
    JasperPrint reportVerlauf4Auftrag(Long auftragId, Long sessionId, boolean projektierung, boolean compact)
            throws AKReportException, FindException;

    /**
     * Erstellt den Verlaufs-Report fuer den Auftrag mit der ID <code>auftragId</code>. <br> Im Gegensatz zu
     * <code>reportVerlauf4Auftrag</code> wird hier nicht(!) nach einem Verlaufsdatensatz gesucht!
     *
     * @param auftragId Auftrags-ID
     * @param sessionId Session-ID des Users
     * @return JasperPrint-Objekt, das den Report darstellt.
     * @throws AKReportException wenn beim Erstellen des Reports ein Fehler auftritt.
     * @throws FindException
     *
     */
    JasperPrint reportTechDetails4Auftrag(Long auftragId, Long sessionId)
            throws AKReportException;

    /**
     * Erstellt einen Report fuer einen best. Verlauf.
     *
     * @param verlaufId
     * @param sessionId
     * @param projektierung
     * @param compact
     * @return
     * @throws AKReportException
     */
    JasperPrint reportVerlauf(Long verlaufId, Long sessionId, boolean projektierung, boolean compact)
            throws AKReportException;

    /**
     * Erstellt einen Report mit den Details zu einem best. Verlauf.
     *
     * @param verlaufId ID des Verlaufs.
     * @return JasperPrint-Objekt, das den Report darstellt.
     * @throws AKReportException wenn beim Erstellen des Report ein Fehler auftritt.
     */
    JasperPrint reportVerlaufDetails(Long verlaufId) throws AKReportException;

    /**
     * Funktion verschiebt den Realisierungstermin eines Bauauftrags. <br> Neben dem Gesamt-Realisierungstermin koennen
     * bzw. werden auch die Zieltermine fuer die einzelnen Abteilungen verschoben.
     *
     * @param verlaufId Bauauftrag, dessen Realisierungstermin verschoben werden soll
     * @param realDate  Neuer Realisierungstermin
     * @param sessionId SessionId des aktuellen Benutzers
     * @param abtModels Termine fuer Abteilungen
     * @throws StoreException Falls ein Fehler auftritt
     *
     */
    void changeRealDate(Long verlaufId, Date realDate, Long sessionId, List<SelectAbteilung4BAModel> abtModels) throws StoreException;

    /**
     * Funktion verschiebt den Realisierungstermin eines Bauauftrags auf den angegebenen Termin. <br/>
     * Dabei wird fuer ALLE Abteilungen des BAs der neue Termin gesetzt.
     *
     * @param verlaufId
     * @param realDate
     * @param user
     */
    void changeRealDate(Long verlaufId, Date realDate, AKUser user) throws StoreException;

    /**
     * Erzeugt einen bestimmten Bauauftrag als PDF und legt dieses im temp. Verzeichnis des Benutzers ab.
     *
     * @param verlaufId     ID des Verlaufs
     * @param sessionId     Id der Benutzer-Session
     * @param projektierung Flag, ob eine Projektierung (true) oder ein Bauauftrag (false) gedruckt werden soll.
     * @param withDetails
     * @throws AKReportException
     * @result Report als byte-array mit Dateinamen
     */
    Pair<byte[], String> saveVerlaufPDF(Long verlaufId, Long sessionId, boolean projektierung, boolean withDetails) throws AKReportException;

    /**
     * Funktion fasst die Bemerkungen aller Abteilungen zusammen
     *
     * @param verlaufId Id des Verlaufs
     * @return String mit Bemerkungen
     * @throws FindException Falls ein Fehler auftrat
     *
     */
    String findBemerkung4Verlauf(Long verlaufId) throws FindException;


    /**
     * Funktion fuer Wholesale Notification bei Bauauftragabschluss.
     *
     * @param wholesaleOrderId      OrderId
     * @param verlaufId             verlaufId des Auftrags
     */
    public void notifyWholesale(String wholesaleOrderId, Long verlaufId);

}
