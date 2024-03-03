/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.02.2010 15:03:12
 */
package de.augustakom.hurrican.model.cc;

import javax.persistence.*;


/**
 * Modell-Klasse fuer die Definition von AuftragMVS Enterprise-Daten.
 */
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS",
        justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@DiscriminatorValue("Enterprise")
public class AuftragMVSEnterprise extends AuftragMVS {

    private static final long serialVersionUID = 2761624828952922303L;
    private String mail;
    private String domain;

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Override
    public String toString() {
        return "AuftragMVSEnterprise [mail=" + mail + ", domain=" + domain + ", getAuftragId()=" + getAuftragId()
                + ", getUserName()=" + getUserName() + ", getPassword()=" + getPassword() + ", getId()=" + getId()
                + "]";
    }

}


