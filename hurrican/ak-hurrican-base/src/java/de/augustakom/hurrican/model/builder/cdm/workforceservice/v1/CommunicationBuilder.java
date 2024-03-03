/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.09.2014
 */
package de.augustakom.hurrican.model.builder.cdm.workforceservice.v1;

import java.util.*;

import de.augustakom.hurrican.model.builder.cdm.workforceservice.WorkforceTypeBuilder;
import de.mnet.esb.cdm.resource.workforceservice.v1.ContactPerson;

/**
 *
 */
public class CommunicationBuilder implements WorkforceTypeBuilder<ContactPerson.Communication> {

    private List<String> phone;
    private List<String> mobile;
    private List<String> email;
    private List<String> fax;

    @Override
    public ContactPerson.Communication build() {
        ContactPerson.Communication communication = new ContactPerson.Communication();
        if (null != this.phone) {
            communication.getPhone().addAll(this.phone);
        }
        if (null != this.mobile) {
            communication.getMobile().addAll(this.mobile);
        }
        if (null != this.email) {
            communication.getEmail().addAll(this.email);
        }
        if (null != this.fax) {
            communication.getFax().addAll(this.fax);
        }
        return communication;
    }

    public CommunicationBuilder withPhones(List<String> phones) {
        this.phone = phones;
        return this;
    }

    public CommunicationBuilder addPhone(String phone) {
        if (null == this.phone) {
            this.phone = new ArrayList<String>();
        }
        this.phone.add(phone);
        return this;
    }

    public CommunicationBuilder withMobiles(List<String> mobiles) {
        this.mobile = mobiles;
        return this;
    }

    public CommunicationBuilder addMobile(String mobile) {
        if (null == this.mobile) {
            this.mobile = new ArrayList<String>();
        }
        this.mobile.add(mobile);
        return this;
    }

    public CommunicationBuilder withEmails(List<String> emails) {
        this.email = emails;
        return this;
    }

    public CommunicationBuilder addEmail(String email) {
        if (null == this.email) {
            this.email = new ArrayList<String>();
        }
        this.email.add(email);
        return this;
    }

    public CommunicationBuilder withFax(List<String> fax) {
        this.fax = fax;
        return this;
    }

    public CommunicationBuilder addFax(String fax) {
        if (null == this.fax) {
            this.fax = new ArrayList<String>();
        }
        this.fax.add(fax);
        return this;
    }
}