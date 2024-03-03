/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.01.2005 11:52:35
 */
package de.augustakom.hurrican.gui.shared;

import java.awt.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.DateTableCellRenderer;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.service.billing.RufnummerService;

/**
 * Dialog, um die aktuellen Rufnummern zu einem Auftrag anzuzeigen.
 *
 *
 */
public class RufnummerDialog extends AbstractServiceOptionDialog {
    private static final Logger LOGGER = Logger.getLogger(RufnummerDialog.class);
    private static final long serialVersionUID = -7269711665424166192L;

    private final Long auftragNoOrig;

    private RufnummerTableModel tbMdlRN;

    /**
     * Konstruktor mit der auftragNoOrig und den anzuzeigenden Rufnummern
     */
    public RufnummerDialog(Long auftragNoOrig) {
        super(null);
        this.auftragNoOrig = auftragNoOrig;
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle("Rufnummern zum Auftrag " + auftragNoOrig);
        configureButton(CMD_SAVE, "OK", null, true, true);
        configureButton(CMD_CANCEL, null, null, false, false);

        tbMdlRN = new RufnummerTableModel();
        tbMdlRN.setData(null);
        AKJTable tbRN = new AKJTable(tbMdlRN, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tbRN.attachSorter();
        tbRN.fitTable(new int[] { 70, 70, 50, 50, 60, 35, 60, 60, 100, 50, 70, 80, 50, 50, 70, 70, 70, 70 });

        TableColumn col = tbRN.getColumnModel().getColumn(RufnummerTableModel.COL_PORTIERUNG_AM);
        col.setCellRenderer(new DateTableCellRenderer(DateTools.PATTERN_DAY_MONTH_YEAR));
        TableColumn col2 = tbRN.getColumnModel().getColumn(RufnummerTableModel.COL_PORTIERUNG_VON);
        col2.setCellRenderer(new DateTableCellRenderer(DateTools.PATTERN_TIME));
        TableColumn col3 = tbRN.getColumnModel().getColumn(RufnummerTableModel.COL_PORTIERUNG_BIS);
        col3.setCellRenderer(new DateTableCellRenderer(DateTools.PATTERN_TIME));

        AKJScrollPane spTable = new AKJScrollPane(tbRN);
        spTable.setPreferredSize(new Dimension(750, 200));

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(spTable, BorderLayout.CENTER);
    }

    private void loadData() {
        SwingWorker<List<Rufnummer>, Void> worker = new SwingWorker<List<Rufnummer>, Void>() {

            @Override
            protected List<Rufnummer> doInBackground() throws Exception {
                RufnummerService rs = getBillingService(RufnummerService.class);
                return rs.findByParam(Rufnummer.STRATEGY_FIND_BY_AUFTRAG_NO_ORIG,
                        new Object[] { auftragNoOrig, Boolean.TRUE });
            }

            @Override
            protected void done() {
                try {
                    List<Rufnummer> rufnummern = get();
                    if ((rufnummern == null) || (rufnummern.isEmpty())) {
                        MessageHelper.showInfoDialog(getMainFrame(), "Es wurden keine Rufnummern zu dem Auftrag ("
                                + auftragNoOrig + ") gefunden!", null, true);
                    }
                    else {
                        tbMdlRN.setData(rufnummern);
                    }
                }
                catch (Exception e) {
                    LOGGER.error(e, e);
                    MessageHelper.showErrorDialog(getMainFrame(), e);
                }
                finally {
                    stopProgressBar();
                    setDefaultCursor();
                }
            }
        };

        setWaitCursor();
        showProgressBar("Rufnummern laden...");
        worker.execute();

    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    @Override
    protected void doSave() {
        prepare4Close();
        setValue(OK_OPTION);
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
}
