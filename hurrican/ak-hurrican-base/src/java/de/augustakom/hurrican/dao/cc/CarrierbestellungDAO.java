/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.08.2004 15:46:29
 */
package de.augustakom.hurrican.dao.cc;

import java.util.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.DeleteDAO;
import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.CarrierVaModus;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.view.AQSView;
import de.augustakom.hurrican.model.cc.view.CuDAVorschau;
import de.mnet.wbci.model.CarrierCode;

/**
 * DAO-Interface fuer Objekte des Typs <code>Carrierbestellung</code>
 *
 *
 */
public interface CarrierbestellungDAO extends FindDAO, FindAllDAO, StoreDAO, ByExampleDAO, DeleteDAO {

    /**
     * Sucht nach den Aderquerschnitten und Leitungslaengen von allen Endstellen, die sich in einer best. GeoId
     * befinden.
     *
     * @param geoId GeoId
     * @return Liste mit Objekten des Typs <code>AQSView</code>
     */
    public List<AQSView> findAqsLL4GeoId(Long geoId);

    /**
     * Erzeugt eine neue Mapping-ID fuer die Verbindung zwischen Objekten des Typs <code>Carrierbestellung</code> und
     * <code>Endstelle</code>.
     *
     * @return
     */
    public Long createNewMappingId();

    /**
     * Sucht nach allen Carrierbestellungen, die einer best. Endstelle zugeordnet sind. <br> Die Ergebnisliste ist nach
     * der CB-ID absteigend sortiert.
     *
     * @param esId ID der Endstelle
     * @return Liste mit Objekten des Typs <code>Carrierbestellung</code>
     */
    public List<Carrierbestellung> findByEndstelle(Long esId);

    /**
     * Sucht nach der letzten/aktuellsten Carrierbestellung, die einer best. Endstelle zugeordnet ist.
     *
     * @param esId ID der Endstelle
     * @return Instanz von <code>Carrierbestellung</code> oder <code>null</code>.
     */
    public Carrierbestellung findLastByEndstelle(Long esId);

    /**
     * Sucht nach allen Carrierbestellungen fuer eine Vertragsnummer.
     *
     * @param vertragsnummer
     * @return Liste mit Objekten des Typs <code>Carrierbestellung</code>, leer aber nicht null
     */
    public List<Carrierbestellung> findByVertragsnummer(String vertragsnummer);


    /**
     * Sucht nach allen Carrier-Eintraegen. <br> Die Sortierung erfolgt ueber das Feld <i>ORDER_NO</i>.
     *
     * @return Liste mit Objekten des Typs <code>Carrier</code>.
     */
    public List<Carrier> findCarrier();

    /**
     * Sucht nach allen Carrier-Eintraegen die mit einem best. Vorabstimmungsmodus angebunden sind. <br> Die Sortierung
     * erfolgt ueber das Feld <i>VA_MODUS</i>.
     *
     * @param modus Vorabstimmungsmodus des Carriers.
     * @return Liste mit Objekten des Typs <code>Carrier</code>.
     */
    public List<Carrier> findCarrierByVaModus(CarrierVaModus modus);

    /**
     * Sucht nach allen Carrier-Eintraegen die für PV auswaehlbar sind. <br> Die Sortierung erfolgt ueber das Feld
     * <i>ORDER_NO</i>.
     *
     * @return Liste mit Objekten des Typs <code>Carrier</code>.
     */
    public List<Carrier> findCarrierForAnbieterwechsel();

    /**
     * Sucht nach allen Carrier-Eintraegen, die mit M-net direkt via WBCI oder über das WBCI-Portal verbunden sind.
     *
     * @return Liste mit Objekten des Typs <code>Carrier</code>, sortiert nach Namen.
     */
    List<Carrier> findWbciAwareCarrier();

    /**
     * Sucht nach allen Carrier-Eintraegen passend zum angegebenen ITU Carrier Code.
     *
     * @return <code>Carrier</code> mit ITU Carrier Code.
     */
    Carrier findCarrierByCarrierCode(CarrierCode carrierCode);

    /**
     * Sucht nach den Carrier mit der angegebenen Portierungskennung.
     *
     * @return <code>Carrier</code> mit der angegebenen Portierungskennung.
     */
    Carrier findCarrierByPortierungskennung(String portierungskennung);

    /**
     * Sucht nach dem Carrier, der fuer einen HVT-Standort benoetigt wird.
     *
     * @param hvtId ID des HVT-Standorts.
     * @return Objekt vom Typ <code>Carrier</code> oder <code>null</code>.
     */
    public Carrier find4HVT(Long hvtId);

    /**
     * Ermittelt die Anzahl der durchgefuehrten CuDA-Bestellungen fuer Auftraege, deren Schaltungstermin > vorgabeSCV
     * ist.
     *
     * @param vorgabeSCV
     * @return Liste mit Objekten des Typs <code>CuDAVorschau</code>.
     */
    public List<CuDAVorschau> createCuDAVorschau(Date vorgabeSCV);

    /**
     * Sucht nach den Carrierbestellungen fuer einen best. Carrier. <br> Ueber die Parameter <code>maxResults</code> und
     * <code>beginWith</code> kann bestimmt werden, ab welchem Datensatz das Ergebnis beginnen soll und wie viele
     * Datensaetze maximal ausgelesen werden.
     *
     * @param carrierId  ID des Carriers
     * @param maxResults Anzahl der maximal gelieferten Datensaetze (Angabe von -1 bedeutet, dass alle Datensaetze
     *                   geliefert werden sollen).
     * @param beginWith  Datensatz, ab dem das Ergebnis beginnen soll
     * @return Liste mit Objekten des Typs <code>Carrierbestellung</code>
     */
    public List<Carrierbestellung> findCBs4Carrier(Long carrierId, int maxResults, int beginWith);

    /**
     * Sucht den IDs der Auftraege, die eine best. Carrier-Bestellung besitzen.
     *
     * @param cbId ID der Carrierbestellung
     * @return Liste mit den Auftrag-IDs, die die angegebene Carrier-Bestellung besitzen
     */
    public List<Long> findAuftragIds4CB(Long cbId);

    /**
     * Ermittelt die HVT-Standort ID zu einer bestimmten Carrierbestellung.
     *
     * @param cbId ID der Carrierbestellung zu der der HVT-Standort ermittelt werden soll
     * @return ID des zugehoerigen HVT-Standorts.
     *
     */
    public Long findHvtStdId4Cb(Long cbId);

}
