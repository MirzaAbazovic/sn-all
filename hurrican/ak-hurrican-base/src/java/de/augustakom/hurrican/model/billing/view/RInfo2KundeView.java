/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.02.2007 12:23:58
 */
package de.augustakom.hurrican.model.billing.view;

import org.apache.commons.lang.StringUtils;

import de.augustakom.hurrican.model.billing.AbstractBillingModel;
import de.augustakom.hurrican.model.billing.Kunde;


/**
 * View-Modell zur Abbildung einer RInfo zum Kunden.
 *
 *
 */
public class RInfo2KundeView extends AbstractBillingModel {

    private Long rinfoNo = null;
    private Long rinfoNoOrig = null;
    private String extDebitorId = null;
    private Long kundeNo = null;
    private Long resellerKundeNo = null;
    private Long areaNo = null;
    private Boolean invMaxi = null;
    private String addressName = null;
    private String addressVorname = null;
    private String addressStrasse = null;
    private String addressNr = null;
    private String addressPLZ = null;
    private String addressPostfach = null;
    private String addressOrt = null;

    /**
     * Ermittelt anhand des Resellers die SAP-Debitorennummer. <br> Bei Reseller 'M-net' ist dies die 'extDebitorId',
     * sonst die 'rinfoNoOrig'.
     *
     * @return
     *
     */
    public String getSAPDebNrByReseller() {
        String account = (Kunde.isResellerMnet(getResellerKundeNo()))
                ? getExtDebitorId() : "" + getRinfoNoOrig();
        return account;
    }

    /**
     * Gibt entweder das Postfach oder die PLZ zurueck - abhaengig davon, was gesetzt ist. <br> Die PLZ hat Vorrang!
     *
     * @return
     *
     */
    public String getPostfachOrPLZ() {
        return (StringUtils.isNotBlank(getAddressPLZ())) ? getAddressPLZ() : getAddressPostfach();
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
     * @return Returns the kundeNo.
     */
    public Long getKundeNo() {
        return this.kundeNo;
    }

    /**
     * @param kundeNo The kundeNo to set.
     */
    public void setKundeNo(Long kundeNo) {
        this.kundeNo = kundeNo;
    }

    /**
     * @return Returns the resellerKundeNo.
     */
    public Long getResellerKundeNo() {
        return this.resellerKundeNo;
    }

    /**
     * @param resellerKundeNo The resellerKundeNo to set.
     */
    public void setResellerKundeNo(Long resellerKundeNo) {
        this.resellerKundeNo = resellerKundeNo;
    }

    /**
     * @return Returns the rinfoNo.
     */
    public Long getRinfoNo() {
        return this.rinfoNo;
    }

    /**
     * @param rinfo The rinfoNo to set.
     */
    public void setRinfoNo(Long rinfoNo) {
        this.rinfoNo = rinfoNo;
    }

    /**
     * @return Returns the rinfoNoOrig.
     */
    public Long getRinfoNoOrig() {
        return this.rinfoNoOrig;
    }

    /**
     * @param rinfoNoOrig The rinfoNoOrig to set.
     */
    public void setRinfoNoOrig(Long rinfoNoOrig) {
        this.rinfoNoOrig = rinfoNoOrig;
    }

    /**
     * @return Returns the areaNo.
     */
    public Long getAreaNo() {
        return this.areaNo;
    }

    /**
     * @param areaNo The areaNo to set.
     */
    public void setAreaNo(Long areaNo) {
        this.areaNo = areaNo;
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
     * @return Returns the addressName.
     */
    public String getAddressName() {
        return this.addressName;
    }

    /**
     * @param addressName The addressName to set.
     */
    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    /**
     * @return Returns the addressNr.
     */
    public String getAddressNr() {
        return this.addressNr;
    }

    /**
     * @param addressNr The addressNr to set.
     */
    public void setAddressNr(String addressNr) {
        this.addressNr = addressNr;
    }

    /**
     * @return Returns the addressOrt.
     */
    public String getAddressOrt() {
        return this.addressOrt;
    }

    /**
     * @param addressOrt The addressOrt to set.
     */
    public void setAddressOrt(String addressOrt) {
        this.addressOrt = addressOrt;
    }

    /**
     * @return Returns the addressPLZ.
     */
    public String getAddressPLZ() {
        return this.addressPLZ;
    }

    /**
     * @param addressPLZ The addressPLZ to set.
     */
    public void setAddressPLZ(String addressPLZ) {
        this.addressPLZ = addressPLZ;
    }

    /**
     * @return Returns the addressStrasse.
     */
    public String getAddressStrasse() {
        return this.addressStrasse;
    }

    /**
     * @param addressStrasse The addressStrasse to set.
     */
    public void setAddressStrasse(String addressStrasse) {
        this.addressStrasse = addressStrasse;
    }

    /**
     * @return Returns the addressVorname.
     */
    public String getAddressVorname() {
        return this.addressVorname;
    }

    /**
     * @param addressVorname The addressVorname to set.
     */
    public void setAddressVorname(String addressVorname) {
        this.addressVorname = addressVorname;
    }

    /**
     * @return Returns the addressPostfach.
     */
    public String getAddressPostfach() {
        return this.addressPostfach;
    }

    /**
     * @param addressPostfach The addressPostfach to set.
     */
    public void setAddressPostfach(String addressPostfach) {
        this.addressPostfach = addressPostfach;
    }

}


