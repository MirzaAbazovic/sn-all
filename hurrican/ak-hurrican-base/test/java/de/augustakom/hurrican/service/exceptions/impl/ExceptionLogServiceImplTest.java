/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.11.2011 16:43:38
 */
package de.augustakom.hurrican.service.exceptions.impl;

import static de.augustakom.common.BaseTest.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.tools.exceptions.ExceptionLogEntryContext;
import de.augustakom.hurrican.dao.exceptions.ExceptionLogEntryDAO;
import de.augustakom.hurrican.model.exceptions.ExceptionLogEntry;
import de.mnet.common.tools.DateConverterUtils;

@Test(groups = { UNIT })
public class ExceptionLogServiceImplTest {

    @InjectMocks
    private ExceptionLogServiceImpl testling;

    @Mock
    private ExceptionLogEntryDAO exceptionLogEntryDAO;

    public final LocalDateTime occurred = LocalDateTime.now().minusDays(1);
    public final ExceptionLogEntryContext context = ExceptionLogEntryContext.HURRICAN_SERVER;

    @BeforeMethod
    public void setUp() {
        testling = new ExceptionLogServiceImpl();
        MockitoAnnotations.initMocks(this);
    }

    public void shouldSaveException() {
        ExceptionLogEntry ex = getException();
        when(exceptionLogEntryDAO.store(ex)).thenReturn(ex);

        testling.saveExceptionLogEntry(ex);

        verify(exceptionLogEntryDAO).store(ex);
        assertEquals(DateConverterUtils.asLocalDateTime(ex.getDateOccurred()), occurred, "date should not be overwritten");
    }

    public void shouldSaveExceptionWithNoDateSet() {
        ExceptionLogEntry ex = getException();
        ex.setDateOccurred(null);
        when(exceptionLogEntryDAO.store(ex)).thenReturn(ex);

        testling.saveExceptionLogEntry(ex);

        verify(exceptionLogEntryDAO).store(ex);
        assertNotNull(ex.getDateOccurred());
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void shouldFailToSaveExceptionAsHostNotSet() {
        ExceptionLogEntry ex = getException();
        ex.setHost(null);
        testling.saveExceptionLogEntry(ex);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void shouldFailToSaveExceptionAsErrorMsgNotSet() {
        ExceptionLogEntry ex = getException();
        ex.setErrorMessage(null);
        testling.saveExceptionLogEntry(ex);
    }

    public void shouldFindExceptionById() {
        final long id = 1L;
        ExceptionLogEntry ex = getException();
        when(exceptionLogEntryDAO.findById(id, ExceptionLogEntry.class)).thenReturn(ex);

        assertSame(testling.findExceptionLogEntryById(id), ex);
    }

    public void shouldGetMailTextForThrownExceptions() {
        long count = 20L;
        when(exceptionLogEntryDAO.getNewExceptionLogEntriesCount()).thenReturn(count);
        String mailText = testling.getMailtextForThrownExceptions();

        assertEquals(mailText.substring(0, 10), "Es sind 20");
    }

    public void shouldNotGetMailTextAsNoExceptionsFound() {
        when(exceptionLogEntryDAO.getNewExceptionLogEntriesCount()).thenReturn(0L);
        assertNull(testling.getMailtextForThrownExceptions());
    }

    public void shouldFindNewExceptions() {
        List<ExceptionLogEntry> newExceptions = Arrays.asList(getException());
        when(exceptionLogEntryDAO.findNewExceptionLogEntries(context, 10)).thenReturn(newExceptions);

        List<ExceptionLogEntry> logEntries = testling.findNewExceptionLogEntries(context, 10);
        assertEquals(logEntries, newExceptions);
    }

    private ExceptionLogEntry getException() {
        return getException("some error", "some host", occurred);
    }

    private ExceptionLogEntry getException(String errorMsg, String host, LocalDateTime occurred) {
        ExceptionLogEntry ex = new ExceptionLogEntry(context, errorMsg);
        ex.setHost(host);
        ex.setDateOccurred( occurred != null ? Date.from(occurred.atZone(ZoneId.systemDefault()).toInstant()) : null);
        return ex;
    }
}
