/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.10.2009 13:10:45
 */
package de.augustakom.hurrican.model.cc;


/**
 *
 */
@SuppressWarnings("unused")
public class DSLAMProfileBuilder extends AbstractCCIDModelBuilder<DSLAMProfileBuilder, DSLAMProfile> {

    private String name = randomString(20);
    private String bemerkung;
    private Bandwidth bandwidth;
    private Bandwidth bandwidthNetto;
    private Boolean fastpath = Boolean.FALSE;
    private Integer tmDown;
    private Integer tmUp;
    private Boolean l2PowersafeEnabled;
    private Boolean forceADSL1;
    private String uetv;
    private Integer downstreamTechLs;
    private Integer upstreamTechLs;
    private Integer fastpathTechLs;
    private Boolean gueltig = Boolean.TRUE;
    private Long baugruppenTypId;
    private Boolean enabledForAutochange = Boolean.FALSE;

    @Override
    public DSLAMProfileBuilder withRandomId() {
        this.id = randomLong(Integer.MAX_VALUE - 100000, Integer.MAX_VALUE);
        return this;
    }

    public DSLAMProfileBuilder withBandwidth(Bandwidth bandwidth) {
        this.bandwidth = bandwidth;
        return this;
    }

    public DSLAMProfileBuilder withBandwidth(int down, int up) {
        this.bandwidth = Bandwidth.create(down, up);
        return this;
    }

    public DSLAMProfileBuilder withBandwidth(int down) {
        this.bandwidth = Bandwidth.create(down);
        return this;
    }

    public DSLAMProfileBuilder withBandwidthNetto(Bandwidth bandwidthNetto) {
        this.bandwidthNetto = bandwidthNetto;
        return this;
    }

    public DSLAMProfileBuilder withFastpath(Boolean fastpath) {
        this.fastpath = fastpath;
        return this;
    }

    public DSLAMProfileBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public DSLAMProfileBuilder withGueltig(Boolean gueltig) {
        this.gueltig = gueltig;
        return this;
    }

    public DSLAMProfileBuilder withTmDown(Integer tmDown) {
        this.tmDown = tmDown;
        return this;
    }

    public DSLAMProfileBuilder withTmUp(Integer tmUp) {
        this.tmUp = tmUp;
        return this;
    }

    public DSLAMProfileBuilder withL2Power(Boolean l2Power) {
        this.l2PowersafeEnabled = l2Power;
        return this;
    }

    public DSLAMProfileBuilder withForceADSL1(Boolean forceADSL1) {
        this.forceADSL1 = forceADSL1;
        return this;
    }

    public DSLAMProfileBuilder withUetv(String uetv) {
        this.uetv = uetv;
        return this;
    }

    public DSLAMProfileBuilder withBaugruppenTypId(Long baugruppenTypId) {
        this.baugruppenTypId = baugruppenTypId;
        return this;
    }

    public DSLAMProfileBuilder withEnabledForAutochange(Boolean enabledForAutochange) {
        this.enabledForAutochange = enabledForAutochange;
        return this;
    }

    /**
     * @see de.augustakom.common.model.EntityBuilder#beforeBuild()
     */
    @Override
    protected void beforeBuild() {
        super.beforeBuild();
        if (bandwidth == null) {
            this.bandwidth = Bandwidth.create(1000, 256);
        }
    }
}


