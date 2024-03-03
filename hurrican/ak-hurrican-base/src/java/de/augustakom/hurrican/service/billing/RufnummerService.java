/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.06.2004 10:50:13
 */
package de.augustakom.hurrican.service.billing;

import java.util.*;
import javax.annotation.*;
import javax.validation.constraints.*;

import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.model.billing.BAuftragBNFC;
import de.augustakom.hurrican.model.billing.DNTNB;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.billing.RufnummerPortierungSelection;
import de.augustakom.hurrican.model.billing.query.BAuftragBNFCQuery;
import de.augustakom.hurrican.model.billing.query.RufnummerQuery;
import de.augustakom.hurrican.model.shared.view.AuftragDNView;
import de.augustakom.hurrican.model.shared.view.AuftragINRufnummerView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.iface.FindByParamService;

/**
 * Service-Interface fuer Objekte des Typs <code>Rufnummer</code>
 * 
 *
 */
public interface RufnummerService extends IBillingService, FindByParamService<Rufnummer> {

    /**
     * Ermittelt eine Rufnummer ueber den PrimaryKey.
     * 
     * @param dnNo PK der gesuchten Rufnummer
     * @return Objekt vom Typ <code>Rufnummer</code> oder <code>null</code>
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     *
     */
    public Rufnummer findDN(Long dnNo) throws FindException;

    /**
     * Ermittelt alle Rufnummern zu der angegebenen DN__NO und dem (optional angegebenen) validFrom Datum. Die
     * Ermittlung erfolgt in aufsteigender Reihenfolge von DN_NO!
     * 
     * @param dnNoOrig
     * @param validFrom
     * @return Liste mit Objekten des Typs {@link Rufnummer}
     * @throws FindException
     */
    public List<Rufnummer> findDNsByDnNoOrig(Long dnNoOrig, Date validFrom) throws FindException;

    /**
     * Ermittelt alle Rufnummern, die im TAL-Wizard angezeigt werden sollen,
     * 
     * @param auftragNoOrig Billing-Auftrag-Nummer
     * @param vorgabeMnet Vorgabedatum der TAL-Bestellung, damit Rufnummer vorausgewählt werden koennen, darf null sein
     * @return Liste von DTOs
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     */
    public List<RufnummerPortierungSelection> findDNs4TalOrder(Long auftragNoOrig, Date vorgabeMnet)
            throws FindException;

    /**
     * Sucht nach allen aktuellen und zukuenftigten Rufnummern, die wegportiert werden.
     * 
     * @param auftragNoOrig Billing-Auftrag-Nummer
     * @return Liste von Rufnummern
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     */
    public List<Rufnummer> findDnsAbgehend(Long auftragNoOrig) throws FindException;

    /**
     * Sucht nach allen aktuellen und zukuenftigten Rufnummern, die zu M-net portiert werden.
     * 
     * @param auftragNoOrig Billing-Auftrag-Nummer
     * @return Liste von Rufnummern
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     */
    public List<Rufnummer> findDnsKommend(Long auftragNoOrig) throws FindException;

    /**
     * Sucht nach allen aktuellen und zukuenftigten Rufnummern, die zu M-net portiert werden. Filtert zusaetzlich das
     * Ergebnis von RufnummerService#findDnsKommend, so dass nur Rufnummer zurueckgeliefert werden, bei denen das
     * RealDate nicht in der Vergangenheit liegt.
     * 
     * @param auftragNoOrig Billing-Auftrag-Nummer
     * @return Liste von Rufnummern
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     */
    List<Rufnummer> findDnsKommendForWbci(Long auftragNoOrig) throws FindException;

    /**
     * Sucht nach den letzten, aktiven Aufträgen, die der angegebene Einzelrufnummer zugeordnet wurden. Es werden nur
     * Aufträge berücksichtigt, die folgende Kriterien erfüllen:
     * <ul>
     * <li>ONKZ stimmt überein</li>
     * <li>DN_BASE stimmt überein</li>
     * <li>AUFTRAG_NO_ORIG is ungleich null (Taifun Nr)</li>
     * <li>DIRECT_DIAL null</li>
     * <li>Gruppierung auf DN__NO mit höchster DN_NO</li>
     * </ul>
     * 
     * @param onkz Ortskennzahl
     * @param dnBase Rufnummer
     * @return die leztzen gültigen Taifun Nr. (AUFTRAG_NO_ORIG), das heißt der Rufnummerneintrag mit der höchsten
     *         DN_NO.
     */
    Set<Long> findAuftragIdsByEinzelrufnummer(String onkz, String dnBase) throws FindException;

    /**
     * Sucht nach den letzten, aktiven Aufträgen, die der angegebene Blockrufnummern-Basis zugeordnet wurden. Es werden
     * nur Aufträge berücksichtigt, die folgende Kriterien erfüllen:
     * <ul>
     * <li>ONKZ stimmt überein</li>
     * <li>DN_BASE stimmt überein</li>
     * <li>AUFTRAG_NO_ORIG is ungleich null (Taifun Nr)</li>
     * <li>DIRECT_DIAL not null</li>
     * <li>Gruppierung auf DN__NO mit höchster DN_NO</li>
     * </ul>
     * 
     * @param onkz Ortskennzahl
     * @param dnBase Rufnummer
     * @return die leztzen gültigen Taifun Nr. (AUFTRAG_NO_ORIG), das heißt der Rufnummerneintrag mit der höchsten
     *         DN_NO.
     */
    Set<Long> findAuftragIdsByBlockrufnummer(String onkz, String dnBase) throws FindException;

    /**
     * Ermittelt alle, ueber die Rufnummer zusammen gehoerenden Taifun-Auftraege. <br/>
     * Taifun Auftraege gehoeren dann zusammen, wenn sie min. eine gleiche Rufnummer besitzen wie der Auftrag, der mit
     * {@code orderNoOrig} angegeben ist. <br/>
     * Ablauf:
     * 
     * <pre>
     *    - ermittelt alle billing-relevanten Rufnummern zu dem angebenen Auftrag
     *    - ermittelt alle weiteren Auftraege, die min. eine der Rufnummern als non-billing-relevant(!) eingetragen haben
     * </pre>
     * 
     * @param orderNoOrig
     * @return Set mit den Taifun Auftragsnummern.
     * @throws FindException
     */
    public @NotNull
    Set<Long> getCorrespondingOrderNoOrigs(Long orderNoOrig) throws FindException;

    /**
     * Ermittelt alle Taifun-Auftraege, die "zusammen gehoeren". <br/>
     * Taifun Auftraege gehoeren dann zusammen, wenn sie die gleiche Rufnummer und den gleichen Auftragsstatus besitzen,
     * wie der Auftrag, der mit {@code billingOrderNoOrig} angegeben ist.
     *
     * @param billingOrderNoOrig
     * @return Set mit den zusammen-gehoerenden Taifun-Auftraegen; min. enthalten in der Liste ist
     *         {@code billingOrderNoOrig}
     * @throws FindException
     */
    Set<Long> getCorrespondingBillingOrders4Klammer(Long billingOrderNoOrig) throws FindException;

    /**
     * Sucht nach den letzten, aktiven Aufträgen, die die angegebene Rufnummernbasis als non billable Rufnummer
     * zugeordnet haben. Es werden nur Aufträge berücksichtigt, die folgende Kriterien erfüllen:
     * <ul>
     * <li>ONKZ stimmt überein</li>
     * <li>DN_BASE stimmt überein</li>
     * <li>NON_BILLABLE ist true</li>
     * <li>AUFTRAG_NO_ORIG is ungleich null (Taifun Nr)</li>
     * 
     * @param onkz ONKZ der Rufnummer, ueber die gesucht werden soll
     * @param dnBase DN-Base der Rufnummer, ueber die gesucht werden soll
     * @param rangeFrom (optional) Angabe von 'rangeFrom' der Rufnummer fuer Bloecke
     * @param rangeTo (optional) Angabe von 'rangeTo' der Rufnummer fuer Bloecke
     * @return
     * @throws FindException
     */
    Set<Long> findNonBillableAuftragIds(String onkz, String dnBase, String rangeFrom, String rangeTo)
            throws FindException;

    /**
     * Sucht nach Auftrags- und Rufnummer-Daten, die zu den Query-Parametern 'passen'.
     * 
     * @param query Query-Objekt mit den Parametern, ueber die gesucht werden soll.
     * @return Liste mit Objekten des Typs <code>AuftragDNView</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public List<AuftragDNView> findAuftragDNViews(RufnummerQuery query) throws FindException;

    /**
     * Sucht nach allen ONKZs (Ortsnetzkennzahlen) der Rufnummern, die dem Auftrag mit der (original) Auftragsnummer
     * <code>auftragNoOrig</code> zugeordnet sind. <br>
     * Ausnahme: Rufnummern vom Typ 'Routing', 'Ansage geaenderte Ruf' und 'Ansage kein Anschluss' werden ignoriert!
     * 
     * @param auftragNoOrig (original) Auftragsnummer
     * @return Liste mit den ONKZs (als String)
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public List<String> findOnkz4AuftragNoOrig(Long auftragNoOrig) throws FindException;

    /**
     * Sucht nach allen Haupt-Rufnummern der Auftraege mit den (original) Auftrags-Nummer, die in
     * <code>auftragNoOrigs</code> angegeben sind.
     * 
     * @param auftragNoOrigs List mit den (original) Auftrags-Nummern, zu denen die jeweilige Haupt-Rufnummer gesucht
     *            wird.
     * @return Map mit den Haupt-Rufnummern. Als Key wird die (original) Auftrags-Nummer und als Value die
     *         Haupt-Rufnummer (ONKZ+DN_BASE) (als String) verwendet.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public Map<Long, String> findHauptRNs(List<Long> auftragNoOrigs) throws FindException;

    /**
     * Sucht nach allen Rufnummern der Auftraege mit den (original) Auftrags-Nummer, die in
     * <code>auftragNoOrigs</code> angegeben sind.
     *
     * @param auftragNoOrigs List mit den (original) Auftrags-Nummern, zu denen die jeweilige Rufnummer gesucht wird.
     * @return List mit Paaren. Als Key wird die (original) Auftrags-Nummer und als Value die Anzahl Rufnummer
     * verwendet.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public List<Pair<Long, Integer>> findRNCount(List<Long> auftragNoOrigs) throws FindException;

    /**
     * Sucht nach der Hauptrufnummer zu einem best. Auftrag. <br>
     * Ueber den Parameter <code>findLast</code> kann definiert werden, ob nach dem letzten Rufnummern-Eintrag (true)
     * gesucht wird. Wird fuer den Parameter 'false' angegeben, wird nach dem aktuellen Datensatz gesucht.
     * 
     * @param auftragNoOrig (original) Auftrags-Nummer
     * @param findLast Flag, ob nach dem letzten (true) oder dem aktuellen (false) Eintrag gesucht wird.
     * @return Instanz von <code>Rufnummer</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public Rufnummer findHauptRN4Auftrag(Long auftragNoOrig, boolean findLast) throws FindException;

    /**
     * Sucht nach allen Rufnummern, die einem best. Auftrag zugeordnet sind bzw. waren. <br>
     * Es werden nur Rufnummern beruecksichtigt, deren Status 'AKT' oder 'NEU' ist. <br>
     * <br>
     * Das Result wird nach folgenden Parametern (in der angegebenen Reihenfolge) sortiert: <br>
     * <ul>
     * <li>MAIN_DN (DESC)
     * <li>ONKZ (ASC)
     * <li>DN_BASE (ASC)
     * <li>RANGE_FROM (ASC)
     * </ul>
     * 
     * @param auftragNoOrig (original) Auftragsnummer deren bisherige Rufnummern gesucht werden.
     * @return Liste mit Objekten des Typs <code>Rufnummer</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public List<Rufnummer> findRNs4Auftrag(Long auftragNoOrig) throws FindException;

    /**
     * Ermittelt alle Rufnummern, die einem bestimmten Auftrag zugeordnet sind. <br>
     * Die Ermittlung erfolgt unabhaengig von Rufnummern-Status! <br>
     * Das Result wird nach folgenden Parametern (in der angegebenen Reihenfolge) sortiert: <br>
     * <ul>
     * <li>MAIN_DN (DESC)
     * <li>ONKZ (ASC)
     * <li>DN_BASE (ASC)
     * <li>RANGE_FROM (ASC)
     * <li>HIST_CNT (ASC)
     * </ul>
     * 
     * @param auftragNoOrig
     * @return Liste mit den Rufnummern, die dem Auftrag zugeordnet sind.
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt
     *
     */
    public List<Rufnummer> findAllRNs4Auftrag(Long auftragNoOrig) throws FindException;

    /**
     * Ermittelt alle Rufnummern eines Auftrags, deren Gueltigkeitsdatum mit <code>validDate</code> uebereinstimmt. <br>
     * Dies ist dann der Fall, wenn DN.VALID_FROM<=validDate && DN.VALID_TO>=validDate ist. (Das validDate wird dabei
     * auf den Tag truncated. Uhrzeiten werden also ignoriert.) <br>
     * Die Methode beruecksichtigt Rufnummern mit jedem Status. <br>
     * Das Result wird nach folgenden Parametern (in der angegebenen Reihenfolge) sortiert: <br>
     * <ul>
     * <li>MAIN_DN (DESC)
     * <li>ONKZ (ASC)
     * <li>DN_BASE (ASC)
     * <li>RANGE_FROM (ASC)
     * <li>HIST_CNT (ASC)
     * </ul>
     * 
     * @param auftragNoOrig (original) Auftragsnummer deren Rufnummern ermittelt werden sollen.
     * @param validDate zu pruefendes Gueltigkeitsdatum
     * @return Liste mit den Rufnummern, die dem Auftrag zum angegebenen Datum zugeordnet sind.
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     *
     */
    public List<Rufnummer> findRNs4Auftrag(Long auftragNoOrig, Date validDate) throws FindException;

    /**
     * Sucht nach dem letzten/aktuellsten (HistLast=1) Eintrag zu einer best. Rufnummer.
     * 
     * @param dnNoOrig (original) Rufnummer-No, deren letzter Datensatz geladen werden soll.
     * @return Instanz von <code>Rufnummer</code> oder <code>null</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public Rufnummer findLastRN(Long dnNoOrig) throws FindException;

    /**
     * Prueft, ob es sich bei dem angegebenen Rufnummern-Block um einen M-net Block handelt. <br>
     * Dies ist dann der Fall, wenn im zugehoerigen DN-Block Eintrag der Carrier auf 'AKOM', 'Mnet' oder 'Nefkom' steht.
     * 
     * @param blockNoOrig (original) Block-No
     * @return true wenn es ein AKom-Block ist
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public boolean isMnetBlock(Long blockNoOrig) throws FindException;

    /**
     * Sucht nach dem Businessnumber-Auftragszusatz zu dem Auftrag mit der (original) ID <code>auftragNo</code>.
     * 
     * @param auftragNoOrig (original) Auftragsnummer des Billing-Auftrags
     * @return BAuftragBNFC-Objekt, das die Business-Nummer enthaelt
     * @throws FindException
     *
     */
    public BAuftragBNFC findBusinessNumber(Long auftragNoOrig) throws FindException;

    /**
     * Sucht nach Auftragsdaten ueber eine Servicerufnummer.
     * 
     * @param query
     * @return
     * @throws FindException
     *
     */
    public List<AuftragINRufnummerView> findAuftragINRufnummerViews(BAuftragBNFCQuery query) throws FindException;

    /**
     * Sucht nach allen Rufnummern die nicht HIST_LAST sind und gueltig_bis bestimmtes Datum sind
     * 
     * @return List vom Typ Long, beinhaltet die DN_NO
     */
    public List<Long> findDnNotHistLastTillDate(Date date) throws FindException;

    /**
     * Ermittelt einen TNB (Teilnehmernetzbetreiber) anhand eines eindeutigen Keys.
     * 
     * @param tnb PrimaryKey des gesuchten TNBs
     * @return Objekt vom Typ <code>DNTNB</code>
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt oder zum angegebenen PrimaryKey kein Datensatz
     *             gefunden wird.
     *
     */
    public DNTNB findTNB(String tnb) throws FindException;

    /**
     * Ermittelt die TNB-Kennung, die fuer die ONKZ zustaendig verwendet werden muss.
     * 
     * @param onkz
     * @return
     */
    public @Nullable
    String findTnbKennung4Onkz(@NotNull String onkz)
            throws FindException;

}
