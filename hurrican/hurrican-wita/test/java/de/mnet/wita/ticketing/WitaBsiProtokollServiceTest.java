/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.02.2012 16:24:31
 */
package de.mnet.wita.ticketing;

import static de.mnet.wita.message.common.BsiProtokollEintragSent.*;
import static de.mnet.wita.model.UserTask.UserTaskStatus.*;
import static org.testng.Assert.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.common.servicetest.AbstractServiceTest;
import de.mnet.wita.dao.MwfEntityDao;
import de.mnet.wita.message.builder.meldung.AbbruchMeldungBuilder;
import de.mnet.wita.message.builder.meldung.AbbruchMeldungPvBuilder;
import de.mnet.wita.message.builder.meldung.AnkuendigungsMeldungPvBuilder;
import de.mnet.wita.message.builder.meldung.AuftragsBestaetigungsMeldungBuilder;
import de.mnet.wita.message.builder.meldung.AuftragsBestaetigungsMeldungPvBuilder;
import de.mnet.wita.message.builder.meldung.EntgeltMeldungBuilder;
import de.mnet.wita.message.builder.meldung.EntgeltMeldungPvBuilder;
import de.mnet.wita.message.builder.meldung.ErledigtMeldungBuilder;
import de.mnet.wita.message.builder.meldung.ErledigtMeldungKundeBuilder;
import de.mnet.wita.message.builder.meldung.ErledigtMeldungPvBuilder;
import de.mnet.wita.message.builder.meldung.QualifizierteEingangsBestaetigungBuilder;
import de.mnet.wita.message.builder.meldung.RueckMeldungPvBuilder;
import de.mnet.wita.message.builder.meldung.TerminAnforderungsMeldungBuilder;
import de.mnet.wita.message.common.BsiProtokollEintragSent;
import de.mnet.wita.message.meldung.AbbruchMeldungPv;
import de.mnet.wita.message.meldung.AnkuendigungsMeldungPv;
import de.mnet.wita.message.meldung.AuftragsBestaetigungsMeldungPv;
import de.mnet.wita.message.meldung.EntgeltMeldungPv;
import de.mnet.wita.message.meldung.IncomingPvMeldung;
import de.mnet.wita.message.meldung.Meldung;
import de.mnet.wita.model.AkmPvUserTask;
import de.mnet.wita.model.UserTask.UserTaskStatus;
import de.mnet.wita.service.WitaUsertaskService;

@Test(groups = BaseTest.SERVICE)
public class WitaBsiProtokollServiceTest extends AbstractServiceTest {

    @Autowired
    private WitaBsiProtokollService witaBsiProtokollService;

    @Autowired
    private MwfEntityDao mwfEntityDao;

    @Autowired
    private WitaUsertaskService witaUsertaskService;

    @DataProvider
    public Object[][] meldungProvider() {
        // @formatter:off
        // Error-Status voellig ausreichend, da nur getestet werden soll, ob der Converter auch angezogen wird
        return new Object[][] {
                { new AbbruchMeldungBuilder().withSentToBsi(ERROR_SEND_TO_BSI).build(),                      ERROR_SEND_TO_BSI },
                { new AbbruchMeldungPvBuilder().withSentToBsi(ERROR_SEND_TO_BSI).build(),                    ERROR_SEND_TO_BSI },
                { new AnkuendigungsMeldungPvBuilder().withSentToBsi(ERROR_SEND_TO_BSI).build(),              ERROR_SEND_TO_BSI },
                { new AuftragsBestaetigungsMeldungBuilder().withSentToBsi(ERROR_SEND_TO_BSI).build(),        ERROR_SEND_TO_BSI },
                { new AuftragsBestaetigungsMeldungPvBuilder().withSentToBsi(ERROR_SEND_TO_BSI).build(),      ERROR_SEND_TO_BSI },
                { new ErledigtMeldungBuilder().withSentToBsi(ERROR_SEND_TO_BSI).build(),                     ERROR_SEND_TO_BSI },
                { new ErledigtMeldungPvBuilder().withSentToBsi(ERROR_SEND_TO_BSI).build(),                   ERROR_SEND_TO_BSI },
                { new ErledigtMeldungKundeBuilder().withSentToBsi(ERROR_SEND_TO_BSI).build(),                ERROR_SEND_TO_BSI },
                { new RueckMeldungPvBuilder().withSentToBsi(ERROR_SEND_TO_BSI).build(),                      ERROR_SEND_TO_BSI },
                { new TerminAnforderungsMeldungBuilder().withSentToBsi(ERROR_SEND_TO_BSI).build(),           ERROR_SEND_TO_BSI },

                { new QualifizierteEingangsBestaetigungBuilder().withSentToBsi(ERROR_SEND_TO_BSI).build(),   DONT_SEND_TO_BSI },
                { new EntgeltMeldungBuilder().withSentToBsi(ERROR_SEND_TO_BSI).build(),                      DONT_SEND_TO_BSI },
                { new EntgeltMeldungPvBuilder().withSentToBsi(ERROR_SEND_TO_BSI).build(),                    DONT_SEND_TO_BSI },
        };
        // @formatter:on
    }

    @Test(dataProvider = "meldungProvider")
    public <T extends Meldung<?>> void testAllConvertersAreInitialized(T meldung, BsiProtokollEintragSent sentStatus)
            throws Exception {
        witaBsiProtokollService.protokolliereNachricht(meldung);
        assertEquals(meldung.getSentToBsi(), sentStatus);
    }

    public void testDontSentPvMeldungenIfAuftragIsNotSet() {
        String extAuftragsnr = "234123";
        AnkuendigungsMeldungPv akmPv = buildAkmPvAndAkmPvUserTask(extAuftragsnr, ERROR_SEND_TO_BSI, GESCHLOSSEN);
        AbbruchMeldungPv abbmPv = buildAbbmPv(extAuftragsnr, ERROR_SEND_TO_BSI);
        flushAndClear();

        witaBsiProtokollService.dontSentPvMeldungenIfAuftragIsNotSet();
        assertSentToBsiFlag(akmPv, DONT_SEND_TO_BSI);
        assertSentToBsiFlag(abbmPv, DONT_SEND_TO_BSI);
    }

    public void testDontSentPvMeldungenIfAuftragIsNotSetIfFlagIsNotError() {
        String extAuftragsnr = "234123";
        AnkuendigungsMeldungPv akmPv = buildAkmPvAndAkmPvUserTask(extAuftragsnr, SENT_TO_BSI, GESCHLOSSEN);
        AbbruchMeldungPv abbmPv = buildAbbmPv(extAuftragsnr, NOT_SENT_TO_BSI);
        flushAndClear();

        witaBsiProtokollService.dontSentPvMeldungenIfAuftragIsNotSet();
        assertSentToBsiFlag(akmPv, SENT_TO_BSI);
        assertSentToBsiFlag(abbmPv, NOT_SENT_TO_BSI);
    }

    public void testDontSentPvMeldungenIfAuftragIsNotSetIfLastMeldungMissing() {
        AnkuendigungsMeldungPv akmPv = buildAkmPvAndAkmPvUserTask("234123", ERROR_SEND_TO_BSI, GESCHLOSSEN);
        flushAndClear();

        witaBsiProtokollService.dontSentPvMeldungenIfAuftragIsNotSet();
        assertSentToBsiFlag(akmPv, ERROR_SEND_TO_BSI);
    }

    public void testDontSentPvMeldungenIfAuftragIsNotSetIfUserTaskIsOpen() {
        AnkuendigungsMeldungPv akmPv = buildAkmPvAndAkmPvUserTask("234123", ERROR_SEND_TO_BSI, OFFEN);
        flushAndClear();

        witaBsiProtokollService.dontSentPvMeldungenIfAuftragIsNotSet();
        assertSentToBsiFlag(akmPv, ERROR_SEND_TO_BSI);
    }

    public void testDontSentPvMeldungenIfAuftragIsNotSetIfLastMeldungHasNoError() {
        String extAuftragsnr1 = "234123";

        AnkuendigungsMeldungPv akmPv = buildAkmPvAndAkmPvUserTask(extAuftragsnr1, ERROR_SEND_TO_BSI, GESCHLOSSEN);
        EntgeltMeldungPv entmPv = buildEntmPv(extAuftragsnr1, DONT_SEND_TO_BSI);
        flushAndClear();

        witaBsiProtokollService.dontSentPvMeldungenIfAuftragIsNotSet();

        assertSentToBsiFlag(akmPv, DONT_SEND_TO_BSI);
        assertSentToBsiFlag(entmPv, DONT_SEND_TO_BSI);
    }

    public void testDontSentPvMeldungenIfAuftragIsNotSetIfOrderIsIncorrect() {
        String extAuftragsnr1 = "234123";

        AbbruchMeldungPv abbmPv = buildAbbmPv(extAuftragsnr1, ERROR_SEND_TO_BSI);
        AnkuendigungsMeldungPv akmPv = buildAkmPvAndAkmPvUserTask(extAuftragsnr1, ERROR_SEND_TO_BSI, GESCHLOSSEN);
        flushAndClear();

        witaBsiProtokollService.dontSentPvMeldungenIfAuftragIsNotSet();

        assertSentToBsiFlag(akmPv, DONT_SEND_TO_BSI);
        assertSentToBsiFlag(abbmPv, DONT_SEND_TO_BSI);
    }

    public void testDontSentPvMeldungenIfAuftragIsNotSetIfFirstMeldungIsNotAkmPv() {
        String extAuftragsnr = "234123";

        AuftragsBestaetigungsMeldungPv abmPv = buildAbmPv(extAuftragsnr, ERROR_SEND_TO_BSI);
        buildAkmPvUserTask(abmPv, GESCHLOSSEN);
        AbbruchMeldungPv abbmPv = buildAbbmPv(extAuftragsnr, ERROR_SEND_TO_BSI);
        flushAndClear();

        witaBsiProtokollService.dontSentPvMeldungenIfAuftragIsNotSet();

        assertSentToBsiFlag(abmPv, DONT_SEND_TO_BSI);
        assertSentToBsiFlag(abbmPv, DONT_SEND_TO_BSI);
    }

    public void testDontSentPvMeldungenIfAuftragIsNotSetMultiple() {
        String extAuftragsnr1 = "234123";
        String extAuftragsnr2 = "07812";
        String extAuftragsnr3 = "73409835";
        String extAuftragsnr4 = "347890";
        String extAuftragsnr5 = "980325";

        AnkuendigungsMeldungPv akmPv1 = buildAkmPvAndAkmPvUserTask(extAuftragsnr1, ERROR_SEND_TO_BSI, GESCHLOSSEN);
        AnkuendigungsMeldungPv akmPv2 = buildAkmPvAndAkmPvUserTask(extAuftragsnr2, ERROR_SEND_TO_BSI, GESCHLOSSEN);
        AnkuendigungsMeldungPv akmPv3 = buildAkmPvAndAkmPvUserTask(extAuftragsnr3, ERROR_SEND_TO_BSI, GESCHLOSSEN);
        AnkuendigungsMeldungPv akmPv4 = buildAkmPvAndAkmPvUserTask(extAuftragsnr4, ERROR_SEND_TO_BSI, GESCHLOSSEN);

        AuftragsBestaetigungsMeldungPv abmPv1 = buildAbmPv(extAuftragsnr1, ERROR_SEND_TO_BSI);
        AuftragsBestaetigungsMeldungPv abmPv2 = buildAbmPv(extAuftragsnr2, ERROR_SEND_TO_BSI);
        AuftragsBestaetigungsMeldungPv abmPv5 = buildAbmPv(extAuftragsnr5, ERROR_SEND_TO_BSI);

        AbbruchMeldungPv abbmPv3 = buildAbbmPv(extAuftragsnr3, ERROR_SEND_TO_BSI);

        EntgeltMeldungPv entmPv1 = buildEntmPv(extAuftragsnr1, ERROR_SEND_TO_BSI);
        EntgeltMeldungPv entmPv4 = buildEntmPv(extAuftragsnr4, ERROR_SEND_TO_BSI);
        flushAndClear();

        witaBsiProtokollService.dontSentPvMeldungenIfAuftragIsNotSet();

        assertSentToBsiFlag(akmPv1, DONT_SEND_TO_BSI);
        assertSentToBsiFlag(abmPv1, DONT_SEND_TO_BSI);
        assertSentToBsiFlag(entmPv1, DONT_SEND_TO_BSI);

        assertSentToBsiFlag(akmPv2, ERROR_SEND_TO_BSI);
        assertSentToBsiFlag(abmPv2, ERROR_SEND_TO_BSI);

        assertSentToBsiFlag(akmPv3, DONT_SEND_TO_BSI);
        assertSentToBsiFlag(abbmPv3, DONT_SEND_TO_BSI);

        assertSentToBsiFlag(akmPv4, DONT_SEND_TO_BSI);
        assertSentToBsiFlag(entmPv4, DONT_SEND_TO_BSI);

        assertSentToBsiFlag(abmPv5, ERROR_SEND_TO_BSI);
    }

    private AnkuendigungsMeldungPv buildAkmPvAndAkmPvUserTask(String extAuftragsnr,
            BsiProtokollEintragSent sentToBsiFlag, UserTaskStatus status) {
        AnkuendigungsMeldungPv akmPv = (new AnkuendigungsMeldungPvBuilder()).withExterneAuftragsnummer(extAuftragsnr)
                .withSentToBsi(sentToBsiFlag).build();
        mwfEntityDao.store(akmPv);

        buildAkmPvUserTask(akmPv, status);
        return akmPv;
    }

    private AuftragsBestaetigungsMeldungPv buildAbmPv(String extAuftragsnr, BsiProtokollEintragSent sentToBsiFlag) {
        AuftragsBestaetigungsMeldungPv abmPv = new AuftragsBestaetigungsMeldungPvBuilder().withExterneAuftragsnummer(
                extAuftragsnr).withSentToBsi(sentToBsiFlag).build();
        mwfEntityDao.store(abmPv);
        return abmPv;
    }

    private AbbruchMeldungPv buildAbbmPv(String extAuftragsnr, BsiProtokollEintragSent sentToBsiFlag) {
        AbbruchMeldungPv abbmPv = new AbbruchMeldungPvBuilder().withExterneAuftragsnummer(extAuftragsnr).withSentToBsi(sentToBsiFlag).build();
        mwfEntityDao.store(abbmPv);
        return abbmPv;
    }

    private EntgeltMeldungPv buildEntmPv(String extAuftragsnr, BsiProtokollEintragSent sentToBsiFlag) {
        EntgeltMeldungPv entmPv = new EntgeltMeldungPvBuilder().withExterneAuftragsnummer(extAuftragsnr).withSentToBsi(sentToBsiFlag).build();
        mwfEntityDao.store(entmPv);
        return entmPv;
    }

    private <T extends Meldung<?> & IncomingPvMeldung> void buildAkmPvUserTask(T meldung, UserTaskStatus status) {
        AkmPvUserTask usertask = witaUsertaskService.createAkmPvUserTask(meldung);
        usertask.setStatus(status);
        witaUsertaskService.storeUserTask(usertask);
    }

    private <T extends Meldung<?>> void assertSentToBsiFlag(T meldung, BsiProtokollEintragSent expectedSentToBsi) {
        Meldung<?> toTest = mwfEntityDao.findById(meldung.getId(), meldung.getClass());
        assertEquals(toTest.getSentToBsi(), expectedSentToBsi);
    }

}
