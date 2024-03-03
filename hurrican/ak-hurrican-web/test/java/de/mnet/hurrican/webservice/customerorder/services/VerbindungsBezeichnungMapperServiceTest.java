package de.mnet.hurrican.webservice.customerorder.services;

import static org.testng.Assert.*;

import java.util.*;
import org.testng.annotations.Test;

import de.mnet.esb.cdm.customer.customerorderservice.v1.ESBFault;

/**
 * UT for {@link VerbindungsBezeichnungMapperService}
 */
public class VerbindungsBezeichnungMapperServiceTest {

    @Test
    public void testMapToLong() throws Exception {
        final List<Long> mapped = VerbindungsBezeichnungMapperService.mapToLong(Arrays.asList("1", "2"));
        assertEquals(mapped, Arrays.asList(1L, 2L));
    }

    @Test(expectedExceptions = ESBFault.class)
    public void testMapToLong_InvalidNumber() throws Exception {
        final List<Long> mapped = VerbindungsBezeichnungMapperService.mapToLong(Arrays.asList("1", "wrongnumber"));
    }
}