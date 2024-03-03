/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.11.2010 07:12:09
 */
package de.augustakom.hurrican.gui.hvt.hardware;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJOptionDialog;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKReferenceField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.service.iface.ISimpleFindService;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.file.FileFilterExtension;
import de.augustakom.common.tools.file.FileTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChange;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChangeDlu;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChangeDluV5;
import de.augustakom.hurrican.model.cc.hardware.HWDlu;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.service.cc.HWBaugruppenChangeService;
import de.augustakom.hurrican.service.cc.HWSwitchService;
import de.augustakom.hurrican.service.cc.QueryCCService;


/**
 * Panel fuer die Planung / Ausfuehrung von Baugruppen-Schwenks des Typs 'DLU Schwenk'.
 */
public class HWBaugruppenChangeDluPanel extends AbstractHWBaugruppenChangeDefinitionPanel implements
        AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(HWBaugruppenChangeDluPanel.class);

    private static final String RESOURCE = "de/augustakom/hurrican/gui/hvt/hardware/resources/HWBaugruppenChangeDluPanel.xml";

    private static final String DLU_ACCESSCONTROLLER = "dlu.accesscontroller";
    private static final String DLU_MEDIAGATEWAY = "dlu.mediagateway";
    private static final String DLU_SWITCH = "dlu.switch";
    private static final String DLU_NUMBER = "dlu.number";
    private static final String DLU_TO_CHANGE = "dlu.to.change";
    private static final String SAVE_HW_BG_CHANGE_DLU = "save.hw.bg.change.dlu";
    private static final String V5_IMPORT = "v5.import";
    private static final String CPS_REINIT = "cps.reinit";

    // GUI
    private AKReferenceField rfDluOld;
    private AKJTextField tfDluNumber;
    private AKJTextField tfSwitch;
    private AKJTextField tfMediaGateway;
    private AKJTextField tfAccessController;
    private AKJButton btnSave;
    private AKJButton btnV5Import;
    private AKJButton btnCpsReInit;

    // Modelle
    private HWBaugruppenChangeDlu hwBgChangeDlu;
    private List<HWBaugruppenChangeDluV5> v5Mappings;

    // sonstiges
    private File lastDir;

    /**
     * Konstruktor mit Angabe der Planung.
     *
     * @param hwBaugruppenChange
     */
    public HWBaugruppenChangeDluPanel(HWBaugruppenChange hwBaugruppenChange) {
        super(RESOURCE, hwBaugruppenChange);
        this.hwBaugruppenChange = hwBaugruppenChange;
        createGUI();
        loadData();
    }

    @Override
    protected final void createGUI() {
        AKJLabel lblDluOld = getSwingFactory().createLabel(DLU_TO_CHANGE);
        AKJLabel lblDluNumber = getSwingFactory().createLabel(DLU_NUMBER);
        AKJLabel lblSwitch = getSwingFactory().createLabel(DLU_SWITCH);
        AKJLabel lblMediaGateway = getSwingFactory().createLabel(DLU_MEDIAGATEWAY);
        AKJLabel lblAccessController = getSwingFactory().createLabel(DLU_ACCESSCONTROLLER);

        rfDluOld = getSwingFactory().createReferenceField(DLU_TO_CHANGE);
        tfDluNumber = getSwingFactory().createTextField(DLU_NUMBER);
        tfSwitch = getSwingFactory().createTextField(DLU_SWITCH);
        tfMediaGateway = getSwingFactory().createTextField(DLU_MEDIAGATEWAY);
        tfAccessController = getSwingFactory().createTextField(DLU_ACCESSCONTROLLER);
        btnSave = getSwingFactory().createButton(SAVE_HW_BG_CHANGE_DLU, getActionListener());
        btnV5Import = getSwingFactory().createButton(V5_IMPORT, getActionListener());
        btnCpsReInit = getSwingFactory().createButton(CPS_REINIT, getActionListener());

        AKJPanel detailPnl = new AKJPanel(new GridBagLayout());
        detailPnl.add(lblDluOld, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        detailPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        detailPnl.add(rfDluOld, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        detailPnl.add(lblDluNumber, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        detailPnl.add(tfDluNumber, GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        detailPnl.add(lblSwitch, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        detailPnl.add(tfSwitch, GBCFactory.createGBC(0, 0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        detailPnl.add(lblMediaGateway, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        detailPnl.add(tfMediaGateway, GBCFactory.createGBC(0, 0, 2, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        detailPnl.add(lblAccessController, GBCFactory.createGBC(0, 0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        detailPnl.add(tfAccessController, GBCFactory.createGBC(0, 0, 2, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        detailPnl.add(btnSave, GBCFactory.createGBC(0, 0, 2, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        detailPnl.add(btnV5Import, GBCFactory.createGBC(0, 0, 2, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        detailPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 7, 1, 1, GridBagConstraints.NONE));
        detailPnl.add(btnCpsReInit, GBCFactory.createGBC(0, 0, 2, 8, 1, 1, GridBagConstraints.HORIZONTAL));
        detailPnl.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 3, 9, 1, 1, GridBagConstraints.BOTH));

        this.setLayout(new BorderLayout());
        this.add(detailPnl, BorderLayout.CENTER);
    }

    @Override
    protected void addPanelWithSourceData() {
        // not needed for this panel
    }

    @Override
    protected void addPanelWithDestinationData() {
        // not needed for this panel
    }

    @Override
    public final void loadData() {
        try {
            clearAll();

            ISimpleFindService simpleFindService = getCCService(QueryCCService.class);
            rfDluOld.setFindService(simpleFindService);

            HWDlu dluExample = new HWDlu();
            dluExample.setHvtIdStandort(hwBaugruppenChange.getHvtStandort().getId());
            rfDluOld.setReferenceFindExample(dluExample);

            Set<HWBaugruppenChangeDlu> dluSet = hwBaugruppenChange.getHwBgChangeDlu();
            hwBgChangeDlu = ((dluSet != null) && (dluSet.size() == 1)) ? dluSet.iterator().next() : null;
            if (hwBgChangeDlu != null) {
                rfDluOld.setReferenceObject(hwBgChangeDlu.getDluRackOld());
                tfDluNumber.setText(hwBgChangeDlu.getDluNumberNew());
                tfSwitch.setText(hwBgChangeDlu.getDluSwitchNew().getName());
                tfMediaGateway.setText(hwBgChangeDlu.getDluMediaGatewayNew());
                tfAccessController.setText(hwBgChangeDlu.getDluAccessControllerNew());

                HWBaugruppenChangeService hwBgChangeService = getCCService(HWBaugruppenChangeService.class);
                v5Mappings = hwBgChangeService.findDluV5Mappings(hwBgChangeDlu.getId());
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        finally {
            validateByStatus();
        }
    }

    @Override
    protected void execute(String command) {
        if (SAVE_HW_BG_CHANGE_DLU.equals(command)) {
            saveHwBgChangeDlu();
        }
        else if (V5_IMPORT.equals(command)) {
            doV5Import();
        }
        else if (CPS_REINIT.equals(command)) {
            doCpsReInit();
        }
    }

    /**
     * Speichert die angegebenen Werte in einem {@code HWBaugruppenChangeDlu} Modell
     */
    private void saveHwBgChangeDlu() {
        try {
            if (hwBgChangeDlu == null) {
                hwBgChangeDlu = new HWBaugruppenChangeDlu();

                Set<HWBaugruppenChangeDlu> dluSet = new HashSet<>();
                dluSet.add(hwBgChangeDlu);
                hwBaugruppenChange.setHwBgChangeDlu(dluSet);
            }

            hwBgChangeDlu.setDluRackOld(rfDluOld.getReferenceObjectAs(HWRack.class));
            hwBgChangeDlu.setDluNumberNew(tfDluNumber.getText(null));
            String hwSwitchName = tfSwitch.getText(null);
            if (hwSwitchName != null) {
                HWSwitchService hwSwitchService = getCCService(HWSwitchService.class);
                hwBgChangeDlu.setDluSwitchNew(hwSwitchService.findSwitchByName(hwSwitchName));
            }
            hwBgChangeDlu.setDluMediaGatewayNew(tfMediaGateway.getText(null));
            hwBgChangeDlu.setDluAccessControllerNew(tfAccessController.getText(null));

            HWBaugruppenChangeService hwBgChangeService = getCCService(HWBaugruppenChangeService.class);
            hwBgChangeService.saveHWBaugruppenChange(hwBaugruppenChange);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        finally {
            validateByStatus();
        }
    }


    /**
     * Fuehrt den Import eines Excel-Files mit HW_EQN/V5-Port Mappings durch.
     */
    private void doV5Import() {
        try {
            final JFileChooser fileChooser = new JFileChooser(lastDir);
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.addChoosableFileFilter(new FileFilterExtension(FileFilterExtension.FILE_EXTENSION_EXCEL));
            fileChooser.setAcceptAllFileFilterUsed(Boolean.FALSE);

            if (fileChooser.showOpenDialog(getMainFrame()) == JFileChooser.APPROVE_OPTION) {
                File importFile = fileChooser.getSelectedFile();
                if (importFile != null) {
                    lastDir = importFile.getParentFile();
                    int option = MessageHelper.showYesNoQuestion(getMainFrame(),
                            String.format("Soll der Import von File %s durchgeführt werden?",
                                    importFile.getAbsolutePath()),
                            "Import durchführen?"
                    );

                    if (option == AKJOptionDialog.YES_OPTION) {
                        setWaitCursor();

                        InputStream fileInputStream = null;
                        HWBaugruppenChangeService hwBgChangeService = null;
                        try {
                            fileInputStream = new FileInputStream(importFile);
                            hwBgChangeService = getCCService(HWBaugruppenChangeService.class);

                            hwBgChangeService.importDluV5Mappings(hwBaugruppenChange, fileInputStream,
                                    HurricanSystemRegistry.instance().getSessionId());
                        }
                        finally {
                            FileTools.closeStreamSilent(fileInputStream);
                        }
                        v5Mappings = hwBgChangeService.findDluV5Mappings(hwBgChangeDlu.getId());

                        if (CollectionTools.isNotEmpty(v5Mappings)) {
                            MessageHelper.showInfoDialog(getMainFrame(), "Es wurden {0} V5-Mappings importiert.",
                                    "Import durchgeführt",
                                    new Object[] { String.format("%s", v5Mappings.size()) }, true);
                        }
                        else {
                            MessageHelper.showInfoDialog(getMainFrame(), "Es wurden keine V5-Mappings importiert!",
                                    "keine Mappings importiert", null, true);
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        finally {
            validateByStatus();
            setDefaultCursor();
        }
    }


    /**
     * Veranlasst einen CPS Re-Init der aktiven Auftraege.
     */
    private void doCpsReInit() {
        try {
            int option = MessageHelper.showYesNoQuestion(getMainFrame(),
                    "Soll der CPS Re-Init wirklich durchgeführt werden?", "CPS Re-Init?");

            if (option == AKJOptionDialog.YES_OPTION) {
                setWaitCursor();
                showProgressBar("CPS Re-Init working");

                SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
                    @Override
                    protected String doInBackground() throws Exception {
                        HWBaugruppenChangeService hwBgChangeService = getCCService(HWBaugruppenChangeService.class);
                        String warnings = hwBgChangeService.createCPSReInitTransactions(
                                hwBaugruppenChange, HurricanSystemRegistry.instance().getSessionId());

                        return (StringUtils.isNotBlank(warnings))
                                ? String.format("CPS Re-Init mit Warnungen ausgeführt:%n%s", warnings)
                                : "CPS Re-Init erfolgreich ausgeführt";
                    }

                    @Override
                    protected void done() {
                        try {
                            String message = get();
                            MessageHelper.showInfoDialog(getMainFrame(), message, "CPS Re-Init abgeschlossen", null, true);
                        }
                        catch (Exception e) {
                            LOGGER.error(e.getMessage(), e);
                            MessageHelper.showErrorDialog(getMainFrame(), e);
                        }
                        finally {
                            stopProgressBar();
                            setDefaultCursor();
                        }
                    }
                };
                worker.execute();
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }


    @Override
    protected void validateByStatus() {
        if (hwBaugruppenChange.isPreparingAllowed()) {
            rfDluOld.setEnabled(CollectionTools.isEmpty(v5Mappings));
            btnSave.setEnabled(true);
            btnV5Import.setEnabled(hwBgChangeDlu != null && hwBgChangeDlu.getId() != null);
            btnCpsReInit.setEnabled(false);
        }
        else if (hwBaugruppenChange.isExecuteAllowed()) {
            rfDluOld.setEnabled(false);
            btnSave.setEnabled(true);
            btnV5Import.setEnabled(false);
            btnCpsReInit.setEnabled(false);
        }
        else if (hwBaugruppenChange.isCloseAllowed()) {
            rfDluOld.setEnabled(false);
            btnSave.setEnabled(false);
            btnV5Import.setEnabled(false);
            btnCpsReInit.setEnabled(true);
        }
    }

    @Override
    protected final void clearAll() {
        GuiTools.cleanFields(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        // not needed for this panel
    }

}
