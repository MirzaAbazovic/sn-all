/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File Created: 07.10.2004 16:13:24
 */
package de.augustakom.hurrican.service.internet;

import de.augustakom.common.service.iface.IServiceLocatorNames;
import de.augustakom.common.service.locator.SpringStandaloneServiceLocator;


/**
 * ServiceLocator fuer die Internet-Services. <br> Der ServiceLocator basiert auf dem Spring-Framework.
 *
 *
 */
public class InternetServiceLocator extends SpringStandaloneServiceLocator {

    /**
     * @see de.augustakom.common.service.locator.SpringStandaloneServiceLocator#getXMLConfiguration()
     */
    public String getXMLConfiguration() {
        return "de/augustakom/hurrican/service/internet/resources/InternetServices.xml";
    }

    /**
     * @see de.augustakom.common.service.iface.IServiceLocator#getServiceLocatorName()
     */
    public String getServiceLocatorName() {
        return IServiceLocatorNames.HURRICAN_INTERNET_SERVICE;
    }

}
