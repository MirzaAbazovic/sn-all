/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.08.2015
 */
package de.mnet.wita.citrus.actions;

import com.consol.citrus.actions.AbstractTestAction;

/**
 *
 */
public abstract class AbstractWitaTestAction extends AbstractTestAction {

    /**
     * Constructor setting the action name field.
     * @param actionName
     */
    public AbstractWitaTestAction(String actionName) {
        setName(actionName);
    }
}
