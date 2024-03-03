/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.12.13
 */
package de.mnet.wita.message.auftrag;

import static de.mnet.wita.TestGroups.*;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.mnet.wita.message.GeschaeftsfallTyp;

@Test(groups = UNIT)
public class KundenwunschterminTest {

    @DataProvider
    public Object[][] possibleForAtLeastOne() {
        // @formatter:off
        return new Object[][] {
                {Kundenwunschtermin.Zeitfenster.SLOT_2, true, new GeschaeftsfallTyp[] { GeschaeftsfallTyp.KUENDIGUNG_KUNDE} },
                {Kundenwunschtermin.Zeitfenster.SLOT_2, false, new GeschaeftsfallTyp[] { GeschaeftsfallTyp.BEREITSTELLUNG} },
                {Kundenwunschtermin.Zeitfenster.SLOT_3, true, new GeschaeftsfallTyp[] { GeschaeftsfallTyp.RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG} },
                {Kundenwunschtermin.Zeitfenster.SLOT_3, true, new GeschaeftsfallTyp[] { GeschaeftsfallTyp.VERBUNDLEISTUNG} },
                {Kundenwunschtermin.Zeitfenster.SLOT_3, false, new GeschaeftsfallTyp[] { GeschaeftsfallTyp.BEREITSTELLUNG} },
                {Kundenwunschtermin.Zeitfenster.SLOT_4, true, new GeschaeftsfallTyp[] { GeschaeftsfallTyp.RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG} },
                {Kundenwunschtermin.Zeitfenster.SLOT_4, true, new GeschaeftsfallTyp[] { GeschaeftsfallTyp.VERBUNDLEISTUNG} },
                {Kundenwunschtermin.Zeitfenster.SLOT_4, false, new GeschaeftsfallTyp[] { GeschaeftsfallTyp.BEREITSTELLUNG} },
                {Kundenwunschtermin.Zeitfenster.SLOT_6, true, new GeschaeftsfallTyp[] { GeschaeftsfallTyp.RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG} },
                {Kundenwunschtermin.Zeitfenster.SLOT_6, true, new GeschaeftsfallTyp[] { GeschaeftsfallTyp.VERBUNDLEISTUNG} },
                {Kundenwunschtermin.Zeitfenster.SLOT_6, false, new GeschaeftsfallTyp[] { GeschaeftsfallTyp.BEREITSTELLUNG} },
                {Kundenwunschtermin.Zeitfenster.SLOT_7, true, new GeschaeftsfallTyp[] { GeschaeftsfallTyp.BEREITSTELLUNG} },
                {Kundenwunschtermin.Zeitfenster.SLOT_7, false, new GeschaeftsfallTyp[] { GeschaeftsfallTyp.KUENDIGUNG_KUNDE} },
                {Kundenwunschtermin.Zeitfenster.SLOT_9, true, new GeschaeftsfallTyp[] { GeschaeftsfallTyp.BEREITSTELLUNG} },
                {Kundenwunschtermin.Zeitfenster.SLOT_9, false, new GeschaeftsfallTyp[] { GeschaeftsfallTyp.KUENDIGUNG_KUNDE} },
        };
        // @formatter:on
    }

    @Test(dataProvider = "possibleForAtLeastOne")
    public void isPossibleForAtLeastOneOf(Kundenwunschtermin.Zeitfenster zeitfenster, boolean expectedResult, GeschaeftsfallTyp[] geschaeftsfallTyps) {
        Assert.assertEquals(zeitfenster.isPossibleForAtLeastOneOf(geschaeftsfallTyps), expectedResult);
    }

    @DataProvider
    public Object[][] defaultZeitfenster() {
        // @formatter:off
        return new Object[][] {
                { GeschaeftsfallTyp.RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG, Kundenwunschtermin.Zeitfenster.SLOT_9},
                { GeschaeftsfallTyp.KUENDIGUNG_KUNDE, Kundenwunschtermin.Zeitfenster.SLOT_2},
                { GeschaeftsfallTyp.BEREITSTELLUNG, Kundenwunschtermin.Zeitfenster.SLOT_9},
                { GeschaeftsfallTyp.VERBUNDLEISTUNG, Kundenwunschtermin.Zeitfenster.SLOT_9},
        };
        // @formatter:on
    }

    @Test(dataProvider = "defaultZeitfenster")
    public void getDefaultZeitfenster(GeschaeftsfallTyp geschaeftsfallTyp,
            Kundenwunschtermin.Zeitfenster expectedResult) {
        Assert.assertEquals(Kundenwunschtermin.Zeitfenster.getDefaultZeitfenster(geschaeftsfallTyp), expectedResult);
    }

}
