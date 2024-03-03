/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.11.13 08:58
 */
package de.augustakom.hurrican.service.cc.impl.command.verlauf;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.service.cc.CCAuftragService;

/**
 *
 */
@Test(groups = { BaseTest.UNIT })
public class CheckOnkzAsbAvailableCommandTest extends BaseTest {

    @Mock
    CCAuftragService auftragService;

    @InjectMocks
    CheckOnkzAsbAvailableCommand cut;

    @BeforeMethod
    void setUp() {
        cut = new CheckOnkzAsbAvailableCommand();
        initMocks(this);
    }

    public void testExecuteWithInvalidOnkz() throws Exception {
        final long auftragId = 1234L;
        cut.prepare(CheckOnkzAsbAvailableCommand.KEY_AUFTRAG_ID, auftragId);
        when(auftragService.findOnkzAsb4Auftrag(auftragId)).thenReturn(new Pair<String, Integer>(null, 123));

        final ServiceCommandResult result = (ServiceCommandResult) cut.execute();

        assertThat(result.getCheckStatus(), equalTo(ServiceCommandResult.CHECK_STATUS_INVALID));
    }

    public void testExecuteWithInvalidAsb() throws Exception {
        final long auftragId = 1234L;
        cut.prepare(CheckOnkzAsbAvailableCommand.KEY_AUFTRAG_ID, auftragId);
        when(auftragService.findOnkzAsb4Auftrag(auftragId)).thenReturn(new Pair<String, Integer>("089", null));

        final ServiceCommandResult result = (ServiceCommandResult) cut.execute();

        assertThat(result.getCheckStatus(), equalTo(ServiceCommandResult.CHECK_STATUS_INVALID));
    }

    public void testExecuteWithBothInvalid() throws Exception {
        final long auftragId = 1234L;
        cut.prepare(CheckOnkzAsbAvailableCommand.KEY_AUFTRAG_ID, auftragId);
        when(auftragService.findOnkzAsb4Auftrag(auftragId)).thenReturn(new Pair<String, Integer>(null, null));

        final ServiceCommandResult result = (ServiceCommandResult) cut.execute();

        assertThat(result.getCheckStatus(), equalTo(ServiceCommandResult.CHECK_STATUS_INVALID));
    }

    public void testExecuteWithNull() throws Exception {
        final long auftragId = 1234L;
        cut.prepare(CheckOnkzAsbAvailableCommand.KEY_AUFTRAG_ID, auftragId);
        when(auftragService.findOnkzAsb4Auftrag(auftragId)).thenReturn(null);

        final ServiceCommandResult result = (ServiceCommandResult) cut.execute();

        assertThat(result.getCheckStatus(), equalTo(ServiceCommandResult.CHECK_STATUS_OK));
    }

    public void testExecuteWithBothValid() throws Exception {
        final long auftragId = 1234L;
        cut.prepare(CheckOnkzAsbAvailableCommand.KEY_AUFTRAG_ID, auftragId);
        when(auftragService.findOnkzAsb4Auftrag(auftragId)).thenReturn(new Pair<String, Integer>("089", 123));

        final ServiceCommandResult result = (ServiceCommandResult) cut.execute();

        assertThat(result.getCheckStatus(), equalTo(ServiceCommandResult.CHECK_STATUS_OK));
    }
}
