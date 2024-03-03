/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.03.2012 15:26:54
 */
package de.augustakom.hurrican.model.wholesale;

import java.util.*;

import de.augustakom.hurrican.model.cc.fttx.CVlan;
import de.mnet.annotation.ObjectsAreNonnullByDefault;

/**
 * DTO für Wholesale Pbit
 */
@ObjectsAreNonnullByDefault
public class WholesalePbit {

    private String service;
    private int limit;

    public static List<WholesalePbit> createPbitsFromCVlans(List<CVlan> cvlans) {
        List<WholesalePbit> pbits = new ArrayList<WholesalePbit>();
        for (CVlan cvlan : cvlans) {
            if (cvlan.getPbitLimit() != null) {
                WholesalePbit wholesalePbit = new WholesalePbit();
                wholesalePbit.setService(cvlan.getTyp().name());
                wholesalePbit.setLimit(cvlan.getPbitLimit());
                pbits.add(wholesalePbit);
            }
        }
        return pbits;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}


