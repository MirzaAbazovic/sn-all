/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.01.2006 15:09:35
 */
package de.augustakom.hurrican.gui.tools.rechnung;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;
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
 * Panel, um den Rechnungskopiervorgang zu starten. <br> In dem Panel koennen/muessen div. Parameter fuer den Job
 * eingetragen werden.
 *
 *
 */
public class RechnungKopierPanel extends AbstractSchedulerPanel implements AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(RechnungKopierPanel.class);

    private RechnungBasePanel rechBasePnl = null;
    private AKJTextField tfTBaseDirBill = null;
    private AKJTextField tfPortalBase = null;
    private AKJTextField tfPortalBillImp = null;

    private String signatureJob = null;

    public RechnungKopierPanel() {
        super("de/augustakom/hurrican/gui/tools/rechnung/resources/RechnungKopierPanel.xml");
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJLabel lblTaifun = getSwingFactory().createLabel("taifun", AKJLabel.LEFT, Font.BOLD);
        AKJLabel lblTBaseDirBill = getSwingFactory().createLabel("taifun.base.dir");
        AKJLabel lblPortalBase = getSwingFactory().createLabel("portal.base.dir");
        AKJLabel lblExport = getSwingFactory().createLabel("export", AKJLabel.LEFT, Font.BOLD);
        AKJLabel lblPortalBillImp = getSwingFactory().createLabel("portal.bill.import.dir");

        tfTBaseDirBill = getSwingFactory().createTextField("taifun.base.dir");
        tfPortalBase = getSwingFactory().createTextField("portal.base.dir");
        tfPortalBillImp = getSwingFactory().createTextField("portal.bill.import.dir");
        AKJButton btnStart = getSwingFactory().createButton("start", getActionListener());
        AKJButton btnCancel = getSwingFactory().createButton("cancel", getActionListener());

        rechBasePnl = new RechnungBasePanel();
        AKJPanel taifun = new AKJPanel(new GridBagLayout());
        taifun.add(lblTaifun, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        taifun.add(lblTBaseDirBill, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        taifun.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.NONE));
        taifun.add(tfTBaseDirBill, GBCFactory.createGBC(100, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel exp = new AKJPanel(new GridBagLayout());
        exp.add(lblExport, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        exp.add(lblPortalBase, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        exp.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.NONE));
        exp.add(tfPortalBase, GBCFactory.createGBC(100, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        exp.add(lblPortalBillImp, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        exp.add(tfPortalBillImp, GBCFactory.createGBC(100, 0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel btnPnl = new AKJPanel(new GridBagLayout());
        btnPnl.add(btnStart, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(btnCancel, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        this.setLayout(new GridBagLayout());
        this.add(rechBasePnl, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(taifun, GBCFactory.createGBC(100, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(exp, GBCFactory.createGBC(100, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(btnPnl, GBCFactory.createGBC(0, 0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL, new Insets(10, 2, 2, 2)));
        this.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 1, 5, 1, 1, GridBagConstraints.BOTH));

    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        try {
            RegistryService rs = getCCService(RegistryService.class);

            tfTBaseDirBill.setText(System.getProperty("taifun.base.dir.bill"));
            tfPortalBase.setText(System.getProperty("portal.import.base.path"));
            tfPortalBillImp.setText(System.getProperty("portal.import.bill.path"));

            signatureJob = rs.getStringValue(RegistryService.REGID_SCHEDULER_COPY_INVOICE_JOB);
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
            startSignature();
        }
        else if ("cancel".equals(command)) {
            interruptSignature();
        }
    }

    /*
     * Startet die Rechnungs-Signierung fuer den angegebenen Rechnungsmonat.
     */
    private void startSignature() {
        try {
            // pruefen, ob Signatur-Job gerade ausgefuehrt wird
            if (getSchedulerClient().isJobRunning(signatureJob)) {
                throw new HurricanGUIException("Es laeuft gerade ein Kopier-Job.\n" +
                        "Bitte warten Sie, bis der aktuelle Job beendet wurde.");
            }

            // Job-Parameter erstellen.
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.putAll(rechBasePnl.getParameters());
            jobDataMap.put("src.dir.bills", tfTBaseDirBill.getText());
            jobDataMap.put("portal.import.base", tfPortalBase.getText());
            jobDataMap.put("portal.import.bill", tfPortalBillImp.getText());

            // Kopier-Job triggern
            rechBasePnl.validateParameters(jobDataMap);
            validate(jobDataMap);
            getSchedulerClient().triggerJobWithVolatileTrigger(signatureJob, jobDataMap);

            MessageHelper.showInfoDialog(getMainFrame(), "Kopieren wurde gestartet.\n" +
                            "Nach Ablauf des Kopiervorgangs erhalten Sie eine Info-Mail an die angegebene Adresse.",
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

    /*
     * Bricht den Kopier-Job auf dem AK-Scheduler ab.
     */
    private void interruptSignature() {
        try {
            int opt = MessageHelper.showYesNoQuestion(getMainFrame(),
                    "Wollen Sie das Kopieren der Rechnungen wirklich abbrechen?", "Kopieren abbrechen?");
            if (opt == JOptionPane.YES_OPTION) {
                getSchedulerClient().interruptJob(signatureJob);
                MessageHelper.showInfoDialog(getMainFrame(), "Job wird bei naechster Gelegenheit abgebrochen!", null, true);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * Ueberprueft, ob alle notwendigen Parameter definiert wurden.
     */
    private void validate(JobDataMap jobDataMap) throws HurricanGUIException {
        if (StringUtils.isBlank(jobDataMap.getString("src.dir.bills"))) {
            throw new HurricanGUIException("Bitte tragen Sie das Taifun-Basisverzeichnis ein.");
        }
        if (StringUtils.isBlank(jobDataMap.getString("portal.import.base"))) {
            throw new HurricanGUIException("Bitte tragen Sie das Basisverzeichnis vom Portal ein.");
        }
        if (StringUtils.isBlank(jobDataMap.getString("portal.import.bill"))) {
            throw new HurricanGUIException("Bitte tragen Sie das Export-Verzeichnis fuer die Rechnungen ein.");
        }
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

}


