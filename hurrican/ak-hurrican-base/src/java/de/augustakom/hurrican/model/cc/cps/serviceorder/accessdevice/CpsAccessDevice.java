/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.05.2014
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder.accessdevice;

import java.util.*;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import de.augustakom.hurrican.model.cc.cps.serviceorder.AbstractCPSServiceOrderDataModel;

/**
 * Created by guiber on 02.05.2014.
 */
@XStreamAlias("ACCESS_DEVICE")
public class CpsAccessDevice extends AbstractCPSServiceOrderDataModel {

    @XStreamImplicit(itemFieldName = "ITEM")
    private List<CpsItem> items;

    public List<CpsItem> getItems() {
        return items;
    }

    public void setItems(List<CpsItem> items) {
        this.items = items;
    }
}