package de.augustakom.hurrican.model.cc;

import javax.persistence.*;
import javax.validation.constraints.*;

import de.mnet.annotation.ObjectsAreNonnullByDefault;

@Entity
@Table(name = "T_GEO_ID_DISTRICT")
@ObjectsAreNonnullByDefault
public class GeoIdDistrict extends GeoIdLocation {
    @NotNull
    private String name;

    @NotNull
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
