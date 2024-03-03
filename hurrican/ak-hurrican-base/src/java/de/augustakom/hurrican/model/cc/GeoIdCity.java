package de.augustakom.hurrican.model.cc;

import javax.persistence.*;
import javax.validation.constraints.*;

import de.mnet.annotation.ObjectsAreNonnullByDefault;

@Entity
@Table(name = "T_GEO_ID_CITY")
@ObjectsAreNonnullByDefault
public class GeoIdCity extends GeoIdLocation {
    @NotNull
    private String name;
    @NotNull
    private GeoIdCountry country;

    @NotNull
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "COUNTRY_ID")
    public GeoIdCountry getCountry() {
        return country;
    }

    public void setCountry(GeoIdCountry country) {
        this.country = country;
    }
}
