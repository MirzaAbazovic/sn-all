/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.2009 14:20:33
 */
package de.mnet.migration.common.exception;

import de.mnet.migration.common.result.TransformationResult;


/**
 * Runtime Exception, die vom Transformator geworfen werden kann, um ein TransformationResult an den Controller zu
 * kommunizieren.
 *
 *
 */
public class TransformationException extends RuntimeException {

    private final TransformationResult result;

    public TransformationException(TransformationResult result) {
        super();
        this.result = result;
    }

    public TransformationResult getResult() {
        return result;
    }

}
