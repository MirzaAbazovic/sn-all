/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.04.2009 12:54:19
 */
package de.augustakom.hurrican.model.billing;


/**
 * Modell zur Abbildung der Service-Rooms aus Taifun.
 *
 *
 */
public class ServiceRoom extends AbstractBillingModel {

    private Long serviceRoomNo = null;
    private String onkz = null;
    private Long asb = null;

    /**
     * @return the serviceRoomNo
     */
    public Long getServiceRoomNo() {
        return serviceRoomNo;
    }

    /**
     * @param serviceRoomNo the serviceRoomNo to set
     */
    public void setServiceRoomNo(Long serviceRoomNo) {
        this.serviceRoomNo = serviceRoomNo;
    }

    /**
     * @return the onkz
     */
    public String getOnkz() {
        return onkz;
    }

    /**
     * @param onkz the onkz to set
     */
    public void setOnkz(String onkz) {
        this.onkz = onkz;
    }

    /**
     * @return the asb
     */
    public Long getAsb() {
        return asb;
    }

    /**
     * @param asb the asb to set
     */
    public void setAsb(Long asb) {
        this.asb = asb;
    }

}


