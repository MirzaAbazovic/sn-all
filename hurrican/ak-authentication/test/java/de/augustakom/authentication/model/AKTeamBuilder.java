/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.06.2012 08:35:38
 */
package de.augustakom.authentication.model;

import de.augustakom.common.model.EntityBuilder;

/**
 *
 */
@SuppressWarnings("unused")
public class AKTeamBuilder extends EntityBuilder<AKTeamBuilder, AKTeam> {

    private Long id;
    private String name = randomString();

    public AKTeamBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public AKTeamBuilder withName(String name) {
        this.name = name;
        return this;
    }
}


