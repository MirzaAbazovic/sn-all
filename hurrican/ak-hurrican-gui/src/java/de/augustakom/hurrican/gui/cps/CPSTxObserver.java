/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 *
 */
package de.augustakom.hurrican.gui.cps;

import java.awt.*;

import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.common.gui.swing.table.AKTableSorter;
import de.augustakom.common.gui.utils.GuiTools;

/**
 * A class can implement the Observer interface when it wants to be informed of changes in observable objects.
 *
 *
 */
public interface CPSTxObserver {

    /**
     * This method is called whenever the observed object is changed. An application calls an Observable object's
     * notifyObservers method to have all the object's observers notified of the change.
     *
     * @param observable
     */
    public void update(CPSTxObservable observable);


    /**
     * Helper for CPSTxObserver
     *
     *
     */
    public static class CPSTxObserverHelper {

        /**
         * clean components for container
         *
         * @param container
         */
        public static void cleanFields(CPSTxObservable observable, Container container) {
            if (null != observable && observable instanceof CPSTxTable && ((CPSTxTable) observable).getSelectedRows().length != 1) {
                GuiTools.cleanFields(container);
            }
        }

        /**
         * clean content of table
         *
         * @param observable
         * @param cpsTable
         */
        public static void cleanTable(CPSTxObservable observable, CPSTable cpsTable) {
            if (null != observable && ((CPSTxTable) observable).getSelectedRows().length != 1 && null != cpsTable) {
                AKTableSorter sorter = (AKTableSorter) cpsTable.getModel();
                AKTableModel tableModel = (AKTableModel) sorter.getModel();
                tableModel.setData(null);
            }
        }
    }
}
