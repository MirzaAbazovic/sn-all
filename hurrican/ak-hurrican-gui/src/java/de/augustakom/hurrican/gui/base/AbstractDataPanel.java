/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.07.2004 11:52:27
 */
package de.augustakom.hurrican.gui.base;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import de.augustakom.common.gui.iface.AKModelOwner;
import de.augustakom.common.gui.iface.AKModelWatcher;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.tools.lang.ObjectChangeDetector;

/**
 * Abstrakte Implementierung fuer Panels, die speicherbare Daten darstellen.
 *
 *
 */
public abstract class AbstractDataPanel extends AbstractServicePanel implements AKModelOwner, AKModelWatcher {

    private ObjectChangeDetector detector = null;

    /**
     * Konstruktor mit Angabe der Resource-Datei.
     *
     * @param resource
     */
    public AbstractDataPanel(String resource) {
        super(resource);
        detector = new ObjectChangeDetector();
    }

    public AbstractDataPanel(String resource, LayoutManager layout) {
        super(resource, layout);
        detector = new ObjectChangeDetector();
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelWatcher#addObjectToWatch(java.lang.String, java.lang.Object)
     */
    @Override
    public void addObjectToWatch(String name, Object toWatch) {
        detector.addObjectToWatch(name, toWatch);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelWatcher#removeObjectFromWatch(java.lang.String)
     */
    @Override
    public void removeObjectFromWatch(String name) {
        detector.removeObjectFromWatch(name);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelWatcher#removeObjectsFromWatch()
     */
    @Override
    public void removeObjectsFromWatch() {
        detector.removeObjectsFromWatch();
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelWatcher#hasChanged(java.lang.String, java.lang.Object)
     */
    @Override
    public boolean hasChanged(String name, Object actualModel) {
        return detector.hasChanged(name, actualModel);
    }

    /**
     * Zeigt einen OptionDialog an, ob die Aenderungen gespeichert werden sollen oder nicht.
     *
     * @return ausgewaehlte Option (entweder JOptionPane.YES_OPTION oder JOptionPane.NO_OPTION).
     */
    protected int saveQuestion() {
        String title = "Änderungen speichern?";
        String msg = "Es wurden Daten geändert.\nWollen Sie die Änderungen noch speichern?";

        return MessageHelper.showConfirmDialog(this, msg, title,
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    }

    /**
     * Kann von den Ableitungen ueberschrieben werden, um die Ansicht zu aktualisiern. <br> Die Methode wird auch von
     * dem Key-Listener aufgerufen, der ueber die Methode <code>getRefreshKeyListener()</code> zurueck gegeben wird.
     */
    protected void refresh() {
    }

    /**
     * Gibt einen KeyListener zurueck, der auf die Taste 'F5' reagiert und dabei die Methode <code>refresh()</code>
     * aufruft.
     *
     * @return KeyListener
     */
    protected KeyListener getRefreshKeyListener() {
        return new RefreshKeyListener();
    }

    /**
     * KeyListener, der auf F5 reagiert und die Methode refresh aufruft.
     */
    class RefreshKeyListener extends KeyAdapter {
        /**
         * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
         */
        @Override
        public void keyReleased(KeyEvent e) {
            if (KeyEvent.VK_F5 == e.getKeyCode()) {
                refresh();
            }
        }
    }
}


