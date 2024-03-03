/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.08.2004 13:58:07
 */
package de.augustakom.hurrican.service.cc.impl.command;

import org.apache.log4j.Logger;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.AKAuthenticationServiceNames;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.authentication.service.IAuthenticationService;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.service.iface.IServiceObject;
import de.augustakom.common.service.locator.ServiceLocatorRegistry;
import de.augustakom.hurrican.HurricanConstants;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.iface.IHurricanService;
import de.augustakom.hurrican.service.base.impl.AbstractHurricanServiceCommand;
import de.augustakom.hurrican.service.billing.IBillingService;
import de.augustakom.hurrican.service.billing.utils.IBillingServiceFinder;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.ICCService;
import de.augustakom.hurrican.service.cc.utils.ICCServiceFinder;
import de.augustakom.hurrican.service.reporting.IReportService;


/**
 * Abstrakte Implementierung eines ServiceCommands.
 *
 *
 */
public abstract class AbstractServiceCommand extends AbstractHurricanServiceCommand implements ICCServiceFinder, IBillingServiceFinder {
    private static final Logger LOGGER = Logger.getLogger(AbstractServiceCommand.class);

    private <T extends IServiceObject> T getService(String serviceName, Class<T> serviceType)
            throws ServiceNotFoundException {
        // Wenn der ServiceCommand nicht aus dem App-Context kommt, dann
        // via Servicelocator holen.
        // Wenn alles komplett auf DI umgestellt ist, dann kann man das entfernen
        // -> alles aus dem AppContext
        if (applicationContext != null) {
            return applicationContext.getBean(serviceName, serviceType);
        }
        else {
            return ServiceLocatorRegistry.instance().getServiceLocator()
                    .getService(serviceName, serviceType, null);
        }
    }

    private <T extends IServiceObject> T getService(Class<T> serviceType) throws ServiceNotFoundException {
        return getService(serviceType.getName(), serviceType);
    }


    /**
     * @see de.augustakom.hurrican.service.base.utils.IServiceFinder#findService(java.lang.String, java.lang.Class)
     */
    @Override
    public <T extends IHurricanService> T findService(String serviceName, Class<T> serviceType) throws ServiceNotFoundException {
        throw new ServiceNotFoundException(
                "findService not implemented in " + AbstractServiceCommand.class.getName());
    }

    /**
     * @see de.augustakom.hurrican.service.billing.utils.IBillingServiceFinder#getBillingService(java.lang.Class)
     */
    @Override
    public <T extends IBillingService> T getBillingService(Class<T> serviceType) throws ServiceNotFoundException {
        return getService(serviceType);
    }

    /**
     * @see de.augustakom.hurrican.service.billing.utils.IBillingServiceFinder#getBillingService(java.lang.String,
     * java.lang.Class)
     */
    @Override
    public <T extends IBillingService> T getBillingService(String serviceName, Class<T> serviceType) throws ServiceNotFoundException {
        return getService(serviceName, serviceType);
    }

    /**
     * @see de.augustakom.hurrican.service.cc.utils.ICCServiceFinder#getCCService(java.lang.String, java.lang.Class)
     */
    @Override
    public <T extends ICCService> T getCCService(String name, Class<T> type) throws ServiceNotFoundException {
        return getService(name, type);
    }

    /**
     * @see de.augustakom.hurrican.service.cc.utils.ICCServiceFinder#getCCService(java.lang.Class)
     */
    @Override
    public <T extends ICCService> T getCCService(Class<T> type) throws ServiceNotFoundException {
        return getService(type);
    }

    /**
     * Gibt einen Reporting-Service zurueck.
     *
     * @param name Name des Services.
     * @param type Typ des Services
     * @return Reporting-Service
     * @throws ServiceNotFoundException wenn der Service nicht gefunden werden konnte.
     */
    public <T extends IReportService> T getReportService(String name, Class<T> type) throws ServiceNotFoundException {
        return getService(name, type);
    }

    /**
     * @see getReportService(String, Class)
     */
    public <T extends IReportService> T getReportService(Class<T> type) throws ServiceNotFoundException {
        return getService(type);
    }

    /**
     * Sucht nach einem Authentication-Service und gibt diesen zurueck.
     *
     * @param name Name des gesuchten Services.
     * @param type Typ des gesuchten Services.
     * @return gesuchter Service vom Typ <code>type</code>
     * @throws ServiceNotFoundException wenn der Service nicht gefunden werden konnte.
     */
    protected <T extends IAuthenticationService> T getAuthenticationService(String name, Class<T> type) throws ServiceNotFoundException {
        return getService(name, type);
    }

    /**
     * Ermittelt die AuftragDaten zu der angegebenen Id <code>aId</code>. <br> Die Suche erfolgt innerhalb der aktuellen
     * Transaktion.
     *
     * @param aId
     * @return Instanz von <code>AuftragDaten</code>.
     * @throws FindException wenn bei der Ermittlung der Daten ein Fehler auftritt.
     */
    public AuftragDaten getAuftragDatenTx(Long aId) throws FindException {
        try {
            CCAuftragService as = getCCService(CCAuftragService.class);
            AuftragDaten ad = as.findAuftragDatenByAuftragIdTx(aId);
            return ad;
        }
        catch (Exception e) {
            throw new FindException(e.getMessage(), e);
        }
    }

    /**
     * Ermittelt die AuftragTechnik zu der angegebenen Id <code>aId</code>. <br> Die Suche erfolgt innerhalb der
     * aktuellen Transaktion.
     *
     * @param aId
     * @return Instanz von <code>AuftragTechnik</code>.
     * @throws FindException wenn bei der Ermittlung der Daten ein Fehler auftritt.
     */
    public AuftragTechnik getAuftragTechnikTx(Long aId) throws FindException {
        try {
            CCAuftragService as = getCCService(CCAuftragService.class);
            AuftragTechnik at = as.findAuftragTechnikByAuftragIdTx(aId);
            return at;
        }
        catch (Exception e) {
            throw new FindException(e.getMessage(), e);
        }
    }

    /**
     * Ermittelt das Benutzer-Objekt ueber die Session-ID
     *
     * @param sessionId Session-ID des Users
     * @return Instanz von AKUser
     * @throws FindException wenn der User nicht ermittelt werden konnte.
     *
     */
    protected AKUser getAKUser(Long sessionId) throws FindException {
        try {
            AKUserService uss = getAuthenticationService(AKAuthenticationServiceNames.USER_SERVICE, AKUserService.class);
            AKUser user = uss.findUserBySessionId(sessionId);
            if (user == null) {
                throw new FindException("Benutzer konnte nicht ermittelt werden!");
            }
            return user;
        }
        catch (FindException e) {
            throw e;
        }
        catch (Exception e) {
            throw new FindException("Fehler bei der Ermittlung des aktuellen Users: " + e.getMessage(), e);
        }
    }

    /**
     * @see getAKUser(Long)
     */
    protected AKUser getAKUserSilent(Long sessionId) {
        try {
            return getAKUser(sessionId);
        }
        catch (Exception e) {
            LOGGER.debug("getAKUserSilent() - exception finding user: " + e.getMessage());
        }
        return null;
    }

    /**
     * Gibt den LoginNamen des aktuellen Users zurueck. <br> Sollte der User nicht ermittelt werden koennen, wird
     * 'unknown' zurueck gegeben.
     *
     * @param sessionId
     * @return
     *
     */
    protected String getAKUserName(Long sessionId) {
        AKUser user = getAKUserSilent(sessionId);
        return user != null ? user.getLoginName() : HurricanConstants.UNKNOWN;
    }

}


