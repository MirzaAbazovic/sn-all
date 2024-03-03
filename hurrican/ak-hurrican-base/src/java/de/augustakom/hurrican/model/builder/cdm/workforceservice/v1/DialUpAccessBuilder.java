/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.09.2014
 */
package de.augustakom.hurrican.model.builder.cdm.workforceservice.v1;

import de.augustakom.hurrican.model.builder.cdm.workforceservice.WorkforceTypeBuilder;
import de.mnet.esb.cdm.resource.workforceservice.v1.OrderTechnicalParams;

/**
 *
 */
public class DialUpAccessBuilder implements WorkforceTypeBuilder<OrderTechnicalParams.DialUpAccess> {

    private String accountId;
    private String password;
    private String realm;

    @Override
    public OrderTechnicalParams.DialUpAccess build() {
        OrderTechnicalParams.DialUpAccess dialUpAccess = new OrderTechnicalParams.DialUpAccess();
        dialUpAccess.setAccountId(this.accountId);
        dialUpAccess.setPassword(this.password);
        dialUpAccess.setRealm(this.realm);
        return dialUpAccess;
    }

    public DialUpAccessBuilder withAccountId(String accountId) {
        this.accountId = accountId;
        return this;
    }

    public DialUpAccessBuilder withPassword(String password) {
        this.password = password;
        return this;
    }

    public DialUpAccessBuilder withRealm(final String realm)    {
        this.realm = realm;
        return this;
    }
}
