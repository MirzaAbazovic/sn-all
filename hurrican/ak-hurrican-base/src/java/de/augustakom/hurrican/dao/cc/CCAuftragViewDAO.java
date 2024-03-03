/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.07.2004 07:58:54
 */
package de.augustakom.hurrican.dao.cc;

import java.util.*;

import de.augustakom.hurrican.model.cc.CCAddress;
import de.augustakom.hurrican.model.cc.view.AuftragIntAccountQuery;
import de.augustakom.hurrican.model.cc.view.AuftragIntAccountView;
import de.augustakom.hurrican.model.cc.view.CCAuftragIDsView;
import de.augustakom.hurrican.model.cc.view.CCAuftragProduktVbzQuery;
import de.augustakom.hurrican.model.cc.view.CCAuftragProduktVbzView;
import de.augustakom.hurrican.model.cc.view.CCKundeAuftragView;
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
import de.augustakom.hurrican.model.shared.view.IncompleteAuftragView;
import de.augustakom.hurrican.model.shared.view.WbciRequestCarrierView;
import de.augustakom.hurrican.service.base.exceptions.FindException;


/**
 * DAO-Interface fuer DAO-Klassen, die View-Modelle fuer Auftraege erstellen (z.B. <code>AuftragCarrierView</code>).
 *
 *
 */
public interface CCAuftragViewDAO {

    /* Keys fuer die Maps, die die Methode findActiveAuftraege4AM zurueck gibt. */
    public static final String AM_KEY_CC_AUFTRAG_ID = "CC_AUFTRAG_ID";
    public static final String AM_KEY_CC_AUFTRAG_STATUS_ID = "AUFTRAG_STATUS_ID";
    public static final String AM_KEY_PRODAK_ORDER__NO = "PRODAK_ORDER__NO";
    public static final String AM_KEY_BUENDEL_NR = "BUENDEL_NR";
    public static final String AM_KEY_BUENDEL_NR_HERKUNFT = "BUENDEL_NR_HERKUNFT";
    public static final String AM_KEY_PROD_ID = "PROD_ID";
    public static final String AM_KEY_ANSCHLUSSART = "ANSCHLUSSART";
    public static final String AM_KEY_AKTIONS_ID = "AKTIONS_ID";
    public static final String AM_KEY_ANZAHL = "ANZAHL";
    /* Ende Keys fuer findActiveAuftraege4AM */

    /**
     * Sucht nach Auftragsdaten.
     *
     * @param query Query-Objekt mit den Suchparametern.
     * @return Liste mit Objekten des Typs <code>AuftragDatenView</code>
     */
    public List<AuftragDatenView> findAuftragDatenViews(AuftragDatenQuery query);

    /**
     * Sucht nach Auftraegen, die innerhalb eines best. Zeitraums in Betrieb gegangen, geaendert oder gekuendigt
     * wurden/werden bzw. deren Vorgabe-SCV innerhalb dieses Bereichs liegt.
     *
     * @param query Query-Objekt mit den Suchparametern.
     * @return Liste mit Objekten des Typs <code>AuftragInbetriebnahmeView</code>.
     */
    public List<AuftragRealisierungView> findRealisierungViews(AuftragRealisierungQuery query);

    /**
     * Sucht nach Auftrags- und Carrierdaten.
     *
     * @param query Query-Objekt mit den Suchparametern.
     * @return Liste mit Objekten des Typs <code>AuftragCarrierView</code>
     */
    public List<AuftragCarrierView> findAuftragCarrierViews(AuftragCarrierQuery query);

    /**
     * Suche nach WBCI Requests.
     *
     * @param vorabstimmungsId
     * @return
     */
    public List<WbciRequestCarrierView> findWbciRequestCarrierViews(String vorabstimmungsId);

    /**
     * Sucht nach Auftrags- und Endstellendaten.
     *
     * @param query Query-Objekt mit den Suchparametern.
     * @return Liste mit Objekten des Typs <code>AuftragEndstelleView</code>
     */
    public List<AuftragEndstelleView> findAuftragEndstelleViews(AuftragEndstelleQuery query);

    /**
     * Sucht nach Auftrags- und Equipment-Daten.
     *
     * @param query Query-Objekt mit den Suchparametern.
     * @return Liste mit Objekten des Typs <code>AuftragEquipmentView</code>
     */
    public List<AuftragEquipmentView> findAuftragEquipmentViews(AuftragEquipmentQuery query);

    /**
     * Sucht nach Auftrags- und Endstellendaten fuer VPN-Auftraege.
     *
     * @param vpnId ID des VPNs, dessen zugehoerige Auftraege ermittelt werden sollen. Wird fuer die ID
     *              <code>null</code> und fuer die Kunden-No eine Liste angegeben, werden alle Auftraege der
     *              entsprechenden Kunden gesucht, die KEINEM VPN zugeordnet sind.
     * @param kNos  Liste mit Kundennummern, auf die die Suche eingeschraenkt werden soll.
     * @return Liste mit Objekten des Typs <code>AuftragEndstelleView</code>
     */
    public List<AuftragEndstelleView> findAuftragEndstelleViews4VPN(Long vpnId, List<Long> kNos);

    /**
     * Sucht nach den gekuendigten(!) Auftrags- und Endstellendaten fuer eine best. GeoID und Physik-Typen. <br> Die
     * Liste kann verwendet werden, um nach den Endstellen fuer eine Anschlussuebernahme zu suchen.
     *
     * @param geoId        GeoID, an der die Endstellen zugeordnet sein muessen.
     * @param physikTypIds Liste mit den Physiktyp-IDs, die unterstuetzt werden muessen.
     * @return Liste mit Objekten des Typs <code>AuftragEndstelleView</code>.
     */
    public List<AuftragEndstelleView> findAESViews4Uebernahme(Long geoId, List<Long> physikTypIds);

    /**
     * Sucht nach den gekuendigten(!) Auftrags- und Endstellendaten fuer einen best. Kunden. Abhaengig von
     * <code>produtkGruppeExclusive</code> werden nur Auftraege gesucht, die sich innerhalb (false) oder ausserhalb
     * (true) der angegebenen ProduktGruppe befinden. Die Liste kann fuer die Endstellen-Auswahl bei Up-/Downgrades
     * verwendet werden.
     *
     * @param kundeNoOrig              (original) Kundennummer
     * @param produktGruppeIdExclusive ID der ProduktGruppe, die ausgschlossen werden soll
     * @return Liste mit Objekten des Typs <code>AuftragEndstelleView</code>.
     */
    public List<AuftragEndstelleView> findAESViews4KundeAndPG(Long kundeNoOrig, Long produktGruppeId,
            boolean produktGruppeExclusive);

    /**
     * Sucht nach gekuendigten(!) Auftraegen (bzw. Endstellen) eines best. Kunden in einer best. Strasse. <br> Abhaengig
     * von <code>analog2isdn</code> wird nach Analog- (true) oder ISDN-Auftraegen (false) gesucht. Die Liste kann fuer
     * die Endstellen-Auswahl bei Wandel ISDN->Analog verwendet werden.
     *
     * @param geoId       GeoID, an der sich die Endstellen befinden muessen
     * @param kundeNoOrig (original) Kundennummer des Kunden, dem die Auftraege zugeordnet sein muessen.
     * @param analog2isdn Angabe, ob nach Endstellen fuer den Wandel Analog->ISDN (true) oder fuer ISDN->Analog (false)
     *                    gesucht wird. <br> Wird fuer den Parameter <code>null</code> angegeben, werden alle
     *                    gekuendigten Auftraege des Kunden gesucht - unabhaengig vom Produkt.
     * @return Liste mit Objekten des Typs <code>AuftragEndstelleView</code>.
     */
    public List<AuftragEndstelleView> findAESViews4Wandel(Long geoId, Long kundeNoOrig, Boolean analog2isdn);

    /**
     * Sucht nach allen Endstellen (bzw. Auftraegen) zu einer best. GeoID, die fuer eine Port-Aenderung (WITA: LAE,
     * AEN-LMAE, SER-POW) in Frage kommen <br> Die in Frage kommenden Auftraege muessen folgende Bedingungen erfuellen:
     * <ul> <li>Endstelle befindet sich an angegebener GeoID <li>Auftrag befindet sich in einem Kuendigungs-Status
     * </ul>
     *
     * @param geoId
     * @param esTyp
     * @return Liste mit Objekten des Typs {@link AuftragEndstelleView}
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt
     */
    public List<AuftragEndstelleView> findAESViews4TalPortAenderung(Long geoId, String esTyp);

    /**
     * Sucht nach Auftrags-, Produkt- und Leitungsdaten.
     *
     * @param query Query-Objekt mit den Suchparametern.
     * @return Liste mit Objekten des Typs <code>CCAuftragProduktVbzView</code>
     */
    public List<CCAuftragProduktVbzView> findAuftragProduktVbzViews(CCAuftragProduktVbzQuery query);

    /**
     * Sucht nach Auftrags- und IntAccount-Daten.
     *
     * @param query Query-Objekt mit den Suchparametern. Es werden immer alle Parameter ausgewertet!
     * @return Liste mit Objekten des Typs <code>AuftragIntAccountView</code>.
     */
    public List<AuftragIntAccountView> findAuftragAccountViews(AuftragIntAccountQuery query);

    /**
     * Sucht nach der CC-Auftrags-ID und der VerbindungsBezeichnung (Technischen Dokumentationsnummer) ueber die
     * (original) Auftrags-Nummer aus dem Billing-System.
     *
     * @param auftragNoOrig (original) Auftrags-Nummer aus dem Billing-System.
     * @return Liste mit Objekten des Typs <code>CCAuftragIdsView</code>
     */
    public List<CCAuftragIDsView> findAufragIdAndVbz4AuftragNoOrig(Long auftragNoOrig);

    /**
     * @param buendelNr         Buendel-Nr
     * @param buendelNrHerkunft
     * @return Liste mit Objekten des Typs <code>CCAuftragIdsView</code>
     * @see findAufragIdAndVbz4AuftragNoOrig Statt ueber die (original) Auftrags-Nr. wird allerdings ueber die
     * Buendel-Nr gesucht.
     */
    public List<CCAuftragIDsView> findAuftragIdAndVbz4BuendelNr(Integer buendelNr, String buendelNrHerkunft);

    /**
     * Sucht nach allen wichtigen Auftragsdaten aus dem CC-System, die einem best. Kunden zugeordnet sind.
     *
     * @param kundeNoOrig         (original) Kundennummer
     * @param orderByBuendel      Flag, ob nach der Buendel-Nr (true) sortiert werden soll. Wenn <code>false</code>
     *                            angegeben wird, wird nach der Auftrags-ID sortiert!
     * @param excludeInvalid      Flag, ob ungueltige Auftraege (storno, Absage, Kuendigung) heraus gefiltert (true)
     *                            werden sollen.
     * @param excludeKonsolidiert Flag, ob konsolidierte Auftrage heraus gefiltert werden sollen.
     * @return Liste mit Objekten des Typs <code>CCKundeAuftragView</code>
     */
    public List<CCKundeAuftragView> findAuftragViews4Kunde(Long kundeNoOrig, boolean orderByBuendel,
            boolean excludeInvalid, boolean excludeKonsolidiert);

    /**
     * Sucht nach allen aktiven CC-Auftraegen fuer einen Kunden (evtl. eingeschraenkt auf einen bestimmten
     * Taifun-Auftrag). <br> Es werden dabei nur die Auftraege beruecksichtigt, die eine Beziehung zu Billing-Auftraegen
     * haben koennen/muessen. In der Result-List der Methode sind Objekte des Typs <code>Map</code> enthalten. Diese
     * enthalten wiederum folgende Keys: <ul> <li><code>PRODAK_ORDER__NO</code> fuer den Zugriff auf die (original)
     * Auftrags-No aus dem Billing-System. Wert ist vom Typ Integer. <li><code>PROD_ID_TEXT</code> fuer den Zugriff auf
     * die Produkt-ID aus dem Billing-System (Bezeichnung_I). Wert ist vom Typ String. <li><code>ANSCHLUSSART</code>
     * fuer den Zugriff auf die Produktbezeichnung. (Wert vom Typ String.) <li><code>AKTIONS_ID</code> fuer den Zugriff
     * auf die Aktions-ID des CC-Produkts (Wert ist vom Typ Integer.) <li><code>ANZAHL</code> fuer den Zugriff auf die
     * Anzahl der CC-Auftraege. (Wert vom Typ Integer.) </ul> Alle Keys sind in diesem Interface als Konstante (mit
     * Prefix: <code>AM_KEY_</code>) definiert.
     *
     * @param kundeNoOrig
     * @param taifunOrderNoOrig (optional!) Angabe der Taifun-Auftragsnummer, auf die sich die Ermittlung beschraenken
     *                         soll
     * @return
     */
    public List<Map<String, Object>> findActiveAuftraege4AM(Long kundeNoOrig, Long taifunOrderNoOrig);

    /**
     * Sucht nach allen zukuenftigen Auftraegen.
     *
     * @return Liste mit Objekten des Typs <code>AuftragVorlaufView</code>.
     */
    public List<AuftragVorlaufView> findAuftragsVorlauf();

    /**
     * Sucht nach allen unvollstaendigen bzw. noch nicht abgeschlossenen Auftraegen.
     *
     * @return Liste mit Objekten des Typs <code>IncompleteAuftragView</code>.
     */
    public List<IncompleteAuftragView> findIncomplete(final Date gueltigVon);

    /**
     * Funktion liefert alle Hurrican-Aufträge sowie zugehörige Kundennummer und Taifun-Auftragsnummer zu der
     * übergebenen Id einer {@link CCAddress}
     *
     * @return eine Liste mit gefundenen Hurrican-Aufträge und zugehörigen Taifun-Nummern
     */
    public List<CCKundeAuftragView> findKundeAuftragViews4Address(Long addressId);

    /**
     * Funktion liefert alle Hurrican-Aufträge sowie zugehörige Kundennummer und Taifun-Auftragsnummer zu den
     * übergebenen AuftragsIDs zurück.
     *
     * @return eine Liste mit gefundenen Hurrican-Aufträge und zugehörigen Taifun-Nummern
     */
    public List<CCAuftragIDsView> findAufragIdAndVbz4AuftragIds(Collection<Long> auftragIds);
}
