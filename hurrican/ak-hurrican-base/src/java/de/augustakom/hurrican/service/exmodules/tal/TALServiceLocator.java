/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File Created: 03.06.2004 12:16:24
 */
package de.augustakom.hurrican.service.exmodules.tal;

import de.augustakom.common.service.iface.IServiceLocatorNames;
import de.augustakom.common.service.locator.SpringStandaloneServiceLocator;


/**
 * ServiceLocator fuer die TAL-Services. <br> Der ServiceLocator basiert auf dem Spring-Framework.
 *
 *
 */
public class TALServiceLocator extends SpringStandaloneServiceLocator {

    /**
     * @see de.augustakom.common.service.locator.SpringStandaloneServiceLocator#getXMLConfiguration()
     */
    public String getXMLConfiguration() {
        return "de/augustakom/hurrican/service/exmodules/tal/resources/TALServices.xml";
    }

    /**
     * @see de.augustakom.common.service.iface.IServiceLocator#getServiceLocatorName()
     */
    public String getServiceLocatorName() {
        return IServiceLocatorNames.HURRICAN_TAL_SERVICE;
    }

}
