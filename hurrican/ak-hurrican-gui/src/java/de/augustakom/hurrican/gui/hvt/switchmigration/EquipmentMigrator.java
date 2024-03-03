/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.10.2011 12:21:23
 */
package de.augustakom.hurrican.gui.hvt.switchmigration;

import java.util.*;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.hardware.HWSwitch;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.service.cc.utils.CCServiceFinder;

/**
 * Migriert die Switchkennung von {@link Equipment}s.
 *
 *
 * @since Release 10
 */
class EquipmentMigrator {

    private RangierungsService rangierungsService;
    private HVTService hvtService;

    /**
     * @return Returns the rangierungsService.
     * @throws ServiceNotFoundException
     */
    protected RangierungsService getRangierungsService() throws ServiceNotFoundException {
        if (rangierungsService == null) {
            rangierungsService = CCServiceFinder.instance().getCCService(RangierungsService.class);
        }
        return rangierungsService;
    }

    /**
     * @return Returns the hvtService.
     * @throws ServiceNotFoundException
     */
    protected HVTService getHvtService() throws ServiceNotFoundException {
        if (hvtService == null) {
            hvtService = CCServiceFinder.instance().getCCService(HVTService.class);
        }
        return hvtService;
    }

    protected List<HVTStandort> findActiveHVTStandorte(List<String> technicalLocations) throws FindException,
            ServiceNotFoundException {
        return getHvtService().findHVTStandorteByBezeichnung(technicalLocations, true);
    }

    /**
     * findet fuer die Liste an gegebenen HVT-Standorten, die jeweils den gegebenen {@link HWSwitch} referenzieren, eine
     * Liste an {@link Equipment}s.
     *
     * @param sourceHwSwitch
     * @param technicalLocations
     * @return
     * @throws ServiceNotFoundException
     * @throws FindException
     */
    List<Equipment> find(HWSwitch sourceHwSwitch, List<String> technicalLocations)
            throws ServiceNotFoundException, FindException {

        if (CollectionTools.isEmpty(technicalLocations)) {
            return Collections.emptyList();
        }
        List<HVTStandort> hvtStandorte = findActiveHVTStandorte(technicalLocations);

        List<Equipment> equipments = new ArrayList<>();
        Equipment example = new Equipment();
        example.setHwSwitch(sourceHwSwitch);

        for (HVTStandort hvtStandort : hvtStandorte) {
            example.setHvtIdStandort(hvtStandort.getHvtIdStandort());
            List<Equipment> equipments4HVTStandort = getRangierungsService().findEquipments(example);
            if (CollectionTools.isNotEmpty(equipments4HVTStandort)) {
                equipments.addAll(equipments4HVTStandort);
            }
        }
        return equipments;
    }

    /**
     * aendert fuer die Liste an {@link Equipment}s den referenzierten {@link HWSwitch}.
     *
     * @param destinationHwSwitch
     * @param equipments
     * @throws ServiceNotFoundException
     * @throws StoreException
     */
    void migrate(HWSwitch destinationHwSwitch, List<Equipment> equipments) throws ServiceNotFoundException,
            StoreException {
        if (CollectionTools.isNotEmpty(equipments)) {
            for (Equipment equipment : equipments) {
                equipment.setHwSwitch(destinationHwSwitch);
            }
            getRangierungsService().saveEquipments(equipments);
        }
    }

}
