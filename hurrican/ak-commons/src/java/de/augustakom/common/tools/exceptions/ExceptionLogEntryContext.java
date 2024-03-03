/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.11.2011 13:23:24
 */
package de.augustakom.common.tools.exceptions;

public enum ExceptionLogEntryContext {
    HURRICAN_SERVER("Hurrican.Server.HurricanExceptionLogErrorHandler"),
    HURRICAN_SERVER_IO_ARCHIVE("Hurrican.Server.IoArchiveJmsListener"),
    WBCI_LOCATION_SEARCH_RESPONSE_ERROR("WBCI.LocationSearch.ResponseError"),
    WBCI_LOCATION_SEARCH_REQUEST_ERROR("WBCI.LocationSearch.RequestError"),
    WBCI_SCHEDULER_SEARCH_REQUEST_ERROR("WBCI.SendRequestsScheduler.RequestError"),
    WITA_SCHEDULER_SEARCH_REQUEST_ERROR("WITA.SendRequestsScheduler.RequestError"),
    ATLAS_ERROR_SERVICE_ERROR("Atlas.ErrorService.RequestError");

    public String identifier;

    private ExceptionLogEntryContext(String identifier) {
        this.identifier = identifier;
    }
}
