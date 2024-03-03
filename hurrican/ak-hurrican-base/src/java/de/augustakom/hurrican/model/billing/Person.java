/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.09.2004 07:41:00
 */
package de.augustakom.hurrican.model.billing;


/**
 * Modell fuer die Abbildung einer Person aus dem Billing-System.
 *
 *
 */
public class Person extends AbstractBillingModel {

    private static final long serialVersionUID = -7768652941540988341L;

    private Long personNo;
    private String geschlecht;
    private String name;
    private String vorname;
    private String language;

    public Long getPersonNo() {
        return personNo;
    }

    public void setPersonNo(Long personNo) {
        this.personNo = personNo;
    }

    public String getGeschlecht() {
        return geschlecht;
    }

    public void setGeschlecht(String geschlecht) {
        this.geschlecht = geschlecht;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVorname() {
        return vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

}


