/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.12.2008 10:44:37
 */
package de.augustakom.hurrican.model.shared.iface;

/**
 * Interface fuer Modelle / Objekte, die einen Verweis auf ein Rack 'kennen'.
 *
 *
 */
public interface IRackAware {

    /**
     * Gibt die Rack-ID zurueck
     *
     * @return
     *
     */
    public Long getRackId();

    /**
     * Definiert die Rack-ID fuer das Objekt.
     *
     * @param rackId
     *
     */
    public void setRackId(Long rackId);

}
