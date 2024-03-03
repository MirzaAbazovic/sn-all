/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.07.2010 10:21:18
 */
package de.mnet.migration.common;

import static de.mnet.migration.common.MigrationTransformator.*;
import static org.testng.Assert.*;

import org.testng.annotations.Test;

import de.mnet.migration.common.result.TransformationResult;
import de.mnet.migration.common.result.TransformationStatus;
import de.mnet.migration.common.util.Messages;


/**
 *
 */
public class MigrationTransformatorTest {

    static class ConcreteMigrationTransformator extends MigrationTransformator<Integer> {

        static class MigMessages extends Messages {
            private Messages.Message TEST_MESSAGE = new Messages.Message(
                    TransformationStatus.BAD_DATA, 0x1L,
                    "Dies ist ein Test");
        }

        static MigMessages messages = new MigMessages();

        /**
         * @see de.mnet.migration.common.MigrationTransformator#transform(java.lang.Object)
         */
        @Override
        public TransformationResult transform(Integer row) {
            messages.prepare("Test Transformator: ");
            messages.TEST_MESSAGE.add();
            return null;
        }

    }

    @Test(groups = "unit")
    public void testGetMessagesIfDefined() {
        MigrationTransformator<Integer> transformator = new ConcreteMigrationTransformator();
        transformator.transform(13);
        Messages messages = transformator.getMessagesIfDefined();
        assertNotNull(messages);
    }

    @Test(groups = "unit")
    public void testGetMessagesIfDefinedEvaluate() {
        MigrationTransformator<Integer> transformator = new ConcreteMigrationTransformator();
        transformator.transform(13);
        Messages messages = transformator.getMessagesIfDefined();
        assertNotNull(messages);
        TransformationResult result = messages.evaluate(target("Test"));
        assertTrue(result.getInfoText().contains("Dies ist ein Test"));
    }

}
