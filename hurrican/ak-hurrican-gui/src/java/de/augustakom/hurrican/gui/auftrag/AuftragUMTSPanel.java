/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.08.2010 11:10:33
 */

package de.augustakom.hurrican.gui.auftrag;

import java.awt.*;
import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.iface.AKManageableComponent;
import de.augustakom.common.gui.iface.AKModelOwner;
import de.augustakom.common.gui.iface.AKSaveableAware;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.model.cc.AuftragUMTS;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.service.cc.UMTSService;


/**
 *
 */
public class AuftragUMTSPanel extends AbstractAuftragPanel implements AKModelOwner,
        AKDataLoaderComponent, AKSaveableAware {

    private static final Logger LOGGER = Logger.getLogger(AuftragUMTSPanel.class);

    private static final String RESOURCE = "de/augustakom/hurrican/gui/auftrag/resources/AuftragUMTSPanel.xml";

    private static final String WATCH_AUFTRAG_UMTS = "auftrag.umts";

    private static final String TITLE_UMTSDATA = "title.umtsData";
    private static final String DEFAULT_MOBILFUNKANBIETER = "default.mobilfunkanbieter";
    private static final String DEFAULT_SIMKARTENNUMMER = "default.simKartennummer";
    private static final String DEFAULT_APN = "default.apn";

    private static final String ADDDATA = "addData";
    private static final String MOBILFUNKANBIETER = "mobilfunkanbieter";
    private static final String SIMKARTENNUMMER = "simKartennummer";
    private static final String MOBILFUNKRUFNUMMER = "mobilfunkrufnummer";
    private static final String RAHMENVERTRAGSNUMMER = "rahmenvertragsnummer";
    private static final String APN = "apn";

    // GUI Objekte
    private AKJButton btnAddData;
    private AKJTextField tfMobilfunkanbieter;
    private AKJTextField tfSIMKartennummer;
    private AKJTextField tfMobilfunkrufnummer;
    private AKJTextField tfRahmenvertragsnummer;
    private AKJTextField tfAPN;

    // Modelle
    private CCAuftragModel auftragModel = null;
    private AuftragUMTS auftragUMTS = null;

    // Services
    private UMTSService umtsService;

    // Managed Components
    private AKManageableComponent[] managedComponents;

    public AuftragUMTSPanel() {
        super(RESOURCE);
        createGUI();
        loadData();
    }

    @Override
    protected final void createGUI() {
        AKJLabel lblMobilfunkanbieter = getSwingFactory().createLabel(MOBILFUNKANBIETER);
        AKJLabel lblSimKartennummer = getSwingFactory().createLabel(SIMKARTENNUMMER);
        AKJLabel lblMobilfunkrufnummer = getSwingFactory().createLabel(MOBILFUNKRUFNUMMER);
        AKJLabel lblRahmenvertragsnummer = getSwingFactory().createLabel(RAHMENVERTRAGSNUMMER);
        AKJLabel lblAPN = getSwingFactory().createLabel(APN);

        btnAddData = getSwingFactory().createButton(ADDDATA, getActionListener(), null);
        tfMobilfunkanbieter = getSwingFactory().createTextField(MOBILFUNKANBIETER, false);
        tfSIMKartennummer = getSwingFactory().createTextField(SIMKARTENNUMMER, false);
        tfMobilfunkrufnummer = getSwingFactory().createTextField(MOBILFUNKRUFNUMMER, false);
        tfRahmenvertragsnummer = getSwingFactory().createTextField(RAHMENVERTRAGSNUMMER, false);
        tfAPN = getSwingFactory().createTextField(APN, false);

        AKJPanel btnPnl = new AKJPanel(new GridBagLayout());
        btnPnl.add(btnAddData, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        btnPnl.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel fill = new AKJPanel();
        fill.setPreferredSize(new Dimension(250, 1));

        AKJPanel dataPnl = new AKJPanel(new GridBagLayout(), getSwingFactory().getText(TITLE_UMTSDATA));
        dataPnl.add(btnPnl, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPnl.add(fill, GBCFactory.createGBC(0, 0, 1, 0, 3, 1, GridBagConstraints.HORIZONTAL));
        dataPnl.add(lblMobilfunkanbieter, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.NONE));
        dataPnl.add(tfMobilfunkanbieter, GBCFactory.createGBC(100, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPnl.add(lblSimKartennummer, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPnl.add(tfSIMKartennummer, GBCFactory.createGBC(0, 0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPnl.add(lblMobilfunkrufnummer, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPnl.add(tfMobilfunkrufnummer, GBCFactory.createGBC(0, 0, 2, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPnl.add(lblRahmenvertragsnummer, GBCFactory.createGBC(0, 0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPnl.add(tfRahmenvertragsnummer, GBCFactory.createGBC(0, 0, 2, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPnl.add(lblAPN, GBCFactory.createGBC(0, 0, 0, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPnl.add(tfAPN, GBCFactory.createGBC(0, 0, 2, 5, 1, 1, GridBagConstraints.HORIZONTAL));

        this.setLayout(new GridBagLayout());
        this.add(dataPnl, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 1, 1, 1, 1, GridBagConstraints.BOTH));

        disableDataComponents();

        managedComponents = new AKManageableComponent[] { btnAddData };
        manageGUI(managedComponents);
    }

    @Override
    public final void loadData() {
        try {
            umtsService = getCCService(UMTSService.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    public void setModel(Observable model) throws AKGUIException {
        try {
            auftragModel = null;
            if (model instanceof CCAuftragModel) {
                auftragModel = (CCAuftragModel) model;
            }

            readModel();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKGUIException("Error loading UMTS data: " + e.getMessage(), e);
        }
    }

    @Override
    public void readModel() throws AKGUIException {
        GuiTools.cleanFields(this);
        disableDataComponents();
        auftragUMTS = null;
        if (auftragModel != null) {
            try {
                auftragUMTS = umtsService.findAuftragUMTS(auftragModel.getAuftragId());
                addObjectToWatch(WATCH_AUFTRAG_UMTS, auftragUMTS);
                if (auftragUMTS != null) {
                    showValues();
                    enableDataComponents();
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public Object getModel() {
        if (dataComponentsEnabled() && (auftragUMTS != null)) {
            auftragUMTS.setMobilfunkanbieter(tfMobilfunkanbieter.getText(null));
            auftragUMTS.setSimKartennummer(tfSIMKartennummer.getText(null));
            auftragUMTS.setMobilfunkrufnummer(tfMobilfunkrufnummer.getText(null));
            auftragUMTS.setRahmenvertragsnummer(tfRahmenvertragsnummer.getText(null));
            auftragUMTS.setApn(tfAPN.getText(null));
        }

        return auftragUMTS;
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    @Override
    public void saveModel() throws AKGUIException {
        if ((auftragUMTS != null) && (hasModelChanged())) {
            try {
                auftragUMTS = umtsService.saveAuftragUMTS(auftragUMTS, HurricanSystemRegistry.instance().getSessionId());
                addObjectToWatch(WATCH_AUFTRAG_UMTS, auftragUMTS);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }
    }

    @Override
    public boolean hasModelChanged() {
        getModel();
        return hasChanged(WATCH_AUFTRAG_UMTS, auftragUMTS);
    }

    private void createAuftragUMTS() {
        auftragUMTS = new AuftragUMTS();
        auftragUMTS.setAuftragId(auftragModel.getAuftragId());
        addObjectToWatch(WATCH_AUFTRAG_UMTS, auftragUMTS);

        //Defaultwerte setzen
        auftragUMTS.setMobilfunkanbieter(getSwingFactory().getText(DEFAULT_MOBILFUNKANBIETER));
        auftragUMTS.setSimKartennummer(getSwingFactory().getText(DEFAULT_SIMKARTENNUMMER));
        auftragUMTS.setApn(getSwingFactory().getText(DEFAULT_APN));
    }

    private boolean dataComponentsEnabled() {
        return !btnAddData.isEnabled();
    }

    private void enableDataComponents() {
        GuiTools.enableContainerComponents(this, true);
        btnAddData.setEnabled(false);
    }

    private void disableDataComponents() {
        GuiTools.enableContainerComponents(this, false);
        btnAddData.setEnabled(true);
        manageGUI(managedComponents);
    }

    private void showValues() {
        if (auftragUMTS != null) {
            tfMobilfunkanbieter.setText(auftragUMTS.getMobilfunkanbieter());
            tfSIMKartennummer.setText(auftragUMTS.getSimKartennummer());
            tfMobilfunkrufnummer.setText(auftragUMTS.getMobilfunkrufnummer());
            tfRahmenvertragsnummer.setText(auftragUMTS.getRahmenvertragsnummer());
            tfAPN.setText(auftragUMTS.getApn());
        }
    }

    @Override
    protected void execute(String command) {
        if (StringUtils.equals(command, ADDDATA)) {
            enableDataComponents();
            createAuftragUMTS();
            showValues();
        }
    }

    @Override
    public void setSaveable(boolean saveable) {
    }

}
