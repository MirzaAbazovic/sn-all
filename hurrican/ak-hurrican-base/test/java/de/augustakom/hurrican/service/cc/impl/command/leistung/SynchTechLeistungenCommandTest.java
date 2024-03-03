package de.augustakom.hurrican.service.cc.impl.command.leistung;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import com.google.common.collect.ImmutableList;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.service.base.AKServiceCommandChain;
import de.augustakom.common.service.iface.IServiceCommand;
import de.augustakom.common.tools.lang.RandomTools;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.dao.cc.ProfileDAO;
import de.augustakom.hurrican.model.cc.Auftrag2TechLeistung;
import de.augustakom.hurrican.model.cc.Auftrag2TechLeistungBuilder;
import de.augustakom.hurrican.model.cc.AuftragAktion;
import de.augustakom.hurrican.model.cc.AuftragAktionBuilder;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.command.ServiceCommand;
import de.augustakom.hurrican.model.cc.view.LeistungsDiffView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.ChainService;
import de.augustakom.hurrican.service.cc.ProfileService;
import de.mnet.common.service.locator.ServiceLocator;

public class SynchTechLeistungenCommandTest {

    @Spy
    private SynchTechLeistungenCommand cut;

    @Mock
    private CCLeistungsService leistungsService;
    @Mock
    private ProfileService profileService;
    @Mock
    private ChainService chainService;
    @Mock
    private AKServiceCommandChain serviceChain;
    @Mock
    private ServiceLocator serviceLocator;
    @Mock
    private ProfileDAO profileDAO;


    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(cut, "ccls", leistungsService);
        ReflectionTestUtils.setField(cut, "profileService", profileService);
        ReflectionTestUtils.setField(cut, "serviceLocator", serviceLocator);
        doReturn(chainService).when(cut).getCCService(ChainService.class);
        doReturn(serviceChain).when(cut).createAkServiceCommandChain();
    }

    @Test
    public void testSyncTechLeistungen4Auftrag_WithZugaenge() throws Exception {
        final Long quantity = 2L;
        final AuftragAktion auftragAktion = new AuftragAktionBuilder().withRandomId().build();
        final LeistungsDiffView zugang = createZugang(quantity);

        prepareAndInitCommandUnderTest(auftragAktion, zugang.getAuftragId(), zugang);

        cut.synchTechLeistungen4Auftrag();

        final ArgumentCaptor<Auftrag2TechLeistung> a2tlCaptor = ArgumentCaptor.forClass(Auftrag2TechLeistung.class);
        verify(leistungsService, times(quantity.intValue())).saveAuftrag2TechLeistung(a2tlCaptor.capture());

        for (final Auftrag2TechLeistung savedA2Tl : a2tlCaptor.getAllValues()) {
            assertThat(savedA2Tl.getAuftragId(), equalTo(zugang.getAuftragId()));
            assertThat(savedA2Tl.getTechLeistungId(), equalTo(zugang.getTechLeistungId()));
            assertThat(savedA2Tl.getAktivVon(), equalTo(zugang.getAktivVon()));
            assertThat(savedA2Tl.getAktivBis(), equalTo(zugang.getAktivBis()));
            assertThat(savedA2Tl.getQuantity(), equalTo(1L));
            assertThat(savedA2Tl.getAuftragAktionsIdAdd(), equalTo(auftragAktion.getId()));
            assertThat(savedA2Tl.getAuftragAktionsIdRemove(), nullValue(Long.class));
        }
    }


    private LeistungsDiffView createZugang(Long quantity) {
        final LeistungsDiffView zugang = new LeistungsDiffView();
        zugang.setAuftragId(815L);
        zugang.setTechLeistungId(518L);
        zugang.setAktivVon(new Date());
        zugang.setAktivBis(Date.from(LocalDate.now().plusDays(2).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        zugang.setZugang(true);
        zugang.setQuantity(quantity);
        return zugang;
    }

    private void prepareAndInitCommandUnderTest(AuftragAktion auftragAktion, long auftragId, LeistungsDiffView... leistungsDiffs) throws FindException {
        cut.prepare(SynchTechLeistungenCommand.KEY_AUFTRAG_ID, auftragId);
        cut.prepare(SynchTechLeistungenCommand.KEY_AUFTRAG_AKTION, auftragAktion);
        cut.prepare(SynchTechLeistungenCommand.KEY_LEISTUNG_DIFF, ImmutableList.copyOf(leistungsDiffs));
        cut.prepare(SynchTechLeistungenCommand.KEY_PROD_ID, 1234L);
        cut.checkValues();
    }

    @Test
    public void testSyncTechLeistungen4Auftrag_WithAbgaenge() throws Exception {
        final AuftragAktion auftragAktion = new AuftragAktionBuilder().withRandomId().build();
        final LeistungsDiffView abgang = createAbgang();

        final Auftrag2TechLeistung a2Tl = new Auftrag2TechLeistungBuilder().withRandomId().build();

        when(leistungsService.findAuftrag2TechLeistungen(abgang.getAuftragId(),
                abgang.getTechLeistungId(), true)).thenReturn(ImmutableList.of(a2Tl));

        prepareAndInitCommandUnderTest(auftragAktion, abgang.getAuftragId(), abgang);
        cut.synchTechLeistungen4Auftrag();

        verify(leistungsService).saveAuftrag2TechLeistung(a2Tl);

        assertTrue(Date.from(a2Tl.getAktivBis().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant()).equals(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant())));
        assertThat(a2Tl.getAuftragAktionsIdRemove(), equalTo(auftragAktion.getId()));
    }

    private LeistungsDiffView createAbgang() {
        final LeistungsDiffView abgang = new LeistungsDiffView();
        abgang.setAuftragId(RandomTools.createLong());
        abgang.setTechLeistungId(RandomTools.createLong());
        abgang.setZugang(false);
        abgang.setQuantity(1L);
        return abgang;
    }


    @Test
    public void testSyncTechLeistungen4Auftrag_ExecuteTechLsCommands() throws Exception {
        LeistungsDiffView lsDiff = createZugang(1L);
        final Auftrag2TechLeistung a2Tl = new Auftrag2TechLeistungBuilder().withRandomId().build();
        a2Tl.setTechLeistungId(lsDiff.getTechLeistungId());
        a2Tl.setAuftragId(lsDiff.getAuftragId());
        a2Tl.setAktivVon(lsDiff.getAktivVon());
        a2Tl.setAktivBis(lsDiff.getAktivBis());
        final ServiceCommand lsCommand = new ServiceCommand() {
        };
        lsCommand.setClassName(RandomTools.createString());
        final AKWarnings expectedWarnings = new AKWarnings();
        expectedWarnings.addAKWarning(this, RandomTools.createString());
        final IServiceCommand serviceCmd = mock(IServiceCommand.class);

        when(leistungsService.findAuftrag2TechLeistungen(lsDiff.getAuftragId(),
                lsDiff.getTechLeistungId(), true)).thenReturn(ImmutableList.of(a2Tl));
        when(chainService.findServiceCommands4Reference(lsDiff.getTechLeistungId(), TechLeistung.class, ServiceCommand.COMMAND_TYPE_LS_ZUGANG))
                .thenReturn(ImmutableList.of(lsCommand));
        when(serviceChain.getWarnings()).thenReturn(expectedWarnings);
        when(serviceLocator.getCmdBean(lsCommand.getClassName())).thenReturn(serviceCmd);

        cut.prepare(SynchTechLeistungenCommand.KEY_EXECUTE_LS_COMMANDS, true);
        prepareAndInitCommandUnderTest(null, lsDiff.getAuftragId(), lsDiff);
        cut.synchTechLeistungen4Auftrag();

        assertTrue(cut.getWarnings().isNotEmpty());

        verify(serviceCmd).prepare(AbstractLeistungCommand.KEY_AUFTRAG_ID, lsDiff.getAuftragId());
        verify(serviceCmd).prepare(AbstractLeistungCommand.KEY_TECH_LEISTUNG_ID, lsDiff.getTechLeistungId());
        verify(serviceCmd).prepare(AbstractLeistungCommand.KEY_TECH_LEISTUNG_AKTIV_VON, lsDiff.getAktivVon());
        verify(serviceCmd).prepare(AbstractLeistungCommand.KEY_TECH_LEISTUNG_AKTIV_BIS, lsDiff.getAktivBis());
        verify(serviceCmd).prepare(AbstractLeistungCommand.KEY_SERVICE_CALLBACK, null);

        verify(serviceChain).executeChain();
    }

}
