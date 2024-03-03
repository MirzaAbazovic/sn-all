/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.03.2009 13:40:11
 */
package de.mnet.hurrican.webservice.base;

import javax.servlet.*;
import org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint;
import org.springframework.ws.server.endpoint.annotation.Endpoint;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.HurricanConstants;
import de.augustakom.hurrican.service.billing.IBillingService;
import de.augustakom.hurrican.service.billing.utils.BillingServiceFinder;
import de.augustakom.hurrican.service.cc.ICCService;
import de.augustakom.hurrican.service.cc.utils.CCServiceFinder;

/**
 * Abstrakte Klasse fuer alle (M-net / Hurrican) Endpoints, die die SOAP calls 'marshallen'.
 *
 *
 * @deprecated {@link Endpoint} Annotation benutzen
 */
@Deprecated
public abstract class MnetAbstractMarshallingPayloadEndpoint extends AbstractMarshallingPayloadEndpoint {

    /**
     * Gibt die aktuelle Session-ID der Hurrican-Authentication zurueck.
     *
     * @return Session-ID der Hurrican-Authentication
     */
    protected Long getSessionId() {
        ServletContext ctx = MnetServletContextHelper.getServletContextFromTCH();
        return (Long) ctx.getAttribute(HurricanConstants.HURRICAN_SESSION_ID);
    }

    /**
     * Gibt eine Instanz des angeforderten CC-Service zurueck.
     */
    protected <T extends ICCService> T getCCService(Class<T> clazz) throws ServiceNotFoundException {
        return CCServiceFinder.instance().getCCService(clazz);
    }

    /**
     * Gibt eine Instanz des angeforderten Billing-Service zurueck.
     */
    protected <T extends IBillingService> T getBillingService(Class<T> clazz) throws ServiceNotFoundException {
        return BillingServiceFinder.instance().getBillingService(clazz);
    }

}


