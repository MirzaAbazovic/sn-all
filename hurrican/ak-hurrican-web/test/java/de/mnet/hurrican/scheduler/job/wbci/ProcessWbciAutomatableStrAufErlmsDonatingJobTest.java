/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.12.2015
 */
package de.mnet.hurrican.scheduler.job.wbci;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
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
import de.mnet.wbci.service.WbciAutomationDonatingService;

/**
 * Test-Klasse fuer {@link ProcessWbciAutomatableStrAufErlmsDonatingJob}
 */
@Test(groups = BaseTest.UNIT)
public class ProcessWbciAutomatableStrAufErlmsDonatingJobTest {

    @Mock
    private JobExecutionContext jobExecutionContextMock;

    @Mock
    private WbciAutomationDonatingService wbciAutomationDonatingServiceMock;

    @Mock
    private LogDBJobErrorHandler logDBJobErrorHandlerMock;

    @Mock
    private SendMailJobErrorHandler sendMailJobErrorHandlerMock;

    @Spy
    @InjectMocks
    private ProcessWbciAutomatableStrAufErlmsDonatingJob job = new ProcessWbciAutomatableStrAufErlmsDonatingJob() {
        @Override
        protected <T extends IServiceObject> T getService(Class<T> type) throws ServiceNotFoundException {
            return (T) wbciAutomationDonatingServiceMock;
        }
    };

    @BeforeMethod
    public void beforeMethod() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testExecuteInternal() throws Exception {
        AKUser user = new AKUser();
        when(wbciAutomationDonatingServiceMock.processAutomatableStrAufhErlmsDonating(any(AKUser.class), anyLong()))
                .thenReturn(Collections.<String>emptyList());
        doReturn(user).when(job).getCurrentUser(anyLong());

        job.executeInternal(jobExecutionContextMock);

        verify(wbciAutomationDonatingServiceMock).processAutomatableStrAufhErlmsDonating(eq(user), anyLong());
        verify(logDBJobErrorHandlerMock, never()).handleError(eq(jobExecutionContextMock), any(Throwable.class), any(Object[].class));
        verify(sendMailJobErrorHandlerMock, never()).handleError(eq(jobExecutionContextMock), any(Throwable.class), any(Object[].class));
    }

    @Test
    public void testExecuteInternalWithRuntimeException() throws Exception {
        RuntimeException runtimeException = new RuntimeException();

        AKUser user = new AKUser();
        doReturn(user).when(job).getCurrentUser(anyLong());

        doThrow(runtimeException).when(wbciAutomationDonatingServiceMock).processAutomatableStrAufhErlmsDonating(eq(user), anyLong());

        job.executeInternal(jobExecutionContextMock);

        verify(wbciAutomationDonatingServiceMock).processAutomatableStrAufhErlmsDonating(eq(user), anyLong());
        verify(logDBJobErrorHandlerMock).handleError(eq(jobExecutionContextMock), eq(runtimeException), any(Object[].class));
        verify(sendMailJobErrorHandlerMock).handleError(eq(jobExecutionContextMock), eq(runtimeException), any(Object[].class));
    }


}
