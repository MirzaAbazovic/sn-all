/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.07.2010 17:41:59
 */
package de.mnet.migration.common.util;

import static org.testng.Assert.*;

import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import de.mnet.migration.base.MigrationBaseTest;
import de.mnet.migration.common.MigrationTransformator;
import de.mnet.migration.common.result.TransformationResult;
import de.mnet.migration.common.result.TransformationStatus;


/**
 *
 */
public class MessagesTest extends MigrationBaseTest {
    private static final Logger LOGGER = Logger.getLogger(MessagesTest.class);

    static class TestMessages extends Messages {

        private Messages.Message PRIVATE_MESSAGE_FOUND = new Messages.Message(
                TransformationStatus.WARNING, 0x01L,
                "Private message has been found!");
    }

    static TestMessages messages = new TestMessages();

    @Test(groups = "unit")
    public void testFindPrivate() {
        messages.prepare("");
        messages.PRIVATE_MESSAGE_FOUND.add();
        TransformationResult result = messages.evaluate(MigrationTransformator.target("Test"));
        LOGGER.debug(result.getInfoText());
        assertTrue(result.getInfoText().contains("Private message"));
    }

    @Test(groups = "unit")
    public void testFindSuperclass() {
        messages.prepare("");
        messages.ERROR.add("Test");
        TransformationResult result = messages.evaluate(MigrationTransformator.target("Test"));
        LOGGER.debug(result.getInfoText());
        assertTrue(result.getInfoText().contains("Error in Transformation"));
    }

}
