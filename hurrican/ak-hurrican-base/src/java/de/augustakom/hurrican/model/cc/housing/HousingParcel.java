/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.03.2010 10:09:35
 */
package de.augustakom.hurrican.model.cc.housing;

import javax.persistence.*;
import javax.validation.constraints.*;

import de.augustakom.hurrican.model.cc.AbstractCCIDModel;


/**
 * Modell zur Abbildung einer abgetrennten Parzelle innerhalb eines Housing-Gebaeudes.
 *
 *
 */
@Entity
@Table(name = "T_HOUSING_PARCEL")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_HOUSING_PARCEL_0", allocationSize = 1)
public class HousingParcel extends AbstractCCIDModel {

    private String parcel;
    private Long qm;

    @Column(name = "PARCEL")
    @NotNull
    public String getParcel() {
        return parcel;
    }

    public void setParcel(String parcel) {
        this.parcel = parcel;
    }

    @Column(name = "QM")
    @NotNull
    public Long getQm() {
        return qm;
    }

    public void setQm(Long qm) {
        this.qm = qm;
    }

}


