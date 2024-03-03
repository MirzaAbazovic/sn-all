/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.07.2004 08:19:23
 */
package de.augustakom.common.gui.iface;

import java.util.*;


/**
 * Listener-Interface fuer (GUI-)Komponenten, die ueber eine beliebige Selektierung (z.B. Doppelklick in einer Tabelle)
 * benachrichtigt werden sollen.
 *
 *
 */
public interface AKObjectSelectionListener extends EventListener {

    /**
     * Kann aufgerufen werden, um das z.Z. selektierte Objekte (z.B. aus einer Tabelle oder aus einem Tree) zu
     * uebergeben.
     *
     * @param selection z.Z. selektiertes Objekt.
     */
    public void objectSelected(Object selection);

}


