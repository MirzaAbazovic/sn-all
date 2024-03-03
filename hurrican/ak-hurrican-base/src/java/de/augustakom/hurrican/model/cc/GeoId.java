/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.12.2010 09:24:21
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;
import javax.annotation.*;
import javax.persistence.*;
import javax.validation.constraints.*;
import com.google.common.collect.Maps;
import org.apache.commons.lang.SystemUtils;

import de.augustakom.common.tools.lang.StringTools;
import de.mnet.annotation.ObjectsAreNonnullByDefault;

/**
 * Modell-Klasse fuer die Abbildung einer Geo-ID. <br> Bei der Geo-ID handelt es sich um eine eindeutige
 * Lokalitaetsinformation.
 */
@Entity
@Table(name = "T_GEO_ID_CACHE")
@ObjectsAreNonnullByDefault
public class GeoId extends GeoIdLocation {
    public static final String STREET = "streetSection.name";
    public static final String HOUSENUM = "houseNum";
    public static final String HOUSENUM_EXT = "houseNumExtension";
    public static final String CITY = "streetSection.zipCode.city.name";
    public static final String ZIPCODE = "streetSection.zipCode.zipCode";
    public static final String DISTRICT = "streetSection.district.name";
    public static final Object COMPLETE_HOUSENUM = "completeHouseNum";
    public static final String ID = "id";

    @Nullable
    private String houseNum;
    @Nullable
    private String houseNumExtension;
    @Nullable
    private String onkz;
    @Nullable
    private String asb;
    @Nullable
    private String kvz;
    @Nullable
    private String agsn;
    @Nullable
    private String distance;
    @Nullable
    private Boolean noDTAGTAL;
    @NotNull
    private GeoIdStreetSection streetSection;
    private Map<String, GeoIdCarrierAddress> carrierAddresses = Maps.newHashMap();

    @Nullable
    @Column(name = "HOUSENUM")
    public String getHouseNum() {
        return houseNum;
    }

    public void setHouseNum(@Nullable String houseNum) {
        this.houseNum = houseNum;
    }

    @Nullable
    @Column(name = "HOUSENUMEXTENSION")
    public String getHouseNumExtension() {
        return houseNumExtension;
    }

    public void setHouseNumExtension(@Nullable String houseNumExtension) {
        this.houseNumExtension = houseNumExtension;
    }

    @Nullable
    public String getOnkz() {
        return onkz;
    }

    public void setOnkz(@Nullable String onkz) {
        this.onkz = onkz;
    }

    @Nullable
    public String getAsb() {
        return asb;
    }

    public void setAsb(@Nullable String asb) {
        this.asb = asb;
    }

    @Nullable
    public String getKvz() {
        return kvz;
    }

    public void setKvz(@Nullable String kvz) {
        this.kvz = kvz;
    }

    @Nullable
    public String getAgsn() {
        return agsn;
    }

    public void setAgsn(@Nullable String agsn) {
        this.agsn = agsn;
    }

    @Nullable
    public String getDistance() {
        return distance;
    }

    public void setDistance(@Nullable String distance) {
        this.distance = distance;
    }

    @Nullable
    @Column(name = "NO_DTAG_TAL")
    public Boolean getNoDTAGTAL() {
        return noDTAGTAL;
    }

    public void setNoDTAGTAL(@Nullable Boolean noDTAGTAL) {
        this.noDTAGTAL = noDTAGTAL;
    }

    @NotNull
    @ManyToOne(optional = true)
    @JoinColumn(name = "STREETSECTION_ID")
    public GeoIdStreetSection getStreetSection() {
        return streetSection;
    }

    public void setStreetSection(GeoIdStreetSection streetSection) {
        this.streetSection = streetSection;
    }

    @ElementCollection(fetch = FetchType.EAGER)
    @JoinTable(name = "T_GEO_ID_CARRIER_ADDRESS", joinColumns = @JoinColumn(name = "GEO_ID"))
    @MapKeyColumn(name = "CARRIER")
    public Map<String, GeoIdCarrierAddress> getCarrierAddresses() {
        return carrierAddresses;
    }

    public void setCarrierAddresses(Map<String, GeoIdCarrierAddress> carrierAddresses) {
        this.carrierAddresses = carrierAddresses;
    }

    /**
     * Gibt den kombinierten String aus 'strasse' und 'abschnitt' zurueck.
     *
     * @return
     */
    @Transient
    public String getStreetAndHouseNum() {
        return StringTools.join(new String[] { getStreetSection().getName(), getHouseNum(), getHouseNumExtension() },
                " ", true);
    }

    /**
     * Gibt eine Kombination von 'houseNum' und 'houseNumExtension' (getrennt durch ' ') zurueck.
     *
     * @return
     */
    @Transient
    public String getCompleteHouseNum() {
        return StringTools.join(new String[] { getHouseNum(), getHouseNumExtension() }, " ", true);
    }

    /**
     * Gibt eine Uebersicht der Strasse als String zurueck.
     *
     * @return String mit den Details des Objekts.
     */
    @Transient
    public String createInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("Geo ID:  ").append(getId());
        sb.append(SystemUtils.LINE_SEPARATOR);
        sb.append("Strasse:  ").append(getStreetAndHouseNum());
        sb.append(SystemUtils.LINE_SEPARATOR);
        sb.append("PLZ:  ").append(getZipCode() != null ? getZipCode() : "");
        sb.append(SystemUtils.LINE_SEPARATOR);
        sb.append("Ort:  ").append(getCity() != null ? getCity() : "");
        sb.append(SystemUtils.LINE_SEPARATOR);
        sb.append("Ortsteil:  ").append(getDistrict() != null ? getDistrict() : "");
        sb.append(SystemUtils.LINE_SEPARATOR);
        return sb.toString();
    }

    @Transient
    public String getStreet() {
        return getStreetSection().getName();
    }

    @Transient
    public String getZipCode() {
        return getStreetSection().getZipCode().getZipCode();
    }

    @Transient
    @Nullable
    public String getDistrict() {
        return ((getStreetSection() != null) && (getStreetSection().getDistrict() != null))
                ? getStreetSection().getDistrict().getName()
                : null;
    }

    @Transient
    public String getCity() {
        return getStreetSection().getZipCode().getCity().getName();
    }

    @Transient
    public String getCountry() {
        return getStreetSection().getZipCode().getCity().getCountry().getName();
    }

}
