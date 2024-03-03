/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.06.2011 09:39:30
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;
import javax.persistence.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import de.augustakom.hurrican.model.cc.hardware.HWSwitch;

/**
 * Modell fuer Endgeraete-Typen.
 */
@Entity
@Table(name = "T_EG_TYPE")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_EG_TYPE_0", allocationSize = 1)
public class EGType extends AbstractCCUserModel {

    private String hersteller = null;
    private String modell = null;
    private List<HWSwitch> orderedCertifiedSwitches;

    @Column(name = "HERSTELLER")
    public String getHersteller() {
        return hersteller;
    }

    public void setHersteller(String hersteller) {
        this.hersteller = hersteller;
    }

    @Column(name = "MODELL")
    public String getModell() {
        return modell;
    }

    public void setModell(String modell) {
        this.modell = modell;
    }

    /**
     * Returns a ordered List of all certified {@link HWSwitch} of the current EG-Type. The first entry in the list,
     * will be the highest prioritized.
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "T_EG_TYPE_2_HWSWITCH",
            joinColumns = @JoinColumn(name = "EG_TYPE_ID"),
            inverseJoinColumns = @JoinColumn(name = "HW_SWITCH_ID")
    )
    @OrderColumn(name = "PRIORITY")
    @Fetch(value = FetchMode.SUBSELECT)
    public List<HWSwitch> getOrderedCertifiedSwitches() {
        return orderedCertifiedSwitches;
    }

    public void setOrderedCertifiedSwitches(List<HWSwitch> orderedCertifiedSwitches) {
        this.orderedCertifiedSwitches = orderedCertifiedSwitches;
    }
}
