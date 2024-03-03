/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.03.2007 08:53:18
 */
package de.augustakom.hurrican.gui.reporting.konfiguration;

import java.awt.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.hurrican.gui.base.AbstractAdminPanel;
import de.augustakom.hurrican.model.reporting.Report;
import de.augustakom.hurrican.service.reporting.ReportConfigService;


/**
 * Panel fuer die Administration der Reports.
 *
 *
 */
public class ReportTablePanel extends AbstractAdminPanel {

    private static final Logger LOGGER = Logger.getLogger(ReportTablePanel.class);

    private AbstractAdminPanel detailPanel = null;

    private AKJTable tbReports = null;
    private AKReflectionTableModel<Report> tbModelReports = null;


    /**
     * Standardkonstruktor
     */
    public ReportTablePanel(AbstractAdminPanel detailPanel) {
        super(null);
        this.detailPanel = detailPanel;
        createGUI();
    }

    /**
     * Gibt die Tabelle zurueck.
     *
     * @return
     */
    protected AKJTable getReportTable() {
        return tbReports;
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        // Table für Reports
        tbModelReports = new AKReflectionTableModel<Report>(
                new String[] { "ID", "Name", "User", "Beschreibung" },
                new String[] { "id", "name", "userw", "description" },
                new Class[] { Long.class, String.class, String.class, String.class });

        tbReports = new AKJTable(tbModelReports, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbReports.attachSorter();
        tbReports.addTableListener(this);
        tbReports.fitTable(new int[] { 50, 350, 70, 400 });
        AKJScrollPane tableSP = new AKJScrollPane(tbReports, new Dimension(900, 150));

        this.setLayout(new BorderLayout());
        this.add(tableSP, BorderLayout.CENTER);
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#loadData()
     */
    @Override
    public final void loadData() {
        try {
            setWaitCursor();

            ReportConfigService rs = getReportService(ReportConfigService.class);
            List<Report> reports = rs.findReports();
            tbModelReports.setData(reports);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        finally {
            setDefaultCursor();
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#showDetails(java.lang.Object)
     */
    @Override
    public void showDetails(Object details) {
        detailPanel.showDetails(details);
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#createNew()
     */
    @Override
    public void createNew() {
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#saveData()
     */
    @Override
    public void saveData() {
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
