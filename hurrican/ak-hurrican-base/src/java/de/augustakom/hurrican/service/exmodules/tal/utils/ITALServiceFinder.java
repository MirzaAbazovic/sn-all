/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.06.2007 13:14:15
 */
package de.augustakom.hurrican.service.exmodules.tal.utils;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.service.base.utils.IServiceFinder;
import de.augustakom.hurrican.service.exmodules.tal.ITALService;


/**
 * Interface fuer Service-Finder, die nach TAL-Services suchen.
 *
 *
 */
public interface ITALServiceFinder extends IServiceFinder {

    /**
     * Sucht nach einem TAL-Service mit dem angegebenen Namen und Typ.
     *
     * @param serviceName Name des gesuchten TAL-Services
     * @param serviceType Typ des gesuchten TAL-Services
     * @return Service-Objekt
     * @throws ServiceNotFoundException wenn der gesuchte Service nicht gefunden werden konnte.
     */
    public ITALService getTALService(String serviceName, Class serviceType)
            throws ServiceNotFoundException;

    /**
     * Sucht nach einem TAL-Service des angegebenen Typs. <br> Diese Methode geht davon aus, dass der Name des gesuchten
     * TAL-Services gleich dem Klassennamen ist.
     *
     * @param serviceType Typ des gesuchten Services (Klassenname ist gleichzeitig der gesuchte Service-Name).
     * @return Service-Objekt
     * @throws ServiceNotFoundException wenn der gesuchte Service nicht gefunden werden konnte.
     */
    public ITALService getTALService(Class serviceType) throws ServiceNotFoundException;

}


