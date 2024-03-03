package de.augustakom.hurrican.model.cc;

import javax.annotation.*;
import javax.persistence.*;
import javax.validation.constraints.*;

import de.mnet.annotation.ObjectsAreNonnullByDefault;

@Entity
@Table(name = "T_GEO_ID_STREET_SECTION")
@ObjectsAreNonnullByDefault
public class GeoIdStreetSection extends GeoIdLocation {
    @NotNull
    private String name;
    @NotNull
    private GeoIdZipCode zipCode;
    @Nullable
    private GeoIdDistrict district;

    @NotNull
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "ZIP_CODE_ID")
    public GeoIdZipCode getZipCode() {
        return zipCode;
    }

    public void setZipCode(GeoIdZipCode zipCode) {
        this.zipCode = zipCode;
    }

    @Nullable
    @ManyToOne(optional = true)
    @JoinColumn(name = "DISTRICT_ID")
    public GeoIdDistrict getDistrict() {
        return district;
    }

    public void setDistrict(@Nullable GeoIdDistrict district) {
        this.district = district;
    }
}
