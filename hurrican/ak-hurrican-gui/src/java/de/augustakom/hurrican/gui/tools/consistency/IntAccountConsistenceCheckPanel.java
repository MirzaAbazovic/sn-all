/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.07.2005 13:42:34
 */
package de.augustakom.hurrican.gui.tools.consistency;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.hurrican.model.cc.IntAccount;
import de.augustakom.hurrican.service.cc.ConsistenceCheckService;


/**
 * Panel fuer den Konsistenz-Check der IntAccounts. <br> Es werden alle Accounts ermittelt, die mehreren aktiven
 * Auftraegen zugeordnet sind.
 *
 *
 */
public class IntAccountConsistenceCheckPanel extends AbstractConsistenceCheckPanel<List<IntAccount>, Void> {

    private static final Logger LOGGER = Logger.getLogger(IntAccountConsistenceCheckPanel.class);

    private AKReflectionTableModel<IntAccount> tbMdlResult = null;

    public IntAccountConsistenceCheckPanel() {
        super(null);
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJLabel lblDesc = new AKJLabel("Ermittelt IntAccounts, die mehreren aktiven Auftraegen zugeordnet sind.");

        AKJPanel north = new AKJPanel(new GridBagLayout());
        north.add(lblDesc, GBCFactory.createGBC(100, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 10, 10)));

        tbMdlResult = new AKReflectionTableModel<IntAccount>(
                new String[] { "ID", "Account", "LI_NR" },
                new String[] { "id", "account", "liNr" },
                new Class[] { Long.class, String.class, Integer.class });
        AKJTable tbResult = new AKJTable(tbMdlResult, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbResult.attachSorter();
        tbResult.fitTable(new int[] { 90, 120, 55 });

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(north, BorderLayout.NORTH);
        getChildPanel().add(new AKJScrollPane(tbResult), BorderLayout.CENTER);
    }

    /**
     * @see de.augustakom.hurrican.gui.tools.consistency.AbstractConsistenceCheckPanel#executeCheckConsistence()
     */
    @Override
    public void executeCheckConsistence() {
        swingWorker = new SwingWorker<List<IntAccount>, Void>() {
            @Override
            protected List<IntAccount> doInBackground() throws Exception {
                ConsistenceCheckService ccs = getCCService(ConsistenceCheckService.class);
                return ccs.findMultipleUsedIntAccounts();
            }

            @Override
            protected void done() {
                try {
                    tbMdlResult.setData(get());
                }
                catch (CancellationException e) {
                    LOGGER.info(e.getMessage(), e);
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    MessageHelper.showErrorDialog(getMainFrame(), e);
                }
                finally {
                    enableGuiButtonsForSwingWorkerInProgress(false);
                    setDefaultCursor();
                    stopProgressBar();
                }
            }
        };

        setWaitCursor();
        showProgressBar("Accounts prüfen...");
        enableGuiButtonsForSwingWorkerInProgress(true);
        tbMdlResult.setData(null);
        swingWorker.execute();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
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
}


