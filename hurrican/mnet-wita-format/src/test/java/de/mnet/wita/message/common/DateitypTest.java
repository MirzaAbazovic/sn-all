package de.mnet.wita.message.common;

import static de.mnet.wita.TestGroups.*;
import static org.testng.Assert.*;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@Test(groups = UNIT)
public class DateitypTest {

    @DataProvider
    private Object[][] fromExtensionDataProvider() {
        return new Object[][] {
                {"pdf", Dateityp.PDF},
                {"Pdf", Dateityp.PDF},
                {"PDF", Dateityp.PDF},
                {"tif", Dateityp.TIFF},
                {"jpg", Dateityp.JPEG},
                {"doc", Dateityp.MSWORD},
                {"xls", Dateityp.EXCEL},
                {"txt", Dateityp.UNKNOWN},
        };
    }

    @Test(dataProvider = "fromExtensionDataProvider")
    public void fromExtension(String fileExtension, Dateityp expected) {
        assertEquals(Dateityp.fromExtension(fileExtension), expected);
    }

}
