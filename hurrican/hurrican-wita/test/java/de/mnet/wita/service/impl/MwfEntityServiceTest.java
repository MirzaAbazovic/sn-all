/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.10.2011 13:58:09
 */
package de.mnet.wita.service.impl;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.mnet.common.servicetest.AbstractServiceTest;
import de.mnet.wita.dao.MwfEntityDao;
import de.mnet.wita.message.Auftrag;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.MnetWitaRequest;
import de.mnet.wita.message.TerminVerschiebung;
import de.mnet.wita.message.builder.AuftragBuilder;
import de.mnet.wita.message.builder.meldung.AuftragsBestaetigungsMeldungBuilder;
import de.mnet.wita.message.builder.meldung.QualifizierteEingangsBestaetigungBuilder;
import de.mnet.wita.message.builder.meldung.TerminAnforderungsMeldungBuilder;
import de.mnet.wita.message.common.BsiDelayProtokollEintragSent;
import de.mnet.wita.message.common.EmailStatus;
import de.mnet.wita.message.common.SmsStatus;
import de.mnet.wita.message.meldung.AuftragsBestaetigungsMeldung;
import de.mnet.wita.message.meldung.QualifizierteEingangsBestaetigung;
import de.mnet.wita.message.meldung.TerminAnforderungsMeldung;
import de.mnet.wita.service.MwfEntityService;

@Test(groups = BaseTest.SLOW)
public class MwfEntityServiceTest extends AbstractServiceTest {

    private static final int MAX_SEND_BLOCK_PER_GESCHAEFTSFALL = 12;
    private static final int COUNT_OF_IMPLEMENTED_GESCHAEFTSFALLE = 8;

    @Autowired
    private MwfEntityService underTest;

    @Autowired
    private MwfEntityDao mwfEntityDao;

    @DataProvider
    public Object[][] geschaeftsfaelleToFind() {
        // @formatter:off
        return new Object[][] {
                { GeschaeftsfallTyp.BEREITSTELLUNG },
                { GeschaeftsfallTyp.KUENDIGUNG_KUNDE },
                { GeschaeftsfallTyp.LEISTUNGSMERKMAL_AENDERUNG },
                { GeschaeftsfallTyp.LEISTUNGS_AENDERUNG },
                { GeschaeftsfallTyp.PORTWECHSEL },
                { GeschaeftsfallTyp.PROVIDERWECHSEL },
                { GeschaeftsfallTyp.VERBUNDLEISTUNG },
        };
        // @formatter:on
    }

    private Auftrag createAuftrag(GeschaeftsfallTyp geschaeftsfallTyp, int days) {
        LocalDate kundenwunschTermin = (days >= 0) ? LocalDate.now().plusDays(days) : LocalDate.now().minusDays(days
                * -1);
        Auftrag auftrag = new AuftragBuilder(geschaeftsfallTyp).buildAuftragWithKundenwunschTermnin(kundenwunschTermin);
        auftrag.setRequestWurdeStorniert(false);
        mwfEntityDao.store(auftrag);
        return auftrag;
    }

    @Test(dataProvider = "geschaeftsfaelleToFind")
    public void testFindUnsentRequestsForEveryGeschaeftsfall(GeschaeftsfallTyp geschaeftsfallTyp) throws FindException {
        Auftrag auftrag = createAuftrag(geschaeftsfallTyp, 1);

        List<Long> result = underTest
                .findUnsentRequestsForEveryGeschaeftsfall(MAX_SEND_BLOCK_PER_GESCHAEFTSFALL);
        assertNotEmpty(result);
        assertTrue(result.size() <= (MAX_SEND_BLOCK_PER_GESCHAEFTSFALL * COUNT_OF_IMPLEMENTED_GESCHAEFTSFALLE));
        if (!result.contains(auftrag.getId())) {
            // if the expected request is not in the result list, increase the block size and
            // check if the query works correctly
            result = underTest.findUnsentRequestsForEveryGeschaeftsfall(Integer.MAX_VALUE);
        }
        assertThat(result, hasItem(auftrag.getId()));
    }

    @Test(dataProvider = "geschaeftsfaelleToFind")
    public void testFindUnsentRequestsForEveryGeschaeftsfallWithExceedingAmountOfAuftraege(
            GeschaeftsfallTyp geschaeftsfallTyp)
            throws FindException {

        for (int i = MAX_SEND_BLOCK_PER_GESCHAEFTSFALL * -1; i < 0; i++) {
            createAuftrag(geschaeftsfallTyp, i);
        }
        Auftrag auftrag = createAuftrag(geschaeftsfallTyp, 1);

        List<Long> result = underTest.findUnsentRequestsForEveryGeschaeftsfall(MAX_SEND_BLOCK_PER_GESCHAEFTSFALL);

        assertNotEmpty(result);
        assertTrue(result.size() <= (MAX_SEND_BLOCK_PER_GESCHAEFTSFALL * COUNT_OF_IMPLEMENTED_GESCHAEFTSFALLE));
        assertThat(result, not(hasItem(auftrag.getId())));
    }

    public void testFindUnsentRequestsOrder() throws FindException {
        Auftrag auftrag = new AuftragBuilder(GeschaeftsfallTyp.BEREITSTELLUNG)
                .buildAuftragWithKundenwunschTermnin(LocalDate.now().minusDays(2).minusYears(2));
        Auftrag auftrag2 = new AuftragBuilder(GeschaeftsfallTyp.BEREITSTELLUNG)
                .buildAuftragWithKundenwunschTermnin(LocalDate.now().minusDays(2).minusYears(2));
        auftrag.setRequestWurdeStorniert(false);
        auftrag2.setRequestWurdeStorniert(false);
        mwfEntityDao.store(auftrag);
        mwfEntityDao.store(auftrag2);

        List<Long> result = underTest.findUnsentRequestsForEveryGeschaeftsfall(MAX_SEND_BLOCK_PER_GESCHAEFTSFALL);
        assertTrue(result.size() >= 2);

        // reihenfolge checken
        int index = 0;
        int auftragindex = 0;
        int auftrag2index = 0;

        for (Long requestId : result) {
            index++;
            if (NumberTools.equal(requestId, auftrag.getId())) {
                auftragindex = index;
            }
            if (NumberTools.equal(requestId, auftrag2.getId())) {
                auftrag2index = index;
            }
        }
        assertTrue((auftragindex < auftrag2index));
    }

    public void testFindMwfEntitiesByProperty() {
        LocalDateTime now = LocalDate.now().atStartOfDay();
        LocalDateTime yesterday = now.minusDays(1);
        LocalDateTime tomorrow = now.plusDays(1);

        Auftrag auftrag1 = new AuftragBuilder(GeschaeftsfallTyp.BEREITSTELLUNG).withSentAt(now).buildValid();
        mwfEntityDao.store(auftrag1);

        Auftrag auftrag2 = new AuftragBuilder(GeschaeftsfallTyp.BEREITSTELLUNG).withSentAt(now).buildValid();
        mwfEntityDao.store(auftrag2);

        Auftrag auftrag3 = new AuftragBuilder(GeschaeftsfallTyp.BEREITSTELLUNG).withSentAt(tomorrow).buildValid();
        mwfEntityDao.store(auftrag3);

        Auftrag auftrag4 = new AuftragBuilder(GeschaeftsfallTyp.BEREITSTELLUNG).withSentAt(yesterday).buildValid();
        mwfEntityDao.store(auftrag4);

        flushAndClear();

        List<Auftrag> result = underTest.findMwfEntitiesByProperty(Auftrag.class, "sentAt", Date.from(now.atZone(ZoneId.systemDefault()).toInstant()));

        assertThat(result, hasSize(2));
        assertThat(result, containsInAnyOrder(auftrag1, auftrag2));
    }

    public void testIsLastMeldungTam() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tenSec = now.plusSeconds(10);
        LocalDateTime fiveMin = now.plusMinutes(5);
        LocalDateTime nineDays = now.plusDays(9);
        LocalDateTime nineteenDays = now.plusDays(19);

        String extOrderNo = "1231231230";

        Auftrag neu = (new AuftragBuilder(GeschaeftsfallTyp.BEREITSTELLUNG)).withExterneAuftragsnummer(extOrderNo)
                .withSentAt(now).buildValid();
        mwfEntityDao.store(neu);
        QualifizierteEingangsBestaetigung qeb = (new QualifizierteEingangsBestaetigungBuilder())
                .withExterneAuftragsnummer(extOrderNo).withVersandZeitstempel(tenSec).build();
        mwfEntityDao.store(qeb);
        AuftragsBestaetigungsMeldung abm = (new AuftragsBestaetigungsMeldungBuilder())
                .withExterneAuftragsnummer(extOrderNo).withVersandZeitstempel(fiveMin).build();
        mwfEntityDao.store(abm);

        TerminAnforderungsMeldung tam1 = (new TerminAnforderungsMeldungBuilder()).withExterneAuftragsnummer(extOrderNo)
                .withVersandZeitstempel(nineDays).build();
        mwfEntityDao.store(tam1);
        flushAndClear();
        assertFalse(underTest.isLastMeldungTam(tam1));

        TerminAnforderungsMeldung tam2 = (new TerminAnforderungsMeldungBuilder()).withExterneAuftragsnummer(extOrderNo)
                .withVersandZeitstempel(nineteenDays).build();
        mwfEntityDao.store(tam2);
        flushAndClear();
        assertTrue(underTest.isLastMeldungTam(tam2));
    }

    public void testIsLastMeldungTamWithTamTvTam() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nineDays = now.plusDays(9);
        LocalDateTime tenDays = now.plusDays(10);
        LocalDateTime nineteenDays = now.plusDays(19);

        String extOrderNo = "1231231230";

        Auftrag neu = (new AuftragBuilder(GeschaeftsfallTyp.BEREITSTELLUNG)).withExterneAuftragsnummer(extOrderNo)
                .withSentAt(now).buildValid();
        mwfEntityDao.store(neu);

        TerminAnforderungsMeldung tam1 = (new TerminAnforderungsMeldungBuilder()).withExterneAuftragsnummer(extOrderNo)
                .withVersandZeitstempel(nineDays).build();
        mwfEntityDao.store(tam1);

        TerminVerschiebung tv = new TerminVerschiebung(neu, (LocalDate.now()).plusDays(20));
        tv.setSentAt(Date.from(tenDays.atZone(ZoneId.systemDefault()).toInstant()));
        mwfEntityDao.store(tv);

        TerminAnforderungsMeldung tam2 = (new TerminAnforderungsMeldungBuilder()).withExterneAuftragsnummer(extOrderNo)
                .withVersandZeitstempel(nineteenDays).build();
        mwfEntityDao.store(tam2);
        flushAndClear();
        assertFalse(underTest.isLastMeldungTam(tam2));
    }


    public void testFindDelayedRequestsToBeSentToBsi() {
        // WITA-Auftrag - bereits an WITA uebermittelt; also nicht vorgehalten - nicht in Result erwartet
        Auftrag auftragAlreadySentToWita = new AuftragBuilder(GeschaeftsfallTyp.BEREITSTELLUNG)
                .buildAuftragWithKundenwunschTermnin(LocalDate.now().plusDays(10));
        auftragAlreadySentToWita.setSentAt(new Date());
        mwfEntityDao.store(auftragAlreadySentToWita);

        // WITA-Auftrag - noch innerhalb der Verzoegerung von x Minuten; also nicht vorgehalten - nicht in Result erwartet
        Auftrag auftragInMinutesOnHold = new AuftragBuilder(GeschaeftsfallTyp.BEREITSTELLUNG)
                .buildAuftragWithKundenwunschTermnin(LocalDate.now().plusDays(10));
        auftragInMinutesOnHold.setMwfCreationDate(Date.from(LocalDateTime.now().plusMinutes(5).atZone(ZoneId.systemDefault()).toInstant())); // plus 5 Minuten, da in Tests "minutes.requests.on.hold" auf 0 gestellt ist
        mwfEntityDao.store(auftragInMinutesOnHold);

        // WITA-Auftrag mit relativ nahem KWT; nicht vorgehalten - nicht in Result erwartet
        Auftrag auftragNotDelayed = new AuftragBuilder(GeschaeftsfallTyp.BEREITSTELLUNG)
                .buildAuftragWithKundenwunschTermnin(LocalDate.now().plusDays(10));
        mwfEntityDao.store(auftragNotDelayed);

        // vorgehaltener WITA-Auftrag; Delay bereits als BSI-Protokolleintrag geschrieben - nicht in Result erwartet
        Auftrag auftragDelayAlreadySent = new AuftragBuilder(GeschaeftsfallTyp.BEREITSTELLUNG)
                .buildAuftragWithKundenwunschTermnin(LocalDate.now().plusDays(10));
        auftragDelayAlreadySent.setDelaySentToBsi(BsiDelayProtokollEintragSent.DELAY_SENT_TO_BSI);
        mwfEntityDao.store(auftragDelayAlreadySent);

        // vorgehaltener WITA-Auftrag - wird im Result erwartet!
        Auftrag auftragDelayed = new AuftragBuilder(GeschaeftsfallTyp.BEREITSTELLUNG)
                .buildAuftragWithKundenwunschTermnin(LocalDate.now().plusYears(2));
        mwfEntityDao.store(auftragDelayed);
        flushAndClear();

        List<MnetWitaRequest> result = underTest.findDelayedRequestsToBeSentToBsi(5);
        assertNotEmpty(result);
        boolean auftragDelayedFound = false;
        for (MnetWitaRequest request : result) {
            if (NumberTools.equal(request.getId(), auftragDelayed.getId())) {
                auftragDelayedFound = true;
            }

            if (NumberTools.equal(request.getId(), auftragNotDelayed.getId())) {
                fail("Auftrag fuer BSI-Delay Protokoll angesehen, der nicht vorgehalten sein sollte");
            }

            if (NumberTools.equal(request.getId(), auftragInMinutesOnHold.getId())) {
                fail("Auftrag fuer BSI-Delay Protokoll angesehen, bei dem lediglich die minutes.on.hold noch nicht abgelaufen ist");
            }

            if (NumberTools.equal(request.getId(), auftragDelayAlreadySent.getId())) {
                fail("Auftrag fuer BSI-Delay Protokoll angesehen, obwohl Delay-Eintrag schon an BSI gesendet wurde");
            }

            if (NumberTools.equal(request.getId(), auftragAlreadySentToWita.getId())) {
                fail("Auftrag fuer BSI-Delay Protokoll angesehen, der schon an die WITA uebermittelt wurde");
            }
        }
        assertTrue(auftragDelayedFound, "vorgehaltener Auftrag wurde nicht ermittelt!");
    }


    public void testCheckMeldungReceived() {
        String extOrderNo = "1231231231";

        Auftrag neu = (new AuftragBuilder(GeschaeftsfallTyp.BEREITSTELLUNG)).withExterneAuftragsnummer(extOrderNo)
                .withSentAt(LocalDateTime.now()).buildValid();
        mwfEntityDao.store(neu);
        QualifizierteEingangsBestaetigung qeb = (new QualifizierteEingangsBestaetigungBuilder())
                .withExterneAuftragsnummer(extOrderNo)
                .withVersandZeitstempel(LocalDateTime.now().plusMinutes(5))
                .withSmsStatus(SmsStatus.VERALTET)
                .withEmailStatus(EmailStatus.VERALTET)
                .build();
        mwfEntityDao.store(qeb);

        assertTrue(underTest.checkMeldungReceived(extOrderNo, QualifizierteEingangsBestaetigung.class));
    }


    public void testCreateMeldungWithoutSmsStatus() {
        QualifizierteEingangsBestaetigung qeb = (new QualifizierteEingangsBestaetigungBuilder())
                .withExterneAuftragsnummer("123456")
                .withSmsStatus(null)
                .withEmailStatus(EmailStatus.OFFEN)
                .build();
        assertNull(qeb.getSmsStatus());
        mwfEntityDao.store(qeb);
    }

    public void testCreateMeldungWithoutEmailStatus() {
        QualifizierteEingangsBestaetigung qeb = (new QualifizierteEingangsBestaetigungBuilder())
                .withExterneAuftragsnummer("1234567")
                .withSmsStatus(SmsStatus.OFFEN)
                .withEmailStatus(null)
                .build();
        assertNull(qeb.getEmailStatus());
        mwfEntityDao.store(qeb);
    }
}
