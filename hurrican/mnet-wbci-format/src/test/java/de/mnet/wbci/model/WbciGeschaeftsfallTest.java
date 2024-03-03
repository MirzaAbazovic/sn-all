package de.mnet.wbci.model;

import static de.mnet.wbci.TestGroups.*;
import static org.testng.Assert.*;

import java.util.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.mnet.wbci.model.builder.WbciGeschaeftsfallKueMrnBuilder;

@Test(groups = UNIT)
public class WbciGeschaeftsfallTest {

    @DataProvider
    public Object[][] getOrderNoOrigsDataProvider() {
        return new Object[][] {
                { new WbciGeschaeftsfallKueMrnBuilder()
                        .withBillingOrderNoOrig(1L)
                        .withNonBillingRelevantOrderNos(new HashSet<>(Arrays.asList(2L, 3L)))
                        .build(), new HashSet<>(Arrays.asList(1L, 2L, 3L)) },
                { new WbciGeschaeftsfallKueMrnBuilder()
                        .withBillingOrderNoOrig(1L)
                        .withNonBillingRelevantOrderNos(null)
                        .build(), new HashSet<>(Arrays.asList(1L)) },
                { new WbciGeschaeftsfallKueMrnBuilder()
                        .withBillingOrderNoOrig(1L)
                        .withNonBillingRelevantOrderNos(Collections.EMPTY_SET)
                        .build(), new HashSet<>(Arrays.asList(1L)) },
                { new WbciGeschaeftsfallKueMrnBuilder()
                        .withBillingOrderNoOrig(null)
                        .withNonBillingRelevantOrderNos(Collections.EMPTY_SET)
                        .build(), Collections.EMPTY_SET },
                { new WbciGeschaeftsfallKueMrnBuilder()
                        .withBillingOrderNoOrig(null)
                        .withNonBillingRelevantOrderNos(new HashSet<>(Arrays.asList(2L, 3L)))
                        .build(), new HashSet<>(Arrays.asList(2L, 3L)) },
        };
    }

    @Test(dataProvider = "getOrderNoOrigsDataProvider")
    public void getOrderNoOrigs(WbciGeschaeftsfall wbciGeschaeftsfall, Set<Long> expectedOrderNoOrigs) {
        final Set<Long> orderNoOrigs = wbciGeschaeftsfall.getOrderNoOrigs();
        assertNotNull(orderNoOrigs);
        assertEquals(orderNoOrigs.size(), expectedOrderNoOrigs.size());
        assertTrue(orderNoOrigs.containsAll(expectedOrderNoOrigs));
    }

}
