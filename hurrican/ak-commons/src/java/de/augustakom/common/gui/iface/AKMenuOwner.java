/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.07.2004 10:37:57
 */
package de.augustakom.common.gui.iface;

import de.augustakom.common.gui.swing.AKJMenu;


/**
 * Interface fuer GUI-Komponenten, die eine Menu-Definition besitzen (koennen). <br> Das Menu der Komponente kann im
 * MainFrame angezeigt werden.
 */
public interface AKMenuOwner {

    /**
     * Gibt das Menu zurueck, das angezeigt werden soll.
     *
     * @return AKJMenu
     */
    public AKJMenu getMenuOfOwner();

    /**
     * Kann aufgerufen werden, um alle MenuOwnerListener ueber eine Aenderung in der Menu-Struktur zu informieren.
     */
    public void notifyMenuOwnerListeners();

    /**
     * Fuegt dem MenuOwner einen Listener hinzu, der ueber eine Aenderung in der Menu-Struktur benachrichtigt werden
     * kann.
     *
     * @param listener Listener, der ueber Aenderungen in der Menu-Struktur informiert werden soll.
     */
    public void addMenuOwnerListener(AKMenuOwnerListener listener);

    /**
     * Entfernt einen Listener von dem Menu-Owner.
     *
     * @param listener zu entfernender Listener.
     */
    public void removeMenuOwnerListener(AKMenuOwnerListener listener);

}


