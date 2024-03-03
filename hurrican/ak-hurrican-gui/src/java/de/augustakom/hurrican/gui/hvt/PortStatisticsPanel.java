/**
 * Copyright (c) 2010 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.02.2010 16:40:45
 */
package de.augustakom.hurrican.gui.hvt;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKFilterTableModel;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.swing.table.AKTableSorter;
import de.augustakom.common.gui.swing.table.FilterOperator;
import de.augustakom.common.gui.swing.table.FilterOperators;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.model.cc.view.PortStatisticsView;
import de.augustakom.hurrican.service.cc.PortStatisticService;

/**
 * Panel für die Anzeige des Portverbrauchs.
 */
public class PortStatisticsPanel extends AbstractServicePanel implements AKDataLoaderComponent, ItemListener {

    private static final Logger LOGGER = Logger.getLogger(PortStatisticsPanel.class);

    private AKJTable tbStatistics;
    private AKJComboBox cbFilterGroup;
    private AKJComboBox cbFilterMonth;
    private AKReflectionTableModel<PortStatisticsView> tbMdlStatistics;

    /**
     * Default-Const.
     */
    public PortStatisticsPanel() {
        super("de/augustakom/hurrican/gui/hvt/resources/PortStatisticsPanel.xml");
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJLabel lbFilterGroup = getSwingFactory().createLabel("filter.group");
        AKJLabel lbFilterMonth = getSwingFactory().createLabel("filter.month");

        cbFilterGroup = getSwingFactory().createComboBox("filter.group");
        cbFilterGroup.addItemListener(this);
        cbFilterMonth = getSwingFactory().createComboBox("filter.month");
        cbFilterMonth.addItemListener(this);

        tbMdlStatistics = new AKReflectionTableModel<PortStatisticsView>(
                new String[] { "Niederlassung", "ONKZ", "ASB", "Monat", "Analog", "ISDN", "ISDN Tk-Anlagen", "Summe", "Diff", "%", "ADSL", "Diff", "%", "SDSL", "PMX" },
                new String[] { "niederlassung", "onkz", "asb", "monat", "analogPorts", "isdnPorts", "isdnTkPorts", "telephonySumme", "telephonyDiff", "telephonyProzent",
                        "adslPorts", "adslDiff", "adslProzent", "sdslPorts", "pmxPorts" },
                new Class<?>[] { String.class, String.class, Number.class, String.class, Number.class, Number.class, Number.class, Number.class,
                        Number.class, Number.class, Number.class, Number.class, Number.class, Number.class, Number.class }
        );
        tbStatistics = new AKJTable(tbMdlStatistics);
        tbStatistics.attachSorter();

        AKJScrollPane spStatistics = new AKJScrollPane(tbStatistics);

        AKJPanel filterPanel = new AKJPanel(new GridBagLayout(), "Filter");
        filterPanel.add(lbFilterGroup, GBCFactory.createGBC(10, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        filterPanel.add(cbFilterGroup, GBCFactory.createGBC(90, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        filterPanel.add(lbFilterMonth, GBCFactory.createGBC(10, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        filterPanel.add(cbFilterMonth, GBCFactory.createGBC(90, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        this.setLayout(new GridBagLayout());
        this.add(filterPanel, GBCFactory.createGBC(100, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(spStatistics, GBCFactory.createGBC(100, 100, 0, 1, 1, 1, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5)));
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        try {
            PortStatisticService statService = getCCService(PortStatisticService.class);
            List<PortStatisticsView> statistics = statService.retrievePortStatistics();
            tbMdlStatistics.setData(statistics);
            Set<String> groups = new TreeSet<String>();
            Set<String> months = new TreeSet<String>();
            for (PortStatisticsView view : statistics) {
                groups.add(view.getNiederlassung() + "/" + view.getOnkz() + "/" + view.getAsb());
                months.add(view.getMonat());
            }
            cbFilterGroup.addItem(null);
            cbFilterGroup.addItems(groups);
            cbFilterMonth.addItem(null);
            cbFilterMonth.addItems(months);
        }
        catch (ServiceNotFoundException ex) {
            MessageHelper.showErrorDialog(this, ex);
        }
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

    @Override
    public void itemStateChanged(ItemEvent e) {
        AKFilterTableModel filterModel = (AKFilterTableModel) ((AKTableSorter) tbStatistics.getModel()).getModel();
        filterModel.clearFilter();
        if (cbFilterGroup.getSelectedItem() != null) {
            String filterGroup = (String) cbFilterGroup.getSelectedItem();
            String[] parts = filterGroup.split("/");
            filterModel.addFilter(new FilterOperator(FilterOperators.EQ, parts[0], 0));
            filterModel.addFilter(new FilterOperator(FilterOperators.EQ, parts[1], 1));
            filterModel.addFilter(new FilterOperator(FilterOperators.EQ, Integer.valueOf(parts[2]), 2));
        }
        if (cbFilterMonth.getSelectedItem() != null) {
            filterModel.addFilter(new FilterOperator(FilterOperators.EQ, cbFilterMonth.getSelectedItem(), 3));
        }
    }
}
