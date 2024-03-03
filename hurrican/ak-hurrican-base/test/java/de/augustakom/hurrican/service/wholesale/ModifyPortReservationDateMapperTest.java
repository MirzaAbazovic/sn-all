package de.augustakom.hurrican.service.wholesale;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.time.*;
import org.testng.annotations.Test;

import de.augustakom.common.tools.lang.RandomTools;
import de.augustakom.hurrican.model.wholesale.WholesaleModifyPortReservationDateRequest;
import de.augustakom.hurrican.model.wholesale.WholesaleModifyPortReservationDateResponse;
import de.mnet.esb.cdm.resource.resourceordermanagementnotificationservice.v1.ErrorType;
import de.mnet.esb.cdm.resource.resourceordermanagementnotificationservice.v1.NotifyPortOrderUpdate;

public class ModifyPortReservationDateMapperTest {

    @Test
    public void testToWholesaleModifyPortReservationDateRequest() throws Exception {
        final LocalDate execDate = LocalDate.MIN;
        final String lineId = RandomTools.createString();
        final Long sessionId = RandomTools.createLong();

        final WholesaleModifyPortReservationDateRequest result =
                ModifyPortReservationDateMapper.toWholesaleModifyPortReservationDateRequest(lineId, execDate, sessionId);

        assertThat(result.getDesiredExecutionDate(), equalTo(result.getDesiredExecutionDate()));
        assertThat(result.getLineId(), equalTo(result.getLineId()));
        assertThat(result.getSessionId(), equalTo(sessionId));
    }

    @Test
    public void testToModifyPortReservationDateNotification() throws Exception {
        final String orderId = RandomTools.createString();
        final WholesaleModifyPortReservationDateResponse in = new WholesaleModifyPortReservationDateResponse();
        in.setExecutionDate(LocalDate.MAX);
        in.setLineId(RandomTools.createString());

        final NotifyPortOrderUpdate result = ModifyPortReservationDateMapper.toModifyPortReservationDateNotification(orderId, in);
        assertThat(result.getNotificationType(), equalTo("modifyPortReservationDateNotification"));
        assertThat(result.getOrderId(), equalTo(orderId));
        assertThat(result.getNotification().getExecutionDate(), equalTo(in.getExecutionDate()));
        assertThat(result.getNotification().getLineId(), equalTo(in.getLineId()));
        assertThat(result.getError(), nullValue(ErrorType.class));
    }
}