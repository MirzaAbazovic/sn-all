/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.06.2004 13:15:14
 */
package de.augustakom.hurrican.service.billing;

import java.time.*;
import java.util.*;
import javax.annotation.*;

import de.augustakom.hurrican.model.billing.Account;
import de.augustakom.hurrican.model.billing.Adresse;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.billing.BAuftragConnect;
import de.augustakom.hurrican.model.billing.BAuftragKombi;
import de.augustakom.hurrican.model.billing.BAuftragPos;
import de.augustakom.hurrican.model.billing.BAuftragScv;
import de.augustakom.hurrican.model.billing.Leistung;
import de.augustakom.hurrican.model.billing.view.BAuftragLeistungView;
import de.augustakom.hurrican.model.billing.view.BAuftragVO;
import de.augustakom.hurrican.model.billing.view.BAuftragView;
import de.augustakom.hurrican.model.billing.view.BVPNAuftragView;
import de.augustakom.hurrican.model.billing.view.EndstelleAnsprechpartnerView;
import de.augustakom.hurrican.model.billing.view.TimeSlotView;
import de.augustakom.hurrican.model.cc.IntAccount;
import de.augustakom.hurrican.model.cc.query.AuftragSAPQuery;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;


/**
 * Interface definiert spezielle Methoden fuer Service-Implementierungen, die Objekte vom Typ <code>BAuftrag</code> UND
 * <code>BAuftragPos</code> verwalten.
 *
 *
 */
public interface BillingAuftragService extends IBillingService {

    /**
     * Sucht nach einem Billing-Auftrag ueber die (original) Auftragsnummer. Es wird nur letzte Auftrag zurueck gegeben
     * (HistLast='1')!
     *
     * @param auftragNoOrig (original) Auftragsnummer
     * @return BAuftrag oder <code>null</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    @CheckForNull
    public BAuftrag findAuftrag(Long auftragNoOrig) throws FindException;

    /**
     * Sucht nach dem aktuellen Billing-Auftrag (HistStatus = 'AKT') ueber die (original) Auftragsnummer.
     *
     * @param auftragNoOrig (original) Auftragsnummer
     * @return BAuftrag oder <code>null</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public BAuftrag findAuftragAkt(Long auftragNoOrig) throws FindException;

    /**
     * Sucht nach allen (nicht gekuendigten) Auftraegen, die einem best. Buendel zugeordnet sind.
     *
     * @param buendelNo Buendel-No
     * @return Liste mit allen (aktuellen) Auftraegen eines Buendels.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public List<BAuftrag> findByBuendelNo(Integer buendelNo) throws FindException;

    /**
     * Sucht nach den Auftragspositionen eines best. Auftrags. <br> Ueber das Flag <code>onlyAct</code> kann definiert
     * werden, ob nur nach aktiven oder auch nach inaktiven Leistungen gesucht wird. Eine AuftragsPos/Leistung, die in
     * der Zukunft gekuendigt wird, wird nicht als aktiv angesehen!
     *
     * @param auftragNoOrig (original) Auftragsnummer, des Auftrags, dessen Positionen gesucht werden sollen.
     * @param onlyAct       Filterung auf aktive oder auch inaktive Leistungen/Positionen
     * @return Liste mit BAuftragPos-Objekten oder <code>null</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public List<BAuftragPos> findAuftragPositionen(Long auftragNoOrig, boolean onlyAct) throws FindException;

    /**
     * Sucht nach den Auftragspositionen eines best. Auftrags, die Leistungen mit der externen Leistungskennzeichnung
     * <code>idExternLeistungen</code> besitzen. <br> Ueber das Flag <code>onlyAct</code> kann definiert werden, ob nur
     * nach aktiven oder auch nach inaktiven Leistungen gesucht wird. Eine AuftragsPos/Leistung, die in der Zukunft
     * gekuendigt wird, wird nicht als aktiv angesehen!
     *
     * @param auftragNoOrig     (original) Auftragsnummer, des Auftrags, dessen Positionen gesucht werden sollen.
     * @param onlyAct           Filterung auf aktive oder auch inaktive Leistungen/Positionen
     * @param externLeistungNos Liste mit den externen Leistung-IDs, die beruecksichtigt werden sollen.
     * @return Liste mit BAuftragPos-Objekten oder <code>null</code>
     * @throws FindException
     *
     */
    public List<BAuftragPos> findAuftragPositionen(Long auftragNoOrig, boolean onlyAct,
            List<Long> externLeistungNos) throws FindException;


    /**
     * Ermittelte die Auftragsposition zum angegebenen Auftrag, die die Leistung mit der externen Kennzeichnung
     * 'externLeistungNo' besitzt. <br> Dabei ist es unerheblich, ob die Auftragsposition noch aktiv ist oder nicht.
     *
     * @param auftragNoOrig
     * @param externLeistungNo
     * @return
     * @throws FindException
     *
     */
    public BAuftragPos findAuftragPosition(Long auftragNoOrig, Long externLeistungNo) throws FindException;


    /**
     * Ermittelt alle Billing-Auftraege (bzw. die AUFTRAG__NOs davon), die eine Auftragsposition besitzen, die folgenden
     * Bedingungen entspricht:
     * <ul>
     *     <li>EXT_LEISTUNG__NO der zugehoerigen Leistung bzw. Werteliste ist in {@code externLeistungNos} enthalten</li>
     *     <li>chargeTo Datum der Auftragsposition == {@code chargeTo}</li>
     * </ul>
     * @param externLeistungNos zu beruecksichtigende EXT_LEISTUNG__NOs
     * @param chargeTo zu beruecksichtigendes chargeTo fuer die Auftragspositionen
     * @param daysToGoBack Anzahl der Tage (in die Vergangenheit), die ebenfalls beruecksichtigt werden sollen
     * @return Set mit den AUFTRAG__NOs der matchenden Billing-Auftraege
     * @throws FindException
     */
    public Set<Long> findAuftragNoOrigsWithExtLeistungNos(Set<Long> externLeistungNos, LocalDate chargeTo, int daysToGoBack) throws FindException;

    /**
     * Ermittelt eine Auftragsuebersicht zu dem angegebenen Kunden.
     *
     * @param kundeNo
     * @return
     * @throws FindException
     */
    public List<BAuftragView> findAuftragViews(Long kundeNo) throws FindException;

    /**
     * Ermittelt einige Basis-Informationen zu dem angegebenen TAI-Auftrag
     *
     * @param auftragNoOrig
     * @return
     * @throws FindException
     */
    public BAuftragVO getBasicOrderInformation(Long auftragNoOrig) throws FindException;

    /**
     * Findet alle relevanten Daten der Auftragspositionen und Leistungen zu einem bestimmten BAuftrag.
     *
     * @param auftragNoOrig         originale Auftrags-Nummer
     * @param onlyAct               Flag, ob nur nach aktiven Leistungen/Positionen gesucht werden soll.
     * @param onlyLeistungen4Extern Flag, ob nur nach Leistungen gesucht werden soll, die eine Steuerung fuer externe
     *                              Systeme besitzen.
     * @return Liste mit Objekten des Typs <code>BAuftragLeistungView</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public List<BAuftragLeistungView> findAuftragLeistungViews4Auftrag(Long auftragNoOrig, boolean onlyAct,
            boolean onlyLeistungen4Extern) throws FindException;

    /**
     * Ermittelt alle produkt-relevanten Leistungen zu dem angegebenen Auftrag. <br/> Es wird dabei nicht unterschieden,
     * ob die Leistung aktuell noch aktiv oder bereits gekuendigt ist!
     *
     * @param auftragNoOrig
     * @return
     */
    public List<BAuftragLeistungView> findProduktLeistungen4Auftrag(Long auftragNoOrig) throws FindException;

    /**
     * Sucht nach allen Auftraegen und den zugehoerigen Leistungen eines best. Kunden. <br> Ueber das Flag
     * <code>onlyAct</code> kann bestimmt werden, ob alle (false) oder nur aktive (true) Leistungen ermittelt werden
     * sollen.
     *
     * @param kundeNoOrig Kunden-No des Kunden, dessen aktuelle Auftraege gesucht werden.
     * @param onlyAct     Flag ob nur aktive (true) oder auch bereits gekuendigte (false) Leistungen ermittelt werden
     *                    sollen.
     * @return Liste mit Objekten vom Typ <code>BAuftragLeistungView</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public List<BAuftragLeistungView> findAuftragLeistungViews(Long kundeNoOrig, boolean onlyAct) throws FindException;

    /**
     * Sucht nach allen Leistungen mit entsprechenden extProdNos ODER externMiscNOs zum angegebenen Auftrag und Produkt
     *
     * @param auftragNoOrig (Taifun-) Auftrag-Id
     * @param extProdNo     (Hurrican-) Produkt-Id
     * @param externMiscNOs - darf nicht null oder leer sein
     * @return Liste mit Objekten vom Typ <code>BAuftragLeistungView</code> die den Suchkriterien entsprechen oder eine
     * leere Liste, falls keine Suchkriterien angegeben wurden.
     * @throws FindException falls bei der Abfrage ein unerwarteter Fehler auftritt.
     */
    public List<BAuftragLeistungView> findAuftragLeistungViewsByExtProdNoAndExtMiscNOs(Long auftragNoOrig, Long extProdNo,
            Collection<Long> externMiscNOs) throws FindException;

    /**
     * Ermittelt von dem Auftrag mit der Id {@code auftragNoOrig} alle Positionen, deren zugehoerige {@link
     * Leistung#getExternMiscNo()} dem Wert {@code externMiscNo} entspricht und prueft, ob bei min. einem Eintrag {@link
     * BAuftragPos#getChargedUntil()} dem Wert {@code null} entspricht.
     *
     * @param auftragNoOrig
     * @param externMiscNo
     * @return
     */
    public boolean hasUnchargedServiceElementsWithExtMiscNo(Long auftragNoOrig, Long externMiscNo)
            throws FindException;

    /**
     * Sucht nach allen aktiven Billing-Auftraegen fuer einen Kunden, die fuer den AuftragsMonitor interessant sind.
     *
     * @param kundeNoOrig       Angabe der Kundennummer, zu der eine Uebersicht der Billing-Auftraege ermittelt werden
     *                          soll
     * @param taifunOrderNoOrig (optional!) Angabe einer Taifun-Auftragsnummer, auf die sich die Suche beschraenken
     *                          soll
     * @return Liste mit Objekten des Typs <code>BAuftragLeistungView</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public List<BAuftragLeistungView> findActiveAuftraege4AM(Long kundeNoOrig, Long taifunOrderNoOrig) throws FindException;

    /**
     * Sucht nach allen VPN-Auftraegen, die im Billing-System eingetragen sind.
     *
     * @return Liste mit Objekten des Typs <code>BVPNAuftragView</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public List<BVPNAuftragView> findVPNAuftraege() throws FindException;

    /**
     * Ermittelt alle Accounts, die dem Auftrag mit der ID <code>auftragNo</code> zugeordnet sind. <br>
     *
     * @param auftragNo ID des Auftrags (Achtung: nicht die __NO!)
     * @return Liste mit Objekten des Typs <code>Account</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     *
     */
    public List<Account> findAccounts4Auftrag(Long auftragNo) throws FindException;

    /**
     * Ermittelt den Account des Auftrags. <br> Die Funktion liefert bei mehreren Accounts immer den ersten zurueck!
     *
     * @param auftragNoOrig original Auftragsnummer
     * @param useAkt        Flag, ob der Account von der aktuellen (true) oder der letzten (false)
     *                      Auftragshistorisierung ermittelt werden soll.
     * @return zugeordneter Account
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt
     *
     */
    public Account findAccount4Auftrag(Long auftragNoOrig, boolean useAkt) throws FindException;

    /**
     * @param auftragNoOrig Auftragsnummer
     * @return Adress-Objekt der Anschlussadresse
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     *
     * @deprecated use {@link BillingAuftragService#findAnschlussAdresse4Auftrag(Long, String)} Liefert zu einem Auftrag
     * die Anschluss-Adresse
     */
    @Deprecated
    public Adresse findAnschlussAdresse4Auftrag(Long auftragNoOrig) throws FindException;

    /**
     * @param auftragNoOrig
     * @param esTyp
     * @return
     * @throws FindException
     * @see #findAnschlussAdresse4Auftrag(java.lang.Long)
     */
    public Adresse findAnschlussAdresse4Auftrag(Long auftragNoOrig, String esTyp) throws FindException;

    /**
     * Funktion liefert alle fuer einen Report relevanten Auftrags-Positionen
     *
     * @param auftragNoOrig Auftragsnummer
     * @param onlyAkt       liefert nur aktuelle Leistungen, d.h. alle Leistungen ohne End-Datum
     * @return Liste mit Auftragspositionen
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     *
     */
    public List<BAuftragPos> findBAuftragPos4Report(Long auftragNoOrig, Boolean onlyAkt) throws FindException;

    /**
     * Funktion liefert zu einem Auftrag den Auftragszusatz Kombi
     *
     * @param auftragNo Auftragsnummer
     * @return AuftragKombi-Objekt
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     *
     */
    public BAuftragKombi findAuftragKombiByAuftragNo(Long auftragNo) throws FindException;

    /**
     * Funktion liefert zu einem Auftrag den Auftragszusatz SCV
     *
     * @param auftragNo Auftragsnummer
     * @return AuftragSCV-Objekt
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     *
     */
    public BAuftragScv findAuftragScvByAuftragNo(Long auftragNo) throws FindException;

    /**
     * Ermittelt den Auftragszusatz 'Connect' zu dem angegebenen Auftrag.
     *
     * @param auftragNo Nummer des Auftrags, zu dem der Connect-Zusatz ermittelt werden soll.
     * @return Instanz von <code>BAuftragConnect</code>
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     *
     */
    public BAuftragConnect findAuftragConnectByAuftragNo(Long auftragNo) throws FindException;

    /**
     * Findet den letzten Auftrag (hoechter HIST_COUNT) zu einer best. (original) Auftragsnummer mit dem ASTATUS 7
     * (=STORNIERT)
     *
     * @param auftragNoOrig Auftragsnummer, nach der gesucht werden soll
     * @return BAuftrag oder <code>null</code>
     *
     */
    public BAuftrag findAuftragStornoByAuftragNoOrig(Long auftragNoOrig) throws FindException;

    /**
     * Funktion liefert den aktuell gueltigen Auftrag zu einer SAP-Auftragsnummer.
     *
     * @param sapOrderId SAP-Auftragsnummer
     * @return BAuftrag-Objekt
     * @throws FindException
     *
     */
    public BAuftrag findAuftrag4SAPOrderId(String sapOrderId) throws FindException;

    /**
     * Liefert alle Auftraege, die den SAP IDs im Query-Objekt entsprechen
     *
     * @param query Query-Objekt mit den Suchparametern
     * @return Liste mit BillingAuftraegen
     * @throws FindException
     *
     */
    public List<BAuftrag> findAuftraege4SAP(AuftragSAPQuery query) throws FindException;

    /**
     * Funktion uebertraegt den Account eines Hurrican Auftrags nach Taifun. Der Account wird nur angelegt, wenn in
     * Taifun noch kein Account besteht.
     *
     * @param auftragNo Id des Auftrags, dessen Account uebertragen werden soll
     * @param acc       Account-Objekt
     * @throws StoreException Falls ein Fehler auftrat
     *
     */
    public void updateAccount(Long auftragNo, IntAccount acc) throws StoreException;

    /**
     * Funktion ermittelt das Realsierungszeitfenster zu einem Auftrag
     *
     * @param auftragNoOrig ID des Taifun-Auftrags (AUFTRAG__NO!!)
     * @return TimeSlotView-Objekt
     * @throws FindException Falls ein Fehler auftrat
     *
     */
    public TimeSlotView findTimeSlotView4Auftrag(Long auftragNoOrig) throws FindException;

    /**
     * Ermittelt das Realisierungszeitfenster zu allen angegebenen Auftraegen.
     * @param auftragNoOrigs
     * @return Map mit {@link TimeSlotView} zur Auftrags-Nummer
     * @throws FindException
     */
    public Map<Long, TimeSlotView> findTimeSlotViews4Auftrag(List<Long> auftragNoOrigs) throws FindException;

    /**
     * Funktion ermittelt die DebitorNr des Auftrags
     *
     * @param auftragNo die interne AuftragsNr des Taifun-Auftrags
     * @return DebitorNr des Auftrags
     * @throws FindException Falls ein Fehler auftrat
     *
     */
    public String findDebitorNrForAuftragNo(Long auftragNo) throws FindException;

    /**
     * Sucht den aktuell gueltigen Auftrag dem das Geraet (Fritz-Box) mit der cwmpId fuer den Zeitpunkt valid zugeordnet
     * ist. Wirf eine Exception wenn die Trefferliste nicht eindeutig ist (Taifun Daten falsch gepflegt).
     *
     * @param cwmpId CWMP-ID der Fritz Box
     * @param valid
     * @return einen Auftrag oder <code>null</code> wenn keiner gefunden ist
     * @throws FindException
     */
    BAuftrag findAuftrag4CwmpId(String cwmpId, Date valid) throws FindException;

    /**
     * Sucht nach allen aktiven Aufträgen für die angegebene GeoId. Es werden nur die letzten aktiven Aufträge in der
     * Historie berücksichtigt. Ein Autrag ist noch aktiv, wenn er: <ul> <li>nicht storniert wurde (hist_status !=
     * UNG)</li> <li>nicht gekündigt wurde (cancel_date is NULL) </li> <li>das kündigungsdatum in der Zukunft liegt
     * (cacel_date > now)</li> </ul>
     *
     * @param geoId                 Eindeutige geografische ID des Kunden
     * @param ignoreCancelledOrders {@code true} um bereits gekuendigte Auftraege (=Kuendigungsdatum in der
     *                              Vergangenheit) auszuschliessen; {@code false} um auch gekuendigte Auftraege zu
     *                              ermitteln
     * @return Liste von AuftragIds
     * @throws FindException Falls während der Suche einen Fehler auftreten sollte, wird eine FindException geworfen.
     */
    Set<Long> findAuftragIdsByGeoId(Long geoId, boolean ignoreCancelledOrders) throws FindException;

    /**
     * Findet den Endstellenansprechpartner für einen Auftrag. Je nach Auftragsart ist die Datenquelle unterschiedlich:
     * <ul> <li>Geschäftlich: Daten aus BAuftrag</li> <li>Privat: Daten aus Kunde</li> </ul>
     *
     * @param auftragNoOrig
     * @return
     * @throws FindException
     */
    EndstelleAnsprechpartnerView findEndstelleAnsprechpartner(Long auftragNoOrig) throws FindException;
}
