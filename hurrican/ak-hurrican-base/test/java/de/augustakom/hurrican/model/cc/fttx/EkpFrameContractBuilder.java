/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.04.2012 15:51:01
 */
package de.augustakom.hurrican.model.cc.fttx;

import java.util.*;

import de.augustakom.hurrican.model.cc.AbstractCCIDModelBuilder;

@SuppressWarnings("unused")
public class EkpFrameContractBuilder extends AbstractCCIDModelBuilder<EkpFrameContractBuilder, EkpFrameContract> {

    private String ekpId = randomString(4);
    private String frameContractId = randomString(8);
    private List<CVlan> cvlans = new ArrayList<CVlan>();
    private Map<A10NspPort, Boolean> a10NspPortsOfEkp = new HashMap<A10NspPort, Boolean>();
    private int svlanFaktor = 50;

    public EkpFrameContractBuilder withEkpId(String ekpId) {
        this.ekpId = ekpId;
        return this;
    }

    public EkpFrameContractBuilder withFrameContractId(String frameContractId) {
        this.frameContractId = frameContractId;
        return this;
    }

    public EkpFrameContractBuilder addCVlan(CVlan cvlan) {
        cvlans.add(cvlan);
        return this;
    }

    public EkpFrameContractBuilder withCVlans(List<CVlan> cvlans) {
        this.cvlans = cvlans;
        return this;
    }

    public EkpFrameContractBuilder addA10NspPort(A10NspPort a10NspPort, Boolean isDefault4Ekp) {
        a10NspPortsOfEkp.put(a10NspPort, isDefault4Ekp);
        return this;
    }

}
