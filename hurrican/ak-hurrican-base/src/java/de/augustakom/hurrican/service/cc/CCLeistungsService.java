/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.11.2005 10:43:05
 */
package de.augustakom.hurrican.service.cc;

import java.time.*;
import java.util.*;
import javax.annotation.*;
import javax.validation.constraints.*;

import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.model.cc.Auftrag2TechLeistung;
import de.augustakom.hurrican.model.cc.AuftragAktion;
import de.augustakom.hurrican.model.cc.Bandwidth;
import de.augustakom.hurrican.model.cc.IpMode;
import de.augustakom.hurrican.model.cc.Produkt2TechLeistung;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.temp.LeistungsDiffCheck;
import de.augustakom.hurrican.model.cc.view.LeistungsDiffView;
import de.augustakom.hurrican.model.cc.view.TechLeistungSynchView;
import de.augustakom.hurrican.service.base.exceptions.DeleteException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;

/**
 * Service-Interface fuer die Verwaltung von Leistungen zu Auftraegen.
 *
 *
 */
public interface CCLeistungsService extends ICCService {

    /**
     * Sucht nach allen konfigurierten technischen Leistungen.
     *
     * @param onlyActual Flag, ob nach allen (false) oder nur nach aktuell gueltigen Leistungs-Konfigurationen gesucht
     *                   wird.
     * @return Liste mit Objekten des Typs <code>TechLeistung</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    List<TechLeistung> findTechLeistungen(boolean onlyActual) throws FindException;

    /**
     * Ermittelt alle technischen Leistungen eines bestimmten Typs.
     *
     * @param techLsTyp Typ der zu ermittelnden technischen Leistungen
     * @return Liste mit Objekten des Typs <code>TechLeistung</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     *
     */
    List<TechLeistung> findTechLeistungenByType(String techLsTyp) throws FindException;

    /**
     * Ermittelt die technische Leistung mit der ID <code>techLeistungId</code>.
     *
     * @param techLeistungId ID der gesuchten techn. Leistung
     * @return Objekt vom Typ <code>TechLeistung</code> oder <code>null</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     *
     */
    TechLeistung findTechLeistung(Long techLeistungId) throws FindException;

    /**
     * Sucht nach den technischen Leistungszuordnungen eines Produkts. <br>
     *
     * @param prodId   Produkt-ID
     * @param techLsTyp (optional) (optional) Leistungstyp-Angabe fuer die gesuchten Leistungen, die dem Produkt zugeordnet
     *                    sind.
     * @param defaults (optional) Einschraenkung, ob nur nach den Defaults (Boolean.TRUE), den nicht-standard Leistungen
     *                 (Boolean.FALSE) oder allen (null) Zuordnungen zum Produkt gesucht wird.
     * @return Liste mit Objekten des Typs <code>Produkt2TechLeistung</code>.
     * @throws FindException
     *
     */
    List<Produkt2TechLeistung> findProd2TechLs(Long prodId, String techLsTyp, Boolean defaults) throws FindException;

    /**
     * Speichert die angegebene Produkt-2-techLeistung ab.
     *
     * @param toSave zu speicherndes Objekt
     * @throws StoreException wenn beim Speichern ein Fehler auftritt.
     *
     */
    void saveProdukt2TechLeistung(Produkt2TechLeistung toSave) throws StoreException;

    /**
     * Loescht die angegebene Produkt-2-techLeistung
     *
     * @param p2tlId ID der zu loeschenden Konfiguration
     * @throws DeleteException wenn beim Loeschen ein Fehler auftritt.
     *
     */
    void deleteProdukt2TechLeistung(Long p2tlId) throws DeleteException;

    /**
     * Sucht nach der aktuell gueltigen Konfiguration der techn. Leistung mit der Kennzeichnung
     * <code>externLeistungNo</code>.
     *
     * @param externLeistungNo Kennzeichnung der gesuchten Leistung (LEISTUNG.EXTERN_LEISTUNG__NO aus dem
     *                         Billing-System).
     * @return Instanz von <code>TechLeistung</code> oder <code>null</code>.
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt oder mehrere aktuell gueltige Konfigurationen
     *                       gefunden werden.
     */
    TechLeistung findTechLeistungByExtLstNo(Long externLeistungNo) throws FindException;

    /**
     * Ermittelt alle technischen Leistungen zu einem bestimmten Auftrag.
     *
     * @param auftragId  Auftrags-ID
     * @param lsTyp      (optional) Angabe des gesuchten Leistungstyps (Konstante aus TechLeistung)
     * @param onlyActive Flag, ob nur aktive (true) oder auch bereits gekuendigte (false) Leistungen ermittelt werden
     *                   sollen.
     * @return Liste mit Objekten des Typs <code>TechLeistung</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     *
     */
    List<TechLeistung> findTechLeistungen4Auftrag(Long auftragId, String lsTyp, boolean onlyActive)
            throws FindException;

    /**
     * Bestimme die Bandbreite (Up- und Downstream) über die TechLeistung.TYP_DOWNSTREAM und TechLeistung.TYP_UPSTREAM
     *
     * @param auftragId                technisch Auftrags-ID
     * @param loadDefaultIfNotAssigned gibt an, ob die Default-Leistung des angegebenen Typs geladen werden soll, wenn
     *                                 dem Auftrag keine Leistung des Typs direkt zugeordnet ist.
     * @return
     * @throws FindException
     */
    @CheckForNull
    Bandwidth findBandwidth4Auftrag(Long auftragId, boolean loadDefaultIfNotAssigned) throws FindException;

    /**
     * Ermittelt alle technischen Leistungen zu bestimmten Auftraegen.
     *
     * @param auftragIds Auftrags-ID
     * @param lsTyp      (optional) Angabe des gesuchten Leistungstyps (Konstante aus TechLeistung)
     * @param onlyActive Flag, ob nur aktive (true) oder auch bereits gekuendigte (false) Leistungen ermittelt werden
     *                   sollen.
     * @return Map, die Auftrags-ID auf Liste mit Objekten des Typs <code>TechLeistung</code> mapt
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     *
     */
    Map<Long, List<TechLeistung>> findTechLeistungen4Auftraege(List<Long> auftragIds, String lsTyp,
            boolean onlyActive) throws FindException;

    /**
     * Ermittelt eine bestimmte technische Leistung zu dem Auftrag. Es wird davon ausgegangen, dass nur eine Leistung
     * des angegebenen Typs auf dem Auftrag aktiv sein kann. <br> Sollten doch mehrere Leistungen des Typs aktiv sein,
     * wird eine entsprechende FindException erzeugt.
     *
     * @param auftragId                Auftrags-ID
     * @param lsTyp                    gesuchter Leistungstyp
     * @param loadDefaultIfNotAssigned gibt an, ob die Default-Leistung des angegebenen Typs geladen werden soll, wenn
     *                                 dem Auftrag keine Leistung des Typs direkt zugeordnet ist.
     * @return Objekt vom Typ TechLeistung
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt oder wenn mehr als eine Leistung des angegebenen
     *                       Typs auf dem Auftrag gefunden wurden.
     *
     */
    TechLeistung findTechLeistung4Auftrag(Long auftragId, String lsTyp, boolean loadDefaultIfNotAssigned)
            throws FindException;

    /**
     * Ermittelt alle {@link Auftrag2TechLeistung} Objekte zu dem Auftrag mit der Id {@code auftragId}, die zum
     * Zeitpunkt {@code when} aktiv sind.
     *
     * @param auftragId
     * @param when      techn. Leistung muss zum angegebenen Datum gültig sein (optional)
     * @return
     */
    List<Auftrag2TechLeistung> findAuftrag2TechLeistungen(Long auftragId, @Nullable LocalDate when);

    /**
     * Ermittelt alle TechLeistung-Nummern zu dem Auftrag mit der Id {@code auftragId}, die zum Zeitpunkt {@code when}
     * aktiv sind. Wird fuer {@code when} null uebergeben, so wird keine Gueltigkeitspruefung durchgefuehrt.
     *
     * @param auftragId
     * @param when      techn. Leistung muss zum angegebenen Datum gültig sein (optional)
     * @return
     */
    Set<Long> findTechLeistungenNo4Auftrag(Long auftragId, @Nullable LocalDate when);

    /**
     * Ermittelt eine bestimmte technische Leistung zu dem Auftrag zu einem angegebenen Zeitpunkt. <br> Es wird davon
     * ausgegangen, dass nur eine Leistung des angegebenen Typs auf dem Auftrag zum angegebenen Zeitpunkt aktiv sein
     * kann. <br> Sollten doch mehrere Leistungen des Typs aktiv sein, wird eine entsprechende FindException erzeugt.
     *
     * @param auftragId Auftrags-ID
     * @param lsTyp     gesuchter Leistungstyp
     * @param validDate zu beruecksichtigendes Datum
     * @return Objekt vom Typ TechLeistung
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt oder wenn mehr als eine Leistung des angegebenen
     *                       Typs auf dem Auftrag gefunden wurden.
     *
     */
    TechLeistung findTechLeistung4Auftrag(Long auftragId, String lsTyp, Date validDate) throws FindException;

    /**
     * @param auftragId
     * @param lsTypes        Typen der techn. Leistungen
     * @param validDate
     * @param ignoreAktivVon
     * @return Liste der gefundenen Entities oder leere Liste
     * @throws FindException
     */
    @Nonnull
    List<Auftrag2TechLeistung> findAuftrag2TechLeistungen(@Nonnull Long auftragId, @Nonnull String[] lsTypes,
            @Nonnull Date validDate,
            boolean ignoreAktivVon) throws FindException;

    List<TechLeistung> findTechLeistungen4Auftrag(Long auftragId, String lsTyp, Date validDate)
            throws FindException;

    List<TechLeistung> findTechLeistungen4Auftrag(List<LeistungsDiffView> diffs, String lsTyp, Date validDate)
            throws FindException;

    List<TechLeistung> findTechLeistungen4Auftrag(Long auftragId, LocalDate validDate)
            throws FindException;

    /**
     * @see CCLeistungsService#isTechLeistungActive(List, Long, Date) Es werden hierbei alle angegebenen
     * Auftrag-IDs ueberprueft!
     */
    boolean isTechLeistungActive(List<Long> auftragIds, Long extLeistungNo, Date validDate)
            throws FindException;

    /**
     * Prueft, ob die technische Leistung mit der angegebenen externen Leistungsnummer auf dem Auftrag zum Zeitpunkt
     * 'validDate' aktiv ist.
     */
    boolean isTechLeistungActive(Long auftragId, Long extLeistungNo, Date validDate) throws FindException;

    /**
     * Ermittelt alle techn. Leistungen, die den Parametern entsprechen.
     *
     * @param typ       (optional) Typ der gesuchten Leistung(en)
     * @param strValue  (optional) Str-Value der gesuchten Leistung(en)
     * @param longValue (optional) Long-Value der gesuchten Leistung(en)
     * @return Liste mit Objekten des Typs <code>TechLeistung</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     *
     */
    List<TechLeistung> findTechLeistungen(String typ, String strValue, Long longValue) throws FindException;

    /**
     * Ermittelt die technische Leistung, bei der typ/longValue uebereinstimmen.
     *
     * @param typ
     * @param longValue
     * @return
     * @throws FindException
     */
    TechLeistung findTechLeistung(String typ, Long longValue) throws FindException;

    /**
     * Ueberprueft, ob dem angegebenen Auftrag die Leistung 'externLeistungNo' zugeordnet ist. <br>
     *
     * @param externLeistungNo gesuchte Leistung
     * @param auftragId        Auftrags-ID zu der die Leistung ermittelt werden soll
     * @param onlyActive       Flag, ob nur nach noch aktiven Leistungen gesucht werden soll.
     * @return true wenn die Leistung auf dem Auftrag vorhanden ist.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     *
     */
    boolean hasTechLeistungWithExtLeistungNo(Long externLeistungNo, Long auftragId, boolean onlyActive)
            throws FindException;

    /**
     * Ueberprueft, ob dem angegebenen Auftrag die Leistung 'externLeistungNo' zugeordnet ist. <br>
     *     Es muss die Leistung mit der Ende in der Zukunft sein
     *
     * @param externLeistungNo gesuchte Leistung
     * @param auftragId        Auftrags-ID zu der die Leistung ermittelt werden soll
     * @return true wenn die Leistung auf dem Auftrag vorhanden ist.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    boolean hasTechLeistungEndsInFutureWithExtLeistungNo(Long externLeistungNo, Long auftragId)
            throws FindException;

    /**
     * Ueberprueft, ob dem angegebenen Auftrag die technische Leistung mit der ID 'techLsId' zugeordnet ist.
     *
     * @param techLsId   ID der abzufragenden techn. Leistung
     * @param auftragId  Auftrags-ID zu der die Leistung ermittelt werden soll
     * @param onlyActive Flag, ob nur nach noch aktiven Leistungen gesucht werden soll.
     * @return true wenn die Leistung auf dem Auftrag vorhanden ist.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     *
     */
    boolean hasTechLeistung(Long techLsId, Long auftragId, boolean onlyActive) throws FindException;

    /**
     * Speichert das angegebene Objekt.
     *
     * @param toSave zu speicherndes Objekt
     * @throws StoreException wenn beim Speichern ein Fehler auftritt.
     */
    void saveLeistungKonfig(TechLeistung toSave) throws StoreException;

    /**
     * Ermittelt alle Zuordnungen von einem Auftrag zu den techn. Leistungen. Ueber das Flag <code>onlyAct</code> wird
     * bestimmt ob alle (false) oder nur die noch aktiven (true) Leistungen ermittelt werden sollen.
     *
     * @param ccAuftragId Auftrags-ID
     * @param techLsId    (optional) Angabe der TechLeistung-ID deren Zuordnungen zum Auftrag ermittelt werden sollen.
     *                    Ist die ID nicht angegeben, werden aller techn. Leistungen beachtet.
     * @param onlyAct     Flag, ob nur aktive Leistungen ermittelt werden sollen.
     * @return die ermittelten technischen Leistungen
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     *
     */
    List<Auftrag2TechLeistung> findAuftrag2TechLeistungen(Long ccAuftragId, Long techLsId, boolean onlyAct)
            throws FindException;

    /**
     * Ermittelt alle Auftragsleistungen, die zu dem angegebenen Verlauf realisiert oder gekuendigt wurden.
     *
     * @param verlaufId ID des Verlaufs.
     * @return Liste mit Objekten des Typs <code>AuftragTechLeistung</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     *
     */
    List<Auftrag2TechLeistung> findAuftrag2TechLeistungen4Verlauf(Long verlaufId) throws FindException;


    /**
     * Ermittelt alle techn. Leistungen, die dem Verlauf mit der angegebenen Id zugeordnet sind.
     * @param verlaufId
     * @param onlyZugang    Flag definiert, ob nur Leistungs-Zugaenge (true) oder alle Leistungen zum Verlauf ermittelt
     *                      werden sollen
     * @return
     * @throws FindException
     */
    @NotNull
    List<TechLeistung> findTechLeistungen4Verlauf(Long verlaufId, boolean onlyZugang) throws FindException;


    /**
     * Speichert die angegebene Auftrags-Leistungs-Zuordnung.
     *
     * @param toSave zu speicherndes Objekt
     * @throws StoreException
     *
     */
    void saveAuftrag2TechLeistung(Auftrag2TechLeistung toSave) throws StoreException;

    /**
     * Ermittelt alle technischen Leistungen des Auftrags und verschiebt die aktivVon/aktivBis Werte. Sofern eine {@link
     * AuftragAktion} angegeben ist, werden nur die technischen Leistungen modifiziert, die mit dieser {@link
     * AuftragAktion} verbunden sind; ist keine {@link AuftragAktion} angegeben, so werden alle aktivVon/aktivBis Werte
     * verschoben, bei denen das {@code originalDate} uebereinstimmt.
     *
     * @param auftragId     Auftrags-ID
     * @param originalDate  urspruengliches Datum, das angepasst werden soll
     * @param modifiedDate  Datum, auf das die Leistungen, die mit {@code originalDate} uebereinstimmen verschoben
     *                      werden soll
     * @param auftragAktion (optional) AuftragAktion, deren Datum verschoben wird
     * @return Liste mit den angepassten {@link Auftrag2TechLeistung} Objekten
     * @throws StoreException wenn bei der Datumsanpssung ein Fehler auftritt
     */
    @Nonnull
    List<Auftrag2TechLeistung> modifyAuftrag2TechLeistungen(Long auftragId, LocalDate originalDate,
            LocalDate modifiedDate, AuftragAktion auftragAktion)
            throws StoreException;

    /**
     * Ermittelt die Leistungs-Differenzen zwischen dem Billing- und CC-System zu dem angegebenen Auftrag.
     *
     * @param ccAuftragId   Auftrags-ID aus dem CC-System
     * @param auftragNoOrig (original) Auftrags-ID aus dem Billing-System
     * @param prodId        (Hurrican) Produkt-ID des zu pruefenden Auftrags
     * @return Liste mit Objekten des Typs <code>LeistungsDiffView</code> - sortiert nach Leistungszugaengen und
     * Leistungsabgaengen.
     * @throws FindException
     *
     */
    @Nonnull
    List<LeistungsDiffView> findLeistungsDiffs(Long ccAuftragId, Long auftragNoOrig, Long prodId)
            throws FindException;

    /**
     * Funktion ermittelt zu einmaligen Taifun-Leistungen, die zugehörigen technischen Leistungen in Taifun. Die Menge
     * wird dabei über alle Taifun-Leistungen aufsummiert.
     *
     * @param auftragNoOrig
     * @return
     * @throws FindException
     */
    List<TechLeistungSynchView> findOneTimeTechServices(Long auftragNoOrig) throws FindException;

    /**
     * Gleicht die technischen Leistungen eines bestimmten Auftrags mit dem Billing-System ab.
     *
     * @param ccAuftragId       Auftrags-ID aus dem CC-System
     * @param auftragNoOrig     (original) Auftrags-ID aus dem Billing-System
     * @param prodId            (Hurrican) Produkt-ID
     * @param verlaufId         (optional) ID des Verlauf-Datensatzes, zu dem die Aenderungen stattfinden.
     * @param executeLsCommands Flag, ob die Commands zu den abgeglichenen/geaenderten Leistungen ausgefuehrt werden
     *                          sollen.
     * @param sessionId         ID der aktuellen User-Session
     * @throws StoreException wenn bei dem Abgleich ein Fehler auftritt.
     * @result AKWarnings Warn-Meldungen die evtl. von den ausgefuehrten Commands erzeugt wurden.
     *
     */
    AKWarnings synchTechLeistungen4Auftrag(Long ccAuftragId, Long auftragNoOrig, Long prodId,
            Long verlaufId, boolean executeLsCommands, Long sessionId)
            throws StoreException;

    /**
     * Ueberprueft, ob die Leistungs-Differenzen auf einem Auftrag (definiert in den Leistungs-Diffs) abgeglichen werden
     * koennen. <br> Dazu werden zu den angegebenen technischen Leistungen zugehoerige Check-Commands (je nach Vorgang
     * Zugangs- oder Kuendigungs-Checks) aufgerufen.
     *
     * @param leistungsDiff abzugleichende Leistungen
     * @param sessionId     Session-ID des Users
     * @return Objekt vom Typ <code>LeistungsDiffPossible</code>. Definiert ueber seine Parameter, ob der Abgleich
     * durchgefuehrt werden kann. Wenn nicht, sind entsprechende Messages in dem Objekt gesetzt.
     * @throws HurricanServiceCommandException
     *
     */
    LeistungsDiffCheck checkLeistungsDiffs(List<LeistungsDiffView> leistungsDiff, Long sessionId)
            throws HurricanServiceCommandException;

    /**
     * Ermittelt die IDs der Abteilungen, die fuer die zu realisierenden/kuendigenden Leistungen zu einem bestimmten
     * Verlauf konfiguriert sind.
     *
     * @param verlaufId Verlaufs-ID, ueber die die Leistungs-Differenz ermittelt werden soll.
     * @return Liste mit den IDs der Abteilungen, die aufgrund der Leistungs-Konfiguration (TechLeistung) einen
     * Bauauftrag erhalten sollen/muessen.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    List<Long> findAbtIds4VerlaufTechLs(Long verlaufId) throws FindException;

    /**
     * Ermittelt die Anzahl der technischen Leistungen zu den angegebenen Typen, die dem Auftrag zugeordnet sind. Dabei
     * wird die Anzahl entweder ueber die reine Anzahl Datensaetze ermittelt oder ueber die Quantity.
     *
     * @param auftragId
     * @param activeDate
     * @param extLeistungNoFilter (optional!) Damit koennen die ausgewerteten Leistungen, die ueber die {@code
     *                            techLsTypes} festgelegt wurden noch weiter eingeschraenkt werden (auf eine bestimmte
     *                            techn. Leistung).
     * @param techLsTypes
     * @return
     * @throws FindException
     */
    @NotNull
    Integer getAssignedQuantity(Long auftragId, Date activeDate, Long extLeistungNoFilter, String... techLsTypes)
            throws FindException;

    /**
     * Ermittelt die Anzahl der konfigurierten Endgeräte Ports. Entweder ist die Anzahl auf einer Leistung im Attribut
     * Quantity hinterlegt oder die Anzahl der (gleichartigen) Leistungen bestimmt wieviele Ports konfiguriert sind. (Da
     * während der Implementierung dieser Methode noch nicht klar ist, wie die Anzahl Ports konfiguriert werden soll,
     * sieht diese Methode beide Wege vor.)
     */
    @Nonnull
    Integer getCountEndgeraetPort(@Nonnull Long auftragId, @Nonnull Date when) throws FindException;

    /**
     * Prüft ob der zum Auftrag gehörende Internet-Account gesperrt sein muss oder nicht. Hierzu wird geprüft, ob es
     * sich um einen Auftrag mit VoIP-Leistung vom Typ PMX handelt dem gleichzeitig auch ein Upstream mit einer
     * Bandbreite von höchstens 5Mbit zugeordnet ist.
     *
     * @param auftragId
     * @return <code>true</code> wenn eine VoIP-Leistung vom Typ PMX und gleichzeitig ein Upstream <= 5Mbit für den
     * Auftrag konfiguriert sind, ansonsten <code>false</code>
     * @throws FindException
     */
    boolean checkMustAccountBeLocked(Long auftragId) throws FindException;

    /**
     * Gibt einen Bandbreiten-String zum Auftrag zurueck.
     *
     * @param auftragId
     * @return String des Formats <TechLeistung.TYP_DOWNSTREAM>/<TechLeistung.TYP_UPSTREAM>, falls beide Leistungen
     * gesetzt sind, ansonsten {@code null}
     * @throws FindException
     */
    String getBandwidthString(Long auftragId) throws FindException;

    /**
     * @param auftragNoOrig Taifun-Auftragsnummer oder bei Wholesale = {@code null}
     * @see de.augustakom.hurrican.service.cc.CCLeistungsService#findLeistungsDiffs(java.lang.Long, java.lang.Long,
     * java.lang.Long)
     */
    @Nonnull
    List<LeistungsDiffView> findLeistungsDiffs(Long ccAuftragId, Long auftragNoOrig, Long prodId,
            ExterneAuftragsLeistungen externeAuftragsLeistungen, Date defaultlDate)
            throws FindException;

    /**
     * Gleicht die technischen Leistungen des angegebenen Auftrags ab. Die Differenz der technischen Leistungen wird
     * hier bereits angegeben (im Unterschied zu {@link CCLeistungsService#synchTechLeistungen4Auftrag(Long, Long, Long,
     * Long, boolean, Long)}.
     *
     * @param ccAuftragId
     * @param prodId
     * @param verlaufId
     * @param executeLsCommands
     * @param sessionId
     * @param diffs
     * @return
     * @throws StoreException
     * @see CCLeistungsService#synchTechLeistungen4Auftrag(Long, Long, Long, Long, boolean, Long)
     */
    AKWarnings synchTechLeistung4Auftrag(Long ccAuftragId, Long prodId, Long verlaufId,
            boolean executeLsCommands, Long sessionId, List<LeistungsDiffView> diffs) throws StoreException;

    /**
     * Gleicht die technischen Leistungen des angegebenen Auftrags ab. Wird fuer Auftraege verwendet, die keinen Verlauf
     * haben (z.B. Wholesale-Auftraege). Statt einer Verlaufs-Id wird hier der Realisierungstermin angegeben
     *
     * @param ccAuftragId
     * @param prodId
     * @param realisierungsTermin
     * @param executeLsCommands
     * @param sessionId
     * @param diffs
     * @param auftragAktion       Referenz fuer die {@link AuftragAktion}, durch die die Aenderungen ausgeloest werden
     * @return
     * @throws StoreException
     */
    AKWarnings synchTechLeistung4Auftrag(Long ccAuftragId, Long prodId, Date realisierungsTermin,
            boolean executeLsCommands, Long sessionId, List<LeistungsDiffView> diffs, AuftragAktion auftragAktion)
            throws StoreException;

    /**
     * Ermittelt alle {@link Auftrag2TechLeistung} Datensaetze zu dem Auftrag mit der Id {@code auftragId} und filtert
     * die Datensaetze heraus, deren die durch die {@link AuftragAktion} ausgeloest wurden. Die Datensaetze, die durch
     * die {@link AuftragAktion} angelegt wurden, werden geloescht; die durch die {@link AuftragAktion} deaktivierten
     * Leistungen werden wieder aktiviert.
     *
     * @param auftragId
     * @param auftragAktion die {@link AuftragAktion}, deren Aenderungen wieder rueckgaengig gemacht werden sollen
     * @throws StoreException
     */
    void cancelAuftrag2TechLeistungen(Long auftragId, AuftragAktion auftragAktion) throws StoreException;

    /**
     * Ermittelt den IP Mode fuer einen techn. Auftrag. <ul> <li>keine IP Leistungen oder nur IPv4 Leistungen
     * konfiguriert -> IPv4 <li>nur IPv6 Leistungen konfiguriert -> DS_LITE <li>IPv4 und IPv6 Leistungen konfiguriert ->
     * DUAL_STACK </ul>
     *
     * @return IPModeEnum (Rueckgabewert ist nie null)
     */
    IpMode queryIPMode(Long auftragId, @Nullable LocalDate when);

    /**
     * Prueft Leistungen fuer die Aussage, ob auf dem Auftrag <b>KEIN</b> IAD Device (Fritzbox) notwendig ist. Speziell
     * fuer FTTX Auftraege sind fuer eine vollstaendige Aussage weitere Pruefungen notwendig - Ausnahme: Business. In
     * folgenden Faellen ist fuer den Auftrag <b>KEIN</b> Device notwendig: <br/> <ul> <li>eine der folgenden Leistungen
     * ist dem Auftrag zugeordnet</li> <ul> <li>VOIP_TK</li> <li>VOIP_PMX</li> <li>'weiterer Endgeraeteport' (entspricht
     * > 1x VOIP_MGA)</li> </ul> </ul>
     */
    boolean deviceNecessary(@NotNull Long auftragId, @NotNull Date execDate) throws FindException;

    /**
     * Prueft, ob der Auftrag eine aktive oder zukünftige VoIP Leistung besitzt.
     */
    boolean hasVoipLeistungFromThenOn(@NotNull Long auftragId, @NotNull Date date) throws FindException;

    /**
     * Prueft, ob der Auftrag eine aktive oder zukünftige VoIP Leistung besitzt.
     */
    boolean hasVoipLeistungFromThenOn(@NotNull List<LeistungsDiffView> diffs, @NotNull Date date) throws FindException;

    default boolean hasVoipLeistungFromNowOn(@NotNull Long auftragId) throws FindException {
        return hasVoipLeistungFromThenOn(auftragId, new Date());
    }

    default boolean hasVoipLeistungFromNowOn(@NotNull List<LeistungsDiffView> diffs) throws FindException {
        return hasVoipLeistungFromThenOn(diffs, new Date());
    }

    /**
     * Teilweise besitzen die technischen Leistungen ein aktiv-von Datum, das in der Zukunft ist. Dieses wird erst bei
     * erfolgreicher BA-Erstellung umgeschrieben. Deshalb muss noch einmal zusaetzlich geprueft werden, ob vielleicht
     * die gesuchte Leistung zum ersten Gueltigkeitsdatum von zugeordneten technischen Leistungen aktiv ist.
     *
     * @param auftragId
     * @return das erste in der zukunft liegende Datum einer techn. Leistung oder <code>null</code> wenn keine
     * Leistungen existieren bzw. Leistungen bereits in der Vergangenheit begonnen haben
     * @throws FindException
     */
    Date getFirstFutureTechLsDate(Long auftragId) throws FindException;

    /**
     * siehe {@link CCLeistungsService#getFirstFutureTechLsDate(Long)}
     */
    Date getFirstFutureTechLsDate(@NotNull List<LeistungsDiffView> diffs) throws FindException;
}
