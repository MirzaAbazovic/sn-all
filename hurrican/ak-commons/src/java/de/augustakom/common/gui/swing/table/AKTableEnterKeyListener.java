/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.07.2010 17:30:19
 */

package de.augustakom.common.gui.swing.table;

import java.awt.event.*;
import java.util.*;
import javax.swing.event.*;

import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.swing.AKJTable;

/**
 * Key-Listener fuer Tabelle tbResult<br> Durch druecken der Enter Taste werden die Objekte des Typs
 * <code>AKObjectSelectionListener</code> ueber die Selektion informiert.
 *
 *
 */
public class AKTableEnterKeyListener extends KeyAdapter {
    private final EventListenerList listenerList = new EventListenerList();
    private final List<AKTableModel<?>> modelList = new LinkedList<>();

    /**
     * Konstruktor mit Angabe des Listener-Objekts, das benachrichtigt werden soll, wenn eine Tabellenzeile durch Enter
     * aktiviert wurde.
     */
    public AKTableEnterKeyListener(AKObjectSelectionListener toNotify) {
        super();
        listenerList.add(AKObjectSelectionListener.class, toNotify);
    }

    /**
     * Die <code>table</code> der <code>event</code> Klasse liefert eine Referenz auf <code>AKTableSorter</code>. Aus
     * dem Sorter holt diese Funktion das eigentliche Tabellenmodell.
     *
     * @param mtm zu konvertierende Referenz (instance of AKTableSorter)
     */
    private AKTableModel<?> getTableModel(AKMutableTableModel<?> mtm) {
        if (mtm == null) {
            return null;
        }

        if (mtm instanceof AKTableSorter<?>) {
            AKTableSorter<?> ts = (AKTableSorter<?>) mtm;
            mtm = (AKMutableTableModel<?>) ts.getModel();
            if ((mtm != null) && (mtm instanceof AKTableModel<?>)) {
                return (AKTableModel<?>) mtm;
            }
        }
        else if (mtm instanceof AKTableModel<?>) {
            return (AKTableModel<?>) mtm;
        }

        return null;
    }


    /**
     * Prueft ob das Tabel Model in der Liste als Typ eine Entsprechung hat.
     */
    private boolean isTableModelPresent(AKTableModel<?> tm) {
        for (AKTableModel<?> model : modelList) {
            if (model.getClass().isInstance(tm)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Event Handler um VK_ENTER Nachrichten zu empfangen und abhaengig vom Tabellenmodell (bspw. KundeAdresseTableModel
     * oder AuftragDatenTableModel) zu verarbeiten.
     */
    @Override
    public void keyPressed(KeyEvent event) {
        if ((event.getSource() instanceof AKJTable) && (event.getKeyCode() == KeyEvent.VK_ENTER)) {
            AKJTable table = (AKJTable) event.getSource();
            AKTableModel<?> tm = getTableModel((AKMutableTableModel<?>) table.getModel());

            if ((tm != null) && ((modelList.isEmpty()) || (isTableModelPresent(tm)))) {
                event.consume();
                Object selection = tm.getDataAtRow(table.getSelectedRow());

                AKObjectSelectionListener[] listeners = listenerList.getListeners(AKObjectSelectionListener.class);
                for (AKObjectSelectionListener listener : listeners) {
                    listener.objectSelected(selection);
                }
            }
        }
    }

    /**
     * Fuegt dem KeyListener einen AKObjectSelectionListener hinzu.
     */
    public void addObjectSelectionListener(AKObjectSelectionListener toNotify) {
        listenerList.add(AKObjectSelectionListener.class, toNotify);
    }

    /**
     * Entfernt einen AKObjectSelectionListener.
     */
    public void removeObjectSelectionListener(AKObjectSelectionListener toRemove) {
        listenerList.remove(AKObjectSelectionListener.class, toRemove);
    }

    /**
     * Fuegt der Liste ein Table Model hinzu.
     */
    public void addAKTableModel(AKTableModel<?> tm) {
        modelList.add(tm);
    }

    /**
     * Entfernt aus der Liste ein Table Model.
     */
    public void removeAKTableModel(AKTableModel<?> tm) {
        Iterator<AKTableModel<?>> it = modelList.iterator();
        while (it.hasNext()) {
            AKTableModel<?> model = it.next();
            if (model.getClass().isInstance(tm)) {
                it.remove();
            }
        }
    }
}
