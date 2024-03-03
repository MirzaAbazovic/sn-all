/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.02.2006 14:54:27
 */
package de.augustakom.hurrican.model.cc;


/**
 * Modell zur Abbildung von Carrier-Mappings. <br>
 *
 *
 */
public class CarrierMapping extends AbstractCCIDModel {

    private Long carrierId = null;
    private Long carrierContactId = null;
    private Long carrierKennungId = null;
    private String userW = null;

    /**
     * @return the carrierId
     */
    public Long getCarrierId() {
        return carrierId;
    }

    /**
     * @param carrierId the carrierId to set
     */
    public void setCarrierId(Long carrierId) {
        this.carrierId = carrierId;
    }

    /**
     * @return the carrierContactId
     */
    public Long getCarrierContactId() {
        return carrierContactId;
    }

    /**
     * @param carrierContactId the carrierContactId to set
     */
    public void setCarrierContactId(Long carrierContactId) {
        this.carrierContactId = carrierContactId;
    }

    /**
     * @return the carrierKennungId
     */
    public Long getCarrierKennungId() {
        return carrierKennungId;
    }

    /**
     * @param carrierKennungId the carrierKennungId to set
     */
    public void setCarrierKennungId(Long carrierKennungId) {
        this.carrierKennungId = carrierKennungId;
    }

    /**
     * @return Returns the userW.
     */
    public String getUserW() {
        return userW;
    }

    /**
     * @param userW The userW to set.
     */
    public void setUserW(String userW) {
        this.userW = userW;
    }
}


