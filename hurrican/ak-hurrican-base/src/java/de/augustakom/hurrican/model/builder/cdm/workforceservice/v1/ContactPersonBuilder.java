/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.09.2014
 */
package de.augustakom.hurrican.model.builder.cdm.workforceservice.v1;

import de.augustakom.hurrican.model.builder.cdm.workforceservice.WorkforceTypeBuilder;
import de.mnet.esb.cdm.resource.workforceservice.v1.ContactPerson;

/**
 *
 */
public class ContactPersonBuilder implements WorkforceTypeBuilder<ContactPerson> {

    protected String role;
    protected String gender;
    protected String firstName;
    protected String lastName;
    protected ContactPerson.Communication communication;

    @Override
    public ContactPerson build() {
        ContactPerson cp = new ContactPerson();
        cp.setRole(this.role);
        cp.setGender(this.gender);
        cp.setFirstName(this.firstName);
        cp.setLastName(this.lastName);
        cp.setCommunication(this.communication);
        return cp;
    }

    public ContactPersonBuilder withRole(String role) {
        this.role = role;
        return this;
    }

    public ContactPersonBuilder withGender(String gender) {
        this.gender = gender;
        return this;
    }

    public ContactPersonBuilder withLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public ContactPersonBuilder withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public ContactPersonBuilder withCommunication(ContactPerson.Communication communication) {
        this.communication = communication;
        return this;
    }
}