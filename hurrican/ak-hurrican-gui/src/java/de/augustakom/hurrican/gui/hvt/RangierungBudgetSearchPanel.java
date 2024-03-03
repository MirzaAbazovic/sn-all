/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.10.2007 10:28:39
 */
package de.augustakom.hurrican.gui.hvt;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.iface.AKSearchComponent;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKReferenceField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKReferenceAwareTableModel;
import de.augustakom.common.gui.swing.table.AKTableDoubleClickMouseListener;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.service.iface.ISimpleFindService;
import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.model.cc.RangierungsAuftrag;
import de.augustakom.hurrican.model.cc.query.RangierungsAuftragBudgetQuery;
import de.augustakom.hurrican.model.cc.view.HVTGruppeStdView;
import de.augustakom.hurrican.model.cc.view.RangierungsAuftragBudgetView;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.QueryCCService;
import de.augustakom.hurrican.service.cc.RangierungAdminService;


/**
 * Panel fuer die Suche von Rangierungs-Budgets (Budgets fuer die Erweiterung von HVTs).
 *
 *
 */
public class RangierungBudgetSearchPanel extends AbstractServicePanel implements AKSearchComponent,
        AKDataLoaderComponent, AKObjectSelectionListener {

    private static final Logger LOGGER = Logger.getLogger(RangierungBudgetSearchPanel.class);

    private AKReferenceField rfHVT = null;
    private AKJDateComponent dcFaelligVon = null;
    private AKJDateComponent dcFaelligBis = null;
    private AKReferenceAwareTableModel<RangierungsAuftragBudgetView> tbMdlResult = null;

    /**
     * Default-Const.
     */
    public RangierungBudgetSearchPanel() {
        super("de/augustakom/hurrican/gui/hvt/resources/RangierungBudgetSearchPanel.xml");
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJLabel lblHVT = getSwingFactory().createLabel("hvt.standort");
        AKJLabel lblFaelligVon = getSwingFactory().createLabel("faellig.von");
        AKJLabel lblFaelligBis = getSwingFactory().createLabel("faellig.bis");
        AKJLabel lblResult = getSwingFactory().createLabel("result");

        rfHVT = getSwingFactory().createReferenceField("hvt.standort");
        dcFaelligVon = getSwingFactory().createDateComponent("faellig.von");
        dcFaelligBis = getSwingFactory().createDateComponent("faellig.bis");
        AKJButton btnSearch = getSwingFactory().createButton("search", getActionListener());

        AKJPanel top = new AKJPanel(new GridBagLayout(), "Suchparameter");
        top.add(lblHVT, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        top.add(rfHVT, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(lblFaelligVon, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(dcFaelligVon, GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(lblFaelligBis, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(dcFaelligBis, GBCFactory.createGBC(0, 0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(btnSearch, GBCFactory.createGBC(0, 0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL, 10));
        top.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 4, 2, 1, 1, GridBagConstraints.HORIZONTAL));

        tbMdlResult = new AKReferenceAwareTableModel<RangierungsAuftragBudgetView>(
                new String[] { "ID", "HVT", "Fällig am", "Rangiert am",
                        "IA-Nummer", "Budget", "aktiviert am", "geschlossen am" },
                new String[] { "rangierungsAuftragId", "hvtStandortId", "faelligAm", "ausgefuehrtAm",
                        "iaNummer", "budget", "budgetCreatedAt", "budgetClosedAt" },
                new Class[] { Long.class, String.class, Date.class,
                        Date.class, String.class, Float.class, Date.class, Date.class }
        );
        AKJTable tbResult = new AKJTable(tbMdlResult, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbResult.attachSorter();
        tbResult.addMouseListener(new AKTableDoubleClickMouseListener(this));
        tbResult.fitTable(new int[] { 45, 180, 75, 85, 100, 70, 75, 85 });
        AKJScrollPane spResult = new AKJScrollPane(tbResult, new Dimension(700, 400));

        AKJPanel result = new AKJPanel(new GridBagLayout());
        result.add(lblResult, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        result.add(spResult, GBCFactory.createGBC(100, 100, 0, 1, 2, 1, GridBagConstraints.BOTH));

        this.setLayout(new BorderLayout());
        this.add(top, BorderLayout.NORTH);
        this.add(result, BorderLayout.CENTER);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        try {
            HVTService hvts = getCCService(HVTService.class);
            List<HVTGruppeStdView> hvtViews = hvts.findHVTViews();

            ISimpleFindService sfs = getCCService(QueryCCService.class);
            rfHVT.setFindService(sfs);

            tbMdlResult.addReference(1,
                    CollectionMapConverter.convert2Map(hvtViews, "getHvtIdStandort", null), "name");
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if ("search".equals(command)) {
            doSearch();
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKObjectSelectionListener#objectSelected(java.lang.Object)
     */
    @Override
    public void objectSelected(Object selection) {
        if (selection instanceof RangierungsAuftragBudgetView) {
            try {
                RangierungsAuftragBudgetView view = (RangierungsAuftragBudgetView) selection;

                RangierungAdminService ras = getCCService(RangierungAdminService.class);
                RangierungsAuftrag ra = ras.findRA(view.getRangierungsAuftragId());

                RangierungBudgetFrame rbf = new RangierungBudgetFrame(ra);
                getMainFrame().registerFrame(rbf, false);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.IFilterOwner#clearFilter()
     */
    public void clearFilter() {
        GuiTools.cleanFields(this);
    }

    /**
     * @see de.augustakom.hurrican.gui.base.IFilterOwner#doSearch(de.augustakom.common.gui.swing.AKJTable)
     */
    @Override
    public void doSearch() {
        try {
            setWaitCursor();
            tbMdlResult.setData(null);

            RangierungAdminService ras = getCCService(RangierungAdminService.class);
            List<RangierungsAuftragBudgetView> result =
                    ras.findRABudgetViews(getFilter());
            tbMdlResult.setData(result);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        finally {
            setDefaultCursor();
        }
    }

    private RangierungsAuftragBudgetQuery getFilter() {
        RangierungsAuftragBudgetQuery query = new RangierungsAuftragBudgetQuery();
        query.setHvtStandortId(rfHVT.getReferenceIdAs(Long.class));
        query.setFaelligVon(dcFaelligVon.getDate(null));
        query.setFaelligBis(dcFaelligBis.getDate(null));
        return query;
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

}


