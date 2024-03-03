/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.02.14
 */
package de.augustakom.hurrican.tools.comparator;

import static org.testng.Assert.*;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.shared.iface.HwEqnAwareModel;

@Test(groups = BaseTest.UNIT)
public class HwEqnComparatorTest extends BaseTest {

    private class HwEqnHolder implements HwEqnAwareModel {
        private String hwEqn;

        public HwEqnHolder(String hwEqn) {
            this.hwEqn = hwEqn;
        }

        public String getHwEQN() {
            return hwEqn;
        }
    }

    @DataProvider
    public Object[][] dataProviderCompare() {
        return new Object[][] {
                { "C-Arnul204-Arnul202-01", "C-Arnul204-Arnul204-01", -2 },
                { "DTAG-L2TP-01--9", "DTAG-L2TP-01--89", -1 },
                { "FOM 2 04-04", "FOM 2 04-05", -1 },
                { "U01-1-000-14", "U01-2-000-14", -1 },
                { "C-Arnul204-Arnul204-01", "C-Arnul204-Arnul202-01", +2 },
                { "DTAG-L2TP-01--89", "DTAG-L2TP-01--9", +1 },
                { "FOM 2 04-05", "FOM 2 04-04", +1 },
                { "U01-2-000-14", "U01-1-000-14", +1 },
                { "Ü01/1-302-07", "Ü01/1-302-07", 0 },
                { "1-1-12-9", "1-1-12-10", -1 },
                { "1-1-12-10", "1-1-12-9", +1 },
                { "1-1-12-9", "1-2-13-1", -1 }
        };
    }

    @Test(dataProvider = "dataProviderCompare")
    public void testCompare(String hwEqn1, String hwEqn2, int result) {
        final HwEqnHolder holder1 = new HwEqnHolder(hwEqn1);
        final HwEqnHolder holder2 = new HwEqnHolder(hwEqn2);
        final HwEqnComparator comparator = new HwEqnComparator();
        assertEquals(comparator.compare(holder1, holder2), result);
    }
}
