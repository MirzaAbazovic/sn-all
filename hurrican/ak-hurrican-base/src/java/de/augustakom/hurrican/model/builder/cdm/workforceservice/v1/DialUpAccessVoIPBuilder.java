/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.06.2015
 */
package de.augustakom.hurrican.model.builder.cdm.workforceservice.v1;

import de.augustakom.hurrican.model.builder.cdm.workforceservice.WorkforceTypeBuilder;
import de.mnet.esb.cdm.resource.workforceservice.v1.OrderTechnicalParams;

/**
 *
 */
public class DialUpAccessVoIPBuilder implements WorkforceTypeBuilder<OrderTechnicalParams.DialUpAccessVoIP> {

    private String account;
    private String password;

    @Override
    public OrderTechnicalParams.DialUpAccessVoIP build() {
        OrderTechnicalParams.DialUpAccessVoIP dialUpAccessVoIP = new OrderTechnicalParams.DialUpAccessVoIP();
        dialUpAccessVoIP.setAccount(this.account);
        dialUpAccessVoIP.setPassword(this.password);
        return dialUpAccessVoIP;
    }

    public DialUpAccessVoIPBuilder withAccount(String account) {
        this.account = account;
        return this;
    }

    public DialUpAccessVoIPBuilder withPassword(String password) {
        this.password = password;
        return this;
    }

}
