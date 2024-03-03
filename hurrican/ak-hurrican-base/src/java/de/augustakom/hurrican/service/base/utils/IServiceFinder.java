/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.06.2004 08:32:25
 */
package de.augustakom.hurrican.service.base.utils;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.service.base.iface.IHurricanService;

/**
 * Interface definiert Methoden fuer eine Klasse, die nach Hurrican-Services suchen kann.
 *
 *
 */
public interface IServiceFinder {

    /**
     * Durchsucht alle registrierten ServiceLocator nach einem Service mit dem angegebenen Namen und Typ. <br> Die erste
     * Uebereinstimmung wird zurueck gegeben.
     *
     * @param serviceName Name des gesuchten Services.
     * @param serviceType Typ des gesuchten Services.
     * @return Service-Objekt.
     * @throws ServiceNotFoundException wenn der gesuchte Service auf keinem registrierten ServiceLocator gefunden
     *                                  wurde.
     */
    public <T extends IHurricanService> T findService(String serviceName, Class<T> serviceType)
            throws ServiceNotFoundException;

}

