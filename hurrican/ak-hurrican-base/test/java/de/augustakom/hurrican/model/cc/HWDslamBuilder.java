/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.2009 11:00:56
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.hurrican.model.cc.hardware.HWDslam;
import de.augustakom.hurrican.model.cc.hardware.HWRack;

/**
 * EntityBuilder for HWDslam objects
 *
 *
 */
@SuppressWarnings("unused")
public class HWDslamBuilder extends HWRackBuilder<HWDslamBuilder, HWDslam> {

    private String dhcp82Name = null;
    private Integer vpiUbrADSL = null;
    private Integer vpiSDSL = null;
    private Integer vpiCpeMgmt = null;
    private Integer outerTagADSL = 123;
    private Integer outerTagSDSL = 124;
    private Integer outerTagVoip = 2123;
    private Integer outerTagCpeMgmt = 4;
    private Integer outerTagIadMgmt = 3;
    private Integer brasVpiADSL = null;
    private Integer brasOuterTagADSL = 125;
    private Integer brasOuterTagSDSL = 126;
    private Integer brasOuterTagVoip = 2124;
    private String ipAdress = "127.0.0.1";
    private String einbauplatz = null;
    private String anschluss = null;
    private String ansSlotPort = null;
    private String ansArt = null;
    private Boolean schmidtschesKonzept = null;
    private String softwareVersion = null;
    private String erxIfaceDaten = null;
    private String erxStandort = null;
    private String physikArt = HWDslam.ETHERNET;
    private String physikWert = null;
    private String atmPattern = null;
    private Integer ccOffset = null;
    private Integer svlan;
    private String altGslamBez;
    private String dslamType;

    @Override
    protected void initialize() {
        super.withRackTyp(HWRack.RACK_TYPE_DSLAM);
    }

    public HWDslamBuilder asAtmDslam() {
        physikArt = HWDslam.ATM;
        vpiUbrADSL = 123;
        vpiSDSL = 124;
        vpiCpeMgmt = 125;
        outerTagADSL = null;
        outerTagSDSL = null;
        outerTagVoip = null;
        outerTagCpeMgmt = null;
        outerTagIadMgmt = null;
        brasVpiADSL = 321;
        brasOuterTagADSL = null;
        brasOuterTagSDSL = null;
        brasOuterTagVoip = null;
        return this;
    }

    public HWDslamBuilder withSVlan(final Integer svlan) {
        this.svlan = svlan;
        return this;
    }

    public HWDslamBuilder withAltGslamBez(final String altGslamBez) {
        this.altGslamBez = altGslamBez;
        return this;
    }

    public HWDslamBuilder withPhysikArt(String physikArt) {
        this.physikArt = physikArt;
        return this;
    }

    public HWDslamBuilder withOuterTagAdsl(Integer outerTagADSL) {
        this.outerTagADSL = outerTagADSL;
        return this;
    }

    public HWDslamBuilder withOuterTagSdsl(Integer outerTagSDSL) {
        this.outerTagSDSL = outerTagSDSL;
        return this;
    }

    public HWDslamBuilder withOuterTagVoip(Integer outerTagVoip) {
        this.outerTagVoip = outerTagVoip;
        return this;
    }

    public HWDslamBuilder withOuterTagCpeMgmt(Integer outerTagCpeMgmt) {
        this.outerTagCpeMgmt = outerTagCpeMgmt;
        return this;
    }

    public HWDslamBuilder withOuterTagIadMgmt(Integer outerTagIadMgmt) {
        this.outerTagIadMgmt = outerTagIadMgmt;
        return this;
    }

    public HWDslamBuilder withBrasOuterTagADSL(Integer brasOuterTagADSL) {
        this.brasOuterTagADSL = brasOuterTagADSL;
        return this;
    }

    public HWDslamBuilder withBrasOuterTagSDSL(Integer brasOuterTagSDSL) {
        this.brasOuterTagSDSL = brasOuterTagSDSL;
        return this;
    }

    public HWDslamBuilder withBrasOuterTagVoip(Integer brasOuterTagVoip) {
        this.brasOuterTagVoip = brasOuterTagVoip;
        return this;
    }

    public HWDslamBuilder withCcOffset(Integer ccOffset) {
        this.ccOffset = ccOffset;
        return this;
    }

    public HWDslamBuilder withIpAddress(String ipAdress) {
        this.ipAdress = ipAdress;
        return this;
    }

    public HWDslamBuilder withDslamType(final String dslamType) {
        this.dslamType = dslamType;
        return this;
    }
}
