/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.01.2006 12:02:40
 */
package de.augustakom.hurrican.gui.tools.rechnung;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;
import org.quartz.JobDataMap;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.base.AbstractSchedulerPanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.service.cc.RegistryService;


/**
 * Panel fuer den Export der EVNs fuer das Kundenportal Muenchen.
 *
 *
 */
public class EVNExportPanel extends AbstractSchedulerPanel implements AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(EVNExportPanel.class);

    private RechnungBasePanel rechBasePnl = null;
    private AKJTextField tfSrcDirEvnPDF = null;
    private AKJTextField tfSrcDirEvnASCII = null;
    private AKJTextField tfPortalEvnImpPDF = null;
    private AKJTextField tfPortalEvnImpASCII = null;

    private String exportEVNJob = null;

    /**
     * Default-Const.
     */
    public EVNExportPanel() {
        super("de/augustakom/hurrican/gui/tools/rechnung/resources/EVNExportPanel.xml");
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJLabel lblTaifun = getSwingFactory().createLabel("taifun", AKJLabel.LEFT, Font.BOLD);
        AKJLabel lblSrcDirEvnPDF = getSwingFactory().createLabel("taifun.base.dir.pdf");
        AKJLabel lblSrcDirEvnASCII = getSwingFactory().createLabel("taifun.base.dir.ascii");
        AKJLabel lblExport = getSwingFactory().createLabel("export", AKJLabel.LEFT, Font.BOLD);
        AKJLabel lblPortalEvnImpPDF = getSwingFactory().createLabel("portal.evn.import.dir.pdf");
        AKJLabel lblPortalEvnImpASCII = getSwingFactory().createLabel("portal.evn.import.dir.ascii");

        tfSrcDirEvnPDF = getSwingFactory().createTextField("taifun.base.dir.pdf");
        tfSrcDirEvnASCII = getSwingFactory().createTextField("taifun.base.dir.ascii");
        tfPortalEvnImpPDF = getSwingFactory().createTextField("portal.evn.import.dir.pdf");
        tfPortalEvnImpASCII = getSwingFactory().createTextField("portal.evn.import.dir.ascii");

        AKJButton btnStart = getSwingFactory().createButton("start", getActionListener());
        AKJButton btnCancel = getSwingFactory().createButton("cancel", getActionListener());

        rechBasePnl = new RechnungBasePanel();

        //@formatter:off
        AKJPanel taifun = new AKJPanel(new GridBagLayout());
        taifun.add(lblTaifun        , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        taifun.add(lblSrcDirEvnPDF  , GBCFactory.createGBC(  0,  0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        taifun.add(new AKJPanel()   , GBCFactory.createGBC(  0,  0, 1, 1, 1, 1, GridBagConstraints.NONE));
        taifun.add(tfSrcDirEvnPDF   , GBCFactory.createGBC(100,  0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        taifun.add(lblSrcDirEvnASCII, GBCFactory.createGBC(  0,  0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        taifun.add(tfSrcDirEvnASCII , GBCFactory.createGBC(100,  0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel export = new AKJPanel(new GridBagLayout());
        export.add(lblExport           , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        export.add(lblPortalEvnImpPDF  , GBCFactory.createGBC(  0,  0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        export.add(new AKJPanel()      , GBCFactory.createGBC(  0,  0, 1, 1, 1, 1, GridBagConstraints.NONE));
        export.add(tfPortalEvnImpPDF   , GBCFactory.createGBC(100,  0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        export.add(lblPortalEvnImpASCII, GBCFactory.createGBC(  0,  0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        export.add(new AKJPanel()      , GBCFactory.createGBC(  0,  0, 1, 2, 1, 1, GridBagConstraints.NONE));
        export.add(tfPortalEvnImpASCII , GBCFactory.createGBC(100,  0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel btnPnl = new AKJPanel(new GridBagLayout());
        btnPnl.add(btnStart     , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(btnCancel    , GBCFactory.createGBC(  0,  0, 1, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(new AKJPanel(),GBCFactory.createGBC(100,  0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        this.setLayout(new GridBagLayout());
        this.add(rechBasePnl    , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(taifun         , GBCFactory.createGBC(  0,  0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(export         , GBCFactory.createGBC(  0,  0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(btnPnl         , GBCFactory.createGBC(  0,  0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL, new Insets(10,2,2,2)));
        this.add(new AKJPanel() , GBCFactory.createGBC(100,100, 1, 4, 1, 1, GridBagConstraints.BOTH));
        //@formatter:on
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        try {
            RegistryService rs = getCCService(RegistryService.class);
            exportEVNJob = rs.getStringValue(RegistryService.REGID_SCHEDULER_EXPORT_EVN_JOB);
            tfSrcDirEvnPDF.setText(System.getProperty("taifun.base.dir.evn.pdf"));
            tfSrcDirEvnASCII.setText(System.getProperty("taifun.base.dir.evn.ascii"));
            tfPortalEvnImpPDF.setText(System.getProperty("portal.import.evn.path.pdf"));
            tfPortalEvnImpASCII.setText(System.getProperty("portal.import.evn.path.ascii"));
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
        if ("start".equals(command)) {
            startJob();
        }
        else if ("cancel".equals(command)) {
            interruptJob();
        }
    }

    /* Startet den Export-Job. */
    private void startJob() {
        try {
            // pruefen, ob Export-Job gerade ausgefuehrt wird
            if (getSchedulerClient().isJobRunning(exportEVNJob)) {
                throw new HurricanGUIException("Es laeuft gerade ein Rechnungsexport-Job.\n" +
                        "Bitte warten Sie mit dem Export, bis der aktuelle Job beendet wurde.");
            }

            // Job-Parameter erstellen.
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.putAll(rechBasePnl.getParameters());
            jobDataMap.put("src.dir.evn.pdf", tfSrcDirEvnPDF.getText());
            jobDataMap.put("src.dir.evn.ascii", tfSrcDirEvnASCII.getText());
            jobDataMap.put("portal.import.evn.pdf", tfPortalEvnImpPDF.getText());
            jobDataMap.put("portal.import.evn.ascii", tfPortalEvnImpASCII.getText());

            LOGGER.debug(">>>>>>>> Export-Year: " + jobDataMap.get("bill.year"));
            LOGGER.debug(">>>>>>>> Export-Month: " + jobDataMap.get("bill.month"));

            // Signatur-Job triggern
            rechBasePnl.validateParameters(jobDataMap);
            getSchedulerClient().triggerJobWithVolatileTrigger(exportEVNJob, jobDataMap);

            MessageHelper.showInfoDialog(getMainFrame(), "EVN-Export wurde gestartet.\n" +
                            "Nach Ablauf des Exports erhalten Sie eine Info-Mail an die angegebene Adresse.",
                    null, true
            );
        }
        catch (HurricanGUIException e) {
            MessageHelper.showInfoDialog(getMainFrame(), e.getMessage(), null, true);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /* Versucht, den Export-Job zu unterbrechen. */
    private void interruptJob() {
        try {
            int opt = MessageHelper.showYesNoQuestion(getMainFrame(),
                    "Wollen Sie den EVN-Export wirklich abbrechen?",
                    "Export abbrechen?");
            if (opt == JOptionPane.YES_OPTION) {
                getSchedulerClient().interruptJob(exportEVNJob);

                MessageHelper.showInfoDialog(getMainFrame(),
                        "Job wird bei naechster Gelegenheit abgebrochen!", null, true);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

}


