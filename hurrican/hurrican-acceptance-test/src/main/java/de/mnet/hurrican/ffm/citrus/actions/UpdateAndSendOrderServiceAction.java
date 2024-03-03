/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.02.2015
 */
package de.mnet.hurrican.ffm.citrus.actions;

import static org.testng.Assert.*;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.service.cc.ffm.FFMService;

/**
 * Test action calls remote Hurrican service for updating and sending a FFM order.
 * Action is able to expect some service exception when calling the remote service in error situations.
 */
public class UpdateAndSendOrderServiceAction extends AbstractFFMAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateAndSendOrderServiceAction.class);
    /**
     * Model object to create order from
     */
    private final Verlauf bauauftrag;
    /** Optional expected exception when calling remote service */
    private Class<? extends Exception> expectedException = null;
    private String expectedExceptionMesssage;

    public UpdateAndSendOrderServiceAction(FFMService ffmService, Verlauf bauauftrag) {
        super("createAndSendOrder", ffmService);
        this.bauauftrag = bauauftrag;
    }

    @Override
    public void doExecute(TestContext context) {
        if (bauauftrag == null) {
            throw new CitrusRuntimeException("No proper Bauauftrag defined for send order action");
        }

        try {
            ffmService.updateAndSendOrder(bauauftrag);
        } catch (Exception e) {
            if (expectedException != null) {
                LOGGER.error("check expected exception '{}':", expectedException.getSimpleName(), e);
                assertEquals(e.getClass(), expectedException, "expected an exception of typ " + expectedException.getSimpleName());
                assertEquals(e.getMessage(), expectedExceptionMesssage, "expected Exception message is not equal");
                return;
            }
            else {
                throw new CitrusRuntimeException("Failed to call ffm service operation", e);
            }
        }

        if (expectedException != null) {
            throw new CitrusRuntimeException("Expected exception not thrown!");
        }
    }

    /**
     * Adds optional expected exception in builder pattern style.
     * @param exceptionClass
     * @return
     */
    public UpdateAndSendOrderServiceAction expectException(Class<? extends Exception> exceptionClass) {
        this.expectedException = exceptionClass;
        return this;
    }

    public UpdateAndSendOrderServiceAction expectExceptionMessage(String exceptionMessage) {
        this.expectedExceptionMesssage = exceptionMessage;
        return this;
    }
}
