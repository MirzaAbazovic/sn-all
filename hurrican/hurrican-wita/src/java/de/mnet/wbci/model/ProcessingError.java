/* 
 * Copyright (c) 2014 - M-net Telekommunikations GmbH 
 * All rights reserved. 
 * ------------------------------------------------------- 
 * File created: 20.02.14 
 */
package de.mnet.wbci.model;

/**
 * Processing error container holding error classification and message. By default
 * error is of technical nature which means that error and
 */
public class ProcessingError {
    private final MeldungsCode code;
    private final String errorMessage;
    private final boolean technical;

    /**
     * Default constructor using meldungs code, error message and technical fields.
     * @param code
     * @param errorMessage
     * @param technical
     */
    public ProcessingError(MeldungsCode code, String errorMessage, boolean technical) {
        this.code = code;
        this.errorMessage = errorMessage;
        this.technical = technical;
    }

    /**
     * Default constructor using meldungs code and error message fields.
     * @param code
     * @param errorMessage
     */
    public ProcessingError(MeldungsCode code, String errorMessage) {
        this(code, errorMessage, true);
    }

    public MeldungsCode getMeldungsCode() {
        return code;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isTechnical() {
        return technical;
    }
}
