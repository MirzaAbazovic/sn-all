/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.04.2012 14:09:01
 */
package de.augustakom.hurrican.model.cc;

/**
 *
 */
public class SchnittstelleBuilder extends AbstractCCIDModelBuilder<SchnittstelleBuilder, Schnittstelle> {

    @SuppressWarnings("unused")
    private String schnittstelle = randomString(20);

    public SchnittstelleBuilder withSchnittstelle(String schnittstelle) {
        this.schnittstelle = schnittstelle;
        return this;
    }
}


