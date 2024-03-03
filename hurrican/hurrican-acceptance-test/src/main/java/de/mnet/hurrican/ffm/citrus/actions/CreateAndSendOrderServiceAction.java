/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.09.2014
 */
package de.mnet.hurrican.ffm.citrus.actions;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.service.cc.ffm.FFMService;
import de.mnet.hurrican.ffm.citrus.VariableNames;

/**
 * Test action calls remote Hurrican service for creating and sending a new FFM order. Action is able to expect some
 * service exception when calling the remote service in error situations.
 *
 *
 */
public class CreateAndSendOrderServiceAction extends AbstractFFMAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateAndSendOrderServiceAction.class);
    /**
     * Model object to create order from
     */
    private final Verlauf bauauftrag;
    /**
     * Optional expected exception when calling remote service
     */
    private Class<? extends Exception> expectedException = null;

    public CreateAndSendOrderServiceAction(FFMService ffmService, Verlauf bauauftrag) {
        super("createAndSendOrder", ffmService);
        this.bauauftrag = bauauftrag;
    }

    @Override
    public void doExecute(TestContext context) {
        if (bauauftrag == null) {
            throw new CitrusRuntimeException("No proper Bauauftrag defined for send order action");
        }

        try {
            String orderId = ffmService.createAndSendOrder(bauauftrag);
            context.setVariable(VariableNames.WORKFROCE_ORDER_ID, orderId);
        }
        catch (Exception e) {
            if (expectedException != null) {
                LOGGER.error("check expected exception '{}':", expectedException.getSimpleName(), e);
                Assert.assertEquals(e.getClass(), expectedException, "expected an exception of typ " + expectedException.getSimpleName());
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
     *
     * @param exceptionClass
     * @return
     */
    public CreateAndSendOrderServiceAction expectException(Class<? extends Exception> exceptionClass) {
        this.expectedException = exceptionClass;
        return this;
    }
}
