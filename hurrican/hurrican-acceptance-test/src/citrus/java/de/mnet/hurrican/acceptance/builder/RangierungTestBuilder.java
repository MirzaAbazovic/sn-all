/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.11.2014
 */
package de.mnet.hurrican.acceptance.builder;

import javax.inject.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.HWBaugruppeBuilder;
import de.augustakom.hurrican.model.cc.PhysikTyp;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.RangierungBuilder;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.RangierungsService;

/**
 *
 */
@Component
@Scope("prototype")
public class RangierungTestBuilder {

    @Autowired
    protected Provider<HWBaugruppeBuilder> baugruppeBuilderProvider;
    @Autowired
    protected Provider<EquipmentBuilder> equipmentBuilderProvider;
    @Autowired
    protected Provider<RangierungBuilder> rangierungBuilderProvider;
    @Autowired
    protected HWService hwService;
    @Autowired
    protected RangierungsService rangierungsService;

    public <T extends HWRack> Equipment buildRangierung(HVTStandort hvtStandort, T rack) throws FindException, StoreException,
            ValidationException {
        HWBaugruppe baugruppe = baugruppeBuilderProvider.get().build();
        baugruppe.setRackId(rack.getId());
        HWBaugruppenTyp baugruppenTyp = hwService.findBaugruppenTypByName("VDGE");
        baugruppe.setHwBaugruppenTyp(baugruppenTyp);
        baugruppe = hwService.saveHWBaugruppe(baugruppe);

        Equipment equipment = equipmentBuilderProvider.get().build();
        equipment.setHvtIdStandort(hvtStandort.getId());
        equipment.setHwBaugruppenId(baugruppe.getId());
        equipment.setHwSchnittstelle(Equipment.HW_SCHNITTSTELLE_VDSL2);
        equipment = rangierungsService.saveEquipment(equipment);

        Rangierung rangierung = rangierungBuilderProvider.get().build();
        rangierung.setEqInId(equipment.getId());
        rangierung.setHvtIdStandort(hvtStandort.getId());
        rangierung.setPhysikTypId(PhysikTyp.PHYSIKTYP_FTTB_VDSL);
        rangierungsService.saveRangierung(rangierung, false);
        return equipment;
    }
}
