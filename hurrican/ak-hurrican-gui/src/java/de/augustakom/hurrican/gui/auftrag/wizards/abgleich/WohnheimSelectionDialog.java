/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.08.2004 12:58:12
 */
package de.augustakom.hurrican.gui.auftrag.wizards.abgleich;

import java.awt.*;
import java.util.List;
import java.util.*;
import javax.swing.*;

import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.cc.Wohnheim;


/**
 * Dialog fuer die Auswahl eines Wohnheims. <br> Das ausgewaehlte Wohnheim wird ueber die Methode
 * <code>setValue(Object)</code> gespeichert und kann vom Aufrufer des Dialogs abgefragt werden.
 *
 *
 */
public class WohnheimSelectionDialog extends AbstractServiceOptionDialog {

    private AKJTable tbWH = null;

    private List<Wohnheim> wohnheime = null;

    /**
     * Konstruktor.
     *
     * @param wohnheime Liste mit den selektierbaren Wohnheimen.
     */
    public WohnheimSelectionDialog(List<Wohnheim> wohnheime) {
        super(null);
        this.wohnheime = wohnheime;
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle("Wohnheim wählen");

        configureButton(CMD_SAVE, "OK", "Übernimmt das selektierte Wohnheim", true, true);
        configureButton(CMD_CANCEL, null, null, false, false);

        WohnheimTableModel tbMdlWH = new WohnheimTableModel();
        tbMdlWH.setData(wohnheime);
        tbWH = new AKJTable(tbMdlWH, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbWH.fitTable(new int[] { 150, 100, 50, 100, 80 });

        AKJScrollPane spTable = new AKJScrollPane(tbWH);
        spTable.setPreferredSize(new Dimension(505, 200));

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(spTable, BorderLayout.CENTER);
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    @Override
    protected void doSave() {
        boolean showMessage = true;
        int selectedRow = tbWH.getSelectedRow();
        if (selectedRow >= 0) {
            AKMutableTableModel model = (AKMutableTableModel) tbWH.getModel();
            Object o = model.getDataAtRow(selectedRow);
            if (o instanceof Wohnheim) {
                showMessage = false;
                prepare4Close();
                setValue(o);
            }
        }

        if (showMessage) {
            String msg = "Bitte selektieren Sie zuerst ein Wohnheim.";
            MessageHelper.showInfoDialog(this, msg, null, true);
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

    static class WohnheimTableModel extends AKTableModel<Wohnheim> {
        private static final int COL_NAME = 0;
        private static final int COL_STRASSE = 1;
        private static final int COL_PLZ = 2;
        private static final int COL_ORT = 3;
        private static final int COL_HAUPTKUNDE_NO = 4;

        private static final int COL_COUNT = 5;

        /**
         * @see javax.swing.table.TableModel#getColumnCount()
         */
        @Override
        public int getColumnCount() {
            return COL_COUNT;
        }

        /**
         * @see javax.swing.table.TableModel#getColumnName(int)
         */
        @Override
        public String getColumnName(int column) {
            switch (column) {
                case COL_NAME:
                    return "Wohnheim";
                case COL_STRASSE:
                    return "Strasse";
                case COL_PLZ:
                    return "PLZ";
                case COL_ORT:
                    return "Ort";
                case COL_HAUPTKUNDE_NO:
                    return "Hauptkunde-No";
                default:
                    return null;
            }
        }

        /**
         * @see javax.swing.table.TableModel#getValueAt(int, int)
         */
        @Override
        public Object getValueAt(int row, int column) {
            Object o = getDataAtRow(row);
            if (o instanceof Wohnheim) {
                Wohnheim wh = (Wohnheim) o;
                switch (column) {
                    case COL_NAME:
                        return wh.getName();
                    case COL_STRASSE:
                        return wh.getStrasse();
                    case COL_PLZ:
                        return wh.getPlz();
                    case COL_ORT:
                        return wh.getOrt();
                    case COL_HAUPTKUNDE_NO:
                        return wh.getKundeNo();
                    default:
                        break;
                }
            }
            return null;
        }

        /**
         * @see javax.swing.table.TableModel#isCellEditable(int, int)
         */
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }

        /**
         * @see javax.swing.table.TableModel#getColumnClass(int)
         */
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return (columnIndex == COL_HAUPTKUNDE_NO) ? Long.class : super.getColumnClass(columnIndex);
        }

    }
}


