/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.04.2010 12:01:33
 */
package de.augustakom.hurrican.model.cc.equipment;

import javax.persistence.*;

import de.augustakom.hurrican.model.cc.AbstractCCIDModel;
import de.augustakom.hurrican.model.cc.EqStatus;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.Rangierung.Freigegeben;


/**
 * Modell-Klasse zur Protokollierung von einzelnen Port-Wechseln, die bei einem Baugruppenwechsel vorkommen (koennen).
 */
@Entity
@Table(name = "T_HW_BG_CHANGE_PORT2PORT")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_HW_BG_CHANGE_PORT2PORT_0", allocationSize = 1)
public class HWBaugruppenChangePort2Port extends AbstractCCIDModel {

    private Equipment equipmentOld;
    private Equipment equipmentOldIn;
    private Equipment equipmentNew;
    private Equipment equipmentNewIn;
    private EqStatus eqStateOrigOld;
    private EqStatus eqStateOrigNew;
    private Freigegeben rangStateOrigOld;
    private Freigegeben rangStateOrigNew;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "EQ_ID_OLD", nullable = true)
    public Equipment getEquipmentOld() {
        return equipmentOld;
    }

    public void setEquipmentOld(Equipment equipmentOld) {
        this.equipmentOld = equipmentOld;
    }

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "EQ_ID_OLD_IN", nullable = true)
    public Equipment getEquipmentOldIn() {
        return equipmentOldIn;
    }

    public void setEquipmentOldIn(Equipment equipmentOldIn) {
        this.equipmentOldIn = equipmentOldIn;
    }

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "EQ_ID_NEW", nullable = true)
    public Equipment getEquipmentNew() {
        return equipmentNew;
    }

    public void setEquipmentNew(Equipment equipmentNew) {
        this.equipmentNew = equipmentNew;
    }

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "EQ_ID_NEW_IN", nullable = true)
    public Equipment getEquipmentNewIn() {
        return equipmentNewIn;
    }

    public void setEquipmentNewIn(Equipment equipmentNewIn) {
        this.equipmentNewIn = equipmentNewIn;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "EQ_STATE_ORIG_OLD")
    public EqStatus getEqStateOrigOld() {
        return eqStateOrigOld;
    }

    public void setEqStateOrigOld(EqStatus eqStateOrigOld) {
        this.eqStateOrigOld = eqStateOrigOld;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "EQ_STATE_ORIG_NEW")
    public EqStatus getEqStateOrigNew() {
        return eqStateOrigNew;
    }

    public void setEqStateOrigNew(EqStatus status) {
        this.eqStateOrigNew = status;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "RANG_FREIGABE_ORIG_OLD")
    public Freigegeben getRangStateOrigOld() {
        return rangStateOrigOld;
    }

    public void setRangStateOrigOld(Freigegeben rangStateOrigOld) {
        this.rangStateOrigOld = rangStateOrigOld;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "RANG_FREIGABE_ORIG_NEW")
    public Freigegeben getRangStateOrigNew() {
        return rangStateOrigNew;
    }

    public void setRangStateOrigNew(Freigegeben rangStateOrigNew) {
        this.rangStateOrigNew = rangStateOrigNew;
    }

}


