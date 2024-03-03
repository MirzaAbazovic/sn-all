/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.10.2014
 */
package de.mnet.wita.service;

import de.mnet.common.service.SchemaValidationService;
import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.WitaMessage;
import de.mnet.wita.exceptions.WitaBaseException;

/**
 * Validation service for validation the lineorder and lineordernotification messages. Used both for incoming and
 * outgoing messages.
 */
public interface WitaSchemaValidationService extends SchemaValidationService, WitaService {

    /**
     * Used for verifying that outgoing WITA Messages are valid according to the CDM schema specification.
     *
     * @param witaMessage any type of {@link WitaMessage}
     * @param witaCdmVersion specifies the {@link WitaCdmVersion} for the validation
     * @throws WitaBaseException if the witaMessage is not valid
     */
    void validateWitaMessage(WitaMessage witaMessage, WitaCdmVersion witaCdmVersion) throws WitaBaseException;
}
