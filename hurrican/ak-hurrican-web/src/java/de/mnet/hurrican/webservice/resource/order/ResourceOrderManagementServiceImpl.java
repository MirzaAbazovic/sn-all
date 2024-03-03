/*

 * Copyright (c) 2016 - M-net Telekommunikations GmbH

 * All rights reserved.

 * -------------------------------------------------------

 * File created: 01.03.2016

 */

package de.mnet.hurrican.webservice.resource.order;


import static de.augustakom.hurrican.service.wholesale.ModifyPortReservationDateMapper.*;
import static de.augustakom.hurrican.service.wholesale.ReleasePortRequestMapper.*;
import static de.augustakom.hurrican.service.wholesale.ReservePortRequestMapper.*;

import java.time.*;
import java.util.function.*;
import javax.annotation.*;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.augustakom.hurrican.model.wholesale.WholesaleModifyPortReservationDateRequest;
import de.augustakom.hurrican.model.wholesale.WholesaleModifyPortReservationDateResponse;
import de.augustakom.hurrican.model.wholesale.WholesaleReleasePortRequest;
import de.augustakom.hurrican.model.wholesale.WholesaleReservePortRequest;
import de.augustakom.hurrican.model.wholesale.WholesaleReservePortResponse;
import de.augustakom.hurrican.service.wholesale.WholesaleException;
import de.augustakom.hurrican.service.wholesale.WholesaleService;
import de.mnet.esb.cdm.resource.resourceordermanagementnotificationservice.v1.NotifyPortOrderUpdate;
import de.mnet.esb.cdm.resource.resourceordermanagementnotificationservice.v1.ResourceOrderManagementNotificationService;
import de.mnet.esb.cdm.resource.resourceordermanagementservice.v1.Ekp;
import de.mnet.esb.cdm.resource.resourceordermanagementservice.v1.Product;
import de.mnet.esb.cdm.resource.resourceordermanagementservice.v1.ReservePort;
import de.mnet.esb.cdm.resource.resourceordermanagementservice.v1.ResourceOrderManagementService;
import de.mnet.hurrican.startup.HurricanWebUserSession;


@Component
/**
 * Implementiert den Endpoint des ResourceOrderManagementService
 * Responses erfolgen asynchron über den ResourceOrderManagementNotificationService, das Routing zum Aufrufer erfolgt über Atlas
 * anhand eines Prefixes in der orderId
 * Wird derzeit von Hermes konsumiert (orderId = HER_*)
 */
public class ResourceOrderManagementServiceImpl implements ResourceOrderManagementService {

    private static final Logger LOGGER = Logger.getLogger(ResourceOrderManagementServiceImpl.class);

    @Resource(name = "de.augustakom.hurrican.service.wholesale.WholesaleService")
    private WholesaleService wholesaleService;

    @Autowired
    private ResourceOrderManagementNotificationService notificationService;

    /**
     * Gibt Port wieder frei und sendet eine Benachrichtigung &uuml;ber den (Miss-)Erfolg an den Aufrufer.
     *
     * @param orderId die OrderID.
     * @param lineId  die LineID.
     */
    @Override
    public void releasePort(String orderId, String lineId, LocalDate releaseDate) {
        handleRequest(
                () -> {
                    final WholesaleReleasePortRequest whsRequest =
                            toWholesaleReleasePortRequest(orderId, lineId, HurricanWebUserSession.getSessionId(), releaseDate);
                    return toReleasePortResponseNotification(orderId, lineId, wholesaleService.releasePort(whsRequest));
                },
                e -> {
                    LOGGER.error("Fehler bei der Portfreigabe", e);
                    return toReleasePortErrorNotification(e, orderId);
                }
        );
    }

    @Override
    public void cancelModifyPort(String orderId, String lineId) {
        throw new NotImplementedException("cancelModifyPort is not supported");
    }

    @Override
    public void modifyPort(String orderId, String lineId, Ekp ekp, Ekp reseller, Product product, LocalDate desiredExecutionDate, boolean changeOfPortAllowed) {
        throw new NotImplementedException("modifyPort is not supported");
    }



    /**
     * Löst eine Portreservierung/Bauauftragserstelung aus und sendet eine Benachrichtigung über den (Miss-)Erfolg an
     * den Aufrufer.
     */
    @Override
    public void reservePort(String orderId, String extOrderId, Ekp ekp, Ekp reseller, long geoId, ReservePort.ContactPersons contactPersons, Product product,
            LocalDate desiredExecutionDate, ReservePort.TimePeriod timePeriod, String connectionUnitLocation) {
        handleRequest(
                () -> {
                    final WholesaleReservePortRequest whsRequest =
                            toWholesaleReservePortRequest(orderId, extOrderId, ekp, reseller, geoId, contactPersons, product,
                                    desiredExecutionDate, HurricanWebUserSession.getSessionId(), timePeriod, connectionUnitLocation);
                    final WholesaleReservePortResponse whsResponse = wholesaleService.reservePort(whsRequest);
                    return toReservePortResponseNotification(whsResponse, orderId);
                },
                e -> {
                    LOGGER.error("Fehler bei der Portreservierung, sende error-notification", e);
                    return toReservePortErrorNotification(e, orderId);
                }
        );
    }

    @Override
    public void modifyPortReservationDate(String orderId, String lineId, LocalDate desiredExecutionDate) {
        handleRequest(
                () -> {
                    final WholesaleModifyPortReservationDateRequest whsRequest =
                            toWholesaleModifyPortReservationDateRequest(lineId, desiredExecutionDate, HurricanWebUserSession.getSessionId());
                    final WholesaleModifyPortReservationDateResponse whsResponse =
                            wholesaleService.modifyPortReservationDate(whsRequest);
                    return toModifyPortReservationDateNotification(orderId, whsResponse);
                },
                e -> {
                    LOGGER.error(
                            String.format("Fehler bei der Terminverschiebung von lineId: %s orderId %s execDate: %s, sende error-notification",
                                    orderId, lineId, desiredExecutionDate), e);
                    return toModifyPortReservationDateErrorNotification(e, orderId);
                }
        );
    }

    private void handleRequest(final Supplier<NotifyPortOrderUpdate> requestHandler,
            final Function<WholesaleException, NotifyPortOrderUpdate> errorHandler) {
        try {
            notificationService.notifyPortOrderUpdate(requestHandler.get());
        }
        catch (WholesaleException e) {
            notificationService.notifyPortOrderUpdate(errorHandler.apply(e));
        }
        catch (Exception e) {
            LOGGER.error("Unerwarteter Fehler im ResourceOrderManagementService: ", e);
        }
    }
}
