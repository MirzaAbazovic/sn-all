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
public class AKUserRoleBuilder extends EntityBuilder<AKUserRoleBuilder, AKUserRole> {

    private Long id = null;
    private AKUserBuilder userBuilder = null;
    private AKRoleBuilder roleBuilder = null;


    public AKUserBuilder getUserBuilder() {
        return userBuilder;
    }

    public AKRoleBuilder getRoleBuilder() {
        return roleBuilder;
    }


    public AKUserRoleBuilder withUserBuilder(AKUserBuilder userBuilder) {
        this.userBuilder = userBuilder;
        return this;
    }

    public AKUserRoleBuilder withRoleBuilder(AKRoleBuilder roleBuilder) {
        this.roleBuilder = roleBuilder;
        return this;
    }

    public AKUserRoleBuilder withRandomRole() {
        this.roleBuilder = getBuilder(AKRoleBuilder.class)
                .withRandomName()
                .withApplicationBuilder(getBuilder(AKApplicationBuilder.class).withRandomName());
        return this;
    }

}
