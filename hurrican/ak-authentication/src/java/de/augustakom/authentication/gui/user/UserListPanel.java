/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.05.2004 15:06:42
 */
package de.augustakom.authentication.gui.user;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

import de.augustakom.authentication.gui.GUISystemRegistry;
import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.gui.swing.AKJAbstractPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.table.AKTableSorter;


/**
 * Panel fuer die Darstellung einer Tabelle mit AKUser-Objekten.
 */
public class UserListPanel extends AKJAbstractPanel {

    private List<AKUser> model = null;
    private AKTableSorter<AKUser> tableSorter = null;

    /**
     * Konstruktor mit Angabe der darzustellenden AKUser-Objekte.
     */
    public UserListPanel(List<AKUser> users) {
        super("de/augustakom/authentication/gui/users/resources/UserListPanel.xml");
        this.model = users;
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        UserTableModel tableModel = new UserTableModel();
        tableModel.setData(model);

        tableSorter = new AKTableSorter<>(tableModel);
        AKJTable table = new AKJTable(tableSorter);
        tableSorter.addMouseListenerToHeaderInTable(table);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoResizeMode(AKJTable.AUTO_RESIZE_OFF);
        table.addMouseListener(new TableMouseListener());
        fitColumns(table);

        AKJScrollPane tableScroll = new AKJScrollPane(table);
        tableScroll.setPreferredSize(new Dimension(480, 200));

        this.setLayout(new BorderLayout());
        this.add(tableScroll, BorderLayout.CENTER);
    }

    /**
     * Setzt die preferredWidth der Table-Columns
     */
    private void fitColumns(AKJTable table) {
        TableColumn column = null;
        for (int i = 0; i < table.getColumnCount(); i++) {
            column = table.getColumnModel().getColumn(i);
            if (i == UserTableModel.COL_ID) {
                column.setPreferredWidth(50);
            }
            else if ((i >= UserTableModel.COL_NAME) && (i <= UserTableModel.COL_DEPARTMENT)) {
                column.setPreferredWidth(120);
            }
            else if (i == UserTableModel.COL_ACTIVE) {
                column.setPreferredWidth(50);
            }
        }
    }

    @Override
    protected void execute(String command) {
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    /**
     * MouseListener fuer die Tabelle
     */
    class TableMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            try {
                setWaitCursor();
                if (e.getClickCount() == 2) {
                    AKJTable table = (AKJTable) e.getSource();
                    int row = table.getSelectedRow();

                    UserDataFrame dataFrame = new UserDataFrame(tableSorter.getDataAtRow(row));
                    GUISystemRegistry.instance().getMainFrame().registerFrame(dataFrame, false);
                }
            }
            finally {
                setDefaultCursor();
            }
        }
    }
}
