package de.augustakom.hurrican.service.wholesale;

import static de.augustakom.hurrican.service.wholesale.ReleasePortRequestMapper.*;
import static de.augustakom.hurrican.service.wholesale.WholesaleException.WholesaleFehlerCode.*;
import static org.testng.Assert.*;

import java.time.*;
import org.testng.annotations.Test;

import de.mnet.esb.cdm.resource.resourceordermanagementnotificationservice.v1.NotifyPortOrderUpdate;

public class ReleasePortRequestMapperTest {

    /**
     * Test f&uuml;r {@link ReleasePortRequestMapper#toReleasePortResponseNotification(String, String, LocalDate)}
     * <p>
     * Testet das Mapping einer Erfolgsmeldung beim ReleasePortRequest.
     */
    @Test
    public void testToReleasePortResponseNotification() {
        final String orderId = "some_order_id";
        final String lineId = "some_line_id";
        final LocalDate executionDate = LocalDate.now();

        final NotifyPortOrderUpdate current = toReleasePortResponseNotification(orderId, lineId, executionDate);

        assertNotNull(current);
        assertNotNull(current.getNotification());
        assertEquals(current.getOrderId(), orderId);
        assertEquals(current.getNotification().getLineId(), lineId);
        assertEquals(current.getNotification().getExecutionDate(), executionDate);
    }

    /**
     * Test f&uuml;r {@link ReleasePortRequestMapper#toReleasePortErrorNotification(WholesaleException, String)}
     * <p>
     * Testet das Mapping einer Fehlermeldung beim ReleasePortRequest.
     */
    @Test
    public void testToReleasePortErrorNotification() {
        final String orderId = "some_order_id";
        final String lineId = "some_line_id";
        final String errMsg = "Port konnte nicht freigegeben werden";
        final WholesaleServiceException e = new WholesaleServiceException(ERROR_RELEASING_PORT, errMsg, lineId,
                new IllegalArgumentException());

        final NotifyPortOrderUpdate current = toReleasePortErrorNotification(e, orderId);

        assertNotNull(current);
        assertNotNull(current.getError());
        assertEquals(current.getError().getErrorCode(), "HUR-0007");
        assertEquals(current.getError().getErrorDescription(), e.getFehlerBeschreibung());
        assertEquals(current.getNotificationType(), "releasePortResponse");
        assertEquals(current.getOrderId(), orderId);
    }

}