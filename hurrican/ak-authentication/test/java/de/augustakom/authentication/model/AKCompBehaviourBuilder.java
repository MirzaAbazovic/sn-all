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
public class AKCompBehaviourBuilder extends EntityBuilder<AKCompBehaviourBuilder, AKCompBehavior> {

    private Long id = null;
    private AKGUIComponentBuilder componentBuilder = null;
    private AKRoleBuilder roleBuilder = null;
    private boolean visible = true;
    private boolean executable = true;


    public AKCompBehaviourBuilder isExecutable(boolean executable) {
        this.executable = executable;
        return this;
    }

    public AKCompBehaviourBuilder isVisible(boolean visible) {
        this.visible = visible;
        return this;
    }

    public AKCompBehaviourBuilder withRoleBuilder(AKRoleBuilder roleBuilder) {
        this.roleBuilder = roleBuilder;
        return this;
    }

    public AKCompBehaviourBuilder withComponentBuilder(AKGUIComponentBuilder componentBuilder) {
        this.componentBuilder = componentBuilder;
        return this;
    }
}
