/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.01.2012 16:52:59
 */
package de.augustakom.hurrican.model.cc;


@SuppressWarnings("unused")
public class AuftragMVSSiteBuilder extends AuftragMVSBuilder<AuftragMVSSiteBuilder, AuftragMVSSite> {

    private String subdomain = "mysubdomain";
    private AuftragMVSEnterprise parent;

    private AuftragMVSEnterpriseBuilder parentBuilder = getBuilder(AuftragMVSEnterpriseBuilder.class);

    public AuftragMVSSiteBuilder withAuftragMVSEnterpriseBuilder(AuftragMVSEnterpriseBuilder auftragMVSEnterpriseBuilder) {
        this.parentBuilder = auftragMVSEnterpriseBuilder;
        return this;
    }

    public AuftragMVSSiteBuilder withSubdomain(String subdomain) {
        this.subdomain = subdomain;
        return this;
    }
}


