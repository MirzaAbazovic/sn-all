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
 * Panel fuer den Export der Rechnungen und EVNs zum ScanView-Archiv
 *
 *
 */
public class Export4ArchivePanel extends AbstractSchedulerPanel implements AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(Export4ArchivePanel.class);

    private RechnungBasePanel rechBasePnl = null;
    private AKJTextField tfSrcDirBill = null;
    private AKJTextField tfSrcDirEvnPDF = null;
    private AKJTextField tfSrcDirEvnASCII = null;
    private AKJTextField tfArchiveImp = null;
    private AKJTextField tfArchiveDb = null;

    private String exportEVNJob = null;

    /**
     * Default-Const.
     */
    public Export4ArchivePanel() {
        super("de/augustakom/hurrican/gui/tools/rechnung/resources/Export4ArchivePanel.xml");
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJLabel lblTaifun = getSwingFactory().createLabel("taifun", AKJLabel.LEFT, Font.BOLD);
        AKJLabel lblSrcDirBill = getSwingFactory().createLabel("taifun.base.dir.bill");
        AKJLabel lblSrcDirEvnPDF = getSwingFactory().createLabel("taifun.base.dir.evn.pdf");
        AKJLabel lblSrcDirEvnASCII = getSwingFactory().createLabel("taifun.base.dir.evn.ascii");
        AKJLabel lblExport = getSwingFactory().createLabel("export", AKJLabel.LEFT, Font.BOLD);
        AKJLabel lblArchiveImp = getSwingFactory().createLabel("archive.import.dir");
        AKJLabel lblArchiveDb = getSwingFactory().createLabel("archive.db");

        tfSrcDirBill = getSwingFactory().createTextField("taifun.base.dir.bill");
        tfSrcDirEvnPDF = getSwingFactory().createTextField("taifun.base.dir.evn.pdf");
        tfSrcDirEvnASCII = getSwingFactory().createTextField("taifun.base.dir.evn.ascii");
        tfArchiveImp = getSwingFactory().createTextField("archive.import.dir");
        tfArchiveDb = getSwingFactory().createTextField("archive.db");

        AKJButton btnStart = getSwingFactory().createButton("start", getActionListener());
        AKJButton btnCancel = getSwingFactory().createButton("cancel", getActionListener());

        rechBasePnl = new RechnungBasePanel();

        AKJPanel taifun = new AKJPanel(new GridBagLayout());
        taifun.add(lblTaifun, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        taifun.add(lblSrcDirBill, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        taifun.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.NONE));
        taifun.add(tfSrcDirBill, GBCFactory.createGBC(100, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        taifun.add(lblSrcDirEvnPDF, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        taifun.add(tfSrcDirEvnPDF, GBCFactory.createGBC(100, 0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        taifun.add(lblSrcDirEvnASCII, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        taifun.add(tfSrcDirEvnASCII, GBCFactory.createGBC(100, 0, 2, 3, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel export = new AKJPanel(new GridBagLayout());
        export.add(lblExport, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        export.add(lblArchiveImp, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        export.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.NONE));
        export.add(tfArchiveImp, GBCFactory.createGBC(100, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        export.add(lblArchiveDb, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        export.add(tfArchiveDb, GBCFactory.createGBC(100, 0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel btnPnl = new AKJPanel(new GridBagLayout());
        btnPnl.add(btnStart, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(btnCancel, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        this.setLayout(new GridBagLayout());
        this.add(rechBasePnl, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(taifun, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(export, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(btnPnl, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL, new Insets(10, 2, 2, 2)));
        this.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 1, 4, 1, 1, GridBagConstraints.BOTH));
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    public final void loadData() {
        try {
            RegistryService rs = getCCService(RegistryService.class);
            exportEVNJob = rs.getStringValue(RegistryService.REGID_SCHEDULER_EXPORT_ARCHIVE_JOB);

            tfSrcDirBill.setText(System.getProperty("taifun.base.dir.bill"));
            tfSrcDirEvnPDF.setText(System.getProperty("taifun.base.dir.evn.pdf"));
            tfSrcDirEvnASCII.setText(System.getProperty("taifun.base.dir.evn.ascii"));
            tfArchiveImp.setText(System.getProperty("scanview.import.path"));
            tfArchiveDb.setText(rs.getStringValue(RegistryService.REGID_SCANVIEW_DATABASE));
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
            jobDataMap.put("src.dir.bills", tfSrcDirBill.getText());
            jobDataMap.put("src.dir.evn.pdf", tfSrcDirEvnPDF.getText());
            jobDataMap.put("src.dir.evn.ascii", tfSrcDirEvnASCII.getText());
            jobDataMap.put("portal.import", tfArchiveImp.getText());
            jobDataMap.put("archive.db", tfArchiveDb.getText());

            // Signatur-Job triggern
            rechBasePnl.validateParameters(jobDataMap);
            getSchedulerClient().triggerJobWithVolatileTrigger(exportEVNJob, jobDataMap);

            MessageHelper.showInfoDialog(getMainFrame(), "Archiv-Export wurde gestartet.\n" +
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
    public void update(Observable o, Object arg) {
    }

}


