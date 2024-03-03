/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.12.13
 */
package de.mnet.wbci.exception;

import de.mnet.wbci.model.WbciRequestStatus;

/**
 * This exception is thrown when an attempt is made to update a request status to a invalid state (e.g. when a message
 * is received out of sequence - ABBM-TR before RUEM_VA is received)
 */
public class InvalidRequestStatusChangeException extends WbciServiceException {
    private static final long serialVersionUID = -7386762110841895708L;

    private final Long wbciRequestId;
    private final WbciRequestStatus currentStatus;
    private final WbciRequestStatus newStatus;

    public InvalidRequestStatusChangeException(Long wbciRequestId, WbciRequestStatus currentStatus, WbciRequestStatus newStatus) {
        super(String.format("Update of WBCIRequest (ID='%s') Status failed. Cannot update from '%s' to '%s'", wbciRequestId, currentStatus, newStatus));
        this.wbciRequestId = wbciRequestId;
        this.currentStatus = currentStatus;
        this.newStatus = newStatus;
    }

    public InvalidRequestStatusChangeException(Long wbciRequestId, WbciRequestStatus currentStatus, WbciRequestStatus newStatus, Exception nestedException) {
        super(String.format("Update of WBCIRequest (ID='%s') Status failed. Cannot update from '%s' to '%s'", wbciRequestId, currentStatus, newStatus), nestedException);
        this.wbciRequestId = wbciRequestId;
        this.currentStatus = currentStatus;
        this.newStatus = newStatus;
    }

    public Long getWbciRequestId() {
        return wbciRequestId;
    }

    public WbciRequestStatus getCurrentStatus() {
        return currentStatus;
    }

    public WbciRequestStatus getNewStatus() {
        return newStatus;
    }
}
