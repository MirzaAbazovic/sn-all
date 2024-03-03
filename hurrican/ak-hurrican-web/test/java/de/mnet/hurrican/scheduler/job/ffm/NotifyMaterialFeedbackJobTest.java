/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.09.14
 */
package de.mnet.hurrican.scheduler.job.ffm;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.quartz.JobExecutionContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.service.cc.ICCService;
import de.augustakom.hurrican.service.cc.ffm.FFMService;

@Test(groups = BaseTest.UNIT)
public class NotifyMaterialFeedbackJobTest {

    @Mock
    private JobExecutionContext jobExecutionContextMock;
    @Mock
    private FFMService ffmService;

    @Spy
    @InjectMocks
    private NotifyMaterialFeedbackJob job = new NotifyMaterialFeedbackJob() {
        @Override
        protected <T extends ICCService> T getCCService(Class<T> type) throws ServiceNotFoundException {
            return (T) ffmService;
        }
    };

    @BeforeMethod
    public void beforeMethod() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testExecuteInternal() throws Exception {
        job.executeInternal(jobExecutionContextMock);

        Mockito.verify(ffmService).createMailsForFfmFeedbacks();
    }

}
