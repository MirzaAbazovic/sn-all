/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.07.2004 08:13:15
 */
package de.augustakom.common.gui.swing.table;

import java.awt.event.*;
import javax.swing.event.*;

import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.swing.AKJTable;


/**
 * Mouse-Listener fuer eine Tabelle, die auf Klicks reagiert. <br> Durch den Klick wird ein Objekt des Typs
 * <code>AKObjectSelectionListener</code> ueber die Selektion informiert.
 *
 *
 */
public class AKTableSingleClickMouseListener extends MouseAdapter {

    protected EventListenerList listenerList = new EventListenerList();

    /**
     * Konstruktor mit Angabe des Listener-Objekts, das benachrichtigt werden soll, wenn ein Tabellenzeile durch Klick
     * selektiert wurde.
     *
     * @param toNotify
     */
    public AKTableSingleClickMouseListener(AKObjectSelectionListener toNotify) {
        super();
        listenerList.add(AKObjectSelectionListener.class, toNotify);
    }

    /**
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() instanceof AKJTable) {
            AKJTable table = (AKJTable) e.getSource();
            AKMutableTableModel tm = (AKMutableTableModel) table.getModel();
            Object selection = tm.getDataAtRow(table.getSelectedRow());

            AKObjectSelectionListener[] listener = listenerList.getListeners(AKObjectSelectionListener.class);
            for (int i = 0; i < listener.length; i++) {
                listener[i].objectSelected(selection);
            }
        }
    }

    /**
     * Fuegt dem MouseListener einen AKObjectSelectionListener hinzu.
     *
     * @param toNotify
     */
    public void addObjectSelectionListener(AKObjectSelectionListener toNotify) {
        listenerList.add(AKObjectSelectionListener.class, toNotify);
    }

    /**
     * Entfernt einen AKObjectSelectionListener.
     *
     * @param toRemove
     */
    public void removeObjectSelectionListener(AKObjectSelectionListener toRemove) {
        listenerList.remove(AKObjectSelectionListener.class, toRemove);
    }
}


