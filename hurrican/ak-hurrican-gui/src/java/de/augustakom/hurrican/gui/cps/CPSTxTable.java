/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 *
 */
package de.augustakom.hurrican.gui.cps;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;

import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.gui.base.TableOpenOrderAction;

/**
 * AKJTable-Implementierung fuer eine CPS-Transaktion-Tabelle. <br>
 *
 *
 */
public class CPSTxTable extends CPSTable implements CPSTxObservable {

    private final List<CPSTxObserver> observers = new ArrayList<CPSTxObserver>();

    /**
     * Default-Konstruktor
     */
    public CPSTxTable(String resource) throws HurricanGUIException {
        super(resource);
        init();
        setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    }

    private void init() {
        addPopupAction(new TableOpenOrderAction());
    }

    /**
     * @see javax.swing.JTable#prepareRenderer(javax.swing.table.TableCellRenderer, int, int)
     */
    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Object value = getValueAt(row, column);

        boolean isSelected = false;
        boolean hasFocus = false;

        // Only indicate the selection and focused cell if not printing
        if (!isPaintingForPrint()) {
            isSelected = isCellSelected(row, column);

            boolean rowIsLead = (selectionModel.getLeadSelectionIndex() == row);
            boolean colIsLead = (columnModel.getSelectionModel().getLeadSelectionIndex() == column);
            hasFocus = (rowIsLead && colIsLead) && isFocusOwner();
        }

        return renderer.getTableCellRendererComponent(this, value, isSelected, hasFocus, row, column);
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {
        this.repaint();
    }

    /**
     * @see de.augustakom.hurrican.gui.cps.CPSTxObservable#addObserver(de.augustakom.hurrican.gui.cps.CPSTxObserver)
     */
    public void addObserver(CPSTxObserver observer) {
        observers.add(observer);
    }

    /**
     * @see de.augustakom.hurrican.gui.cps.CPSTxObservable#removeObserver(de.augustakom.hurrican.gui.cps.CPSTxObserver)
     */
    public void removeObserver(CPSTxObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers() {
        Iterator<CPSTxObserver> observerIterator = observers.iterator();

        while (observerIterator.hasNext()) {
            CPSTxObserver observer = observerIterator.next();
            observer.update(this);
        }
    }

}
