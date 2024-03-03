/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.10.2009 09:58:03
 */
package de.augustakom.hurrican.model.cc;


/**
 * Modell um ACLs zu einem Endgeraet zu halten
 *
 *
 */
public class EndgeraetAcl extends AbstractCCIDModel {
    private String name;
    private String routerTyp;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRouterTyp() {
        return routerTyp;
    }

    public void setRouterTyp(String routerTyp) {
        this.routerTyp = routerTyp;
    }
}
