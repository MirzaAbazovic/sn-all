/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.03.2016
 */
package de.mnet.hurrican.webservice.ngn.service;

import java.util.*;

import de.mnet.hurrican.webservice.ngn.model.PortierungRequest;
import de.mnet.hurrican.webservice.ngn.model.PortierungResponse;
import de.mnet.hurrican.webservice.ngn.model.PortierungResult;
import de.mnet.hurrican.webservice.ngn.model.ValidationRequest;
import de.mnet.hurrican.webservice.ngn.model.ValidationResponse;

/**
 * Die Migration der Portierungskennenung fuer NGN
 */
public interface PortierungService {

    boolean isFeatureFlagActive();

    PortierungResponse migratePortierungskennung(PortierungRequest portierungRequest)
            throws PortierungskennungMigrationException;

    ValidationResponse validatePortierungskennung(ValidationRequest validationRequest)
            throws PortierungskennungMigrationException;

    PortierungResult performPhoneNumbersMigration(List<Long> dnNumbers);
}
