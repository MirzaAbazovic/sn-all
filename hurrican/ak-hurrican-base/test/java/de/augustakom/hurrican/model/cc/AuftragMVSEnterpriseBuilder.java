/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.01.2012 16:52:59
 */
package de.augustakom.hurrican.model.cc;


@SuppressWarnings("unused")
public class AuftragMVSEnterpriseBuilder extends AuftragMVSBuilder<AuftragMVSEnterpriseBuilder, AuftragMVSEnterprise> {

    private String mail = "test@m-net.de";
    private String domain = "mydomain";
    private Long id;

    public AuftragMVSEnterpriseBuilder withRandomId() {
        this.id = getLongId();
        return this;
    }

    public AuftragMVSEnterpriseBuilder withDomain(String domain) {
        this.domain = domain;
        return this;
    }

    public AuftragMVSEnterpriseBuilder withMail(String mail) {
        this.mail = mail;
        return this;
    }
}


