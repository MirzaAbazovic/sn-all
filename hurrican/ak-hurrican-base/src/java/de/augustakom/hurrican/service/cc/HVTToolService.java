/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.03.2005 14:18:40
 */
package de.augustakom.hurrican.service.cc;

import java.util.*;

import de.augustakom.common.service.iface.IServiceCallback;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.HVTBestellHistory;
import de.augustakom.hurrican.model.cc.HVTBestellung;
import de.augustakom.hurrican.model.cc.RangSchnittstelle;
import de.augustakom.hurrican.model.cc.view.EquipmentBelegungView;
import de.augustakom.hurrican.model.cc.view.HVTBestellungView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;


/**
 * Service-Interface zur Verwaltung und Ueberwachung von HVTs.
 *
 *
 */
public interface HVTToolService extends ICCService {

    /**
     * Wert fuer Callback-Aktion, um eine Bestaetigung anzufordern. Callback-Handler muss in der doCallback-Methode ein
     * Objekt vom Typ <code>Boolean</code> zurueck liefern!
     */
    int CALLBACK_CONFIRM = 1;

    /**
     * Key fuer Callback Parameter-Map, um die Anzahl der einzuspielenden Stifte zu uebergeben.
     */
    String CALLBACK_PARAM_ANZAHL_STIFTE = "anzahl.stifte";
    /**
     * Key fuer Callback Parameter-Map, um die Anzahl der noch verfuegbaren Stifte der Leiste zu uebergeben.
     */
    String CALLBACK_PARAM_ANZAHL_OFFEN = "anzahl.offen";
    /**
     * Key fuer Callback Parameter-Map, um die Bezeichnung der Leiste zu uebergeben, auf die die Stifte eingespielt
     * werde.
     */
    String CALLBACK_PARAM_LEISTE = "leiste";

    /**
     * Speichert die angegebene HVT-Bestellung.
     *
     * @param toSave zu speicherndes Objekt
     * @throws StoreException      wenn beim Speichern ein Fehler auftritt.
     * @throws ValidationException wenn ungueltige Daten entdeckt wurden.
     */
    void saveHVTBestellung(HVTBestellung toSave) throws StoreException, ValidationException;

    /**
     * Sucht nach einer best. HVT-Bestellung ueber die ID.
     *
     * @param id ID der gesuchten HVT-Bestellung.
     * @return Instanz von <code>HVTBestellung</code> oder <code>null</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    HVTBestellung findHVTBestellung(Long id) throws FindException;

    /**
     * Sucht nach HVT-Bestellungen zu einem best. UEVT.
     *
     * @param uevtId ID des UEVTs, dessen HVT-Bestellungen ermittelt werden sollen.
     * @return Liste mit Objekten des Typs <code>HVTBestellung</code>.
     * @throws FindException wenn bei der Ermittlung der Daten ein Fehler auftritt.
     */
    List<HVTBestellung> findHVTBestellungen(Long uevtId) throws FindException;

    /**
     * Ermittelt eine Liste mit Views ueber alle HVTs mit zugehoerigen (offenen!) HVT-Bestellungen.
     *
     * @return Liste mit Objekten des Typs <code>HVTBestellungView</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    List<HVTBestellungView> findHVTBestellungViews() throws FindException;

    /**
     * Sucht nach allen History-Eintraegen zu einer best. HVT-Bestellung. <br> (In der History sind die einzelnen
     * Stift-Vergaben einer HVT-Bestellung protokolliert.)
     *
     * @param hvtBestellId ID der HVT-Bestellung
     * @return Liste mit Objekten des Typs <code>HVTBestellHistory</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    List<HVTBestellHistory> findHVTBestellHistories(Long hvtBestellId) throws FindException;

    /**
     * Ermittelt die Equipment-Belegung eines best. UEVTs.
     *
     * @param uevtId ID des UEVTs
     * @return Liste mit Objekten des Typs <code>EquipmentBelegungView</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    List<EquipmentBelegungView> findEquipmentBelegung(Long uevtId) throws FindException;

    /**
     * Fuellt eine best. Leiste mit Stiften aus der HVT-Bestellung mit der ID <code>hvtBestellungId</code> auf. Der
     * zugehoerige UEVT wird ueber die HVT-Bestellung ermittelt.
     *
     * @param hvtBestellungId ID der HVT-Bestellung, von der die Stifte eingespielt werden sollen.
     * @param leiste1         Leiste, auf die die Stifte verteilt werden sollen.
     * @param kvzNummer       Angabe der KVZ-Nummer fuer den Port
     * @param sessionId       ID der User-Session
     * @param uevtClusterNo   ÜVt-Cluster-Nr, die den Dtag-Ports zugeordnet wird (Pflichtfeld).
     * @param serviceCallback Angabe eines Callback-Handlers. Der Aktions-Key sowie die Keys der Parameter-Map sind in
     *                        diesem Interface als Konstante definiert. <br> Key: CALLBACK_CONFIRM Params:
     *                        CALLBACK_PARAM_ANZAHL_STIFTE, CALLBACK_PARAM_ANZAHL_OFFEN, CALLBACK_PARAM_LEISTE
     * @param createLeiste    Flag definiert, ob eine neue Leiste angelegt oder eine bestehende Leiste verwendet werden
     *                        soll.
     * @param createStifte    Anzahl der Stifte, die bei Anlage einer neuen Leiste angelegt werden sollen.
     * @return Liste mit den angelegten Equipments.
     * @throws StoreException wenn bei der Stiftvergabe ein Fehler auftritt.
     */
    List<Equipment> fillUevt(Long hvtBestellungId, String leiste1, String kvzNummer,
            Integer uevtClusterNo, IServiceCallback serviceCallback, boolean createLeiste, int createStifte,
            Long sessionId) throws StoreException;

    /**
     * Sucht nach allen Equipments (=Stiften), die auf einer best. Leiste eines UEVTs definiert sind.
     *
     * @param hvtIdStd ID des zug. HVT-Standorts
     * @param uevt     Bezeichnung des UEVTs
     * @param leiste1  Bezeichnung der Leiste, deren Equipments ermittelt werden sollen.
     * @return Liste mit Objekten des Typs <code>Equipment</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    List<Equipment> findEquipments(Long hvtIdStd, String uevt, String leiste1) throws FindException;

    /**
     * Liefert alle verfuegbaren Rang_Schnittstellen-Werte fuer einen bestimmten HVT
     *
     * @param hvtStandortId Id des HVT-Standorts
     * @return Moegliche Rang_Schnittstellen fuer einen HVT-Standort
     * @throws FindException Falls ein Fehler auftrat
     *
     */
    List<RangSchnittstelle> findAvailableSchnittstellen4HVT(Long hvtStandortId) throws FindException;

    /**
     * Funktion für die Produktionsfreigabe einer MDU Im Anschluss an die initiale Provisionierung der MDU per CPS muss
     * Command über die Produktionsfreigabe der MDU informiert werden.
     *
     * @param mdu   Bezeichnung der MDU
     * @param datum Zeitstempel (Produktionsfreigabe)
     * @throws Exception Falls ein Fehler auftrat
     */
    void activateMDU(String mdu, Date datum) throws Exception;

    /**
     * Login Command-System
     *
     * @return sessionId SessionId des Command-Systems
     * @throws Exception
     */
    String loginCommand() throws Exception;

    /**
     * Logout Command-System
     *
     * @param sessionId SessionId des Command-Systems
     * @throws Exception
     */
    void logoutCommand(String sessionId) throws Exception;
}
