/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.10.2014
 */
package de.mnet.wita.route;

/**
 *
 */
public interface WitaCamelConstants {

    /**
     * Route-ID fuer die WITA-Route, ueber die Line Order WITA-Nachrichten zu DTAGn ach aussen verschickt werden und
     * zusätzliche operationen wie IO archive Einträge getätigt werden.
     */
    String WITA_LINE_ORDER_OUT_ROUTE = "witaLineOrderOutRoute";

    /**
     * Route-ID fuer die WITA-Route, ueber die Line Order WITA-Nachrichten von DTAG empfangen werden.
     */
    String WITA_LINE_ORDER_IN_V1_ROUTE_ID = "witaLineOrderInV1Route";

    /**
     * Route-ID fuer die WITA-Biz-Route, ueber die eingehende Line Order WITA-Nachrichten (CDM-Version v1)
     * empfangen und verarbeitet werden.
     */
    String WITA_LINE_ORDER_IN_BIZ_V1_ROUTE_ID = "witaLineOrderInBizV1Route";

    /**
     * Route-ID fuer die WITA-Ems-Tx-Route, ueber die eingehende WBCI-Nachrichten (CDM-Version v1) empfangen und
     * verarbeitet werden.
     */
    String WITA_LINE_ORDER_IN_EMS_TX_V1_ROUTE_ID = "witaLineOrderInEmsTxV1Route";


    /**
     * Route-ID fuer die WITA-Biz-Route, ueber die eingehende Line Order WITA-Nachrichten (CDM-Version v2) empfangen und
     * verarbeitet werden.
     */
    String WITA_LINE_ORDER_IN_BIZ_V2_ROUTE_ID = "witaLineOrderInBizV2Route";

    /**
     * Route-ID fuer die WITA-Ems-Tx-Route, ueber die eingehende WBCI-Nachrichten (CDM-Version v2) empfangen und
     * verarbeitet werden.
     */
    String WITA_LINE_ORDER_IN_EMS_TX_V2_ROUTE_ID = "witaLineOrderInEmsTxV2Route";

    /**
     * Route-ID fuer die WITA-Route, ueber die Line Order WITA-Nachrichten von DTAG empfangen werden.
     */
    String WITA_LINE_ORDER_IN_V2_ROUTE_ID = "witaLineOrderInV2Route";

}
