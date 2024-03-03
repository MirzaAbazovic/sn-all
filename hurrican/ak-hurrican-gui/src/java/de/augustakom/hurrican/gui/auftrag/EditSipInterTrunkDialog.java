/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.12.2010 13:14:01
 */
package de.augustakom.hurrican.gui.auftrag;

import java.awt.*;
import java.util.*;
import java.util.List;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.cc.AuftragSIPInterTrunk;
import de.augustakom.hurrican.model.cc.hardware.HWSwitch;
import de.augustakom.hurrican.service.cc.HWSwitchService;
import de.augustakom.hurrican.service.cc.SIPInterTrunkService;


/**
 * Dialog, um ein AuftragSIPInterTrunk anzulegen bzw. zu editieren.
 */
public class EditSipInterTrunkDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(EditSipInterTrunkDialog.class);

    private static final String RESOURCE = "de/augustakom/hurrican/gui/auftrag/resources/EditSipInterTrunkDialog.xml";

    private AKJComboBox cbSwitch = null;
    private AKJTextField tfTrunkGroup = null;

    private final AuftragSIPInterTrunk auftragSipInterTrunk;

    /**
     * Konstruktor mit Angabe des AuftragSIPInterTrunk Objekts, das editiert werden soll.
     *
     * @param auftragSipInterTrunk
     */
    public EditSipInterTrunkDialog(AuftragSIPInterTrunk auftragSipInterTrunk) {
        super(RESOURCE);
        this.auftragSipInterTrunk = auftragSipInterTrunk;
        if (this.auftragSipInterTrunk == null) {
            throw new IllegalArgumentException("No object defined!");
        }
        createGUI();
        loadData();
    }

    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("title"));

        AKJLabel lblSwitch = getSwingFactory().createLabel("switch");
        AKJLabel lblTrunkGroup = getSwingFactory().createLabel("trunkgroup");

        cbSwitch = getSwingFactory().createComboBox("switch",
                new AKCustomListCellRenderer<>(HWSwitch.class, HWSwitch::getName));
        tfTrunkGroup = getSwingFactory().createTextField("trunkgroup", true, true);

        getChildPanel().setLayout(new GridBagLayout());
        getChildPanel().add(lblSwitch, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        getChildPanel().add(cbSwitch, GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(lblTrunkGroup, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(tfTrunkGroup, GBCFactory.createGBC(100, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
    }

    @Override
    public final void loadData() {
        try {
            HWSwitchService hwSwitchService = getCCService(HWSwitchService.class);
            tfTrunkGroup.setText(auftragSipInterTrunk.getTrunkGroup());
            List<HWSwitch> switches = hwSwitchService.findAllSwitches();
            cbSwitch.addItems(switches, true, HWSwitch.class);
            cbSwitch.setSelectedItem(auftragSipInterTrunk.getHwSwitch());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    protected void doSave() {
        try {
            auftragSipInterTrunk.setHwSwitch((HWSwitch)cbSwitch.getSelectedItem());
            auftragSipInterTrunk.setTrunkGroup(tfTrunkGroup.getText(null));

            SIPInterTrunkService service = getCCService(SIPInterTrunkService.class);
            service.saveSIPInterTrunk(auftragSipInterTrunk, HurricanSystemRegistry.instance().getSessionId());

            prepare4Close();
            setValue(OK_OPTION);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    @SuppressWarnings("squid:UnusedProtectedMethod")
    protected void execute(String command) {
        // not needed in this dialog
    }

    @Override
    public void update(Observable o, Object arg) {
        // not needed in this dialog
    }

}
