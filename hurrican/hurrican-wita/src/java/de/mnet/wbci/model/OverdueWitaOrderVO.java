/* 
 * Copyright (c) 2014 - M-net Telekommunikations GmbH 
 * All rights reserved. 
 * ------------------------------------------------------- 
 * File created: 23.06.2014 
 */
package de.mnet.wbci.model;

import java.util.*;

import de.augustakom.common.tools.lang.Pair;
import de.mnet.wita.model.WitaCBVorgang;

/**
 * Container for preagreements with missing open {@link WitaCBVorgang}s.
 */
public class OverdueWitaOrderVO extends BaseOverdueVo {

    private static final long serialVersionUID = -7911318500748099411L;
    private Technologie mNetTechnologie;
    private List<Pair<String, String>> assignedWitaOrders;

    public Technologie getmNetTechnologie() {
        return mNetTechnologie;
    }

    public void setmNetTechnologie(Technologie mNetTechnologie) {
        this.mNetTechnologie = mNetTechnologie;
    }

    public List<Pair<String, String>> getAssignedWitaOrders() {
        return assignedWitaOrders;
    }

    public void setAssignedWitaOrders(List<Pair<String, String>> assignedWitaOrders) {
        this.assignedWitaOrders = assignedWitaOrders;
    }

    public void addAssignedWitaOrder(Pair<String, String> witaOrderInformation) {
        if (assignedWitaOrders == null) {
            assignedWitaOrders = new ArrayList<>();
        }
        assignedWitaOrders.add(witaOrderInformation);
    }

}
