package de.augustakom.hurrican.service.exceptions.impl;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.tools.exceptions.ExceptionLogEntryContext;
import de.augustakom.hurrican.model.exceptions.ExceptionLogEntry;
import de.augustakom.hurrican.service.exceptions.ExceptionLogService;
import de.mnet.esb.cdm.shared.errorhandlingservice.v1.ErrorHandlingService;
import de.mnet.esb.cdm.shared.errorhandlingservice.v1.HandleError;

public class AtlasErrorHandlingServiceImplTest {
    @Mock
    private ErrorHandlingService errorHandlingService;
    @Mock
    private ExceptionLogService exceptionLogService;
    @Spy
    @InjectMocks
    private AtlasErrorHandlingServiceImpl testling;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testHandleError() throws Exception {
        HandleError error = new HandleError();
        testling.handleError(error);
        verify(errorHandlingService).handleError(error);
        verify(exceptionLogService, never()).saveExceptionLogEntry(any(ExceptionLogEntry.class));
    }

    @Test
    public void testHandleErrorExceptionHandling() throws Exception {
        doThrow(new RuntimeException("TEST")).when(errorHandlingService).handleError(any(HandleError.class));
        HandleError error = new HandleError();
        testling.handleError(error);
        verify(errorHandlingService).handleError(error);

        ArgumentCaptor<ExceptionLogEntry> captor = ArgumentCaptor.forClass(ExceptionLogEntry.class);
        verify(exceptionLogService).saveExceptionLogEntry(captor.capture());
        ExceptionLogEntry logEntry = captor.getValue();

        //assert the generated log entry
        assertEquals(logEntry.getContext(), ExceptionLogEntryContext.ATLAS_ERROR_SERVICE_ERROR.identifier);
        String regex = "Unable to send message to ATLAS ESB ErrorHandlingService:\\nESB-ERROR-Message:\\n.*";
        assertTrue(logEntry.getErrorMessage().matches(regex),
                String.format("error message \n\"%s\"\nwon't match to regex \n\"%s\"", logEntry.getErrorMessage(), regex));
        assertNull(logEntry.getSolution());
        assertNull(logEntry.getBearbeiter());
    }
}