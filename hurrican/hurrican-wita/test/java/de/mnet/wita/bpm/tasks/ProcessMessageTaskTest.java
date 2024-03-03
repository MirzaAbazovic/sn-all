/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.06.2011 09:28:18
 */
package de.mnet.wita.bpm.tasks;

import static de.mnet.wita.message.GeschaeftsfallTyp.*;
import static de.mnet.wita.model.TamUserTask.TamBearbeitungsStatus.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.time.*;
import java.time.temporal.*;
import java.util.*;
import org.activiti.engine.delegate.DelegateExecution;
import org.apache.commons.lang.NotImplementedException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.model.AKUserBuilder;
import de.augustakom.authentication.service.exceptions.AKAuthenticationException;
import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.CarrierbestellungBuilder;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.wita.exceptions.AuftragNotFoundException;
import de.mnet.wita.exceptions.MessageOutOfOrderException;
import de.mnet.wita.exceptions.WitaBpmException;
import de.mnet.wita.message.Auftrag;
import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.message.auftrag.Auftragsposition.ProduktBezeichner;
import de.mnet.wita.message.builder.AuftragBuilder;
import de.mnet.wita.message.builder.meldung.AbbruchMeldungBuilder;
import de.mnet.wita.message.builder.meldung.AuftragsBestaetigungsMeldungBuilder;
import de.mnet.wita.message.builder.meldung.EntgeltMeldungBuilder;
import de.mnet.wita.message.builder.meldung.ErledigtMeldungBuilder;
import de.mnet.wita.message.builder.meldung.MessageBuilder;
import de.mnet.wita.message.builder.meldung.QualifizierteEingangsBestaetigungBuilder;
import de.mnet.wita.message.builder.meldung.VerzoegerungsMeldungBuilder;
import de.mnet.wita.message.meldung.AbbruchMeldung;
import de.mnet.wita.message.meldung.AuftragsBestaetigungsMeldung;
import de.mnet.wita.message.meldung.EntgeltMeldung;
import de.mnet.wita.message.meldung.ErledigtMeldung;
import de.mnet.wita.message.meldung.Meldung;
import de.mnet.wita.message.meldung.QualifizierteEingangsBestaetigung;
import de.mnet.wita.message.meldung.VerzoegerungsMeldung;
import de.mnet.wita.message.meldung.position.AenderungsKennzeichen;
import de.mnet.wita.message.meldung.position.Leitung;
import de.mnet.wita.message.meldung.position.LeitungsAbschnitt;
import de.mnet.wita.model.TamUserTask.TamBearbeitungsStatus;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.model.WitaCBVorgangBuilder;

@Test(groups = BaseTest.UNIT)
public class ProcessMessageTaskTest extends AbstractProcessingWitaTaskTest<ProcessMessageTask> {

    public ProcessMessageTaskTest() {
        super(ProcessMessageTask.class);
    }

    @Test(expectedExceptions = NotImplementedException.class)
    public void testTeq() throws Exception {
        DelegateExecution execution = createExecution(MeldungsType.TEQ);
        underTest.execute(execution);
    }

    @DataProvider
    public Object[][] meldungProvider() {
        // @formatter:off
        return new Object[][] {
            {
                MeldungsType.QEB, new QualifizierteEingangsBestaetigungBuilder(),
                QualifizierteEingangsBestaetigung.class, new WitaCBVorgang()
            },
            {
                MeldungsType.VZM, new VerzoegerungsMeldungBuilder(),
                VerzoegerungsMeldung.class, new WitaCBVorgang()
            }
        };
        // @formatter:on
    }

    @Test(dataProvider = "meldungProvider")
    public <T extends Meldung<?>> void testMeldung(MeldungsType meldungstype, MessageBuilder<T, ?, ?> builder,
            Class<T> clazz, WitaCBVorgang inputCBVorgang) throws Exception {
        witaCBVorgang = inputCBVorgang;
        witaCBVorgang.setWitaGeschaeftsfallTyp(BEREITSTELLUNG);
        DelegateExecution execution = createExecution(meldungstype);
        T meldung = builder.build();

        when(mwfEntityDao.findById(any(Long.class), eq(clazz))).thenReturn(meldung);
        when(carrierElTalService.findCBVorgang(any(Long.class))).thenReturn(inputCBVorgang);
        when(workflowTaskService.validateMwfInput(meldung, execution)).thenReturn(true);

        underTest.execute(execution);

        verify(workflowTaskService).validateMwfInput(meldung, execution);
        verify(mwfCbVorgangConverterService).write(eq(witaCBVorgang), eq(meldung));
    }

    @Test
    public void testEntm() throws Exception {
        witaCBVorgang.setWitaGeschaeftsfallTyp(BEREITSTELLUNG);
        DelegateExecution execution = createExecution(MeldungsType.ENTM);

        EntgeltMeldung meldung = new EntgeltMeldungBuilder().build();
        when(mwfEntityDao.findById(any(Long.class), eq(EntgeltMeldung.class))).thenReturn(meldung);

        try {
            underTest.execute(execution);
        }
        catch (MessageOutOfOrderException e) {
            return;
        }
        fail("No Exception thrown for ENTM in ProcessMessage!");
    }

    @Test
    public void testAbbmHvt() throws Exception {
        DelegateExecution execution = createExecution(MeldungsType.ABBM);
        witaCBVorgang.setCbVorgangRefId(1L);

        AbbruchMeldung abbm = (new AbbruchMeldungBuilder()).build();
        abbm.setId(1L);

        Auftrag auftrag = (new AuftragBuilder(BEREITSTELLUNG)).buildAuftragWithSchaltungKupfer();
        auftrag.getGeschaeftsfall().getAuftragsPosition().setProduktBezeichner(ProduktBezeichner.HVT_2H);

        when(mwfEntityDao.getAuftragOfCbVorgang(any(Long.class))).thenReturn(auftrag);
        when(mwfEntityDao.findById(abbm.getId(), AbbruchMeldung.class)).thenReturn(abbm);
        when(workflowTaskService.validateMwfInput(abbm, execution)).thenReturn(true);

        underTest.execute(execution);

        verify(workflowTaskService).validateMwfInput(abbm, execution);
        verify(mwfCbVorgangConverterService).write(eq(witaCBVorgang), eq(abbm));
        verify(witaTalOrderService).markWitaCBVorgangAsKlaerfall(witaCBVorgang.getId(), underTest.HVT_TO_KVZ_ABBM_AUF_NEU);
    }

    @Test
    public void testAbbmKue() throws Exception {
        DelegateExecution execution = createExecution(MeldungsType.ABBM);
        witaCBVorgang.setCbVorgangRefId(1L);
        witaCBVorgang.setWitaGeschaeftsfallTyp(KUENDIGUNG_KUNDE);

        AbbruchMeldung abbm = (new AbbruchMeldungBuilder()).build();
        abbm.setId(1L);

        Auftrag auftrag = (new AuftragBuilder(KUENDIGUNG_KUNDE)).buildAuftragWithSchaltungKupfer();
        auftrag.getGeschaeftsfall().getAuftragsPosition().setProduktBezeichner(ProduktBezeichner.HVT_2H);

        when(mwfEntityDao.getAuftragOfCbVorgang(any(Long.class))).thenReturn(auftrag);
        when(mwfEntityDao.findById(abbm.getId(), AbbruchMeldung.class)).thenReturn(abbm);
        when(workflowTaskService.validateMwfInput(abbm, execution)).thenReturn(true);

        underTest.execute(execution);

        verify(workflowTaskService).validateMwfInput(abbm, execution);
        verify(mwfCbVorgangConverterService).write(eq(witaCBVorgang), eq(abbm));
        verify(witaTalOrderService).checkHvtKueAndCancelKvzBereitstellung(witaCBVorgang.getId());
    }

    @Test
    public void testAbm() throws Exception {
        DelegateExecution execution = createExecution(MeldungsType.ABM);

        AuftragsBestaetigungsMeldung abm = (new AuftragsBestaetigungsMeldungBuilder()).build();
        abm.setId(1L);

        Auftrag auftrag = (new AuftragBuilder(BEREITSTELLUNG)).buildAuftragWithSchaltungKupfer();
        auftrag.getGeschaeftsfall().getAuftragsPosition().setProduktBezeichner(ProduktBezeichner.HVT_2H);

        when(mwfEntityDao.getAuftragOfCbVorgang(any(Long.class))).thenReturn(auftrag);
        when(mwfEntityDao.findById(abm.getId(), AuftragsBestaetigungsMeldung.class)).thenReturn(abm);
        when(workflowTaskService.validateMwfInput(abm, execution)).thenReturn(true);

        underTest.execute(execution);

        verify(workflowTaskService).validateMwfInput(abm, execution);
        verify(mwfCbVorgangConverterService).write(eq(witaCBVorgang), eq(abm));
        verify(witaCheckConditionService).checkConditionsForAbm(witaCBVorgang, abm);
    }

    @Test
    public void testAbmValidationFailed() throws Exception {
        DelegateExecution execution = createExecution(MeldungsType.ABM);

        AuftragsBestaetigungsMeldung abm = (new AuftragsBestaetigungsMeldungBuilder()).build();
        abm.setId(1L);

        Auftrag auftrag = (new AuftragBuilder(BEREITSTELLUNG)).buildAuftragWithSchaltungKupfer();
        auftrag.getGeschaeftsfall().getAuftragsPosition().setProduktBezeichner(ProduktBezeichner.HVT_2H);

        when(mwfEntityDao.getAuftragOfCbVorgang(any(Long.class))).thenReturn(auftrag);
        when(mwfEntityDao.findById(abm.getId(), AuftragsBestaetigungsMeldung.class)).thenReturn(abm);
        when(workflowTaskService.validateMwfInput(abm, execution)).thenReturn(false);

        underTest.execute(execution);

        verify(workflowTaskService).validateMwfInput(abm, execution);
        verify(mwfCbVorgangConverterService, never()).write(eq(witaCBVorgang), eq(abm));
        verify(witaCheckConditionService).checkConditionsForAbm(witaCBVorgang, abm);
    }

    @Test
    public void testPreprocessAbmForLAE() throws Exception {
        AuftragsBestaetigungsMeldung abm = (new AuftragsBestaetigungsMeldungBuilder())
                .withGeschaeftsfallTyp(LEISTUNGS_AENDERUNG).withLeitung(null).build();

        Carrierbestellung referencingCb = (new CarrierbestellungBuilder()).withLbz("96W/089/089/123456789")
                .withLl("100/50").withAqs("10/20").withMaxBruttoBitrate("3500").build();

        Carrierbestellung cbMock = mock(Carrierbestellung.class);
        when(carrierService.findCB(anyLong())).thenReturn(cbMock);
        when(witaDataService.getReferencingCarrierbestellung(any(WitaCBVorgang.class), eq(cbMock))).thenReturn(
                referencingCb);

        WitaCBVorgang cbVorgang = new WitaCBVorgang();
        underTest.preprocessAbm(null, cbVorgang, abm);

        verify(mwfEntityService).store(abm);
        verify(witaTalOrderService, never()).checkAndAdaptHvtToKvzBereitstellung(anyLong(), any(LocalDateTime.class), any(Date.class));

        Leitung result = abm.getLeitung();
        assertThat(result.getLeitungsBezeichnung().getLeitungsbezeichnungString(), equalTo("96W/89/89/0123456789"));
        assertThat(result.getLeitungsAbschnitte().toString(), equalTo(LeitungsAbschnitt.valueOf("100/50", "10/20")
                .toString()));
        assertThat(result.getMaxBruttoBitrate(), equalTo("3500"));
        verify(underTest).checkAndAdaptEarliestSendDateForBereitstellungAuftrag(cbVorgang, abm);
        verify(underTest).checkVerbindlicherLieferterminHvtToKvzWechsel(cbVorgang, abm);
        verify(witaCheckConditionService).checkConditionsForAbm(cbVorgang, abm);
    }

    @Test
    public void testPreprocessAbmForRexMk() throws Exception {
        AuftragsBestaetigungsMeldung abm = (new AuftragsBestaetigungsMeldungBuilder())
                .withGeschaeftsfallTyp(RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG).withLeitung(null).build();
        underTest.preprocessAbm(null, new WitaCBVorgang(), abm);

        verify(witaTalOrderService, never()).checkAndAdaptHvtToKvzBereitstellung(anyLong(), any(LocalDateTime.class), any(Date.class));
        verify(witaCheckConditionService).checkConditionsForAbm(any(WitaCBVorgang.class), any(AuftragsBestaetigungsMeldung.class));
    }

    @Test
    public void testPreprocessAbmForKueKd() throws Exception {
        LocalDateTime versandZeitstempel = LocalDateTime.now();
        LocalDateTime expectedEsd = LocalDateTime.ofInstant(versandZeitstempel.atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault()).truncatedTo(ChronoUnit.DAYS);
        LocalDate vlt = LocalDate.now();
        Date expectedVlt = Date.from(vlt.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Long cbId = 1L;
        AuftragsBestaetigungsMeldung abm = (new AuftragsBestaetigungsMeldungBuilder())
                .withGeschaeftsfallTyp(KUENDIGUNG_KUNDE)
                .withLeitung(null)
                .withVersandZeitstempel(versandZeitstempel)
                .withVerbindlicherLiefertermin(vlt)
                .build();

        WitaCBVorgang cbVorgang = new WitaCBVorgangBuilder()
                .withWitaGeschaeftsfallTyp(KUENDIGUNG_KUNDE)
                .withId(cbId)
                .build();

        underTest.preprocessAbm(null, cbVorgang, abm);

        verify(witaTalOrderService).checkAndAdaptHvtToKvzBereitstellung(cbId, expectedEsd, expectedVlt);
        verify(witaCheckConditionService).checkConditionsForAbm(cbVorgang, abm);
    }

    @Test
    public void testPreprocessAbmException() throws Exception {
        AuftragsBestaetigungsMeldung abm = (new AuftragsBestaetigungsMeldungBuilder())
                .withGeschaeftsfallTyp(LEISTUNGS_AENDERUNG).withLeitung(null).build();

        when(carrierService.findCB(anyLong())).thenThrow(new FindException());

        WitaCBVorgang cbVorgang = new WitaCBVorgang();
        underTest.preprocessAbm(null, cbVorgang, abm);

        verify(workflowTaskService).setWorkflowToError(any(DelegateExecution.class), anyString(),
                any(FindException.class));
    }

    @DataProvider
    public Object[][] dataProviderErlm() {
        // @formatter:off
        return new Object[][] {
                { AenderungsKennzeichen.STANDARD, true, true, true },
                { AenderungsKennzeichen.STANDARD, true, false, true },
                { AenderungsKennzeichen.STANDARD, false, true, false },
                { AenderungsKennzeichen.STANDARD, false, false, false },

                { AenderungsKennzeichen.STORNO, true, true, true },
                { AenderungsKennzeichen.STORNO, true, false, false },
                { AenderungsKennzeichen.STORNO, false, true, true },
                { AenderungsKennzeichen.STORNO, false, false, false },
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderErlm")
    public void testErlm(AenderungsKennzeichen aenderungsKennzeichen, boolean abmReceived, boolean stornoSent,
            boolean expectedResult) throws Exception {
        DelegateExecution execution = createExecution(MeldungsType.ERLM);

        Auftrag auftrag = (new AuftragBuilder(BEREITSTELLUNG)).withExterneAuftragsnummer(
                witaCBVorgang.getCarrierRefNr()).buildValid();
        ErledigtMeldung erlm = (new ErledigtMeldungBuilder()).withId(1L)
                .withAenderungsKennzeichen(aenderungsKennzeichen).build();

        when(mwfEntityDao.getAuftragOfCbVorgang(witaCBVorgang.getId())).thenReturn(auftrag);
        when(mwfEntityDao.findById(erlm.getId(), ErledigtMeldung.class)).thenReturn(erlm);

        when(
                mwfEntityService.checkMeldungReceived(auftrag.getExterneAuftragsnummer(),
                        AuftragsBestaetigungsMeldung.class)
        ).thenReturn(abmReceived);
        if (!stornoSent) {
            when(mwfEntityDao.getStornosOfCbVorgang(witaCBVorgang.getId())).thenThrow(new AuftragNotFoundException(""));
        }
        when(workflowTaskService.validateMwfInput(erlm, execution)).thenReturn(true);

        underTest.execute(execution);

        if (expectedResult) {
            verify(mwfCbVorgangConverterService).write(witaCBVorgang, erlm);
            verify(workflowTaskService).validateMwfInput(erlm, execution);
        }
        else {
            verify(workflowTaskService).setWorkflowToError(eq(execution), anyString());
        }
        verifyNoMoreInteractions(mwfCbVorgangConverterService, workflowTaskService);
    }

    @Test
    public void testWorkflowError() throws Exception {
        DelegateExecution execution = createExecution(null, null, null, null, true);

        underTest.execute(execution);

        verify(workflowTaskService).setWorkflowToError(eq(execution), anyString());
        verifyNoMoreInteractions(mwfCbVorgangConverterService, workflowTaskService);
    }

    @Test(expectedExceptions = WitaBpmException.class)
    public void testStorno() throws Exception {
        underTest.processMessage(createExecution(MeldungsType.STORNO));
    }

    @Test(expectedExceptions = WitaBpmException.class)
    public void testTv() throws Exception {
        underTest.processMessage(createExecution(MeldungsType.TV));
    }

    private DelegateExecution createExecution(MeldungsType messageType) throws FindException {
        return createExecution(witaCBVorgang, messageType, 1L, null, false);
    }

    @Test
    public void checkVerbindlicherLieferterminHvtToKvzWechselGfTypLae() throws StoreException,
            AKAuthenticationException, ValidationException, FindException {
        WitaCBVorgang cbVorgang = new WitaCBVorgangBuilder()
                .withWitaGeschaeftsfallTyp(LEISTUNGS_AENDERUNG)
                .build();
        underTest.checkVerbindlicherLieferterminHvtToKvzWechsel(cbVorgang, null);
        verify(witaTalOrderService, never()).markWitaCBVorgangAsKlaerfall(anyLong(), anyString());
        verify(userService, never()).findById(anyLong());
        verify(carrierElTalService, never()).findCBVorgang(anyLong());
        verify(witaTalOrderService, never()).
                doTerminverschiebung(anyLong(),
                        any(LocalDate.class), any(AKUser.class), anyBoolean(),
                        anyString(), any(TamBearbeitungsStatus.class));
    }

    @Test
    public void checkVerbindlicherLieferterminHvtToKvzWechselNotHvtToKvz() throws StoreException,
            AKAuthenticationException, ValidationException, FindException {
        WitaCBVorgang cbVorgang = new WitaCBVorgangBuilder()
                .withWitaGeschaeftsfallTyp(BEREITSTELLUNG)
                .build();
        underTest.checkVerbindlicherLieferterminHvtToKvzWechsel(cbVorgang, null);
        verify(witaTalOrderService, never()).markWitaCBVorgangAsKlaerfall(anyLong(), anyString());
        verify(userService, never()).findById(anyLong());
        verify(carrierElTalService, never()).findCBVorgang(anyLong());
        verify(witaTalOrderService, never()).doTerminverschiebung(anyLong(), any(LocalDate.class), any(AKUser.class),
                anyBoolean(), anyString(), any(TamBearbeitungsStatus.class));
    }

    @Test
    public void checkVerbindlicherLieferterminHvtToKvzWechselAbweichend() throws StoreException,
            AKAuthenticationException, ValidationException, FindException {
        Date vorgabeDatum = new Date();
        AuftragsBestaetigungsMeldung abm = new AuftragsBestaetigungsMeldungBuilder()
                .withGeschaeftsfallTyp(BEREITSTELLUNG)
                .withVerbindlicherLiefertermin(DateConverterUtils.asLocalDate(vorgabeDatum).plusDays(1))
                .build();
        WitaCBVorgang kuendigung = new WitaCBVorgangBuilder()
                .withId(3L)
                .withWitaGeschaeftsfallTyp(KUENDIGUNG_KUNDE)
                .withReturnRealDate(vorgabeDatum)
                .build();
        WitaCBVorgang neubestellung = new WitaCBVorgangBuilder()
                .withId(2L)
                .withWitaGeschaeftsfallTyp(BEREITSTELLUNG)
                .withCbVorgangRefId(kuendigung.getId())
                .build();
        final AKUser akUser = new AKUserBuilder().withRandomId().build();
        when(userService.findById(neubestellung.getUserId())).thenReturn(akUser);
        when(carrierElTalService.findCBVorgang(neubestellung.getCbVorgangRefId())).thenReturn(kuendigung);
        when(witaTalOrderService.doTerminverschiebung(kuendigung.getId(), abm.getVerbindlicherLiefertermin(),
                akUser, true, null, TV_60_TAGE)).thenReturn(neubestellung);

        underTest.checkVerbindlicherLieferterminHvtToKvzWechsel(neubestellung, abm);
        verify(userService).findById(neubestellung.getUserId());
        verify(carrierElTalService).findCBVorgang(neubestellung.getCbVorgangRefId());
        verify(witaTalOrderService).doTerminverschiebung(kuendigung.getId(), abm.getVerbindlicherLiefertermin(),
                akUser, true, null, TV_60_TAGE);
    }

    @Test
    public void checkVerbindlicherLieferterminHvtToKvzWechsel() throws StoreException, FindException, ValidationException {
        Date vorgabeDatum = new Date();
        AuftragsBestaetigungsMeldung abm = new AuftragsBestaetigungsMeldungBuilder()
                .withGeschaeftsfallTyp(BEREITSTELLUNG)
                .withVerbindlicherLiefertermin(DateConverterUtils.asLocalDate(vorgabeDatum))
                .build();
        WitaCBVorgang kuendigung = new WitaCBVorgangBuilder()
                .withId(1L)
                .withWitaGeschaeftsfallTyp(KUENDIGUNG_KUNDE)
                .withReturnRealDate(vorgabeDatum)
                .build();
        WitaCBVorgang neubestellung = new WitaCBVorgangBuilder()
                .withId(2L)
                .withWitaGeschaeftsfallTyp(BEREITSTELLUNG)
                .withCbVorgangRefId(kuendigung.getId())
                .build();
        when(carrierElTalService.findCBVorgang(neubestellung.getCbVorgangRefId())).thenReturn(kuendigung);

        underTest.checkVerbindlicherLieferterminHvtToKvzWechsel(neubestellung, abm);
        verify(witaTalOrderService, never()).markWitaCBVorgangAsKlaerfall(anyLong(), anyString());
        verify(witaTalOrderService, never()).doTerminverschiebung(anyLong(), any(LocalDate.class),
                any(AKUser.class), anyBoolean(), anyString(), any(TamBearbeitungsStatus.class));
    }

}
