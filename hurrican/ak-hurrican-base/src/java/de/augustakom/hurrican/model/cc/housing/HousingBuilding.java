/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.03.2010 10:07:35
 */
package de.augustakom.hurrican.model.cc.housing;

import java.util.*;
import javax.persistence.*;
import javax.validation.constraints.*;

import de.augustakom.hurrican.model.cc.AbstractCCIDModel;
import de.augustakom.hurrican.model.cc.CCAddress;


/**
 * Modell fuer die Abbildung eines Housing-Gebaeudes.
 *
 *
 */
@Entity
@Table(name = "T_HOUSING_BUILDING")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_HOUSING_BUILDING_0", allocationSize = 1)
public class HousingBuilding extends AbstractCCIDModel {

    private String building;
    private CCAddress address;
    private Set<HousingFloor> floors = new HashSet<HousingFloor>();

    @Column(name = "BUILDING")
    @NotNull
    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "ADDRESS_ID", nullable = false)
    public CCAddress getAddress() {
        return address;
    }

    public void setAddress(CCAddress address) {
        this.address = address;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "BUILDING_ID", nullable = false)
    public Set<HousingFloor> getFloors() {
        return floors;
    }

    public void setFloors(Set<HousingFloor> floors) {
        this.floors = floors;
    }

}


