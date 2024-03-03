/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.06.2005 11:09:14
 */
package de.augustakom.hurrican.model.billing;


/**
 * Modell zur Abbildung einer RechnungsInfo.
 *
 *
 */
public class RInfo extends AbstractBillingModel {

    private static final long serialVersionUID = -3007597982616555071L;

    private Long rinfoNo = null;
    private String description = null;
    private Long kundeNo = null;
    private Long adresseNo = null;
    private String extDebitorId = null;
    private Boolean invElectronic = null;
    private Boolean invMaxi = null;
    private Long finanzNo = null;

    /**
     * @return Returns the adresseNo.
     */
    public Long getAdresseNo() {
        return adresseNo;
    }

    /**
     * @param adresseNo The adresseNo to set.
     */
    public void setAdresseNo(Long adresseNo) {
        this.adresseNo = adresseNo;
    }

    /**
     * @return Returns the description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return Returns the kundeNo.
     */
    public Long getKundeNo() {
        return kundeNo;
    }

    /**
     * @param kundeNo The kundeNo to set.
     */
    public void setKundeNo(Long kundeNo) {
        this.kundeNo = kundeNo;
    }

    /**
     * @return Returns the rinfoNo.
     */
    public Long getRinfoNo() {
        return rinfoNo;
    }

    /**
     * @param rinfoNo The rinfoNo to set.
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
     * @param rinfoNoOrig The rinfoNoOrig to set.
     * @deprecated
     */
    public void setRinfoNoOrig(Long rinfoNoOrig) {
        this.setRinfoNo(rinfoNoOrig);
    }

    /**
     * @return Returns the invElectronic.
     */
    public Boolean getInvElectronic() {
        return this.invElectronic;
    }

    /**
     * @param invElectronic The invElectronic to set.
     */
    public void setInvElectronic(Boolean invElectronic) {
        this.invElectronic = invElectronic;
    }

    /**
     * @return Returns the invMaxi.
     */
    public Boolean getInvMaxi() {
        return this.invMaxi;
    }

    /**
     * @param invMaxi The invMaxi to set.
     */
    public void setInvMaxi(Boolean invMaxi) {
        this.invMaxi = invMaxi;
    }

    /**
     * @return Returns the extDebitorId.
     */
    public String getExtDebitorId() {
        return this.extDebitorId;
    }

    /**
     * @param extDebitorId The extDebitorId to set.
     */
    public void setExtDebitorId(String extDebitorId) {
        this.extDebitorId = extDebitorId;
    }

    /**
     * @return finanzNo
     */
    public Long getFinanzNo() {
        return finanzNo;
    }

    /**
     * @param finanzNo Festzulegender finanzNo
     */
    public void setFinanzNo(Long finanzNo) {
        this.finanzNo = finanzNo;
    }


}


