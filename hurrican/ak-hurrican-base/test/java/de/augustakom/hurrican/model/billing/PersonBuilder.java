/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.10.2009 13:50:55
 */
package de.augustakom.hurrican.model.billing;

import java.util.*;

@SuppressWarnings("unused")
public class PersonBuilder extends BillingEntityBuilder<PersonBuilder, Person> {

    private Long personNo;
    private String geschlecht;
    private String name;
    private String vorname;
    private String language;
    private Date dateW;
    private String userW;

    public PersonBuilder withPersonNo(Long personNo) {
        this.personNo = personNo;
        return this;
    }

    public PersonBuilder withGeschlecht(String geschlecht) {
        this.geschlecht = geschlecht;
        return this;
    }

    public PersonBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public PersonBuilder withVorname(String vorname) {
        this.vorname = vorname;
        return this;
    }

    public PersonBuilder withLanguage(String language) {
        this.language = language;
        return this;
    }
}


