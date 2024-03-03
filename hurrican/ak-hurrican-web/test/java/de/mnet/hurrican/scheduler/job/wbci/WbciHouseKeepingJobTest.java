/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.01.14
 */
package de.mnet.hurrican.scheduler.job.wbci;

import static org.mockito.Mockito.*;

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
import de.mnet.wbci.service.WbciGeschaeftsfallService;

@Test(groups = BaseTest.UNIT)
public class WbciHouseKeepingJobTest {
    @Mock
    private JobExecutionContext jobExecutionContextMock;

    @Mock
    private WbciGeschaeftsfallService wbciGeschaeftsfallServiceMock;

    @Mock
    private LogDBJobErrorHandler logDBJobErrorHandlerMock;

    @Mock
    private SendMailJobErrorHandler sendMailJobErrorHandlerMock;

    @Spy
    @InjectMocks
    private WbciHouseKeepingJob job = new WbciHouseKeepingJob() {
        @Override
        protected <T extends IServiceObject> T getService(Class<T> type) throws ServiceNotFoundException {
            return (T) wbciGeschaeftsfallServiceMock;
        }
    };

    @BeforeMethod
    public void beforeMethod() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testExecuteInternal() throws Exception {
        when(wbciGeschaeftsfallServiceMock.autoCompleteEligiblePreagreements()).thenReturn(0);
        when(wbciGeschaeftsfallServiceMock.updateExpiredPreagreements()).thenReturn(0);
        job.executeInternal(jobExecutionContextMock);
        verify(wbciGeschaeftsfallServiceMock).autoCompleteEligiblePreagreements();
        verify(wbciGeschaeftsfallServiceMock).updateExpiredPreagreements();
    }

    @Test
    public void testExecuteInternalWithExceptionHandling() throws Exception {
        WbciServiceException wbciServiceException = new WbciServiceException();
        when(wbciGeschaeftsfallServiceMock.autoCompleteEligiblePreagreements()).thenThrow(wbciServiceException);
        job.executeInternal(jobExecutionContextMock);
        verify(logDBJobErrorHandlerMock).handleError(jobExecutionContextMock, wbciServiceException, null);
        verify(sendMailJobErrorHandlerMock).handleError(jobExecutionContextMock, wbciServiceException, null);
    }
}
