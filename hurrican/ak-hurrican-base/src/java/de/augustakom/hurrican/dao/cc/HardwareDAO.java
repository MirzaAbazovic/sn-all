/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.12.2008 16:40:51
 */
package de.augustakom.hurrican.dao.cc;

import java.util.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp;
import de.augustakom.hurrican.model.cc.hardware.HWDluView;
import de.augustakom.hurrican.model.cc.hardware.HWDslam;
import de.augustakom.hurrican.model.cc.hardware.HWOlt;
import de.augustakom.hurrican.model.cc.hardware.HWOltChild;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.model.shared.view.SwitchMigrationSearchCriteria;
import de.augustakom.hurrican.model.shared.view.SwitchMigrationView;

/**
 * Interface zur Definition der DAO-Methoden, um Hardware-Objekte zu verwalten.
 *
 *
 */
public interface HardwareDAO extends FindDAO, FindAllDAO, StoreDAO, ByExampleDAO {

    /**
     * Sucht nach allen Racks, die einem best. HVT zugeordnet sind. <br> (Es werden nur Racks ermittelt, die ein
     * Gueltig-Bis Datum '01.01.2200' besitzen.)
     *
     * @param hvtIdStandort Standort ID des HVT-Standorts
     * @return Liste mit HWRack-Objekten oder <code>null</code>
     *
     */
    List<HWRack> findRacks(Long hvtIdStandort);

    /**
     * Sucht nach allen Racks, die einem best. HVT zugeordnet und eins bestimmten Typs sind. <br> (Es werden nur Racks
     * ermittelt, die ein Gueltig-Bis Datum '01.01.2200' besitzen.)
     *
     * @param hvtIdStandort Standort ID des HVT-Standorts
     * @param typ           Klasse des Hardware-Typs
     * @param onlyActive    Falls nur aktive Komponenten gesucht werden
     * @return Liste mit HWRack-Objekten oder <code>null</code>
     *
     */
    <T extends HWRack> List<T> findRacks(Long hvtIdStandort, Class<T> typ, boolean onlyActive);

    <T extends HWRack> List<?> findRacksWithBaugruppenTyp(Long hvtIdStandort, Class<T> typ, Long hwBgTypId,
            boolean onlyActive);

    /**
     * Ermittelt alle Baugruppen-Typen mit einem bestimmten Prefix-Namen (Wildcard wird angehaengt).
     *
     * @param prefix     zu verwendender Prefix fuer den BaugruppenTyp-Namen
     * @param onlyActive Flag, ob nur aktive Baugruppen-Typen ermittelt werden sollen
     * @return Liste mit Objekten des Typs <code>HWBaugruppenTyp</code>
     *
     */
    List<HWBaugruppenTyp> findBaugruppenTypen(String prefix, boolean onlyActive);

    /**
     * Ermittelt eine Liste mit den EWSD-Baugruppen eines bestimmten HVTs. <br> Es wird zusaetzlich ueberprueft, ob die
     * Equipments der Baugruppe auch wirklich keiner Rangierung zugeordnet sind.
     *
     * @param hvtIdStandort ID des HVT-Standorts.
     * @param onlyFree      Flag, ob nur freie (true) oder alle (false) Baugruppen ermittelt werden sollen
     * @return Liste mit Objekten des Typs <code>HWDluView</code>, die je eine Baugruppe darstellen.
     *
     */
    List<HWDluView> findEWSDBaugruppen(Long hvtIdStandort, boolean onlyFree);

    /**
     * Ermittelt einen DSLAM ueber dessen IP-Adresse.
     *
     * @param ipAddress IP-Adresse des gesuchten DSLAMs
     * @return Objekt vom Typ <code>HWDslam</code>
     *
     */
    HWDslam findHWDslamByIP(String ipAddress);

    /**
     * Erstellt eine Liste der zu migrierenden Aufträge zu dem gegebenen Standort Dabei sucht das SQL Statement nach
     * allen Aufträgen, die mindestens ein Equipment besitzen, dass als Switch {@code switchName} konfiguriert hat.
     * Dabei ist es unerheblich, ob das Equipment an EQ_IN oder EQ_OUT an Rangierung1 oder Rangierung2 hängt.
     */
    List<SwitchMigrationView> createSwitchMigrationViews(SwitchMigrationSearchCriteria searchCriteria);

    /**
     *  Sucht nach allen Racks mit der angegebenen Bezeichnung, die ein gültiges Gueltig-Von und Gueltig-Bis Datum haben.
     *  (Gueltig-Von <= aktuelles Datum, Gueltig-Bis > aktuelles Datum)
     *
     * @param bezeichnung
     * @return aktives HWRack-Objekt oder <code>null</code>
     */
    HWRack findActiveRackByBezeichnung(String bezeichnung);

    /**
     * Findet die {@link HWOltChild}-Objekte anhand ihrer Olt
     *
     * @param oltId
     * @return die gefundenen {@link HWOltChild}-Objekte oder eine leere Liste
     */
    <T extends HWOltChild> List<T> findHWOltChildByOlt(Long oltId, Class<T> clazz);

    /**
     * Findet {@link HWOltChild}-Objekte anhand der Seriennummer *
     *
     * @param serialNo
     * @return Liste mit Objekten des Typs <code>HWOnt</code>
     */
    <T extends HWOltChild> List<T> findHWOltChildBySerialNo(String serialNo,  Class<T> clazz);

    /**
     * Sucht den Racktypen fuer EQ_IN
     *
     * @return null wenn kein Rack verfuegbar
     */
    String findRackType4EqIn(Long rangierungId);

    /**
     * GSLAMs werden hier nicht betrachtet
     *
     * @param hwOltChild das Rack das am OLT hängt
     * @return die gefundene Olt oder <code>null</code>
     */
    HWOlt findHwOltForRack(final HWRack hwOltChild);

    /**
     *
     * @param rangierungId ID der Rangierung
     * @return Das HWRack auf dem sich der EqIn befindet
     */
    HWRack findRack4EqInOfRangierung(Long rangierungId);
}
