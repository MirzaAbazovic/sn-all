package de.mnet.hurrican.scheduler.job.base;

import static org.mockito.Mockito.*;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.tools.exceptions.ExceptionLogEntryContext;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.model.exceptions.ExceptionLogEntry;
import de.augustakom.hurrican.service.exceptions.ExceptionLogService;
import de.mnet.hurrican.scheduler.job.errorhandler.LogDBJobErrorHandler;
import de.mnet.hurrican.scheduler.job.errorhandler.SendMailJobErrorHandler;
import de.mnet.hurrican.scheduler.job.errorhandler.SendMailJobWarningsHandler;

public class AbstractSendScheduledRequestsJobTest {

    private ExceptionLogEntryContext logContext = ExceptionLogEntryContext.WBCI_SCHEDULER_SEARCH_REQUEST_ERROR;
    @Mock
    private ExceptionLogService exceptionLogService;
    @Mock
    private SendMailJobWarningsHandler sendMailJobWarningsHandler;
    @Mock
    private SendMailJobErrorHandler sendMailJobErrorHandler;
    @Mock
    private LogDBJobErrorHandler logDBJobErrorHandler;
    @Spy
    @InjectMocks
    private AbstractSendScheduledRequestsJob testling = new AbstractSendScheduledRequestsJob() {
        @Override
        protected ExceptionLogEntryContext getSchedulerExceptionLogEntryContext() {
            return logContext;
        }

        @Override
        protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        }

        @Override
        protected SendMailJobWarningsHandler getSendMailJobWarningHandler() {
            return sendMailJobWarningsHandler;
        }

        @Override
        protected SendMailJobErrorHandler getSendMailJobErrorHandler() {
            return sendMailJobErrorHandler;
        }

        @Override
        protected LogDBJobErrorHandler getLogDBJobErrorHandler() {
            return logDBJobErrorHandler;
        }
    };
    @Mock
    private JobExecutionContext context;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testHandleError() throws Exception {
        Exception testExp = new Exception("TEST");
        testling.handleError(context, testExp);

        ArgumentCaptor<ExceptionLogEntry> ac = ArgumentCaptor.forClass(ExceptionLogEntry.class);
        verify(exceptionLogService).saveExceptionLogEntry(ac.capture());
        verify(testling, never()).handleErrorFallback(any(JobExecutionContext.class), any(Exception.class));
        Assert.assertEquals(ac.getValue().getContext(), logContext.identifier);
        Assert.assertEquals(ac.getValue().getErrorMessage(), "TEST");
    }

    @Test
    public void testHandleWarnings() throws Exception {
        AKWarnings akWarnings = new AKWarnings();
        testling.handleWarnings(context, akWarnings);

        verify(sendMailJobWarningsHandler).handleWarnings(context, akWarnings);
    }

    @Test
    public void testHandleErrorFallback() throws Exception {
        Exception testExp = new Exception("TEST");
        RuntimeException testExp2 = new RuntimeException("TEST2");
        when(exceptionLogService.saveExceptionLogEntry(any(ExceptionLogEntry.class))).thenThrow(testExp2);
        testling.handleError(context, testExp);

        verify(testling).handleErrorFallback(context, testExp);
        verify(logDBJobErrorHandler).handleError(context, testExp, null);
        verify(sendMailJobErrorHandler).handleError(context, testExp, null);
        verify(testling).handleErrorFallback(context, testExp2);
        verify(logDBJobErrorHandler).handleError(context, testExp2, null);
        verify(sendMailJobErrorHandler).handleError(context, testExp2, null);
    }
}