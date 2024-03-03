/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.08.2007 11:22:48
 */
package de.augustakom.hurrican.gui.tools.sap;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.gui.base.AbstractDataPanel;
import de.augustakom.hurrican.service.exmodules.sap.ISAPService;
import de.augustakom.hurrican.service.exmodules.sap.utils.ISAPServiceFinder;
import de.augustakom.hurrican.service.exmodules.sap.utils.SAPServiceFinder;


/**
 * Abstraktes Panel fuer alle Panels, die SAP-Daten darstellen.
 *
 *
 */
public abstract class AbstractSAPPanel extends AbstractDataPanel implements ISAPServiceFinder {

    /**
     * @param resource
     */
    public AbstractSAPPanel(String resource) {
        super(resource);
    }

    /**
     * @see de.augustakom.hurrican.service.sap.utils.ISAPServiceFinder#getSAPService(java.lang.Class)
     */
    public ISAPService getSAPService(Class serviceType) throws ServiceNotFoundException {
        return SAPServiceFinder.instance().getSAPService(serviceType);
    }

    /**
     * @see de.augustakom.hurrican.service.sap.utils.ISAPServiceFinder#getSAPService(java.lang.String, java.lang.Class)
     */
    public ISAPService getSAPService(String serviceName, Class serviceType) throws ServiceNotFoundException {
        return SAPServiceFinder.instance().getSAPService(serviceName, serviceType);
    }

}


