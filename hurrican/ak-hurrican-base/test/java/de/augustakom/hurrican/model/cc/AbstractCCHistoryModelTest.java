package de.augustakom.hurrican.model.cc;

import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.common.tools.DateConverterUtils;

@Test(groups = BaseTest.UNIT)
public class AbstractCCHistoryModelTest {

    @DataProvider
    public Object[][] isValidDataProvider() {
        return new Object[][] {
                { null, null, null, false },
                { dt(2014, 1, 1), null, dt(2014, 1, 1), false },
                { null, dt(2014, 1, 1), dt(2014, 1, 1), false },
                { dt(2014, 1, 2), null, dt(2014, 1, 1), false },
                { dt(2014, 1, 1), dt(2014, 1, 1), dt(2014, 1, 1), true },
                { dt(2014, 1, 1), dt(2014, 1, 1), dt(2014, 1, 2), false },
                { dt(2014, 1, 1), dt(2014, 1, 2), dt(2014, 1, 2), true },
                { dt(2014, 1, 1), dt(2014, 1, 2), dt(2014, 1, 3), false },
        };
    }

    @Test(dataProvider = "isValidDataProvider")
    public void isValid(LocalDateTime gueltigVon, LocalDateTime gueltigBis, LocalDateTime referenceDate, boolean isValid) throws Exception {
        AbstractCCHistoryModel historyModel = createHistoryModel(gueltigVon, gueltigBis);
        assertEquals(historyModel.isValid(DateConverterUtils.asDate(referenceDate)), isValid);
    }

    private AbstractCCHistoryModel createHistoryModel(LocalDateTime gueltigVon, LocalDateTime gueltigBis) {
        AbstractCCHistoryModel abstractCCHistoryModel = new AbstractCCHistoryModel() {};
        abstractCCHistoryModel.setGueltigVon(DateConverterUtils.asDate(gueltigVon) );
        abstractCCHistoryModel.setGueltigBis(DateConverterUtils.asDate(gueltigBis) );
        return abstractCCHistoryModel;
    }

    private LocalDateTime dt(int year, int month, int day) {
        return LocalDateTime.of(year, month, day, 0, 0, 0, 0);
    }

}
