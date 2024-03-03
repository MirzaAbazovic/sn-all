package de.mnet.wita.exceptions;

import de.mnet.common.errorhandling.ErrorCode;

public class WitaConfigException extends WitaBaseException {

    private static final long serialVersionUID = 8654167130185284447L;

    public WitaConfigException() {
        setErrorCode(ErrorCode.HUR_DEFAULT);
    }

    public WitaConfigException(String message, Throwable cause) {
        super(message, cause);
        setErrorCode(ErrorCode.HUR_DEFAULT);
    }

    public WitaConfigException(String message) {
        super(message);
        setErrorCode(ErrorCode.HUR_DEFAULT);
    }

    public WitaConfigException(Throwable cause) {
        super(cause);
        setErrorCode(ErrorCode.HUR_DEFAULT);
    }

}
