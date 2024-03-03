/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.08.13
 */
package de.mnet.wbci.route;

/**
 * Interface zur Definition von div. Konstanten fuer die Apache Camel Routen.
 */
public interface WbciCamelConstants {

    /**
     * Route-ID fuer die WBCI-Route, ueber die eingehende WBCI-Nachrichten (CDM-Version v1) empfangen und verarbeitet
     * werden.
     */
    public static final String WBCI_CARRIER_NEGOTIATION_IN_V1_ROUTE_ID = "wbciCarrierNegotiationInV1Route";

    /**
     * Route-ID fuer die WBCI-Biz-Route, ueber die eingehende WBCI-Nachrichten (CDM-Version v1) empfangen und
     * verarbeitet werden.
     */
    public static final String WBCI_IN_BIZ_V1_ROUTE_ID = "wbciInBizV1Route";

    /**
     * Route-ID fuer die WBCI-Ems-Tx-Route, ueber die eingehende WBCI-Nachrichten (CDM-Version v1) empfangen und
     * verarbeitet werden.
     */
    public static final String WBCI_IN_EMS_TX_V1_ROUTE_ID = "wbciInEmsTxV1Route";

    /**
     * Route-ID fuer die WBCI-Route, ueber die Carrier Negotiation WBCI-Nachrichten nach aussen verschickt werden und
     * zusätzliche operationen wie IO archive Einträge getätigt werden.
     */
    public static final String WBCI_CARRIER_NEGOTIATION_OUT_ROUTE_ID = "wbciCarrierNegotiationOutRoute";

    /**
     * Route-ID fuer die WBCI-Route, ueber die Location Service WBCI-Nachrichten nach aussen verschickt werden und
     * zusätzliche operationen wie IO archive Einträge getätigt werden.
     */
    public static final String WBCI_LOCATION_OUT_ROUTE_ID = "wbciLocationServiceOutRoute";

    /**
     * Route-ID fuer die WBCI-Route, ueber die Carrier Negotiation WBCI-Nachrichten nach aussen verschickt werden ohne
     * zusätzliche Logik z.B. für IO archive Einträge.
     */
    public static final String WBCI_CARRIER_NEGOTIATION_SEND_MESSAGE_ROUTE_ID = "wbciCarrierNegotiationSendMessageRoute";
}
