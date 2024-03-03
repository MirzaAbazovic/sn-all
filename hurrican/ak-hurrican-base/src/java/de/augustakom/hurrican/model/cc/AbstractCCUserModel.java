/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.06.2011 09:46:11
 */
package de.augustakom.hurrican.model.cc;

import javax.persistence.*;


/**
 * Abstrakte Modellklasse fuer Modelle, die eine User-Kennung besitzen. <br> In die User-Kennung wird der Benutzername
 * eingetragen, der die letzte Aenderung an dem Modell vorgenommen hat.
 *
 *
 */
@MappedSuperclass
public abstract class AbstractCCUserModel extends AbstractCCIDModel {

    private String userW = null;

    /**
     * @return Returns the userW.
     */
    @Column(name = "USERW")
    public String getUserW() {
        return this.userW;
    }


    /**
     * @param userW The userW to set.
     */
    public void setUserW(String userW) {
        this.userW = userW;
    }
}


