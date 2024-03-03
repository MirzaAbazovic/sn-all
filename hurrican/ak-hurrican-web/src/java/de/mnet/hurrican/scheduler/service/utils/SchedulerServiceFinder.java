/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.10.2004 09:02:05
 */
package de.mnet.hurrican.scheduler.service.utils;

import org.apache.log4j.Logger;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.service.iface.IServiceLocator;
import de.augustakom.common.service.iface.IServiceLocatorNames;
import de.augustakom.common.service.locator.ServiceLocatorRegistry;


/**
 * ServiceFinder-Implementierung, um nach Billing-Services zu suchen.
 *
 *
 */
public class SchedulerServiceFinder {

    private static final Logger LOGGER = Logger.getLogger(SchedulerServiceFinder.class);

    private static SchedulerServiceFinder instance;

    /**
     * Instantiierung nicht erlaubt
     */
    protected SchedulerServiceFinder() {
        // not used
    }

    /**
     * Gibt eine Singleton-Instanz der Klasse zurueck.
     *
     * @return Singleton-Instanz der Klasse.
     */
    public static SchedulerServiceFinder instance() {
        if (instance == null) {
            instance = new SchedulerServiceFinder();
        }
        return instance;
    }


    /**
     * Sucht nach einem Scheduler-Service des angegebenen Typs. <br> Diese Methode geht davon aus, dass der Name des
     * gesuchten Billing-Services gleich dem Klassennamen ist.
     *
     * @param serviceType Typ des gesuchten Services (Klassenname ist gleichzeitig der gesuchte Service-Name).
     * @return Service-Objekt
     * @throws ServiceNotFoundException wenn der gesuchte Service nicht gefunden werden konnte.
     */
    public <T extends SchedulerService> T getSchedulerService(String serviceName, Class<T> serviceType)
            throws ServiceNotFoundException {
        try {
            return getService(IServiceLocatorNames.HURRICAN_SCHEDULER_SERVICE,
                    serviceName, serviceType);
        }
        catch (ClassCastException e) {
            LOGGER.error(e.getMessage(), e);
            throw new ServiceNotFoundException("Der gesuchte Service ist nicht vom Typ " + SchedulerService.class.getName());
        }
    }

    /**
     * Sucht nach einem Scheduler-Service des angegebenen Typs. <br> Diese Methode geht davon aus, dass der Name des
     * gesuchten Billing-Services gleich dem Klassennamen ist.
     *
     * @param serviceType Typ des gesuchten Services (Klassenname ist gleichzeitig der gesuchte Service-Name).
     * @return Service-Objekt
     * @throws ServiceNotFoundException wenn der gesuchte Service nicht gefunden werden konnte.
     */
    public <T extends SchedulerService> T getSchedulerService(Class<T> serviceType) throws ServiceNotFoundException {
        try {
            return getService(IServiceLocatorNames.HURRICAN_SCHEDULER_SERVICE, serviceType.getName(), serviceType);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ServiceNotFoundException("Der gesuchte Service ist nicht vom Typ " + SchedulerService.class.getName());
        }
    }

    /**
     * Sucht auf dem angegebenen ServiceLocator nach einem Service mit Namen <code>serviceName</code>. Der Typ muss mit
     * <code>serviceType</code> uebereinstimmen.
     *
     * @param locatorName Name des ServiceLocators, auf dem der Service gesucht werden soll
     * @param serviceName Name des gesuchten Services
     * @param serviceType Typ des gesuchten Services
     * @return Service-Objekt
     * @throws ServiceNotFoundException wenn der gesuchte Service nicht gefunden wurde.
     */
    protected <T extends SchedulerService> T getService(String locatorName, String serviceName, Class<T> serviceType)
            throws ServiceNotFoundException {
        IServiceLocator locator = ServiceLocatorRegistry.instance().getServiceLocator(locatorName);
        if (locator != null) {
            return locator.getService(serviceName, serviceType, null);
        }

        throw new ServiceNotFoundException("Required ServiceLocator not found in ServiceLocatorRegistry!");
    }

}


