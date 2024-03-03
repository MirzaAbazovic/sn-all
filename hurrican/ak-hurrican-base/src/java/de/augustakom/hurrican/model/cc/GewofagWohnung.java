/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.01.2010 11:13:50
 */
package de.augustakom.hurrican.model.cc;


/**
 * Modelklasse fuer eine Gewofag-Wohnung
 *
 *
 */
public class GewofagWohnung extends AbstractCCIDModel {
    private GeoId geoId;
    public static final String GEO_ID = "geoId";
    private Equipment equipment;
    public static final String EQUIPMENT = "equipment";
    private String name;
    public static final String NAME = "name";
    private String etage;
    public static final String ETAGE = "etage";
    private String lage;
    public static final String LAGE = "lage";
    private String tae;
    public static final String TAE = "tae";

    public GeoId getGeoId() {
        return geoId;
    }

    public void setGeoId(GeoId geoId) {
        this.geoId = geoId;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEtage() {
        return etage;
    }

    public void setEtage(String etage) {
        this.etage = etage;
    }

    public String getLage() {
        return lage;
    }

    public void setLage(String lage) {
        this.lage = lage;
    }

    public String getTae() {
        return tae;
    }

    public void setTae(String tae) {
        this.tae = tae;
    }

}
