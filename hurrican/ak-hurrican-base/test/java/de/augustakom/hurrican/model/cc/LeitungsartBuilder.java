/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.04.2012 14:06:04
 */
package de.augustakom.hurrican.model.cc;

/**
 *
 */
public class LeitungsartBuilder extends AbstractCCIDModelBuilder<LeitungsartBuilder, Leitungsart> {

    @SuppressWarnings("unused")
    private String name = randomString(30);

    public LeitungsartBuilder withName(String name) {
        this.name = name;
        return this;
    }
}


