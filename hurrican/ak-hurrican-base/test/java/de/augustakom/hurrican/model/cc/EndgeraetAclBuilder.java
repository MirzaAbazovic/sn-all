/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.10.2009 10:58:58
 */
package de.augustakom.hurrican.model.cc;

/**
 * Builder fuer Endgeraet-Acl-Objekte.
 *
 *
 */
@SuppressWarnings("unused")
public class EndgeraetAclBuilder extends AbstractCCIDModelBuilder<EndgeraetAclBuilder, EndgeraetAcl> {
    private String name = "ACL_54_TEST_USER (192.168.1.1)";
    private String routerTyp;

    public EndgeraetAclBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public EndgeraetAclBuilder withRouterTyp(String routerTyp) {
        this.routerTyp = routerTyp;
        return this;
    }

}
