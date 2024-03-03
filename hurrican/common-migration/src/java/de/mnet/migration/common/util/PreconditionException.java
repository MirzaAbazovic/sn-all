package de.mnet.migration.common.util;


/**
 * Throw this exception when a precondition in a method call does not hold.
 *
 * @see de.mnet.migration.common.util.Reject
 */
public class PreconditionException extends RuntimeException {
    private static final long serialVersionUID = -6204840303149650090L;

    public PreconditionException() {
        super();
    }

    public PreconditionException(String message) {
        super(message);
    }

    public PreconditionException(String message, Throwable cause) {
        super(message, cause);
    }

    public PreconditionException(Throwable cause) {
        super(cause);
    }
}
