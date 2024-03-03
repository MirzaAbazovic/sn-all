/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.09.2009 12:01:16
 */
package de.augustakom.hurrican.model.cc;


/**
 * EntityBuilder for BrasPool objects
 */
@SuppressWarnings("unused")
public class BrasPoolBuilder extends AbstractCCIDModelBuilder<BrasPoolBuilder, BrasPool> {

    {id = getLongId();}

    private String name = "Test Pool";
    private Integer vp = 5;
    private Integer vcMin = 50;
    private Integer vcMax = 100;
    private String nasIdentifier;
    private Integer port;
    private Integer slot;
    private String backupNasIdentifier;
    private Integer backupPort;
    private Integer backupSlot;


    public BrasPoolBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public BrasPoolBuilder withVp(Integer vp) {
        this.vp = vp;
        return this;
    }

    public BrasPoolBuilder withVcMin(Integer vcMin) {
        this.vcMin = vcMin;
        return this;
    }

    public BrasPoolBuilder withVcMax(Integer vcMax) {
        this.vcMax = vcMax;
        return this;
    }

}
