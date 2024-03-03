/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.06.2004 11:30:51
 */
package de.augustakom.hurrican.service.cc;

import java.util.*;

import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTRaum;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.HVTStandortTechType;
import de.augustakom.hurrican.model.cc.HVTTechnik;
import de.augustakom.hurrican.model.cc.KvzAdresse;
import de.augustakom.hurrican.model.cc.KvzSperre;
import de.augustakom.hurrican.model.cc.UEVT;
import de.augustakom.hurrican.model.cc.UEVT2Ziel;
import de.augustakom.hurrican.model.cc.query.HVTQuery;
import de.augustakom.hurrican.model.cc.view.HVTClusterView;
import de.augustakom.hurrican.model.cc.view.HVTGruppeStdView;
import de.augustakom.hurrican.model.cc.view.UevtBuchtView;
import de.augustakom.hurrican.service.base.exceptions.DeleteException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;

/**
 * Interface zur Definition eines Services, der fuer HVTs zustaendig ist. <br>
 *
 *
 */
public interface HVTService extends ICCService {

    /**
     * Sucht nach einer HVT-Gruppe ueber deren ID.
     *
     * @param hvtGruppeId ID der gesuchten HVT-Gruppe
     * @return HVTGruppe oder <code>null</code>
     * @throws FindException wenn bei der Suche ein Fehler auftritt.
     */
    HVTGruppe findHVTGruppeById(Long hvtGruppeId) throws FindException;

    /**
     * Sucht nach einer HVTGruppe, die einem best. HVT-Standort zugeordnet ist.
     *
     * @param hvtStandortId ID des HVT-Standorts, dessen HVT-Gruppe gesucht wird
     * @return HVTGruppe oder <code>null</code>.
     * @throws FindException wenn bei der Suche ein Fehler auftritt.
     */
    HVTGruppe findHVTGruppe4Standort(Long hvtStandortId) throws FindException;

    /**
     * Sucht nach allen HVT-Gruppen.
     *
     * @return Liste mit Objekten vom Typ <code>HVTGruppe</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    List<HVTGruppe> findHVTGruppen() throws FindException;

    /**
     * Sucht nach wichtigen Daten aller HVT-Standorte und -Gruppen.
     *
     * @param onkz
     * @param asb
     * @param ortsteil
     * @param ort
     * @param standortTypRefId
     * @param clusterId
     * @return Pair mit zwei Liste mit <code>HVTStandort</code> und <code>HVTGruppe</code>.
     */
    Pair<List<HVTStandort>, List<HVTGruppe>> findHVTStandorteAndGruppen(String onkz, Integer asb, String ortsteil,
            String ort, Long standortTypRefId, String clusterId ) throws FindException;

    /**
     * Erzeugt oder aktualisiert eine HVT-Gruppe.
     *
     * @param toSave zu speichernde HVT-Gruppe
     * @return das gespeicherte HVTGruppe Object
     * @throws StoreException wenn beim Speichern ein Fehler auftritt.
     */
    HVTGruppe saveHVTGruppe(HVTGruppe toSave) throws StoreException;

    /**
     * Findet alle HVT-Standorte, die einer best. HVT-Gruppe angehoeren.
     *
     * @param hvtGruppeId ID der HVT-Gruppe
     * @param onlyActive  Angabe, ob nur nach aktiven (gueltigen) HVT-Standorten gesucht werden soll.
     * @return Liste mit Objekten des Typs <code>HVTStandort</code> oder <code>null</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt
     */
    List<HVTStandort> findHVTStandorte4Gruppe(Long hvtGruppeId, boolean onlyActive) throws FindException;

    /**
     * Sucht nach allen HVT-Standorten.
     *
     * @return Liste mit Objekten des Typs <code>HVTStandort</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    List<HVTStandort> findHVTStandorte() throws FindException;

    /**
     * Sucht nach einem best. HVT-Standort ueber dessen ID.
     *
     * @param hvtStdId ID des gesuchten HVT-Standorts.
     * @return HVTStandort-Objekt oder <code>null</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    HVTStandort findHVTStandort(Long hvtStdId) throws FindException;

    /**
     * Ermittelt einen HVT-Standort ueber die ONKZ und den ASB.
     *
     * @param onkz relevante/gesuchte ONKZ
     * @param asb  relevanter/gesuchter ASB
     * @return Objekt vom Typ <code>HVTStandort</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt oder kein passender HVT-Standort ermittelt werden
     *                       konnte.
     *
     */
    HVTStandort findHVTStandort(String onkz, Integer asb) throws FindException;

    /**
     * Findet alle Standorte eines Typs, mit der gegebenen ONKZ und der DTAG ASB (letzte 3 Stellen).
     *
     * @param onkz
     * @param dtagAsb
     * @param standortTypRefId
     * @throws FindException
     */
    List<HVTStandort> findHVTStandort4DtagAsb(String onkz, Integer dtagAsb, Long standortTypRefId)
            throws FindException;

    /**
     * Sucht nach HVTs, die einem sog. Example-Objekt entsprechen.
     *
     * @param example Example-Objekt fuer die Suche. Der Client kann div. Suchkriterien ueber das Example-Objekt
     *                definieren.
     * @return Liste mit Objekten des Typs <code>HVTStandort</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    List<HVTStandort> findHVTStandorte(HVTStandort example) throws FindException;

    /**
     * Sucht nach den Ziel-HVTs die fuer einen UEVT und eine Produkt-Physiktyp-Zuordnung verwendet werden koennen. <br>
     * Ist die P2PT-Zuordnung auf 'virtuell' gesetzt, werden die Ziel-HVTs ueber die Tabelle t_uevt_2_ziel gesucht. Ist
     * 'virtuell' nicht gesetzt, wird nur der HVT zurueck gegeben, dem der UEVT zugeordnet ist.
     *
     * @param uevtId ID des UEVTs
     * @param p2ptId ID der Produkt-Physiktyp-Zuordnung.
     * @return List mit Objekten des Typs <code>HVTStandort</code> die als Ziel-HVTs verwendet werden koennen.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    List<HVTStandort> findHVTStdZiele(Long uevtId, Long p2ptId) throws FindException;

    /**
     * Sucht nach dem HVT, der einem best. UEVT zugeordnet ist.
     *
     * @param uevtId ID des UEVTs dessen HVT gesucht wird.
     * @return Instanz von HVTStandort oder <code>null</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    HVTStandort findHVTStandort4UEVT(Long uevtId) throws FindException;

    /**
     * Ermittelt wichtige Daten zu allen HVT-Standorten und -Gruppen.
     *
     * @return Liste mit Objekten des Typs <code>HVTGruppeStdView</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    List<HVTGruppeStdView> findHVTViews() throws FindException;

    /**
     * Ermittelt wichtige Daten zu allen HVT-Standorten und -Gruppen, die den angegebenen Query-Parametern entsprechen.
     *
     * @param query Query mit den Such-Parametern.
     * @return Liste mit Objekten des Typs <code>HVTGruppeStdView</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     *
     */
    List<HVTGruppeStdView> findHVTViews(HVTQuery query) throws FindException;

    /**
     * Ermittelt eine Liste von Cluster Views zu der Liste von HVTStandorten.
     *
     * @return Liste mit Objekten des Typs <code>HVTClusterView</code>.
     */
    List<HVTClusterView> findHVTClusterViews(List<Long> hvtStandortIds) throws FindException;

    /**
     * Speichert den angegebenen HVT-Standort.
     *
     * @param toSave zu speichernder HVT-Standort
     * @return das gespeicherte HVTStandort Object
     * @throws StoreException wenn beim Speichern ein Fehler auftritt.
     */
    HVTStandort saveHVTStandort(HVTStandort toSave) throws StoreException;

    /**
     * Generiert eine neue Asb Nummer aus einer 3-stelligen (DTAG) ASB Nummer und einem unique Counter.
     *
     * @param asb
     * @throws FindException
     */
    Integer generateAsb4HVTStandort(Integer asb) throws FindException;

    /**
     * Liefert alle Equipments für den Standort und die KVZ Nummer.
     *
     * @param hvtStandortId
     * @param kvzNummer
     */
    List<Equipment> findEquipments4Kvz(Long hvtStandortId, String kvzNummer);

    /**
     * Sucht nach allen UEVTs, die einem best. HVT-Standort zugeordnet sind.
     *
     * @param hvtStandortId ID des HVT-Standorts
     * @return Liste mit Objekten des Typs <code>UEVT</code> oder <code>null</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    List<UEVT> findUEVTs4HVTStandort(Long hvtStandortId) throws FindException;

    /**
     * Sucht nach allen UEVTs. <br> Die UEVTs werden sortiert nach den HVT-IDs zurueck geliefert!
     *
     * @return Liste mit Objekten des Typs <code>UEVT</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    List<UEVT> findUEVTs() throws FindException;

    /**
     * Sucht nach einem best. UEVT ueber dessen ID.
     *
     * @param uevtId ID des gesuchten UEVTs.
     * @return UEVT
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    UEVT findUEVT(Long uevtId) throws FindException;

    /**
     * Ermittelt einen UEVT ueber die angegebenen Suchparameter.
     *
     * @param hvtIdStandort
     * @param uevt
     */
    UEVT findUEVT(Long hvtIdStandort, String uevt) throws FindException;

    /**
     * Sucht nach allen UEVT-Zielen, die fuer einen UEVT definiert werden.
     *
     * @param uevtId ID des UEVTs
     * @return Liste mit Objekten vom Typ <code>UEVT2Ziel</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    List<UEVT2Ziel> findUEVTZiele(Long uevtId) throws FindException;

    /**
     * Erzeugt bzw. aktualisiert einen UEVT und die zugeordneten Physik-Typen.
     *
     * @param toSave    zu speicherndes UEVT-Objekt
     * @param uevtZiele Liste mit den Ziel-HVTs fuer Produkte, die nicht direkt ueber den UEVT realisiert werden
     *                  koennen.
     * @throws StoreException wenn beim Speichern ein Fehler auftritt.
     */
    void saveUEVT(UEVT toSave, List<UEVT2Ziel> uevtZiele) throws StoreException;

    /**
     * Speichert den angegebenen UEVT ab.
     *
     * @param toSave zu speicherndes Objekt.
     * @return das gespeicherte Objekt
     * @throws StoreException wenn beim Speichern ein Fehler auftritt.
     */
    UEVT saveUEVT(UEVT toSave) throws StoreException;

    /**
     * Gibt eine Liste aller konfigurierten HVT-Techniken zurueck.
     *
     * @return Liste mit Objekten des Typs <code>HVTTechnik</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    List<HVTTechnik> findHVTTechniken() throws FindException;

    /**
     * Sucht nach einer best. HVT-Technik ueber deren ID.
     *
     * @param technikId ID der gesuchten Technik.
     * @return HVT-Technik zur angegebenen ID
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt
     */
    HVTTechnik findHVTTechnik(Long technikId) throws FindException;

    /**
     * Gibt alle HVT-Techniken zurueck, die einem best. HVT-Standort zugeordnet sind.
     *
     * @param hvtIdStandort ID des HVT-Standorts
     * @return Liste mit Objekten des Typs <code>HVTTechnik</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    List<HVTTechnik> findHVTTechniken4Standort(Long hvtIdStandort) throws FindException;

    /**
     * Ordnet einem best. HVT-Standort die HVT-Techniken mit den IDs <code>hvtTechnikIds</code> zu.
     *
     * @param hvtIdStandort ID des HVT-Standorts
     * @param hvtTechnikIds IDs der HVT-Techniken, die dem Standort zugeordnet werden sollen
     * @throws StoreException wenn beim Speichern ein Fehler auftritt.
     */
    void saveHVTTechniken4Standort(Long hvtIdStandort, List<Long> hvtTechnikIds) throws StoreException;

    /**
     * Funktion ermittelt anhand der HVT-StandortId die Niederlassungs-Id
     *
     * @param hvtIdStandort ID des HVT-Standorts, zu dem die Niederlassung ermittelt werden soll
     * @return ID der Niederlassung, die dem HVT-Standort zugeordnet ist.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     *
     */
    Long findNiederlassungId4HVTIdStandort(Long hvtIdStandort) throws FindException;

    /**
     * Liefert alle HVTStandort-Objekt eines bestimmten Typs.
     *
     * @param hvtTyp Gesuchter HVT-Typ
     * @return Liste mit den gesuchten HVTStandort-Objekten
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     *
     */
    List<HVTStandort> findHVTStandortByTyp(Long hvtTyp) throws FindException;

    /**
     * Liefert alle HVT-Gruppen, die einer Niederlassung zugeordent sind.
     *
     * @param niederlassungId Id der Niederlassung
     * @return Liste mit Niederlassung-Objekten
     * @throws FindException Falls ein Fehler auftrat
     *
     */
    List<HVTGruppe> findHVTGruppenForNiederlassung(Long niederlassungId) throws FindException;

    /**
     * Liefert alle HVT-Raeume fuer einen HVT-Standort.
     *
     * @param hvtIDStandort ID des betroffenen HVT-Standorts
     * @return Liste mit den gesuchten HVTRaum-Objekten
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     *
     */
    List<HVTRaum> findHVTRaeume4Standort(Long hvtIDStandort) throws FindException;

    /**
     * Liefert einen bestimmten HVT-Raum anhand der ID.
     *
     * @param id ID des gesuchten HVT-Raums
     * @return gesuchter HVT-Raum
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     *
     */
    HVTRaum findHVTRaum(Long id) throws FindException;

    /**
     * Liefert einen bestimmten HVT-Raum anhand der Raumbezeichnung.
     *
     * @param hvtStandortId ID des HVT-Standorts
     * @param name          Name des gesuchten Raums
     * @return gesuchter HVT-Raum
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     *
     */
    HVTRaum findHVTRaumByName(Long hvtStandortId, String name) throws FindException;

    /**
     * Speichert ein HVTRaum-Objekt.
     *
     * @param hvtRaum Zu speicherndes Objekt
     * @return der persistierte HVT Raum
     * @throws StoreException falls ein Fehler auftrat.
     *
     */
    HVTRaum saveHVTRaum(HVTRaum hvtRaum) throws StoreException;

    /**
     * Liefert einen HVT-Gruppe-Datensatz anhand der Bezeichnung
     *
     * @param bezeichnung Bezeichnung des Standorts
     * @return Der gesuchte HVTGruppe-Datensatz oder {@code null}, wenn keine HVT-Gruppe mit dem angegebenen Namen
     * gefunden wurde.
     * @throws FindException Falls ein Fehler auftrat; bzw. wenn mehr als eine HVT-Gruppe den angegebenen Namen
     *                       besitzen
     *
     */
    HVTGruppe findHVTGruppeByBezeichnung(String bezeichnung) throws FindException;

    /**
     * Liefert einen HVT-Standort-Datensatz anhand der Bezeichnung
     *
     * @param bezeichnung Bezeichnung des Standorts
     * @return Der gesuchte HVTStandort-Datensatz
     * @throws FindException Falls ein Fehler auftrat
     *
     */
    HVTStandort findHVTStandortByBezeichnung(String bezeichnung) throws FindException;

    /**
     * Liefert eine Liste der HVT-Standorte anhand einer Liste aus HVT Gruppen Bezeichnungen
     */
    List<HVTStandort> findHVTStandorteByBezeichnung(List<String> bezeichnungen, boolean onlyActive)
            throws FindException;

    /**
     * Liefert einen HVT-Technik-Datensatz anhand des Herstellers
     *
     * @param hersteller Herstellerbezeichnung
     * @return Der gesuchte HVT-Technik-Datensatz
     * @throws FindException Falls ein Fehler auftrat
     *
     */
    HVTTechnik findHVTTechnikByHersteller(String hersteller) throws FindException;

    List<UevtBuchtView> findUevtBuchtenForUevt(Long hvtIdStandort, String uevt) throws FindException;

    /**
     * Sucht nach allen Technologietypen zu einem Standort
     *
     * @return Liste der Technologietypen, null wenn keine Typen verfuegbar
     * @throws FindException Falls ein Fehler auftrat
     */
    List<HVTStandortTechType> findTechTypes4HVTStandort(Long hvtStandortId) throws FindException;

    /**
     * Speichert oder modifiziert einen Technologietypen zu einem Standort
     *
     * @throws StoreException Falls ein Fehler auftrat
     */
    void saveTechType(HVTStandortTechType toSave, Long sessionId) throws StoreException;

    /**
     * Loescht einen Technologietypen zu einem Standort
     *
     * @throws DeleteException Falls ein Fehler auftrat
     */
    void deleteTechType(HVTStandortTechType toDelete) throws DeleteException;

    /**
     * Laedt die Anschlussart fuer einen HVT Standort.
     *
     * @param hvtStandortId
     * @return Anschlussart, falls HVT, KVZ, FTTB, FTTH oder FTTX, null sonst
     * @throws FindException
     */
    Long findAnschlussart4HVTStandort(Long hvtStandortId) throws FindException;

    /**
     * Speichert HVTTechnik
     *
     * @param hvtTechnik
     * @throws StoreException
     */
    HVTTechnik saveHVTTechnik(HVTTechnik hvtTechnik) throws StoreException;

    /**
     * Speicher ein {@link KvzAdresse}-Objekt
     *
     * @param kvzAdresse
     * @throws StoreException
     */
    KvzAdresse saveKvzAdresse(KvzAdresse kvzAdresse) throws StoreException;

    /**
     * Laedt ein {@link KvzAdresse}-Objekt
     *
     * @param hvtStandortId - darf nicht null sein
     * @param kvzNummer     - darf nicht null sein
     * @throws FindException
     */
    KvzAdresse findKvzAdresse(Long hvtStandortId, String kvzNummer) throws FindException;

    /**
     * Laedt eine Liste an {@link KvzAdresse}-Objekten
     *
     * @param hvtStandortId - darf nicht null sein
     * @throws FindException
     */
    List<KvzAdresse> findKvzAdressen(Long hvtStandortId) throws FindException;

    /**
     * Loescht das angegebene {@link KvzAdresse}-Objekt aus der DB
     *
     * @param kvzAdresse - darf nicht null sein
     * @throws DeleteException
     */
    void deleteKvzAdresse(KvzAdresse kvzAdresse) throws DeleteException;

    /**
     * Erzeugt aus den angegebenen Parametern eine {@link KvzSperre}, die bei {@link #validateKvzSperre(Endstelle)}
     * überprüft wird.
     *
     * @param hvtStandortId {@link HVTStandort#id}
     * @param kvzNr         {@link Equipment#kvzNummer}
     * @param comment       (optional) Kommentar zur Sperre
     * @return the created {@link KvzSperre}
     * @throws FindException
     */
    KvzSperre createKvzSperre(Long hvtStandortId, String kvzNr, String comment) throws FindException;

    /**
     * Findet eine KvzSperre für den Standort und GeoId (genauer über ONKZ/ASB des HVT und KVZ-Nummer der GeoId).
     *
     * @param hvtStandortId standort ID
     * @param geoId         Geo-ID
     * @return KvzSperre oder <code>null</code> wenn keine definiert ist
     * @throws FindException
     */
    KvzSperre findKvzSperre(Long hvtStandortId, Long geoId) throws FindException;

    /**
     * Findet eine KvzSperre ueber deren Id.
     * @param kvzSperreId
     * @return
     * @throws FindException
     */
    KvzSperre findKvzSperre(Long kvzSperreId) throws FindException;

    /**
     * Prüft ob für die Endstelle (ONKZ/ASB/KVZ) eine KvzSperre existiert.
     *
     * @param endstelle die Endstelle
     * @throws FindException       bei Lade-Fehler
     * @throws ValidationException wenn eine KvzSperre existiert
     */
    void validateKvzSperre(Endstelle endstelle) throws FindException, ValidationException;

    /**
     * Löscht einen {@link KvzSperre} Datensatz.
     *
     * @param kvzSperreId {@link KvzSperre#id}
     */
    void deleteKvzSperre(Long kvzSperreId);

}
