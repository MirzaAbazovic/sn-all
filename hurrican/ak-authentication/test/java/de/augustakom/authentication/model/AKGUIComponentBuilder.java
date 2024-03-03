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
public class AKGUIComponentBuilder extends EntityBuilder<AKGUIComponentBuilder, AKGUIComponent> {

    private Long id = null;
    private String name = "TestGUIComponent-" + randomString(20);
    private String parent = "de.mnet.hurrican.gui.test.TestFrame";
    private String type = "Button";
    private String description = "Test Button";
    private AKApplicationBuilder applicationBuilder = null;


    public AKGUIComponentBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public AKGUIComponentBuilder withRandomName() {
        this.name = "TestGUIComponent-" + randomString();
        return this;
    }

    public AKGUIComponentBuilder withApplicationBuilder(AKApplicationBuilder applicationBuilder) {
        this.applicationBuilder = applicationBuilder;
        return this;
    }
}
