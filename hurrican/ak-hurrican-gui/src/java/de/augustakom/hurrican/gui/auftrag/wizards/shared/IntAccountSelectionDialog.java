/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.08.2004 09:48:02
 */
package de.augustakom.hurrican.gui.auftrag.wizards.shared;

import java.awt.*;
import java.util.List;
import java.util.*;
import javax.swing.*;

import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.view.AuftragIntAccountView;


/**
 * Dialog zur Auswahl eines IntAccounts. <br> Der ausgewaehlte Account (Objekttyp: <code>AuftragIntAccountView</code>)
 * wird ueber die Methode <code>setValue</code> gespeichert.
 *
 *
 */
public class IntAccountSelectionDialog extends AbstractServiceOptionDialog implements AKObjectSelectionListener {

    private AKJTable tbAccount = null;

    private List<AuftragIntAccountView> accountViews = null;

    /**
     * Konstruktor mit Angabe der Accounts, die zur Auswahl stehen.
     *
     * @param accountViews Liste mit Objekten des Typs <code>AuftragIntAccountView</code>.
     */
    public IntAccountSelectionDialog(List<AuftragIntAccountView> accountViews) {
        super(null);
        this.accountViews = accountViews;
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle("Account auswählen");

        configureButton(CMD_SAVE, "Übernehmen", "Übernimmt den selektierten Account", true, true);
        configureButton(CMD_CANCEL, "Abbrechen", "Dialog ohne Account-Übernahme schliessen", true, true);

        AuftragAccountViewTableModel tbMdlAccount = new AuftragAccountViewTableModel();
        tbMdlAccount.setData(accountViews);
        tbAccount = new AKJTable(tbMdlAccount, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbAccount.fitTable(new int[] { 80, 150, 100, 80 });
        AKJScrollPane spTable = new AKJScrollPane(tbAccount);
        spTable.setPreferredSize(new Dimension(435, 200));

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(spTable, BorderLayout.CENTER);
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#validateSaveButton()
     */
    @Override
    protected void validateSaveButton() {
        // KEINE Ueberpruefung des Save-Buttons
    }

    /**
     * @see de.augustakom.common.gui.iface.AKObjectSelectionListener#objectSelected(java.lang.Object)
     */
    public void objectSelected(Object selection) {
        doSave();
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    @Override
    protected void doSave() {
        int row = tbAccount.getSelectedRow();
        if (row >= 0) {
            AKMutableTableModel mdl = (AKMutableTableModel) tbAccount.getModel();
            Object tmp = mdl.getDataAtRow(row);
            if (tmp instanceof AuftragIntAccountView) {
                prepare4Close();
                setValue(tmp);
            }
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
    public void update(Observable o, Object arg) {
    }

    /**
     * TableModel fuer die Darstellung der AuftragIntAccountView-Objekte.
     */
    static class AuftragAccountViewTableModel extends AKTableModel<AuftragIntAccountView> {
        private static final int COL_ORDER__NO = 0;
        private static final int COL_ANSCHLUSSART = 1;
        private static final int COL_VBZ = 2;
        private static final int COL_ACCOUNT = 3;

        private static final int COL_COUNT = 4;

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
                case COL_ORDER__NO:
                    return "Order__No";
                case COL_ANSCHLUSSART:
                    return "Anschlussart";
                case COL_VBZ:
                    return VerbindungsBezeichnung.VBZ_BEZEICHNUNG;
                case COL_ACCOUNT:
                    return "Account";
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
            if (o instanceof AuftragIntAccountView) {
                AuftragIntAccountView view = (AuftragIntAccountView) o;
                switch (column) {
                    case COL_ORDER__NO:
                        return view.getAuftragNoOrig();
                    case COL_ANSCHLUSSART:
                        return view.getAnschlussart();
                    case COL_VBZ:
                        return view.getVbz();
                    case COL_ACCOUNT:
                        return view.getAccount();
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
            return (columnIndex == COL_ORDER__NO) ? Long.class : super.getColumnClass(columnIndex);
        }
    }

}


