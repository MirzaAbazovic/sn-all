/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.12.2008 12:23:17
 */
package de.augustakom.hurrican.model.cc.hardware;

import de.augustakom.hurrican.model.shared.iface.ISerialNoAwareModel;

/**
 * Modell, um eine Hardware-DLU abzubilden.
 *
 *
 */
public class HWRouter extends HWRack implements ISerialNoAwareModel {

    private String routerTyp = null;
    private String serialNumber = null;


    /**
     * @return routerTyp
     */
    public String getRouterTyp() {
        return routerTyp;
    }

    /**
     * @param routerTyp Festzulegender routerTyp
     */
    public void setRouterTyp(String routerTyp) {
        this.routerTyp = routerTyp;
    }

    /**
     * @return serialNumber
     */
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * @param serialNumber Festzulegender serialNumber
     */
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    /**
     * @see de.augustakom.hurrican.model.shared.iface.ISerialNoAwareModel#getSerialNo()
     */
    public String getSerialNo() {
        return getSerialNumber();
    }

}


