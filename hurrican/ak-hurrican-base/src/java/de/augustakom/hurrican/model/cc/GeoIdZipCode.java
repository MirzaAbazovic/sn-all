package de.augustakom.hurrican.model.cc;

import javax.persistence.*;
import javax.validation.constraints.*;

import de.mnet.annotation.ObjectsAreNonnullByDefault;

@Entity
@Table(name = "T_GEO_ID_ZIPCODE")
@ObjectsAreNonnullByDefault
public class GeoIdZipCode extends GeoIdLocation {
    @NotNull
    private String zipCode;
    @NotNull
    private GeoIdCity city;

    @NotNull
    @Column(name = "ZIP_CODE")
    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "CITY_ID")
    public GeoIdCity getCity() {
        return city;
    }

    public void setCity(GeoIdCity city) {
        this.city = city;
    }
}
