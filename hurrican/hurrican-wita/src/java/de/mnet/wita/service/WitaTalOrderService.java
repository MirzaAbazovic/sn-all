/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.05.2011 08:40:03
 */
package de.mnet.wita.service;

import java.time.*;
import java.util.*;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.exceptions.AKAuthenticationException;
import de.augustakom.common.service.exceptions.ServiceCommandException;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.model.billing.RufnummerPortierungSelection;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.Uebertragungsverfahren;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.model.cc.tal.CBVorgangSubOrder;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.TALOrderService;
import de.mnet.wita.RuemPvAntwortCode;
import de.mnet.wita.config.WitaConstants;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.MnetWitaRequest;
import de.mnet.wita.message.auftrag.Kundenwunschtermin;
import de.mnet.wita.model.AbgebendeLeitungenUserTask;
import de.mnet.wita.model.CbVorgangData;
import de.mnet.wita.model.TamUserTask;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.model.validators.RufnummerPortierungCheck;

/**
 * WITA-spezifische Ableitung des {@link TALOrderService}s.
 */
public interface WitaTalOrderService extends TALOrderService, WitaService, WitaConstants {

    /**
     * Erzeugt fuer die unter {@code cbVorgangData} angegebenen Daten die notwendigen {@link CBVorgang} Objekte.
     *
     * @return Eine Liste mit {@link CBVorgang}-Objekten. Der erste CBVorgang in der Liste ist der Vorgang fuer den
     * Auftrag fuer den die elektronische Carrierbestellung ausgeloest wurde.
     */
    List<CBVorgang> createCBVorgang(CbVorgangData cbVorgangData) throws StoreException;

    /**
     * Erzeugt fuer die unter {@code cbVorgangData} angegebenen Daten eine Kuendigung fuer den HVt-Auftrag und eine
     * Bereistellung (NEU) fuer den KVt-Auftrag. Beide Vorgaenge werden innerhalb einer Transaktion angelegt.
     *
     * @param cbVorgangData
     * @return
     * @throws StoreException
     */
    List<CBVorgang> createHvtKvzCBVorgaenge(CbVorgangData cbVorgangData) throws StoreException;

    @Override
    WitaCBVorgang createCBVorgang(Long cbId, Long auftragId, Set<CBVorgangSubOrder> subOrders4Klammer,
            Long carrierId, Date vorgabe, Long typ, Long usecaseId, Boolean vierDraht, String montagehinweis,
            Long sessionId) throws StoreException;

    @Override
    WitaCBVorgang createCBVorgang(Long cbId, Long auftragId, Set<CBVorgangSubOrder> subOrders4Klammer,
            Long carrierId, Date vorgabe, Long typ, Long usecaseId, Boolean vierDraht, String montagehinweis,
            AKUser user) throws StoreException;

    @Override
    WitaCBVorgang closeCBVorgang(Long id, Long sessionId) throws StoreException, ValidationException;

    /**
     * Schreibt die Result-Werte der Carrierbestellung (cbv) auf die zugehoerige Carrierbestellung.
     *
     * @param sessionId
     * @param cbv
     * @throws FindException
     * @throws StoreException
     */
    void writeDataOntoCarrierbestellung(Long sessionId, WitaCBVorgang cbv) throws FindException, StoreException;

    /**
     * Fuehrt eine Terminverschiebung fuer eine Carrierbestellung durch. Falls die urspruengliche Bestellung noch
     * vorgehalten wird (noch nicht gesendet wurde), wird das Vorgabedatum dieser Bestellung abgeaendert und keine
     * Terminverschiebungs-Meldung erzeugt.
     *
     * @param cbVorgangId       id eines elektronischen Vorgangs, der verschoben werden soll
     * @param neuerTermin       geaenderter Termin
     * @param user              aktueller Hurrican-User
     * @param completeUsertasks gibt an ob offene User-Tasks geschlossen werden sollen
     * @param montagehinweis    Montagehinweis der Terminverschiebung (wird von DTAG nur von TVs nach TAM
     *                          beruecksichtigt)
     * @param tamBearbeitungsStatus Der TAM-Bearbeitungsstatus der User-Task
     * @return geaenderter CB-Vorgang
     */
    WitaCBVorgang doTerminverschiebung(Long cbVorgangId, LocalDate neuerTermin, AKUser user,
            boolean completeUsertasks, String montagehinweis, TamUserTask.TamBearbeitungsStatus tamBearbeitungsStatus) throws StoreException, ValidationException;

    /**
     * Ermittelt den CBVorgang mit der angegebenen Id
     *
     * @param id Id des gesuchten {@link de.mnet.wita.model.WitaCBVorgang}s
     * @return der {@link de.mnet.wita.model.WitaCBVorgang} zu der angegebenen Id
     */
    WitaCBVorgang findCBVorgang(Long id);

    /**
     * Fuehrt eine Terminverschiebung fuer mehrere Carrierbestellungen durch. Falls die urspruengliche Bestellung noch
     * vorgehalten wird (noch nicht gesendet wurde), wird das Vorgabedatum dieser Bestellung abgeaendert und keine
     * Terminverschiebungs-Meldung erzeugt.
     *
     * @param cbVorgangIds          ids der elektronischen Vorgaenge, die verschoben werden sollen
     * @param neuerTermin           geaenderter Termin
     * @param user                  aktueller Hurrican-User
     * @param completeUsertasks     gibt an ob offene User-Tasks geschlossen werden sollen
     * @param montagehinweis        Montagehinweis der Terminverschiebung (wird von DTAG nur von TVs nach TAM
     *                              beruecksichtigt)
     */
    Collection<WitaCBVorgang> doTerminverschiebung(Set<Long> cbVorgangIds, LocalDate neuerTermin, AKUser user,
            boolean completeUsertasks, String montagehinweis, TamUserTask.TamBearbeitungsStatus tamBearbeitungsStatus) throws StoreException, ValidationException;

    /**
     * Storniert den angegeben CB-Vorgang. Wenn der zugehoerige Auftrag noch vorgehalten wird (noch nicht gesendet
     * wurde), wird der Auftrag nur mit einem 'Storniert'-Flag markiert und gar nicht erst an die Telekom geschickt.
     */
    @Override
    WitaCBVorgang doStorno(Long cbVorgangId, AKUser user) throws StoreException, ValidationException;

    /**
     * Storniert die angegebenen CB-Vorgaenge. Wenn ein zugehoeriger Auftrag noch vorgehalten wird (noch nicht gesendet
     * wurde), wird der Auftrag nur mit einem 'Storniert'-Flag markiert und gar nicht erst an die Telekom geschickt.
     */
    Collection<WitaCBVorgang> doStorno(Set<Long> cbVorgangIds, AKUser user) throws StoreException,
            ValidationException, FindException;

    /**
     * Schickt eine ErledigtMeldungKunde an die DTAG.
     *
     * @param cbVorgangId Id des elektronischen Vorgangs fuer den eine ERLMK erstellt werden soll
     * @param user        aktueller Hurrican-User
     */
    WitaCBVorgang sendErlmk(Long cbVorgangId, AKUser user) throws StoreException;

    /**
     * Schickt eine ErledigtMeldungKunde an die DTAG.
     *
     * @param cbVorgaenge Set mit den cbVorgangIds
     * @param user        aktueller Hurrican-User
     */
    Collection<WitaCBVorgang> sendErlmks(Set<Long> cbVorgaenge, AKUser user) throws StoreException;

    /**
     * Sendet eine negative RUEM-PV Meldung.
     *
     * @param businessKey BusinessKey des Workflows, auf dem eine RUEM-PV geschickt werden soll.
     * @param antwortCode {@link RuemPvAntwortCode} der gesendet werden soll
     * @param antwortText zusaetzlicher Text fuer die RUEM-PV Meldung (abhaengig vom angegebenen {@link
     *                    RuemPvAntwortCode} notwendig oder nicht)
     * @param user        aktueller Hurrican-User. UserTask darf von keinem anderen User in bearbeitung sein.
     */
    void sendNegativeRuemPv(String businessKey, RuemPvAntwortCode antwortCode, String antwortText, AKUser user);

    /**
     * Sendet eine positive RUEM-PV Meldung.
     *
     * @param businessKey BusinessKey des Workflows, auf dem eine RUEM-PV geschickt werden soll.
     * @param antwortCode {@link RuemPvAntwortCode} der gesendet werden soll
     * @param antwortText zusaetzlicher Text fuer die RUEM-PV Meldung (abhaengig vom angegebenen {@link
     *                    RuemPvAntwortCode} notwendig oder nicht)
     * @param user        aktueller Hurrican-User. UserTask darf von keinem anderen User in bearbeitung sein.
     */
    void sendPositiveRuemPv(String businessKey, RuemPvAntwortCode antwortCode, String antwortText, AKUser user);

    /**
     * Prüft ob für einen CBVorgang eine Klammerung möglich ist oder nicht
     *
     * @param cbVorgangTyp - Typ des CB-Vorgangs
     * @return ob eine Klammerung moeglich/auswaehbar ist
     */
    boolean isPossibleKlammer4GF(Long cbVorgangTyp, Carrierbestellung cb);

    /**
     * Prüft ob für einen CBVorgang 4 Draht möglich ist oder nicht
     *
     * @param cbVorgangTyp - Typ des CB-Vorgangs
     * @return ob 4-Draht moeglich/auswaehbar ist
     */
    boolean isPossible4Draht4GF(Long cbVorgangTyp, Carrierbestellung cb);

    /**
     * Liefert das Vorgabedatum fuer die Terminverschiebung in 60 Tagen
     */
    Date getVorgabeDatumTv60(GeschaeftsfallTyp geschaeftsfallTyp, String vorabstimmungsId);

    /**
     * Liefert das Vorgabedatum fuer die Terminverschiebung in 30 Tagen
     */
    Date getVorgabeDatumTv30(GeschaeftsfallTyp geschaeftsfallTyp, String vorabstimmungsId);

    /**
     * Ermittelt alle Rufnummern, die im TAL-Wizard angezeigt werden sollen
     */
    List<RufnummerPortierungSelection> getRufnummerPortierungList(Long auftragNoOrig, Date vorgabeMnet);

    /**
     * Ueberprueft, ob die Zeitangabe im Datum-Objekt <code>toCheck</code> sich im angegebenen <code>zeitfenster</code>
     * befindet.
     */
    boolean isDayInZeitfenster(Date toCheck, Kundenwunschtermin.Zeitfenster zeitfenster);

    /**
     * Prueft, ob die angefragten Rufnummerportierungen gleich den bestaetigten Rufnummerportierungen sind. Prueft auch,
     * ob das Portierungsdatum der Rufnummern mit Realisierungsdatum übereinstimmt.
     *
     * @param witaCBVorgangId Id des WITA-Cb-Vorgangs (muss ein WITA-CB-Vorgang sein)
     * @return Objekt, das das Ergebnis der Rufnummer-Portierungs-Pruefung darstellt
     */
    RufnummerPortierungCheck checkRufnummerPortierungAufnehmend(Long witaCBVorgangId);

    /**
     * Prueft, ob die angefragten Rufnummerportierungen bei einer AKM-PV mit den Rufnummerportierungen in Taifun
     * uebereinstimmen.
     *
     * @param abgebendeLeitungUserTask UserTask der geprueft werden soll
     * @return Objekt, das das Ergebnis der Rufnummer-Portierungs-Pruefung darstellt
     */
    RufnummerPortierungCheck checkRufnummerPortierungAbgebend(AbgebendeLeitungenUserTask abgebendeLeitungUserTask);

    /**
     * Prueft, ob sich die Daten der StandortKollokation seit der Erstellung des {@link MnetWitaRequest}s geaendert
     * haben. Wenn ja, werden die Daten aktualisiert. Sofern eine {@link de.mnet.wita.exceptions.WitaDataAggregationException}
     * auftritt, wird KEIN(!) Rollback ausgefuehrt!
     */
    void modifyStandortKollokation(MnetWitaRequest mnetWitaRequest);

    /** ---------------------------------------- Nur fuer Tests ------------------------------------------------- */

    /**
     * Methode, um im Backend den User zu speichern. Ist notwendig wegen Caching der User, wenn man den Namen umsetzen
     * will. Nur fuer Tests!
     */
    void saveUser(AKUser user) throws AKAuthenticationException;

    /**
     * Methode, um im Backend den User zu finden. Ist notwendig wegen Caching der User, wenn man den Namen umsetzen
     * will. Nur fuer Tests!
     *
     * @return gefundener User
     */
    AKUser findUserBySessionId(Long sessionId) throws AKAuthenticationException;

    /**
     * Ermittelt WitaCBVorgaenge, die entweder die angegebene CBId besitzen oder nur die angegebene AuftragsId besitzen
     * und CBId=null. Der letztere Fall tritt bei CBVorgaengen mit REX-MK auf.
     *
     * @return Liste der gefundenen WitaCBVorgaenge absteigend sortiert nach der CBVorgang Id
     */
    List<WitaCBVorgang> getWitaCBVorgaengeForTVOrStorno(Long cbId, Long auftragId);

    /**
     * Aendert das Uebertragungsverfahren am Equipment und loest auf Wunsch eine Wita-Bestellung (LMAE) aus.
     *
     * @return gibt an ob Witatransaktion erfolgreich war
     */
    WitaCBVorgang changeUebertragungsverfahren(Carrierbestellung cb, Equipment dtagEquipment, Date kwtVorgabe,
            Uebertragungsverfahren uebertragungsverfahrenNeu, AKUser user) throws FindException, StoreException,
            ValidationException, ServiceCommandException;

    /**
     * Ermittelt die WitaCBVorgaenge, die die uebergebene Klammernummer haben. Wird noch die AuftragId angegeben, wird
     * der WitaCBVorgang mit der Id aus der List raus genommen.
     *
     * @param klammer   Wita Auftrag Klammer Nummer
     * @param auftragId die Id von dem Auftrag der sich in die Resultat Liste nicht befinden soll. Falls null, alle
     *                  gefundene Aufträge werden zurück gegeben.
     * @return WitaCBVorgaenge die die uebergebene Klammernummer haben bis auf den Auftrag mit der gegebene AuftragId
     */
    List<WitaCBVorgang> findCBVorgaenge4Klammer(Long klammer, Long auftragId);

    /**
     * Ermittelt die WitaCBVorgaenge, die die uebergebene Klammernummer haben.
     *
     * @param klammerId Klammernummer des Auftrags
     * @return WitaCBVorgangsIds die die uebergebene Klammernummer haben.
     */
    SortedSet<Long> findWitaCBVorgaengIDs4Klammer(Long klammerId);

    /**
     * Ueberprueft, ob fuer den angegebenen Auftrag ein WITA Vorgang (grundsaetzlich!) automatisch prozessiert werden
     * darf. <br> Dies ist der Fall, wenn folgende Bedingungen erfuellt sind: <ul> <li> Carrier == DTAG </li> <li>
     * CBVorgangTyp == Neu|Aenderung|Anbieterwechsel|Kuendigung </li> <li> Auftrag für Automatismus erlaubt ist </li>
     * <li> Feature Flag ist gesetzt (abhaengig von {@code cbVorgangTyp} entweder WITA_CANCELLATION_AUTOMATION oder
     * WITA_ORDER_AUTOMATION) </li> </ul>
     *
     * @return
     */
    boolean checkAutoClosingAllowed(AuftragDaten auftragDaten, Long carrierId, Long cbVorgangTyp);

    /**
     * Ueberpruft, ob die Automatisierung auf dem CBVorgang erlaubt ist. <br> Die Automatisierung ist erlaubt wenn: <br>
     * <ul> <li> cbVorgang.returnRealDate spaeter als heute ist </li> <li> cbVorgang.returnRealDate auf einem Werktag
     * liegt </li> <li> Wita Antwort standard und positiv ist </li> <li> Feature ORDER_AUTOMATION online ist </li> <li>
     * die Automatisierung an diesem Produkt möglich ist. </li> </ul>
     *
     * @param cbVorgang
     * @return
     * @throws FindException
     */
    boolean isAutomationAllowed(CBVorgang cbVorgang) throws FindException;

    /**
     * Ermittelt alle {@link WitaCBVorgang} Objekte die automatisiert werden können.
     *
     * @param orderType Var-Args Liste mit den WITA-Vorgangstypen (Wert auf {@link Reference}), die beruecksichtigt
     *                  werden sollen.
     * @return Liste mit den {@link WitaCBVorgang}s-Objekten, die fuer eine automatische Verarbeitung in Frage kommen.
     */
    List<WitaCBVorgang> findWitaCBVorgaengeForAutomation(Long... orderType) throws FindException;

    /**
     * Pruefung, ob das angegebene Ziel-Datum min. X Arbeitstage in der Zukunft liegt. Dies ist notwendig, da bei der
     * TAL-Bestellung bestimmte Mindestlaufzeiten einzuhalten sind. <br> Der Wert X wird dabei aus einer Konfiguration
     * ermittelt (bei ESAA aus RegistryService, bei WITA ueber eine Konstante.) <br> <br> Besonderheit fuer ESAA: <br>
     * Da die Uebermittlung der el. TAL Bestellung immer zu einem best. Zeitpunkt erfolgt, muss hier auch die Uhrzeit
     * beruecksichtigt werden. <br> Wenn die letzte Uebermittlung an den Carrier z.B. um 14 Uhr erfolgt, muss nach 14
     * Uhr ein Vorlauf von X+1 Arbeitstagen angegeben werden.
     *
     * @param toCheck
     * @param geschaeftsfallTyp Der Geschaeftsfalltyp wird benoetigt, weil fuer die Geschaeftsfaelle PV, VBL oder auch
     *                          eine BEREITSTELLUNG mit gesetzter Vorabstimmungs-Id (und TVs, die darauf ausgefuehrt
     *                          werden) geprueft werden muss, ob der darauffolgenden Tag einen Samstag, Sonntag oder
     *                          Feiertag ist. Falls das der Fall ist, ist der angegebene Termin nicht gueltig!
     * @param vorabstimmungsId  Die VorabstimmungsId, falls eine vorhanden ist.
     * @param isHvtToKvz        True, wenn es sich um einen Wechsel von HVt nach KVz handelt
     * @return true wenn das angegebene Zieldatum mit die Mindestarbeitstage nicht unterschreitet
     * @throws FindException wenn die Mindestvorlaufzeit nicht ueberprueft werden kann
     */
    boolean isMinWorkingDaysInFuture(Date toCheck, GeschaeftsfallTyp geschaeftsfallTyp,
            String vorabstimmungsId, boolean isHvtToKvz) throws FindException;

    /**
     * Ermittelt alle {@link WitaCBVorgang}s fuer die angegebene VorabstimmungsId. Es werden alle Vorgaenge unabhaengig
     * vom Status beruecksichtigt.
     *
     * @param vorabstimmungsId
     * @return
     * @throws FindException
     */
    List<WitaCBVorgang> findCBVorgaengeByVorabstimmungsId(String vorabstimmungsId) throws FindException;

    /**
     * Markiert den angegeben WitaVorgang als Klärfall und hinterlegt die angegebene Bemerkung mit Zeitstempel.
     *
     * @param id                 die {@link WitaCBVorgang#getId()}
     * @param klaerfallBemerkung Bemerkungstext der Information gibt, aus welchen Grund der Klaerfall auftratt.
     */
    void markWitaCBVorgangAsKlaerfall(Long id, String klaerfallBemerkung) throws StoreException;

    /**
     * Fuer den angegeben WitaVorgang hinterlegt die angegebene Bemerkung mit Zeitstempel. Der WitaVorgang
     * Klaerfall-Status wird dabei nicht angepasst.
     *
     * @param id                 die {@link WitaCBVorgang#getId()}
     * @param klaerfallBemerkung Bemerkungstext der Information gibt, aus welchen Grund der Klaerfall auftratt.
     */
    void addKlaerfallBemerkung(Long id, String klaerfallBemerkung) throws StoreException;

    /**
     * Generates a String of the different {@link CBVorgang#klaerfallBemerkung}n, if there are one.
     *
     * @param cbIDs {@link CBVorgang#cbId}s
     * @return Formated String of Klaerfallbemerkungen.
     */
    String getKlaerfallBemerkungen(Set<Long> cbIDs);

    /**
     * Locates the Bereitstellung {@link de.mnet.wita.message.Auftrag} using the supplied {@code cbVorgangRefId}. If a
     * non-cancelled, unsent Auftrag is found then the {@code earliesSendDate} of the auftrag is updated to the supplied
     * {@code earliesSendDate}. Additionally the VorgabeMnet {@link de.augustakom.hurrican.model.cc.tal.CBVorgang} is
     * overwritten with the {@code kuendigungsDate} to ensure that the kuendigung and bereitstellung are carried out on
     * the same day. <br /> This method is used during the HVT_TO_KVZ CBVorgang, after the ABM Meldung for the
     * Kuendigung has been received so that the Auftrag for the Bereitstellung is scheduled for sending.
     *
     * @param cbVorgangRefId  used to locate the Bereitstellung CBVorgang
     * @param earliesSendDate the date to schedule the Auftrag to be sent out
     * @param kuendigungsDate the confirmed date of the Kuendigung
     * @return true when an Auftrag is located and adapted, otherwise false.
     */
    boolean checkAndAdaptHvtToKvzBereitstellung(Long cbVorgangRefId, LocalDateTime earliesSendDate, Date kuendigungsDate) throws StoreException;

    /**
     * Überprüft, ob es eine Bereitstellung existiert, die die angegebene Kündigug referenziert. Wenn das der Fall ist,
     * wird die Bereitstellung storniert, was möglich sein sollte, weil diese noch nicht übertragen worden ist.
     * Weiterhin wird die Kündigung als Klaerfall mit einer entsprechenden Bemerkung markiert.
     *
     * @param cbVorgangKueId Die CBVorgang-Id von der HVt-Kuendigung
     * @return true wenn es eine Bereitstellung existiert, die diese Kündigung referenziert und diese auch erfolgreich
     * storniert werden konnte, sonst false.
     */
    boolean checkHvtKueAndCancelKvzBereitstellung(Long cbVorgangKueId) throws StoreException;

    /**
     * Searches for a {@link WitaCBVorgang} Objekte that has a {@code cbVorgangRefId} matching the supplied {@code
     * cbVorgangRefId}.
     *
     * @param cbVorgangRefId the refId to match against
     * @return the matching {@link WitaCBVorgang} or null
     */
    WitaCBVorgang findWitaCBVorgangByRefId(Long cbVorgangRefId);
}
