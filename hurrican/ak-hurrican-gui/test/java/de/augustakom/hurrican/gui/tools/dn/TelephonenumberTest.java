package de.augustakom.hurrican.gui.tools.dn;

import static de.augustakom.common.BaseTest.*;
import static org.testng.Assert.*;

import org.apache.cxf.common.util.StringUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

/**
 *
 */
@Test(groups = UNIT)
public class TelephonenumberTest extends BaseTest {

    @DataProvider
    public Object[][] toStringDP() {
        return new Object[][] {
                { Telephonenumber.parse("0049", "170", "1234567890"), "49 170 1234567890"},
                { Telephonenumber.parse("  0049  ", "   170  ", "  1234567890  "), "49 170 1234567890"},
                { Telephonenumber.parse("0039", "0170", "1234567890"), "39 0170 1234567890"},
        };
    }

    @Test(dataProvider = "toStringDP")
    public void testToString(Telephonenumber telephonenumber, String expectedToStringOutput) {
        assertEquals(telephonenumber.toString(), expectedToStringOutput);
    }

    @DataProvider
    public Object[][] parseDP() {
        return new Object[][] {
                { "0049", "170", "1234567890", 49, "170", 1234567890L },
                { "", "170", "1234567890", null, null, null },
                { "0049", "170", "", 49, "170", null },
                { "0049", "", "1234567890", 49, null, null },
                { "0049", "0170", "1234567890", 49, "170", 1234567890L },
                { "0039", "0170", "1234567890", 39, "0170", 1234567890L}, //fuer Italien wird die Null vor dem AriaCode nicht entfernt !!!
        };
    }

    @Test(dataProvider = "parseDP")
    public void testParse(String cc, String ac, String number, Integer parsedCc, String parsedAc, Long parsedNumber) {
        Telephonenumber telephonenumber = Telephonenumber.parse(cc, ac, number);
        if (StringUtils.isEmpty(cc)) {
            assertNull(telephonenumber);
        }
        else {
            assertNotNull(telephonenumber);
            assertEquals(telephonenumber.getCountryCode(), parsedCc);
            assertEquals(telephonenumber.getAreaCode(), parsedAc);
            assertEquals(telephonenumber.getSubscriberNumber(), parsedNumber);
        }
    }

    @DataProvider
    public Object[][] isPublicLenghtOkDP() {
        return new Object[][] {
                { Telephonenumber.parse("0049", "170", "1234567890"), true}, // 15 Zeichen
                { Telephonenumber.parse("0049", "0170", "1234567890"), true}, // 15 Zeichen
                { Telephonenumber.parse("  0049  ", "   170  ", "  1234567890  "), true}, // 15 Zeichen mit Leerzeichen
                { Telephonenumber.parse("00491", "170", "1234567890"), false}, // 16 Zeichen
                { Telephonenumber.parse("0039", "0170", "1234567890"), false},  //16 Zeichen, fuer Italien wird die fuehrende Null im AreaCode mitgezaehlt
        };
    }

    @Test(dataProvider = "isPublicLenghtOkDP")
    public void testIsPublicLenghtOk(Telephonenumber telephonenumber, boolean lengthOk) {
        assertEquals(telephonenumber.isPublicLenghtOk(), lengthOk);
    }

}
