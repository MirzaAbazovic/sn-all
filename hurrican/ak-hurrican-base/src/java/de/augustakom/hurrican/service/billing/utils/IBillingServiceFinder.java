/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.10.2004 09:00:43
 */
package de.augustakom.hurrican.service.billing.utils;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.service.base.utils.IServiceFinder;
import de.augustakom.hurrican.service.billing.IBillingService;


/**
 * Interface fuer Service-Finder, die nach Billing-Services suchen.
 *
 *
 */
public interface IBillingServiceFinder extends IServiceFinder {

    /**
     * Sucht nach einem Billing-Service mit dem angegebenen Namen und Typ.
     *
     * @param serviceName Name des gesuchten Billing-Services
     * @param serviceType Typ des gesuchten Billing-Services
     * @return Service-Objekt
     * @throws ServiceNotFoundException wenn der gesuchte Service nicht gefunden werden konnte.
     */
    public <T extends IBillingService> T getBillingService(String serviceName, Class<T> serviceType)
            throws ServiceNotFoundException;

    /**
     * Sucht nach einem Billing-Service des angegebenen Typs. <br> Diese Methode geht davon aus, dass der Name des
     * gesuchten Billing-Services gleich dem Klassennamen ist.
     *
     * @param serviceType Typ des gesuchten Services (Klassenname ist gleichzeitig der gesuchte Service-Name).
     * @return Service-Objekt
     * @throws ServiceNotFoundException wenn der gesuchte Service nicht gefunden werden konnte.
     */
    public <T extends IBillingService> T getBillingService(Class<T> serviceType) throws ServiceNotFoundException;

}


