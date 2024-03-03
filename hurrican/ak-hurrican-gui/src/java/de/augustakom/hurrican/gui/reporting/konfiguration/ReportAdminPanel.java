/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.03.2007 14:26:05
 */
package de.augustakom.hurrican.gui.reporting.konfiguration;

import java.awt.*;
import java.beans.*;
import java.util.*;
import javax.swing.event.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.iface.AKNavigationBarListener;
import de.augustakom.common.gui.swing.AKJTabbedPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.table.AKTableSorter;
import de.augustakom.hurrican.gui.base.AbstractAdminPanel;
import de.augustakom.hurrican.model.reporting.Report;


/**
 * Admin-Panel fuer die Verwaltung von Reports.
 *
 *
 */
public class ReportAdminPanel extends AbstractAdminPanel implements ChangeListener, AKNavigationBarListener {

    private static final Logger LOGGER = Logger.getLogger(ReportAdminPanel.class);

    /* Aktuelles Modell, das ueber die Methode showDetails(Object) gesetzt wird. */
    private Report model = null;

    // GUI-Komponenten
    private AKJTable reportTable = null;
    private AKJTabbedPane tabbedPane = null;

    // Panel fuer die Report-Details
    private ReportDetailPanel repDetPnl = null;
    private Report2CmdPanel rep2CmdPnl = null;
    private Report2ProdPanel rep2ProdPnl = null;
    private Report2TechLsPanel rep2TechLsPnl = null;
    private ReportTemplatePanel repTempPnl = null;
    private Report2TxtBausteinGruppePanel rep2TxtPnl = null;
    private Report2UserPanel rep2UsrPnl = null;
    private ReportGroupPanel repGrpPnl = null;

    /**
     * Konstruktor
     */
    public ReportAdminPanel() {
        super(null);
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    protected final void createGUI() {
        // Erzeuge Sub-Panels
        repDetPnl = new ReportDetailPanel(this);
        rep2CmdPnl = new Report2CmdPanel();
        rep2ProdPnl = new Report2ProdPanel();
        rep2TechLsPnl = new Report2TechLsPanel();
        repTempPnl = new ReportTemplatePanel();
        rep2TxtPnl = new Report2TxtBausteinGruppePanel();
        rep2UsrPnl = new Report2UserPanel();
        repGrpPnl = new ReportGroupPanel();

        // Sub-Panels zu AKJTabbedPane hinzufügen
        tabbedPane = new AKJTabbedPane();
        tabbedPane.addTab("Report-Details", repDetPnl);
        tabbedPane.addTab("Commands", rep2CmdPnl);
        tabbedPane.addTab("Produkt-Status", rep2ProdPnl);
        tabbedPane.addTab("Techn. Leistungen", rep2TechLsPnl);
        tabbedPane.addTab("Benutzer-Rollen", rep2UsrPnl);
        tabbedPane.addTab("Vorlage", repTempPnl);
        tabbedPane.addTab("Text-Bausteingruppen", rep2TxtPnl);
        tabbedPane.addTab("Report-Gruppe", repGrpPnl);

        this.setLayout(new BorderLayout());
        this.add(tabbedPane, BorderLayout.CENTER);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKNavigationBarListener#showNavigationObject(java.lang.Object, int)
     */
    public void showNavigationObject(Object obj, int number) throws PropertyVetoException {
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#showDetails(java.lang.Object)
     */
    public void showDetails(Object details) {
        if (details instanceof Report) {
            this.model = (Report) details;
            try {
                // ausgewaehlten Report den Sub-Panels uebergeben
                repDetPnl.setModel(model);
                rep2CmdPnl.setModel(model);
                rep2ProdPnl.setModel(model);
                rep2TechLsPnl.setModel(model);
                repTempPnl.setModel(model);
                rep2TxtPnl.setModel(model);
                rep2UsrPnl.setModel(model);
                repGrpPnl.setModel(model);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#loadData()
     */
    public final void loadData() {
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#createNew()
     */
    public void createNew() {
        // Erzeuge Report und übergebe dies an die Sub-Panels zur Anzeige
        this.model = new Report();

        // Neues Produkt an die Sub-Panels uebergeben
        repDetPnl.setModel(model);
        rep2CmdPnl.setModel(model);
        rep2ProdPnl.setModel(model);
        rep2TechLsPnl.setModel(model);
        repTempPnl.setModel(model);
        rep2TxtPnl.setModel(model);
        rep2UsrPnl.setModel(model);
        repGrpPnl.setModel(model);

        // Setze Focus auf erstes Tab und oberstes Textfeld
        tabbedPane.setSelectedIndex(0);
    }

    /**
     * Uebergibt dem Admin-Panel die Tabelle, die eine Uebersicht ueber die Reports darstellt.
     *
     * @param tableModel
     */
    protected void setReportTable(AKJTable table) {
        this.reportTable = table;
    }

    /**
     * Funktion liefert die Übersicht aller Reprots zurück.
     *
     * @return
     *
     */
    protected AKJTable getReportTable() {
        return reportTable;
    }

    /**
     * Daten im TableModel wurden verändert.
     */
    protected void tableChanged() {
        ((AKTableSorter) reportTable.getModel()).fireTableDataChanged();
    }

    /**
     * Funktion fügt einen Report dem TableModel hinzu.
     *
     * @param rep
     *
     */
    protected void addReport(Report rep) {
        if (!((AKTableSorter) reportTable.getModel()).getData().contains(rep)) {
            ((AKTableSorter) reportTable.getModel()).addObject(rep);
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#saveData()
     */
    public void saveData() {
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    protected void execute(String command) {
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {
    }

    /**
     * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
     */
    public void stateChanged(ChangeEvent e) {
    }
}
