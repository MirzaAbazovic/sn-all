/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.08.2009 11:22:34
 */
package de.augustakom.authentication.model;

import de.augustakom.common.model.EntityBuilder;

/**
 *
 */
@SuppressWarnings("unused")
public class AKRoleBuilder extends EntityBuilder<AKRoleBuilder, AKRole> {

    private Long id = null;
    private String name = "TestRole-" + randomString();
    private String description = "This Role is used only for tests and should never stay in the database";
    private AKApplicationBuilder applicationBuilder = null;
    private boolean admin = false;


    public AKApplicationBuilder getApplicationBuilder() {
        return applicationBuilder;
    }

    public AKRoleBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public AKRoleBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public AKRoleBuilder withRandomName() {
        this.name = "TestRole-" + randomString();
        return this;
    }

    public AKRoleBuilder isAdmin(boolean admin) {
        this.admin = admin;
        return this;
    }

    public AKRoleBuilder withApplicationBuilder(AKApplicationBuilder applicationBuilder) {
        this.applicationBuilder = applicationBuilder;
        return this;
    }
}
