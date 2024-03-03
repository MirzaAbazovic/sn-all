package de.mnet.test.generator;

import static org.testng.Assert.*;

import java.util.*;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.test.generator.model.Address;

@Test(groups = { BaseTest.UNIT })
public class AddressGeneratorTest {

    static public List<String> getAttributeAsList(Address randomAddress) {
        return Arrays.asList(
                randomAddress.getCity(),
                randomAddress.getCountry(),
                randomAddress.getLatCoordination(),
                randomAddress.getLngCoordination(),
                randomAddress.getProvince(),
                randomAddress.getZip(),
                randomAddress.getStreet()
        );
    }

    @Test
    public void testGetRandomAddress() throws Exception {
        for (int i = 0; i < 20; i++) {
            Address adr = AddressGenerator.getInstance().getRandomAddress();
            assertNotNull(adr);
            assertTrue(adr.getZip().matches("^\\d{1,5}$"), adr.getZip());
            for (String value : getAttributeAsList(adr)) {
                assertTrue(StringUtils.isNotEmpty(value));
            }
        }
    }
}