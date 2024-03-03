/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.08.2011 15:51:39
 */
package de.mnet.wita.servicetest.service;

import static de.mnet.wita.service.WitaCheckConditionService.*;
import static org.testng.Assert.*;

import java.time.*;
import java.time.temporal.*;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.mnet.common.servicetest.AbstractServiceTest;
import de.mnet.wita.dao.MwfEntityDao;
import de.mnet.wita.message.Auftrag;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.builder.AuftragBuilder;
import de.mnet.wita.message.builder.meldung.QualifizierteEingangsBestaetigungBuilder;
import de.mnet.wita.message.builder.meldung.TerminAnforderungsMeldungBuilder;
import de.mnet.wita.message.meldung.QualifizierteEingangsBestaetigung;
import de.mnet.wita.message.meldung.TerminAnforderungsMeldung;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.model.WitaCBVorgangBuilder;
import de.mnet.wita.service.WitaCheckConditionService;
import de.mnet.wita.service.impl.DateTimeCalculationService;

@Test(groups = BaseTest.SERVICE)
public class WitaCheckConditionsServiceTest extends AbstractServiceTest {

    @Autowired
    private WitaCheckConditionService underTest;

    @Autowired
    private MwfEntityDao mwfEntityDao;

    @DataProvider
    public Object[][] dataProviderCheckConditionsForStorno() {
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        LocalDateTime nextMonday1 = LocalDateTime.now().plusDays(14).with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY));
        // @formatter:off
        return new Object[][] {
                // ohne TAM
                { nextMonday1, false, null },
                { nextMonday1, false, null },
                { yesterday,   false, STORNO_TOO_LATE },
                { yesterday,   false, STORNO_TOO_LATE },

                // mit TAM
                { nextMonday1, true, null },
                { yesterday,   true,  null },
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderCheckConditionsForStorno")
    public void testCheckConditionsForStorno(LocalDateTime realDate, boolean hasTam, String expectedExceptionMessage) {
        String extAuftragsnr = "TEST456123";

        WitaCBVorgang cbVorgang = (new WitaCBVorgangBuilder()).withCarrierRefNr(extAuftragsnr)
                .withReturnRealDate(Date.from(realDate.atZone(ZoneId.systemDefault()).toInstant())).build();
        Auftrag auftrag = (new AuftragBuilder(GeschaeftsfallTyp.BEREITSTELLUNG)).withExterneAuftragsnummer(
                extAuftragsnr).buildValid();

        QualifizierteEingangsBestaetigung qeb = (new QualifizierteEingangsBestaetigungBuilder())
                .withExterneAuftragsnummer(extAuftragsnr).build();
        mwfEntityDao.store(qeb);
        if (hasTam) {
            TerminAnforderungsMeldung tam = (new TerminAnforderungsMeldungBuilder()).withExterneAuftragsnummer(
                    extAuftragsnr).build();
            mwfEntityDao.store(tam);
        }

        try {
            underTest.checkConditionsForStorno(cbVorgang, auftrag);
            assertNull(expectedExceptionMessage, "Fehler erwartet");
        }
        catch (Exception e) {
            assertEquals(e.getMessage(), expectedExceptionMessage);
        }
    }

    @DataProvider
    public Object[][] dataProviderCheckConditionsForTv() {
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime tomorrow = today.plusDays(1);
        LocalDateTime nextWorkday = (new DateTimeCalculationService()).asWorkingDay(LocalDateTime.now().plusDays(1));
        LocalDateTime nextMonday1 = LocalDateTime.now().plusDays(14).with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        // if Monday is a holiday then gets the next working day after monday
        LocalDateTime nextMonday3 = DateCalculationHelper
                .asWorkingDayAndNextDayNotHoliday(nextMonday1.plusDays(14).toLocalDate()).atStartOfDay();
        LocalDateTime nextFriday1 = DateCalculationHelper
                .asWorkingDayAndNextDayNotHoliday(nextMonday1.plusDays(4).toLocalDate()).atStartOfDay();

        // @formatter:off
        //      [realDate, tvDate, hasTam, expectedExceptionMsg]
        return new Object[][] {
                // ohne TAM
                { nextMonday1, nextMonday3, false, null },
                { nextMonday1, nextWorkday, false, String.format(TV_MINDESTVORLAUFZEIT, "7") },
                { tomorrow, nextFriday1, false, String.format(TV_ONLY_36_HOURS_BEFORE) },

                // mit TAM
                { today, nextFriday1, true, null },
                { today, nextWorkday, true, String.format(TV_MINDESTVORLAUFZEIT, "4") },
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderCheckConditionsForTv")
    public void testCheckConditionsForTv(LocalDateTime realDate, LocalDateTime tvDate, boolean hasTam, String expectedExceptionMsg) {

        String extAuftragsnr = "TEST127234";

        WitaCBVorgang cbVorgang = (new WitaCBVorgangBuilder()).withCarrierRefNr(extAuftragsnr)
                .withReturnRealDate(Date.from(realDate.atZone(ZoneId.systemDefault()).toInstant())).withVorgabeMnet(Date.from(tvDate.atZone(ZoneId.systemDefault()).toInstant())).build();
        Auftrag auftrag = (new AuftragBuilder(GeschaeftsfallTyp.BEREITSTELLUNG)).withExterneAuftragsnummer(
                extAuftragsnr).buildValid();

        QualifizierteEingangsBestaetigung qeb = (new QualifizierteEingangsBestaetigungBuilder())
                .withExterneAuftragsnummer(extAuftragsnr).build();
        mwfEntityDao.store(qeb);
        if (hasTam) {
            TerminAnforderungsMeldung tam = (new TerminAnforderungsMeldungBuilder()).withExterneAuftragsnummer(
                    extAuftragsnr).build();
            mwfEntityDao.store(tam);
        }

        try {
            underTest.checkConditionsForTv(cbVorgang, auftrag, tvDate.toLocalDate());
            assertNull(expectedExceptionMsg, "Fehler erwartet");
        }
        catch (Exception e) {
            assertEquals(e.getMessage(), expectedExceptionMsg);
        }
    }
}
