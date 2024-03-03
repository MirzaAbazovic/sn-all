/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.04.2010 08:39:01
 */
package de.augustakom.hurrican.model.cc.equipment;

import javax.persistence.*;

import de.augustakom.hurrican.model.cc.AbstractCCIDModel;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.model.cc.hardware.HWSubrack;


/**
 * Modell-Klasse fuer die Definition von Baugruppen-Aenderungen vom Typ 'DSLAM Split'.
 *
 *
 */
@Entity
@Table(name = "T_HW_BG_CHANGE_DSLAM_SPLIT")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_HW_BG_CHANGE_DSLAM_SPLIT_0", allocationSize = 1)
public class HWBaugruppenChangeDslamSplit extends AbstractCCIDModel {

    public static final String HW_RACK_OLD = "hwRackOld";
    private HWRack hwRackOld;

    public static final String HW_SUBRACK_OLD = "hwSubrackOld";
    private HWSubrack hwSubrackOld;

    public static final String HW_RACK_NEW = "hwRackNew";
    private HWRack hwRackNew;

    public static final String HW_SUBRACK_NEW = "hwSubrackNew";
    private HWSubrack hwSubrackNew;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "HW_RACK_ID_OLD", nullable = false)
    public HWRack getHwRackOld() {
        return hwRackOld;
    }

    public void setHwRackOld(HWRack hwRackOld) {
        this.hwRackOld = hwRackOld;
    }

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "HW_SUBRACK_ID_OLD", nullable = false)
    public HWSubrack getHwSubrackOld() {
        return hwSubrackOld;
    }

    public void setHwSubrackOld(HWSubrack hwSubrackOld) {
        this.hwSubrackOld = hwSubrackOld;
    }

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "HW_RACK_ID_NEW", nullable = false)
    public HWRack getHwRackNew() {
        return hwRackNew;
    }

    public void setHwRackNew(HWRack hwRackNew) {
        this.hwRackNew = hwRackNew;
    }

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "HW_SUBRACK_ID_NEW", nullable = false)
    public HWSubrack getHwSubrackNew() {
        return hwSubrackNew;
    }

    public void setHwSubrackNew(HWSubrack hwSubrackNew) {
        this.hwSubrackNew = hwSubrackNew;
    }

}


