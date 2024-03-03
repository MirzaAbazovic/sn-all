/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.03.2012 17:23:57
 */
package de.mnet.wita.service.impl;

import static de.mnet.wita.message.GeschaeftsfallTyp.*;
import static de.mnet.wita.service.WitaCheckConditionService.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.anySetOf;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.eq;
import static org.testng.Assert.*;

import java.time.*;
import java.time.temporal.*;
import java.util.*;
import com.google.common.collect.ImmutableList;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.service.cc.ReferenceService;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.wbci.exception.WbciValidationException;
import de.mnet.wita.exceptions.WitaUserException;
import de.mnet.wita.message.Auftrag;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.auftrag.Kundenwunschtermin.Zeitfenster;
import de.mnet.wita.message.builder.AuftragBuilder;
import de.mnet.wita.message.builder.meldung.AbbruchMeldungBuilder;
import de.mnet.wita.message.meldung.AbbruchMeldung;
import de.mnet.wita.message.meldung.AuftragsBestaetigungsMeldung;
import de.mnet.wita.message.meldung.ErledigtMeldung;
import de.mnet.wita.message.meldung.Meldung;
import de.mnet.wita.message.meldung.QualifizierteEingangsBestaetigung;
import de.mnet.wita.message.meldung.TerminAnforderungsMeldung;
import de.mnet.wita.message.meldung.position.AenderungsKennzeichen;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.model.WitaCBVorgangBuilder;
import de.mnet.wita.service.MwfEntityService;
import de.mnet.wita.service.WitaCheckConditionService;
import de.mnet.wita.service.WitaConfigService;
import de.mnet.wita.service.WitaTalOrderService;
import de.mnet.wita.service.WitaWbciServiceFacade;

@Test(groups = BaseTest.UNIT)
public class WitaCheckConditionServiceImplTest extends BaseTest {

    @Spy
    @InjectMocks
    private WitaCheckConditionServiceImpl testling;
    @Mock
    private WitaWbciServiceFacade witaWbciServiceFacade;
    @Mock
    private WitaDataService witaDataService;
    @Mock
    private MwfEntityService mwfEntityService;
    @Mock
    private WitaTalOrderService witaTalOrderService;
    @Mock
    private DateTimeCalculationService dateTimeCalculationService;

    @BeforeMethod
    public void setUp() {
        testling = new WitaCheckConditionServiceImpl();
        MockitoAnnotations.initMocks(this);

        when(dateTimeCalculationService.isStornoPossible(any(Date.class), any(Zeitfenster.class))).thenReturn(true);
        when(dateTimeCalculationService.isTerminverschiebungPossible(any(Date.class), any(Zeitfenster.class)))
                .thenReturn(true);
        when(dateTimeCalculationService.isTerminverschiebungValid(any(LocalDate.class), any(Zeitfenster.class),
                 anyBoolean(), any(GeschaeftsfallTyp.class), anyString(), anyBoolean())
        ).thenReturn(true);
    }

    public void testStorno() throws Exception {
        checkStornoWithQeb(witaCbVorgang("TEST0000"));
    }

    @Test(expectedExceptions = WitaUserException.class, expectedExceptionsMessageRegExp = WitaCheckConditionService.STORNO_ONLY_AFTER_QEB)
    public void testStornoFailsNoQeb() throws Exception {
        checkStorno(witaCbVorgang("TEST111111"));
    }

    @Test(expectedExceptions = WitaUserException.class, expectedExceptionsMessageRegExp = WitaCheckConditionService.STORNO_WITH_KLAMMER_ONLY_AFTER_ABM)
    public void testStornoFailsNoAbmForKlammerung() throws Exception {
        String extAuftragsnr = "TEST123";
        Auftrag auftrag = new AuftragBuilder(BEREITSTELLUNG).withExterneAuftragsnummer(extAuftragsnr)
                .withAuftragsKenner(2L, 3).buildValid();
        WitaCBVorgang witaCbVorgang = witaCbVorgang(extAuftragsnr);

        when(
                mwfEntityService.checkMeldungReceived(witaCbVorgang.getCarrierRefNr(),
                        QualifizierteEingangsBestaetigung.class)
        ).thenReturn(true);

        testling.checkConditionsForStorno(witaCbVorgang, auftrag);
    }

    @Test(expectedExceptions = WitaUserException.class, expectedExceptionsMessageRegExp = WitaCheckConditionService.STORNO_ONLY_BEFORE_ERLM)
    public void testStornoFailsErlmSend() throws Exception {
        String extAuftragsnr = "TEST0000222";
        when(mwfEntityService.checkMeldungReceived(extAuftragsnr, ErledigtMeldung.class)).thenReturn(true);
        checkStornoWithQeb(witaCbVorgang(extAuftragsnr));
    }

    @Test(expectedExceptions = WitaUserException.class, expectedExceptionsMessageRegExp = WitaCheckConditionService.STORNO_ONLY_BEFORE_ABBM)
    public void testStornoFailsAbbmStandardSend() throws Exception {
        String extAuftragsnr = "TEST0000223";
        when(
                mwfEntityService.findMwfEntitiesByProperty(AbbruchMeldung.class, Meldung.EXTERNE_AUFTRAGSNR_FIELD,
                        extAuftragsnr)
        ).thenReturn(
                ImmutableList.of((new AbbruchMeldungBuilder())
                        .withAenderungsKennzeichen(AenderungsKennzeichen.STANDARD).build())
        );
        checkStornoWithQeb(witaCbVorgang(extAuftragsnr));
    }

    public void testStornoOkAbbmStornoSend() throws Exception {
        String extAuftragsnr = "TEST0000224";
        when(
                mwfEntityService.findMwfEntitiesByProperty(AbbruchMeldung.class, Meldung.EXTERNE_AUFTRAGSNR_FIELD,
                        extAuftragsnr)
        ).thenReturn(
                ImmutableList.of((new AbbruchMeldungBuilder()).withAenderungsKennzeichen(AenderungsKennzeichen.STORNO)
                        .build())
        );
        checkStornoWithQeb(witaCbVorgang(extAuftragsnr));
    }

    public void testStornoOnNotSentRequest() {
        String extAuftragsnr = "TEST008899";
        WitaCBVorgang witaCbVorgang = witaCbVorgang(extAuftragsnr);
        Auftrag auftrag = (new AuftragBuilder(GeschaeftsfallTyp.BEREITSTELLUNG)).withExterneAuftragsnummer(
                extAuftragsnr).buildValid();
        when(mwfEntityService.findUnsentRequest(witaCbVorgang.getId())).thenReturn(auftrag);

        testling.checkConditionsForStorno(witaCbVorgang, auftrag);
    }

    @Test
    public void testTv() throws Exception {
        checkTvWithQeb(witaCbVorgang("TEST0000"));
    }

    @Test
    public void testTvWithWbciVaId() throws Exception {
        String wbciVaId = "DEU.MNET.V0001";
        LocalDate requestedTvDate = DateConverterUtils.asLocalDate(getValidVorgabeMnet());

        WitaCBVorgang witaCbVorgang = witaCbVorgang("TEST0000");
        witaCbVorgang.setVorabstimmungsId(wbciVaId);
        Auftrag auftrag = new AuftragBuilder(BEREITSTELLUNG)
                .withExterneAuftragsnummer(witaCbVorgang.getCarrierRefNr())
                .buildValid();
        when(mwfEntityService.checkMeldungReceived(witaCbVorgang.getCarrierRefNr(),
                QualifizierteEingangsBestaetigung.class)).thenReturn(true);
        doNothing().when(witaWbciServiceFacade).checkDateForMatchingWithVorabstimmung(any(Date.class), anyString());

        testling.checkConditionsForTv(witaCbVorgang, auftrag, requestedTvDate);
        verify(witaWbciServiceFacade).checkDateForMatchingWithVorabstimmung(Date.from(requestedTvDate.atStartOfDay(ZoneId.systemDefault()).toInstant()), wbciVaId);
    }

    @Test
    public void testTvWithWbciVaIdAfterTam() throws Exception {
        String wbciVaId = "DEU.MNET.V0001";
        LocalDate requestedTvDate = DateConverterUtils.asLocalDate(getValidVorgabeMnet());

        WitaCBVorgang witaCbVorgang = witaCbVorgang("TEST0000");
        witaCbVorgang.setVorabstimmungsId(wbciVaId);
        Auftrag auftrag = new AuftragBuilder(BEREITSTELLUNG)
                .withExterneAuftragsnummer(witaCbVorgang.getCarrierRefNr())
                .buildValid();

        when(mwfEntityService.checkMeldungReceived(witaCbVorgang.getCarrierRefNr(),
                QualifizierteEingangsBestaetigung.class)).thenReturn(true);
        when(mwfEntityService.checkMeldungReceived(witaCbVorgang.getCarrierRefNr(),
                TerminAnforderungsMeldung.class)).thenReturn(true);

        testling.checkConditionsForTv(witaCbVorgang, auftrag, requestedTvDate);
        verify(witaWbciServiceFacade, never()).checkDateForMatchingWithVorabstimmung(any(Date.class), anyString());
    }

    @Test(expectedExceptions = WitaUserException.class, expectedExceptionsMessageRegExp = "Eine Terminverschiebung für TEST222222 ist nur möglich, wenn für den Ursprungsauftrag bereits eine qualifizierte Eingangsbestätigung eingegangen ist.")
    public void testTvFailsNoQeb() throws Exception {
        WitaCBVorgang witaCbVorgang = witaCbVorgang("TEST222222");
        Auftrag auftrag = new AuftragBuilder(BEREITSTELLUNG).withExterneAuftragsnummer(witaCbVorgang.getCarrierRefNr())
                .buildValid();

        testling.checkConditionsForTv(witaCbVorgang, auftrag, DateConverterUtils.asLocalDate(getValidVorgabeMnet()));
    }

    @Test(expectedExceptions = WitaUserException.class, expectedExceptionsMessageRegExp = "Eine Terminverschiebung für TEST124 ist nur möglich, wenn für den geklammerten Ursprungsauftrag bereits eine Auftragsbestätigung eingegangen ist.")
    public void testTvFailsNoAbmForKlammerung() throws Exception {
        String extAuftragsnr = "TEST124";
        Auftrag auftrag = new AuftragBuilder(BEREITSTELLUNG).withExterneAuftragsnummer(extAuftragsnr)
                .withAuftragsKenner(2L, 3).buildValid();
        WitaCBVorgang witaCbVorgang = witaCbVorgang(extAuftragsnr);

        when(
                mwfEntityService.checkMeldungReceived(witaCbVorgang.getCarrierRefNr(),
                        QualifizierteEingangsBestaetigung.class)
        ).thenReturn(true);

        testling.checkConditionsForTv(witaCbVorgang, auftrag, (LocalDate.now()).plusDays(1));
    }

    @Test(expectedExceptions = WitaUserException.class, expectedExceptionsMessageRegExp = "Eine Terminverschiebung für TEST0000227 ist nur möglich, wenn für den Auftrag noch keine Erledigtmeldung eingegangen ist.")
    public void testTvFailsErlmSend() throws Exception {
        String extAuftragsnr = "TEST0000227";
        when(mwfEntityService.checkMeldungReceived(extAuftragsnr, ErledigtMeldung.class)).thenReturn(true);
        checkTvWithQeb(witaCbVorgang(extAuftragsnr));
    }

    @Test(expectedExceptions = WitaUserException.class, expectedExceptionsMessageRegExp = "Eine Terminverschiebung für TEST0000228 ist nur möglich, wenn für den Auftrag noch keine Abbruchmeldung eingegangen ist.")
    public void testTvFailsAbbmStandardSend() throws Exception {
        String extAuftragsnr = "TEST0000228";
        when(
                mwfEntityService.findMwfEntitiesByProperty(AbbruchMeldung.class, Meldung.EXTERNE_AUFTRAGSNR_FIELD,
                        extAuftragsnr)
        ).thenReturn(
                ImmutableList.of((new AbbruchMeldungBuilder())
                        .withAenderungsKennzeichen(AenderungsKennzeichen.STANDARD).build())
        );
        checkTvWithQeb(witaCbVorgang(extAuftragsnr));
    }

    public void testTvOkAbbmStornoSend() throws Exception {
        String extAuftragsnr = "TEST0000229";
        when(
                mwfEntityService.findMwfEntitiesByProperty(AbbruchMeldung.class, Meldung.EXTERNE_AUFTRAGSNR_FIELD,
                        extAuftragsnr)
        ).thenReturn(
                ImmutableList.of((new AbbruchMeldungBuilder()).withAenderungsKennzeichen(AenderungsKennzeichen.STORNO)
                        .build())
        );
        checkTvWithQeb(witaCbVorgang(extAuftragsnr));
    }

    public void testTvOnNotSentRequest() {
        String extAuftragsnr = "TEST008900";
        WitaCBVorgang witaCbVorgang = witaCbVorgang(extAuftragsnr);
        Auftrag auftrag = (new AuftragBuilder(GeschaeftsfallTyp.BEREITSTELLUNG)).withExterneAuftragsnummer(
                extAuftragsnr).buildValid();
        when(mwfEntityService.findUnsentRequest(witaCbVorgang.getId())).thenReturn(auftrag);

        testling.checkConditionsForTv(witaCbVorgang, auftrag, (LocalDate.now()).plusDays(7));
    }

    @Test(expectedExceptions = WitaUserException.class, expectedExceptionsMessageRegExp = WitaCheckConditionService.TV_TERMIN_IN_VERGANGENHEIT)
    public void testTvFailsDueTerminBeforeToday() throws Exception {
        testling.checkConditionsForTv(witaCbVorgang("TEST11123"), null, (LocalDate.now()).minusDays(1));
    }

    @DataProvider
    public Object[][] dataProviderTvWithMissingTam() {
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime nextWorkday = DateCalculationHelper.asWorkingDay(LocalDate.now().plusDays(1)).atStartOfDay();
        LocalDateTime nextMonday1 = DateCalculationHelper
                .asWorkingDayAndNextDayNotHoliday(LocalDate.now().plusDays(14).with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY)))
                .atStartOfDay();
        LocalDateTime nextFriday1 = DateCalculationHelper
                .asWorkingDayAndNextDayNotHoliday(nextMonday1.plusDays(4).toLocalDate()).atStartOfDay();

        // @formatter:off
        return new Object[][] {
                { today, nextFriday1, true,  null },
                { today, nextFriday1, false, String.format(TV_ONLY_36_HOURS_BEFORE) },
                { today, nextWorkday, true,  String.format(TV_MINDESTVORLAUFZEIT, "4") },
                { today, nextFriday1, true,  null },
                { today, nextFriday1, false, String.format(TV_ONLY_36_HOURS_BEFORE) },
                { today, nextWorkday, true,  String.format(TV_MINDESTVORLAUFZEIT, "4") },
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderTvWithMissingTam")
    public void testTvWithMissingTam(LocalDateTime realDate, LocalDateTime tvDate, boolean tamTooLate, String expectedExceptionMsg)
            throws Exception {

        String extAuftragsnr = "TEST12345";

        WitaCBVorgang cbVorgang = (new WitaCBVorgangBuilder()).withCarrierRefNr(extAuftragsnr)
                .withReturnRealDate(Date.from(realDate.atZone(ZoneId.systemDefault()).toInstant())).withVorgabeMnet(Date.from(tvDate.atZone(ZoneId.systemDefault()).toInstant())).build();
        Auftrag auftrag = (new AuftragBuilder(GeschaeftsfallTyp.BEREITSTELLUNG)).withExterneAuftragsnummer(
                extAuftragsnr).buildValid();

        when(mwfEntityService.checkMeldungReceived(extAuftragsnr, QualifizierteEingangsBestaetigung.class)).thenReturn(
                true);

        doReturn(tamTooLate).when(testling).isTamTooLate(Date.from(realDate.atZone(ZoneId.systemDefault()).toInstant()));

        testling.dateTimeCalculationService = new DateTimeCalculationService();
        testling.dateTimeCalculationService.referenceService = mock(ReferenceService.class);
        testling.dateTimeCalculationService.witaConfigService = mock(WitaConfigService.class);

        Reference reference = new Reference();
        reference.setIntValue(0);
        when(testling.dateTimeCalculationService.referenceService.findReferencesByType(
                Reference.REF_TYPE_WITA_VLT_PUFFERZEIT, Boolean.FALSE)).thenReturn(ImmutableList.of(reference));

        try {
            testling.checkConditionsForTv(cbVorgang, auftrag, tvDate.toLocalDate());
            assertNull(expectedExceptionMsg, "Fehler erwartet");
        }
        catch (Exception e) {
            assertEquals(e.getMessage(), expectedExceptionMsg);
        }
    }

    private void checkStornoWithQeb(WitaCBVorgang witaCbVorgang) throws Exception {
        when(
                mwfEntityService.checkMeldungReceived(witaCbVorgang.getCarrierRefNr(),
                        QualifizierteEingangsBestaetigung.class)
        ).thenReturn(true);
        checkStorno(witaCbVorgang);
    }

    private void checkStorno(WitaCBVorgang witaCbVorgang) {
        Auftrag auftrag = new AuftragBuilder(BEREITSTELLUNG).withExterneAuftragsnummer(witaCbVorgang.getCarrierRefNr())
                .buildValid();
        testling.checkConditionsForStorno(witaCbVorgang, auftrag);
    }

    private void checkTvWithQeb(WitaCBVorgang witaCbVorgang) {
        Auftrag auftrag = new AuftragBuilder(BEREITSTELLUNG).withExterneAuftragsnummer(witaCbVorgang.getCarrierRefNr())
                .buildValid();
        when(mwfEntityService.checkMeldungReceived(
                witaCbVorgang.getCarrierRefNr(), QualifizierteEingangsBestaetigung.class)).thenReturn(true);
        testling.checkConditionsForTv(witaCbVorgang, auftrag, DateConverterUtils.asLocalDate(getValidVorgabeMnet()));
        verify(witaWbciServiceFacade, never()).checkDateForMatchingWithVorabstimmung(any(Date.class), anyString());
    }

    private WitaCBVorgang witaCbVorgang(String carrierRefNr) {
        return witaCbVorgang(carrierRefNr, getValidRealDate(), getValidVorgabeMnet());
    }

    private WitaCBVorgang witaCbVorgang(String carrierRefNr, Date returnRealDate, Date vorgabeMnet) {
        return (new WitaCBVorgangBuilder()).withId(12300123L).withCarrierRefNr(carrierRefNr).withTyp(CBVorgang.TYP_NEU)
                .withStatus(CBVorgang.STATUS_TRANSFERRED).withReturnRealDate(returnRealDate)
                .withVorgabeMnet(vorgabeMnet).build();
    }

    private Date getValidRealDate() {
        return Date.from(LocalDate.now().plusDays(7).with(TemporalAdjusters.nextOrSame(DayOfWeek.THURSDAY)).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private Date getValidVorgabeMnet() {
        return Date.from(LocalDate.now().plusDays(14).with(TemporalAdjusters.nextOrSame(DayOfWeek.THURSDAY)).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    @Test
    public void testCheckConditionsForWbciPreAggrements() throws Exception {
        String vaId1 = "DEU.MNET.0001";
        GeschaeftsfallTyp gfTyp = KUENDIGUNG_KUNDE;
        Date realDate = new Date();
        WitaCBVorgang witaCBVorgang1 = mock(WitaCBVorgang.class);
        when(witaCBVorgang1.getVorabstimmungsId()).thenReturn(vaId1);
        when(witaCBVorgang1.getWitaGeschaeftsfallTyp()).thenReturn(gfTyp);
        WitaCBVorgang witaCBVorgang2 = mock(WitaCBVorgang.class);
        when(witaCBVorgang2.getVorabstimmungsId()).thenReturn(vaId1);
        when(witaCBVorgang2.getWitaGeschaeftsfallTyp()).thenReturn(gfTyp);

        when(witaDataService.loadRufnummern(any(WitaCBVorgang.class))).thenReturn(Arrays.asList(new Rufnummer(), new Rufnummer()));
        doReturn(realDate).when(testling).getRealTermin(any(WitaCBVorgang.class));

        testling.checkConditionsForWbciPreagreement(Arrays.asList(witaCBVorgang1, witaCBVorgang2));

        verify(witaWbciServiceFacade).checkVorabstimmungValidForWitaVorgang(gfTyp, vaId1);
        verify(witaWbciServiceFacade, times(2)).checkDateForMatchingWithVorabstimmung(realDate, vaId1);
        verify(witaWbciServiceFacade).checkRufnummernForMatchingWithVorabstimmung(anySetOf(Rufnummer.class), eq(vaId1));
    }

    @Test(expectedExceptions = WitaUserException.class, expectedExceptionsMessageRegExp = WBCI_MORE_THEN_ONE_VA_ID)
    public void testCheckConditionsForWbciPreAggrementsException() throws Exception {
        String vaId1 = "DEU.MNET.0001";
        String vaId2 = "DEU.MNET.0002";
        WitaCBVorgang witaCBVorgang1 = mock(WitaCBVorgang.class);
        when(witaCBVorgang1.getVorabstimmungsId()).thenReturn(vaId1);
        WitaCBVorgang witaCBVorgang2 = mock(WitaCBVorgang.class);
        when(witaCBVorgang2.getVorabstimmungsId()).thenReturn(vaId2);

        testling.checkConditionsForWbciPreagreement(Arrays.asList(witaCBVorgang1, witaCBVorgang2));
    }

    @Test
    public void testCheckConditionsWbciEmptyVA() throws Exception {
        GeschaeftsfallTyp gfTyp = KUENDIGUNG_KUNDE;
        WitaCBVorgang witaCBVorgang1 = mock(WitaCBVorgang.class);
        when(witaCBVorgang1.getVorabstimmungsId()).thenReturn(null);
        when(witaCBVorgang1.getWitaGeschaeftsfallTyp()).thenReturn(gfTyp);

        testling.checkConditionsForWbciPreagreement(Arrays.asList(witaCBVorgang1));

        verify(witaWbciServiceFacade, never()).checkVorabstimmungValidForWitaVorgang(any(GeschaeftsfallTyp.class), anyString());
        verify(witaWbciServiceFacade, never()).checkDateForMatchingWithVorabstimmung(any(Date.class), anyString());
        verify(witaWbciServiceFacade, never()).checkRufnummernForMatchingWithVorabstimmung(anySetOf(Rufnummer.class), anyString());
    }

    @Test
    public void testCheckConditionsWbciNoWitaCbVorgang() throws Exception {
        CBVorgang cbVorgang = mock(CBVorgang.class);

        testling.checkConditionsForWbciPreagreement(Arrays.asList(cbVorgang));

        verify(witaWbciServiceFacade, never()).checkVorabstimmungValidForWitaVorgang(any(GeschaeftsfallTyp.class), anyString());
        verify(witaWbciServiceFacade, never()).checkDateForMatchingWithVorabstimmung(any(Date.class), anyString());
        verify(witaWbciServiceFacade, never()).checkRufnummernForMatchingWithVorabstimmung(anySetOf(Rufnummer.class), anyString());
    }

    @Test
    public void testCheckConditionsForAbm() throws Exception {
        WitaCBVorgang cbVorgang = mock(WitaCBVorgang.class);
        AuftragsBestaetigungsMeldung abm = mock(AuftragsBestaetigungsMeldung.class);

        doNothing().when(testling).checkWbciConditionsForAbm(cbVorgang, abm);
        doNothing().when(testling).checkAbmLieferterminForProviderwechsel(cbVorgang, abm);
        testling.checkConditionsForAbm(cbVorgang, abm);
        verify(testling).checkWbciConditionsForAbm(cbVorgang, abm);
        verify(testling).checkAbmLieferterminForProviderwechsel(cbVorgang, abm);

        //check call to mark cbVorgang as klaerfall
        when(cbVorgang.getId()).thenReturn(1L);
        String excpMessage = "exception message";
        doThrow(new WbciValidationException(excpMessage)).when(testling).checkWbciConditionsForAbm(cbVorgang, abm);
        testling.checkConditionsForAbm(cbVorgang, abm);
        verify(witaTalOrderService).markWitaCBVorgangAsKlaerfall(1L, excpMessage);
    }

    @Test
    public void testCheckWbciConditionsForAbm() throws Exception {
        WitaCBVorgang cbVorgang = mock(WitaCBVorgang.class);
        AuftragsBestaetigungsMeldung abm = mock(AuftragsBestaetigungsMeldung.class);
        when(cbVorgang.getVorabstimmungsId()).thenReturn(null);

        //check no VA-ID is set
        testling.checkWbciConditionsForAbm(cbVorgang, abm);
        verify(witaWbciServiceFacade, never()).checkDateForMatchingWithVorabstimmung(any(Date.class), anyString());

        //check VA-IDs matches and call of Date matching service
        String vaId = "DEU.MNET.V001";
        when(cbVorgang.getVorabstimmungsId()).thenReturn(vaId);
        when(abm.getVorabstimmungsId()).thenReturn(vaId);
        LocalDate date = LocalDate.now();
        when(abm.getVerbindlicherLiefertermin()).thenReturn(date);
        testling.checkWbciConditionsForAbm(cbVorgang, abm);
        verify(witaWbciServiceFacade).checkDateForMatchingWithVorabstimmung(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()), vaId);
    }

    @DataProvider(name = "checkAbmLieferterminForProviderwechselDP")
    public Object[][] checkAbmLieferterminForProviderwechselDP() {
        LocalDate validDate = LocalDate.of(2014, 5, 19);
        LocalDate dayAfterIsHoliday = LocalDate.of(2014, 5, 28); // 29.05.2014 ist/war ein Feiertag!
        LocalDate friday = LocalDate.of(2014, 5, 29);

        // @formatter:off
        return new Object[][]{
                // NEU
                { validDate         , GeschaeftsfallTyp.BEREITSTELLUNG  , false   , false },
                { dayAfterIsHoliday , GeschaeftsfallTyp.BEREITSTELLUNG  , false   , false },
                { friday            , GeschaeftsfallTyp.BEREITSTELLUNG  , false   , false },
                // NEU mit #abw#
                { validDate         , GeschaeftsfallTyp.BEREITSTELLUNG  , true    , false },
                { dayAfterIsHoliday , GeschaeftsfallTyp.BEREITSTELLUNG  , true    , true },
                { friday            , GeschaeftsfallTyp.BEREITSTELLUNG  , true    , true },
                // VERBUNDLEISTUNG
                { validDate         , GeschaeftsfallTyp.VERBUNDLEISTUNG , false   , false },
                { dayAfterIsHoliday , GeschaeftsfallTyp.VERBUNDLEISTUNG , false   , true },
                { friday            , GeschaeftsfallTyp.VERBUNDLEISTUNG , false   , true },
                // PROVIDERWECHSEL
                { validDate         , GeschaeftsfallTyp.PROVIDERWECHSEL , false   , false },
                { dayAfterIsHoliday , GeschaeftsfallTyp.PROVIDERWECHSEL , false   , true },
                { friday            , GeschaeftsfallTyp.PROVIDERWECHSEL , false   , true },
        };
        // @formatter:on
    }

    @Test(dataProvider = "checkAbmLieferterminForProviderwechselDP")
    public void checkAbmLieferterminForProviderwechsel(LocalDate abmLiefertermin, GeschaeftsfallTyp gfTyp,
            boolean withAbw, boolean expectValidationException) {
        boolean exceptionOccured = false;

        try {
            WitaCBVorgang cbVorgang = new WitaCBVorgangBuilder()
                    .withWitaGeschaeftsfallTyp(gfTyp)
                    .withMontagehinweis((withAbw) ? WitaCBVorgang.ANBIETERWECHSEL_46TKG : null)
                    .setPersist(false).build();

            AuftragsBestaetigungsMeldung abm = mock(AuftragsBestaetigungsMeldung.class);
            when(abm.getVerbindlicherLiefertermin()).thenReturn(abmLiefertermin);

            testling.checkAbmLieferterminForProviderwechsel(cbVorgang, abm);
        }
        catch (WbciValidationException e) {
            e.printStackTrace();
            exceptionOccured = true;
        }

        if (expectValidationException != exceptionOccured) {
            fail("WbciValidationException not expected but occured");
        }
    }

}
