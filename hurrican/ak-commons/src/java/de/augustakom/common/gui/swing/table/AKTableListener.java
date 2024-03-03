/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.12.2004 11:05:02
 */
package de.augustakom.common.gui.swing.table;

import java.awt.*;
import java.awt.event.*;

import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJTable;


/**
 * Mouse- und KeyListener fuer eine Tabelle. <br> Das selektierte Objekt wird der Methode showDetails(Object)
 * uebergeben.
 */
public class AKTableListener extends MouseAdapter implements KeyListener {

    private AKTableOwner owner;
    private boolean acceptDoubleClick = true;

    private boolean pressed = false;
    private boolean released = false;

    /**
     * Konstruktor mit Angabe des Table-Owners.
     */
    public AKTableListener(AKTableOwner owner) {
        this.owner = owner;
    }

    /**
     * Konstruktor mit Angabe des Table-Owners. <br> Ueber das Flag <code>acceptDoubleClick</code> kann definiert
     * werden, ob ein Doppelklick ignoriert oder verwendet werden soll (Standard: true).
     *
     * @param owner
     * @param acceptDoubleClick
     */
    public AKTableListener(AKTableOwner owner, boolean acceptDoubleClick) {
        this.owner = owner;
        this.acceptDoubleClick = acceptDoubleClick;
    }

    /**
     * Benachrichtigt das Panel, dass sich die Selektion geaendert hat
     */
    public void selectionChanged(AKJTable table) {
        try {
            table.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            AKMutableTableModel<?> tm = (AKMutableTableModel<?>) table.getModel();
            Object details = tm.getDataAtRow(table.getSelectedRow());
            owner.showDetails(details);
        }
        finally {
            table.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }

    /**
     * Veranlasst das Panel dazu, die Daten neu zu laden
     */
    private void refresh() {
        if (owner instanceof AKDataLoaderComponent) {
            ((AKDataLoaderComponent) owner).loadData();
        }
    }

    /**
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    @Override
    public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger()) {
            pressed = false;
            fireMouse(e);
        }
        else {
            pressed = true;
            recogniseClick(e);
        }
    }

    /**
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {
            released = false;
            fireMouse(e);
        }
        else {
            released = true;
            recogniseClick(e);
        }
    }

    /**
     * Funktion ist als Ersatz/Zusatz fuer mouseClicked gedacht. <br> Situation: Maus-Klick erfolgt; Maus wird bewegt;
     * Maustaste wird losgelassen --> mouseClicked wird nicht ausgefuehrt. Diese Methode ueberprueft, ob sowohl
     * mousePressed, als auch mouseReleased aufgetreten ist. Ist dies der Fall, wird ueber fireMouse(e) die eigentliche
     * Funktion dennoch aufgerufen.
     *
     *
     */
    protected void recogniseClick(MouseEvent e) {
        if (pressed && released && !e.isPopupTrigger()) {
            fireMouse(e);
            pressed = false;
            released = false;
        }
    }

    /**
     * Aendert die Selektion der Tabelle - falls Event gueltig ist.
     */
    private void fireMouse(MouseEvent e) {
        if (!acceptDoubleClick && (e.getClickCount() >= 2)) {
            return;
        }
        else if ((e.getClickCount() < 2) && (e.getSource() instanceof AKJTable)) {
            selectionChanged((AKJTable) e.getSource());
        }
    }

    /**
     * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
     */
    @Override
    public void keyPressed(KeyEvent e) {
        // not used
    }

    /**
     * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
     */
    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getSource() instanceof AKJTable) {
            if (((e.isControlDown() && (e.getKeyCode() == KeyEvent.VK_A)) ||
                    (e.getKeyCode() == KeyEvent.VK_DOWN)) || (e.getKeyCode() == KeyEvent.VK_UP) ||
                    (e.getKeyCode() == KeyEvent.VK_END) || (e.getKeyCode() == KeyEvent.VK_HOME) ||
                    (e.getKeyCode() == KeyEvent.VK_PAGE_UP) || (e.getKeyCode() == KeyEvent.VK_PAGE_DOWN)) {
                selectionChanged((AKJTable) e.getSource());
            }
            else if (e.getKeyCode() == KeyEvent.VK_F5) {
                refresh();
            }
        }
    }

    /**
     * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
     */
    @Override
    public void keyTyped(KeyEvent e) {
        // not used
    }

}

