/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.2009 12:39:30
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp;

/**
 * EntityBuilder for HWBaugruppenTyp objects
 *
 *
 */
@SuppressWarnings("unused")
public class HWBaugruppenTypBuilder extends AbstractCCIDModelBuilder<HWBaugruppenTypBuilder, HWBaugruppenTyp> {

    private String name = "ABLTF";
    private String hwSchnittstelleName = "ADSL";
    private String hwTypeName = "XDSL_MUC";
    private Integer portCount = Integer.valueOf(24);
    private Integer portCountOut = null;
    private Boolean isActive = Boolean.TRUE;
    private String description = "TEST BaugruppenTyp";
    private HVTTechnikBuilder hvtTechnikBuilder;
    private Bandwidth maxBandwidth;
    private HWBaugruppenTyp.Tunneling tunneling;
    private Schicht2Protokoll defaultSchicht2Protokoll;
    private Boolean bondingCapable = Boolean.FALSE;
    private boolean profileAssignable;

    public Integer getPortCount() {
        return portCount;
    }

    public HVTTechnikBuilder getHvtTechnikBuilder() {
        return hvtTechnikBuilder;
    }

    public HWBaugruppenTypBuilder setSdslValues() {
        name = "SHLTB";
        hwSchnittstelleName = "SDSL";
        hwTypeName = "XDSL_AGB";
        portCount = 24;
        return this;
    }

    public HWBaugruppenTypBuilder setAdslValues() {
        name = "ABLTF";
        hwSchnittstelleName = "ADSL";
        hwTypeName = "XDSL_MUC";
        portCount = 24;
        return this;
    }

    public HWBaugruppenTypBuilder setAbValues() {
        name = "SLMAITFG";
        hwSchnittstelleName = "AB";
        hwTypeName = "EWSD_DLU";
        portCount = 32;
        return this;
    }

    public HWBaugruppenTypBuilder setUk0Values() {
        name = "SLMDTFC";
        hwSchnittstelleName = "UK0";
        hwTypeName = "EWSD_DLU";
        portCount = 24;
        return this;
    }

    public HWBaugruppenTypBuilder setOltValues() {
        name = "OLT_DUMMY";
        hwSchnittstelleName = null;
        hwTypeName = null;
        portCount = 0;
        return this;
    }

    public HWBaugruppenTypBuilder setOntEthValues() {
        name = "ONT_ETH";
        hwSchnittstelleName = "ETH";
        hwTypeName = "ONT";
        portCount = 3;
        return this;
    }

    public HWBaugruppenTypBuilder setOntPotsValues() {
        name = "ONT_POTS";
        hwSchnittstelleName = "POTS";
        hwTypeName = "ONT";
        portCount = 2;
        return this;
    }

    public HWBaugruppenTypBuilder setOntRfValues() {
        name = "ONT_RF";
        hwSchnittstelleName = "RF";
        hwTypeName = "ONT";
        portCount = 1;
        return this;
    }

    public HWBaugruppenTypBuilder setDpoVdslValues() {
        name = "DPO_VDSL2";
        hwSchnittstelleName = "VDSL2";
        hwTypeName = "DPO";
        portCount = 1;
        return this;
    }

    public HWBaugruppenTypBuilder withTunneling(HWBaugruppenTyp.Tunneling tunneling) {
        this.tunneling = tunneling;
        return this;
    }

    public HWBaugruppenTypBuilder withHvtTechnikBuilder(HVTTechnikBuilder hvtTechnikBuilder) {
        this.hvtTechnikBuilder = hvtTechnikBuilder;
        return this;
    }

    public HWBaugruppenTypBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public HWBaugruppenTypBuilder withHwTypeName(String hwTypeName) {
        this.hwTypeName = hwTypeName;
        return this;
    }

    public HWBaugruppenTypBuilder withHwSchnittstelleName(String hwSchnittstelleName) {
        this.hwSchnittstelleName = hwSchnittstelleName;
        return this;
    }

    public HWBaugruppenTypBuilder withPortCount(int portCount) {
        this.portCount = portCount;
        return this;
    }

    public HWBaugruppenTypBuilder withMaxBandwidth(Bandwidth maxBandwidth) {
        this.maxBandwidth = maxBandwidth;
        return this;
    }

    public HWBaugruppenTypBuilder withDefaultSchicht2Protokoll(Schicht2Protokoll schicht2Protokoll) {
        this.defaultSchicht2Protokoll = schicht2Protokoll;
        return this;
    }

    public HWBaugruppenTypBuilder withBondingCapable(Boolean bondingCapable) {
        this.bondingCapable = bondingCapable;
        return this;
    }

    public HWBaugruppenTypBuilder withProfileAssignable(Boolean profileAssignable) {
        this.profileAssignable = profileAssignable;
        return this;
    }
}
