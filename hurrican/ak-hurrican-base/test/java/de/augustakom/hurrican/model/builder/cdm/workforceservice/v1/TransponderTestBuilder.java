/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.02.2015
 */
package de.augustakom.hurrican.model.builder.cdm.workforceservice.v1;

/**
 * Created by glinkjo on 06.02.2015.
 */
public class TransponderTestBuilder extends TransponderBuilder {

    public TransponderTestBuilder() {
        withId(100L);
        withFirstName("Max");
        withLastName("Mustermann");
        withGroup("group");
    }

}
