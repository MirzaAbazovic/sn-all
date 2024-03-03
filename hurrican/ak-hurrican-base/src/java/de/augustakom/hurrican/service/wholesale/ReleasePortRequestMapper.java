/*

 * Copyright (c) 2016 - M-net Telekommunikations GmbH

 * All rights reserved.

 * -------------------------------------------------------

 * File created: 06.04.2016

 */

package de.augustakom.hurrican.service.wholesale;


import java.time.*;

import de.augustakom.hurrican.model.wholesale.WholesaleReleasePortRequest;
import de.mnet.esb.cdm.resource.resourceordermanagementnotificationservice.v1.ErrorType;
import de.mnet.esb.cdm.resource.resourceordermanagementnotificationservice.v1.NotificationType;
import de.mnet.esb.cdm.resource.resourceordermanagementnotificationservice.v1.NotifyPortOrderUpdate;

/**
 * Der ReleasePortRequestMapper. &Uuml;bernimmt Mapping von ReleasePortRequests zu {@link NotifyPortOrderUpdate} im
 * Erfolgs-/und Fehlerfall.
 * <p>
 * Created by petersde on 06.04.2016.
 */
public final class ReleasePortRequestMapper {

    private ReleasePortRequestMapper() {
    }

    /**
     * Mapping zu NotifyPortOrderUpdate f&uuml;r erfolgreiche ReleasePortRequests.
     *
     * @param orderId       die AuftragsID.
     * @param lineId        die LineID.
     * @param executionDate Datum, wann der Port freigegeben wird.
     * @return {@link NotifyPortOrderUpdate} Objekt, dass die &uuml;bergebenen Daten enth&auml;lt.
     */
    public static NotifyPortOrderUpdate toReleasePortResponseNotification(final String orderId, final String lineId, final LocalDate executionDate) {
        final NotifyPortOrderUpdate update = new NotifyPortOrderUpdate();
        update.setOrderId(orderId);
        update.setNotificationType("releasePortResponse");
        final NotificationType notificationType = new NotificationType();
        notificationType.setLineId(lineId);
        notificationType.setExecutionDate(executionDate);
        update.setNotification(notificationType);
        return update;
    }

    /**
     * Mapping zu NotifyPortOrderUpdate f&uuml;r fehlerhafte ReleasePortRequests.
     *
     * @param e       die aufgetretene Exception.
     * @param orderId die AuftragsID.
     * @return {@link NotifyPortOrderUpdate} Objekt mit Fehlercode, Fehlermeldung sowie der AuftragsID.
     */
    public static NotifyPortOrderUpdate toReleasePortErrorNotification(final WholesaleException e, final String orderId) {
        final ErrorType errorType = new ErrorType();
        errorType.setErrorCode(e.fehler.code);
        errorType.setErrorDescription(e.getFehlerBeschreibung());
        final NotifyPortOrderUpdate update = new NotifyPortOrderUpdate();
        update.setError(errorType);
        update.setNotificationType("releasePortResponse");
        update.setOrderId(orderId);
        return update;
    }

    public static WholesaleReleasePortRequest toWholesaleReleasePortRequest(final String orderId, final String lineId, final Long sessionId,
            final LocalDate releaseDate) {
        final WholesaleReleasePortRequest result = new WholesaleReleasePortRequest();
        result.setOrderId(orderId);
        result.setLineId(lineId);
        result.setSessionId(sessionId);
        result.setReleaseDate((releaseDate != null) ? releaseDate : LocalDate.now());
        return result;
    }
}
