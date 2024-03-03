/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.10.13
 */
package de.mnet.wbci.exception;

import de.mnet.common.errorhandling.ErrorCode;

/**
 *
 */
public class DuplicateVaIdException extends WbciServiceException {
    private static final long serialVersionUID = -7386762110841895708L;

    private final String vorabstimmungsId;

    public DuplicateVaIdException(String vorabstimmungsId) {
        super("Duplicate VorabstimmungsId: " + vorabstimmungsId);
        setErrorCode(ErrorCode.WBCI_DUPLICATE_VA_ID);
        this.vorabstimmungsId = vorabstimmungsId;
    }

    public String getVorabstimmungsId() {
        return vorabstimmungsId;
    }
}
