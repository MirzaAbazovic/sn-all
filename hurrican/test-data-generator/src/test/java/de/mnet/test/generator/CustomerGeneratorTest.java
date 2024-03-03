package de.mnet.test.generator;

import static org.testng.Assert.*;

import java.util.*;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.test.generator.model.Customer;

@Test(groups = { BaseTest.UNIT })
public class CustomerGeneratorTest {

    static public List<String> getAttributeAsList(Customer customer) {
        List<String> strings = new ArrayList<>(Arrays.asList(
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getCompanyName()
        ));
        strings.addAll(AddressGeneratorTest.getAttributeAsList(customer.getPrimaryAddress()));
        return strings;
    }

    @Test
    public void testGetRandomCustomer() throws Exception {
        for (int i = 0; i < 20; i++) {
            Customer cust = CustomerGenerator.generateCustomer();
            assertNotNull(cust);
            assertNotNull(cust.getPrimaryAddress());
            for (String value : getAttributeAsList(cust)) {
                assertTrue(StringUtils.isNotEmpty(value));
            }
            assertTrue(cust.getEmail().contains("@"), "wrong e-mail address: " + cust.getEmail());
        }
    }
}