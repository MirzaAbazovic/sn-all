/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.02.2005 10:29:20
 */
package de.augustakom.hurrican.model.billing;

import de.augustakom.hurrican.model.shared.iface.KundenModel;

/**
 * Modell fuer die Abbildung der e-Rechnungs-Daten aus dem Billing-System.
 */
public class WebgatePW extends AbstractBillingModel implements KundenModel {

    private Long pwNo;
    private Long rinfoNo;
    private Long kundeNo;
    private String userId;
    private String password;
    private Boolean changePW;

    /**
     * @return the kundeNo
     */
    public Long getKundeNo() {
        return kundeNo;
    }

    /**
     * @param kundeNo the kundeNo to set
     */
    public void setKundeNo(Long kundeNo) {
        this.kundeNo = kundeNo;
    }

    /**
     * @return the rinfoNo
     */
    public Long getRinfoNo() {
        return rinfoNo;
    }

    /**
     * @param rinfoNo the rinfoNo to set
     */
    public void setRinfoNo(Long rinfoNo) {
        this.rinfoNo = rinfoNo;
    }

    /**
     * @return Returns the rinfoNoOrig.
     * @deprecated
     */
    public Long getRinfoNoOrig() {
        return getRinfoNo();
    }

    /**
     * @param billAccountNo The rinfoNoOrig to set.
     * @deprecated
     */
    public void setRinfoNoOrig(Long billAccountNo) {
        setRinfoNo(billAccountNo);
    }

    /**
     * @return Returns the changePW.
     */
    public Boolean getChangePW() {
        return changePW;
    }

    /**
     * @param changePW The changePW to set.
     */
    public void setChangePW(Boolean changePW) {
        this.changePW = changePW;
    }

    /**
     * @return Returns the pwNo.
     */
    public Long getPwNo() {
        return pwNo;
    }

    /**
     * @param loginNo The pwNo to set.
     */
    public void setPwNo(Long loginNo) {
        this.pwNo = loginNo;
    }

    /**
     * @return Returns the password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password The password to set.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return Returns the userId.
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId The userId to set.
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

}
