package de.mnet.hurrican.scheduler.job.ffm;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.eq;
import static org.testng.Assert.*;

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
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.exceptions.FFMServiceException;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.VerlaufAbteilungBuilder;
import de.augustakom.hurrican.model.cc.VerlaufBuilder;
import de.augustakom.hurrican.model.cc.VerlaufStatus;
import de.augustakom.hurrican.model.cc.view.VerlaufUniversalView;
import de.augustakom.hurrican.model.cc.view.VerlaufUniversalViewBuilder;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.ICCService;
import de.augustakom.hurrican.service.cc.ffm.FFMService;

@Test(groups = BaseTest.UNIT)
public class FfmUpdateOrder4BAsTodayJobTest {

    @Mock
    private JobExecutionContext jobExecutionContextMock;
    @Mock
    private FFMService ffmService;
    @Mock
    private BAService baService;

    @Spy
    @InjectMocks
    private FfmUpdateOrder4BAsTodayJob job = new FfmUpdateOrder4BAsTodayJob() {
        @Override
        protected <T extends ICCService> T getCCService(Class<T> type) throws ServiceNotFoundException {
            if(type.equals(FFMService.class))   {
                return (T) ffmService;
            }
            else if(type.equals(BAService.class))   {
                return (T) baService;
            }
            return null;
        }
    };

    @BeforeMethod
    public void beforeMethod() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testExecuteInternal() throws Exception {
        AKWarnings warnings = new AKWarnings().addAKWarning(this, "test");
        doReturn(warnings).when(job).doFfmUpdates(any(Date.class));

        job.executeInternal(jobExecutionContextMock);

        verify(job).doFfmUpdates(any(Date.class));

        doNothing().when(job).sendErrorMail(any(JobExecutionContext.class), eq(warnings));
        verify(job).sendErrorMail(any(JobExecutionContext.class), eq(warnings));
    }

    @Test
    public void doFfmUpdates() throws FindException {
        VerlaufUniversalView viewSuccess = new VerlaufUniversalViewBuilder().withVerlaufId(1L).build();
        Verlauf verlaufSuccess = new VerlaufBuilder().withId(viewSuccess.getVerlaufId()).withWorkforceOrderId("111").build();
        verlaufSuccess.setAuftragId(1L);

        VerlaufUniversalView viewError = new VerlaufUniversalViewBuilder().withVerlaufId(2L).build();
        Verlauf verlaufError = new VerlaufBuilder().withId(viewError.getVerlaufId()).withWorkforceOrderId("222").build();
        verlaufError.setAuftragId(2L);

        VerlaufUniversalView viewNoFfm = new VerlaufUniversalViewBuilder().withVerlaufId(3L).build();
        Verlauf verlaufNoFfm = new VerlaufBuilder().withId(viewNoFfm.getVerlaufId()).withWorkforceOrderId(null).build();
        verlaufNoFfm.setAuftragId(3L);

        VerlaufUniversalView viewNoActiveFfm = new VerlaufUniversalViewBuilder().withVerlaufId(4L).build();
        Verlauf verlaufNoActiveFfm = new VerlaufBuilder().withId(viewNoActiveFfm.getVerlaufId()).withWorkforceOrderId("444").build();
        verlaufNoFfm.setAuftragId(4L);

        VerlaufUniversalView viewNoBaFound = new VerlaufUniversalViewBuilder().withVerlaufId(5L).build();

        Date now = new Date();

        when(baService.findBAVerlaufViews4Abt(true, Abteilung.FFM, false, now, now)).thenReturn(
                Arrays.asList(viewSuccess, viewError, viewNoFfm, viewNoActiveFfm, viewNoBaFound));

        when(baService.findVerlauf(viewSuccess.getVerlaufId())).thenReturn(verlaufSuccess);
        when(baService.findVerlauf(viewError.getVerlaufId())).thenReturn(verlaufError);
        when(baService.findVerlauf(viewNoFfm.getVerlaufId())).thenReturn(verlaufNoFfm);
        when(baService.findVerlauf(viewNoActiveFfm.getVerlaufId())).thenReturn(verlaufNoActiveFfm);
        when(baService.findVerlauf(viewNoBaFound.getVerlaufId())).thenReturn(null);


        when(baService.findVerlaufAbteilung(verlaufSuccess.getId(), Abteilung.FFM)).thenReturn(
                new VerlaufAbteilungBuilder().withVerlaufStatusId(VerlaufStatus.STATUS_IM_UMLAUF).build());
        when(baService.findVerlaufAbteilung(verlaufError.getId(), Abteilung.FFM)).thenReturn(
                new VerlaufAbteilungBuilder().withVerlaufStatusId(VerlaufStatus.STATUS_IM_UMLAUF).build());
        when(baService.findVerlaufAbteilung(verlaufNoActiveFfm.getId(), Abteilung.FFM)).thenReturn(
                new VerlaufAbteilungBuilder().withVerlaufStatusId(VerlaufStatus.STATUS_ERLEDIGT).build());

        when(ffmService.hasActiveFfmRecord(verlaufSuccess, true)).thenReturn(Boolean.TRUE);
        when(ffmService.hasActiveFfmRecord(verlaufError, true)).thenReturn(Boolean.TRUE);
        when(ffmService.hasActiveFfmRecord(verlaufNoActiveFfm, true)).thenReturn(Boolean.FALSE);

//        when(ffmService.updateAndSendOrder(verlaufSuccess));
        doThrow(new FFMServiceException("error")).when(ffmService).updateAndSendOrder(verlaufError);

        AKWarnings result = job.doFfmUpdates(now);

        verify(ffmService, times(2)).updateAndSendOrder(any(Verlauf.class));
        verify(ffmService).updateAndSendOrder(verlaufSuccess);
        verify(ffmService).updateAndSendOrder(verlaufError);
        verify(ffmService, never()).updateAndSendOrder(verlaufNoActiveFfm);
        verify(ffmService, never()).updateAndSendOrder(verlaufNoFfm);

        assertNotNull(result);
        assertTrue(result.isNotEmpty());
        assertEquals(result.getAKMessages().size(), 1);
        assertTrue(result.getWarningsAsText().contains("Fehler beim FFM Update für Auftrag 2"));
    }
}
