package de.mnet.hurrican.simulator.matcher;

import com.consol.citrus.exceptions.ValidationException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

@Test(groups = BaseTest.UNIT)
public class ContainedInListValidationMatcherTest {

    @DataProvider
    private Object[][] validEntriesDataProvider() {
        return new Object[][] {
                {"2345", "2345, 2346, 2347"},
                {"2346", "2345,2346,2347"},
                {"2347", "           2345,    2346 ,2347"},
                {"   2347    ", "           2345,    2346 ,  2347                   "},
                {"2346", "           2345,    2346 ,2347 (,)"},
                {"2347", "           2345,    2346 ,2347 (,)                 "},
                {"2347", "           2345|    2346 |2347 (|)                 "},
                {"2347", "           2345|    2346 |2347 (|)             adfkjahsdflkjashfdlk    "},
        };
    }

    @Test(dataProvider = "validEntriesDataProvider")
    public void testValidEntries(String value, String control) {
        ContainedInListValidationMatcher matcher = new ContainedInListValidationMatcher();
        matcher.validate("fieldName", value, control, null);
    }

    @DataProvider
    private Object[][] invalidEntriesDataProvider() {
        return new Object[][] {
                {"2344", ""},
                {"2344", null},
                {"", "2345, 2346, 2347"},
                {null, "2345, 2346, 2347"},
                {"2344", "2345, 2346, 2347"},
                {"2347", "           2345,    2346 ,2347 (|)                 "},
                {"2347", " (|)                 "},
        };
    }

    @Test(dataProvider = "invalidEntriesDataProvider", expectedExceptions = ValidationException.class)
    public void testInvalidEntries(String value, String control) {
        ContainedInListValidationMatcher matcher = new ContainedInListValidationMatcher();
        matcher.validate("fieldName", value, control, null);
    }

}
