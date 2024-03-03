/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.02.2010 15:45:25
 */
package de.augustakom.hurrican.model.cc.view;

import de.augustakom.hurrican.model.cc.AbstractCCModel;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp;
import de.augustakom.hurrican.model.cc.hardware.HWRack;


/**
 * View-Modell mit Equipment- sowie den verbundenen Hardware-Daten.
 *
 *
 */
public class HardwareEquipmentView extends AbstractCCModel {

    private static final String EQUIPMENT_PREFIX = "equipment.";
    public static final String EQ_HW_EQN = EQUIPMENT_PREFIX + Equipment.HW_EQN;
    public static final String EQ_STATUS = EQUIPMENT_PREFIX + Equipment.STATUS;
    public static final String EQ_RANG_BUCHT = EQUIPMENT_PREFIX + Equipment.RANG_BUCHT;
    public static final String EQ_RANG_LEISTE1 = EQUIPMENT_PREFIX + Equipment.RANG_LEISTE1;
    public static final String EQ_RANG_STIFT1 = EQUIPMENT_PREFIX + Equipment.RANG_STIFT1;
    public static final String EQ_RANG_SS_TYPE = EQUIPMENT_PREFIX + Equipment.RANG_SS_TYPE;
    public static final String EQ_RANG_SCHNITTSTELLE = EQUIPMENT_PREFIX + Equipment.RANG_SCHNITTSTELLE;
    public static final String EQ_UETV = EQUIPMENT_PREFIX + Equipment.UETV;
    public static final String EQ_VERWENDUNG = EQUIPMENT_PREFIX + Equipment.VERWENDUNG;
    public static final String EQ_KVZ_NR = EQUIPMENT_PREFIX + Equipment.KVZ_NUMMER;
    public static final String BG_TYP = "hwBaugruppenTyp." + HWBaugruppenTyp.NAME;
    public static final String BG_BEMERKUNG = "hwBaugruppe." + HWBaugruppe.BEMERKUNG;
    public static final String RACK_GERAETEBEZ = "hwRack." + HWRack.GERAETE_BEZ;

    private Equipment equipment;
    private HWBaugruppe hwBaugruppe;
    private HWBaugruppenTyp hwBaugruppenTyp;
    private HWRack hwRack;

    public Long getId() {
        return (getEquipment() != null) ? getEquipment().getId() : null;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    public HWBaugruppe getHwBaugruppe() {
        return hwBaugruppe;
    }

    public void setHwBaugruppe(HWBaugruppe hwBaugruppe) {
        this.hwBaugruppe = hwBaugruppe;
    }

    public HWBaugruppenTyp getHwBaugruppenTyp() {
        return hwBaugruppenTyp;
    }

    public void setHwBaugruppenTyp(HWBaugruppenTyp hwBaugruppenTyp) {
        this.hwBaugruppenTyp = hwBaugruppenTyp;
    }

    public HWRack getHwRack() {
        return hwRack;
    }

    public void setHwRack(HWRack hwRack) {
        this.hwRack = hwRack;
    }

}


