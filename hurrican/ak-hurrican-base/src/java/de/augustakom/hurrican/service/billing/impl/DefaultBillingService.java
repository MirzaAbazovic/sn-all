/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.07.2004 08:32:05
 */
package de.augustakom.hurrican.service.billing.impl;

import ch.ergon.taifun.ws.types.AuthenticationType;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.ws.client.core.WebServiceTemplate;

import de.augustakom.common.service.exceptions.ServiceCommandException;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.ws.MnetWebServiceTemplate;
import de.augustakom.hurrican.model.cc.WebServiceConfig;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.base.impl.DefaultHurricanService;
import de.augustakom.hurrican.service.billing.IBillingService;
import de.augustakom.hurrican.service.cc.QueryCCService;


/**
 * Basisklasse fuer alle Hurrican Service-Implementierungen im Billing-Bereich.
 *
 *
 */
public class DefaultBillingService extends DefaultHurricanService implements IBillingService {

    private static final Logger LOGGER = Logger.getLogger(DefaultBillingService.class);

    private MnetWebServiceTemplate billingWebServiceTemplate = null;

    /**
     * @see BillingServiceFinder.getBillingService(Class)
     */
    protected <T extends IBillingService> T getBillingService(Class<T> type) throws ServiceNotFoundException {
        return applicationContext.getBean(type.getName(), type);
    }

    /**
     * Konfiguriert ein WebService-Template mit Daten aus der WSConfig-Tabelle und gibt das Template konfiguriert
     * zurueck. <br> <br> Folgende Konfigurationen werden vorgenommen: <ul> <li>Default-URI des WebServices wird gesetzt
     * <li>Security-Interceptor fuer 'UsernameToken' wird mit UserName/Password konfiguriert (nur, wenn in Registry
     * definiert) </ul>
     *
     * @return das konfigurierte WebServiceTemplate
     * @throws ServiceCommandException falls notwendige Konfigurationsparameter nicht vorhanden sind oder bei der
     *                                 Konfiguration ein Fehler auftritt
     */
    protected WebServiceTemplate configureAndGetBillingWSTemplate() throws ServiceCommandException {
        try {
            QueryCCService qs = applicationContext.getBean(QueryCCService.class.getName(), QueryCCService.class);
            WebServiceConfig wsCfg = qs.findById(
                    WebServiceConfig.WS_CFG_TAIFUN, WebServiceConfig.class);

            MnetWebServiceTemplate wst = getBillingWebServiceTemplatePrivate();
            wst.configureWSTemplate(wsCfg);

            return wst;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException("Error configuring WebServiceTemplate: " + e.getMessage(), e);
        }
    }

    /**
     * Funktion ermittelt Benutzername und Passwort fuer die Billing-WebServices und generiert ein
     * AuthenticationType-Objekt mit diesen Parametern.
     *
     * @return AuthenticationType Objekt fuer die WS-Authentifizierung des Billing-Systems
     */
    protected AuthenticationType configureAndGetAuthenticationType() throws FindException {
        try {
            QueryCCService qs = applicationContext.getBean(QueryCCService.class);
            WebServiceConfig wsCfg = qs.findById(
                    WebServiceConfig.WS_CFG_TAIFUN, WebServiceConfig.class);

            String user = wsCfg.getAppSecurementUser();
            String password = wsCfg.getAppSecurementPassword();
            if (StringUtils.isBlank(user) || StringUtils.isBlank(password)) {
                throw new FindException(
                        "Konnte Benutzername und/oder Passwort für Taifun-WebService nicht ermitteln.");
            }

            AuthenticationType authType = AuthenticationType.Factory.newInstance();
            authType.setUsername(user);
            authType.setPassword(password);

            return authType;
        }
        catch (Exception e) {
            LOGGER.error(e);
            throw new FindException(e.getMessage(), e);
        }
    }

    /**
     * @return
     * @see #getBillingWebServiceTemplate()
     */
    private MnetWebServiceTemplate getBillingWebServiceTemplatePrivate() {
        return billingWebServiceTemplate;
    }

    /**
     * @return the billingWebServiceTemplate
     * @deprecated use configureAndGetBillingWSTemplate()
     */
    @Deprecated
    public MnetWebServiceTemplate getBillingWebServiceTemplate() {
        return billingWebServiceTemplate;
    }

    /**
     * @param billingWebServiceTemplate the billingWebServiceTemplate to set
     */
    public void setBillingWebServiceTemplate(MnetWebServiceTemplate billingWebServiceTemplate) {
        this.billingWebServiceTemplate = billingWebServiceTemplate;
    }

}


