package de.mnet.hurrican.webservice.customerorder.services;

import static de.mnet.hurrican.webservice.customerorder.services.PublicOrderStatus.StatusValue.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import org.apache.commons.collections.ListUtils;
import org.testng.annotations.Test;

import de.mnet.common.tools.DateConverterUtils;
import static de.mnet.hurrican.webservice.customerorder.services.PublicOrderStatus.*;

/**
 * UT for {@link OrderStatusServiceImpl}
 *
 */
public class OrderStatusServiceImplTest {

    @Test
    public void testBestStatus() throws Exception {
        final OrderStatusServiceImpl statusService = new OrderStatusServiceImpl();
        assertEquals(statusService.bestStatus(Arrays.asList(create(ANBIETERWECHSEL),
                create(START_BEARBEITUNG), create(UNDEFINIERT))),
                create(ANBIETERWECHSEL));
        assertEquals(statusService.bestStatus(Arrays.asList(create(START_BEARBEITUNG),
                create(ANBIETERWECHSEL), create(UNDEFINIERT))),
                create(ANBIETERWECHSEL));
        assertEquals(statusService.bestStatus(Arrays.asList(create(UNDEFINIERT),
                create(START_BEARBEITUNG), create(ANBIETERWECHSEL))),
                create(ANBIETERWECHSEL));
        assertEquals(statusService.bestStatus(Arrays.asList(create(UNDEFINIERT),
                create(ANBIETERWECHSEL), create(START_BEARBEITUNG))),
                create(ANBIETERWECHSEL));
        assertEquals(statusService.bestStatus(Arrays.asList(
                create(LEITUNGSBESTELLUNG_NEGATIV), create(START_BEARBEITUNG))),
                create(LEITUNGSBESTELLUNG_NEGATIV));
        assertEquals(statusService.bestStatus(Arrays.asList(
                create(START_BEARBEITUNG), create(LEITUNGSBESTELLUNG_NEGATIV))),
                create(LEITUNGSBESTELLUNG_NEGATIV));
        assertEquals(statusService.bestStatus(ListUtils.EMPTY_LIST), create(UNDEFINIERT));
    }

    @Test
    public void testIsTodayIsInFuture() throws Exception {
        final OrderStatusServiceImpl statusService = new OrderStatusServiceImpl();
        final Date now = new Date();
        final Date yesterday = DateConverterUtils.asDate(LocalDateTime.now().minusDays(1));
        final Date tomorrow = DateConverterUtils.asDate(LocalDateTime.now().plusDays(1));

        assertTrue(statusService.isToday(now));
        assertFalse(statusService.isToday(yesterday));
        assertFalse(statusService.isToday(tomorrow));

        assertFalse(statusService.isInFuture(now));
        assertFalse(statusService.isInFuture(yesterday));
        assertTrue(statusService.isInFuture(tomorrow));
    }

    @Test
    public void testNeedToEvaluate() throws Exception {
        final OrderStatusServiceImpl statusService = new OrderStatusServiceImpl();
        assertTrue(statusService.needToEvaluate(START_BEARBEITUNG, null));
        assertTrue(statusService.needToEvaluate(START_BEARBEITUNG, ListUtils.EMPTY_LIST));
        assertTrue(statusService.needToEvaluate(START_BEARBEITUNG, Arrays.asList(create(UNDEFINIERT)))); // START_BEARBEITUNG > UNDEFINIERT
        assertFalse(statusService.needToEvaluate(START_BEARBEITUNG, Arrays.asList(create(SCHALTTERMIN_NEGATIV)))); // START_BEARBEITUNG < SCHALTTERMIN_NEGATIV
        assertFalse(statusService.needToEvaluate(START_BEARBEITUNG, Arrays.asList(create(UNDEFINIERT), create(SCHALTTERMIN_NEGATIV))));
        assertFalse(statusService.needToEvaluate(START_BEARBEITUNG, Arrays.asList(create(SCHALTTERMIN_NEGATIV), create(UNDEFINIERT))));
        assertFalse(statusService.needToEvaluate(START_BEARBEITUNG, Arrays.asList(create(START_BEARBEITUNG)))); // START_BEARBEITUNG == START_BEARBEITUNG
    }
}