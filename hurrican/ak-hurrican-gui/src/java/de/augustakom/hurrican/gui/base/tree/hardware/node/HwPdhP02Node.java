/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.05.2010 11:41:56
 */
package de.augustakom.hurrican.gui.base.tree.hardware.node;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.hurrican.gui.base.tree.DynamicTree;
import de.augustakom.hurrican.model.cc.AbstractCCIDModel;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.service.cc.utils.CCServiceFinder;


/**
 * Node fuer die Darstellung einer sog. 'P02' Leiste fuer PDH Ports. Der Node ermittelt als Child-Elemente alle Ports
 * eines Standorts, die folgenden Kriterien entsprechen: HW_SCHNITTSTELLE = PDH-OUT RANG_LEISTE_1 = P02
 */
public class HwPdhP02Node extends EquipmentNodeContainer {
    private static final Logger LOGGER = Logger.getLogger(HwPdhP02Node.class);

    private final HVTStandort hvtStandort;

    public HwPdhP02Node(DynamicTree tree, HVTStandort hvtStandort) {
        super(tree, true);
        this.hvtStandort = hvtStandort;
    }

    @Override
    public Collection<Equipment> getEquipments() {
        try {
            RangierungsService rangierungsService = CCServiceFinder.instance().getCCService(RangierungsService.class);

            Equipment example = new Equipment();
            example.setHvtIdStandort(hvtStandort.getId());
            example.setHwSchnittstelle(Equipment.HW_SCHNITTSTELLE_PDH_OUT);
            example.setRangLeiste1(Equipment.RANG_LEISTE_P02);

            List<Equipment> equipments = rangierungsService.findEquipments(example, AbstractCCIDModel.ID);

            return equipments;
        }
        catch (Exception ex) {
            LOGGER.error("Konnte Equipments nicht laden", ex);
        }
        return null;
    }

    @Override
    public String getDisplayName() {
        return "P02";
    }

    @Override
    public String getIcon() {
        return IMAGE_BASE + "dslam.gif";
    }
}


