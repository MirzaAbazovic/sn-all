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
public class AKApplicationBuilder extends EntityBuilder<AKApplicationBuilder, AKApplication> {
    public static final Long APPLICATION_ID_HURRICAN = Long.valueOf(1);

    private Long id = randomLong(100);
    private String name = "TestApp-1"; // + randomString();
    private String description = "TestApplication is used only in tests and should never stay in the database";


    public AKApplicationBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public AKApplicationBuilder withRandomName() {
        this.name = "TestApp-" + randomString();
        return this;
    }
}
