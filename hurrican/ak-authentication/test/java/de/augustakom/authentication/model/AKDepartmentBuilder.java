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
public class AKDepartmentBuilder extends EntityBuilder<AKDepartmentBuilder, AKDepartment> {

    private Long id = null;
    private String name = "TEST-" + randomString();
    private String description = "Department for test cases";

    public AKDepartmentBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public AKDepartmentBuilder withDescription(String description) {
        this.description = description;
        return this;
    }
}
