package de.mnet.hurrican.scheduler.job.wbci;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.*;

import java.util.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.quartz.JobExecutionContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.BaseTest;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.service.iface.IServiceObject;
import de.mnet.hurrican.scheduler.job.errorhandler.LogDBJobErrorHandler;
import de.mnet.hurrican.scheduler.job.errorhandler.SendMailJobErrorHandler;
import de.mnet.wbci.service.WbciAutomationService;

@Test(groups = BaseTest.UNIT)
public class ProcessWbciAutomatableRuemVAsJobTest {
    @Mock
    private JobExecutionContext jobExecutionContextMock;

    @Mock
    private WbciAutomationService wbciAutomationServiceMock;

    @Mock
    private LogDBJobErrorHandler logDBJobErrorHandlerMock;

    @Mock
    private SendMailJobErrorHandler sendMailJobErrorHandlerMock;

    @Spy
    @InjectMocks
    private ProcessWbciAutomatableRuemVAsJob job = new ProcessWbciAutomatableRuemVAsJob() {
        @Override
        protected <T extends IServiceObject> T getService(Class<T> type) throws ServiceNotFoundException {
            return (T) wbciAutomationServiceMock;
        }
    };

    @BeforeMethod
    public void beforeMethod() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testExecuteInternal() throws Exception {
        AKUser user = new AKUser();
        when(wbciAutomationServiceMock.processAutomatableRuemVas(user)).thenReturn(Collections.<String>emptyList());
        doReturn(user).when(job).getCurrentUser(anyLong());

        job.executeInternal(jobExecutionContextMock);

        verify(wbciAutomationServiceMock).processAutomatableRuemVas(user);
        verify(logDBJobErrorHandlerMock, never()).handleError(eq(jobExecutionContextMock), any(Throwable.class), any(Object[].class));
        verify(sendMailJobErrorHandlerMock, never()).handleError(eq(jobExecutionContextMock), any(Throwable.class), any(Object[].class));
    }

    @Test
    public void testExecuteInternalWithRuntimeException() throws Exception {
        RuntimeException runtimeException = new RuntimeException();
        AKUser user = new AKUser();
        doThrow(runtimeException).when(wbciAutomationServiceMock).processAutomatableRuemVas(user);
        doReturn(user).when(job).getCurrentUser(anyLong());

        job.executeInternal(jobExecutionContextMock);

        verify(wbciAutomationServiceMock).processAutomatableRuemVas(user);
        verify(logDBJobErrorHandlerMock).handleError(eq(jobExecutionContextMock), eq(runtimeException), any(Object[].class));
        verify(sendMailJobErrorHandlerMock).handleError(eq(jobExecutionContextMock), eq(runtimeException), any(Object[].class));
    }

}