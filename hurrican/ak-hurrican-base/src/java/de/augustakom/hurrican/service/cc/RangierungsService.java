/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.11.2004 12:04:42
 */
package de.augustakom.hurrican.service.cc;

import java.util.*;
import javax.annotation.*;

import de.augustakom.common.service.iface.IServiceCallback;
import de.augustakom.common.tools.lang.Function2;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.model.cc.Bandwidth;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EqStatus;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.Rangierungsmatrix;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.Schicht2Protokoll;
import de.augustakom.hurrican.model.cc.UEVT;
import de.augustakom.hurrican.model.cc.Uebertragungsverfahren;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.model.cc.query.RangierungQuery;
import de.augustakom.hurrican.model.cc.query.RangierungsmatrixQuery;
import de.augustakom.hurrican.model.cc.temp.CarrierEquipmentDetails;
import de.augustakom.hurrican.model.cc.view.EquipmentInOutView;
import de.augustakom.hurrican.model.cc.view.HardwareEquipmentView;
import de.augustakom.hurrican.model.cc.view.RangierungWithEquipmentView;
import de.augustakom.hurrican.model.cc.view.RangierungsEquipmentView;
import de.augustakom.hurrican.service.base.exceptions.DeleteException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;

/**
 * Service-Interface fuer die Verwaltung von Rangierungen.
 *
 *
 */
public interface RangierungsService extends ICCService {

    /**
     * Wird fuer einen Service-Callback verwendet, um nach dem zugehoerigen Buendel-Produkt zu 'fragen'. <br> Als
     * Ergebnis wird ein Objekt vom Typ <code>Boolean</code> erwartet.
     */
    public static final int CALLBACK_ASK_4_BUENDEL_PRODUKT = 98;

    /**
     * Key fuer Callback Parameter-Map, um die Produkt-ID des Parents zu uebergeben.
     */
    public static final String CALLBACK_PARAM_PARENT_PRODUKT_ID = "parent.produkt.id";

    /**
     * Wird fuer einen Service-Callback verwendet, um zu 'fragen', ob bei einer Anschlussuebernahme der 'alte' Auftrag
     * automatisch auf gekuendigt gesetzt werden soll. <br> Als Ergebnis wird ein Objekt vom Typ <code>Boolean</code>
     * erwartet.
     */
    public static final int CALLBACK_ASK_KUENDIGUNG_AUTOMATIC_4_ANSCHLUSSUEBERNAHME = 99;

    /**
     * Key fuer Callback Parameter-Map, um die ID des 'alten' Auftrags bei einer Anschlussuebernahme zu uebergeben.
     */
    public static final String CALLBACK_PARAM_AUFTRAG_ID_4_ANSCHLUSSUEBERNAHME = "auftrag.id.4.anschlussuebernahme";

    /**
     * Wird fuer einen Service-Callback verwendet, um zu 'fragen', ob der Account des 'alten' Auftrags uebernommen
     * werden soll. <br> Als Ergebnis wird ein Objekt vom Typ <code>Boolean</code> erwartet.
     */
    public static final int CALLBACK_ASK_4_ACCOUNT_UEBERNAHME = 100;

    /**
     * Key fuer Callback Parameter-Map, um den Account des 'alten' Auftrags dem ServiceCallback mitzuteilen.
     */
    public static final String CALLBACK_PARAM_ACCOUNT = "account";

    /**
     * Wird fuer einen Service-Callback verwendet, um zu 'fragen', ob die VPN-Daten des 'alten' Auftrags auf den 'neuen'
     * Auftrag uebernommen werden sollen. <br> Als Ergebnis wird ein Objekt vom Typ <code>Boolean</code> erwartet.
     */
    public static final int CALLBACK_ASK_4_VPN_UEBERNAHME = 101;

    /**
     * Key fuer Callback Parameter-Map, um die Nr. des VPNs zu uebergeben, dem der 'alte' Auftrag zugeordnet ist.
     */
    public static final String CALLBACK_PARAM_VPN_NR = "vpn.nr";

    /**
     * Wird fuer einen Service-Callback verwendet, um nach dem Kuendigungsdatum fuer den Ursprungs-Auftrag zu 'fragen'.
     * <br> Als Ergebnis wird ein Objekt vom Typ <code>java.util.Date</code> erwartet.
     */
    public static final int CALLBACK_ASK_4_KUENDIGUNGS_DATUM = 102;

    /**
     * Key fuer Callback Parameter-Map, um das Vorgabe-SCV Datum des Ziel-Auftrags zu uebergeben.
     */
    public static final String CALLBACK_PARAM_VORGABE_SCV = "vorgabe.scv";

    /**
     * Wird fuer einen Service-Callback verwendet, um nach dem Realisierungsdatum fuer einen Auftrag zu 'fragen'. <br>
     */
    public static final int CALLBACK_ASK_4_REAL_DATE = 103;

    /**
     * Default-Wert fuer die Anzahl der Tage, nach der eine Rangierung wirklich freigegeben werden soll.
     */
    public static final int DELAY_4_RANGIERUNGS_FREIGABE = 10;

    /**
     * Speichert die angegebene Rangierung. <br> Wird fuer <code>makeHistory</code> true angegeben, wird der DS
     * historisiert - sonst nicht.
     *
     * @param toSave      zu speicherndes Objekt
     * @param makeHistory Flag, ob History erzeugt werden soll
     * @return abhaengig von <code>makeHistory</code> das urspruenglich uebergebene Objekt oder der fuer die
     * Historisierung neu erstelle DS.
     * @throws StoreException wenn beim Speichern ein Fehler auftritt.
     */
    public Rangierung saveRangierung(Rangierung toSave, boolean makeHistory) throws StoreException;

    /**
     * Ordnet der Rangierung 'toUse' die angegebenen Equipments zu. <br> Dies wird jedoch nur durchgefuehrt, wenn die
     * Rangierung auf der entsprechenden EQ-Seite noch keine Zuordnung besitzt. Ausserdem wird geprueft, ob das
     * zuzuordnende Equipment nicht bereits einer Rangierung zugeordnet ist.
     *
     * @param toUse          zu aendernde Rangierung
     * @param eqIn           Equipment fuer die EQ_IN Seite
     * @param eqOut          Equipment fuer die EQ_OUT Seite
     * @param changeEqStatus Flag, ob der Status des Equipments auf 'rangiert' gesetzt werden soll
     * @param makeHistory    Flag, ob die Rangierung historiert werden soll
     * @param allowReplace   TODO
     * @throws StoreException wenn bei der Zuordnung ein Fehler auftritt oder die Rangierung auf der zuzuordnenden
     *                        EQ-Seite bereits eine Referenz besitzt.
     *
     */
    public void assignEquipment2Rangierung(Rangierung toUse, Equipment eqIn, Equipment eqOut,
            boolean changeEqStatus, boolean makeHistory, boolean allowReplace) throws StoreException;

    /**
     * 'Oeffnet' eine bestehende Rangierung. Hierbei wird die EQ_IN und/oder die EQ_OUT Seite von der Rangierung
     * genommen. <br> Der Status des jeweiligen Equipment-Datensatzes wird dabei wieder auf 'frei' gesetzt. <br>
     *
     * @param toChange    zu aendernde Rangierung
     * @param breakEqIn   Flag, ob die EQ_IN Seite aufgebrochen werden soll
     * @param breakEqOut  Flag, ob die EQ_OUT Seite aufgebrochen werden soll
     * @param makeHistory Flag, ob die Rangierung historisiert werden soll
     * @return das durch die Historisierung neu erstellte Rangierungs-Objekt.
     * @throws StoreException wenn beim Aufbruch der Rangierung ein Fehler auftritt.
     *
     */
    public Rangierung breakRangierung(Rangierung toChange, boolean breakEqIn, boolean breakEqOut, boolean makeHistory)
            throws StoreException;

    /**
     * 'Öffnet' eine bestehende {@link Rangierung}, indem diese ungültig gesetzt wird ({@code GUELTIG_BIS = now()}). Der
     * Status der Equipment-Datensätze wird dabei auf den übergebenen {@link EqStatus} gesetzt. Voraussetzung, dass eine
     * {@link Rangierung} aufgebrochen werden kann: <ul> <li>{@code rangierung.getEsId() == null}</li> </ul>
     *
     * @param toChange    zu aendernde Rangierung
     * @param newEqStatus neuer zu setztender Status für aufzubrechende {@link Equipment}s
     * @param sessionId   Id der aktuellen Session (um den zugehörigen Benutzer zu finden)
     * @return die erzeugte neue {@link Rangierung} falls {@code makeHistory = true} ansonsten die Übergebene bzw.
     * Geänderte
     * @throws IllegalArgumentException falls übergebene {@link Rangierung} oder {@link EqStatus} {@code null} ist
     * @throws StoreException           wenn beim Aufbruch der Rangierung ein Fehler auftritt bzw. wenn eine
     *                                  Voraussetzung nicht erfüllt ist bzw. kein Benutzer zur übergebenen {@code
     *                                  sessionId} gefunden wird
     */
    public Rangierung breakRangierungUsingNewEqStatus(Rangierung toChange, EqStatus newEqStatus, Long sessionId)
            throws StoreException;

    /**
     * Ermittelt die Endstelle, die auf der Rangierung angegeben ist und prueft, ob die Endstelle auch auf diese
     * Rangierung verweist. Ist dies nicht der Fall, wird die Verbindung korrigiert.
     *
     * @param rangierungToReAttach
     * @param isDefaultRang
     * @throws StoreException
     */
    public void reAttachAccessPoint(Rangierung rangierungToReAttach, boolean isDefaultRang) throws StoreException;

    /**
     * Die Rangierung wird komplett aufgebrochen und deaktiviert.
     *
     * @param toDeactivate aufzubrechende Rangierung(en)
     * @param makeHistory  Flag, ob die alte Rangierung historisiert werden soll (empfohlen!)
     * @param sessionId    SessionID des aktuellen Users
     * @return das modifizierte Rangierungs-Objekt
     * @throws StoreException
     */
    public List<Rangierung> breakAndDeactivateRangierung(List<Rangierung> toDeactivate, boolean makeHistory,
            Long sessionId) throws StoreException;

    /**
     * Fasst die angegebenen Rangierungen zu einem Buendel zusammen. Hierbei erhalten alle Rangierungen der Liste die
     * gleiche LtgGesId. Die fortlaufende Nummer richtet sich dabei nach der Reihenfolge in der Liste. <br> Besitzt die
     * erste Rangierung aus der Liste bereits eine LtgGesId, so wird diese verwendet. Anderenfalls wird eine neue
     * LtgGesId erzeugt.
     *
     * @param toBundle zu buendelnde Rangierungen
     * @throws StoreException wenn bei der Buendelung ein Fehler auftritt.
     *
     */
    public void bundleRangierungen(List<Rangierung> toBundle) throws StoreException;

    /**
     * Ermittelt die Rangierungen der Endstelle und setzt diese auf 'freigabebereit'. <br> Dabei wird das
     * FreigabeAb-Datum der Rangierung auf 'datumFreigabe + x Tage' und die Endstellen-ID der Rangierungen auf
     * <code>-1</code> gesetzt.
     *
     * @param endstelle     Endstelle, deren Rangierungen freigegeben werden sollen.
     * @param datumFreigabe Datum, zu dem die Rangierung freigegeben werden soll. <strong>Wichtig: zu dem Datum wird
     *                      <code>DELAY_4_RANGIERUNGS_FREIGABE</code> addiert!
     * @throws StoreException wenn bei der Freigabe ein Fehler auftritt.
     */
    public void rangierungFreigabebereit(Endstelle endstelle, Date datumFreigabe) throws StoreException;

    /**
     * Sucht nach einer Rangierung ueber die ID. <br> WICHTIG: die Methode unterstuetzt keine Transaktionen!!!
     *
     * @param rangierId ID der gesuchten Rangierung
     * @return Objekt vom Typ <code>Rangierung</code> oder <code>null</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public Rangierung findRangierung(Long rangierId) throws FindException;

    /**
     * @see #findRangierung(Long)  Diese Methode unterstuetzt bestehende Transaktionen!!!
     */
    public Rangierung findRangierungTx(Long rangierId) throws FindException;

    /**
     * Ermittelt die aktive(!) Rangierung zu einem bestimmten Equipment-Datensatz.
     *
     * @param eqId     ID des Equipments
     * @param findEqIn Flag, ob das Equipment in EQ-In (true) oder EQ-Out (false) gesucht werden soll
     * @return Objekt vom Typ <code>Rangierung</code> oder <code>null</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt oder die Anzahl der gefundenen Rangierungen > 1
     *                       ist.
     *
     */
    public Rangierung findRangierung4Equipment(Long eqId, boolean findEqIn) throws FindException;

    /**
     * Ermittelt die aktive(!) Rangierung zu einem bestimmten Equipment. Die Suche erfolgt zuerst ueber EQ_IN; falls
     * keine entsprechende Rangierung gefunden wird, wird noch ueber EQ_OUT gesucht.
     *
     * @param eqId
     * @return
     * @throws FindException
     * @see RangierungsService#findRangierung4Equipment(Long, boolean)
     */
    public Rangierung findRangierung4Equipment(Long eqId) throws FindException;

    /**
     * Ermittelt die aktiven(!) Rangierungen zu einer Liste von Equipments.
     *
     * @param eqIds Liste von Equipments fuer die Rangierungen gesucht sind
     * @return Map, die Equipment-ID (sowohl IN als auch OUT) auf Rangierung mappt, evtl. leer, nie {@code null}
     * @throws FindException
     */
    public Map<Long, Rangierung> findRangierungen4Equipments(Collection<Equipment> eqIds) throws FindException;

    /**
     * Sucht nach einer bestimmten Rangierung ueber die ID. <br> Diese Methode laedt zusaetzlich die Equipment-Modell
     * und uebergibt diese dem Rangierungs-Objekt. WICHTIG: die Methode unterstuetzt keine Transaktionen!!!
     *
     * @param rangierId
     * @return Objekt vom Typ <code>Rangierung</code> inkl. der ermittelten Equipment-Objekte.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     *
     */
    public Rangierung findRangierungWithEQ(Long rangierId) throws FindException;

    /**
     * Sucht nach einer <strong>freien</strong> Rangierung ueber ein Query-Objekt.
     *
     * @param query            Query-Objekt mit den Suchparametern.
     * @param endstelle        die Endstelle, der eine Rangierung zugeordnet werden soll
     * @param checkLtgGesId    Ist dieses Flag gesetzt, dann wird geprueft, ob alle Rangierungen mit der gleichen
     *                         Leitung-Gesamt-ID 'frei' sind.
     * @param childPhysiktypId (optional) ist dieser Parameter angegeben, muss innerhalb der gebuendelten Rangierung
     *                         (ueber LTG_GESAMT_ID) eine Rangierung mit dem Physiktyp <code>childPhysiktypId</code>
     *                         vorhanden sein.
     * @param uevtClusterNo    optionale UEVT-Cluster-No oder {@code null}, falls diese nicht beachtet/geprüft werden
     *                         soll
     * @param uetv             optionales Uebertragungsverfahren oder {@code null}, falls dieses nicht beachtet/geprüft
     *                         werden soll
     * @param bandwidth        optionale Up-/Downstream Bandbreite
     * @return Instanz von <code>Rangierung</code> oder <code>null</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public Rangierung findFreieRangierung(RangierungQuery query, Endstelle endstelle, boolean checkLtgGesId,
            Long childPhysiktypId, Integer uevtClusterNo, Uebertragungsverfahren uetv, Bandwidth bandwidth)
            throws FindException;

    /**
     * @param query            Query-Objekt mit den Suchparametern.
     * @param checkLtgGesId    Ist dieses Flag gesetzt, dann wird geprueft, ob alle Rangierungen mit der gleichen
     *                         Leitung-Gesamt-ID 'frei' sind.
     * @param childPhysiktypId (optional) ist dieser Parameter angegeben, muss innerhalb der gebuendelten Rangierung
     *                         (ueber LTG_GESAMT_ID) eine Rangierung mit dem Physiktyp <code>childPhysiktypId</code>
     *                         vorhanden sein.
     * @param bandwidth        optionale Up-/Downstream Bandbreite
     * @return Liste mit den freien Rangierungen.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     * freien Rangierungen zurueck gegeben, die den Such-Parametern entsprechen.
     */
    public List<Rangierung> findFreieRangierungen(RangierungQuery query, boolean checkLtgGesId,
            Long childPhysiktypId, Bandwidth bandwidth) throws FindException;

    /**
     * Ermittelt zu den angegebenen Rangierungen diverse Daten. Unter anderem sind dies: <ul> <li>zugeordnete Rangierung
     * (ueber LeitungGesamtId) <li>Hardware-Baugruppe <li>Equipment-Daten </ul>
     *
     * @param rangierungen Liste der Rangierungen
     * @param kvzNummer    KVZ Nummer nach der gefiltert werden soll (optional)
     * @return (evtl. gefilterte) Liste der Rangierungen mit ermittelten Daten
     * @throws FindException
     */
    public List<RangierungsEquipmentView> createRangierungsEquipmentView(List<Rangierung> rangierungen, String kvzNummer)
            throws FindException;

    /* Ermittelt das HW-Rack zu einem Port. */
    @CheckForNull
    HWRack getHWRackForEquipment(Equipment equipment, HWService hwService) throws FindException;

    /**
     * Sucht nach den Rangierungen eines Auftrags fuer eine best. Endstelle.
     *
     * @param auftragId ID des Auftrags
     * @param esTyp     Endstellen-Typ deren Rangierung gesucht wird.
     * @return Array von <code>Rangierung</code> oder <code>null</code>. An Index 0 ist die erste Rangierung der
     * Endstelle, an Index 1 (optional) die zusaetzliche Rangierung der Endstelle eingetragen.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public Rangierung[] findRangierungen(Long auftragId, String esTyp) throws FindException;

    /**
     * @see #findRangierungen(Long, String)  Diese Methode unterstuetzt bestehende Transaktionen!!!
     */
    public Rangierung[] findRangierungenTx(Long auftragId, String esTyp) throws FindException;

    @CheckForNull
    Rangierung[] findRangierungen(Long auftragId, String esTyp,
            Function2<Endstelle, Long, String> findEs4Auftrag) throws FindException;

    /**
     * Ermittelt die Rangierung, mit der die Rangierung mit der ID <code>rangierId</code> gekreuzt wurde. <br> Vorgang:
     * <ul> <li>Ursprungs-Datensatz von Rangierung <code>rangierId</code> ermitteln (ueber HISTORY_FROM_RID)
     * <li>aktuelle Rangierung ermitteln, die als EQ-Out/In die EQ-Out/In der zuvor ermittelten Rangierung besitzt
     * </ul>
     *
     * @param rangierId       ID der Rangierung dessen zugehoerige Kreuzung ermittelt werden soll
     * @param filterWithEqOut Flag, ob nach den aktuellen EQ-Out (true) oder EQ-In (false) Stiften fuer die Kreuzung
     *                        gesucht werden soll.
     * @return Objekt vom Typ <code>Rangierung</code> das die zugehoerige Kreuzung von <code>rangierId</code> darstellt
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     */
    public Rangierung findKreuzung(Long rangierId, boolean filterWithEqOut) throws FindException;

    /**
     * Sucht nach einer Rangierung, die von der Rangierung mit der ID <code>rangierungId</code> historisiert wurde.
     *
     * @param rangierungId ID der Ursprungs-Rangierung
     * @return Historisierung der urspruenglichen Rangierung
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public Rangierung findHistoryFrom(Long rangierungId) throws FindException;

    /**
     * Sucht nach allen Rangierungen, die eine best. Leitung-Gesamt-ID besitzen (= Rangierungen, die zusammen
     * gehoeren).
     *
     * @param ltgGesId Leitung-Gesamt-ID
     * @return Liste mit Objekten des Typs <code>Rangierung</code>, moeglicherweise leer, nie {@code null}.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public List<Rangierung> findByLtgGesId(Integer ltgGesId) throws FindException;

    /**
     * Ermittelt zur angegebenen Rangierung die zugeordnete Rangierung.
     *
     * @param rangierung4Match Rangierung, zu der die zugeordnete Rangierung ermittelt werden soll.
     * @return Rangierung, die der angegebenen Rangierung zugeordnet ist
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public Rangierung findRangierungsMatch(Rangierung rangierung4Match) throws FindException;

    /**
     * Sucht nach einer freien Rangierung fuer eine best. Endstelle und ordnet sie dieser zu.
     *
     * @param endstelleId     ID der Endstelle, fuer die eine Rangierung gesucht wird. <br>
     * @param serviceCallback Callback-Objekt, an das der Service 'nachfragen' richten kann. ServiceCallback-Key:
     *                        CALLBACK_ASK_4_BUENDEL_PRODUKT Parameter: CALLBACK_PARAM_PARENT_PRODUKT_ID = ID des
     *                        Parent-Produkts. <br>
     * @return Die Rangierung, die der Endstelle zugeordnet wurde.
     * @throws FindException  wenn bei der Abfrage Fehler auftreten oder keine freie Rangierung gefunden wird.
     * @throws StoreException wenn zwar eine freie Rangierung gefunden, diese aber der Endstelle nicht zugeordnet werden
     *                        konnte.
     */
    public Rangierung assignRangierung2ES(Long endstelleId, IServiceCallback serviceCallback) throws FindException,
            StoreException;

    /**
     * @param endstelleId     ID der Endstelle, fuer die eine Rangierung gesucht wird.
     * @param ceDetails       (optional) wird dieser Parameter uebergeben, erfolgt die Suche nach der passenden
     *                        Rangierung nicht(!) ueber die Rangierungsmatrix. Hier werden dann lediglich die Daten des
     *                        Carrier-Equipments (EQ_OUT) mit den Detail-Parametern abgeglichen!
     * @param serviceCallback Callback-Objekt, an das der Service 'nachfragen' richten kann. ServiceCallback-Key:
     *                        CALLBACK_ASK_4_BUENDEL_PRODUKT Parameter: CALLBACK_PARAM_PARENT_PRODUKT_ID = ID des
     *                        Parent-Produkts.
     * @return Die Rangierung, die der Endstelle zugeordnet wurde.
     * @throws FindException  wenn bei der Abfrage Fehler auftreten oder keine freie Rangierung gefunden wird.
     * @throws StoreException wenn zwar eine freie Rangierung gefunden, diese aber der Endstelle nicht zugeordnet werden
     *                        konnte.
     * @see #assignRangierung2ES(Long, IServiceCallback)
     */
    public Rangierung assignRangierung2ES(Long endstelleId, CarrierEquipmentDetails ceDetails,
            IServiceCallback serviceCallback) throws FindException, StoreException;


    /**
     * Ermittelt fuer die angegebene Endstelle sowie techn. Standort eine moegliche Rangierung (bzw. Kombination aus
     * Rangierung u. Zusatz-Rangierung) und gibt diese zurueck. <br>
     * Im Gegensatz zu {@link de.augustakom.hurrican.service.cc.RangierungsService#assignRangierung2ES(Long, de.augustakom.common.service.iface.IServiceCallback)}
     * findet in dieser Methode keine Zuordnung der Rangierung zur Endstelle statt; die Rangierung wird lediglich als
     * Ergebnis zurueck geliefert. Eine moegliche Zuordnung muss vom Aufrufer durchgefuehrt werden!
     * @param endstelleId
     * @param hvtIdStandort
     * @return Pair mit den gefundenen Rangierungen; First: erste Rangierung; Second: optionale Zusatz-Rangierung.
     * @throws FindException
     */
    public Pair<Rangierung, Rangierung> findPossibleRangierung(Long endstelleId, Long hvtIdStandort)
            throws FindException;



    /**
     * Ordnet die angegebene Rangierung der Endstelle zu.
     *
     * @param rangierung
     * @param rangierungAdd
     * @param endstelle
     * @throws StoreException
     */
    public void attachRangierung2Endstelle(Rangierung rangierung, Rangierung rangierungAdd, Endstelle endstelle)
            throws StoreException, ValidationException;

    /**
     * Fuehrt eine Physik-Aenderung bzw. Uebernahme durch. <br>
     *
     * @param strategy        Gibt die Art der Physik-Uebernahme an (Konstante aus Modell 'PhysikaenderungsTyp').
     * @param auftragIdSrc    ID des Ursprungs-Auftrags, dessen Physik verwendet werden soll
     * @param auftragIdDest   ID des Auftrags, dem eine neue Physik zugeordnet werden soll
     * @param serviceCallback Callback-Objekt, an das der Service 'nachfragen' richten kann. ServiceCallback-Key: <br>
     *                        CALLBACK_ASK_4_BUENDEL_PRODUKT Parameter: CALLBACK_PARAM_PARENT_PRODUKT_ID = ID des
     *                        Parent-Produkts. Das Callback-Objekt muss eine Physiktyp-ID ermitteln, die einen Physiktyp
     *                        des angegebenen Produkts als Parent besitzt! <br> CALLBACK_ASK_KUENDIGUNG_AUTOMATIC_4_ANSCHLUSSUEBERNAHME
     *                        <br> CALLBACK_ASK_4_ACCOUNT_UEBERNAHME
     * @param sessionId       ID der aktuellen User-Session
     * @return Instanz von <code>AKWarnings</code> mit evtl. erzeugten Warnungen.
     * @throws StoreException wenn waehrend der Physik-Aenderung ein Fehler auftritt.
     */
    public AKWarnings physikAenderung(Long strategy, Long auftragIdSrc, Long auftragIdDest,
            IServiceCallback serviceCallback, Long sessionId) throws StoreException;

    /**
     * Laedt die Equipment-Objekt zu der angegebenen Rangierung und uebergibt sie an das Objekt.
     *
     * @param rangierung
     * @throws FindException wenn bei
     *
     */
    public void loadEquipments(Rangierung rangierung) throws FindException;

    /**
     * Sucht nach einem Equipment ueber die ID.
     *
     * @param eqId ID des gesuchten Equipments
     * @return Objekt vom Typ <code>Equipment</code> oder <code>null</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public Equipment findEquipment(Long eqId) throws FindException;

    /**
     * Ermittelt einen Equipment-Datensatz ueber die angegebene DSLAM-ID und den Hardware-Port.
     *
     * @param rackId     ID des DSLAM-Racks
     * @param hwEQN      eindeutiger Hardware Port.
     * @param rangSSType Schnittstellen-Typ (Wildcards koennen ebenfalls uebergeben werden)
     * @return Objekt vom Typ <code>Equipment</code> oder <code>null</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt oder die Anzahl der ermittelten Equipments > 1
     *                       ist.
     *
     */
    public Equipment findEquipment(Long rackId, String hwEQN, String rangSSType) throws FindException;

    /**
     * Ermittelt einen Equipment-Datensatz ueber die angegebene BaugruppenId und den Hardware-Port.
     *
     * @param hwBaugruppeId
     * @param hwEQN         eindeutiger Hardware Port.
     * @param rangSSType    Schnittstellen-Typ (Wildcards koennen ebenfalls uebergeben werden)
     * @return Objekt vom Typ <code>Equipment</code> oder <code>null</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt oder die Anzahl der ermittelten Equipments > 1
     *                       ist.
     *
     */
    public Equipment findEquipmentByBaugruppe(Long hwBaugruppeId, String hwEQN, String rangSSType) throws FindException;

    /**
     * Ermittelt alle Equipments, die dem Example-Objekt 'example' entsprechen.
     *
     * @param example     Example-Objekt mit den gesuchten Parametern
     * @param orderParams Angabe der Equipment-Parameter, ueber die sortiert werden soll.
     * @return Liste mit Objekten des Typs <code>Equipment</code>. Darf {@code null} sein.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     *
     */
    public List<Equipment> findEquipments(Equipment example, String... orderParams) throws FindException;

    /**
     * Ermittelt ein Equipment, das einer Endstelle zugeordnet ist.
     *
     * @param endstelle
     * @param findEquipmentAdditional Angabe, ob das Equipment von einer evtl. vorhandenen Zusatz-Rangierung (true)
     *                                ermittelt werden soll, oder von der Standard-Rangierung (false).
     * @param findEqOut               Angabe, ob das EQ_OUT (true) oder EQ_IN (false) Equipment der Rangierung ermittelt
     *                                werden soll
     * @return ermitteltes Equipment
     * @throws FindException
     */
    public Equipment findEquipment4Endstelle(Endstelle endstelle, boolean findEquipmentAdditional, boolean findEqOut)
            throws FindException;

    /**
     * Ermittelt alle Equipments, die dem Example-Objekt entsprechen. Zusaetzlich werden die Hardware-Informationen
     * (Baugruppen, Baugruppentyp, Rack) der Equipments geladen.)
     *
     * @param example
     * @param orderParams
     * @return
     * @throws FindException
     */
    public List<HardwareEquipmentView> findEquipmentViews(Equipment example, String[] orderParams) throws FindException;

    /**
     * Ermittelt je zwei Equipments, die den Suchparametern in 'example' entsprechen. <br> Die beiden Equipments muessen
     * dabei die identische HW_EQN besitzen. Als HW_SCHNITTSTELLE wird einmal jedoch xxx-IN und einmal xxx-OUT
     * erwartet.
     *
     * @param example Example-Objekt mit den gesuchten Parametern
     * @return Liste mit Objekten des Typs <code>EquipmentInOutView</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     *
     */
    public List<EquipmentInOutView> findEqInOutViews(Equipment example) throws FindException;

    /**
     * Ermittelt alle Equipments, die einer bestimmten HW-Baugruppe zugeordnet sind.
     *
     * @param baugruppeId ID der HW-Baugruppe, deren Equipments ermittelt werden sollen.
     * @return Liste mit den Equipments der Baugruppe, nie {@code null}
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     *
     */
    public List<Equipment> findEquipments4HWBaugruppe(Long baugruppeId) throws FindException;

    /**
     * Ermittelt die Anzahl der vorhandenen Equipment-Eintraege einer best. UEVT-Leiste.
     *
     * @param hvtIdStandort
     * @param rangVerteiler
     * @param rangLeiste1
     * @return Map mit der Anzahl Stifte. Als Key wird der Stift-Typ (z.B. 'N' oder 'H') verwendet, der zughoerige Value
     * enthaelt die Anzahl.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public Map<String, Integer> getEquipmentCount(Long hvtIdStandort, String rangVerteiler, String rangLeiste1)
            throws FindException;

    /**
     * Speichert das Equipment-Objekt ab.
     *
     * @param toSave zu speicherndes Objekt
     * @return
     * @throws StoreException wenn beim Speichern ein Fehler auftritt.
     */
    public Equipment saveEquipment(Equipment toSave) throws StoreException;

    /**
     * Speichert alle in der Liste angegebenen Equipment-Objekte ab.
     *
     * @param equipmentsToSave Liste mit den zu speichernden Objekten
     * @throws StoreException wenn beim Speichern ein Fehler auftritt
     */
    public void saveEquipments(Collection<Equipment> equipmentsToSave) throws StoreException;

    /**
     * Sucht nach allen Rangierungsmatrix-Eintraegen, die den Suchparametern des Query-Objekts entsprechen.
     *
     * @param query Query-Objekt mit den Suchparametern
     * @return Liste mit den nach der Prioritaet sortierten (descending) Rangierungsmatrix-Eintraegen
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public List<Rangierungsmatrix> findMatrix(RangierungsmatrixQuery query) throws FindException;

    /**
     * Speichert eine Rangierungsmatrix ab.
     *
     * @param toSave      zu speicherndes Matrix-Objekt.
     * @param sessionId   die aktuelle User-Session ID
     * @param makeHistory Flag, ob ein bestehender DS historisiert werden soll.
     * @return Abhaengig von <code>makeHistory</code> wird <code>toSave</code> zurueck gegeben oder ein neues Objekt vom
     * Typ <code>Rangierungsmatrix</code>.
     * @throws ValidationException wenn ungueltige Daten zum Speichern uebergeben wurden.
     * @throws StoreException      wenn beim Speichern ein Fehler auftritt.
     */
    public Rangierungsmatrix saveMatrix(Rangierungsmatrix toSave, Long sessionId, boolean makeHistory)
            throws ValidationException, StoreException;

    /**
     * Loescht eine best. Rangierungsmatrix. <br> Die Matrix wird allerdings nicht komplett geloescht, sondern nur das
     * 'GueltigBis'-Datum auf 'jetzt' gesetzt.
     *
     * @param toDelete  zu loeschende Rangierungsmatrix
     * @param sessionId ID der aktuellen User-Session
     * @throws DeleteException wenn beim Loeschen bzw. deaktivieren ein Fehler auftritt.
     */
    public void deleteMatrix(Rangierungsmatrix toDelete, Long sessionId) throws DeleteException;

    /**
     * Erzeugt Standard-Rangierungsmatrix Eintraege fuer die in den Listen enthaltenen UEVTs und Produkte. <br> Bei der
     * Anlage wird ueberprueft, ob fuer eine Kombination UEVT/Produkt/Produkt2Physiktyp bereits ein gueltiger Eintrag
     * besteht. Ist dies der Fall, wird keine neue Matrix angelegt!
     *
     * @param sessionId    ID der User-Session um den Benutzer ermitteln zu koennen.
     * @param uevtIds      Liste mit den UEVT-IDs fuer die die Matrix erzeugt werden soll.
     * @param produktIds   Liste mit den Produkt-IDs fuer die die Matrix erzeugt werden soll.
     * @param physikTypIds (optional) Liste der Physiktypen, die beruecksichtigt werden sollen.
     * @return Liste mit den angelegten Rangierungsmatrizen.
     * @throws StoreException wenn bei der Anlage ein Fehler auftritt.
     */
    public List<Rangierungsmatrix> createMatrix(Long sessionId, List<Long> uevtIds, List<Long> produktIds,
            List<Long> physikTypIds)
            throws StoreException;

    /**
     * Protokolliert die an einer Physik-Uebernahme beteiligten Auftraege.
     *
     * @param auftragIdOld  ID des 'alten' Auftrags
     * @param auftragIdNew  ID des 'neuen' Auftrags
     * @param aenderungstyp Art der Physik-Aenderung.
     */
    public void logPhysikUebernahme(Long auftragIdOld, Long auftragIdNew, Long aenderungstyp);

    /**
     * Fuehrt eine Kreuzung des EQ_OUT Ports fuer die angegebenen Rangierungen durch. Die Rangierungen werden dabei
     * historisiert. Die Endstelle erhaelt die Referenzen der neuen Rangierungen.
     *
     * @param changeReason
     * @param endstelle
     * @param rangierungToChange    uspruengliche Rangierung
     * @param rangierungAddToChange urspruengliche Zusatz-Rangierung
     * @param rangierungToUse       zukuenftige Rangierung fuer die Endstelle; diese Rangierung erhaelt die EQ_OUT ID
     *                              von <code>rangierungToChange</code>
     * @param rangierungAddToUse    zukuenftige Zusatz-Rangierung fuer die Endstelle
     * @param sessionId             Session-ID des aktuellen Users
     * @return eine Liste mit allen Rangierungen (die uebergebenen Rangierungen, sowie die durch die Historisierung neu
     * entstandenen Rangierungen)
     */
    public List<Rangierung> changeEqOut(Reference changeReason, Endstelle endstelle,
            Rangierung rangierungToChange, Rangierung rangierungAddToChange,
            Rangierung rangierungToUse, Rangierung rangierungAddToUse, Long sessionId) throws StoreException, ValidationException;

    /**
     * Kreuzt die EQ-In und/oder EQ-Out IDs der beiden Rangierungen.
     *
     * @param rangSrc         Rangierung die gekreuzt werden soll
     * @param rangDest        Rangierung, mit der die Kreuzung durchgefuehrt werden soll
     * @param eqOut           Flag, ob die EQ-Out IDs gekreuzt werden sollen
     * @param eqIn            Flag, ob die EQ-In IDs gekreuzt werden sollen
     * @param updateES        Flag, ob in den zugehoerigen Endstellen die neue Rangierungs-ID aufgenommen werden soll.
     * @param switchCB        Schreibt die Carrierbestellungen (falls vorhanden) der Endstellen um. (Die CBs der
     *                        Endstelle von Rangierung A werden der Endstelle von Rangierung B uebergeben und
     *                        umgekehrt.) Wichtig: dieser Parameter wird nur dann beruecksichtigt, wenn das Flag
     *                        <code>updateES</code> auf <code>true</code> gesetzt ist.
     * @param ignorePhysiktyp Flag, ob der Physiktyp der beiden Rangierung beachtet werden soll.
     * @param physikaenderung Typ der Physikaenderung (muss gesetzt werden, wenn ein Eintrag in die Tabelle
     *                        T_PHYSIKUEBERNAHME erstellt werden soll).
     * @return Liste mit den NEU erstellten/gekreuzten Rangierungen
     * @throws StoreException
     */
    public List<Rangierung> rangierungenKreuzen(Rangierung rangSrc, Rangierung rangDest, boolean eqOut, boolean eqIn,
            boolean updateES, boolean switchCB, boolean ignorePhysiktyp, Long physikaenderung) throws StoreException;

    /**
     * Ueberprueft die Physiktyp-Zuordnung der Auftraege ab Auftrags-ID <code>startAuftragId</code>. Auftraege, deren
     * Physiktyp-Zuordnung fehlerhaft ist, werden protokolliert und in der Result-Liste zurueck gegeben.
     *
     * @param startAuftragId Auftrags-ID, ab der die Pruefung durchgefuehrt werden soll
     * @param endAuftragId   Auftrags-ID, bis zu der die Pruefung durchgefuehrt werden soll
     * @return Map mit den Auftrag-IDs, denen ungueltige Physiktypen zugeordnet sind. Als Keys werden die Auftrag-IDs
     * verwendet, denen ein ungueltiger Physiktyp zugeordnet ist. Als Value ist ein Text mit entsprechendem Hinweis
     * enthalten.
     * @throws FindException wenn bei der Abfrage der benoetigten Daten ein Fehler auftritt.
     */
    public Map<Long, String> checkPhysiktypen(Long startAuftragId, Long endAuftragId)
            throws FindException;

    /**
     * Funktion liefert alle Equipment-Objekt anhand des HVT und der Leiste
     *
     * @param hvtId  ID des HVT_Standorts
     * @param leiste Leistenbezeichnung
     * @return Gesuchte Equipment-Objekte oder leere Liste
     * @throws FindException falls ein Fehler auftrat
     */
    public List<Equipment> findEQByLeiste(Long hvtId, String leiste) throws FindException;

    /**
     * Funktion liefert ein Equipment-Objekt anhand der Schaltangaben (HVT, Leiste, Stift)
     *
     * @param hvtId  ID des HVT_Standorts
     * @param leiste Leistenbezeichnung
     * @param stift  Stiftbezeichnung
     * @return Gesuchter Equipment-Datensatz
     * @throws FindException Falls ein Fehler auftrat
     *
     */
    public Equipment findEQByLeisteStift(Long hvtId, String leiste, String stift) throws FindException;

    /**
     * Funktion liefert ein Equipment-Objekt anhand der Schaltangaben (HVT, Verteiler, Leiste, Stift)
     *
     * @param hvtId     ID des HVT_Standorts
     * @param verteiler Verteilerbezeichnung
     * @param leiste    Leistenbezeichnung
     * @param stift     Stiftbezeichnung
     * @return Gesuchter Equipment-Datensatz
     * @throws FindException Falls ein Fehler auftrat
     */
    public Equipment findEQByVerteilerLeisteStift(Long hvtId, String verteiler, String leiste, String stift)
            throws FindException;

    /**
     * Funktion liefert ein Equipment-Objekt anhand der Schaltangaben (HVT, KVZ Nummer, KVZ Doppelader)
     *
     * @param hvtId ID des HVT_Standorts
     * @param kvzNr KVZ Nummer
     * @param kvzDA KVZ Doppelader
     * @return Gesuchter Equipment-Datensatz
     * @throws FindException Falls ein Fehler auftrat
     */
    public Equipment findEQByKVZNrKVZDA(Long hvtId, String kvzNr, String kvzDA) throws FindException;

    /**
     * Funktion erzeugt für jeden Stift einen Equipment-Eintrag (beginnend bei 01)
     *
     * @param hvtStandort HVT_Standort
     * @param leiste      Leistenbezeichnung
     * @param stifte      Anzahl der Stifte
     * @param sessionId   die aktuelle SessionId, um den Benutzer zu bestimmen
     * @throws StoreException falls ein Fehler auftrat
     */
    public void createPdhLeisten(HVTStandort hvtStandort, String leiste, Integer stifte, Long sessionId)
            throws StoreException;

    /**
     * Liefert die naechste verfuegbare LeitungGesamtId der Rangierung
     *
     * @return MaxLeitungGesamtId + 1
     * @throws FindException Falls ein Fehler auftrat
     *
     */
    public Integer findNextLtgGesId4Rangierung() throws FindException;

    /**
     * Prüft, ob der übergebene Port in einem HWRack vom Typ 'RACK_TYPE_DSLAM' ist.
     */
    boolean isPortInADslam(Equipment port) throws FindException;

    /**
     * Prüft, ob die übergebene Liste an Ports komplett frei ist und für die Ports gilt: EsId = null und Freigegeben =
     * 1.<br/> Ein Port gilt auch als frei wenn keine Rangierung dafür existiert.
     */
    boolean isListeOfPortsFree(List<Equipment> ports) throws FindException;

    /**
     * Prüft, ob der übergebene Port frei ist und ob für den Port gilt: EsId = null und Freigegeben = 1.<br/> Ein Port
     * gilt auch als frei wenn keine Rangierung dafür existiert.
     */
    boolean isPortFree(Equipment port) throws FindException;

    /**
     * Funktion liefert <code>anzahl</code> aufeinanderfolgender Ports beginnend mit <code>firstPort</code>.
     *
     * @param firstPort
     * @param anzahl
     * @return Liste der Ports oder <code>null</code> falls nicht genügend Ports vorhanden sind.
     */
    public List<Equipment> findConsecutivePorts(Equipment firstPort, int anzahl) throws FindException;

    /**
     * Funktion liefert <code>anzahl</code> aufeinanderfolgender DTAG UeVT Stifte beginnend mit <code>ersterStift</code>
     * .
     *
     * @param ersterStift
     * @param anzahl
     * @return Liste der Ports oder <code>null</code> falls nicht genügend Ports vorhanden sind.
     */
    public List<Equipment> findConsecutiveUEVTStifte(Equipment ersterStift, int anzahl) throws FindException;

    /**
     * Legt das Schicht2-Protokoll <code>protocol</code> auf den Ports <code>consescutivePorts</code> fest. In den
     * Service ausgelagert um eine Transaktion zu haben.
     *
     * @param consecutivePorts
     * @param protocol
     */
    public void setLayer2ProtocolForPorts(List<Equipment> consecutivePorts, Schicht2Protokoll protocol)
            throws StoreException;

    /**
     * Erzeugt alle {@link RangierungWithEquipmentView}s für die übergebenen {@link Equipment}s. Dabei werden die nicht
     * mehr gültigen {@link Rangierung}en ausgeschlossen. Sollten bei dein übergebenen Ids auch {@link Rangierung}Adds
     * dabei sein, werden diese nicht nur zur zugehörigen {@link Rangierung} angezeigt. (Matching über die
     * Leitungs-Gesamt-Id und Leitung-Lfd-Nr)
     *
     * @param rangierungIds Menge mit Ids von {@link Rangierung} zu denen die {@link RangierungWithEquipmentView}s
     *                      geladen werden sollen
     * @return Liste mit {@link RangierungWithEquipmentView}s zu den übergebenen {@link Equipment}s (never {@code null})
     */
    public List<RangierungWithEquipmentView> findRangierungWithEquipmentViews(Set<Long> rangierungIds);

    /**
     * @param eq
     * @return
     */
    public Equipment findCorrespondingEquipment(Equipment eq);

    /**
     * @param rangierung für die der UEVT des EqOUt-Stifts geladen werden soll
     * @return liefert den UEVT für den EqOut-Stift der übergebenen Rangierung.
     * @throws FindException falls kein EqOut-Stift für die Rangierung gefunden, falls dieser keinen UEVT
     *                       (Rang-Verteiler) gesetzt hat oder falls beim Laden des Uevt ein Fehler auftritt
     */
    public UEVT findUevt(Rangierung rangierung) throws FindException;

    /**
     * Prueft ob die benoetigte Bandbreite fuer eine Rangierung moeglich ist. Maximale Bandbreiten sind auf dem
     * Physiktyp UND dem Baugruppentyp hinterlegt. Das Ergebnis ist immer {@code true} ausser {@code
     * PhysikTyp.maxBandwidth} ist verfuegbar und kleiner als {@code requiredBandwidth} ODER {@code
     * HWBaugruppenTyp.maxBandwidth} ist verfuegbar und kleiner als {@code requiredBandwidth}. Ist {@code
     * requiredBandwidth} gleich {@code null} so ist die Bandbreite moeglich!
     *
     * @param rangierung        Rangierung fuer die die Pruefung ausgefuehrt werden soll
     * @param requiredBandwidth benoetigte Bandbreite (optional, nicht jeder Auftrag/Rangierung benoetigt Bandbreiten)
     */
    boolean isBandwidthPossible4Rangierung(@CheckForNull Rangierung rangierung,
            @CheckForNull Bandwidth requiredBandwidth) throws FindException;

    Rangierung[] findRangierungenTxWithoutExplicitFlush(Long auftragId, String esTyp) throws FindException;

    /**
     * Prüft, ob zum HurricanAuftrag mit der angegebenen Id eine Rangierung hinterlegt ist.
     *
     * @param auftragId HurricanAuftrag-Id
     * @param esTyp     Angabe der zu ueberpruefenden Endstelle
     * @return
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt
     */
    boolean hasRangierung(Long auftragId, String esTyp) throws FindException;
}
