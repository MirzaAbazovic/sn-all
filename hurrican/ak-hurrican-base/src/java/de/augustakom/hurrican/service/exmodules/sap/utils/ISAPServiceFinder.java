/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.08.2007 11:26:15
 */
package de.augustakom.hurrican.service.exmodules.sap.utils;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.service.base.utils.IServiceFinder;
import de.augustakom.hurrican.service.exmodules.sap.ISAPService;


/**
 * Interface fuer Service-Finder, die nach SAP-Services suchen.
 *
 *
 */
public interface ISAPServiceFinder extends IServiceFinder {

    /**
     * Sucht nach einem SAP-Service mit dem angegebenen Namen und Typ.
     *
     * @param serviceName Name des gesuchten SAP-Services
     * @param serviceType Typ des gesuchten SAP-Services
     * @return Service-Objekt
     * @throws ServiceNotFoundException wenn der gesuchte Service nicht gefunden werden konnte.
     */
    public ISAPService getSAPService(String serviceName, Class serviceType)
            throws ServiceNotFoundException;

    /**
     * Sucht nach einem SAP-Service des angegebenen Typs. <br> Diese Methode geht davon aus, dass der Name des gesuchten
     * SAP-Services gleich dem Klassennamen ist.
     *
     * @param serviceType Typ des gesuchten Services (Klassenname ist gleichzeitig der gesuchte Service-Name).
     * @return Service-Objekt
     * @throws ServiceNotFoundException wenn der gesuchte Service nicht gefunden werden konnte.
     */
    public ISAPService getSAPService(Class serviceType) throws ServiceNotFoundException;

}


