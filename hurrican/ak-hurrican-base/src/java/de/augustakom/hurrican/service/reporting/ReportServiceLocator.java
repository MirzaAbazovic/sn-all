/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.02.2007 13:26:40
 */
package de.augustakom.hurrican.service.reporting;

import de.augustakom.common.service.iface.IServiceLocatorNames;
import de.augustakom.common.service.locator.SpringStandaloneServiceLocator;

/**
 * ServiceLocator für Report-Services
 *
 *
 */
public class ReportServiceLocator extends SpringStandaloneServiceLocator {

    private String xmlConfigration = "de/augustakom/hurrican/service/reporting/resources/ReportServices.xml";

    @Override
    public String getXMLConfiguration() {
        return xmlConfigration;
    }

    public void setXmlConfigration(String xmlConfigration) {
        this.xmlConfigration = xmlConfigration;
    }

    @Override
    public String getServiceLocatorName() {
        return IServiceLocatorNames.HURRICAN_REPORT_SERVICE;
    }


}
