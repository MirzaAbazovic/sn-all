/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.03.2009 16:17:20
 */
package de.augustakom.hurrican.model.cc;

import org.apache.commons.lang.StringUtils;

import de.augustakom.common.tools.lang.StringTools;


/**
 * Modell-Klasse zur Abbildung eines externen Dienstleisters.
 *
 *
 */
public class ExtServiceProvider extends AbstractCCIDModel {

    /**
     * Referenz-ID fuer Email-Beauftragung
     */
    public static final Integer REF_ID_CONTACT_EMAIL = 15500;
    /**
     * Referenz-Id fuer die manuelle Beauftragung
     */
    public static final Integer REF_ID_CONTACT_MAN = 15501;

    private String name = null;
    private String firstname = null;
    private String street = null;
    private String houseNum = null;
    private String postalCode = null;
    private String city = null;
    private String phone = null;
    private String email = null;
    private String fax = null;
    private Integer contactType = null;

    /**
     * Gibt eine Kombination von 'name' und 'firstname' zurueck. Das Result ist auf 40 Zeichen begrenzt!
     *
     * @return Kombination von 'name' und 'firstname'
     *
     */
    public String getShortName() {
        String shortName = StringTools.join(new String[] { getName(), getFirstname() }, " ", true);
        return StringUtils.abbreviate(shortName, 40);
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Returns the firstname.
     */
    public String getFirstname() {
        return firstname;
    }

    /**
     * @param firstname The firstname to set.
     */
    public void setFirstname(String firstname) {
        this.firstname = firstname;
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
     * @return Returns the phone.
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @param phone The phone to set.
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * @return Returns the email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email The email to set.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return Returns the fax.
     */
    public String getFax() {
        return fax;
    }

    /**
     * @param fax The fax to set.
     */
    public void setFax(String fax) {
        this.fax = fax;
    }

    /**
     * @return Returns the contactType.
     */
    public Integer getContactType() {
        return contactType;
    }

    /**
     * @param contactType The contactType to set.
     */
    public void setContactType(Integer contactType) {
        this.contactType = contactType;
    }

}


