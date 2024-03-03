/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.02.2009 08:31:06
 */
package de.augustakom.hurrican.service.cc;

import java.time.*;
import java.util.*;
import javax.annotation.*;
import javax.validation.constraints.*;

import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.cps.CPSProvisioningAllowed;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionExt;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionLog;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionResult;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionSubOrder;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSGetServiceOrderStatusResponseData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSServiceOrderData.LazyInitMode;
import de.augustakom.hurrican.model.cc.hardware.HWOltChild;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.service.base.exceptions.CommunicationException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;

/**
 * Service-Interface fuer die CPS-Funktionen (CommonProvisioningServer).
 *
 *
 */
public interface CPSService extends ICCService {
    String KEIN_AUFTRAG_GEFUNDEN = "Kein Auftrag gefunden";
    String AUFTRAG_GEFUNDEN_JEDOCH_NICHT_GUELTIG = "Auftrag gefunden, jedoch nicht gueltig";
    String AUFTRAG_SUCHE_NICHT_EINDEUTIG = "Auftragsuche nicht eindeutig (Aufträge zu mehreren Taifun Nummern gefunden)";

    /**
     * Speichert das angegebene CPS-Transaction Objekt.
     *
     * @param toSave    zu speicherndes Objekt
     * @param sessionId Session-ID des Users
     * @throws StoreException wenn beim Speichern ein Fehler auftritt
     *
     */
    CPSTransaction saveCPSTransaction(CPSTransaction toSave, Long sessionId) throws StoreException;

    /**
     * @param toSave
     * @param sessionId
     * @throws StoreException
     *
     * @see CPSService#saveCPSTransaction(CPSTransaction, Long) Im Unterschied zu saveCPSTransaction ist bei dieser
     * Methode das Transaction-Handling auf PROPAGATION_REQUIRES_NEW gesetzt. <br> Diese Methode kann also aufgerufen
     * werden, wenn der Save auf jeden Fall durchgefuehrt werden soll, auch wenn die eigentlich Funktion einen Fehler
     * verursacht. (Zum Beispiel, um den Status der CPS-Tx zu aendern.)
     */
    void saveCPSTransactionTxNew(CPSTransaction toSave, Long sessionId) throws StoreException;

    // @formatter:off
    /**
     * Ermittelt alle 'offenen' CPS-Transaktionen. <br/>
     * Eine CPS-Tx ist offen, wenn folgende Punkte erfuellt sind: <br/>
     * <ul>
     *     <li>(
     *     <li>Tx-Status >= CPSTransaction.TX_STATE_IN_PREPARING
     *     <li>AND
     *     <li>Tx-Status < CPSTransaction.TX_STATE_FAILURE_CLOSED
     *     <li>)
     *     <li>AND
     *     <li>Tx-Status <> CPSTransaction.TX_STATE_CANCELLED
     *     <li>AND
     *     <li>SERVICE_ORDER_TYPE <> CPSTransaction.SERVICE_ORDER_TYPE_QUERY
     * </ul>
     *
     * @return Liste mit den offenen CPS-Transactions (View-Modell fuer weitere Informationen, wie z.B. Produktname
     * etc.)
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt
     *
     */
    // @formatter:on
    List<CPSTransactionExt> findOpenTransactions(Integer limit) throws FindException;

    // @formatter:off
    /**
     * Ermittelt alle abgelaufenen CPS-Transaktionen. <br/>
     * Eine CPS-Tx ist abgelaufen, wenn folgende Punkte erfuellt sind: <br/>
     * <ul>
     *     <li>(
     *     <li>Tx-Status = CPSTransaction.TX_STATE_IN_PREPARING
     *     <li>OR
     *     <li>Tx-Status = CPSTransaction.TX_STATE_IN_PREPARING_FAILURE
     *     <li>)
     *     <li>AND
     *     <li>Tx-Status <> CPSTransaction.TX_STATE_CANCELLED
     *     <li>AND
     *     <li>ServiceOrderType <> CPSTransaction.SERVICE_ORDER_TYPE_QUERY
     *     <li>AND <li>ESTIMATED_EXEC_TIME < expiredDate
     * </ul>
     * Eine CPS-Tx is abgelaufen, wenn die ESTIMATED_EXEC_TIME aelter ist <br> als der aktuelle Tag
     * (0:0Uhr) - 'offsetDays'. <br/>
     *
     * @return Liste mit den abgelaufenen CPS-Transactions (View-Modell fuer weitere Informationen, wie z.B. Produktname
     * etc.)
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt
     *
     */
    // @formatter:on
    List<CPSTransactionExt> findExpiredTransactions(Integer offsetDays) throws FindException;

    /**
     * Ermittelt alle CPS-Transactions, die den Werten aus <code>example</code> entsprechen.
     *
     * @param example Example-Objekt mit den Query-Parametern
     * @return Liste mit den entsprechenden CPS-Transactions
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     *
     */
    List<CPSTransactionExt> findCPSTransaction(CPSTransactionExt example) throws FindException;

    /**
     * Ermittelt eine CPS-Transaction in Abhängigkeit der ID.
     *
     * @param id Id
     * @return CPSTransaction Object
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     *
     */
    CPSTransaction findCPSTransactionById(Long id) throws FindException;

    // @formatter:off
    /**
     * Ueberprueft, ob eine CPS-Provisionierung fuer einen bestimmten Auftrag 'erlaubt' ist. Dies ist dann der Fall,
     * wenn folgende Kriterien zutreffen:
     * <ul>
     *     <li>Produkt muss fuer CPS-Provisionierung freigegeben sein
     *     <li>Auftrag ist nicht als 'manuelle Provisionierung' markiert
     *     <li>Auftrag ist keinem VPN zugeordnet
     *     <li>falls ueber HVT angeschlossen, muss dieser fuer CPS-Prov. zugelassen sein
     * </ul>
     *
     * @param auftragId         ID des Auftrags, der geprueft werden soll
     * @param lazyInitMode      (optional) definiert den Modus fuer den Lazy-Init
     * @param checkAutoCreation Flag definiert, ob das Produkt auf automatische CPS-Generierung geprueft werden soll
     * @param check4SubOrder    Flag gibt an, ob die Ueberpruefung fuer einen Sub-Auftrag durchgefuehrt werden soll. Ist
     *                          dies der Fall, muessen nicht alle Pruefungen (z.B. HVT-Check) durchlaufen werden.
     * @param checkOrderState   Flag, ob der Status des technischen Auftrags geprueft werden soll
     * @return {@link NotNull} {@link CPSProvisioningAllowed} Objekt ueber das definiert ist, ob eine CPS-Provisionierung
     *         fuer den Auftrag moeglich ist.
     * @throws FindException
     *
     */
    // @formatter:on
    CPSProvisioningAllowed isCPSProvisioningAllowed(Long auftragId, LazyInitMode lazyInitMode,
            boolean checkAutoCreation, boolean check4SubOrder, boolean checkOrderState) throws FindException;

    /**
     * Prueft fuer den {@code hwOltChild} ob ein CPS TX der Typ {@code serviceOrderType} moeglich ist.
     *
     * @param hwOltChild
     * @param auftragDaten
     * @param serviceOrderType
     * @return
     * @throws FindException
     */
    List<String> checkIfTxPermitted4OltChild(HWOltChild hwOltChild, AuftragDaten auftragDaten, Long serviceOrderType) throws FindException;

    // @formatter:off
    /**
     * Erstellt CPS-Transaktionen fuer Auftraege mit aktivem Bauauftrag. <br/>
     * Die grundsaetzlichen Bedingungen fuer eine CPS-Provisionierung sind unter <code>isCPSProvisioningAllowed</code>
     * beschrieben. <br/>
     * <br/>
     * Weitere Bedingungen: <br/>
     * <ul>
     *     <li>zum Auftrag existiert ein Provisionierungsauftrag mit RealDate=respectedRealDate
     *     <li>Verlauf ist nicht als manuelle Provisionierung markiert.
     *     <li>Verlauf ist im Status 'bei Technik' resp. 'Kuendigung bei Technik'
     *     <li>fuer den Verlauf existiert keine aktive bzw. abgeschlossene CPS-Transaction
     * </ul>
     * <br/>
     * Die entsprechenden Verlaufs-Datensaetze der techn. Abteilungen (ausser FieldService) werden auf den Status
     * 'CPS-Bearbeitung' gesetzt. <br/>
     * Die generierten CPS-Transaktionen werden in dieser Methode noch NICHT an den CPS uebertragen. Dies muss der
     * Client ueber die Methode <code>sendCPSTx2CPS</code> erledigen.
     *
     * @param respectedRealDate zu beruecksichtigender Realisierungstermin der Bauauftraege
     * @param sessionId         aktuelle Session-ID des Users
     * @return Object vom Typ <code>CPSTransactionResult</code> in dem die generierten CPS-Transactions sowie die
     * aufgenommenen Warnings enthalten sind.
     * @throws StoreException wenn beim Anlegen der Transaktionen ein Fehler auftritt
     *
     */
    // @formatter:on
    CPSTransactionResult createCPSTransactions4BA(Date respectedRealDate, Long sessionId) throws StoreException;

    // @formatter:off
    /**
     * Erstellt fuer einen bestimmten Verlauf eine CPS-Transaction. <br/>
     * Die Bedingungen, ob fuer den Verlauf eine CPS-Tx zulaessig ist, sind unter <code>createCPSTransaction4BA</code>
     * beschrieben. <br/>
     * Statt der Bedingung 'RealDate=respectedRealDate' wird geprueft, ob der Realisierungstermin des angegebenen
     * Verlaufs >= heute ist. <br/>
     * <br/>
     * Die generierte CPS-Transaktion wird in dieser Methode noch NICHT an den CPS uebertragen. Dies muss der
     * Client ueber die Methode <code>sendCPSTx2CPS</code> erledigen. <br>
     *
     * @param verlauf   Verlauf fuer den eine CPS-Transaction erstellt werden soll
     * @param sessionId aktuelle Session-ID des Users
     * @return Object vom Typ <code>CPSTransactionResult</code> in dem die generierten CPS-Transactions sowie die
     * aufgenommenen Warnings enthalten sind.
     * @throws StoreException wenn beim Anlegen der Transaktionen ein Fehler auftritt
     *
     */
    // @formatter:on
    CPSTransactionResult createCPSTransaction4BA(Verlauf verlauf, Long sessionId) throws StoreException;

    // @formatter:off
    /**
     * Erstellt CPS-Transaktionen fuer Auftraege, bei denen Rufnummernleistungen geaendert werden. <br/>
     * Die grundsaetzlichen Bedingungen fuer eine CPS-Provisionierung sind unter <code>isCPSProvisioningAllowed</code>
     * beschrieben. <br/>
     * <br/>
     * Weitere Bedingungen: <br/>
     * <ul>
     *     <li>Auftrag befindet sich im Status 'in Betrieb' oder innerhalb einer Kuendigung
     *     <li>Auftrag besitzt noch nicht provisionierte DN-Leistungen (Zugang od. Kuendigung)
     *     <li>zu aenderende DN-Leistung besitzt als Termin 'changeDate'
     * </ul>
     * <br/>
     * Die generierten CPS-Transaktionen werden in dieser Methode noch NICHT an den CPS uebertragen. Dies muss der
     * Client ueber die Methode <code>sendCPSTx2CPS</code> erledigen. <br> <br> Als Execution-Date fuer die generierten
     * CPS-Tx wird die aktuelle Zeit + 5 Minuten verwendet.
     *
     * @param changeDate Angabe des zu beruecksichtigenden Datums
     * @param sessionId  aktuelle Session-ID des Users
     * @return Object vom Typ <code>CPSTransactionResult</code> in dem die generierten CPS-Transactions sowie die
     * aufgenommenen Warnings enthalten sind.
     * @throws StoreException wenn beim Anlegen der Transaktionen ein Fehler auftritt
     *
     */
    // @formatter:on
    CPSTransactionResult createCPSTransactions4DNServices(Date changeDate, Long sessionId) throws StoreException;

    // @formatter:off
    /**
     * Erstellt CPS-Transaktionen fuer die Durchfuehrung von Sperren / Entsperren. Die grundsaetzlichen Bedingungen fuer
     * eine CPS-Provisionierung sind unter <code>isCPSProvisioningAllowed</code> beschrieben. <br/> <br/>
     * Weitere Bedingungen: <br/>
     * <ul>
     *     <li>Auftrag befindet sich im Status 'in Betrieb' oder innerhalb einer Kuendigung
     *     <li>Auftrag hat eine nicht abgearbeitete Sperre / Entsperre
     * </ul>
     * <br/>
     * Die generierten CPS-Transaktionen werden in dieser Methode noch NICHT an den CPS uebertragen. Dies muss der
     * Client ueber die Methode <code>sendCPSTx2CPS</code> erledigen.
     *
     * @param sessionId aktuelle Session-ID des Users
     * @return Object vom Typ <code>CPSTransactionResult</code> in dem die generierten CPS-Transactions sowie die
     * aufgenommenen Warnings enthalten sind.
     * @throws StoreException wenn beim Anlegen der Transaktionen ein Fehler auftritt
     *
     */
    // @formatter:on
    CPSTransactionResult createCPSTransactions4Lock(Long sessionId) throws StoreException;

    /**
     * Erstellt eine CPS-Transaction, um eine MDU erstmalig zu initialisieren.
     *
     * @param mduRackId      ID der zu initialisierenden MDU
     * @param sendUpdate     Flag gibt an, ob ein UpdateMDU (bei {@code true}) oder ein initMDU geschickt werden soll
     * @param sessionId      aktuelle Session-ID des Users
     * @param useInitialized true, falls das Flag an den CPS uebermittelt werden soll
     * @return Object vom Typ <code>CPSTransactionResult</code> in dem die generierten CPS-Transactions sowie die
     * aufgenommenen Warnings enthalten sind.
     * @throws StoreException
     *
     */
    CPSTransactionResult createCPSTransaction4MDUInit(Long mduRackId, boolean sendUpdate, Long sessionId,
            boolean useInitialized) throws StoreException;

    /**
     * Erstellt eine CPS-Transaction, um eine MDU erstmalig zu initialisieren.
     *
     * @param mduRackId      ID der zu initialisierenden MDU
     * @param sendUpdate     Flag gibt an, ob ein UpdateMDU (bei {@code true}) oder ein initMDU geschickt werden soll
     * @param sessionId      aktuelle Session-ID des Users
     * @param useInitialized true, falls das Flag an den CPS uebermittelt werden soll
     * @return Object vom Typ <code>CPSTransactionResult</code> in dem die generierten CPS-Transactions sowie die
     * aufgenommenen Warnings enthalten sind.
     * @throws StoreException
     *
     */
    CPSTransactionResult createCPSTransaction4MDUInitTx(Long mduRackId, boolean sendUpdate, Long sessionId,
            boolean useInitialized) throws StoreException;

    /**
     * Erstellt eine CPS-Transaktion zum Initialisieren eines HwOltChild (z.B. ONT).
     */
    CPSTransactionResult createCPSTransaction4OltChild(Long oltChildId, final Long serviceOrderType,
            Long sessionId) throws StoreException;

    // @formatter:off
    /**
     * Erzwingt zu einem Auftrag eine CPS-Transaction. <br/>
     * Die grundsaetzlichen Bedingungen fuer eine CPS-Provisionierung sind unter <code>isCPSProvisioningAllowed</code>
     * beschrieben. <br/>
     * <br/>
     * Die Methode ist dafuer verantwortlich, den CPS-Transaction Datensatz zu generieren. Ausserdem werden ueber diese
     * Methode alle fuer den CPS notwendigen Auftragsparameter (z.B. Rufnummern, Ports, Accounts etc.) gesammelt und in
     * eine entsprechende XML-Struktur geschrieben. <br/>
     * Waehrend der Daten-Ermittlung auftretende Fehler werden in CPSTransactionLog protokolliert. <br/>
     * Die generierte CPS-Transaktion wird in dieser Methode noch NICHT an den CPS uebertragen. Dies muss der Client
     * ueber die Methode <code>sendCPSTx2CPS</code> erledigen.
     *
     * @param parameters Parameter im Objekt der Klasse CreateCPSTransactionParameter gebündelt.
     * @return die erstellte CPS-Transaction
     * @throws StoreException wenn beim Anlegen der Transaktionen ein Fehler auftritt
     *
     */
    // @formatter:on
    CPSTransactionResult createCPSTransaction(CreateCPSTransactionParameter parameters) throws StoreException;

    /**
     * Uebergibt dem CPS (CommonProvisioningServer) die Transaktion mit der ID <code>txId</code>. <br/> Der Status der
     * Transaktion wird entsprechend gesetzt (auf IN_PROVISIONING oder TRANSMISSION_FAILURE).
     *
     * @param cpsTx     die zu uebermittelnde CPS-Transaction
     * @param sessionId aktuelle Session-ID des Users
     * @throws StoreException wenn bei der Statusaenderung ein Fehler auftritt
     *
     */
    void sendCPSTx2CPS(CPSTransaction cpsTx, Long sessionId) throws StoreException;

    /**
     * Uebergibt dem CPS (CommonProvisioningServer) die Transaktion mit der ID <code>txId</code>. <br/> Der Status der
     * Transaktion wird entsprechend gesetzt (auf IN_PROVISIONING oder TRANSMISSION_FAILURE).
     *
     * @param cpsTx     die zu uebermittelnde CPS-Transaction
     * @param sessionId aktuelle Session-ID des Users
     * @param sync      wird dieser auf TRUE gesetzt, so wird die Transaktion synchron auf dem CPS ausgefuehrt
     * @throws StoreException wenn bei der Statusaenderung ein Fehler auftritt
     */
    void sendCPSTx2CPS(CPSTransaction cpsTx, Long sessionId, boolean sync) throws StoreException;

    // @formatter:off
    /**
     * Entfernt eine eingestellte CPS-Transaction aus dem CPS. <br/>
     * Dies darf nur erfolgen, so lange die Transaction vom CPS noch nicht bearbeitet wurde; also nur, wenn sich der
     * Status auf PREPARING oder IN_PROVISIONING befindet. <br/>
     * Der Status der CPS-Transaction wird dabei auf 'CANCELLED' gesetzt. <br/>
     * Evtl. vorhandene Referenzen auf die CPS-Transaction (z.B. vom Bauauftrag oder DN-Leistung) werden hierbei
     * entfernt. <br/>
     * Im Falle von TX_SOURCE=HURRICAN_VERLAUF wird auch der Status der VerlaufAbteilungs-Datensaetze zurueck gesetzt
     * (auf Wert 1)!
     *
     * @param txId      ID der abzubrechenden Transaktion
     * @param sessionId aktuelle Session-ID des Users
     * @return true, wenn die Transaktion abgebrochen werden konnte
     * @throws StoreException         wenn beim Abbruch ein Fehler auftritt
     * @throws CommunicationException wenn bei der Kommunikation mit dem CPS ein Fehler auftritt
     *
     */
    // @formatter:on
    boolean cancelCPSTransaction(Long txId, Long sessionId) throws StoreException, CommunicationException;

    /**
     * Entfernt Referenzen auf die angegebene CPS-Transaction. <br> Dabei werden z.B. Rufnummernleistungen sowie
     * Sperr-Eintraege beruecksichtigt.
     *
     * @param txId      ID der CPS-Transaction, die aus den Referenzierungen entfernt werden soll
     * @param sessionId aktuelle Session-ID des Users
     * @throws StoreException wenn beim Entfernen der Referenzierungen ein Fehler auftritt.
     *
     */
    void disjoinCPSTransaction(Long txId, Long sessionId) throws StoreException;

    // @formatter:off
    /**
     * Wird durch den vom CPS aufgerufenen Web-Service aufgerufen, um eine Transaction als abgeschlossen zu markieren
     * (mit SUCCESS oder FAILURE). <br/>
     * Je nach TX_SOURCE werden ausserdem noch folgende Aktionen durchgefuehrt:
     * <ul>
     *    <li>TX_SOURCE=HURRICAN_VERLAUF - Verlaufsdatensaetze der technischen (internen) Abteilungen werden als
     *        'erledigt' markiert. (Falls keine weitere Abteilung an dem Verlauf beteiligt ist, wird der Verlauf
     *        komplett abgeschlossen.)
     *    <li>TX_SOURCE=HURRICAN_LOCK - Ent-/Sperren werden als erledigt markiert.
     * <ul> <br/>
     *
     * @param txId      ID der CPS-Tx, die abgeschlossen werden soll
     * @param cpsResult
     * @param sessionId Id der aktuellen User-Session
     * @return Boolean.TRUE wenn Verarbeitung erfolgreich war
     * @throws StoreException
     *
     */
    // @formatter:on
    Boolean finishCPSTransaction(Long txId, Object cpsResult, Long sessionId) throws StoreException;

    /**
     * Speichert das angegebene CPSTransactionLog Objekt. <br> Die Methode wird in einer neuen Transaction ausgefuehrt!
     *
     * @param toSave zu speicherndes Objekt
     * @throws StoreException wenn beim Speichern ein Fehler auftritt.
     *
     */
    void saveCPSTransactionLogTxNew(CPSTransactionLog toSave) throws StoreException;

    /**
     * @param toSave zu speicherndes Objekt
     * @throws StoreException wenn beim Speichern ein Fehler auftritt.
     * ausgefuehrt!
     */
    void saveCPSTransactionLogInTx(CPSTransactionLog toSave) throws StoreException;

    /**
     * Ermittelt alle Log-Eintraege zu einer bestimmten CPS-Transaction.
     *
     * @param cpsTxId ID der CPS-Transaction, deren Log-Eintraege ermittelt werden sollen
     * @return Liste mit Objekten des Typs <code>CPSTransactionLog</code>
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt
     *
     */
    List<CPSTransactionLog> findCPSTxLogs4CPSTx(Long cpsTxId) throws FindException;

    /**
     * Ermittelt den Status der CPS-Transaktion.
     *
     * @param tx        Die CPS-Transaktion
     * @param sessionId SessionId
     * @return Status der CPS-Transaktion
     */
    CPSGetServiceOrderStatusResponseData getStateForCPSTx(CPSTransactionExt tx, Long sessionId)
    throws StoreException;

    /**
     * Sendet die CPS-Transaktion nochmal.
     *
     * @param tx        Die CPS-Transaktion
     * @param sessionId SessionId
     */
    void resendCPSTx(CPSTransactionExt tx, Long sessionId) throws FindException, StoreException;

    /**
     * Schließt die CPS-Transaktion.
     *
     * @param cpsTxId   ID der zu schliessenden CPS-Tx
     * @param sessionId SessionId
     */
    void closeCPSTx(Long cpsTxId, Long sessionId) throws StoreException;

    /**
     * Speichert das angegeebene Objekt.
     *
     * @param toSave zu speicherndes Objekt.
     * @throws StoreException
     *
     */
    void saveCPSTransactionSubOrder(CPSTransactionSubOrder toSave) throws StoreException;

    /**
     * Ermittelt alle Sub-Order Eintraege zu einer bestimmten CPS-Transaction.
     *
     * @param cpsTxId ID der CPS-Transaction
     * @return
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     *
     */
    List<CPSTransactionSubOrder> findCPSTransactionSubOrders(Long cpsTxId) throws FindException;

    /**
     * Ermittelt alle CPS-Transactions, die fuer einen bestimmten Verlauf erstellt wurden.
     *
     * @param verlaufId ID des Verlaufs
     * @return Liste der CPS-Transactions, die dem Verlauf zugeordnet sind.
     * @throws FindException
     *
     */
    List<CPSTransaction> findCPSTransactions4Verlauf(Long verlaufId) throws FindException;

    /**
     * Ermittelt alle aktiven(!) CPS-Tx zu diversen Filterkriterien
     *
     * @param orderNoOrig      Taifun-Auftragsnummer (null fuer beliebig)
     * @param hwRackId         Hardware Rack ID (fuer Devices, null fuer beliebig)
     * @param serviceOrderType bspw. CreateDevice etc. (null fuer beliebig)
     * @return Liste mit den aktiven CPS-Tx
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     */
    List<CPSTransaction> findActiveCPSTransactions(Long orderNoOrig, Long hwRackId, Long serviceOrderType)
            throws FindException;

    List<CPSTransactionExt> findActiveCPSTransactions(Long orderNoOrig) throws FindException;

    /**
     * Ermittelt alle erfolgreichen(!) CPS-Tx zu diversen Filterkriterien
     *
     * @param orderNoOrig      Taifun-Auftragsnummer (null fuer beliebig)
     * @param hwRackId         Hardware Rack ID (fuer Devices, null fuer beliebig)
     * @param serviceOrderType bspw. CreateDevice etc. (null fuer beliebig)
     * @return Liste mit den erfolgreichen CPS-Tx
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     */
    @Nonnull
    List<CPSTransactionExt> findSuccessfulCPSTransactions(Long orderNoOrig, Long hwRackId, Long serviceOrderType)
            throws FindException;

    /**
     * Ermittelt alle CPS-Transactions zu einem bestimmten techn. Auftrag., States und ServiceOrders
     *
     * @param auftragId
     * @param txStates optional
     * @param serviceOrderTypes optional
     * @return
     */
    List<CPSTransaction> findCPSTransactions4TechOrder(Long auftragId, Collection<Long> txStates, Collection<Long> serviceOrderTypes);

    /**
     * Ermittelt die neuste CPS-Transactions zu einem bestimmten techn. Auftrag., States und ServiceOrders
     *
     * @param auftragId
     * @param txStates optional
     * @param serviceOrderTypes optional
     * @return
     */
    Optional<CPSTransaction> findLatestCPSTransactions4TechOrder(final Long auftragId, Collection<Long> txStates, Collection<Long> serviceOrderTypes);

    /**
     * Ermittelt alle Verlauf-IDs, die einer bestimmten CPS-Transaction zugeordnet sind.
     *
     * @param cpsTx CPS-Transaction
     * @return Liste mit den Verlauf-IDs, die der CPS-Transaction zugeordnet sind.
     * @throws FindException
     *
     */
    List<Long> findVerlaufIDs4CPSTransaction(CPSTransaction cpsTx) throws FindException;

    /**
     * Ermittelt alle CPS-Transaktionen zu einem technischen Auftrag. Die CPS-Transaktionen werden in absteigender
     * Reihenfolge der CPS-Tx ID geliefert.
     *
     * @param ccAuftragModel
     * @return List mit CPS-Transaktionen
     * @throws FindException
     */
    List<CPSTransactionExt> findCPSTransactionsForTechOrder(CCAuftragModel ccAuftragModel) throws FindException;

    /**
     * Ermittelt alle erfolgeichen(!) CPS-Transactions, die fuer einen Auftrag erstellt wurden. <br> (Es wird hier auch
     * der Billing-Auftrag beruecksichtigt!)
     *
     * @param auftragId
     * @return die erfolgreichen CPS-Tx zu dem Auftrag.
     */
    List<CPSTransaction> findSuccessfulCPSTransaction4TechOrder(Long auftragId) throws FindException;

    // @formatter:off
    /**
     * Fuehrt ein 'lazyInit' fuer den Auftrag durch. <br/>
     * Der 'lazyInit' wird in folgenden Faellen durchgefuehrt: <br/>
     * <ul>
     *     <li>SO-Type ist in (modifySubscriber, cancelSubscriber)
     *     <li>Auftrag besitzt keine erfolgreiche CPS-Transaction
     * </ul> <br/>
     * Der Init wird benoetigt, damit der CPS die zu provisionierende Differenz ermitteln kann. <br/>
     * Falls eine {@code followingCPSTx} angegeben ist, wird als Execution-Date (now - 1 Tag) angegeben,
     * ansonsten 'now' <br/>
     * Der 'lazyInit' wird per synchronem WS-Call an den CPS uebergeben.
     *
     * @param auftragId      Auftrags-ID
     * @param followingCPSTx (optional) die CPS-Tx, die nach der Initialisierung durchgefuehrt werden soll
     * @param sessionId
     * @return die generierte CPS-Tx
     * @throws StoreException
     */
    // @formatter:on
    CPSTransactionResult doLazyInit(LazyInitMode lazyInitMode, Long auftragId, CPSTransaction followingCPSTx,
            Long sessionId) throws StoreException;

    /**
     * Ermittelt via CPS fuer eine gegebene techn. Auftrags-ID die momentan gehaltene attainable downstream und upstream
     * Bitrate
     *
     * @param ccAuftragId techn. Auftrags-ID (darf nicht null sein)
     * @param sessionId   darf nicht null sein
     * @return Pair&lt;downstream Bitrate, upstream Bitrate&gt; wenn Daten ermittelt werden konnten, andernfalls null
     */
    Pair<Integer, Integer> queryAttainableBitrate(Long ccAuftragId, Long sessionId) throws FindException;

    /**
     * Ermittelt fuer die Taifun billingOrderNo mit den angegebenen Datum when die Hurrican-Auftrags-Nr.<br> Werden
     * mehrere Aufträge gefunden, erfolt eine Filterung und Sortierung (Aktiv vor Gekündigt, Beachtung
     * Realisierungsdatum und Kündigungsdatum), damit der "beste" Auftrag für eine CPS Transaktion ermittelt wird.
     *
     */
    long getAuftragIdByAuftragNoOrig(long billingOrderNo, LocalDate when, boolean isDelSub);

    /**
     * Ermittelt für Switchname und hwEQN eine Auftrag-ID.<br> Werden mehrere Aufträge gefunden, erfolt eine Filterung
     * und Sortierung (Aktiv vor Gekündigt, Beachtung Realisierungsdatum und Kündigungsdatum), damit der "beste" Auftrag
     * für eine CPS Transaktion ermittelt wird.
     *
     * @param switchAK
     * @param hwEQN
     * @param when
     * @return
     */
    long getAuftragIdBySwitchEqn(String switchAK, String hwEQN, LocalDate when);

    /**
     * Ermittelt die techn. Auftrags-ID für einen radius-Account.
     *
     * @param radiusAccount Benutzername des radius-Accounts
     * @param when          Gueltigkeitsdatum
     * @return
     */
    long getAuftragIdByRadiusAccount(String radiusAccount, LocalDate when);

    /**
     * Ermittelt für Rack-Geraetebezeichnung und hwEQN eine Auftrag-ID.<br> Werden mehrere Auftraege gefunden, erfolgt
     * eine Filterung und Sortierung (Aktiv vor Gekuendigt, Beachtung Realisierungsdatum und Kündigungsdatum), damit der
     * "beste" Auftrag für eine CPS Transaktion ermittelt wird.
     *
     * @param rackBezeichnung
     * @param hwEQN
     * @param when
     * @return
     */
    long getAuftragIdByRackEqn(String rackBezeichnung, String hwEQN, LocalDate when);

    /**
     * Ermittelt für CWMP-ID eine Auftrags-ID.
     *
     */
    long getAuftragIdByCwmpId(String cwmpId, LocalDate when, boolean isDelSub) throws FindException;

    void sendCpsTx2CPSAsyncWithoutNewTx(CPSTransaction cpsTx, Long sessionId) throws StoreException;


    /**
     * liefert den letzten erfolgreichen CPS Service Order Typ für die angegebene hwRackId
     */
    Long getLastCpsTxServiceOrderType4Rack(Long hwRackId) throws FindException;

    /**
     * Sucht die zu jetzt naechste in der Vergangenheit liegende Subscriber Transaktion, welche entweder noch
     * beim CPS liegt (pending) oder bereits erfolgreich durch den CPS verarbeitet ist.
     */
    Long getLastCpsTxServiceOrderType4Subscriber(Long auftragId);

    /**
     * Prüft, ob eine CPS-Transaktion für die angegebene Rack-ID durchgeführt werden darf
     * Hierzu wird die letzte erfolgreiche CPS-Transaktion für das Rack ermittelt.
     */
    boolean isCpsTxServiceOrderTypeExecuteable(Long hwRackId, Long serviceOrderType) throws FindException;

    /**
     * Prueft, welches die naechste gueltige Subscriber Transaktion fuer den angegebenen Auftrag ist.
     * @return der gueltige Service Order Type (ist nie {@code null})
     */
    Long getExecutableCpsTxServiceOrderType4Subscriber(Long auftragId);
}
