/* 
 * Copyright (c) 2014 - M-net Telekommunikations GmbH 
 * All rights reserved. 
 * ------------------------------------------------------- 
 * File created: 30.01.14 
 */
package de.mnet.hurrican.scheduler.job.wbci;

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
import de.mnet.hurrican.scheduler.job.errorhandler.LogDBJobErrorHandler;
import de.mnet.hurrican.scheduler.job.errorhandler.SendMailJobErrorHandler;
import de.mnet.wbci.exception.WbciServiceException;
import de.mnet.wbci.service.WbciEscalationService;

@Test(groups = BaseTest.UNIT)
public class WbciEscalationReportJobTest {
    @Mock
    private JobExecutionContext jobExecutionContextMock;

    @Mock
    private WbciEscalationService wbciEscalationService;
    @Spy
    @InjectMocks
    private WbciEscalationReportJob job = new WbciEscalationReportJob() {
        @Override
        protected <T extends IServiceObject> T getService(Class<T> type) throws ServiceNotFoundException {
            return (T) wbciEscalationService;
        }
    };
    @Mock
    private LogDBJobErrorHandler logDBJobErrorHandlerMock;
    @Mock
    private SendMailJobErrorHandler sendMailJobErrorHandlerMock;

    @BeforeMethod
    public void beforeMethod() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testExecuteInternal() throws Exception {
        when(wbciEscalationService.sendCarrierSpecificEscalationReports()).thenReturn(Collections.EMPTY_LIST);
        job.executeInternal(jobExecutionContextMock);
        verify(wbciEscalationService).sendCarrierSpecificEscalationReports();
        verify(wbciEscalationService).sendCarrierEscalationOverviewReport();
        verify(wbciEscalationService).sendInternalOverviewReport();
    }

    @Test
    public void testExecuteInternalWithExceptionHandling() throws Exception {
        WbciServiceException wbciServiceException = new WbciServiceException();
        doThrow(wbciServiceException).when(wbciEscalationService).sendCarrierEscalationOverviewReport();
        job.executeInternal(jobExecutionContextMock);
        verify(logDBJobErrorHandlerMock).handleError(jobExecutionContextMock, wbciServiceException, null);
        verify(sendMailJobErrorHandlerMock).handleError(jobExecutionContextMock, wbciServiceException, null);
    }

}
