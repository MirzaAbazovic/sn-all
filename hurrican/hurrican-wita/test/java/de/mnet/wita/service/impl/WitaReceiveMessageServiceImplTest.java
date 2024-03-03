package de.mnet.wita.service.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wita.bpm.AbgebendPvWorkflowService;
import de.mnet.wita.bpm.CommonWorkflowService;
import de.mnet.wita.bpm.KueDtWorkflowService;
import de.mnet.wita.bpm.TalOrderWorkflowService;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.builder.meldung.EntgeltMeldungBuilder;
import de.mnet.wita.message.builder.meldung.ErledigtMeldungBuilder;
import de.mnet.wita.message.meldung.EntgeltMeldung;
import de.mnet.wita.message.meldung.ErledigtMeldung;
import de.mnet.wita.message.meldung.IncomingTalOrderMeldung;
import de.mnet.wita.service.WitaReceiveMessageService;

@Test(groups = BaseTest.UNIT)
public class WitaReceiveMessageServiceImplTest extends BaseTest {

    @Mock
    private AbgebendPvWorkflowService abgebendPvWorkflowService;
    @Mock
    private CommonWorkflowService commonWorkflowService;
    @Mock
    private KueDtWorkflowService kueDtWorkflowService;
    @Mock
    private TalOrderWorkflowService talOrderWorkflowService;

    @InjectMocks
    @Spy
    private WitaReceiveMessageService testling = new WitaReceiveMessageServiceImpl();

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testProcessErlmKueKd() throws Exception {
        String externeAuftragsnummer = "12345678";
        ErledigtMeldung meldung = new ErledigtMeldungBuilder()
                .withGeschaeftsfallTyp(GeschaeftsfallTyp.KUENDIGUNG_TELEKOM)
                .withExterneAuftragsnummer(externeAuftragsnummer)
                .build();

        testling.handleWitaMessage(meldung);

        final String expectedExterneAuftragsnummer = externeAuftragsnummer + "-kuedt";
        verify(kueDtWorkflowService, never()).handleWitaMessage((IncomingTalOrderMeldung) any());
        verify(kueDtWorkflowService).newProcessInstance((ErledigtMeldung) argThat(new ArgumentMatcher() {
            @Override
            public boolean matches(Object o) {
                ErledigtMeldung erledigtMeldung = (ErledigtMeldung) o;
                return erledigtMeldung != null
                        && expectedExterneAuftragsnummer.equals(erledigtMeldung.getBusinessKey());
            }
        }));
    }

    @Test
    public void testProcessEntmKueKd() throws Exception {
        String externeAuftragsnummer = "12345678";
        EntgeltMeldung meldung = new EntgeltMeldungBuilder()
                .withGeschaeftsfallTyp(GeschaeftsfallTyp.KUENDIGUNG_TELEKOM)
                .withExterneAuftragsnummer(externeAuftragsnummer)
                .build();

        testling.handleWitaMessage(meldung);

        final String expectedExterneAuftragsnummer = externeAuftragsnummer + "-kuedt";
        verify(kueDtWorkflowService, never()).newProcessInstance((ErledigtMeldung) any());
        verify(kueDtWorkflowService).handleWitaMessage((ErledigtMeldung) argThat(new ArgumentMatcher() {
            @Override
            public boolean matches(Object o) {
                EntgeltMeldung entgeltMeldung = (EntgeltMeldung) o;
                return entgeltMeldung != null
                        && expectedExterneAuftragsnummer.equals(entgeltMeldung.getExterneAuftragsnummer());
            }
        }));
    }

}