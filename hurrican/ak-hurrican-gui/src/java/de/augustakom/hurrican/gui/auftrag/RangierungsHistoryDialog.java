/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.06.2006 15:51:28
 */
package de.augustakom.hurrican.gui.auftrag;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.swing.table.AKTableOwner;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.shared.Equipment4RangierungTableModel;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.service.cc.RangierungsService;


/**
 * Dialog zur Anzeige der History-Datensaetze einer Rangierung.
 *
 *
 */
public class RangierungsHistoryDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent,
        AKTableOwner {

    private static final Logger LOGGER = Logger.getLogger(RangierungsHistoryDialog.class);

    private List<Rangierung> histories = null;

    private AKReflectionTableModel<Rangierung> tbMdlHistories = null;
    private Equipment4RangierungTableModel tbMdlDetails = null;

    /**
     * Default-Const.
     *
     * @param rangierung Rangierung, dessen Historisierungen angezeigt werden sollen
     */
    public RangierungsHistoryDialog(Rangierung rangierung) throws IllegalArgumentException {
        super(null);
        if (rangierung == null) {
            throw new IllegalArgumentException("Rangierung fuer die Historisierung muss angegeben werden!");
        }
        init(rangierung);
    }

    /* Initialisiert den Dialog */
    private void init(Rangierung rangierung) {
        histories = new ArrayList<Rangierung>();
        histories.add(rangierung);
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle("Rangierungs-History");
        configureButton(CMD_CANCEL, "Schliessen", null, true, true);
        configureButton(CMD_SAVE, null, null, false, false);

        tbMdlHistories = new AKReflectionTableModel<Rangierung>(
                new String[] { "Gültig von", "Gültig bis", "ID", "History von" },
                new String[] { "gueltigVon", "gueltigBis", "id", "historyFrom" },
                new Class[] { Date.class, Date.class, Long.class, Long.class });
        AKJTable tbHistories = new AKJTable(tbMdlHistories, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbHistories.addTableListener(this);
        tbHistories.fitTable(new int[] { 80, 80, 90, 90 });
        AKJScrollPane spHistories = new AKJScrollPane(tbHistories, new Dimension(400, 100));

        AKJPanel histPnl = new AKJPanel(new BorderLayout(), "History");
        histPnl.add(spHistories, BorderLayout.CENTER);

        tbMdlDetails = new Equipment4RangierungTableModel();
        AKJTable tbDetails = new AKJTable(tbMdlDetails, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbDetails.fitTable(new int[] { 90, 100, 100, 100, 100 });
        AKJScrollPane spDetails = new AKJScrollPane(tbDetails, new Dimension(510, 265));

        AKJPanel detPnl = new AKJPanel(new BorderLayout(), "Details");
        detPnl.add(spDetails, BorderLayout.CENTER);

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(histPnl, BorderLayout.NORTH);
        getChildPanel().add(detPnl, BorderLayout.CENTER);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        try {
            // in der History nach vorne gehen
            RangierungsService rs = getCCService(RangierungsService.class);
            Rangierung histFuture = histories.get(0);
            while ((histFuture != null) && (histFuture.getHistoryFrom() != null)) {
                histFuture = rs.findRangierung(histFuture.getHistoryFrom());
                if (histFuture != null) {
                    rs.loadEquipments(histFuture);
                    histories.add(histFuture);
                }
            }

            // in der History nach hinten gehen
            Rangierung histPast = null;
            do {
                histPast = rs.findHistoryFrom(histories.get(0).getId());
                if (histPast != null) {
                    rs.loadEquipments(histPast);
                    histories.add(0, histPast);
                }
            }
            while (histPast != null);

            tbMdlHistories.setData(histories);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.table.AKTableOwner#showDetails(java.lang.Object)
     */
    @Override
    public void showDetails(Object details) {
        if (details instanceof Rangierung) {
            try {
                Rangierung rang = (Rangierung) details;
                tbMdlDetails.setRangierung(rang, null);  // TODO: zusaetzliche Rangierung noch ermitteln/angeben
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(this, e);
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
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    @Override
    protected void doSave() {
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

}


