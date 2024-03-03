/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.02.2011 11:48:14
 */
package de.mnet.hurrican.startup;

/**
 * Klasse zur Definition von verschiedenen Konstanten von HurricanWeb.
 */
public class HurricanWebConstants {

    /**
     * Application-Name fuer die HurricanWeb Anwendung. (Verwendung fuer Identifizierung der Applikation gegenueber der
     * Authentication)
     */
    public static final String HURRICAN_WEB_APP_NAME = "hurrican.web";

    /**
     * Konstante fuer den Zugriff auf das SystemProperty das angibt, ob der Scheduler gestartet werden soll.
     */
    public static final String START_SCHEDULER = "start.scheduler";

    /**
     * Konstante fuer den Zugriff auf das SystemProperty das angibt, ob der ReportServer inkl. JMS gestartet werden
     * soll.
     */
    public static final String START_REPORTING = "start.reporting";

    /**
     * Konstante fuer den Zugriff auf das SystemProperty das angibt, ob die WITA-Komponenten gestartet werden sollen.
     */
    public static final String START_WITA = "start.wita";

    public static final String START_LOCATION_NOTIFICATION_CONSUMER = "start.locationconsumer";
    public static final String START_CUSTOMER_ORDER_SERVICE = "start.customerorderservice";

    public static final String START_RESOURCE_REPORTING_SERVICE = "start.resourcereporting";
}
