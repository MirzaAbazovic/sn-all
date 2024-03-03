/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.03.2007 10:21:43
 */
package de.augustakom.hurrican.service.reporting.utils;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.service.base.utils.IServiceFinder;
import de.augustakom.hurrican.service.reporting.IReportService;


/**
 * Interface fuer Service-Finder, die nach Report-Services suchen.
 *
 *
 */
public interface IReportServiceFinder extends IServiceFinder {

    /**
     * Sucht nach einem Report-Service mit dem angegebenen Namen und Typ.
     *
     * @param serviceName Name des gesuchten Report-Service
     * @param serviceType Typ des gesuchten Report-Service
     * @return Service-Objekt
     * @throws ServiceNotFoundException wenn der gesuchte Service nicht gefunden werden konnte.
     */
    public <T extends IReportService> T getReportService(String serviceName, Class<T> serviceType)
            throws ServiceNotFoundException;

    /**
     * Sucht nach einem Report-Service des angegebenen Typs. <br> Diese Methode geht davon aus, dass der Name des
     * gesuchten Report-Services gleich dem Klassennamen ist.
     *
     * @param serviceType Typ des gesuchten Services (Klassenname ist gleichzeitig der gesuchte Service-Name).
     * @return Service-Objekt
     * @throws ServiceNotFoundException wenn der gesuchte Service nicht gefunden werden konnte.
     */
    public <T extends IReportService> T getReportService(Class<T> serviceType) throws ServiceNotFoundException;

}


