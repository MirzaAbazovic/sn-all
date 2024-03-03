/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.06.2004 09:36:42
 */
package de.augustakom.hurrican.dao.billing;

import java.util.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.billing.BAuftragPos;
import de.augustakom.hurrican.model.billing.view.BAuftragLeistungView;
import de.augustakom.hurrican.model.billing.view.BAuftragView;
import de.augustakom.hurrican.model.billing.view.BVPNAuftragView;
import de.augustakom.hurrican.model.billing.view.TimeSlotView;
import de.augustakom.hurrican.model.cc.query.AuftragSAPQuery;
import de.augustakom.hurrican.service.base.exceptions.FindException;


/**
 * Interface definiert spezielle Methoden fuer DAOs, die Objekte vom Typ <code>de.augustakom.hurrican.model.billing.BAuftrag</code>
 * UND <code>de.augustakom.hurrican.model.billing.BAuftragPos</code> verwalten.
 *
 *
 */
public interface AuftragDAO extends ByExampleDAO {

    /**
     * Findet den letzten Auftrag (HistLast='1') zu einer best. (original) Auftragsnummer.
     *
     * @param auftragNoOrig Auftragsnummer, nach der gesucht werden soll
     * @return BAuftrag oder <code>null</code>
     */
    public BAuftrag findByAuftragNoOrig(final Long auftragNoOrig);

    /**
     * Findet den aktuellen Auftrag (HistStatus='AKT') zu einer best. (original) Auftragsnummer.
     *
     * @param auftragNoOrig Auftragsnummer, nach der gesucht werden soll
     * @return BAuftrag oder <code>null</code>
     */
    public BAuftrag findAuftragAkt(final Long auftragNoOrig);

    /**
     * Sucht nach allen aktuellen Auftraegen eines best. Buendels.
     *
     * @param buendelNo Buendel-No
     * @return Liste mit Objekten des Typs <code>BAuftrag</code> oder <code>null</code>.
     */
    public List<BAuftrag> findByBuendelNo(Integer buendelNo);

    /**
     * Sucht nach allen(!) Auftragspositionen zu einem best. BAuftrag - unabhaengig davon, ob eine Position noch aktiv
     * ist oder nicht.
     *
     *
     * @param auftragNoOrig (original) Auftragsnummer des Auftrags, dessen Positionen gesucht werden sollen.
     * @return Liste mit BAuftragPos-Objekten oder <code>null</code>
     */
    public List<?> findAuftragPos4Auftrag(Long auftragNoOrig);

    /**
     * @param auftragNoOrig        (optional)(original) Auftragsnummer des Auftrags, dessen Positionen gesucht werden
     *                             sollen.
     * @param externLeistungNos    Liste mit den zu beruecksichtigenden externen Leistungskennzeichnungen.
     * @param checkInServiceValues Flag, ob nach den externen Leistungsnummern in den Leistungen (false) oder in den
     *                             Wertelisten (=ServiceValues) gesucht werden soll.
     * @param chargeToBegin        (optionale) Angabe eines ChargeTo Datums, das beruecksichtigt werden soll == Beginn
     * @param chargeToEnd          (optionale) Angabe eines ChargeTo Datums, das beruecksichtigt werden soll == Ende
     * @return Liste mit BAuftragPos-Objekten oder <code>null</code>
     *
     * @see #findAuftragPos4Auftrag(java.lang.Long) Diese Methode ermittelt nur Auftragspositionen, deren Leistungen die
     * angegebene externe Leistungskennzeichnung besitzt.
     */
    public List<BAuftragPos> findAuftragPos4Auftrag(Long auftragNoOrig, List<Long> externLeistungNos,
            boolean checkInServiceValues, Date chargeToBegin, Date chargeToEnd);

    /**
     * Sucht nach allen Auftraegen eines Kunden.
     *
     * @param kundeNo Kundennummer
     * @return Liste mit Objekten des Typs <code>BAuftrag</code>.
     *
     */
    public List<BAuftrag> findAuftraege4Kunde(Long kundeNo);

    /**
     * Sucht den aktuell gueltigen Auftrag dem das Geraet (Fritz-Box) mit der cwmpId fuer den Zeitpunkt valid zugeordnet
     * ist. Wirf eine Exception wenn die Trefferliste nicht eindeutig ist (Taifun Daten falsch gepflegt).
     *
     * @param cwmpId CWMP-ID der Fritz Box
     * @param valid
     * @return einen Auftrag oder <code>null</code> wenn keiner gefunden ist
     */
    BAuftrag findAuftrag4CwmpId(String cwmpId, Date valid);

    /**
     * Sucht nach allen VPN-Auftraegen, die im Billing-System eingetragen sind.
     *
     * @return Liste mit Objekten des Typs <code>BVPNAuftragView</code>.
     */
    public List<BVPNAuftragView> findVPNAuftraege();

    /**
     * Funktion liefert alle Auftragspositionen, die für die Report-Erstellung relevant sind
     *
     * @param auftragNoOrig original (Billing-)Auftragsnummer
     * @param onlyAkt       liefert nur aktuelle Leistungen
     * @return Liste mit Objekten des Typs <code>BAuftragPos</code>
     *
     */
    public List<BAuftragPos> findBAuftragPos4Report(Long auftragNoOrig, Boolean onlyAkt);

    /**
     * Findet den letzten Auftrag (hoechter HIST_COUNT) zu einer best. (original) Auftragsnummer mit dem ASTATUS 7
     * (=STORNIERT)
     *
     * @param auftragNoOrig Auftragsnummer, nach der gesucht werden soll
     * @return BAuftrag oder <code>null</code>
     *
     */
    public BAuftrag findAuftragStornoByAuftragNoOrig(Long auftragNoOrig);

    /**
     * Funktion liefert einen Billing-Auftrag anhand der SAP-Auftragsnummer. (Es wird dabei der erste gefunde Auftrag
     * zur SAP-Nummer zurueck gegeben.)
     *
     * @param sapOrderId SAP-Nummer, zu der der Auftrag ermittelt werden soll
     * @return erster gefundener Billing-Auftrag zur SAP-Nummer
     *
     */
    public BAuftrag findBySAPOrderId(String sapOrderId);

    /**
     * Liefert alle Auftraege zu den SAP IDs im Query-Objekt
     *
     * @param query Query-Objekt mit den Suchparametern
     * @return Liste mit Objekten des Typs <code>BAuftrag</code>
     *
     */
    public List<BAuftrag> findAuftraege4SAP(AuftragSAPQuery query);


    /**
     * Liefert eine {@link TimeSlotView} zu einem bestimmten Auftrag
     *
     * @param auftragNoOrig Id des Taifun Auftrags
     * @return TimeSlotView-Objekt
     *
     */
    public TimeSlotView findTimeSlotView4Auftrag(Long auftragNoOrig);

    /**
     * Ermittelt zu allen angegebenen Auftraegen die hinterlegten TimeSlots.
     * @param auftragNoOrigs
     * @return
     */
    public Map<Long, TimeSlotView> findTimeSlotViews4Auftrag(List<Long> auftragNoOrigs);

    /**
     * Sucht nach allen Auftraegen und den zugehoerigen Leistungen eines best. Kunden.<br> Ueber das Flag
     * <code>onlyActLeistungen</code> kann bestimmt werden, ob alle (false) oder nur aktive (true) Leistungen ermittelt
     * werden sollen. Außerdem können mit <code>noKuendAuftraege</code> gekündigte Aufträge ausgefiltert werden und mit
     * <code>onlyLeistungen4Extern</code> werden nur Leistungen mit Physik-Db-Mapping (Produkt oder Leistung) angezeigt
     *
     * @param kundeNoOrig           Kunden-No des Kunden, dessen aktuelle Auftraege gesucht werden.
     * @param taifunOrderNoOrig     (optional!) Angabe einer Taifun-Auftragsnummer, auf die sich die Suche beschraenken
     *                              soll
     * @param noKuendAuftraege      keine gekündigten Aufträge anzeigen (true), sonst alle
     * @param onlyActLeistungen     Flag ob nur aktive (true) oder auch bereits gekuendigte (false) Leistungen ermittelt
     *                              werden sollen.
     * @param onlyLeistungen4Extern nur Leistungen mit Physik-Db-Mapping (Produkt oder Leistung) bei true
     * @return
     */
    public List<BAuftragLeistungView> findAuftragLeistungView(Long kundeNoOrig, Long taifunOrderNoOrig, boolean noKuendAuftraege,
            boolean onlyActLeistungen, boolean onlyLeistungen4Extern);

    /**
     * Ermittelt alle Auftraege des Kunden.
     *
     * @param kundeNo
     * @return
     */
    public List<BAuftragView> findAuftragViews(Long kundeNo);

    /**
     * Funktion ermittelt die DebitorNr des Auftrags
     *
     * @param auftragNo interne AuftragNr des Taifun-Auftrags, darf nicht null sein
     * @return DebitorNr des Auftrags
     * @throws FindException Falls ein Fehler auftrat
     *
     */
    public String findDebitorNrForAuftragNo(Long auftragNo);

    /**
     * Sucht nach allen aktiven Aufträgen für die angegebene GeoId. Es werden nur die letzten aktiven Aufträge in der
     * Historie berücksichtigt. Ein Autrag ist noch aktiv, wenn er: <ul> <li>nicht storniert wurde (hist_status !=
     * UNG)</li> <li>nicht gekündigt wurde (cancel_date is NULL)</li> <li>das kündigungsdatum in der Zukunft liegt
     * (cacel_date > now)</li> </ul>
     *
     * @param geoId                 Eindeutige geografische ID des Kunden
     * @param ignoreCancelledOrders {@code true} um bereits gekuendigte Auftraege (=Kuendigungsdatum in der
     *                              Vergangenheit) auszuschliessen; {@code false} um auch gekuendigte Auftraege zu
     *                              ermitteln
     * @return Liste von AuftragIds
     */
    List<Long> findAuftragIdsByGeoId(Long geoId, boolean ignoreCancelledOrders);

}
