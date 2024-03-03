/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.08.2004 14:24:33
 */
package de.augustakom.hurrican.dao.cc;

import java.util.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.RangSchnittstelle;
import de.augustakom.hurrican.model.cc.query.EquipmentQuery;
import de.augustakom.hurrican.model.cc.view.EquipmentInOutView;
import de.augustakom.hurrican.model.cc.view.HVTBelegungView;
import de.augustakom.hurrican.model.cc.view.UevtCuDAView;


/**
 * DAO-Interface fuer Objekte des Typs <code>Equipment</code>.
 *
 *
 */
public interface EquipmentDAO extends FindDAO, StoreDAO, ByExampleDAO {

    /**
     * Ermittelt die Anzahl der belegten 2-Draht CuDAs der einzelnen HVTs bzw. UEVTs.
     *
     * @param hochbitratig Flag, ob nach hoch- (true) oder niederbitratigen (false) Stiften gesucht werden soll.
     * @return Liste mit Objekten des Typs <code>HVTBelegungView</code>.
     */
    public List<HVTBelegungView> find2DrahtBelegt(boolean hochbitratig);

    /**
     * Ermittelt die Anzahl der belegten 4-Draht CuDAs der einzelnen HVTs. bzw UEVTs
     *
     * @return Liste mit Objekten des Typs <code>HVTBelegungView</code>.
     */
    public List<HVTBelegungView> find4DrahtBelegt();

    /**
     * Ermittelt eine Uebersicht ueber die Stift-Anzahl der einzelnen UEVTs.
     *
     * @return Liste mit Objekten des Typs <code>UevtCuDAView</code>.
     */
    public List<UevtCuDAView> createUevtCuDAViews();

    /**
     * Ermittelt die Anzahl der Equipments, die den Query-Parametern entsprechen.
     *
     * @param query Query-Objekt mit den Such-Parametern.
     * @return Anzahl der Equipments, die mit den Query-Parametern uebereinstimmen.
     */
    public Integer getEquipmentCount(EquipmentQuery query);

    /**
     * Sucht nach allen Equipments, die den Suchparametern entsprechen.
     *
     * @param hvtIdStd      ID des HVT-Standorts
     * @param rangVerteiler Bezeichnung des Verteilers (=UEVT)
     * @param leiste1       Bezeichnung der Leiste
     * @return Liste mit Objekten des Typs <code>Equipment</code>.
     */
    public List<Equipment> findEquipments(Long hvtIdStd, String rangVerteiler, String leiste1);

    /**
     * Ermittelt die Equipments mit den angegebenen Daten.
     *
     * @param rackId     ID des DSLAM-Racks.
     * @param hwEQN      eindeutiger Hardware Port.
     * @param rangSSType Schnittstellen-Typ
     * @return Objekt vom Typ <code>DSLAM</code>
     *
     */
    public List<Equipment> findEquipment(Long rackId, String hwEQN, String rangSSType);

    /**
     * Ermittelt Equipments an einem Standort, die den gegebenen Physiktypen zur Verfuegung stellen koennen.
     *
     * @param hvtStandortId Der Standort, an der ein Equipment benoetigt wird
     * @param physiktypId   Der Physiktyp, der benoetigt wird.
     * @return Liste von Equipments, moeglicherweise leer, nie {@code null}
     */
    public List<Equipment> findEquipmentsForPhysiktyp(Long hvtStandortId, Long physiktypId);

    /**
     * Ermittelt die Equipments mit den angegebenen Daten.
     *
     * @param hwBaugruppenId ID der Baugruppe.
     * @param hwEQN          eindeutiger Hardware Port.
     * @param rangSSType     Schnittstellen-Typ
     * @return Objekt vom Typ <code>DSLAM</code>
     *
     */
    public Equipment findEquipmentByBaugruppe(Long hwBaugruppenId, String hwEQN, String rangSSType);

    /**
     * Ermittelt alle Equipments zu einer Baugruppe
     *
     * @param hwBaugruppenId ID der Baugruppe zu der alle Equipments gesucht werden sollen.
     * @return Liste mit gefundenen Equipments, oder leere Liste
     */
    public List<Equipment> findEquipmentsByBaugruppe(Long hwBaugruppenId);

    /**
     * Ermittelt alle Equipments, die dem Example-Objekt 'example' entsprechen.
     *
     * @param example     Example-Objekt mit den gesuchten Parametern
     * @param orderParams Angabe der Equipment-Parameter, ueber die sortiert werden soll.
     * @return Liste mit Objekten des Typs <code>Equipment</code>
     *
     */
    public List<Equipment> findEquipments(Equipment example, String[] orderParams);

    /**
     * Findet zu einem In- das passende Out-Equipment und umgekehrt.
     *
     * @param eq Das Equipment, zu dem das passende In-/Out-Equipment gesucht werden soll
     * @return Ein Equipment, oder {@code null}, falls keines existiert
     */
    public Equipment findCorrespondingEquipment(Equipment eq);

    /**
     * Ermittelt je zwei Equipments, die den Suchparametern in 'example' entsprechen. <br> Die beiden Equipments muessen
     * dabei die identische HW_EQN besitzen. Als HW_SCHNITTSTELLE wird einmal jedoch xxx-IN und einmal xxx-OUT
     * erwartet.
     *
     * @param example Example-Objekt mit den gesuchten Parametern
     * @return Liste mit Objekten des Typs <code>EquipmentInOutView</code>
     *
     */
    public List<EquipmentInOutView> findEqInOutViews(Equipment example);

    /**
     * Ermittelt die Anzahl und den Physik-Typ einer best. UEVT-Leiste.
     *
     * @param hvtIdStd      ID des HVT-Standorts
     * @param rangVerteiler Bezeichnung des Verteilers (=UEVT)
     * @param leiste1       Bezeichnung der Leiste
     * @return Map mit der Anzahl Stifte. Als Key wird der Stift-Typ (z.B. 'N' oder 'H') verwendet, der zughoerige Value
     * enthaelt die Anzahl.
     */
    public Map<String, Integer> getEquipmentCount(Long hvtIdStd, String rangVerteiler, String leiste1);

    /**
     * Loescht den Datensatz von ProduktEQConfig mit der angegebenen ID.
     *
     * @param id ID des zu loeschenden Datensatzes.
     *
     */
    public void deleteProduktEQConfig(Long id);

    /**
     * Liefert alle verfuegbaren Rang_Schnittstellen-Werte fuer einen bestimmten HVT
     *
     * @param hvtStandortId Id des HVT-Standorts
     * @return Moegliche Rang_Schnittstellen fuer einen HVT-Standort
     *
     */
    public List<RangSchnittstelle> findAvailableSchnittstellen4HVT(Long hvtStandortId);

    /**
     * Liefert alle Equipments für den Standort und die KVZ Nummer.
     */
    public List<Equipment> findEquipments4Kvz(Long hvtStandortId, String kvzNummer);

}
