package de.mnet.test.generator.helper;

import static org.testng.Assert.*;

import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.test.generator.helper.DataHandler.DataFile;

@Test(groups = BaseTest.UNIT)
public class DataHandlerTest {

    @Test
    public void testGetDataFilePath() throws Exception {
        for (DataFile dataFile : DataFile.values()) {
            assertNotNull(dataFile.getReader());
        }
    }
}