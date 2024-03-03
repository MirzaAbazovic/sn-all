/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 *
 */
package de.augustakom.hurrican.gui.cps;

import java.awt.*;
import javax.swing.*;

import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.SwingFactory;
import de.augustakom.common.gui.swing.table.AKTableListener;
import de.augustakom.common.gui.swing.table.AKTableModelXML;
import de.augustakom.common.gui.swing.table.AKTableOwner;
import de.augustakom.common.gui.swing.table.AKTableSorter;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionLog;

/**
 *
 */
public class CPSTxLogPanel extends AKJPanel implements AKTableOwner, CPSTxObserver {
    private final static String TABLE_RESOURCE = "de/augustakom/hurrican/gui/cps/resources/CPSTxLogTable.xml";
    private final static String MESSAGE = "message";

    private AKTableListener tableListener = null;
    private CPSTable logTable = null;
    private CPSTxTextArea logDetailTextArea = null;

    /**
     * Default-Konstruktor
     */
    public CPSTxLogPanel() {
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    private void createGUI() {
        setLayout(new GridLayout(1, 1));

        logDetailTextArea = new CPSTxTextArea();
        logDetailTextArea.setLineWrap(Boolean.TRUE);
        logDetailTextArea.setWrapStyleWord(Boolean.TRUE);
        JScrollPane soDataScrollPane = new CPSTxScrollPane(logDetailTextArea, SwingFactory
                .getInstance(CPSTxPanel.RESOURCE).getText(MESSAGE));

        tableListener = new AKTableListener(this, false);

        logTable = new CPSTable(TABLE_RESOURCE);
        logTable.addMouseListener(tableListener);
        logTable.addKeyListener(tableListener);
        logTable.getSelectionModel().addListSelectionListener(
                new CPSLogTableSelectionListener(logTable, logDetailTextArea));

        @SuppressWarnings("unchecked")
        AKTableSorter<CPSTransactionLog> sorter = (AKTableSorter<CPSTransactionLog>) logTable
                .getModel();
        @SuppressWarnings("unchecked")
        AKTableModelXML<CPSTransactionLog> model = (AKTableModelXML<CPSTransactionLog>) sorter
                .getModel();
        logTable.fitTable(model.getFitList());

        AKJScrollPane scrollPane = new AKJScrollPane(logTable);
        scrollPane.setPreferredSize(new Dimension(350, 150));

        this.add(scrollPane);
        this.add(soDataScrollPane);
    }

    /**
     * @return the logTable
     */
    public CPSTable getLogTable() {
        return logTable;
    }

    /**
     * @return the tableListener
     */
    public AKTableListener getTableListener() {
        return tableListener;
    }

    /**
     * @see de.augustakom.common.gui.swing.table.AKTableOwner#showDetails(java.lang.Object)
     */
    @Override
    public void showDetails(Object details) {
        if (logTable.getSelectedRowCount() > 1) {
            GuiTools.cleanFields(this);
        }
        else if (details instanceof CPSTransactionLog) {
            logDetailTextArea.setText(((CPSTransactionLog) details).getMessage());
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.cps.CPSTxObserver#update(de.augustakom.hurrican.gui.cps.CPSTxObservable)
     */
    @Override
    public void update(CPSTxObservable observable) {
        CPSTxObserverHelper.cleanTable(observable, logTable);
    }
}
