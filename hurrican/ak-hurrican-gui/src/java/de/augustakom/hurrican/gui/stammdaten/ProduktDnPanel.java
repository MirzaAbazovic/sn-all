/*
 * Copyright (c) 2006 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.10.2006 09:30:06
 */
package de.augustakom.hurrican.gui.stammdaten;

import java.awt.*;
import java.util.List;
import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.iface.AKModelOwner;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.model.billing.OE;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.hardware.HWSwitch;
import de.augustakom.hurrican.service.billing.OEService;
import de.augustakom.hurrican.service.cc.HWSwitchService;

/**
 * Sub-Panel, um die DN-Daten fuer ein Produkt zu konfigurieren.
 *
 */
public class ProduktDnPanel extends AbstractServicePanel implements AKModelOwner, AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(ProduktDnPanel.class);

    /* Aktuelles Modell, das ueber die Methode showDetails(Object) gesetzt wird. */
    private Produkt model = null;

    // GUI-Elemente fuer das 'DN'-Panel
    private AKJFormattedTextField tfMinDnCount = null;
    private AKJFormattedTextField tfMaxDnCount = null;
    private AKJCheckBox chbDNBlock = null;
    private AKJComboBox cbDNTyp = null;
    private AKJComboBox cbSwitch = null;

    public ProduktDnPanel() {
        super("de/augustakom/hurrican/gui/stammdaten/resources/ProduktDnPanel.xml");
        createGUI();
        loadData();
    }

    @Override
    protected final void createGUI() {
        // Erzeuge GUI-Komponenten und ordne diese auf dem Panel an
        AKJLabel lblMinDNCount = getSwingFactory().createLabel("min.dn.count");
        AKJLabel lblMaxDNCount = getSwingFactory().createLabel("max.dn.count");
        AKJLabel lblDNBlock = getSwingFactory().createLabel("dn.block");
        AKJLabel lblDNTyp = getSwingFactory().createLabel("dn.typ");
        AKJLabel lblSwitch = getSwingFactory().createLabel("switch");

        tfMinDnCount = getSwingFactory().createFormattedTextField("min.dn.count");
        tfMaxDnCount = getSwingFactory().createFormattedTextField("max.dn.count");
        chbDNBlock = getSwingFactory().createCheckBox("dn.block");
        cbDNTyp = getSwingFactory().createComboBox("dn.typ", new AKCustomListCellRenderer<>(OE.class, OE::getName));
        cbSwitch = getSwingFactory().createComboBox("switch",
                new AKCustomListCellRenderer<>(HWSwitch.class, HWSwitch::getName));

        AKJPanel left = new AKJPanel(new GridBagLayout());
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        left.add(lblMinDNCount, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        left.add(tfMinDnCount, GBCFactory.createGBC(100, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblMaxDNCount, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfMaxDnCount, GBCFactory.createGBC(100, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblDNBlock, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(chbDNBlock, GBCFactory.createGBC(100, 0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblDNTyp, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(cbDNTyp, GBCFactory.createGBC(100, 0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblSwitch, GBCFactory.createGBC(0, 0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(cbSwitch, GBCFactory.createGBC(100, 0, 3, 4, 1, 1, GridBagConstraints.HORIZONTAL));

        this.setLayout(new GridBagLayout());
        this.add(left, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 1, 1, 1, 1, GridBagConstraints.BOTH));

        // Bitte alle GUI Komponenten auf Rechte prüfen, da diverse User nur auf wenige Komponenten rechte haben!
        manageGUI(tfMinDnCount, tfMaxDnCount, chbDNBlock, cbDNTyp);
    }

    @Override
    public final void loadData() {
        try {
            // Laedt alle moeglichen Rufnummerntypen.
            OEService oes = getBillingService(OEService.class);
            List<OE> dntypes = oes.findOEByOeTyp(OE.OE_OETYP_RUFNUMMER, OEService.FIND_STRATEGY_ALL);
            cbDNTyp.addItems(dntypes, true, OE.class);
            loadSwitchList();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    private void loadSwitchList() throws ServiceNotFoundException {
        HWSwitchService hwSwitchService = getCCService(HWSwitchService.class);
        List<HWSwitch> switches = hwSwitchService.findAllSwitches();
        cbSwitch.removeAllItems();
        if (CollectionTools.isNotEmpty(switches)) {
            cbSwitch.addItems(switches, true, HWSwitch.class);
            cbSwitch.setSelectedIndex(0);
        }
    }

    @Override
    @SuppressWarnings("squid:UnusedProtectedMethod")
    protected void execute(String command) {
        // not needed in this panel
    }

    @Override
    public void setModel(Observable model) {
        this.model = (model instanceof Produkt) ? (Produkt) model : null;
        readModel();
    }

    @Override
    public void readModel() {
        // Lösche alle Einträge
        GuiTools.cleanFields(this);

        // Lade Daten aus Model in GUI-Komponenten
        if (model != null) {
            tfMinDnCount.setValue(model.getMinDnCount());
            tfMaxDnCount.setValue(model.getMaxDnCount());
            chbDNBlock.setSelected(model.getDnBlock());
            cbDNTyp.selectItem("getOeNoOrig", OE.class, model.getDnTyp());
            if (model.getHwSwitch() != null) {
                cbSwitch.selectItem("getName", HWSwitch.class, model.getHwSwitch().getName());
            }
        }
    }

    @Override
    public Object getModel() {
        // Daten aus GUI-Komponenten in Model setzen
        model.setMinDnCount(tfMinDnCount.getValueAsInt(0));
        model.setMaxDnCount(tfMaxDnCount.getValueAsInt(0));
        model.setDnBlock(chbDNBlock.isSelected());
        model.setDnTyp((Long) cbDNTyp.getSelectedItemValue("getOeNoOrig", Long.class));
        HWSwitch hwSwitch = (HWSwitch) cbSwitch.getSelectedItem();
        model.setHwSwitch(hwSwitch != null && hwSwitch.getId() != null ? hwSwitch : null);
        return model;
    }

    @Override
    public boolean hasModelChanged() {
        return false;
    }

    @Override
    public void saveModel() {
        // not needed for this Panel
    }

    @Override
    public void update(Observable o, Object arg) {
        // not needed for this Panel
    }

}
