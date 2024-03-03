/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.07.2008 11:08:26
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.common.tools.lang.StringTools;


/**
 * Modell zur Abbildung von Daten zur Kontaktierung eines Carriers.
 *
 *
 */
public class CarrierContact extends AbstractCCIDModel {

    public static final Long TYP_ALLGEMEIN = 23000L;
    public static final Long TYP_WBCI_CONTACT = 23001L;
    public static final Long TYP_WITA_CONTACT = 23002L;
    public static final Long TYP_WBCI_ESCALATION_CONTACT = 23004L;

    private Long carrierId = null;
    private String branchOffice = null;
    private String ressort = null;
    private String contactName = null;
    private String street = null;
    private String houseNum = null;
    private String postalCode = null;
    private String city = null;
    private String faultClearingPhone = null;
    private String faultClearingFax = null;
    private String faultClearingEmail = null;
    private String userW = null;
    private Long contactType;

    /**
     * Gibt die Felder 'branchOffice' und 'ressort' kombiniert zurueck.
     *
     * @return Kombination aus 'branchOffice' und 'ressort' (mit ' - ' getrennt)
     */
    public String getDescription() {
        return StringTools.join(new String[] { getBranchOffice(), getRessort() }, " - ", true);
    }

    /**
     * @return Returns the branchOffice.
     */
    public String getBranchOffice() {
        return branchOffice;
    }

    /**
     * @param branchOffice The branchOffice to set.
     */
    public void setBranchOffice(String branchOffice) {
        this.branchOffice = branchOffice;
    }

    /**
     * @return Returns the ressort.
     */
    public String getRessort() {
        return ressort;
    }

    /**
     * @param ressort The ressort to set.
     */
    public void setRessort(String ressort) {
        this.ressort = ressort;
    }

    /**
     * @return Returns the contactName.
     */
    public String getContactName() {
        return contactName;
    }

    /**
     * @param contactName The contactName to set.
     */
    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    /**
     * @return Returns the street.
     */
    public String getStreet() {
        return street;
    }

    /**
     * @param street The street to set.
     */
    public void setStreet(String street) {
        this.street = street;
    }

    /**
     * @return Returns the houseNum.
     */
    public String getHouseNum() {
        return houseNum;
    }

    /**
     * @param houseNum The houseNum to set.
     */
    public void setHouseNum(String houseNum) {
        this.houseNum = houseNum;
    }

    /**
     * @return Returns the postalCode.
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * @param postalCode The postalCode to set.
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * @return Returns the city.
     */
    public String getCity() {
        return city;
    }

    /**
     * @param city The city to set.
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * @return Returns the faultClearingPhone.
     */
    public String getFaultClearingPhone() {
        return faultClearingPhone;
    }

    /**
     * @param faultClearingPhone The faultClearingPhone to set.
     */
    public void setFaultClearingPhone(String faultClearingPhone) {
        this.faultClearingPhone = faultClearingPhone;
    }

    /**
     * @return Returns the faultClearingFax.
     */
    public String getFaultClearingFax() {
        return faultClearingFax;
    }

    /**
     * @param faultClearingFax The faultClearingFax to set.
     */
    public void setFaultClearingFax(String faultClearingFax) {
        this.faultClearingFax = faultClearingFax;
    }

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
     * @return the faultClearingEmail
     */
    public String getFaultClearingEmail() {
        return faultClearingEmail;
    }

    /**
     * @param faultClearingEmail the faultClearingEmail to set
     */
    public void setFaultClearingEmail(String faultClearingEmail) {
        this.faultClearingEmail = faultClearingEmail;
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

    public Long getContactType() {
        return contactType;
    }

    public void setContactType(Long contactType) {
        this.contactType = contactType;
    }
}
