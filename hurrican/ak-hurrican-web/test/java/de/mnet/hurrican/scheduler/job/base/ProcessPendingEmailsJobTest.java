/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.02.2010 22:25:55
 */

package de.mnet.hurrican.scheduler.job.base;

import static org.mockito.Mockito.*;

import java.util.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.mail.MailSendException;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.exceptions.ProcessPendingEmailsException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.MailService;
import de.mnet.hurrican.scheduler.job.errorhandler.LogDBJobErrorHandler;
import de.mnet.hurrican.scheduler.job.errorhandler.SendMailJobErrorHandler;

@Test(groups = BaseTest.UNIT)
public class ProcessPendingEmailsJobTest {

    @Mock
    private MailService mailService;
    @Mock
    private LogDBJobErrorHandler logDbHandler;
    @Mock
    private SendMailJobErrorHandler sendMailJobErrorHandler;

    @Spy
    @InjectMocks
    private ProcessPendingEmailsJob testling;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        doNothing().when(testling).init();
    }

    @Test
    public void testNormalException() throws Exception {
        ProcessPendingEmailsException excp = new ProcessPendingEmailsException("TEST", Arrays.<Exception>asList(new FindException("NOT FOUND")));
        doThrow(excp).when(mailService).processPendingEmails();
        try {
            testling.executeInternal(mock(JobExecutionContext.class));
        }
        catch (JobExecutionException jobExcp) {
            Assert.assertEquals(jobExcp.getUnderlyingException(), (excp));
        }
        finally {
            verify(logDbHandler, never()).handleError(any(JobExecutionContext.class), any(Throwable.class), any(Object[].class));
            verify(sendMailJobErrorHandler, never()).handleError(any(JobExecutionContext.class), any(Throwable.class), any(Object[].class));
            verify(testling).init();
        }
    }

    @Test
    public void testMailSendException() throws Exception {
        ProcessPendingEmailsException excp = new ProcessPendingEmailsException("TEST", Arrays.<Exception>asList(new MailSendException("NOT SEND")));
        doThrow(excp).when(mailService).processPendingEmails();
        try {
            testling.executeInternal(mock(JobExecutionContext.class));
        }
        catch (JobExecutionException jobExcp) {
            Assert.assertEquals(jobExcp.getUnderlyingException(), (excp));
        }
        finally {
            verify(logDbHandler).handleError(any(JobExecutionContext.class), any(Throwable.class), any(Object[].class));
            verify(sendMailJobErrorHandler).handleError(any(JobExecutionContext.class), any(Throwable.class), any(Object[].class));
            verify(testling).init();
        }
    }
}
