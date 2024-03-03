/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.07.2004 13:51:29
 */
package de.augustakom.hurrican.service.cc;

import java.time.*;
import java.util.*;
import javax.annotation.*;
import net.sf.jasperreports.engine.JasperPrint;

import de.augustakom.common.service.iface.IServiceCallback;
import de.augustakom.common.tools.lang.Either;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.common.tools.reports.AKReportException;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragAktion;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.AuftragWholesale;
import de.augustakom.hurrican.model.cc.Leitungsnummer;
import de.augustakom.hurrican.model.cc.Wohnheim;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.model.cc.hardware.HWSwitch;
import de.augustakom.hurrican.model.cc.query.AuftragSAPQuery;
import de.augustakom.hurrican.model.cc.temp.AuftragsMonitor;
import de.augustakom.hurrican.model.cc.temp.RevokeCreationModel;
import de.augustakom.hurrican.model.cc.temp.RevokeTerminationModel;
import de.augustakom.hurrican.model.cc.view.AuftragIntAccountQuery;
import de.augustakom.hurrican.model.cc.view.AuftragIntAccountView;
import de.augustakom.hurrican.model.cc.view.CCAuftragIDsView;
import de.augustakom.hurrican.model.cc.view.CCAuftragProduktVbzQuery;
import de.augustakom.hurrican.model.cc.view.CCAuftragProduktVbzView;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.model.shared.view.AuftragCarrierQuery;
import de.augustakom.hurrican.model.shared.view.AuftragCarrierView;
import de.augustakom.hurrican.model.shared.view.AuftragDatenQuery;
import de.augustakom.hurrican.model.shared.view.AuftragDatenView;
import de.augustakom.hurrican.model.shared.view.AuftragEndstelleQuery;
import de.augustakom.hurrican.model.shared.view.AuftragEndstelleView;
import de.augustakom.hurrican.model.shared.view.AuftragEquipmentQuery;
import de.augustakom.hurrican.model.shared.view.AuftragEquipmentView;
import de.augustakom.hurrican.model.shared.view.AuftragRealisierungQuery;
import de.augustakom.hurrican.model.shared.view.AuftragRealisierungView;
import de.augustakom.hurrican.model.shared.view.AuftragVorlaufView;
import de.augustakom.hurrican.model.shared.view.WbciRequestCarrierView;
import de.augustakom.hurrican.service.base.exceptions.DeleteException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.LoadException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.impl.command.RevokeCreationCommand;
import de.augustakom.hurrican.service.cc.impl.command.RevokeTerminationCommand;
import de.augustakom.hurrican.service.cc.utils.CalculatedSwitch4VoipAuftrag;

/**
 * Service-Interface, um CC-Auftraege zu verwalten.
 */
public interface CCAuftragService extends ICCService {

    /**
     * Wird fuer einen Service-Callback verwendet, um dem Aufrufer eine Information ueber einen angelegten Auftrag
     * mitzuteilen.
     */
    int CALLBACK_CREATE_AUFTRAG_INFO = 300;

    /**
     * Key fuer Callback Parameter-Map, um eine Info-Message zu uebergeben.
     */
    String CALLBACK_PARAM_CREATE_AUFTRAG_INFO = "create.auftrag.info";

    /**
     * Prueft ob die Anzahln an N-Draht-Option-Auftraegen die mit dem angegebenen Auftrag gebuendelt sind mit der
     * Konfiguration des Produkts (@see Produkt#sdslNDraht , @see SdslNdraht) uebereinstimmt
     *
     * @param auftragId ID des zu pruefenden Auftrags
     * @return Pair<CheckAnzNdrahtResult, Collection<AuftragDaten>> Ergebnis der pruefung + die betrachteten Auftraege
     * (leer bei Wert NO_NDRAHT_CONFIG)
     */
    Pair<CheckAnzNdrahtResult, Collection<AuftragDaten>> checkAnzahlNdrahtOptionAuftraege(long auftragId);

    /**
     * @see CCAuftragService#createAuftrag(Long, AuftragDaten, AuftragTechnik, Wohnheim, Long, IServiceCallback)
     */
    Auftrag createAuftrag(Long kundeNo, AuftragDaten aDaten, AuftragTechnik aTechnik,
            Long sessionId, IServiceCallback serviceCallback) throws StoreException;

    /**
     * Erstellt einen neuen Hurrican-Auftrag fuer den Kunden mit der Kundennummer <code>kundeNo</code>. <br> Die Objekte
     * <code>aDaten</code> und <code>aTechnik</code> werden dem neu angelegten Hurrican-Auftrag zugeordnet. Neben den
     * bereits gesetzten Eigenschaften erhalten die Objekte noch div. weitere Daten (z.B. Gueltig von, Gueltig bis
     * etc.). <br> <br> <strong>Das Objekt <code>aDaten</code> muss auf jeden Fall eine gueltige Produkt-ID besitzen!
     * Ist dies nicht der Fall, wird die Methode abgebrochen und eine Exception erzeugt.</strong> <br> <br> Wenn
     * notwendig, wird fuer den Auftrag auch eine VerbindungsBezeichnung (Leitungsnummer) erzeugt und dem Auftrag
     * zugeordnet. In diesem Fall werden auch die benoetigten Endstellen erzeugt und zugeordnet. <br> Tritt innerhalb
     * der Methode ein Fehler auf, werden alle bis zu diesem Zeitpunkt vorgenommenen DB-Aenderungen rueckgaengig
     * gemacht!
     *
     * @param kundeNo         Kundennummer
     * @param aDaten          AuftragDaten, die dem neuen Auftrag zugeordnet werden sollen
     * @param aTechnik        AutragTechnik, die dem neuen Auftrag zugeordnet werden soll
     * @param wohnheim        Wohnheim-Objekt, ueber das die VerbindungsBezeichnung ermittelt werden soll.
     * @param sessionId       Session-ID des aktuellen Users.
     * @param serviceCallback (optional) ServiceCallback fuer Informationen an den Aufrufer. <br> Das ServiceCallback
     *                        wird aufgerufen, wenn bei einer 'SDSL 4-Draht Option' ein alter SDSL-Auftrag gekuendigt
     *                        und ein neuer SDSL-Auftrag automatisch angelegt wurde.
     * @return Auftrag-Objekt, das neu angelegt wurde.
     * @throws StoreException wenn bei der Aktion ein Fehler auftritt.
     */
    Auftrag createAuftrag(Long kundeNo, AuftragDaten aDaten, AuftragTechnik aTechnik,
            Wohnheim wohnheim, Long sessionId, IServiceCallback serviceCallback) throws StoreException;

    /**
     * Erzeugt einen technischen Auftrag in Hurrican
     *
     * @param kundeNo   Kundennummer
     * @param auftragNo Auftragsnummer im Billing-System
     * @param sessionId
     * @return Erzeugtes Auftrag-Objekt
     * @throws StoreException Falls ein Fehler auftrat
     */
    Auftrag createTechAuftrag(Long kundeNo, Long auftragNo, Long sessionId) throws StoreException;

    /**
     * Ermittelt anhand der Billing-Haupt-/Unterauftragslogik die {@link AuftragDaten#buendelNr} und speichert diese.
     * Zudem wird ggf. bei den hinterlegten Hurrican-Hauptauftr?gen die entsprechende {@link BAuftrag#hauptAuftragNo}
     * als {@link AuftragDaten#buendelNr} hinterlegt. Ist eine unterschiedliche B?ndel-Nr bereits hinterlegt, wird eine
     * {@link StoreException} ausgel?st.
     *
     * @param ad       {@link AuftragDaten} Objekt des Hurrican-Auftrags
     * @param bAuftrag Billing-Auftrags Objekt {@link BAuftrag}
     * @throws FindException
     * @throws StoreException
     */
    void createBillingHauptauftragsBuendel(AuftragDaten ad, BAuftrag bAuftrag) throws FindException, StoreException;

    /**
     * Methode, um auf einem (aktiven) Auftrag einen Produktwechsel durchzufuehren. <br> Dabei wird der 'alte' Auftrag
     * samt Physik gekuendigt und ein neue Auftrag angelegt. <br> Die Physik des alten Auftrags wird auf den neuen
     * Auftrag uebernommen - die genaue Funktion ist durch den Physikaenderungstyp definiert.
     *
     * @param auftragIdOld        ID des 'alten', zu kuendigenden Auftrags
     * @param physikaenderungsTyp Art der Physikaenderung (z.B. Anschlussuebernahme)
     * @param newProdId           Produkt-ID fuer den neuen Auftrag
     * @param newAuftragNoOrig    neue Auftragsnummer aus dem Billing-System
     * @param date                Vorgabedatum fuer den neuen Auftrag und Kuendigungsdatum fuer den alten Auftrag
     * @param sessionId           Session-ID des Users
     * @param serviceCallback     ServiceCallback-Objekt
     * @return der generierte Auftrag
     * @throws StoreException wenn beim Produktwechsel ein Fehler auftritt.
     */
    Auftrag changeProduct(Long auftragIdOld, Long physikaenderungsTyp, Long newProdId,
            Long newAuftragNoOrig, Date date, Long sessionId, IServiceCallback serviceCallback) throws StoreException;

    /**
     * Kopiert den Auftrag mit der ID <code>id2Copy</code> <code>anzahlCopies</code>-Mal. Wird eine Buendel-Nr (und
     * Herkunft) angegeben, wird diese fuer die weiteren Auftraege verwendet. Ansonsten wird eine neue
     * Hurrican-Buendel-Nr erzeugt und in die Auftraege geschrieben. <br> <br> Wichtig: Es muss entweder
     * <code>parentAuftragId</code> oder <code>buendelNr</code> angegeben werden!
     *
     * @param sessionId       Session-ID des aktuellen Users.
     * @param id2Copy         ID des zu kopierenden Auftrags (zwingend erforderlich)
     * @param parentAuftragId ID des Auftrags, ueber den die Abrechnung erfolgt.
     * @param buendelNr       Angabe der Buendel-Nr oder <code>null</code>.
     * @param buendelHerkunft
     * @param anzahlCopies    Anzahl der anzulegenden Kopien (zwingend erforderlich)
     * @param vorgabeSCV      Vorgabe-SCV Datum fuer die Kopien
     * @param copyES          Flag, ob die Endstellen-Daten ebenfalls kopiert werden sollen (excl. Rangierung!)
     * @return Liste mit den erzeugten Auftraegen.
     * @throws StoreException wenn beim Kopieren ein Fehler auftritt.
     */
    List<Auftrag> copyAuftrag(Long sessionId, Long id2Copy, Long parentAuftragId, Integer buendelNr,
            String buendelHerkunft,
            int anzahlCopies, Date vorgabeSCV, boolean copyES) throws StoreException;

    /**
     * Erstellt eine Uebersicht zwischen den aktuellen CC- und Billing-Auftraegen eines Kunden.
     *
     * @param kundeNo           Kundennummer des Kunden, fuer den der Auftragsmonitor erstellt werden soll.
     * @param taifunOrderNoOrig (optional!) Angabe eines Taifun Auftrags, fuer den der Abgleich durchgefuehrt werden
     *                          soll
     * @return Liste mit Objekten des Typs <code>AuftragsMonitor</code>
     * @throws LoadException wenn bei der Erstellung des AMs ein Fehler auftritt.
     */
    List<AuftragsMonitor> createAuftragMonitor(Long kundeNo, Long taifunOrderNoOrig) throws LoadException;

    /**
     * Speichert das Auftrag-Object. <br>
     *
     * @param auftrag zu speicherndes Auftrag-Object
     * @return der uebergebene und gespeicherte Auftrag
     * @throws StoreException wenn beim Speichern ein Fehler auftritt.
     */
    Auftrag saveAuftrag(Auftrag auftrag) throws StoreException;

    /**
     * Speichert das AuftragDaten-Objekt. <br> In der DB wird der aktuelle Datensatz auf <code>gueltigBis</code> = jetzt
     * gesetzt und ein neuer Datensatz mit den Daten von <code>AuftragDaten</code> erzeugt.
     *
     * @param auftragDaten  zu speicherndes Objekt
     * @param createHistory Flag bestimmt, ob der Datensatz historisiert werden soll.
     * @return Abhaengig von createHistory wird <code>auftragDaten</code> oder eine neue Instanz von
     * <code>AuftragDaten</code> zurueck gegeben.
     * @throws StoreException wenn beim Speichern ein Fehler auftritt.
     */
    AuftragDaten saveAuftragDaten(AuftragDaten auftragDaten, boolean createHistory) throws StoreException;

    /**
     * Speichert das angegebene AuftragDaten Objekt. Transaktionen werden von dieser Methode NICHT unterstuetzt!
     */
    void saveAuftragDatenNoTx(AuftragDaten auftragDaten) throws StoreException;

    /**
     * gleiche Funktion wie bei <code>saveAuftragDaten(AuftragDaten)</code>, nur mit Objekten des Typs
     * <code>AuftragTechnik</code>.
     *
     * @param auftragTechnik zu speicherndes Objekt (das neu angelegt wird)
     * @param createHistory  Flag bestimmt, ob der Datensatz historisiert werden soll.
     * @return Abhaengig von createHistory wird <code>auftragTechnik</code> oder eine neue Instanz von
     * <code>AuftragTechnik</code> zurueck gegeben.
     * @throws StoreException wenn beim Speichern ein Fehler auftritt.
     */
    AuftragTechnik saveAuftragTechnik(AuftragTechnik auftragTechnik, boolean createHistory)
            throws StoreException;

    /**
     * Sucht nach einem CC-Auftrag ueber dessen ID.
     *
     * @param ccAuftragId ID des CC-Auftrags
     * @return CC-Auftrag.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    Auftrag findAuftragById(Long ccAuftragId) throws FindException;

    /**
     * Sucht nach der CC-Auftrags-ID und der VerbindungsBezeichnung (Technischen Dokumentationsnummer) ueber die
     * (original) Auftrags-Nummer aus dem Billing-System und fasst diese mit den Ergebnissen aus der Suche via Buendel
     * und Buendelherkunft zusammen.
     *
     * @param auftragNoOrig (original) Auftrags-Nummer aus dem Billing-System.
     * @return Liste mit Objekten des Typs <code>CCAuftragIdsView</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    Collection<CCAuftragIDsView> findAufragIdAndVbz4AuftragNoOrigAndBuendel(Long auftragNoOrig, Integer buendelNr,
            String buendelNrHerkunft) throws FindException;

    /**
     * Sucht nach der CC-Auftrags-ID und der VerbindungsBezeichnung (Technischen Dokumentationsnummer) ueber die
     * (original) Auftrags-Nummer aus dem Billing-System.
     *
     * @param auftragNoOrig (original) Auftrags-Nummer aus dem Billing-System.
     * @return Liste mit Objekten des Typs <code>CCAuftragIdsView</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    List<CCAuftragIDsView> findAufragIdAndVbz4AuftragNoOrig(Long auftragNoOrig) throws FindException;

    /**
     * @param buendel           Buendel-Nr
     * @param buendelNrHerkunft Angabe, aus welchem System die Buendel-Nr stammt (siehe Konstanten in {@link
     *                          de.augustakom.hurrican.model.cc.AuftragDaten})
     * @return Liste mit Objekten des Typs <code>CCAuftragIdsView</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     * @see CCAuftragService#findAufragIdAndVbz4AuftragNoOrig Statt ueber die (original) Auftragsnummer wird ueber die
     * Bundel-Nr gesucht.
     */
    List<CCAuftragIDsView> findAuftragIdAndVbz4Buendel(Integer buendel, String buendelNrHerkunft)
            throws FindException;

    /**
     * Befüllt die Auftragsviews zu einer Liste von Hurrican-AuftragsIDs.
     */
    List<CCAuftragIDsView> findAuftragIdAndVbz4AuftragIds(Collection<Long> auftragIds) throws FindException;

    /**
     * Sucht nach Auftragsdaten. <br>
     *
     * @param query           Query-Objekt mit den Suchparametern
     * @param loadBillingData ueber dieses Flag kann bestimmt werden, ob auch die zugehoerigen Daten aus dem
     *                        Billing-System geladen werden sollen.
     * @return Liste mit Objekten des Typs <code>AuftragDatenView</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    List<AuftragDatenView> findAuftragDatenViews(AuftragDatenQuery query, boolean loadBillingData)
            throws FindException;

    /**
     * Sucht nach Auftragsdaten ueber SAP IDs
     *
     * @param query Query-Objekt mit den Suchparameter
     * @return Liste mit Objekten des Typs <code>AuftragDatenView</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    List<AuftragDatenView> findAuftragDatenViews(AuftragSAPQuery query) throws FindException;

    /**
     * Sucht nach Auftragsdaten, deren Inbetriebnahme (oder Vorgabe-SCV) bzw. Realisierungstermin innerhalb eines best.
     * Zeitraums liegt.
     *
     * @param query           Query-Objekt mit den Suchparametern
     * @param loadBillingData Flag, ob die zugehoerigen Auftragsdaten aus dem Billing-System geladen werden sollen.
     * @return Liste mit Objekten des Typs <code>AuftragInbetriebnahmeView</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    List<AuftragRealisierungView> findRealisierungViews(AuftragRealisierungQuery query,
            boolean loadBillingData) throws FindException;

    /**
     * Sucht nach den Auftrags-Daten ueber eine CC-AuftragsID. <br> WICHTIG: <br> Die Methode unterstuetzt keine
     * Transaktionen! <br> Soll ein Auftrag innerhalb einer Transaktion geladen werden, muss die Methode
     * <code>findAuftragDatenByAuftragIdTx</code> verwendet werden!
     *
     * @param auftragId ID des CC-Auftrags dessen Daten geladen werden sollen.
     * @return AuftragDaten
     * @throws FindException wenn beim Laden ein Fehler auftritt.
     */
    AuftragDaten findAuftragDatenByAuftragId(Long auftragId) throws FindException;

    /**
     * @see CCAuftragService#findAuftragDatenByAuftragId(Long) Diese Methode unterstuetzt bestehende Transaktionen!!!
     */
    AuftragDaten findAuftragDatenByAuftragIdTx(Long auftragId) throws FindException;

    /**
     * Sucht nach den Auftrags-Daten ueber eine Kundennummer.
     */
    List<AuftragDaten> findAuftragDatenByKundeNo(Long kundeNo) throws FindException;

    /**
     * Sucht nach allen Auftraegen, die zu einem bestimmten Buendel gehoeren. <br> WICHTIG: <br> Die Methode
     * unterstuetzt keine Transaktionen! <br> Soll ein Auftrag innerhalb einer Transaktion geladen werden, muss die
     * Methode <code>findAuftragDaten4BuendelTx</code> verwendet werden!
     *
     * @param buendelNr         Buendel-Nr
     * @param buendelNrHerkunft
     * @return Liste mit Objekten des Typs <code>AuftragDaten</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    List<AuftragDaten> findAuftragDaten4Buendel(Integer buendelNr, String buendelNrHerkunft)
            throws FindException;

    /**
     * @see CCAuftragService#findAuftragDaten4Buendel(Integer, String) Diese Methode unterstuetzt bestehende
     * Transaktionen!!!
     */
    List<AuftragDaten> findAuftragDaten4BuendelTx(Integer buendelNr, String buendelNrHerkunft)
            throws FindException;

    /**
     * Sucht nach allen Auftragsdaten zu einer bestimmten Billing-Auftragsnummer. (Auftraege mit Status 'storno',
     * 'Absage' oder 'konsolidiert' werden NICHT betrachtet!)
     *
     * @param orderNoOrig
     * @return Liste mit Objekten des Typs <code>AuftragDaten</code>.
     */
    List<AuftragDaten> findAuftragDaten4OrderNoOrig(Long orderNoOrig) throws FindException;

    List<AuftragDaten> findAuftragDaten4OrderNoOrigTx(Long orderNoOrig) throws FindException;

    /**
     * Sucht nach allen Auftragsdaten zu einer bestimmten Billing-Auftragsnummer. (Auftraege mit Status 'storno',
     * 'Absage' oder 'konsolidiert' WERDEN betrachtet!)
     *
     * @param orderNoOrig
     * @return Liste mit Objekten des Typs <code>AuftragDaten</code>.
     */
    List<AuftragDaten> findAllAuftragDaten4OrderNoOrig(Long orderNoOrig) throws FindException;

    List<AuftragDaten> findAllAuftragDaten4OrderNoOrigTx(Long orderNoOrig) throws FindException;

    /**
     * Sucht nach den Auftrags-Daten zu einer bestimmten Endstelle.
     *
     * @param endstelleId ID der Endstelle, deren Auftrags-Daten ermittelt werden sollen.
     * @return Instanz von <code>AuftragDaten</code> oder <code>null</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    AuftragDaten findAuftragDatenByEndstelle(Long endstelleId) throws FindException;

    /**
     * @see CCAuftragService#findAuftragDatenByEndstelle(Long) Diese Methode unterstuetzt bestehende Transaktionen!!!
     */
    AuftragDaten findAuftragDatenByEndstelleTx(Long endstelleId) throws FindException;

    /**
     * Sucht nach Auftrags-Daten, deren Endstelle der GeoId zugeordnet ist. Optional kann noch auf eine Liste von
     * Produkten (deren ID) gefiltert werden.
     *
     * @param geoId      GeoId der Endstelle
     * @param produktIds [optional] Produkt IDs
     * @return Liste mit AuftragDaten
     */
    List<AuftragDaten> findAuftragDatenByGeoIdProduktIds(Long geoId, Long... produktIds);

    /**
     * Sucht nach allen AuftragDaten-Datensaetzen, die einen bestimmten Port nutzen oder nutzten.
     *
     * @param equipmentId ID des Equipments
     * @return Liste von AuftragDaten, eventuell leer
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    List<AuftragDaten> findAuftragDatenByEquipment(Long equipmentId) throws FindException;

    /**
     * Sucht Auftragsdaten nach dem Equipment switch und hwEQN bzw. gültigkeit der Auftragsdaten/technik. <br>
     * <b>Achtung:</b> sollte nur für "echte" (EWSD) switches verwendet werden, da ansonsten die Treffermenge riesig
     * werden kann.
     *
     * @param switchAK switch name z.B. AUG01
     */
    List<AuftragDaten> findAuftragDatenByEquipment(String switchAK, String hwEQN, Date gueltig) throws FindException;

    /**
     * Sucht Auftragsdaten nach der Rack(DSLAM) Gerätebezeichnung und hwEQN bzw. Gültigkeit der Auftragsdaten/technik.
     */
    List<AuftragDaten> findAuftragDatenByRackAndEqn(String rackBezeichnung, String hwEQN, Date gueltig)
            throws FindException;

    /**
     * Sucht nach allen AuftragDaten-Datensaetzen, die einen Port auf einer bestimmten Baugruppe nutzen oder nutzten.
     *
     * @param baugruppeId
     * @return Liste von AuftragDaten, eventuell leer
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    List<AuftragDaten> findAuftragDatenByBaugruppe(Long baugruppeId) throws FindException;

    /**
     * Sucht nach dem Vater-Produkt bzw. dem Vater-Auftrag eines best. Buendels. <br> <br> Es handelt sich dann um das
     * Vater-Produkt bzw. den Vater-Auftrag, wenn das Flag 'isParent' des zugehoerigen Produkts auf <code>true</code>
     * gesetzt ist. <br> Es wird natuerlich nur nach den aktuellen(!) Auftrags-Daten gesucht. <br> Ausserdem darf der
     * Status der Auftrags-Daten nicht auf 'storno' oder 'Absage' stehen und darf auch nicht gekuendigt sein (Status <
     * Kuendigung).
     *
     * @param kundeNo         Kundennummer die dem Auftrag zugeordnet sein muss.
     * @param buendelNr       Nr. des Buendels, von dem der Parent-Auftrag gesucht wird.
     * @param buendelHerkunft Name der Buendel-Herkunft fuer die gesucht Auftrags-Daten.
     * @return Instanz von <code>AuftragDaten</code> oder <code>null</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    AuftragDaten findParentAuftragDaten(Long kundeNo, Integer buendelNr, String buendelHerkunft)
            throws FindException;

    /**
     * Sucht nach allen Auftraegen eines Kunden, die sperr-relevant sind.
     *
     * @param kundeNo Kundennummer des Kunden, dessen sperr-relevante Auftraege gesucht werden.
     * @return Liste mit Objekten des Typs <code>AuftragDaten</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    List<AuftragDaten> findAuftragDaten4Sperre(Long kundeNo) throws FindException;

    /**
     * Sucht nach dem Hauptauftrag zu einem SIP Inter Trunk Endkunden Auftrag
     *
     * @param auftragId ID des Sip Inter Trunk Endkunden Auftrags
     * @return <code>AuftragDaten</code> des Hauptauftrags (SIP Inter Trunk)
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    AuftragDaten findMainOrder4SIPCustomer(Long auftragId) throws FindException;

    /**
     * Sucht nach dem aktuellen AuftragTechnik-Datensatz ueber die CC-AuftragsID. <br> WICHTIG: <br> Die Methode
     * unterstuetzt keine Transaktionen! <br> Soll ein Auftrag innerhalb einer Transaktion geladen werden, muss die
     * Methode <code>findAuftragTechnikByAuftragIdTx</code> verwendet werden!
     *
     * @param auftragId ID des CC-Auftrags, dessen techn. Daten geladen werden sollen.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    AuftragTechnik findAuftragTechnikByAuftragId(Long auftragId) throws FindException;

    /**
     * @see CCAuftragService#findAuftragTechnikByAuftragId(Long) Diese Methode unterstuetzt bestehende Transaktionen!!!
     */
    AuftragTechnik findAuftragTechnikByAuftragIdTx(Long auftragId) throws FindException;

    /**
     * Sucht nach dem aktuellen AuftragTechnik-Datensatz, der einer best. Endstellen-Gruppe zugeordnet ist.
     *
     * @param esGruppeId ID der Endstellen-Gruppe.
     * @return Objekt vom Typ AuftragTechnik oder <code>null</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    AuftragTechnik findAuftragTechnik4ESGruppe(Long esGruppeId) throws FindException;

    /**
     * Sucht nach allen AuftragTechnik-Datensaetzen, denen ein best. IntAccount zugeordnet ist.
     *
     * @param intAccountId ID des IntAccounts.
     * @return Liste mit Objekten des Typs <code>AuftragTechnik</code> (potenziell leer)
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    List<AuftragTechnik> findAuftragTechnik4IntAccount(Long intAccountId) throws FindException;

    /**
     * Sucht nach Auftrags- und Carrierdaten. <br>
     *
     * @param query Query-Objekt mit den Suchparametern
     * @return Liste mit Objekten des Typs <code>AuftragCarrierView</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    List<AuftragCarrierView> findAuftragCarrierViews(AuftragCarrierQuery query) throws FindException;

    /**
     * Ermittelt alle WBCI-Vorabstimmungen mit der angegebenen Id.
     *
     * @param vorabstimmungsId
     * @return
     * @throws FindException
     */
    List<WbciRequestCarrierView> findWbciRequestCarrierViews(String vorabstimmungsId) throws FindException;

    /**
     * Sucht nach Auftrags- und Endstellendaten. <br>
     *
     * @param query Query-Objekt mit den Suchparametern.
     * @return Liste mit Objekten des Typs <code>AuftragEndstelleView</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    List<AuftragEndstelleView> findAuftragEndstelleViews(AuftragEndstelleQuery query) throws FindException;

    /**
     * Sucht nach Auftrags- und Endstellendaten fuer VPN-Auftraege.
     *
     * @param vpnId ID des VPNs, dessen zugehoerige Auftraege ermittelt werden sollen. Wird fuer die ID
     *              <code>null</code> und fuer die Kunden-No eine Liste angegeben, werden alle Auftraege der
     *              entsprechenden Kunden gesucht, die KEINEM VPN zugeordnet sind.
     * @param kNos  Liste mit (original) Kundennummern, auf die die Suche eingeschraenkt werden soll.
     * @return Liste mit Objekten des Typs <code>AuftragEndstelleView</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    List<AuftragEndstelleView> findAuftragEndstelleViews4VPN(Long vpnId, List<Long> kNos)
            throws FindException;

    /**
     * Sucht nach Auftrags- und Equipment-Daten. <br>
     *
     * @param query Query-Objekt mit den Suchparametern.
     * @return Liste mit Objekten des Typs <code>AuftragEquipmentView</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    List<AuftragEquipmentView> findAuftragEquipmentViews(AuftragEquipmentQuery query) throws FindException;

    /**
     * Sucht nach Auftrags-, Produkt- und Leitungsdaten. <br>
     *
     * @param query Query-Objekt mit den Suchparametern.
     * @return Liste mit Objekten des Typs <code>CCAuftragProduktVbzView</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    List<CCAuftragProduktVbzView> findAuftragProduktVbzViews(CCAuftragProduktVbzQuery query)
            throws FindException;

    /**
     * Sucht nach allen Auftraegen (ausser AK-Connect), die in der Zukunft anstehen.
     *
     * @return Liste mit Objekten des Typs <code>AuftragVorlaufView</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    List<AuftragVorlaufView> findAuftragsVorlauf() throws FindException;

    /**
     * Sucht nach Auftrags- und IntAccount-Daten. <br>
     *
     * @param query Query-Objekt mit den Suchparametern. Es werden alle Parameter ausgewertet - unabhaengig ob
     *              <code>null</code> oder nicht!
     * @return Liste mit Objekten des Typs <code>AuftragIntAccountView</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    List<AuftragIntAccountView> findAuftragAccountViews(AuftragIntAccountQuery query) throws FindException;

    /**
     * Sucht nach allen AuftragStatus-Eintraegen.
     *
     * @return Liste mit Objekten des Typs <code>AuftragStatus</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    List<AuftragStatus> findAuftragStati() throws FindException;

    /**
     * Sucht nach einem AuftragStatus-Objekt ueber dessen ID.
     *
     * @param statusId ID des gesuchten Objekts.
     * @return AuftragStatus zur ID.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    AuftragStatus findAuftragStatus(Long statusId) throws FindException;

    /**
     * Erstellt ein JasperPrint-Objekt mit den Account-Daten eines Kunden.
     *
     * @param auftragId Auftrags-ID eines beliebigen Kundenauftrags, der Accounts besitzt.
     * @param sessionId Session-ID
     * @throws AKReportException
     */
    JasperPrint reportAccounts(Long auftragId, Long sessionId) throws AKReportException;

    /**
     * Methode macht eine Auftrags-Kuendigung rueckgaengig. Durchgefuehrte Aktionen: siehe {@link
     * RevokeTerminationCommand}
     */
    AKWarnings revokeTermination(RevokeTerminationModel revokeTermination) throws StoreException;

    /**
     * Die Methode setzt einen Auftrag im Status 'in Betrieb' aus 'Erfassung' zurueck. Durchgefuehrte Aktionen: siehe
     * {@link RevokeCreationCommand}
     *
     * @param revokeCreationModel Modell mit den Optionen fuer die Operation.
     * @return AKWarnings - aufgetretene Warnungen
     * @throws StoreException
     */
    AKWarnings revokeCreation(RevokeCreationModel revokeCreationModel) throws StoreException;

    /**
     * Prueft, ob der gegebene Auftrag eine 4Draht-Option zugeordnet hat.
     *
     * @param auftragId CC Auftrag der geprueft werden soll
     * @return true falls der Auftrag ein 4Draht-Auftrag ist oder einen 4Draht-Auftrag zugeordnet ist
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    boolean has4DrahtOption(Long auftragId) throws FindException;

    /**
     * Prueft, ob der gegebene Auftrag eine 4Draht-Option zugeordnet hat.
     *
     * @param orderNo billing Auftrag Id
     * @return true falls der Auftrag ein 4Draht-Auftrag ist oder einen 4Draht-Auftrag zugeordnet ist
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    boolean has4DrahtOptionOrderNo(Long orderNo) throws FindException;

    /**
     * @return die aktuell höchste AuftragsId oder <code>null</code> zurück
     */
    Long findMaxAuftragId();

    /**
     * Sucht nach einer bestimmten Leitungsnummer anhand der übergebenen ID
     *
     * @param leitungsnummerId ID der gesuchten Leitungsnummer
     * @return Instanz der gefundenen <code>Leitungsnummer</code> oder <code>null</code>
     * @throws FindException falls bei der Suchabfrage ein Fehler auftritt
     */
    Leitungsnummer findLeitungsnummerById(Long leitungsnummerId);

    /**
     * Sucht nach einer bestimmten Leitungsnummer anhand eines Auftrags
     *
     * @param auftrag Auftrag, für die Leitungsnummer gesucht werden sollen
     * @return Liste von <code>Leitungsnummer</code>-Objekten oder <code>null</code>
     * @throws FindException falls bei der Suchabfrage ein Fehler auftritt
     */
    List<Leitungsnummer> findLeitungsnummerByAuftrag(CCAuftragModel auftrag);

    /**
     * Methode, um die übergebenen Leitungsnummer zu speichern
     *
     * @param toStore Leitungsnummer, die gespeichert werden soll
     * @throws StoreException falls ein Fehler beim Speichern auftritt
     */
    void saveLeitungsnummer(Leitungsnummer toStore);

    /**
     * Methode, um die übergebenen Leitungsnummer zu löschen
     *
     * @param toDelete Leitungsnummer, die gelöscht werden soll
     * @throws DeleteException falls ein Fehler beim Löschen auftritt
     */
    void deleteLeitungsnummer(Leitungsnummer toDelete);

    void changeCustomerIdOnAuftrag(Long auftragNoOrig, Long sourceCustomerNo, Long targetCustomerNo)
            throws StoreException;

    /**
     * Liefert bei VoIP-Aufträgen die Switch Kennung von AuftragTechnik ansonsten aus der Endstelle B/ersten Rangierung
     * (nicht die Additional Rangierung)/EQ_IN.
     */
    HWSwitch getSwitchKennung4Auftrag(Long auftragId);

    /**
     * Berechnet die Switch Kennung für einen VoIP-Auftrag anhand folgendem Vorgehen: <ul> <li>von den Endgeräten des
     * Auftrags</li> <l>vom Produkt des Auftrages</l> </ul>
     * <p>
     * <b>Bemerkung:</b> Die Methode sollte aufgerufen werden um den Switch zu ermitteln der auf AuftragTechnik
     * gespeichert werden soll.
     *
     * @return der berechnete Switch (left) oder <code>TRUE</code> (right) wenn es sich nicht um einen VOIP Auftrag
     * handelt
     */
    Either<CalculatedSwitch4VoipAuftrag, Boolean> calculateSwitch4VoipAuftrag(Long auftragId) throws FindException;

    /**
     * Speichert den übergebenen HWSwitch auf dem AuftragTechnik zu dem Auftrag ohne AuftragTechnik zu historisieren.
     */
    void updateSwitchForAuftrag(Long auftragId, HWSwitch switchTo);

    /**
     * Ermittelt den Hurrican Auftraeg zu einer LineId (=VBZ), der den Status 'in Betrieb' oder falls bereits
     * gekuendigt, deren Kuendigung vor {@code when} ist.
     *
     * @throws FindException wird in folgenden Situationen geworfen: <ul> <li>Es wurde mehr als ein Auftrag zur LineId
     *                       mit Status 'in Betrieb' gefunden <li>bei der Suchabfrage tritt ein unerwarteter Fehler auf
     *                       </ul>
     */
    Auftrag findActiveOrderByLineId(String lineId, LocalDate when) throws FindException;

    /**
     * Ermittelt den Hurrican Auftraeg zu einer LineId (=VBZ) und einem Status.
     *
     * @param lineId        die gesuchte LineId.
     * @param auftragStatus der gesuchte Status.
     * @throws FindException wird in folgenden Situationen geworfen: <ul> <li>Es wurde mehr als ein Auftrag zur LineId
     *                       mit den angegeben Stati gefunden <li>bei der Suchabfrage tritt ein unerwarteter Fehler auf
     *                       </ul>
     */
    Auftrag findOrderByLineIdAndAuftragStatus(String lineId, Long auftragStatus) throws FindException;

    /**
     * Suche aktive Aktion fuer Auftrag.
     *
     * @return aktive Aktion oder null falls keine aktive Aktion vorhanden
     */
    AuftragAktion getActiveAktion(Long auftragId, AuftragAktion.AktionType type) throws FindException;

    /**
     * Speichert eine AuftragAktion zu einem Auftrag.
     */
    void saveAuftragAktion(AuftragAktion aktion) throws StoreException;

    /**
     * Speichert eine AuftragWholesale zu einem Auftrag.
     */
    void saveAuftragWholesale(AuftragWholesale auftragWholesale) throws StoreException;

    /**
     * Suche passende AuftragWholesale zu einem Auftrag mit AuftragId.
     * @param auftragId
     * @return
     * @throws FindException
     */
    AuftragWholesale findAuftragWholesaleByAuftragId(Long auftragId) throws FindException;

    /**
     * Sucht aktive Aktion fuer Auftrag und aendert den Wunschtermin. Dies ist sinnvoll beispielsweise fuer Tests, wenn
     * der Wunschtermin in der Vergangenheit liegen soll.
     *
     * @return modifizierte aktive Aktion oder null falls keine aktive Aktion gefunden
     */
    AuftragAktion modifyActiveAktion(Long auftragId, AuftragAktion.AktionType type,
            LocalDate modifiedExecutionDate) throws StoreException;

    /**
     * Verschiebt die {@link AuftragAktion} auf die Auftrags-ID {@code newOrderId}. Die urspruengliche Auftrags-Id wird
     * in {@link AuftragAktion#setPreviousAuftragId(Long)} geschrieben!
     */
    void moveModifyPortAktion(AuftragAktion auftragAktion, Long newOrderId);

    /**
     * Storniert die angegebene {@link AuftragAktion}
     */
    void cancelAuftragAktion(AuftragAktion toCancel) throws StoreException;

    /**
     * Ermittelt alle aktiven AuftragTechnik - Objekte die an der angegebenen Baugruppe realisiert sind
     */
    List<AuftragDaten> findAktiveAuftragDatenByBaugruppe(Long baugruppeId);

    /**
     * Ermittelt alle aktiven AuftragDaten - Objekte die zu dem angegebenen {@code ortsteil} zugewiesen sind mit der
     * angegebenen {@code produktGruppe}.
     *
     * @param ortsteil
     * @param produktGrouppe
     * @return Liste mit AuftragDaten
     */
    @Nonnull
    List<AuftragDaten> findAktiveAuftragDatenByOrtsteilAndProduktGroup(@Nonnull String ortsteil, @Nonnull String produktGrouppe);

    /**
     * Ermittelt ob die Automatisierung von dem Produkt möglich ist. Die Automatisierung ist möglich wenn folgende
     * Bedingungen erfuellt sind: <ul> <li>Produktkonfiguration erlaubt Automatismus <li>Auftrag ist keinem VPN
     * zugeordnet <li>Taifun Auftragsstatus bzw. ATyp passt zu {@code cbVorgangTyp} <ul> <li>cbVorgangTyp=8001 -->
     * ATyp=KUEND <li>sonst: Auftragsstatus Billing = NEU </ul> </ul>
     *
     * @param auftragDaten
     * @return True wenn die Automatisierung möglich ist, sonst false
     */
    Boolean isAutomationPossible(AuftragDaten auftragDaten, Long cbVorgangTyp);

    /**
     * @param auftragId
     * @return paar aus Onkz und Asb abhaengig von der Konfiguration des Produkts, oder <@code>null</@code> falls a)
     * Onkz und Asb nicht ermittelt werden koennen b) das Produkt entsprechend konfiguriert ist keine Onkz/Asb zu
     * benoetigen
     * @throws RuntimeException wenn aus der im Produkt definierten Quelle Onkz/Asb nicht ermittelt werden koennen
     */
    @CheckForNull
    Pair<String, Integer> findOnkzAsb4Auftrag(long auftragId);

    /**
     * Prueft, ob der Endstelle B des Auftrages {@code auftragId} eine GeoId und der GeoId ein AGS_N Schluessel
     * zugeordnet sind.
     */
    void checkAgsn4Auftrag(final long auftragId) throws FindException;

    /**
     * Searches for the HWRack associated with a Hurrican-Auftrag. Only valid (gueltigVon < now < gueltigBis) HWRacks
     * that are linked to valid Equipment are considered.
     *
     * @param auftragId Hurrican Auftrag Id
     * @return The HWRack-Object oder <code>null</code> if no match found
     * @throws FindException
     */
    HWRack findHwRackByAuftragId(Long auftragId) throws FindException;

    enum CheckAnzNdrahtResult {
        LESS_THAN_EXPECTED,
        AS_EXPECTED,
        MORE_THAN_EXPECTED,
        /**
         * Produkt hate keine Konfiguration fuer SDSL-N-Draht
         */
        NO_NDRAHT_CONFIG
    }

    /**
     * Ermittelt die Auftragsdaten zu der angegebenen LineId zwecks Portfreigabe.
     *
     * @param lineId des gesuchten Ports
     * @param auftragStatus auftragsstati für die Filterung, dürfen nicht leer sein!
     * @return die zugehoerigen AuftragsDaten, falls nichts gefunden wurde oder <code>auftragStatus</code> leer ist <code>null</code>
     * @throws FindException falls technische Fehler bei der Ermittlung auftreten.
     */
    AuftragDaten findAuftragDatenByLineIdAndStatus(String lineId, Long... auftragStatus) throws FindException;

    AuftragTechnik findAuftragTechnik4VbzId(Long vbzId);

}
