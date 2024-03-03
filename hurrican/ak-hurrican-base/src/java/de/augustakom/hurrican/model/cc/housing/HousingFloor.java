/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.03.2010 10:09:05
 */
package de.augustakom.hurrican.model.cc.housing;

import java.util.*;
import javax.persistence.*;
import javax.validation.constraints.*;

import de.augustakom.hurrican.model.cc.AbstractCCIDModel;


/**
 * Modell zur Abbildung eines Stockwerks innerhalb eines Housing-Gebaeudes.
 *
 *
 */
@Entity
@Table(name = "T_HOUSING_FLOOR")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_HOUSING_FLOOR_0", allocationSize = 1)
public class HousingFloor extends AbstractCCIDModel {

    private String floor;
    private Set<HousingRoom> rooms = new HashSet<HousingRoom>();

    @Column(name = "FLOOR")
    @NotNull
    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "FLOOR_ID", nullable = false)
    public Set<HousingRoom> getRooms() {
        return rooms;
    }

    public void setRooms(Set<HousingRoom> rooms) {
        this.rooms = rooms;
    }

}


