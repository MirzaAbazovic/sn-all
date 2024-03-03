/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.02.14
 */
package de.mnet.wbci.exception;

import static org.apache.commons.lang.StringUtils.*;

import de.mnet.common.errorhandling.ErrorCode;

/**
 *
 */
public class WbciRequestNotFoundException extends WbciServiceException {
    private static final long serialVersionUID = -6345147332001196113L;

    private final String vorabstimmungsId;
    private final String stornoOrAenderungsId;

    /**
     * Default constructor using vorabstimmungsId and stornoOrAenderungsId.
     *
     * @param vorabstimmungsId
     * @param stornoOrAenderungsId
     */
    public WbciRequestNotFoundException(String vorabstimmungsId, String stornoOrAenderungsId) {
        super(String.format(
                "Could not find WbciRequest for VorabstimmungsId '%s'%s",
                vorabstimmungsId,
                (isEmpty(stornoOrAenderungsId)) ? "." : String.format(" and Storno-, TerminverschiebungsId '%s'.",
                        stornoOrAenderungsId)
        ));
        this.vorabstimmungsId = vorabstimmungsId;
        this.stornoOrAenderungsId = stornoOrAenderungsId;

        setErrorCode(ErrorCode.WBCI_UNKNOWN_VA);
    }

    public String getVorabstimmungsId() {
        return vorabstimmungsId;
    }

    public String getStornoOrAenderungsId() {
        return stornoOrAenderungsId;
    }
}
