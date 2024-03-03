package de.mnet.test.generator.model;

import java.io.*;

/**
 *
 */
public class Address implements Serializable {

    private static final long serialVersionUID = 3270822223015039028L;
    private boolean primary;
    private String street;
    private String zip;
    private String city;
    private String province;
    private String country;
    private String latCoordination;
    private String lngCoordination;

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLatCoordination() {
        return latCoordination;
    }

    public void setLatCoordination(String latCoordination) {
        this.latCoordination = latCoordination;
    }

    public String getLngCoordination() {
        return lngCoordination;
    }

    public void setLngCoordination(String lngCoordination) {
        this.lngCoordination = lngCoordination;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String toString() {
        return "Address{" +
                "primary=" + primary +
                ", street='" + street + '\'' +
                ", zip='" + zip + '\'' +
                ", city='" + city + '\'' +
                ", province='" + province + '\'' +
                ", country='" + country + '\'' +
                ", latCoordination='" + latCoordination + '\'' +
                ", lngCoordination='" + lngCoordination + '\'' +
                '}';
    }
}
