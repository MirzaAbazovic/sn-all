/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.03.2005 13:41:59
 */
package de.augustakom.hurrican.gui.hvt;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.hurrican.gui.base.AbstractDataPanel;
import de.augustakom.hurrican.model.cc.HVTBestellHistory;
import de.augustakom.hurrican.model.cc.HVTBestellung;
import de.augustakom.hurrican.service.cc.HVTToolService;


/**
 * Panel zur Darstellung der Histories zu einer HVT-Bestellung. Die History ist eine Protokollierung ueber die
 * Stift-Aufteilung einer HVT-Bestellung (wann wieviele Stifte von wem vergeben wurden).
 *
 *
 */
public class HVTBestellHistoryPanel extends AbstractDataPanel {

    private static final Logger LOGGER = Logger.getLogger(HVTBestellHistoryPanel.class);

    private AKJTable tbHistory = null;
    private AKReflectionTableModel<HVTBestellHistory> tbMdlHistory = null;

    private HVTBestellung model = null;

    /**
     * Default-Konstruktor.
     */
    public HVTBestellHistoryPanel() {
        super(null);
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        tbMdlHistory = new AKReflectionTableModel<HVTBestellHistory>(
                new String[] { "Leiste", "Anzahl", "Bearbeiter", "Datum" },
                new String[] { "leiste", "anzahl", "bearbeiter", "datum" },
                new Class[] { String.class, String.class, String.class, Date.class });
        tbHistory = new AKJTable(tbMdlHistory, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tbHistory.fitTable(new int[] { 65, 65, 100, 70 });
        AKJScrollPane spTable = new AKJScrollPane(tbHistory);
        spTable.setPreferredSize(new Dimension(325, 250));

        this.setLayout(new BorderLayout());
        this.add(spTable, BorderLayout.CENTER);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#setModel(java.util.Observable)
     */
    @Override
    public void setModel(Observable model) {
        this.model = null;
        if (model instanceof HVTBestellung) {
            this.model = (HVTBestellung) model;
        }

        readModel();
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#readModel()
     */
    @Override
    public void readModel() {
        tbMdlHistory.removeAll();
        if (model != null) {
            try {
                setWaitCursor();
                tbHistory.setWaitCursor();

                HVTToolService hts = getCCService(HVTToolService.class);
                LOGGER.info(">>>>>>>> bestell-ID: " + model.getId());
                List<HVTBestellHistory> histories = hts.findHVTBestellHistories(model.getId());
                tbMdlHistory.setData(histories);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
            finally {
                tbHistory.setDefaultCursor();
                setDefaultCursor();
            }
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#saveModel()
     */
    @Override
    public void saveModel() throws AKGUIException {
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#getModel()
     */
    @Override
    public Object getModel() {
        return null;
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#hasModelChanged()
     */
    @Override
    public boolean hasModelChanged() {
        return false;
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

}


