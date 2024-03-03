/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.2009 12:23:27
 */
package de.augustakom.hurrican.model.cc;


/**
 * EntityBuilder for HVTTechnik objects
 *
 *
 */
@SuppressWarnings("unused")
public class HVTTechnikBuilder extends AbstractCCIDModelBuilder<HVTTechnikBuilder, HVTTechnik> {
    private String hersteller = "Siemens-Test";
    private String cpsName;

    public HVTTechnikBuilder toAlcatel() {
        hersteller = "Alcatel-Lucent";
        cpsName = "Alcatel";
        id = HVTTechnik.ALCATEL;
        setPersist(false);
        return this;
    }

    public HVTTechnikBuilder toSiemens() {
        hersteller = "Siemens";
        cpsName = "Siemens";
        id = HVTTechnik.SIEMENS;
        setPersist(false);
        return this;
    }

    public HVTTechnikBuilder toHuawei() {
        hersteller = "Huawei";
        cpsName = "Huawei";
        id = HVTTechnik.HUAWEI;
        setPersist(false);
        return this;
    }

    public HVTTechnikBuilder withHersteller(String hersteller) {
        this.hersteller = hersteller;
        return this;
    }

    public HVTTechnikBuilder withCpsName(String cpsName) {
        this.cpsName = cpsName;
        return this;
    }

}
