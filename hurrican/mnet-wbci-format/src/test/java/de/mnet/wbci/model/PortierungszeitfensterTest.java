package de.mnet.wbci.model;

import java.time.*;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class PortierungszeitfensterTest {

    @DataProvider
    public Object[][] dataProviderZfTimeFrom() {
        // @formatter:off
        return new Object[][] {
                { Portierungszeitfenster.ZF1,  6, 0 },
                { Portierungszeitfenster.ZF2,  6, 0 },
                { Portierungszeitfenster.ZF3,  0, 0 }
        };
        // @formatter:on
    }

    @DataProvider
    public Object[][] dataProviderZfTimeTo() {
        // @formatter:off
        return new Object[][] {
                { Portierungszeitfenster.ZF1,  8, 0 },
                { Portierungszeitfenster.ZF2,  12, 0 },
                { Portierungszeitfenster.ZF3,  0, 0 }
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderZfTimeFrom")
    public void testTimeFrom(Portierungszeitfenster zf, int hourOfDay, int minuteOfDay) throws Exception {
        LocalDateTime localTime = zf.timeFrom(LocalDate.now());

        Assert.assertEquals(localTime.toLocalDate(), LocalDate.now());
        Assert.assertEquals(localTime.getHour(), hourOfDay);
        Assert.assertEquals(localTime.getMinute(), minuteOfDay);
    }

    @Test(dataProvider = "dataProviderZfTimeTo")
    public void testTimeTo(Portierungszeitfenster zf, int hourOfDay, int minuteOfDay) throws Exception {
        LocalDateTime localTime = zf.timeTo(LocalDate.now());

        Assert.assertEquals(localTime.toLocalDate(), LocalDate.now());
        Assert.assertEquals(localTime.getHour(), hourOfDay);
        Assert.assertEquals(localTime.getMinute(), minuteOfDay);
    }
}