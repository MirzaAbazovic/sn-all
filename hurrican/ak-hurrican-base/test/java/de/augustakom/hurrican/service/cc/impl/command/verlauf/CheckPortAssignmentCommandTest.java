/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.11.2011 09:34:14
 */
package de.augustakom.hurrican.service.cc.impl.command.verlauf;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.service.cc.VoIPService;

/**
 *
 */
@Test(groups = { BaseTest.UNIT })
public class CheckPortAssignmentCommandTest extends BaseTest {

    private VoIPService voipServiceMock;
    private CheckPortAssignmentCommand cut;

    @BeforeMethod
    public void setUp() {
        voipServiceMock = mock(VoIPService.class);
        cut = new CheckPortAssignmentCommand() {
            @Override
            void init() throws ServiceNotFoundException {
                setVoipService(voipServiceMock);
            }
        };
        cut.setVoipService(voipServiceMock);
    }

    //@formatter:off
    @DataProvider(name="testExecuteDP")
    public Object[][] executeDP()   {
        return new Object[][]   {
                new Object[] { new AKWarnings(), ServiceCommandResult.CHECK_STATUS_OK }, // keine Warnungen, kein Fehler
                new Object[] { new AKWarnings().addAKWarning(this, "warning"), ServiceCommandResult.CHECK_STATUS_OK } // eine Warnung, trotzdem kein Fehler, Warnung gespeichert
        };
    }
    //@formatter:on

    @SuppressWarnings("unchecked")
    @Test(dataProvider = "testExecuteDP")
    public void testExecute(AKWarnings akWarnings, int status) throws Exception {
        when(voipServiceMock.validatePortAssignment(any(Collection.class))).thenReturn(akWarnings);
        ServiceCommandResult res = (ServiceCommandResult) cut.execute();
        assertEquals(res.getCheckStatus(), status);
        assertTrue(((akWarnings.isEmpty() == true) && (cut.getWarnings() == null))
                || (akWarnings.isEmpty() == cut.getWarnings().isEmpty()));
    }
}


