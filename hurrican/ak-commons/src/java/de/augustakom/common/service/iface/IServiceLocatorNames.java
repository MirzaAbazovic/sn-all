/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.05.2004 15:35:10
 */
package de.augustakom.common.service.iface;


/**
 * Interface zur Definition der ServiceLocator-Namen. <br> Damit die Clients die Namen der einzelnen ServiceLocator
 * nicht selbst halten muessen, sind die Namen aller verfuegbaren ServiceLocator in diesem Interface definiert.
 *
 *
 */
public interface IServiceLocatorNames {

    /**
     * Name des AuthenticationService-Locators.
     */
    public static final String AUTHENTICATION_SERVICE = "ak.authentication.service";

    public static final String AUTHENTICATION_TREE_SERVICE = "ak.authentication.tree.service";

    /**
     * Name des CC Service-Locators der Hurrican-Applikation.
     */
    public static final String HURRICAN_CC_SERVICE = "ak.hurrican.cc.service";

    /**
     * Name des Billing Service-Locators der Hurrican-Applikation.
     */
    public static final String HURRICAN_BILLING_SERVICE = "ak.hurrican.billing.service";

    /**
     * Name des Internet Services-Locators der Hurrican-Applikation.
     */
    public static final String HURRICAN_INTERNET_SERVICE = "ak.hurrican.internet.service";

    /**
     * Name des Report Service-Locators der Hurrican-Applikation.
     */
    public static final String HURRICAN_REPORT_SERVICE = "ak.hurrican.report.service";

    /**
     * Name des Scheduler Service-Locators.
     */
    public static final String HURRICAN_SCHEDULER_SERVICE = "ak.scheduler.service";

    /**
     * Name des Production Service-Locators der Hurrican-Applikation.
     */
    public static final String HURRICAN_PRODUCTION_SERVICE = "ak.hurrican.production.service";

    /**
     * Name des TAL Service-Locators der Hurrican-Applikation.
     */
    public static final String HURRICAN_TAL_SERVICE = "ak.hurrican.tal.service";

    /**
     * Name des SAP Service-Locators der Hurrican-Applikation.
     */
    public static final String HURRICAN_SAP_SERVICE = "ak.hurrican.sap.service";

    /**
     * Service Locator fuer die Report-Server-Implementierung.
     */
    public static final String HURRICAN_REPORT_SERVER_SERVICE = "ak.hurrican.report.server.service";

    /**
     * Service Locator fuer alle Hurrican-Services.
     */
    public static final String HURRICAN_UNIFIED_SERVICE = "ak.hurrican.report.unified.service";

}
