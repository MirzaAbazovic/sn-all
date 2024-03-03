/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.04.2012 14:12:50
 */
package de.augustakom.hurrican.model.cc.fttx;

import javax.persistence.*;
import javax.validation.constraints.*;

import de.augustakom.hurrican.model.cc.AbstractCCIDModel;

/**
 * Modelklasse zur Abbildung eines C-Vlan
 */
@Entity
@Table(name = "T_FTTX_CVLAN", uniqueConstraints = { @UniqueConstraint(columnNames = { "TYP", "EKP_FRAME_CONTRACT_ID" }) })
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_FTTX_CVLAN_0", allocationSize = 1)
public class CVlan extends AbstractCCIDModel {

    public static enum CVlanProtocoll {
        PPPoE,
        IPoE
    }

    private CvlanServiceTyp typ;
    private Integer value;
    private Integer pbitLimit;
    private CVlanProtocoll protocoll;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "TYP")
    public CvlanServiceTyp getTyp() {
        return typ;
    }

    public void setTyp(CvlanServiceTyp typ) {
        this.typ = typ;
    }

    @Column(name = "VLAN_VALUE")
    @NotNull
    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    /**
     * @return Pbit-Limitierung in kbit/s (NICHT Pbit)
     */
    @Column(name = "PBIT_LIMIT")
    public Integer getPbitLimit() {
        return pbitLimit;
    }

    public void setPbitLimit(Integer pbitLimit) {
        this.pbitLimit = pbitLimit;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "PROTOCOLL")
    @NotNull
    public CVlanProtocoll getProtocoll() {
        return protocoll;
    }

    public void setProtocoll(CVlanProtocoll protocoll) {
        this.protocoll = protocoll;
    }

}


