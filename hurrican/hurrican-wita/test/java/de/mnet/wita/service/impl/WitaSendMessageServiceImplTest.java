package de.mnet.wita.service.impl;

import static de.augustakom.common.BaseTest.*;
import static de.augustakom.hurrican.service.location.CamelProxyLookupService.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import javax.validation.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.hurrican.service.location.CamelProxyLookupService;
import de.mnet.wita.WitaMessage;
import de.mnet.wita.bpm.TalOrderWorkflowService;
import de.mnet.wita.dao.MwfEntityDao;
import de.mnet.wita.exceptions.WitaBaseException;
import de.mnet.wita.integration.LineOrderService;
import de.mnet.wita.message.Auftrag;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.MnetWitaRequest;
import de.mnet.wita.message.Storno;
import de.mnet.wita.message.TerminVerschiebung;
import de.mnet.wita.message.builder.AuftragBuilder;
import de.mnet.wita.message.builder.StornoBuilder;
import de.mnet.wita.message.builder.TerminVerschiebungBuilder;
import de.mnet.wita.message.builder.meldung.RueckMeldungPvBuilder;
import de.mnet.wita.message.meldung.RueckMeldungPv;
import de.mnet.wita.service.WitaConfigService;
import de.mnet.wita.service.WitaTalOrderService;

@Test(groups = UNIT)
public class WitaSendMessageServiceImplTest {

    @Spy
    @InjectMocks
    private WitaSendMessageServiceImpl testling;

    @Mock
    private CamelProxyLookupService camelProxyLookupServiceMock;

    @Mock
    private WitaConfigService witaConfigServiceMock;

    @Mock
    private LineOrderService lineOrderService;
    @Mock
    private MwfEntityDao mwfEntityDao;
    @Mock
    private WitaTalOrderService witaTalOrderService;
    @Mock
    private TalOrderWorkflowService talOrderWorkflowService;

    @BeforeMethod
    public void setUp() {
        testling = new WitaSendMessageServiceImpl();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSendMessage() throws Exception {
        when(camelProxyLookupServiceMock.lookupCamelProxy(PROXY_LINE_ORDER,LineOrderService.class))
                .thenReturn(lineOrderService);

        RueckMeldungPv rueckMeldungPv = new RueckMeldungPvBuilder().build();
        testling.sendAndProcessMessage(rueckMeldungPv);

        verify(camelProxyLookupServiceMock).lookupCamelProxy(PROXY_LINE_ORDER,LineOrderService.class);
        verify(witaConfigServiceMock, never()).isSendAllowed((de.mnet.wita.message.MnetWitaRequest) any());
        verify(lineOrderService).sendToWita(rueckMeldungPv);
    }

    @DataProvider
    public Object[][] sendRequestDataProvider() {
        return new Object[][] {
                { false, new AuftragBuilder(GeschaeftsfallTyp.BEREITSTELLUNG).buildValid(), false },
                { true, new AuftragBuilder(GeschaeftsfallTyp.BEREITSTELLUNG).buildValid(), true },
                { false, new StornoBuilder(GeschaeftsfallTyp.BEREITSTELLUNG).buildValid(), false },
                { true, new StornoBuilder(GeschaeftsfallTyp.BEREITSTELLUNG).buildValid(), true },
                { false, new TerminVerschiebungBuilder(GeschaeftsfallTyp.BEREITSTELLUNG).buildValid(), false },
                { true, new TerminVerschiebungBuilder(GeschaeftsfallTyp.BEREITSTELLUNG).buildValid(), true },
        };
    }

    @Test(dataProvider = "sendRequestDataProvider")
    public void testSendRequeset(boolean sendAllowed, MnetWitaRequest request, boolean requestSent) throws Exception {
        when(camelProxyLookupServiceMock.lookupCamelProxy(PROXY_LINE_ORDER,LineOrderService.class))
                .thenReturn(lineOrderService);

        when(witaConfigServiceMock.isSendAllowed(request)).thenReturn(sendAllowed);
        testling.sendAndProcessMessage(request);

        verify(camelProxyLookupServiceMock).lookupCamelProxy(PROXY_LINE_ORDER,LineOrderService.class);
        verify(witaConfigServiceMock).isSendAllowed(request);
        verify(lineOrderService, requestSent ? times(1) : times(0)).sendToWita(request);
    }

    @DataProvider
    public Object[][] sendScheduledRequestDataProvider() {
        return new Object[][] {
                { new AuftragBuilder(GeschaeftsfallTyp.BEREITSTELLUNG).buildValid() },
                { new StornoBuilder(GeschaeftsfallTyp.BEREITSTELLUNG).buildValid() },
                { new TerminVerschiebungBuilder(GeschaeftsfallTyp.BEREITSTELLUNG).buildValid() },
        };
    }

    @Test(dataProvider = "sendScheduledRequestDataProvider")
    public void testSendSchedueldRequeset(MnetWitaRequest request) throws Exception {
        when(mwfEntityDao.findById(request.getId(), MnetWitaRequest.class)).thenReturn(request);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object request = invocation.getArguments()[0];
                if (request instanceof Auftrag) {
                    ((Auftrag) request).setSentAt(new Date());
                }
                return invocation;
            }
        }).when(testling).sendAndProcessMessage(any(MnetWitaRequest.class));
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object request = invocation.getArguments()[0];
                if (request instanceof Storno || request instanceof TerminVerschiebung) {
                    ((MnetWitaRequest) request).setSentAt(new Date());
                }
                return invocation;
            }
        }).when(talOrderWorkflowService).sendTvOrStornoRequest(any(MnetWitaRequest.class));

        assertTrue(testling.sendScheduledRequest(request.getId()));
        verify(witaTalOrderService).modifyStandortKollokation(request);
        if (request.isAuftrag()) {
            verify(testling).sendAndProcessMessage(request);
            verify(talOrderWorkflowService, never()).sendTvOrStornoRequest(any(MnetWitaRequest.class));
        }
        if (request.isStorno() || request.isTv()) {
            verify(testling, never()).sendAndProcessMessage(any(WitaMessage.class));
            verify(talOrderWorkflowService).sendTvOrStornoRequest(request);
        }
    }

    @Test
    public void testHandleExceptionModifyStandortKollokation() throws Exception {
        Auftrag auftrag = new AuftragBuilder(GeschaeftsfallTyp.BEREITSTELLUNG).buildValid();
        when(mwfEntityDao.findById(auftrag.getId(), MnetWitaRequest.class)).thenReturn(auftrag);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object request = invocation.getArguments()[0];
                if (request instanceof MnetWitaRequest) {
                    ((MnetWitaRequest) request).setSentAt(new Date());
                }
                return invocation;
            }
        }).when(testling).sendAndProcessMessage(any(MnetWitaRequest.class));
        doThrow(Exception.class).when(witaTalOrderService).modifyStandortKollokation(any(MnetWitaRequest.class));

        assertTrue(testling.sendScheduledRequest(auftrag.getId()));
        verify(witaTalOrderService).modifyStandortKollokation(auftrag);
        verify(testling).sendAndProcessMessage(auftrag);
        verify(talOrderWorkflowService, never()).sendTvOrStornoRequest(any(MnetWitaRequest.class));
    }

    @Test(expectedExceptions = WitaBaseException.class, expectedExceptionsMessageRegExp = "Invalid MnetWitaRequest:.*")
    public void testWitaBaseException() throws Exception {
        Auftrag auftrag = new AuftragBuilder(GeschaeftsfallTyp.BEREITSTELLUNG).buildValid();
        when(mwfEntityDao.findById(auftrag.getId(), MnetWitaRequest.class)).thenReturn(auftrag);
        doThrow(new ValidationException("bla")).when(testling).sendAndProcessMessage(any(MnetWitaRequest.class));

        testling.sendScheduledRequest(auftrag.getId());
    }

    @Test(expectedExceptions = WitaBaseException.class, expectedExceptionsMessageRegExp = "Could not load already scheduled MnetWitaRequest with id '5'")
    public void testCouldNotLoadRequestException() throws Exception {
        when(mwfEntityDao.findById(5L, MnetWitaRequest.class)).thenReturn(null);
        testling.sendScheduledRequest(5L);
    }

}
