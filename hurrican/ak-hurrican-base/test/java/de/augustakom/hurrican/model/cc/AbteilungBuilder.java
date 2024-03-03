/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.08.2009 15:37:48
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.hurrican.service.cc.ICCService;


/**
 *
 */
@SuppressWarnings("unused")
public class AbteilungBuilder extends AbstractCCIDModelBuilder<AbteilungBuilder, Abteilung> implements ICCService {
    {id = getLongId();}

    private String name = "TestAbteilung";
    private NiederlassungBuilder niederlassungBuilder = null;
    private Boolean relevant4Proj = null;
    private Boolean relevant4Ba = null;
    private Boolean valid4UniversalGui = Boolean.FALSE;


    public NiederlassungBuilder getNiederlassungBuilder() {
        return niederlassungBuilder;
    }

    /**
     * @param name the name to set
     */
    public AbteilungBuilder withName(String name) {
        this.name = name;
        return this;
    }


    public AbteilungBuilder withNiederlassungBuilder(NiederlassungBuilder niederlassungBuilder) {
        this.niederlassungBuilder = niederlassungBuilder;
        return this;
    }

}
