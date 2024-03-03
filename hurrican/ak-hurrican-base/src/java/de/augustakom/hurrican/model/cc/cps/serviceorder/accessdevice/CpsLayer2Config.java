/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.05.2014
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder.accessdevice;

import java.util.*;
import javax.annotation.*;
import com.google.common.collect.Lists;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import de.augustakom.hurrican.model.cc.cps.serviceorder.AbstractCPSServiceOrderDataModel;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSPBITData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSVlanData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.PbitAware;

/**
 * Created by guiber on 02.05.2014.
 */
@XStreamAlias("LAYER2_CONFIG")
public class CpsLayer2Config extends AbstractCPSServiceOrderDataModel implements PbitAware {

    @XStreamImplicit(itemFieldName = "PBIT")
    private List<CPSPBITData> pbits = Lists.newArrayList();

    @XStreamImplicit(itemFieldName = "VLAN")
    private List<CPSVlanData> vlans;

    public List<CPSPBITData> getPbits() {
        return pbits;
    }

    public void setPbits(List<CPSPBITData> pbits) {
        this.pbits = pbits;
    }

    public List<CPSVlanData> getVlans() {
        return vlans;
    }

    public void setVlans(List<CPSVlanData> vlans) {
        this.vlans = vlans;
    }

    @Override
    public void addPbit(@Nonnull CPSPBITData pbit) {
        this.pbits.add(pbit);
    }
}
