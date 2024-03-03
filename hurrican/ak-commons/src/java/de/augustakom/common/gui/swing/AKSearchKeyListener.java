/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.12.2005 10:46:50
 */
package de.augustakom.common.gui.swing;

import java.awt.event.*;
import org.apache.commons.lang.ArrayUtils;

import de.augustakom.common.gui.iface.AKSearchComponent;


/**
 * KeyListener, ueber den eine Such-Funktion ausgefuehrt/aufgerufen werden kann.
 *
 *
 */
public class AKSearchKeyListener extends KeyAdapter {

    private AKSearchComponent searchComponent = null;
    private int[] searchKeys = null;

    /**
     * Konstruktor fuer den SearchKey-Listener. <br>
     *
     * @param searchComp Angabe der Komponente, die die Suche durchfuehren soll
     * @param searchKeys Array mit den KeyCodes der Tasten, die die Suche ausloesen sollen.
     */
    public AKSearchKeyListener(AKSearchComponent searchComp, int[] searchKeys) {
        super();
        this.searchComponent = searchComp;
        this.searchKeys = ArrayUtils.clone(searchKeys);

        if (searchComponent == null) {
            throw new IllegalArgumentException("Es wird ein Objekt vom Typ ISearchComponent benoetigt!");
        }
        if ((this.searchKeys == null) || (this.searchKeys.length <= 0)) {
            throw new IllegalArgumentException("Bitte definieren Sie die SearchKeys!");
        }
    }

    /**
     * @see java.awt.event.KeyAdapter#keyPressed(java.awt.event.KeyEvent)
     */
    @Override
    public void keyPressed(KeyEvent e) {
        for (int i = 0; i < searchKeys.length; i++) {
            if (searchKeys[i] == e.getKeyCode()) {
                searchComponent.doSearch();
                break;
            }
        }
    }

}


