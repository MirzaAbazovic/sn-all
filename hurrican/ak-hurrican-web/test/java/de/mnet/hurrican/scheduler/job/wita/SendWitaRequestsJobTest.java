package de.mnet.hurrican.scheduler.job.wita;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.quartz.JobExecutionContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.service.cc.ICCService;
import de.mnet.wita.exceptions.WitaBaseException;
import de.mnet.wita.service.MwfEntityService;
import de.mnet.wita.service.WitaSendMessageService;

public class SendWitaRequestsJobTest {

    @Mock
    private JobExecutionContext jobExecutionContextMock;

    @Mock
    private WitaSendMessageService witaSendMessageService;
    @Mock
    private MwfEntityService mwfEntityService;
    @Spy
    @InjectMocks
    private SendWitaRequestsJob job = new SendWitaRequestsJob() {
        @Override
        protected <T extends ICCService> T getCCService(Class<T> type) throws ServiceNotFoundException {
            return (T) witaSendMessageService;
        }
    };

    @BeforeMethod
    public void beforeMethod() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testExecuteInternalWithNoRequestsToBeSent() throws Exception {
        when(mwfEntityService.findUnsentRequestsForEveryGeschaeftsfall(anyInt())).thenReturn(Collections.<Long>emptyList());

        job.executeInternal(jobExecutionContextMock);

        verify(mwfEntityService).findUnsentRequestsForEveryGeschaeftsfall(anyInt());
        verify(job, never()).handleError(eq(jobExecutionContextMock), any(Exception.class));
        verify(witaSendMessageService, never()).sendScheduledRequest(anyLong());
    }

    @Test
    public void testExecuteInternalWithMultipleRequestsToBeSent() throws Exception {
        List<Long> requestIds = Arrays.asList(1L, 2L);
        when(mwfEntityService.findUnsentRequestsForEveryGeschaeftsfall(anyInt())).thenReturn(requestIds);

        job.executeInternal(jobExecutionContextMock);

        verify(mwfEntityService).findUnsentRequestsForEveryGeschaeftsfall(anyInt());
        verify(job, never()).handleError(eq(jobExecutionContextMock), any(Exception.class));
        verify(witaSendMessageService).sendScheduledRequest(1L);
        verify(witaSendMessageService).sendScheduledRequest(2L);
    }

    @Test
    public void testExecuteInternalWithWbciBaseException() throws Exception {
        List<Long> requestIds = Arrays.asList(1L, 2L);
        when(mwfEntityService.findUnsentRequestsForEveryGeschaeftsfall(anyInt())).thenReturn(requestIds);
        when(witaSendMessageService.sendScheduledRequest(1L)).thenThrow(new WitaBaseException());

        job.executeInternal(jobExecutionContextMock);

        verify(mwfEntityService).findUnsentRequestsForEveryGeschaeftsfall(anyInt());
        verify(job).handleError(eq(jobExecutionContextMock), any(WitaBaseException.class));
        verify(witaSendMessageService).sendScheduledRequest(1L);
        verify(witaSendMessageService).sendScheduledRequest(2L);
    }

    @Test
    public void testExecuteInternalWithRuntimeException() throws Exception {
        RuntimeException runtimeException = new RuntimeException();
        when(mwfEntityService.findUnsentRequestsForEveryGeschaeftsfall(anyInt())).thenThrow(runtimeException);

        job.executeInternal(jobExecutionContextMock);

        verify(job).handleError(eq(jobExecutionContextMock), any(RuntimeException.class));
        verify(witaSendMessageService, never()).sendScheduledRequest(anyLong());
    }

    @Test
    public void testExecuteInternalWithWarnings() throws Exception {
        List<Long> requestIds = new ArrayList<>();
        for (int i = 1; i <= 110; i++) {
            requestIds.add((long) i);
        }
        when(witaSendMessageService.sendScheduledRequest(anyLong())).thenReturn(true);
        when(mwfEntityService.findUnsentRequestsForEveryGeschaeftsfall(anyInt())).thenReturn(requestIds);

        job.executeInternal(jobExecutionContextMock);

        ArgumentCaptor<AKWarnings> ac = ArgumentCaptor.forClass(AKWarnings.class);
        verify(job).handleWarnings(eq(jobExecutionContextMock), ac.capture());
        assertTrue(ac.getValue().getWarningsAsText().startsWith("More than 100 unsent MnetWitaRequests found; send was limitted to the first 100 rows!"));
        verify(job, never()).handleError(eq(jobExecutionContextMock), any(RuntimeException.class));
        verify(witaSendMessageService, times(100)).sendScheduledRequest(anyLong());
    }
}