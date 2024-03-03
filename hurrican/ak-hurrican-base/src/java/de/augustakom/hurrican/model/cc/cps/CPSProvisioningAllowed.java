/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.06.2009 11:10:48
 */
package de.augustakom.hurrican.model.cc.cps;

import de.augustakom.hurrican.model.base.AbstractHurricanModel;


/**
 * DataTransfer-Objekt, um das Result der Pruefung aufzunehmen, ob ein Auftrag per CPS provisioniert werden darf.
 *
 *
 */
public class CPSProvisioningAllowed extends AbstractHurricanModel {

    private boolean provisioningAllowed = false;
    private String noCPSProvisioningReason = null;

    /**
     * Default-Const.
     */
    public CPSProvisioningAllowed() {
    }

    /**
     * @param provisioningAllowed
     * @param noCPSProvisioningReason
     */
    public CPSProvisioningAllowed(boolean provisioningAllowed, String noCPSProvisioningReason) {
        super();
        this.provisioningAllowed = provisioningAllowed;
        this.noCPSProvisioningReason = noCPSProvisioningReason;
    }

    /**
     * @return the provisioningAllowed
     */
    public boolean isProvisioningAllowed() {
        return provisioningAllowed;
    }

    /**
     * @param provisioningAllowed the provisioningAllowed to set
     */
    public void setProvisioningAllowed(boolean provisioningAllowed) {
        this.provisioningAllowed = provisioningAllowed;
    }

    /**
     * @return the noCPSProvisioningReason
     */
    public String getNoCPSProvisioningReason() {
        return noCPSProvisioningReason;
    }

    /**
     * @param noCPSProvisioningReason the noCPSProvisioningReason to set
     */
    public void setNoCPSProvisioningReason(String noCPSProvisioningReason) {
        this.noCPSProvisioningReason = noCPSProvisioningReason;
    }

}


