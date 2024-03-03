package de.mnet.wita.message.meldung.position;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class LeitungsAbschnittTest {

    @DataProvider
    public Object[][] dataProviderValueOf() {
        // @formatter:off
        return new Object[][] {
                { null, null, Collections.EMPTY_LIST },
                { "", null, Collections.EMPTY_LIST },
                { null, "", Collections.EMPTY_LIST },
                { "", "", Collections.EMPTY_LIST },
                { "100", null, Collections.EMPTY_LIST },
                { null, "10", Collections.EMPTY_LIST },
                { "100/50/20", "10/20", Collections.EMPTY_LIST },
                { "100/50/20", "10/20/", Collections.EMPTY_LIST },
                { "/100/50", "10/20", Collections.EMPTY_LIST },
                { "/100/50", "10/20/", Collections.EMPTY_LIST },
                { "100/50", "10//20", Collections.EMPTY_LIST },

                { "100", "10", Arrays.asList(abschnitt(1, "100", "10")) },
                { "100/50", "10/20", Arrays.asList(abschnitt(1, "100", "10"), abschnitt(2, "50", "20")) },
                { "100/50/", "10/20/", Arrays.asList(abschnitt(1, "100", "10"), abschnitt(2, "50", "20")) },
                { "100//50", "10//20", Arrays.asList(abschnitt(1, "100", "10"), abschnitt(2, "", ""), abschnitt(3, "50", "20")) },
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderValueOf")
    public void testValueOf(String ll, String aqs, List<LeitungsAbschnitt> expectedResult) {
        assertThat(LeitungsAbschnitt.valueOf(ll, aqs).toString(), equalTo(expectedResult.toString()));
    }

    private LeitungsAbschnitt abschnitt(int idx, String ll, String aqs) {
        return new LeitungsAbschnitt(idx, ll, aqs);
    }
}
