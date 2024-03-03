/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.06.2009 15:18:51
 */
package de.augustakom.hurrican.model.shared.iface;


/**
 * Interface fuer Modelle, die eine Seriennummer "kennen".
 *
 *
 */
public interface ISerialNoAwareModel {

    /**
     * Gibt eine Seriennummer zurueck.
     *
     * @return
     *
     */
    public String getSerialNo();

}


