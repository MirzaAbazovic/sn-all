package de.mnet.hurrican.atlas.simulator.wita;

/**
 * Abstract class defines default message header names.
 *
 *
 */
public final class WitaLineOrderMessageHeaders {

    /**
     * Constant header names automatically added to incoming request message
     */
    public static final String INTERFACE_NAMESPACE = "interfaceNamespace";
    public static final String INTERFACE_VERSION = "interfaceVersion";
    public static final String EXTERNAL_ORDER_ID = "externalOrderId";
    public static final String CONTRACT_ID = "contractId";
    public static final String CUSTOMER_ID = "customerId";
    public static final String THIRD_PARTY_SALESMAN_CUSTOMER_ID = "3rdPartySalesmanCustomerId";

    /**
     * Header name for external order id of an WITA order.
     */
    public static final String JMS_HEADER_NAME_EXTERNAL_ORDER = "externalOrderId";

    /**
     * Prevent instantiation.
     */
    private WitaLineOrderMessageHeaders() {
        super();
    }
}
