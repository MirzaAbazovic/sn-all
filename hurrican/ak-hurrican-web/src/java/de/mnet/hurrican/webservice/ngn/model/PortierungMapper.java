/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.03.2016
 */
package de.mnet.hurrican.webservice.ngn.model;

import java.util.*;
import java.util.stream.*;

import de.mnet.hurrican.ngn.portierungservice.BillingOrderNumberWeb;
import de.mnet.hurrican.ngn.portierungservice.PhoneNumber;
import de.mnet.hurrican.ngn.portierungservice.PhoneNumberMigrationStatus;
import de.mnet.hurrican.ngn.portierungservice.PhoneNumbersMigrationResponse;
import de.mnet.hurrican.ngn.portierungservice.PortierungRequestWeb;
import de.mnet.hurrican.ngn.portierungservice.PortierungResponseWeb;
import de.mnet.hurrican.ngn.portierungservice.PortierungStatusEntryWeb;
import de.mnet.hurrican.ngn.portierungservice.PortierungStatusEnumWeb;
import de.mnet.hurrican.ngn.portierungservice.PortierungStatusWeb;
import de.mnet.hurrican.ngn.portierungservice.PortierungWarningWeb;
import de.mnet.hurrican.ngn.portierungservice.ValidationRequestWeb;
import de.mnet.hurrican.ngn.portierungservice.ValidationResponseWeb;
import de.mnet.hurrican.ngn.portierungservice.ValidationStatusEnumWeb;
import de.mnet.hurrican.ngn.portierungservice.ValidationStatusWeb;

/**
 * Mapping zwischen internen und externen Portierungsdaten
 */
public class PortierungMapper {
    public static PortierungRequest toInternalPortierungRequest(PortierungRequestWeb portierungRequest) {
        List<Long> orderNumbersOrign = portierungRequest.getBillingOrderNumbers().stream()
                .map(BillingOrderNumberWeb::getBillingOrderNumber)
                .collect(Collectors.toList());

        return new PortierungRequest(orderNumbersOrign);
    }

    /**
     * Umwandlung der internen Portierung-Response in die externe Form
     */
    public static PortierungResponseWeb toExternalPortierungResponse(PortierungResponse response) {
        PortierungResponseWeb portierungResponse = new PortierungResponseWeb();
        List<PortierungStatusEntryWeb> statusEntryList = portierungResponse.getPortierungStatusEntries();
        PortierungResult portierungResult = response.getPortierungResult();
        Set<Map.Entry<Long, PortierungStatus>> entries = portierungResult.getPortierungResults().entrySet();
        for (Map.Entry<Long, PortierungStatus> entry : entries) {
            PortierungStatus portierungStatus = entry.getValue();

            PortierungStatusEntryWeb portierungStatusEntryWeb = new PortierungStatusEntryWeb();

            BillingOrderNumberWeb billingOrderNumberWeb = new BillingOrderNumberWeb();
            billingOrderNumberWeb.setBillingOrderNumber(entry.getKey());
            portierungStatusEntryWeb.setBillingOrderNumber(billingOrderNumberWeb);

            PortierungStatusWeb statusWeb = portierungStatusWeb(portierungStatus);
            portierungStatusEntryWeb.setPortierungStatus(statusWeb);
            statusEntryList.add(portierungStatusEntryWeb);
        }
        return portierungResponse;
    }

    private static PortierungStatusWeb portierungStatusWeb(PortierungStatus portierungStatus) {
        PortierungStatusWeb statusWeb = new PortierungStatusWeb();
        statusWeb.setMessage(portierungStatus.getMessage());
        statusWeb.setPortierungStatusEnum(PortierungStatusEnumWeb.fromValue(portierungStatus.getPortierungStatusEnum().value()));
        addWarnings2Status(statusWeb, portierungStatus);
        return statusWeb;
    }

    public static PhoneNumbersMigrationResponse toExternalResponse(PortierungResult res) {
        final PhoneNumbersMigrationResponse response = new PhoneNumbersMigrationResponse();
        for (Map.Entry<Long, PortierungStatus> e : res.getPortierungResults().entrySet()) {
            final PhoneNumber phoneNumber = new PhoneNumber();
            phoneNumber.setDnNo(e.getKey());
            final PhoneNumberMigrationStatus migrationStatus = new PhoneNumberMigrationStatus();
            migrationStatus.setPhoneNumber(phoneNumber);
            migrationStatus.setPortierungStatus(portierungStatusWeb(e.getValue()));
            response.getPhoneNumberMigrationStatus().add(migrationStatus);
        }
        return response;
    }

    private static void addWarnings2Status(PortierungStatusWeb statusWeb, PortierungStatus portierungStatus) {
        Optional<PortierungWarnings> warningsOptional = portierungStatus.getPortierungWarnings();
        if (warningsOptional.isPresent()) {
            for (PortierungWarning portierungWarning : warningsOptional.get().getWarnings()) {
                PortierungWarningWeb warningWeb = new PortierungWarningWeb();
                warningWeb.setMessage(portierungWarning.getMessage());
                statusWeb.getHints().add(warningWeb);
            }
        }
    }

    /**
     * Umwandlung des externen validation requests in die interne Form
     */
    public static ValidationRequest toInternalValidationRequest(ValidationRequestWeb validationRequest) {
        return new ValidationRequest(validationRequest.getBillingOrderNumber().getBillingOrderNumber());
    }

    /**
     * Umwandlung der ValidationResponse in die externe Form
     */
    public static ValidationResponseWeb toExternalValidationResponse(ValidationResponse validationResponse) {
        ValidationResponseWeb response = new ValidationResponseWeb();
        BillingOrderNumberWeb billingOrderNumber = new BillingOrderNumberWeb();
        billingOrderNumber.setBillingOrderNumber(validationResponse.getOrderNoOrig());
        response.setBillingOrderNumber(billingOrderNumber);
        ValidationStatus validationStatus = validationResponse.getStatus();
        response.setValidationStatus(toExternalValidationStatus(validationStatus));
        return response;
    }

    private static ValidationStatusWeb toExternalValidationStatus(ValidationStatus internalValidationStatus) {
        ValidationStatusWeb validationStatus = new ValidationStatusWeb();
        ValidationStatusEnum validationStatusEnum = internalValidationStatus.getStatusEnum();
        validationStatus.setValidationStatusEnum(ValidationStatusEnumWeb.fromValue(validationStatusEnum.name()));
        validationStatus.setMessage(internalValidationStatus.getMsg());
        return validationStatus;
    }
}

