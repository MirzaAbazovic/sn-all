/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.02.2016
 */
package de.mnet.hurrican.webservice.ngn.endpoint;

import java.util.*;
import java.util.stream.*;
import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.ws.server.endpoint.annotation.Endpoint;

import de.mnet.hurrican.ngn.portierungservice.FaultDetails;
import de.mnet.hurrican.ngn.portierungservice.PhoneNumber;
import de.mnet.hurrican.ngn.portierungservice.PhoneNumberMigrationStatus;
import de.mnet.hurrican.ngn.portierungservice.PhoneNumbersMigrationRequest;
import de.mnet.hurrican.ngn.portierungservice.PhoneNumbersMigrationResponse;
import de.mnet.hurrican.ngn.portierungservice.PortierungFault;
import de.mnet.hurrican.ngn.portierungservice.PortierungRequestWeb;
import de.mnet.hurrican.ngn.portierungservice.PortierungResponseWeb;
import de.mnet.hurrican.ngn.portierungservice.ValidationRequestWeb;
import de.mnet.hurrican.ngn.portierungservice.ValidationResponseWeb;
import de.mnet.hurrican.webservice.ngn.model.PortierungMapper;
import de.mnet.hurrican.webservice.ngn.model.PortierungRequest;
import de.mnet.hurrican.webservice.ngn.model.PortierungResponse;
import de.mnet.hurrican.webservice.ngn.model.PortierungResult;
import de.mnet.hurrican.webservice.ngn.model.ValidationRequest;
import de.mnet.hurrican.webservice.ngn.model.ValidationResponse;
import de.mnet.hurrican.webservice.ngn.service.PortierungService;
import de.mnet.hurrican.webservice.ngn.service.PortierungskennungMigrationException;

/**
 * Implementation of webservice endpoint {@link de.mnet.hurrican.ngn.portierungservice.PortierungServiceWeb}
 */
@Endpoint("portierungServiceSoapWebEndpoint")
public class PortierungWebEndpoint implements de.mnet.hurrican.ngn.portierungservice.PortierungServiceWeb {

    public enum FaultMessage {
        SERVICE_OFFLINE("-1", "NGN Portierungsservice wurde abgeschaltet");

        private String errorCode;
        private String errorMessage;

        FaultMessage(String errorCode, String errorMessage) {
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
        }

        public static void throwFaultException(FaultMessage faultMessagesEnum) throws PortierungFault {
            final FaultDetails fault = new FaultDetails();
            fault.setErrorCode(faultMessagesEnum.errorCode);
            fault.setErrorMessage(faultMessagesEnum.errorMessage);
            throw new PortierungFault(faultMessagesEnum.errorMessage, fault);
        }
    }

    private PortierungService portierungService;

    public PortierungWebEndpoint(PortierungService portierungService) {
        this.portierungService = portierungService;
    }

    @Override
    public ValidationResponseWeb validatePortierungskennung(ValidationRequestWeb validationRequest) throws PortierungFault {
        checkIfServiceActive();

        ValidationRequest internalValidationRequest = PortierungMapper.toInternalValidationRequest(validationRequest);
        try {
            ValidationResponse validationResponse = portierungService.validatePortierungskennung(internalValidationRequest);
            return PortierungMapper.toExternalValidationResponse(validationResponse);
        }
        catch (PortierungskennungMigrationException ex) {
            throw new PortierungFault(ex.getMessage(), ex);
        }

    }

    @Override
    public PortierungResponseWeb migratePortierungskennung(PortierungRequestWeb portierungRequest) throws PortierungFault {
        checkIfServiceActive();

        PortierungRequest internalPortierungRequest = PortierungMapper.toInternalPortierungRequest(portierungRequest);
        try {
            PortierungResponse response = portierungService.migratePortierungskennung(internalPortierungRequest);
            return PortierungMapper.toExternalPortierungResponse(response);
        }
        catch (PortierungskennungMigrationException ex) {
            throw new PortierungFault(ex.getMessage(), ex);
        }
    }

    @Override
    public PhoneNumbersMigrationResponse migratePhoneNumbers(PhoneNumbersMigrationRequest migratePhoneNumbers) throws PortierungFault {
        if (migratePhoneNumbers != null && CollectionUtils.isNotEmpty(migratePhoneNumbers.getNewPhoneNumbers())) {

            final List<Long> dnNumbers = migratePhoneNumbers.getNewPhoneNumbers()
                    .stream().map(p -> p.getDnNo())
                    .distinct()
                    .collect(Collectors.toList());
            final PortierungResult portierungResult = portierungService.performPhoneNumbersMigration(dnNumbers);
            return PortierungMapper.toExternalResponse(portierungResult);
        } else {
            // nothing to do
            return new PhoneNumbersMigrationResponse();
        }
    }

    private void checkIfServiceActive() throws PortierungFault {
        if (!isFeatureFlagActive()) {
            FaultMessage.throwFaultException(FaultMessage.SERVICE_OFFLINE);
        }
    }

    @VisibleForTesting
    protected boolean isFeatureFlagActive() {
        return portierungService.isFeatureFlagActive();
    }


}
