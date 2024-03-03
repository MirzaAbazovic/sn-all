/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.02.2015
 */
package de.augustakom.hurrican.model.cc.innenauftrag;

import javax.persistence.*;

import de.augustakom.hurrican.model.cc.AbstractCCIDModel;

/**
 *
 */
@MappedSuperclass
public abstract class AbstractIaLevel extends AbstractCCIDModel {
    private static final long serialVersionUID = -2828300306082290461L;

    private String name;
    private String sapId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "SAP_ID")
    public String getSapId() {
        return sapId;
    }

    public void setSapId(String sapId) {
        this.sapId = sapId;
    }
}