/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.05.2009 07:19:26
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder;

import java.util.*;
import com.thoughtworks.xstream.annotations.XStreamAlias;


/**
 * Abstrakte Modell-Klasse zur Abbildung von IP-Daten. Die Ableitungen der Klasse koennen/muessen die IP-Adresse in der
 * gewuenschten Form - IPv4 oder IPv6 - aufnehmen.
 *
 *
 */
public class AbstractCPSRadiusIPData extends AbstractCPSServiceOrderDataModel {

    @XStreamAlias("ACLIN")
    private String aclIn = null;
    @XStreamAlias("ACLOUT")
    private String aclOut = null;
    @XStreamAlias("ROUTES")
    private List<CPSRadiusRoutesData> routes = null;

    /**
     * Fuegt der IP-Adresse eine neue Route hinzu.
     *
     * @param toAdd
     *
     */
    public void addCPSRadiusRoute(CPSRadiusRoutesData toAdd) {
        if (getRoutes() == null) {
            setRoutes(new ArrayList<CPSRadiusRoutesData>());
        }
        getRoutes().add(toAdd);
    }

    /**
     * @return the aclIn
     */
    public String getAclIn() {
        return aclIn;
    }

    /**
     * @param aclIn the aclIn to set
     */
    public void setAclIn(String aclIn) {
        this.aclIn = aclIn;
    }

    /**
     * @return the aclOut
     */
    public String getAclOut() {
        return aclOut;
    }

    /**
     * @param aclOut the aclOut to set
     */
    public void setAclOut(String aclOut) {
        this.aclOut = aclOut;
    }

    /**
     * @return the routes
     */
    public List<CPSRadiusRoutesData> getRoutes() {
        return routes;
    }

    /**
     * @param routes the routes to set
     */
    public void setRoutes(List<CPSRadiusRoutesData> routes) {
        this.routes = routes;
    }

}


