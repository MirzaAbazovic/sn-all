/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.06.2004 15:43:45
 */
package de.augustakom.hurrican.dao.billing;

import java.util.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.hurrican.model.billing.DNBlock;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.billing.query.RufnummerQuery;
import de.augustakom.hurrican.model.shared.view.AuftragDNView;

/**
 * DAO-Definition zur Verwaltung von Objekten vom Typ <code>Rufnummer</code>.
 * 
 *
 */
public interface RufnummerDAO extends ByExampleDAO, FindDAO {

    /**
     * Ermittelt alle Rufnummern zu der angegebenen DN__NO und dem (optional angegebenen) validFrom Datum. Die
     * Ermittlung erfolgt in aufsteigender Reihenfolge von DN_NO!
     * 
     * @param dnNoOrig
     * @param validFrom
     * @return Liste mit Objekten des Typs {@link Rufnummer}
     */
    public List<Rufnummer> findDNsByDnNoOrig(Long dnNoOrig, Date validFrom);

    /**
     * Sucht nach allen aktuellen(!) Rufnummern, die den Query-Parametern entsprechen.
     * 
     * @param query Query-Objekt mit den Suchparametern.
     * @return Liste von Rufnummer-Objekten oder <code>null</code>
     */
    public List<Rufnummer> findByQuery(final RufnummerQuery query);

    /**
     * Sucht nach Rufnummer-, Auftrags- und Kunden-Daten.
     * 
     * @param query Query-Objekt mit den Such-Parametern.
     * @return Liste mit Objekten des Typs <code>AuftragDNView</code>
     */
    public List<AuftragDNView> findAuftragDNViewsByQuery(final RufnummerQuery query);

    /**
     * Sucht nach allen Rufnummern, die einem best. Billing-Auftrag zugeordnet sind. Ueber den Parameter
     * <code>onlyActual</code> kann angegeben werden, ob nur die aktuellen (true) oder alle (false) Rufnummern gesucht
     * werden sollen.
     * 
     * @param auftragNoOrig original Auftragsnummer
     * @param onlyActual Suche nach allen (false) oder aktuellen/neuen (true) Rufnummern
     * @param onlyLast Suche nach den letzten (HistLast=1) Rufnummern
     * @return Liste von Rufnummer-Objekten oder <code>null</code>
     */
    public List<Rufnummer> findByAuftragNoOrig(final Long auftragNoOrig, final boolean onlyActual,
            final boolean onlyLast);

    /**
     * Sucht nach allen Rufnummern, die einem best. Billing-Auftrag zugeordnet sind. <br>
     * Es wird nur nach Rufnummern mit dem Status 'AKT' oder 'NEU' gesucht.
     * 
     * @param auftragNoOrig original Auftragsnummer
     * @return Liste von Rufnummer-Objekten oder <code>null</code>
     */
    public List<Rufnummer> findByAuftragNoOrig(Long auftragNoOrig);

    /**
     * Ermittelt alle Rufnummern, die einem bestimmten Auftrag zugeordnet sind. <br>
     * Die Ermittlung erfolgt unabhaengig von Rufnummern-Status! <br>
     * Das Result wird nach folgenden Parametern sortiert: <br>
     * <ul>
     * <li>MAIN_DN (DESC)
     * <li>ONKZ (ASC)
     * <li>DN_BASE (ASC)
     * <li>RANGE_FROM (ASC)
     * </ul>
     * 
     * @param auftragNoOrig
     * @return Liste mit den Rufnummern, die dem Auftrag zugeordnet sind.
     *
     */
    public List<Rufnummer> findAllRNs4Auftrag(Long auftragNoOrig);

    /**
     * Ermittelt alle Rufnummern eines Auftrags, deren Gueltigkeitsdatum mit <code>validDate</code> uebereinstimmt. <br>
     * Dies ist dann der Fall, wenn DN.VALID_FROM<=validDate && DN.VALID_TO>=validDate ist. <br>
     * Die Methode beruecksichtigt Rufnummern mit jedem Status. <br>
     * Das Result wird nach folgenden Parametern sortiert: <br>
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
     *
     */
    public List<Rufnummer> findRNs4Auftrag(Long auftragNoOrig, Date validDate);

    /**
     * Sucht nach allen ONKZs (Ortsnetzkennzahlen) der Rufnummern, die einem best. Auftrag zugeordnet sind.
     * 
     * @param auftragNoOrig (original) Auftragsnummer
     * @return Liste mit den ONKZs
     */
    public List<String> findOnkz4AuftragNoOrig(final Long auftragNoOrig);

    /**
     * Sucht nach allen Haupt-Rufnummern der Auftraege mit den (original) Auftrags-Nummer, die in
     * <code>auftragNoOrigs</code> angegeben sind.
     * 
     * @param auftragNoOrigs List mit den (original) Auftrags-Nummern, zu denen die jeweilige Haupt-Rufnummer gesucht
     *            wird.
     * @return Map mit den Haupt-Rufnummern. Als Key wird die (original) Auftrags-Nummer und als Value die
     *         Haupt-Rufnummer (als String) verwendet.
     */
    public Map<Long, String> findHauptRNs(List<Long> auftragNoOrigs);

    /**
     * Sucht nach dem letzten (HistLast=1) Rufnummern-Eintrag fuer die angegebene (original) Rufnummern-No.
     * 
     * @param dnNoOrig
     * @return Instanz von <code>Rufnummer</code> oder <code>null</code>.
     */
    public Rufnummer findLastRufnummer(Long dnNoOrig);

    /**
     * Sucht nach dem letzten (HistLast=1) DNBlock-Eintrag fuer die angegebene (original) Block-No.
     * 
     * @param blockNoOrig
     * @return Instanz von <code>DNBlock</code> oder <code>null</code>.
     */
    public DNBlock findLastDNBlock(Long blockNoOrig);

    /**
     * Sucht nach allen Rufnummern die nicht HIST_LAST sind und gueltig_bis bestimmtes Datum sind
     * 
     * @return List vom Typ Long, beinhaltet die DN_NO
     */
    public List<Long> findDnNotHistLastTillDate(Date date);

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
    Set<Long> findAuftragIdsByEinzelrufnummer(String onkz, String dnBase);

    /**
     * Sucht nach den letzten, aktiven Aufträg, die der angegebene Basis-Blockrufnummer zugeordnet wurden. Es werden nur
     * Aufträge berücksichtigt, die folgende Kriterien erfüllen:
     * <ul>
     * <li>ONKZ stimmt überein</li>
     * <li>DN_BASE stimmt überein</li>
     * <li>AUFTRAG_NO_ORIG is ungleich null (Taifun Nr)</li>
     * <li>DIRECT_DIAL not null</li>
     * <li>Gruppierung auf DN__NO mit höchster DN_NO</li>
     * </ul>
     * 
     * @param onkz Ortskennzahl
     * @param dnBase Rufnummerbasis der Blockrufnummern
     * @return die leztzen gültigen Taifun Nr. (AUFTRAG_NO_ORIG), das heißt der Rufnummerneintrag mit der höchsten
     *         DN_NO.
     */
    Set<Long> findAuftragIdsByBlockrufnummer(String onkz, String dnBase);

    /**
     * Sucht nach allen aktiven Billing-Aufträgen, die die angegebene Rufnummernbasis als non billable Rufnummer
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
     */
    Set<Long> findNonBillableAuftragIds(String onkz, String dnBase, String rangeFrom, String rangeTo);

}
