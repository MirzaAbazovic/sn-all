/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 *
 */
package de.augustakom.hurrican.gui.cps;

import java.awt.*;

import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.table.AKTableListener;
import de.augustakom.common.gui.swing.table.AKTableModelXML;
import de.augustakom.common.gui.swing.table.AKTableOwner;
import de.augustakom.common.gui.swing.table.AKTableSorter;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionSubOrder;

/**
 *
 */
public class CPSTxOrderPanel extends AKJPanel implements AKTableOwner {
    private final static String TABLE_RESOURCE = "de/augustakom/hurrican/gui/cps/resources/CPSTxOrderTable.xml";

    private CPSTable orderTable = null;

    /**
     * Default-Konstruktor
     */
    public CPSTxOrderPanel() {
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    private void createGUI() {
        setLayout(new GridLayout(1, 1));
        AKTableListener tableListener = new AKTableListener(this, false);

        orderTable = new CPSTable(TABLE_RESOURCE);
        orderTable.addMouseListener(tableListener);
        orderTable.addKeyListener(tableListener);

        @SuppressWarnings("unchecked")
        AKTableSorter<CPSTransactionSubOrder> sorter = (AKTableSorter<CPSTransactionSubOrder>) orderTable
                .getModel();
        @SuppressWarnings("unchecked")
        AKTableModelXML<CPSTransactionSubOrder> model = (AKTableModelXML<CPSTransactionSubOrder>) sorter
                .getModel();
        orderTable.fitTable(model.getFitList());

        AKJScrollPane scrollPane = new AKJScrollPane(orderTable);
        scrollPane.setPreferredSize(new Dimension(350, 150));

        this.add(scrollPane);
    }

    /**
     * @return the logTable
     */
    public CPSTable getOrderTable() {
        return orderTable;
    }

    /**
     * @see de.augustakom.common.gui.swing.table.AKTableOwner#showDetails(java.lang.Object)
     */
    @Override
    public void showDetails(Object details) {
    }

}
