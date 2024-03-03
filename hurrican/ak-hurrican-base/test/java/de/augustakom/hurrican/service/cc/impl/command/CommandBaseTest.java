package de.augustakom.hurrican.service.cc.impl.command;

import static org.testng.Assert.*;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.base.ServiceCommandResult;

/**
 * CommandBaseTest
 */
public abstract class CommandBaseTest extends BaseTest {

    protected void assertCommandResult(Object cmdResult, int checkStatus, String message, Class<?> cmdClass) {
        if (cmdResult instanceof ServiceCommandResult) {
            ServiceCommandResult result = (ServiceCommandResult) cmdResult;
            assertEquals(result.getCheckStatus(), checkStatus);
            assertEquals(result.getMessage(), message);
            assertEquals(result.getCommandClass(), cmdClass);
        }
        else {
            fail("Result of command is not of type " + ServiceCommandResult.class);
        }
    }
}
