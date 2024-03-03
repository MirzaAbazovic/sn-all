/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.04.2010 08:30:50
 */
package de.augustakom.hurrican.model.cc.equipment;

import javax.persistence.*;
import javax.validation.constraints.*;

import de.augustakom.hurrican.model.cc.AbstractCCIDModel;
import de.augustakom.hurrican.model.cc.Equipment;


/**
 * Modell-Klasse definiert ein Mapping zwischen einem (alten) HW_EQN und dem neuen zugehoerigen V5 Port.
 */
@Entity
@Table(name = "T_HW_BG_CHANGE_DLU_V5")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_HW_BG_CHANGE_DLU_V5_0", allocationSize = 1)
public class HWBaugruppenChangeDluV5 extends AbstractCCIDModel {

    public static final String HW_BG_CHANGE_DLU_ID = "hwBgChangeDluId";
    private Long hwBgChangeDluId;
    private Equipment dluEquipment;
    private String hwEqn;
    private String v5Port;
    private String userW;

    @Column(name = "HW_BG_CHANGE_DLU_ID")
    @NotNull
    public Long getHwBgChangeDluId() {
        return hwBgChangeDluId;
    }

    public void setHwBgChangeDluId(Long hwBgChangeDluId) {
        this.hwBgChangeDluId = hwBgChangeDluId;
    }

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "EQUIPMENT_ID", nullable = false)
    public Equipment getDluEquipment() {
        return dluEquipment;
    }

    public void setDluEquipment(Equipment dluEquipment) {
        this.dluEquipment = dluEquipment;
    }

    @Column(name = "HW_EQN")
    @NotNull
    public String getHwEqn() {
        return hwEqn;
    }

    public void setHwEqn(String hwEqn) {
        this.hwEqn = hwEqn;
    }

    @Column(name = "V5_PORT")
    @NotNull
    public String getV5Port() {
        return v5Port;
    }

    public void setV5Port(String v5Port) {
        this.v5Port = v5Port;
    }

    @Column(name = "USERW")
    public String getUserW() {
        return userW;
    }

    public void setUserW(String userW) {
        this.userW = userW;
    }

}


