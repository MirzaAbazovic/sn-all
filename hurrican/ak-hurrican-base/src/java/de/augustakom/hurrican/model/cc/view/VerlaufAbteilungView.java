/*
 * Copyright (c) 2009 - M-net Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.05.2009 11:10:26
 */
package de.augustakom.hurrican.model.cc.view;

import de.augustakom.hurrican.model.cc.AbstractCCModel;


/**
 * View-Klasse fuer Abteilungs- und Niederlassungszuordnung des Bauauftrags.
 *
 *
 */
public class VerlaufAbteilungView extends AbstractCCModel {

    private Long abtId = null;
    private Long nlId = null;

    /**
     * @return the abtId
     */
    public Long getAbtId() {
        return abtId;
    }

    /**
     * @param abtId the abtId to set
     */
    public void setAbtId(Long abtId) {
        this.abtId = abtId;
    }

    /**
     * @return the nlId
     */
    public Long getNlId() {
        return nlId;
    }

    /**
     * @param nlId the nlId to set
     */
    public void setNlId(Long nlId) {
        this.nlId = nlId;
    }


}


