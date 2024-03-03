package de.mnet.hurrican.atlas.simulator;

/**
 *
 */
public enum SimulatorVariableNames {

    ESB_TRACKING_ID("esbTrackingId"),
    INTERFACE_VERSION("interfaceVersion"),
    ORDER_ID("orderId"),
    CORRELATION_ID("correlationId");

    private String name;

    /**
     * Constructor using name field.
     */
    private SimulatorVariableNames(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
