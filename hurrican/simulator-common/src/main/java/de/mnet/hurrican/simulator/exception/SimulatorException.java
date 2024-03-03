package de.mnet.hurrican.simulator.exception;

/**
 *
 */
public class SimulatorException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor using fields.
     *
     * @param message
     */
    public SimulatorException(String message) {
        super(message);
    }

    /**
     * Constructor using fields.
     *
     * @param cause
     */
    public SimulatorException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor using fields.
     *
     * @param message
     * @param cause
     */
    public SimulatorException(String message, Throwable cause) {
        super(message, cause);
    }
}
