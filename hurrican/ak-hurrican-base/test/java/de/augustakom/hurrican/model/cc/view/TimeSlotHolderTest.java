package de.augustakom.hurrican.model.cc.view;

import java.time.*;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.billing.view.TimeSlotViewBuilder;
import de.augustakom.hurrican.model.cc.tal.TalRealisierungsZeitfenster;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class TimeSlotHolderTest {


    @DataProvider
    private Object[][] matchCandidateDP() {
        LocalDate rt = LocalDate.now();

        LocalDate today = LocalDate.now();
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        LocalTime to = LocalTime.of(8, 15);
        LocalTime from = LocalTime.of(9, 15);

        TimeSlotHolder.Candidate none = TimeSlotHolder.Candidate.None;
        TimeSlotHolder.Candidate tal = TimeSlotHolder.Candidate.TAL;
        TimeSlotHolder.Candidate billing = TimeSlotHolder.Candidate.BILLING;
        TalRealisierungsZeitfenster talVormittag = TalRealisierungsZeitfenster.VORMITTAG;

        // @formatter:off
        return new Object[][] {
                { rt, null, null, null, null, null, none },
                { rt, today, null, null, null, null, none },
                { rt, null, talVormittag, null, null, null, none },
                { rt, tomorrow, talVormittag, null, null, null, none },
                { rt, null, null, today, null, null, none },
                { rt, null, null, today, from, null, none },
                { rt, null, null, today, null, to, none },
                { rt, null, null, tomorrow, from, to, none },
                { rt, tomorrow, talVormittag, tomorrow, from, to, none },

                { rt, null, null, today, from, to, billing },
                { rt, today, talVormittag, null, null, null, tal },
                { rt, today, talVormittag, today, from, to, tal },
                { rt, today, talVormittag, tomorrow, from, to, tal },
                { rt, tomorrow, talVormittag, today, from, to, billing },
        };
        // @formatter:on
    }

    @Test(dataProvider = "matchCandidateDP")
    public void testMatchCandidate(LocalDate realisierungstermin, LocalDate talRealisierungsDay,
            TalRealisierungsZeitfenster talZeitfenster, LocalDate billingDay, LocalTime billingFrom,
            LocalTime billingTo, TimeSlotHolder.Candidate expectedCandidate) throws Exception {

        TimeSlotHolderBuilder timeSlotHolderBuilder = new TimeSlotHolderBuilder();

        if (talRealisierungsDay != null) {
            timeSlotHolderBuilder
                    .withTalRealisierungsDay(talRealisierungsDay)
                    .withTalRealisierungsZeitfenster(talZeitfenster);
        }
        if (billingDay != null) {
            TimeSlotViewBuilder timeSlotViewBuilder = new TimeSlotViewBuilder();
            timeSlotViewBuilder.withDate(billingDay);
            if (billingFrom != null) {
                timeSlotViewBuilder.withDaytimeFrom(billingFrom);
            }
            if (billingTo != null) {
                timeSlotViewBuilder.withDaytimeTo(billingTo);
            }
            timeSlotHolderBuilder.withTimeSlotViewBuilder(timeSlotViewBuilder);
        }

        TimeSlotHolder.Candidate candidate = timeSlotHolderBuilder.build().matchCandidate(realisierungstermin);

        Assert.assertEquals(expectedCandidate, candidate);
    }
}
