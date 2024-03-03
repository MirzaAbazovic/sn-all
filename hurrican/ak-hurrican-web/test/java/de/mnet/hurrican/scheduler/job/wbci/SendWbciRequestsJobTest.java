/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.10.13
 */
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

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.service.iface.IServiceObject;
import de.mnet.wbci.exception.WbciBaseException;
import de.mnet.wbci.service.WbciSchedulerService;

@Test(groups = BaseTest.UNIT)
public class SendWbciRequestsJobTest {

    @Mock
    private JobExecutionContext jobExecutionContextMock;

    @Mock
    private WbciSchedulerService wbciSchedulerServiceMock;

    @Spy
    @InjectMocks
    private SendWbciRequestsJob job = new SendWbciRequestsJob() {
        @Override
        protected <T extends IServiceObject> T getService(Class<T> type) throws ServiceNotFoundException {
            return (T) wbciSchedulerServiceMock;
        }
    };

    @BeforeMethod
    public void beforeMethod() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testExecuteInternalWithNoRequestsToBeSent() throws Exception {
        when(wbciSchedulerServiceMock.findScheduledWbciRequestIds()).thenReturn(Collections.<Long>emptyList());

        job.executeInternal(jobExecutionContextMock);

        verify(wbciSchedulerServiceMock).findScheduledWbciRequestIds();
        verify(job, never()).handleError(eq(jobExecutionContextMock), any(Exception.class));
    }

    @Test
    public void testExecuteInternalWithMultipleRequestsToBeSent() throws Exception {
        List<Long> requestIds = Arrays.asList(1L, 2L);
        when(wbciSchedulerServiceMock.findScheduledWbciRequestIds()).thenReturn(requestIds);

        job.executeInternal(jobExecutionContextMock);

        verify(wbciSchedulerServiceMock).findScheduledWbciRequestIds();
        verify(wbciSchedulerServiceMock, times(requestIds.size())).sendScheduledRequest(anyLong());
        verify(job, never()).handleError(eq(jobExecutionContextMock), any(Exception.class));
    }

    @Test
    public void testExecuteInternalWithWbciBaseException() throws Exception {
        List<Long> requestIds = Arrays.asList(1L, 2L);
        when(wbciSchedulerServiceMock.findScheduledWbciRequestIds()).thenReturn(requestIds);
        doThrow(new WbciBaseException()).when(wbciSchedulerServiceMock).sendScheduledRequest(1L);

        job.executeInternal(jobExecutionContextMock);

        verify(wbciSchedulerServiceMock).findScheduledWbciRequestIds();
        verify(wbciSchedulerServiceMock, times(requestIds.size())).sendScheduledRequest(anyLong());
        verify(job).handleError(eq(jobExecutionContextMock), any(WbciBaseException.class));
    }

    @Test
    public void testExecuteInternalWithRuntimeException() throws Exception {
        List<Long> requestIds = Arrays.asList(1L, 2L);
        RuntimeException runtimeException = new RuntimeException();
        when(wbciSchedulerServiceMock.findScheduledWbciRequestIds()).thenReturn(requestIds);
        doThrow(runtimeException).when(wbciSchedulerServiceMock).sendScheduledRequest(1L);

        job.executeInternal(jobExecutionContextMock);

        verify(wbciSchedulerServiceMock).findScheduledWbciRequestIds();
        verify(wbciSchedulerServiceMock, times(1)).sendScheduledRequest(anyLong());
        verify(job).handleError(eq(jobExecutionContextMock), eq(runtimeException));
    }

}
