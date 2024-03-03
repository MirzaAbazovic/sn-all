/*

 * Copyright (c) 2016 - M-net Telekommunikations GmbH

 * All rights reserved.

 * -------------------------------------------------------

 * File created: 22.04.2016

 */

package de.augustakom.hurrican.service.wholesale;


import de.mnet.esb.cdm.resource.resourceordermanagementnotificationservice.v1.ErrorType;
import de.mnet.esb.cdm.resource.resourceordermanagementnotificationservice.v1.NotifyPortOrderUpdate;

final class ErrorNotificationMapper {

    private ErrorNotificationMapper() {
    }

    static NotifyPortOrderUpdate toErrorNotification(final WholesaleException e, final String orderId, final String notificationType) {
        final ErrorType errorType = new ErrorType();
        errorType.setErrorCode(e.fehler.code);
        errorType.setErrorDescription(e.getFehlerBeschreibung());

        final NotifyPortOrderUpdate update = new NotifyPortOrderUpdate();
        update.setError(errorType);
        update.setNotificationType(notificationType);
        update.setOrderId(orderId);

        return update;
    }
}
