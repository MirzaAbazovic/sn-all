/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.09.14
 */
package de.mnet.hurrican.ffm.citrus.actions;

import com.consol.citrus.actions.AbstractTestAction;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;

import de.augustakom.hurrican.service.cc.ffm.FFMService;

/**
 * Abstract FFM test action provides access to specific test data.
 *
 *
 */
public abstract class AbstractFFMAction extends AbstractTestAction {

    protected FFMService ffmService;

    /**
     * Constructor setting the action name field.
     *
     * @param actionName
     */
    public AbstractFFMAction(String actionName) {
        setName(actionName);
    }

    /**
     * Constructor setting the action name field and assign the {@link FFMService}.
     *
     * @param actionName
     * @param ffmService
     */
    public AbstractFFMAction(String actionName, FFMService ffmService) {
        this(actionName);
        this.ffmService = ffmService;
    }

    @Override
    public void execute(TestContext context) {
        try {
            super.execute(context);
        }
        catch (Error e) {  // NOSONAR squid:S1181 ; errors should cause to fail the test!
            throw new CitrusRuntimeException(e.getMessage(), e);
        }
    }
}
