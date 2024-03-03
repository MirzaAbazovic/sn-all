/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File Created: 27.08.2007 14:08:24
 */
package de.augustakom.hurrican.service.exmodules.sap;

import de.augustakom.common.service.iface.IServiceLocatorNames;
import de.augustakom.common.service.locator.SpringStandaloneServiceLocator;


/**
 * ServiceLocator fuer die SAP-Services. <br> Der ServiceLocator basiert auf dem Spring-Framework.
 *
 *
 */
public class SAPServiceLocator extends SpringStandaloneServiceLocator {

    /**
     * @see de.augustakom.common.service.locator.SpringStandaloneServiceLocator#getXMLConfiguration()
     */
    public String getXMLConfiguration() {
        return "de/augustakom/hurrican/service/exmodules/sap/resources/SAPServices.xml";
    }

    /**
     * @see de.augustakom.common.service.iface.IServiceLocator#getServiceLocatorName()
     */
    public String getServiceLocatorName() {
        return IServiceLocatorNames.HURRICAN_SAP_SERVICE;
    }

}
