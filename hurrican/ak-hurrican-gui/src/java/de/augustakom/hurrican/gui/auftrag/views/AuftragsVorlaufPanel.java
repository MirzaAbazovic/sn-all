/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.02.2005 11:08:25
 */
package de.augustakom.hurrican.gui.auftrag.views;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.event.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJInternalFrame;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJToggleButton;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKFilterTableModel;
import de.augustakom.common.gui.swing.table.AKFilterTableModelListener;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.swing.table.AKTableDoubleClickMouseListener;
import de.augustakom.common.gui.swing.table.AKTableSorter;
import de.augustakom.common.gui.swing.table.FilterOperator;
import de.augustakom.common.gui.swing.table.FilterOperators;
import de.augustakom.hurrican.gui.auftrag.AuftragDataFrame;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.model.shared.view.AuftragVorlaufView;
import de.augustakom.hurrican.service.cc.CCAuftragService;


/**
 * Panel zur Darstellung aller Auftraege, die noch zu realisieren sind.
 *
 *
 */
public class AuftragsVorlaufPanel extends AbstractServicePanel implements AKDataLoaderComponent,
        AKObjectSelectionListener, AKFilterTableModelListener {

    private static final Logger LOGGER = Logger.getLogger(AuftragsVorlaufPanel.class);

    private static final String FILTER_NAME = "ZellenFilter";

    // GUI-Komponenten
    private AKJButton btnRefresh = null;
    private AKJToggleButton btnFilter = null;
    private AKJTable tbVorlauf = null;
    private AKReflectionTableModel<AuftragVorlaufView> tbMdlVorlauf = null;

    private Icon iconFilterApply = null;
    private Icon iconFilterUndo = null;

    /**
     * Default-Konstruktor.
     */
    public AuftragsVorlaufPanel() {
        super("de/augustakom/hurrican/gui/auftrag/resources/AuftragsVorlaufPanel.xml");
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        btnRefresh = getSwingFactory().createButton("refresh", getActionListener(), null);
        btnFilter = getSwingFactory().createToggleButton("filter", getActionListener(), false);
        btnFilter.setBorder(null);

        iconFilterApply = getSwingFactory().getIcon("de/augustakom/hurrican/gui/images/filter.gif");
        iconFilterUndo = getSwingFactory().getIcon("de/augustakom/hurrican/gui/images/filter_undo.gif");

        AKJPanel btnPnl = new AKJPanel(new GridBagLayout());
        btnPnl.add(btnRefresh, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(btnFilter, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        tbMdlVorlauf = new AKReflectionTableModel<AuftragVorlaufView>(
                new String[] { "Vorgabe AM", "Name", "Vorname", "Real.-Datum", "Auftragsart", VerbindungsBezeichnung.VBZ_BEZEICHNUNG, "Produkt",
                        "Endstelle", "ES-Plz", "ES-Ort", "Schnittstelle", "Leitungsart",
                        "Auftrag-ID (CC)", "Order__No", "Bearbeiter" },
                new String[] { "vorgabeSCV", "name", "vorname", "realisierungstermin", "anlass", "vbz", "anschlussart",
                        "endstelle", "endstellePLZ", "endstelleOrt", "schnittstelle", "leitungsart",
                        "auftragId", "auftragNoOrig", "uebernommenVon" },
                new Class[] { Date.class, String.class, String.class, Date.class, String.class, String.class, String.class,
                        String.class, String.class, String.class, String.class, String.class,
                        Long.class, Long.class, String.class }
        );
        tbMdlVorlauf.addFilterTableModelListener(this);
        tbVorlauf = new AKJTable(tbMdlVorlauf, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tbVorlauf.attachSorter();
        tbVorlauf.addMouseListener(new AKTableDoubleClickMouseListener(this));
        tbVorlauf.fitTable(new int[] { 75, 100, 70, 75, 90, 90, 110, 100, 40, 100, 90, 90, 90, 90, 100 });
        AKJScrollPane spTable = new AKJScrollPane(tbVorlauf);

        this.setLayout(new BorderLayout());
        this.add(btnPnl, BorderLayout.NORTH);
        this.add(spTable, BorderLayout.CENTER);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        setWaitCursor();
        showProgressBar("lade Aufträge...");
        tbMdlVorlauf.setData(null);
        enableButtons(false);

        final SwingWorker<List<AuftragVorlaufView>, Void> worker = new SwingWorker<List<AuftragVorlaufView>, Void>() {

            @Override
            protected List<AuftragVorlaufView> doInBackground() throws Exception {
                CCAuftragService as = getCCService(CCAuftragService.class);
                List<AuftragVorlaufView> result = as.findAuftragsVorlauf();
                return result;
            }

            @Override
            protected void done() {
                try {
                    List<AuftragVorlaufView> result = get();
                    tbMdlVorlauf.setData(result);
                    refreshRowCount();
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    MessageHelper.showErrorDialog(getMainFrame(), e);
                }
                finally {
                    enableButtons(true);
                    btnFilter.setSelected(false);
                    stopProgressBar();
                    setDefaultCursor();
                }
            }


        };
        worker.execute();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if ("refresh".equals(command)) {
            loadData();
        }
        else if ("filter".equals(command)) {
            applyFilter2Table();
        }
    }

    /**
     * Veranlasst das TableModel dazu, die Daten zu filtern.
     */
    protected void applyFilter2Table() {
        try {
            AKFilterTableModel model = ((AKFilterTableModel) ((AKTableSorter) tbVorlauf.getModel()).getModel());
            if (btnFilter.isSelected()) {
                if (tbVorlauf.getSelectedRow() < 0) {
                    MessageHelper.showInfoDialog(this,
                            "Bitte selektieren Sie zuerst eine Zelle, nach deren Inhalt gefiltert werden soll.", null, true);
                    return;
                }

                model.addFilter(new FilterOperator(FILTER_NAME, FilterOperators.EQ,
                        tbVorlauf.getModel().getValueAt(tbVorlauf.getSelectedRow(), tbVorlauf.getSelectedColumn()),
                        tbVorlauf.getSelectedColumn()));
                btnFilter.setIcon(iconFilterUndo);
            }
            else {
                model.removeFilter(FILTER_NAME);
                tbVorlauf.editingCanceled(new ChangeEvent(this));
                btnFilter.setIcon(iconFilterApply);
            }
        }
        finally {
            refreshRowCount();
            btnFilter.repaint();
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKObjectSelectionListener#objectSelected(java.lang.Object)
     */
    @Override
    public void objectSelected(Object selection) {
        if (selection instanceof CCAuftragModel) {
            AuftragDataFrame.openFrame((CCAuftragModel) selection);
        }
    }

    /**
     * Aktualisiert den Row-Count im Frame-Header.
     */
    protected void refreshRowCount() {
        Container parent = this.getParent();
        while (parent != null) {
            if (parent instanceof AKJInternalFrame) {
                AKJInternalFrame frame = (AKJInternalFrame) parent;
                StringBuilder newTitle = new StringBuilder("Auftrags-Vorlauf");
                newTitle.append(" - Anzahl: ");
                newTitle.append(tbMdlVorlauf.getRowCount());
                frame.setTitle(newTitle.toString());
                break;
            }
            else {
                parent = parent.getParent();
            }
        }
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

    /* Setzt die Buttons auf enabled/disabled */
    private void enableButtons(boolean enable) {
        btnFilter.setEnabled(enable);
        btnRefresh.setEnabled(enable);
    }

    /**
     * @see de.augustakom.common.gui.swing.table.AKFilterTableModelListener#tableFiltered()
     */
    @Override
    public void tableFiltered() {
        refreshRowCount();
    }

}


