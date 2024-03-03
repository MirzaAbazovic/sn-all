/*

 * Copyright (c) 2016 - M-net Telekommunikations GmbH

 * All rights reserved.

 * -------------------------------------------------------

 * File created: 01.03.2016

 */

package de.augustakom.hurrican.service.wholesale;


import static de.augustakom.hurrican.service.wholesale.ReservePortRequestMapper.NotificationDetailKey.*;
import static java.util.stream.Collectors.*;

import java.time.*;
import java.util.*;
import java.util.stream.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;

import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.model.wholesale.WholesaleContactPerson;
import de.augustakom.hurrican.model.wholesale.WholesaleEkpFrameContract;
import de.augustakom.hurrican.model.wholesale.WholesaleProduct;
import de.augustakom.hurrican.model.wholesale.WholesaleProductAttribute;
import de.augustakom.hurrican.model.wholesale.WholesaleProductName;
import de.augustakom.hurrican.model.wholesale.WholesaleReservePortRequest;
import de.augustakom.hurrican.model.wholesale.WholesaleReservePortResponse;
import de.mnet.esb.cdm.resource.resourceordermanagementnotificationservice.v1.ErrorType;
import de.mnet.esb.cdm.resource.resourceordermanagementnotificationservice.v1.NotificationDetailType;
import de.mnet.esb.cdm.resource.resourceordermanagementnotificationservice.v1.NotificationType;
import de.mnet.esb.cdm.resource.resourceordermanagementnotificationservice.v1.NotifyPortOrderUpdate;
import de.mnet.esb.cdm.resource.resourceordermanagementservice.v1.ContactPersonType;
import de.mnet.esb.cdm.resource.resourceordermanagementservice.v1.Ekp;
import de.mnet.esb.cdm.resource.resourceordermanagementservice.v1.Product;
import de.mnet.esb.cdm.resource.resourceordermanagementservice.v1.ReservePort;

public final class ReservePortRequestMapper {

    /**
     * Enum used for keys of NotificationDetailType
     */
    enum NotificationDetailKey {
        HURRICAN_AUFTRAGS_ID,
        A10NSP,
        A10NSP_PORT,
        SVLAN_EKP,
        DPO_NOCH_NICHT_VERBAUT,
        MANUELLE_PORTZUWEISUNG,
    }

    private ReservePortRequestMapper() {
    }

    public static NotifyPortOrderUpdate toReservePortResponseNotification(final WholesaleReservePortResponse response,
            final String orderId) {
        final NotifyPortOrderUpdate update = notifyPortOrderUpdate(orderId, "reservePortResponse");
        final NotificationType notificationType = notificationType(response);

        notificationType
                .getNotificationDetail()
                .addAll(notificationDetails(response));
        notificationType.setExecutionDate(response.getExecutionDate());
        update.setNotification(notificationType);

        return update;
    }

    private static NotifyPortOrderUpdate notifyPortOrderUpdate(String orderId, String reservePortResponse) {
        final NotifyPortOrderUpdate update = new NotifyPortOrderUpdate();
        update.setOrderId(orderId);
        update.setNotificationType(reservePortResponse);
        return update;
    }

    private static NotificationType notificationType(WholesaleReservePortResponse response) {
        final NotificationType notificationType = new NotificationType();
        notificationType.setLineId(response.getLineId());
        notificationType.setExecutionDate(response.getExecutionDate());
        return notificationType;
    }

    private static ImmutableList<NotificationDetailType> notificationDetails(WholesaleReservePortResponse response) {
        ImmutableList.Builder<NotificationDetailType> builder = ImmutableList.<NotificationDetailType>builder()
                .add(notificationDetailType(HURRICAN_AUFTRAGS_ID.name(), response.getHurricanAuftragId()))
                .add(notificationDetailType(DPO_NOCH_NICHT_VERBAUT.name(), String.valueOf(response.isDpoNochNichtVerbaut())));
        if (StringUtils.isNotBlank(response.getA10nsp())) {
            builder.add(notificationDetailType(A10NSP.name(), response.getA10nsp()));
        }
        if (StringUtils.isNotBlank(response.getA10nspPort())) {
            builder.add(notificationDetailType(A10NSP_PORT.name(), response.getA10nspPort()));
        }
        if (StringUtils.isNotBlank(response.getSvlanEkp())) {
            builder.add(notificationDetailType(SVLAN_EKP.name(), response.getSvlanEkp()));
        }
        if (response.isManuellePortzuweisung()) {
            builder.add(notificationDetailType(MANUELLE_PORTZUWEISUNG.name(), String.valueOf(response.isManuellePortzuweisung())));
        }
        return builder.build();
    }

    private static NotificationDetailType notificationDetailType(final String key, final String value) {
        final NotificationDetailType notificationDetailType = new NotificationDetailType();
        notificationDetailType.setKey(key);
        notificationDetailType.setValue(value);
        return notificationDetailType;
    }

    public static NotifyPortOrderUpdate toReservePortErrorNotification(final WholesaleException e, final String orderId) {
        final ErrorType errorType = errorType(e);
        final NotifyPortOrderUpdate update = notifyPortOrderUpdate(orderId, "reservePortResponse");
        update.setError(errorType);

        return update;
    }

    private static ErrorType errorType(WholesaleException e) {
        final ErrorType errorType = new ErrorType();
        errorType.setErrorCode(e.fehler.code);
        errorType.setErrorDescription(e.getFehlerBeschreibung());
        return errorType;
    }

    public static WholesaleReservePortRequest toWholesaleReservePortRequest(final String orderId, final String extOrderId, final Ekp ekp,
            final Ekp reseller, final long geoId, ReservePort.ContactPersons contactPersons, final Product product, final LocalDate desiredExecutionDate, final Long sessionId,
            final ReservePort.TimePeriod timePeriod, final String connectionUnitLocation) {
        final WholesaleReservePortRequest result = new WholesaleReservePortRequest();
        result.setGeoId(geoId);
        result.setOrderId(orderId);
        result.setExtOrderId(extOrderId);
        result.setContactPersons(toWholesaleContactPersons(contactPersons));
        result.setProduct(toWholesaleProduct(product));
        result.setDesiredExecutionDate(desiredExecutionDate);
        result.setEkpFrameContract(toWholesaleEkpFrameContract((reseller == null) ? ekp : reseller));
        result.setSessionId(sessionId);
        if (timePeriod != null) {
            result.setZeitFensterAnfang(timePeriod.getBegin());
            result.setZeitfensterEnde(timePeriod.getEnd());
        }
        result.setLageTaeOnt(connectionUnitLocation);
        return result;
    }

    private static List<WholesaleContactPerson> toWholesaleContactPersons(ReservePort.ContactPersons contactPersons) {
        return Optional.ofNullable(contactPersons)
                .map(ReservePort.ContactPersons::getContactPerson)
                .map(List::stream)
                .map(contactPersonTypeStream -> contactPersonTypeStream.map(ReservePortRequestMapper::toWholesaleContactPerson)
                        .collect(toList()))
                .orElse(null);
    }

    private static WholesaleContactPerson toWholesaleContactPerson(ContactPersonType contactPersonType) {
        WholesaleContactPerson wholesaleContactPerson = new WholesaleContactPerson();
        wholesaleContactPerson.setSalutation(contactPersonType.getSalutation());
        wholesaleContactPerson.setFirstName(contactPersonType.getFirstName());
        wholesaleContactPerson.setLastName(contactPersonType.getLastName());
        wholesaleContactPerson.setPhoneNumber(contactPersonType.getPhoneNumber());
        wholesaleContactPerson.setFaxNumber(contactPersonType.getFaxNumber());
        wholesaleContactPerson.setEmailAddress(contactPersonType.getEmailAddress());
        wholesaleContactPerson.setMobilePhoneNumber(contactPersonType.getMobilePhoneNumber());
        wholesaleContactPerson.setRole(contactPersonType.getRole());
        return wholesaleContactPerson;
    }

    private static WholesaleEkpFrameContract toWholesaleEkpFrameContract(Ekp ekp) {
        final WholesaleEkpFrameContract ekpFrameContract = new WholesaleEkpFrameContract();
        ekpFrameContract.setEkpId(ekp.getId());
        ekpFrameContract.setEkpFrameContractId(ekp.getFrameContractId());
        return ekpFrameContract;
    }

    private static WholesaleProduct toWholesaleProduct(Product product) {
        final Set<WholesaleProductAttribute> productAttributes = wholesaleProductAttributes(product);
        final WholesaleProduct wholesaleProduct = new WholesaleProduct();
        wholesaleProduct.setName(
                WholesaleProductName.of(product.getProductName())
                        .orElseThrow(() -> new NoWholesaleProductException(String.format("unknown product %s", product.getProductName()))));
        wholesaleProduct.setAttributes(productAttributes);
        return wholesaleProduct;
    }

    private static Set<WholesaleProductAttribute> wholesaleProductAttributes(Product product) {
        return (product.getProductAttributes() == null) ? Sets.newHashSet() :
                product
                        .getProductAttributes()
                        .getServiceAttribute()
                        .stream()
                        .map(WholesaleProductAttribute::valueOf)
                        .collect(Collectors.toSet());
    }

    public static NotifyPortOrderUpdate toBauauftragChangedSuccessNotification(final String orderId) {
        final NotifyPortOrderUpdate bauauftragChangedResponse = notifyPortOrderUpdate(orderId, "bauauftragChangedResponse");
        bauauftragChangedResponse.setNotification(new NotificationType());
        return bauauftragChangedResponse;
    }

    public static NotifyPortOrderUpdate toBauauftragChangedErrorNotification(final String orderId, Pair<Long, String> reasonCode) {
        final NotifyPortOrderUpdate update = notifyPortOrderUpdate(orderId, "bauauftragChangedResponse");
        ErrorType errorType = errorType(reasonCode);
        update.setError(errorType);
        return update;
    }

    private static ErrorType errorType(Pair<Long, String> reasonCode) {
        ErrorType errorType = new ErrorType();
        errorType.setErrorCode(reasonCode.getFirst().toString());
        errorType.setErrorDescription(reasonCode.getSecond());
        return errorType;
    }

}
