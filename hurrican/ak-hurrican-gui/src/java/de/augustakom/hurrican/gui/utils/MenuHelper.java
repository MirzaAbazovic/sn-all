/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.08.2004 08:27:22
 */
package de.augustakom.hurrican.gui.utils;

import java.util.*;

import de.augustakom.common.gui.swing.AKJMenu;
import de.augustakom.common.gui.swing.AKJMenuItem;
import de.augustakom.common.gui.swing.AdministrationMouseListener;


/**
 * Hilfsklasse, um Menus und MenuItems zu erstellen.
 *
 *
 */
public class MenuHelper {

    /**
     * Erstellt eine Instanz von AKJMenu mit den angegebenen Parametern. <br> Dem Menu wird zusaetzlich ein
     * MouseListener vom Typ <code>AdministrationMouseListener</code> uebergeben.
     *
     * @param text     Text fuer das Menu
     * @param tooltip  Tooltip fuer das Menu
     * @param mnemonic Mnemonic fuer das Menu
     * @return Instanz von AKJMenu.
     */
    public static AKJMenu createMenu(String text, String tooltip, int mnemonic) {
        AKJMenu menu = new AKJMenu();
        menu.addMouseListener(new AdministrationMouseListener());
        menu.setText(text);
        menu.setToolTipText(tooltip);
        menu.setMnemonic(mnemonic);
        return menu;
    }

    /**
     * @param menu
     * @param menuItems
     * @see addMenuItems2Menu(AKJMenu, List, boolean)
     */
    public static void addMenuItems2Menu(AKJMenu menu, List menuItems) {
        addMenuItems2Menu(menu, menuItems, false);
    }

    /**
     * Fuegt die MenuItems der Liste <code>menuItems</code> dem Menu hinzu. Ueber das Flag <code>addSep</code> wird
     * definiert, ob VOR die Items ein Separator eingefuegt werden soll.
     *
     * @param menu
     * @param menuItems
     */
    public static void addMenuItems2Menu(AKJMenu menu, List menuItems, boolean addSep) {
        if (menuItems != null && menu != null) {
            if (addSep && !menuItems.isEmpty()) {
                menu.addSeparator();
            }

            for (Iterator iter = menuItems.iterator(); iter.hasNext(); ) {
                AKJMenuItem element = (AKJMenuItem) iter.next();
                menu.add(element);
            }
        }
    }

}


