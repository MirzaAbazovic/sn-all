/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.03.2007 13:32:08
 */
package de.augustakom.common.service.iface;

import de.augustakom.common.service.base.AKServiceCommandChain;


/**
 * Interface fuer Service-Commands, die die uebergeordnete Chain 'kennen' (duerfen).
 *
 *
 */
public interface IServiceCommandChainAware {

    /**
     * Uebergibt dem Command die uebergeordnete Command-Chain.
     *
     * @param chain
     *
     */
    public void setServiceCommandChain(AKServiceCommandChain chain);

    /**
     * Speichert das Objekt <code>value</code> als CommandContext-Parameter in der Command-Chain.
     *
     * @param key
     * @param value
     *
     */
    public void setContextParameter(Object key, Object value);

    /**
     * Gibt den CommandContext-Parameter zu dem Key <code>key</code> zurueck.
     *
     * @param key
     * @return
     *
     */
    public Object getContextParameter(Object key);

}


