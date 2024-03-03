/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.10.2009 15:36:00
 */
package de.augustakom.hurrican.gui.auftrag;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.tools.tal.wizard.CBVorgangDetailWizardPanel;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.Uebertragungsverfahren;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.mnet.wita.service.WitaTalOrderService;

/**
 * Dialog, um den DTAG-Port einer Rangierung zu editieren
 *
 *
 */
public class EditDtagPortDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent, ActionListener {

    private static final long serialVersionUID = 1166105474795041820L;

    private static final Logger LOGGER = Logger.getLogger(EditDtagPortDialog.class);

    // GUI-Elemete
    private AKJComboBox cbUetv;
    private AKJCheckBox chkSendTalOrder;
    private AKJDateComponent dcVorgabeDatum;

    // Modelle
    private final Equipment dtagEquipment;
    private final Carrierbestellung carrierBestellung;

    // Services
    private RangierungsService rangierungsService;
    private WitaTalOrderService witaTalOrderService;

    public EditDtagPortDialog(Equipment equipment, Carrierbestellung carrierBestellung) {
        super("de/augustakom/hurrican/gui/auftrag/resources/EditDtagPortDialog.xml");
        this.dtagEquipment = equipment;
        this.carrierBestellung = carrierBestellung;
        initServices();
        createGUI();
        loadData();
    }

    private void initServices() {
        try {
            rangierungsService = getCCService(RangierungsService.class);
            witaTalOrderService = getCCService(WitaTalOrderService.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("title"));
        AKJLabel lblUetv = getSwingFactory().createLabel("uetv");
        AKJLabel lblSendTalOrder = getSwingFactory().createLabel("sendTalOrder");
        AKJLabel lblVorgabeDatum = getSwingFactory().createLabel("vorgabeDatum");

        cbUetv = getSwingFactory().createComboBox("uevt");
        chkSendTalOrder = getSwingFactory().createCheckBox("sendTalOrder");
        chkSendTalOrder.addActionListener(this);
        chkSendTalOrder.setSelected(false);
        dcVorgabeDatum = getSwingFactory().createDateComponent("vorgabeDatum");
        dcVorgabeDatum.setEnabled(false);

        // @formatter:off
        getChildPanel().setLayout(new GridBagLayout());
        getChildPanel().add(new AKJPanel()      , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.NONE));
        getChildPanel().add(lblUetv             , GBCFactory.createGBC(  0,  0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(new AKJPanel()      , GBCFactory.createGBC(  0,  0, 2, 1, 1, 1, GridBagConstraints.NONE));
        getChildPanel().add(cbUetv              , GBCFactory.createGBC(  0,  0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(lblSendTalOrder     , GBCFactory.createGBC(  0,  0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(chkSendTalOrder     , GBCFactory.createGBC(  0,  0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(lblVorgabeDatum     , GBCFactory.createGBC(  0,  0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(dcVorgabeDatum      , GBCFactory.createGBC(  0,  0, 3, 3, 5, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(new AKJPanel()      , GBCFactory.createGBC(100,100, 4, 4, 1, 1, GridBagConstraints.BOTH));
     // @formatter:on
    }

    @Override
    public final void loadData() {
        try {
            cbUetv.addItems(EnumSet.allOf(Uebertragungsverfahren.class), true);
            if (dtagEquipment != null) {
                cbUetv.selectItem(dtagEquipment.getUetv());
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    protected void doSave() {
        try {
            if (dtagEquipment != null) {
                if (chkSendTalOrder.isSelected()) {
                    Date vorgabeDate = dcVorgabeDatum.getDate(null);
                    if (vorgabeDate == null) {
                        MessageHelper.showInfoDialog(this, "Bitte Vorgabedatum für Leistungsmerkmaländerung angeben!");
                        return;
                    }
                    Uebertragungsverfahren uebertragungsVerfahrenNeu = (Uebertragungsverfahren) cbUetv
                            .getSelectedItem();
                    witaTalOrderService.changeUebertragungsverfahren(carrierBestellung, dtagEquipment, vorgabeDate,
                            uebertragungsVerfahrenNeu, HurricanSystemRegistry.instance().getCurrentUser());
                    MessageHelper.showInfoDialog(this, CBVorgangDetailWizardPanel.MSG_BESTELLUNG_UEBERMITTELT);
                }
                else {
                    dtagEquipment.setUetv((Uebertragungsverfahren) cbUetv.getSelectedItem());
                    rangierungsService.saveEquipment(dtagEquipment);
                }
            }
            else {
                MessageHelper.showErrorDialog(this, new Exception("Equipment konnte nicht gefunden werden!"));
            }
            prepare4Close();
            setValue(dtagEquipment);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == chkSendTalOrder) {
            if (chkSendTalOrder.isSelected()) {
                this.dcVorgabeDatum.setEnabled(true);
            }
            else {
                this.dcVorgabeDatum.setEnabled(false);
            }
        }
    }

    @Override
    protected void execute(String command) {
        // not used
    }

    @Override
    public void update(Observable arg0, Object arg1) {
        // not used
    }
}
