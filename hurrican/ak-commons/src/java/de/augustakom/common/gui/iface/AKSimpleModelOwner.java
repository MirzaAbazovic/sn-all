/*
 * Copyright (c) 2006 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.10.2006 07:40:00
 */
package de.augustakom.common.gui.iface;

import java.util.*;

import de.augustakom.common.gui.exceptions.AKGUIException;


/**
 * Interface fuer Objekte, die ein Modell besitzen koennen bzw. denen ein Modell uebergeben werden soll.
 *
 *
 */
public interface AKSimpleModelOwner {

    /**
     * Uebergibt der Implementierung das darzustellende Modell.
     *
     * @param model das zu uebergebende Modell.
     * @throws AKGUIException z.B. wenn ein 'falsches' Modell uebergeben wurde.
     */
    public void setModel(Observable model) throws AKGUIException;

    /**
     * Gibt das Modell zurueck. <br> Das zurueck gelieferte Modell muss nicht dem Modell entsprechen, das ueber die
     * Methode <code>setModel(Observable)</code> gesetzt wurde.
     *
     * @return
     */
    public Object getModel();

}


