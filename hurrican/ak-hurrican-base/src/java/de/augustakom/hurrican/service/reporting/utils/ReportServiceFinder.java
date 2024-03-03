/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.03.2007 10:23:05
 */
package de.augustakom.hurrican.service.reporting.utils;

import org.apache.log4j.Logger;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.service.iface.IServiceLocatorNames;
import de.augustakom.hurrican.service.base.utils.HurricanServiceFinder;
import de.augustakom.hurrican.service.reporting.IReportService;


/**
 * ServiceFinder-Implementierung, um nach ReportServices zu suchen.
 *
 *
 */
public class ReportServiceFinder extends HurricanServiceFinder implements IReportServiceFinder {

    private static final Logger LOGGER = Logger.getLogger(ReportServiceFinder.class);

    private static ReportServiceFinder instance = null;

    /* Privater Konstruktor */
    protected ReportServiceFinder() {
    }

    /**
     * Gibt eine Singleton-Instanz der Klasse zurueck.
     *
     * @return Singleton-Instanz der Klasse.
     */
    public static ReportServiceFinder instance() {
        if (instance == null) {
            instance = new ReportServiceFinder();
        }
        return instance;
    }

    /**
     * @see de.augustakom.hurrican.service.internet.utils.IInternetServiceFinder#getInternetService(java.lang.String,
     * java.lang.Class)
     */
    public <T extends IReportService> T getReportService(String serviceName, Class<T> serviceType)
            throws ServiceNotFoundException {
        try {
            return getService(IServiceLocatorNames.HURRICAN_REPORT_SERVICE, serviceName, serviceType);
        }
        catch (ClassCastException e) {
            LOGGER.error(e.getMessage(), e);
            throw new ServiceNotFoundException("Der gesuchte Service ist nicht vom Typ " + IReportService.class.getName());
        }
    }

    /**
     * @see de.augustakom.hurrican.service.internet.utils.IInternetServiceFinder#getInternetService(java.lang.Class)
     */
    public <T extends IReportService> T getReportService(Class<T> serviceType) throws ServiceNotFoundException {
        try {
            return getService(IServiceLocatorNames.HURRICAN_REPORT_SERVICE, serviceType.getName(), serviceType);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ServiceNotFoundException("Der gesuchte Service ist nicht vom Typ " + IReportService.class.getName());
        }
    }
}


