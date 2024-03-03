/*

 * Copyright (c) 2016 - M-net Telekommunikations GmbH

 * All rights reserved.

 * -------------------------------------------------------

 * File created: 22.04.2016

 */

package de.augustakom.hurrican.service.wholesale;


import static de.augustakom.hurrican.service.wholesale.ErrorNotificationMapper.*;

import java.time.*;

import de.augustakom.hurrican.model.wholesale.WholesaleModifyPortReservationDateRequest;
import de.augustakom.hurrican.model.wholesale.WholesaleModifyPortReservationDateResponse;
import de.mnet.esb.cdm.resource.resourceordermanagementnotificationservice.v1.NotificationType;
import de.mnet.esb.cdm.resource.resourceordermanagementnotificationservice.v1.NotifyPortOrderUpdate;

public final class ModifyPortReservationDateMapper {

    private static final String NOTIFICATION_TYPE = "modifyPortReservationDateNotification";

    private ModifyPortReservationDateMapper() {
    }

    public static WholesaleModifyPortReservationDateRequest toWholesaleModifyPortReservationDateRequest(final String lineId, final LocalDate desiredExecDate, final Long sessionId) {
        final WholesaleModifyPortReservationDateRequest request = new WholesaleModifyPortReservationDateRequest();
        request.setLineId(lineId);
        request.setDesiredExecutionDate(desiredExecDate);
        request.setSessionId(sessionId);
        return request;
    }

    public static NotifyPortOrderUpdate toModifyPortReservationDateNotification(final String orderId, final WholesaleModifyPortReservationDateResponse response) {
        final NotifyPortOrderUpdate update = new NotifyPortOrderUpdate();
        final NotificationType notificationType = new NotificationType();
        notificationType.setLineId(response.getLineId());
        notificationType.setExecutionDate(response.getExecutionDate());
        update.setNotification(notificationType);
        update.setOrderId(orderId);
        update.setNotificationType(NOTIFICATION_TYPE);
        return update;
    }

    public static NotifyPortOrderUpdate toModifyPortReservationDateErrorNotification(final WholesaleException e, final String orderId) {
        return toErrorNotification(e, orderId, NOTIFICATION_TYPE);
    }

}
