/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.11.2005 14:32:29
 */
package de.augustakom.common.gui.iface;


/**
 * Listener fuer Actions, um ueber das Ende einer Action informiert zu werden.
 */
public interface AKActionFinishListener {

    /**
     * Wird aufgerufen, wenn die Action ihre Bearbeitung abgeschlossen hat.
     *
     * @param finishSuccessful Flag, ob die Action erfolgreich (true) durchgefuehrt wurde.
     */
    void actionFinished(boolean finishSuccessful);

}


