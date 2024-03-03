/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.10.2004 08:55:21
 */
package de.augustakom.hurrican.service.cc.utils;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.service.base.utils.IServiceFinder;
import de.augustakom.hurrican.service.cc.ICCService;


/**
 * Interface fuer Service-Finder, die nach CC-Services suchen.
 *
 *
 */
public interface ICCServiceFinder extends IServiceFinder {

    /**
     * Sucht nach einem CC-Service mit dem angegebenen Namen und Typ.
     *
     * @param serviceName Name des gesuchten CC-Services.
     * @param serviceType Typ des gesuchten CC-Service.
     * @return Service-Objekt
     * @throws ServiceNotFoundException wenn der gesuchte Service nicht gefunden werden konnte.
     */
    public <T extends ICCService> T getCCService(String serviceName, Class<T> serviceType)
            throws ServiceNotFoundException;

    /**
     * Sucht nach einem CC-Service des angegebenen Typs. <br> Diese Methode geht davon aus, dass der Name des gesuchten
     * CC-Services gleich dem Klassennamen ist.
     *
     * @param serviceType Typ des gesuchten CC-Service (Klassenname ist gleichzeitig der gesuchte Service-Name).
     * @return Service-Objekt
     * @throws ServiceNotFoundException wenn der gesuchte Service nicht gefunden werden konnte.
     */
    public <T extends ICCService> T getCCService(Class<T> type) throws ServiceNotFoundException;

}


