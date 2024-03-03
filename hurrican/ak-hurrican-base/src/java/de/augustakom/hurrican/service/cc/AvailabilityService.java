/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.12.2010 11:07:44
 */
package de.augustakom.hurrican.service.cc;

import java.util.*;
import javax.annotation.*;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.exceptions.AvailabilityException;
import de.augustakom.hurrican.model.cc.GeoId;
import de.augustakom.hurrican.model.cc.GeoId2TechLocation;
import de.augustakom.hurrican.model.cc.GeoId2TechLocationView;
import de.augustakom.hurrican.model.cc.GeoIdClarification;
import de.augustakom.hurrican.model.cc.GeoIdLocation;
import de.augustakom.hurrican.model.cc.KvzSperre;
import de.augustakom.hurrican.model.cc.query.GeoIdClarificationQuery;
import de.augustakom.hurrican.model.cc.query.GeoIdQuery;
import de.augustakom.hurrican.model.cc.query.GeoIdSearchQuery;
import de.augustakom.hurrican.model.cc.vento.availability.VentoGetAvailabilityInformationRequest;
import de.augustakom.hurrican.model.cc.vento.availability.VentoGetAvailabilityInformationResponse;
import de.augustakom.hurrican.model.cc.view.GeoIdClarificationView;
import de.augustakom.hurrican.model.shared.iface.AddressModel;
import de.augustakom.hurrican.service.base.exceptions.DeleteException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;

/**
 * Service-Interface fuer die Verwaltung von Verfuegbarkeitsinformationen innerhalb der Hurrican-Datenbank. <br> (Fuer
 * die (WebService)Zugriffe auf das Vento-System steht ein eigener Service zur Verfuegung!)
 */
public interface AvailabilityService extends ICCService {

    String EXCEPTION = "exception";
    String WARNING = "warning";
    String SUCCESS = "success";

    <T extends GeoIdLocation> T save(T location);

    /**
     * Ermittelt alle {@link de.augustakom.hurrican.model.cc.GeoId} Eintraege, die den Query-Parametern entsprechen.
     *
     * @param query Query-Objekt mit den Such-Parametern.
     * @return Liste mit {@link de.augustakom.hurrican.model.cc.GeoId} Objekten
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt
     */
    List<GeoId> findGeoIdsByQuery(GeoIdQuery query) throws FindException;

    /**
     * Ermittelt alle {@link de.augustakom.hurrican.model.cc.GeoId} Eintraege, die den Query-Parametern entsprechen.
     *
     * @param query Query-Objekt mit den Such-Parametern.
     * @return Liste mit {@link de.augustakom.hurrican.model.cc.GeoId} Objekten
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt
     */
    List<GeoId> findGeoIdsBySearchQuery(GeoIdSearchQuery query) throws FindException;

    /**
     * @see de.augustakom.hurrican.service.cc.AvailabilityService#findOrCreateGeoId(Long, Long)
     *
     * @param geoId ID der zu ladenden Geo-ID
     * @return die ermittelte {@link de.augustakom.hurrican.model.cc.GeoId}
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt. Wichtig: es wird keine Exception generiert,
     *                       wenn keine GeoID mit der angegebenen ID gefunden wurde!
     */
    GeoId findGeoId(Long geoId) throws FindException;

    /**
     * Ermittelt die {@link GeoId} in der Hurrican Cache-Tabelle und gibt den Eintrag zurueck. <br/>
     * Falls die GeoId noch nicht im Cache ist, wird diese angelegt!
     *
     * @param geoId     ID der gesuchten {@link GeoId}
     * @param sessionId ID der aktuellen User-Session
     * @return Ermittelte bzw. neu generierter {@link GeoId} Eintrag.
     * @throws AvailabilityException wenn bei der Suche bzw. Anlage der GeoID ein Fehler auftritt
     */
    GeoId findOrCreateGeoId(Long geoId, Long sessionId) throws AvailabilityException;

    /**
     * Speichert das angegebene {@link de.augustakom.hurrican.model.cc.GeoId} Objekt.
     *
     * @param toSave zu speicherndes Objekt
     * @return die gespeicherte Instanz
     * @throws StoreException
     */
    GeoId saveGeoId(GeoId toSave) throws StoreException;

    /**
     * Speichert das angegebene Mapping-Objekt.
     *
     * @param toSave    zu speicherndes Objekt
     * @param sessionId aktuelle Session ID des Users
     * @return die gespeicherte Instanz
     * @throws StoreException wenn beim Speichern ein Fehler auftritt
     */
    GeoId2TechLocation saveGeoId2TechLocation(GeoId2TechLocation toSave, Long sessionId)
            throws StoreException;

    /**
     * Löscht das angegebene View-Objekt bzw das dazugehörige Mapping-Objekt.
     *
     * @param toDelete zu löschendes Objekt
     * @throws DeleteException wenn beim Löschen ein Fehler auftritt
     */
    void deleteGeoId2TechLocationView(GeoId2TechLocationView toDelete) throws DeleteException;

    /**
     * Ermittelt alle Mapping-Eintraege zu einer bestimmten Geo-ID.
     *
     * @param geoId Geo-ID, deren Mappings zu technischen Standorten ermittelt werden sollen.
     * @return Liste mit den Mappings zu technischen Standorten (Objekte vom Typ {@link GeoId2TechLocation}
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     */
    List<GeoId2TechLocation> findGeoId2TechLocations(Long geoId) throws FindException;

    /**
     * Ermittelt alle View-Eintraege zu einer Geo-ID.
     *
     * @param geoId Geo-ID, deren Views zu technischen Standorten ermittelt werden sollen.
     * @return Liste mit den Mappings zu technischen Standorten (Objekte vom Typ {@link GeoId2TechLocationView}
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     */
    List<GeoId2TechLocationView> findGeoId2TechLocationViews(Long geoId) throws FindException;

    /**
     * Ermittelt den Mapping-Eintrag zur Kombination aus Geo-ID und Standort-ID.
     *
     * @param geoId         Geo-ID
     * @param hvtIdStandort ID des technischen Standorts
     * @return Mapping
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt
     */
    GeoId2TechLocation findGeoId2TechLocation(Long geoId, Long hvtIdStandort) throws FindException;

    /**
     * Ermittelt alle Geo-IDs zu den angegebenen Strassen-Informationen. Gesucht wird im lokalen Vento Cache.
     *
     * @param street      Strassenname
     * @param houseNum    Hausnummer
     * @param houseNumExt Hausnummerzusatz
     * @param zipCode     Postleitzahl
     * @param city        Stadt
     * @param district    Ortsteil
     * @return Liste mit allen Geo-IDs zu den angegebenen Informationen.
     * @throws StoreException           wenn bei der Ermittlung ein Fehler auftritt.
     */
    List<GeoId> mapLocationDataToGeoIds(String street, String houseNum, String houseNumExt, String zipCode,
            String city, String district)
            throws StoreException;

    /**
     * Ermittelt die Availability Informationen zu einer Geo-ID. Unter anderem sind dies: <li>Connection-Type (z.B. HVT,
     * FTTB, FTTH, Gewofag etc.) <li>Technology-Type (z.B. ISDN, POTS, ADSL_ISDN, SDSL, SHDSL etc.) <li>Leitungslaenge
     * (bei HVT) <li>max. Bandbreite, falls speziell konfiguriert
     *
     * @param request Geo-ID zu der die Informationen ermittelt werden sollen.
     * @return availability information zu dem request
     */
    VentoGetAvailabilityInformationResponse getAvailabilityInformation(
            VentoGetAvailabilityInformationRequest request) throws FindException;

    /**
     * Ermittelt alle Einträge aus der Geo ID Klärungsliste, die in der Liste {@code states} aufgeführt sind.
     *
     * @param states Clarification states
     * @param geoIds wenn vorhanden und nicht leer, schränkt die Ergebnismenge auf die enthaltenen GeoIds ein
     * @return Liste mit den Klärungen
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt
     */
    List<GeoIdClarification> findGeoIdClarificationsByStatus(List<GeoIdClarification.Status> states,
            List<Long> geoIds) throws FindException;

    /**
     * Ermittelt alle Einträge aus der Geo ID Klärungsliste, die in der Liste {@code states} aufgeführt sind. Fuegt
     * jeweils die Adressedaten der Geo ID hinzu.
     *
     * @return Liste mit den Klärungen als View
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt
     */
    List<GeoIdClarificationView> findGeoIdClarificationViewsByStatus(List<GeoIdClarification.Status> states)
            throws FindException;

    /**
     * Ermittelt alle Einträge aus der Geo ID Klärungsliste, die mit den Parametern in der Query übereinstimmen.
     *
     * @return Liste mit den Klärungen
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt
     */
    List<GeoIdClarificationView> findGeoIdClarificationViewsByQuery(GeoIdClarificationQuery query)
            throws FindException;

    /**
     * Ermittelt den Geo ID Klärfall anhand seiner {@code id}.
     *
     * @return Klärfall
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt
     */
    GeoIdClarification findGeoIdClarificationById(Long id) throws FindException;

    /**
     * Speichert die angegebene Klärung.
     *
     * @param toSave    zu speicherndes Objekt
     * @param sessionId aktuelle Session ID des Users
     * @throws StoreException wenn beim Speichern ein Fehler auftritt
     */
    void saveGeoIdClarification(GeoIdClarification toSave, Long sessionId) throws StoreException;

    /**
     * Ermittelt existierende {@link GeoId2TechLocation} Einträge für die {@code geoId}. Die Ergebnismenge enthält nur
     * Einträge, die in der Konfiguration (Prioritätenliste) erlaubt sind. So erscheinen alle die Einträge nicht, deren
     * Standorttyp in der Prioritätenliste fehlen. <br> Dabei erfolgt die Ermittlung der {@link GeoId2TechLocation}s
     * sortiert nach der Prioritaet der Produkt/Standortyp Konfiguration.
     *
     * @param geoId  GeoID
     * @param prodId ID des Hurrican Produkts
     * @return Liste mit GeoId2TechLocation
     * @throws FindException
     */
    List<GeoId2TechLocation> findPossibleGeoId2TechLocations(GeoId geoId, Long prodId) throws FindException;

    /**
     * Import-Funktion, um einzelne GeoIDs (oder auch ganze Abschnitte) in Hurrican zu importieren und diese einem
     * technischen Standort zuzuordnen. <br> Als Import-File wird eine Excel-Datei mit folgenden Spalten (in genau
     * dieser Reihenfolge) erwartet: <ul> <li>STANDORT (Name des technischen Standorts) <li>STRASSE <li>HAUSNUMMER (bzw.
     * Hausnummernbereich) <li>HAUSNUMMER_ZUSATZ <li>PLZ <li>ORT <li>ONKZ <li>ASB <li>KVZ_NUMMER <li>ENTFERNUNG </ul>
     * (Die erste Zeile des Excel-Sheets dient als Ueberschrift und wird im Import nicht beruecksichtigt!
     *
     * @param filename Dateiname des Excel-Files
     * @return Map mit folgenden Keys: exception, success, warning (die zugehoerigen Values sind Strings mit Angabe von
     * Fehler-, Erfolgs- bzw. Warnmeldungen)
     * @throws StoreException Falls eine Fehler auftrat
     * @throws FindException  Wenn bei der Ermittlung der notwendigen Daten ein Fehler auftritt
     */
    Map<String, Object> importGeoIdsAnKVZ(String filename, Long sessionId) throws StoreException, FindException;

    /**
     * Import-Funktion, analog zu importGeoIdsAnKVZ() <br> Als Import-File wird eine Excel-Datei mit folgenden Spalten
     * (in genau dieser Reihenfolge) erwartet: <ul> <li>STANDORT (Name des technischen Standorts) <li>STRASSE
     * <li>HAUSNUMMER (bzw. Hausnummernbereich) <li>HAUSNUMMER_ZUSATZ <li>PLZ <li>ORT <li>ONKZ <li>ASB <li>KVZ_NUMMER
     * <li>ENTFERNUNG </ul> (Die erste Zeile des Excel-Sheets dient als Ueberschrift und wird im Import nicht
     * beruecksichtigt!
     *
     * @param filename Dateiname des Excel-Files
     * @return Map mit folgenden Keys: exception, success, warning (die zugehoerigen Values sind Strings mit Angabe von
     * Fehler-, Erfolgs- bzw. Warnmeldungen)
     * @throws StoreException Falls eine Fehler auftrat
     * @throws FindException  Wenn bei der Ermittlung der notwendigen Daten ein Fehler auftritt
     */
    Map<String, Object> importGeoIdsForVDSLAnHVT(String filename, Long sessionId) throws StoreException,
            FindException;

    /**
     * Liefert eine Anschlussadresse in der Dtag-Schreibweise. Diese Schreibweise wird via Vento-Web-Service ermittelt.
     * Es wird ein neues Adress-Objekt mit den Informationen aus {@code address} erstellt (ein Clone). Die
     * Lokalitaetsinformationen werden jedoch mit den DTAG Schreibweisen ersetzt.
     *
     * @param geoId   GeoId der Endstelle
     * @param address Anschlussadresse deren Dtag-Schreibweise ermittelt werden soll
     * @return ein AdressModel in Dtag-Schreibweise
     * @throws FindException            Wenn bei der Ermittlung der notwendigen Daten ein Fehler auftritt. z.B. wenn der
     *                                  Vento Web-Service nicht verfügbar ist.
     * @throws FindException
     */
    AddressModel getDtagAddressForCb(Long geoId, AddressModel address) throws FindException;

    /**
     * Gibt ONKZ und ASB zur angegebenen GeoId zurueck, oder null, falls keine geoId uebergeben wurde.
     *
     * @throws FindException            Wenn bei der Ermittlung der notwendigen Daten ein Fehler auftritt. z.B. wenn der
     *                                  Vento Web-Service nicht verfügbar ist.
     * @throws ServiceNotFoundException
     */
    Pair<Integer, String> findAsbAndOnKzForGeoId(Long geoId) throws FindException, ServiceNotFoundException;

    /**
     * Gibt ein Standortkuerzel zur angegebenen GeoId zurueck, oder null, falls in Vento kein Kuerzel gepflegt oder
     * keine geoId uebergeben wurde.
     *
     * @throws FindException            Wenn bei der Ermittlung der notwendigen Daten ein Fehler auftritt. z.B. wenn der
     *                                  Vento Web-Service nicht verfügbar ist.
     * @throws ServiceNotFoundException
     */
    String findStandortKuerzelForGeoId(Long geoId) throws FindException, ServiceNotFoundException;


    /**
     * Ermittelt alle {@link de.augustakom.hurrican.model.cc.GeoId}s, die von der KVZ-Sperre betroffen sind und ordnet
     * diese dem Standort mit Id {@code futureHvtIdStandort} zu.
     * @param kvzSperre KVZ-Sperre, ueber die die betroffenen GeoIds ermittelt werden kann
     * @param currentHvtIdStandort Id des Standorts, dem die GeoIds bisher zugeordnet sind
     * @param futureHvtIdStandort Id des Standorts, dem die GeoIds zugeordnet werden sollen
     * @param sessionId aktuelle Session-Id des Users
     * @throws de.augustakom.hurrican.service.base.exceptions.StoreException wenn beim Umschreiben der GeoIds ein Fehler
     * auftritt
     */
    void moveKvzLocationsToHvt(@Nonnull KvzSperre kvzSperre, @Nonnull Long currentHvtIdStandort,
            @Nonnull Long futureHvtIdStandort, @Nonnull Long sessionId)
            throws StoreException;

}
