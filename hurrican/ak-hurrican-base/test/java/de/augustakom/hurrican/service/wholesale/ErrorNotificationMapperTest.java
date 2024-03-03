package de.augustakom.hurrican.service.wholesale;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.testng.annotations.Test;

import de.augustakom.common.tools.lang.RandomTools;
import de.mnet.esb.cdm.resource.resourceordermanagementnotificationservice.v1.NotificationType;
import de.mnet.esb.cdm.resource.resourceordermanagementnotificationservice.v1.NotifyPortOrderUpdate;

public class ErrorNotificationMapperTest {

    @Test
    public void testToErrorNotification() throws Exception {
        final WholesaleException e = new TechnischNichtMoeglichException(RandomTools.createString());
        final String orderId = RandomTools.createString();
        final String notificationType = RandomTools.createString();

        final NotifyPortOrderUpdate result = ErrorNotificationMapper.toErrorNotification(e, orderId, notificationType);

        assertThat(result.getNotificationType(), equalTo(notificationType));
        assertThat(result.getOrderId(), equalTo(orderId));
        assertThat(result.getError().getErrorCode(), equalTo(e.fehler.code));
        assertThat(result.getError().getErrorDescription(), equalTo(e.getFehlerBeschreibung()));
        assertThat(result.getNotification(), nullValue(NotificationType.class));
    }
}