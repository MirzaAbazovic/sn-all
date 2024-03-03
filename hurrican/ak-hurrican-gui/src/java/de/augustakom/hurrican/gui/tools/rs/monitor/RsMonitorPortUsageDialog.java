/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.01.2010 08:28:58
 */
package de.augustakom.hurrican.gui.tools.rs.monitor;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKTableModelXML;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.cc.RsmPortUsage;
import de.augustakom.hurrican.model.cc.RsmRangCount;
import de.augustakom.hurrican.service.cc.MonitorService;


/**
 * Dialog, zur Anzeige des Port-Verbrauchs der letzten x Monate.
 *
 *
 */
public class RsMonitorPortUsageDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(RsMonitorPortUsageDialog.class);

    private static final String DIALOG_TITLE = "dialog.title";
    private static final String PROGRESS_TEXT = "progress.text";
    private static final long serialVersionUID = 5266455832654569555L;

    private AKJTable rsMonitorPortUsageTable;
    private final RsmRangCount rsmRangCountModel;

    /**
     * Konstruktor fuer den Dialog zur Anzeige des Port-Verbrauchs letzter x Monate.
     *
     * @param zvStandortView
     */
    public RsMonitorPortUsageDialog(RsmRangCount rsmRangCountModel) {
        super("de/augustakom/hurrican/gui/tools/rs/monitor/resources/RsMonitorPortUsageDialog.xml", true, false);
        this.rsmRangCountModel = rsmRangCountModel;

        if (rsmRangCountModel == null) {
            throw new IllegalArgumentException("Es muss ein RsmRangCountModel angegeben werden.");
        }

        createGUI();
        loadData();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected final void createGUI() {
        setTitle(StringTools.formatString(getSwingFactory().getText(DIALOG_TITLE),
                new String[] {
                        String.format("%s", RsmPortUsage.PORT_USAGE_MONTH_COUNT),
                        String.format("%s", rsmRangCountModel.getAverageUsage()) }
        ));

        rsMonitorPortUsageTable = new AKJTable();
        rsMonitorPortUsageTable.setModel(new AKTableModelXML<RsmPortUsage>("de/augustakom/hurrican/gui/tools/rs/monitor/resources/RsMonitorPortUsageTable.xml"));
        rsMonitorPortUsageTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        rsMonitorPortUsageTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        rsMonitorPortUsageTable.fitTable(((AKTableModelXML<RsmPortUsage>) rsMonitorPortUsageTable.getModel()).getFitList());

        AKJScrollPane tableSP = new AKJScrollPane(rsMonitorPortUsageTable);
        tableSP.setPreferredSize(new Dimension(350, 130));

        AKJPanel detailPanel = new AKJPanel(new GridBagLayout());
        detailPanel.add(tableSP, GBCFactory.createGBC(100, 85, 0, 2, 4, 1, GridBagConstraints.BOTH));

        getChildPanel().add(detailPanel);
    }

    @Override
    public final void loadData() {
        RsMonitorPortUsageLoader rsMonitorPortUsageLoader = new RsMonitorPortUsageLoader();
        rsMonitorPortUsageLoader.execute();
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

    @Override
    protected void doSave() {
    }

    /*
     * Klasse zum Laden der Daten
     */
    class RsMonitorPortUsageLoader extends SwingWorker<List<RsmPortUsage>, Void> {
        RsMonitorPortUsageLoader() {
            setWaitCursor();
            showProgressBar(getSwingFactory().getText(PROGRESS_TEXT));
        }

        /**
         * @see javax.swing.SwingWorker#doInBackground()
         */
        @Override
        protected List<RsmPortUsage> doInBackground() throws Exception {
            MonitorService ms = getCCService(MonitorService.class);

            return ms.findRsmPortUsage(
                    rsmRangCountModel.getHvtStandortId(),
                    rsmRangCountModel.getKvzNummer(),
                    rsmRangCountModel.getPhysiktyp(),
                    rsmRangCountModel.getPhysiktypAdd());
        }

        /**
         * @see javax.swing.SwingWorker#done()
         */
        @Override
        protected void done() {
            try {
                List<RsmPortUsage> data = get();

                @SuppressWarnings("unchecked")
                AKTableModelXML<RsmPortUsage> tableModel = (AKTableModelXML<RsmPortUsage>) rsMonitorPortUsageTable.getModel();
                tableModel.setData(data);

                if ((null != data) && (!data.isEmpty())) {
                    rsMonitorPortUsageTable.changeSelection(0, 0, Boolean.FALSE, Boolean.FALSE);
                }
            }
            catch (CancellationException e) {
                LOGGER.debug("RsMonitorPortUsageLoader canceled", e);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
            }
            finally {
                stopProgressBar();
                setDefaultCursor();
            }
        }
    }

}
