/*
 * Copyright (c) 2006 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.01.2007 08:59:18
 */
package de.augustakom.common.gui.iface;


/**
 * Interface fuer Components, die ein Modell/Objekt 'kennen'.
 */
public interface AKModelAwareComponent {

    /**
     * Uebergibt der Component ein Modell.
     */
    void setModel(Object model);

    /**
     * Gibt das Modell der Component zurueck.
     */
    Object getModel();

}


