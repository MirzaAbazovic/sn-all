/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.03.2010 10:09:23
 */
package de.augustakom.hurrican.model.cc.housing;

import java.util.*;
import javax.persistence.*;
import javax.validation.constraints.*;

import de.augustakom.hurrican.model.cc.AbstractCCIDModel;


/**
 * Modell zur Abbildung eines Raums innerhalb eines Housing-Gebaeudes.
 *
 *
 */
@Entity
@Table(name = "T_HOUSING_ROOM")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_HOUSING_ROOM_0", allocationSize = 1)
public class HousingRoom extends AbstractCCIDModel {

    private String room;
    private Set<HousingParcel> parcels = new HashSet<HousingParcel>();

    @Column(name = "ROOM")
    @NotNull
    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "ROOM_ID", nullable = false)
    public Set<HousingParcel> getParcels() {
        return parcels;
    }

    public void setParcels(Set<HousingParcel> parcels) {
        this.parcels = parcels;
    }

}


