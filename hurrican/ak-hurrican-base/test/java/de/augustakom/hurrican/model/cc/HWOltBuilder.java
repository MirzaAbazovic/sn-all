/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.10.2009 07:57:48
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;

import de.augustakom.hurrican.model.cc.hardware.HWOlt;
import de.augustakom.hurrican.model.cc.hardware.HWRack;


/**
 * Entity Builder for HWOlt objects
 *
 *
 */
@SuppressWarnings("unused")
public class HWOltBuilder extends HWRackBuilder<HWOltBuilder, HWOlt> {

    private Long rackId;
    private String serialNo = randomString(30);
    private String qinqVon = randomString(10);
    private String ipNetzVon;
    private Integer oltNr = randomInt(1, Integer.MAX_VALUE);
    private Date vlanAktivAb;

    @Override
    protected void initialize() {
        super.withRackTyp(HWRack.RACK_TYPE_OLT);
    }

    public HWOltBuilder withIpNetzVon(String ipNetzVon) {
        this.ipNetzVon = ipNetzVon;
        return this;
    }

    public HWOltBuilder withOltNr(Integer oltNr) {
        this.oltNr = oltNr;
        return this;
    }

    public HWOltBuilder withVlanAktivAb(Date vlanAktivAb) {
        this.vlanAktivAb = vlanAktivAb;
        return this;
    }
}


