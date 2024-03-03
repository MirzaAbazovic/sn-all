/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.05.2004
 */
package de.augustakom.common.service.locator;

import java.util.*;
import org.apache.log4j.Logger;
import org.springframework.context.ConfigurableApplicationContext;

import de.augustakom.common.service.iface.IServiceLocator;
import de.augustakom.common.service.iface.IServiceLocatorNames;


/**
 * Registry, um ServiceLocator zu 'speichern'. <br>
 *
 *
 * @deprecated Use {@link de.mnet.common.service.locator.ServiceLocator} instead
 */
@Deprecated
public final class ServiceLocatorRegistry {

    private static final Logger LOGGER = Logger.getLogger(ServiceLocatorRegistry.class);

    /* Singleton-Instanz der Registry. */
    private static ServiceLocatorRegistry instance = null;

    private IServiceLocator serviceLocator = null;
    private ConfigurableApplicationContext applicationContext = null;

    /**
     * Standardkonstruktor fuer die Registry.
     */
    private ServiceLocatorRegistry() {
    }

    /**
     * Gibt eine Singleton-Instanz der Registry zurueck.
     *
     * @return
     */
    public static ServiceLocatorRegistry instance() {
        if (instance == null) {
            instance = new ServiceLocatorRegistry();
        }

        return instance;
    }

    /**
     * Fuegt der Registry einen ServiceLocator mit eindeutigem Namen hinzu. <br> Ueber den angegebenen Namen kann der
     * ServiceLocator wieder erreicht werden.
     *
     * @param serviceLocator Instanz des ServiceLocators, der in der Registry gespeichert werden soll.
     * @throws IllegalArgumentException wenn bereits ein ServiceLocator mit dem Namen gespeichert wurde.
     */
    public void addServiceLocator(IServiceLocator serviceLocator){
        LOGGER.debug("Servicelocator set: " + serviceLocator.getServiceLocatorName());
        if (this.serviceLocator != null) {
            throw new IllegalArgumentException("ServiceLocator wurde schon gesetzt!");
        }
        this.serviceLocator = serviceLocator;
    }

    public void setApplicationContext(ConfigurableApplicationContext appContext) {
        if (applicationContext != null) {
            LOGGER.warn("Setting Application Context even though it has been already set!");
        }
        LOGGER.info("ApplicationContext set!");
        this.applicationContext = appContext;
        this.serviceLocator = new ContextWrappingServiceLocator(appContext, IServiceLocatorNames.HURRICAN_UNIFIED_SERVICE);
    }

    /**
     * Entfernt einen ServiceLocator aus der Registry. <br> Bevor der ServiceLocator aus der Registry ausgetragen wird,
     * wird die Methode <code>closeServiceLocator</code> aufgerufen.
     *
     * @param name Name des ServiceLocators, der entfernt werden soll.
     */
    public void removeServiceLocator(String name) {
        LOGGER.debug("Tried to remove Servicelocator: " + name);
        if (IServiceLocatorNames.HURRICAN_UNIFIED_SERVICE.equals(name)) {
            if (applicationContext != null) {
                LOGGER.info("Shutting down Application Context by ServiceLocatorRegistry");
                applicationContext.close();
                applicationContext = null;
            }
            this.serviceLocator = null;
        }
    }

    /**
     * Entfernt alle ServiceLocator aus der Registry. <br>
     *
     * @see ServiceLocatorRegistry#removeServiceLocator(java.lang.String)
     */
    public void removeServiceLocators() {
        if (this.serviceLocator != null) {
            LOGGER.debug("Servicelocator removed: " + serviceLocator.getServiceLocatorName());
            this.serviceLocator = null;
        }
        if (this.applicationContext != null) {
            applicationContext.close();
        }
    }

    /**
     * Gibt den ServiceLocator zurueck, der mit dem angegebenen Namen hinterlegt ist oder <code>null</code>, falls kein
     * ServiceLocator mit diesem Namen existiert.
     *
     * @param name Name des gewuenschten Service-Locators.
     * @return
     */
    public IServiceLocator getServiceLocator(String name) {
        return serviceLocator;
    }

    /**
     * Gibt den ServiceLocator zurueck
     */
    public IServiceLocator getServiceLocator() {
        return serviceLocator;
    }

    /**
     * Gibt alle in der Registry gespeicherten ServiceLocator-Objekte zurueck.
     *
     * @return
     */
    public Collection<IServiceLocator> getServiceLocators() {
        if (serviceLocator != null) {
            return Collections.singleton(serviceLocator);
        }
        else {
            return Collections.emptySet();
        }
    }

    /**
     * Gibt eine Liste aller verfuegbaren/angemeldeten ServiceLocator zurueck.
     *
     * @return
     */
    public String[] getServiceLocatorNames() {
        Set<String> keys = Collections.singleton(serviceLocator.getServiceLocatorName());
        return keys.toArray(new String[keys.size()]);
    }
}
