package de.mnet.common.exceptions;

import org.springframework.util.StringUtils;

import de.mnet.common.errorhandling.ErrorCode;

/**
 * Base Service exception interface. All service exceptions should extend or implement this interface. Common services may use
 * this basic interface in method signature.
 *
 *
 */
public class ServiceException extends RuntimeException {
    /** Serial version uid*/
    private static final long serialVersionUID = -464287794073430635L;

    /** Special exception properties expressed to the Atlas ESB error service */
    private ErrorCode errorCode = ErrorCode.HUR_DEFAULT;
    private String processName;

    public ServiceException() {
        super();
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(Throwable cause) {
        super(cause);
    }

    /**
     * Sets the error code associated with this exception.
     * @param errorCode
     * @return
     */
    public ServiceException setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    /**
     * Sets the process name where this exception did occur from its service class name.
     * @param serviceClass
     * @return
     */
    public ServiceException setProcessName(Class<?> serviceClass) {
        this.processName = serviceClass.getSimpleName();
        return this;
    }

    /**
     * Gets the process name where the exception did occur.
     * @return
     */
    public String getProcessName() {
        if (StringUtils.hasText(processName)) {
            return processName;
        } else if (getStackTrace().length > 0) {
            return getStackTrace()[0].getClassName();
        } else {
            return "Unknown";
        }
    }

    /**
     * Gets this exceptions error code.
     * @return
     */
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
