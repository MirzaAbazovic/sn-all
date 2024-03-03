/*
 * Copyright (c) 2006 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.12.2006 09:43:16
 */
package de.augustakom.hurrican.model.cc;

import javax.persistence.*;


/**
 * Abstrakte Modellklasse fuer Modelle, die eine Historisierung und eine User-Kennung besitzen. <br> In die User-Kennung
 * wird der Benutzername eingetragen, der die letzte Aenderung an dem Modell vorgenommen hat.
 *
 *
 */
@MappedSuperclass
public abstract class AbstractCCHistoryUserModel extends AbstractCCHistoryModel {

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


