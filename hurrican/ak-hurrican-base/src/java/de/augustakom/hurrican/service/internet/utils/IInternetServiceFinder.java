/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.10.2004 16:45:43
 */
package de.augustakom.hurrican.service.internet.utils;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.service.base.utils.IServiceFinder;
import de.augustakom.hurrican.service.internet.IInternetService;


/**
 * Interface fuer Service-Finder, die nach Internet-Services suchen.
 *
 *
 */
public interface IInternetServiceFinder extends IServiceFinder {

    /**
     * Sucht nach einem Internet-Service mit dem angegebenen Namen und Typ.
     *
     * @param serviceName Name des gesuchten Internet-Service
     * @param serviceType Typ des gesuchten Internet-Service
     * @return Service-Objekt
     * @throws ServiceNotFoundException wenn der gesuchte Service nicht gefunden werden konnte.
     */
    public <T extends IInternetService> T getInternetService(String serviceName, Class<T> serviceType)
            throws ServiceNotFoundException;

    /**
     * Sucht nach einem Internet-Service des angegebenen Typs. <br> Diese Methode geht davon aus, dass der Name des
     * gesuchten Billing-Services gleich dem Klassennamen ist.
     *
     * @param serviceType Typ des gesuchten Services (Klassenname ist gleichzeitig der gesuchte Service-Name).
     * @return Service-Objekt
     * @throws ServiceNotFoundException wenn der gesuchte Service nicht gefunden werden konnte.
     */
    public <T extends IInternetService> T getInternetService(Class<T> serviceType) throws ServiceNotFoundException;

}


