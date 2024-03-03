/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.01.2010 12:18:19
 */
package de.augustakom.hurrican.model.cc;


/**
 *
 */
@SuppressWarnings("unused")
public class GewofagWohnungBuilder extends AbstractCCIDModelBuilder<GewofagWohnungBuilder, GewofagWohnung> {

    private GeoIdBuilder geoIdBuilder;
    private EquipmentBuilder equipmentBuilder;
    private String name = "999.456";
    private String etage = "EG";
    private String lage = "4 li";
    private String tae = "999.456-TAE1";


    public GeoIdBuilder getGeoIdBuilder() {
        return geoIdBuilder;
    }

    public EquipmentBuilder getEquipmentBuilder() {
        return equipmentBuilder;
    }


    public GewofagWohnungBuilder withGeoIdBuilder(GeoIdBuilder geoIdBuilder) {
        this.geoIdBuilder = geoIdBuilder;
        return this;
    }

    public GewofagWohnungBuilder withEquipmentBuilder(EquipmentBuilder equipmentBuilder) {
        this.equipmentBuilder = equipmentBuilder;
        return this;
    }

    public GewofagWohnungBuilder withName(String name) {
        this.name = name;
        this.tae = name + "-TAE1";
        return this;
    }

    public GewofagWohnungBuilder withTae(String tae) {
        this.tae = tae;
        return this;
    }

    public GewofagWohnungBuilder withEtage(String etage) {
        this.etage = etage;
        return this;
    }
}
