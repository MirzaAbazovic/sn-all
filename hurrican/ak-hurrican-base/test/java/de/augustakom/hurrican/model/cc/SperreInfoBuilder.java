/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.08.2009 12:08:05
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.common.service.iface.IServiceObject;


/**
 *
 */
@SuppressWarnings("unused")
public class SperreInfoBuilder extends AbstractCCIDModelBuilder<SperreInfoBuilder, SperreInfo> implements
        IServiceObject {
    private AbteilungBuilder abteilungBuilder = null;
    private String email = "SperreTest@email.com";
    private Boolean active = true;


    public SperreInfoBuilder withAbteilungBuilder(AbteilungBuilder abteilungBuilder) {
        this.abteilungBuilder = abteilungBuilder;
        return this;
    }

    public SperreInfoBuilder withEmail(String email) {
        this.email = email;
        return this;
    }


}
