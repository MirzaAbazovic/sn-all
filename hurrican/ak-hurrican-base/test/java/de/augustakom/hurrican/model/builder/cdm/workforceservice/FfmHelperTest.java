package de.augustakom.hurrican.model.builder.cdm.workforceservice;

import static de.augustakom.common.BaseTest.UNIT;
import static org.testng.Assert.*;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

@Test(groups = UNIT)
public class FfmHelperTest extends BaseTest{

    @DataProvider
    private Object[][] convertBooleanDataProvider() {
        return new Object[][] {
                {Boolean.TRUE, "ja"},
                {Boolean.FALSE, "nein"},
                {null, "nein"},
        };
    }

    @Test(dataProvider = "convertBooleanDataProvider")
    public void convertBoolean(Boolean toConvert, String expected) {
        assertEquals(FfmHelper.convertBoolean(toConvert), expected);
    }

}
