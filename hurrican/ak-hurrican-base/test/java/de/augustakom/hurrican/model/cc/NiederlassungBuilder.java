/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.08.2009 15:42:20
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.common.service.iface.IServiceObject;


/**
 *
 */
@SuppressWarnings("unused")
public class NiederlassungBuilder extends AbstractCCIDModelBuilder<NiederlassungBuilder, Niederlassung> implements IServiceObject {

    private String name = "TestStadt-" + randomString(40);
    private String dispoTeampostfach = "Test@Service.de";
    private Long parentId = null;
    private String dispoPhone = "089 0815";
    private Reference ipLocation = null;

    @Override
    protected void initialize() {
        id = getLongId();
    }

    public NiederlassungBuilder withIpLocation(Reference ipLocation) {
        this.ipLocation = ipLocation;
        return this;
    }

}
