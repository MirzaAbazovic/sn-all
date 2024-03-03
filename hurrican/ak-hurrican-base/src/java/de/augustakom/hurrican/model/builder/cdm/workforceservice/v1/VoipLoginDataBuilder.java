/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.02.2015 11:17
 */
package de.augustakom.hurrican.model.builder.cdm.workforceservice.v1;

import de.augustakom.hurrican.model.builder.cdm.workforceservice.WorkforceTypeBuilder;
import de.mnet.esb.cdm.resource.workforceservice.v1.OrderTechnicalParams;

/**
 *
 */
public class VoipLoginDataBuilder implements WorkforceTypeBuilder<OrderTechnicalParams.DialNumber.VoIPLogin>  {

    private String sipLogin;
    private String sipPassword;
    private String sipMainNumber;

    @Override
    public OrderTechnicalParams.DialNumber.VoIPLogin build() {
        final OrderTechnicalParams.DialNumber.VoIPLogin voipLogin = new OrderTechnicalParams.DialNumber.VoIPLogin();
        voipLogin.setSIPMainNumber(this.sipMainNumber);
        voipLogin.setSIPLogin(this.sipLogin);
        voipLogin.setSIPPassword(this.sipPassword);
        return voipLogin;
    }

    public VoipLoginDataBuilder withSipLogin(final String sipLogin) {
        this.sipLogin = sipLogin;
        return this;
    }

    public VoipLoginDataBuilder withSipPassword(final String sipPassword)   {
        this.sipPassword = sipPassword;
        return this;
    }

    public VoipLoginDataBuilder withSipMainNumber(final String sipMainNumber)   {
        this.sipMainNumber = sipMainNumber;
        return this;
    }
}
