/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.10.2007 13:59:11
 */
package de.augustakom.hurrican.gui.stammdaten;

import java.awt.*;
import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.ProduktEQConfig;
import de.augustakom.hurrican.service.cc.RangierungAdminService;


/**
 * Dialog zur Generierung eines ProduktEQConfig-Objekts. <br> Das generierte Objekt wird ueber die Methode setValue
 * geschrieben und kann vom Client ueber getValue referenziert werden. <br>
 *
 *
 */
public class ProduktEQConfigDialog extends AbstractServiceOptionDialog {

    private static final Logger LOGGER = Logger.getLogger(ProduktEQConfigDialog.class);

    private Produkt produkt = null;

    private AKJFormattedTextField tfGroup = null;
    private AKJComboBox cbEqTyp = null;
    private AKJTextField tfEqParam = null;
    private AKJTextField tfEqValue = null;
    private AKJCheckBox chbRangDefault = null;
    private AKJCheckBox chbRangAdditional = null;

    /**
     * Konstruktor mit Angabe des Produkts, fuer das die Konfiguration vorgenommen wird.
     *
     * @param produkt
     */
    public ProduktEQConfigDialog(Produkt produkt) {
        super("de/augustakom/hurrican/gui/stammdaten/resources/ProduktEQConfigDialog.xml");
        this.produkt = produkt;
        if (this.produkt == null) {
            throw new IllegalArgumentException("Produkt muss definiert werden!");
        }
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("title"));

        AKJLabel lblGroup = getSwingFactory().createLabel("config.group");
        AKJLabel lblEqTyp = getSwingFactory().createLabel("eq.typ");
        AKJLabel lblEqParam = getSwingFactory().createLabel("eq.param");
        AKJLabel lblEqValue = getSwingFactory().createLabel("eq.value");
        AKJLabel lblRangDefault = getSwingFactory().createLabel("rangierung.default");
        AKJLabel lblRangAdditional = getSwingFactory().createLabel("rangierung.additional");

        tfGroup = getSwingFactory().createFormattedTextField("config.group");
        cbEqTyp = getSwingFactory().createComboBox("eq.typ");
        tfEqParam = getSwingFactory().createTextField("eq.param");
        tfEqValue = getSwingFactory().createTextField("eq.value");
        chbRangDefault = getSwingFactory().createCheckBox("rangierung.default");
        chbRangAdditional = getSwingFactory().createCheckBox("rangierung.additional");

        AKJPanel child = getChildPanel();
        child.setLayout(new GridBagLayout());
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        child.add(lblGroup, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        child.add(tfGroup, GBCFactory.createGBC(0, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(lblEqTyp, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(cbEqTyp, GBCFactory.createGBC(0, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(lblEqParam, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(tfEqParam, GBCFactory.createGBC(0, 0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(lblEqValue, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(tfEqValue, GBCFactory.createGBC(0, 0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(lblRangDefault, GBCFactory.createGBC(0, 0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(chbRangDefault, GBCFactory.createGBC(0, 0, 3, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(lblRangAdditional, GBCFactory.createGBC(0, 0, 1, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(chbRangAdditional, GBCFactory.createGBC(0, 0, 3, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 4, 6, 1, 1, GridBagConstraints.BOTH));
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    @Override
    protected void doSave() {
        try {
            ProduktEQConfig eqConfig = new ProduktEQConfig();
            eqConfig.setProdId(produkt.getId());
            eqConfig.setConfigGroup(tfGroup.getValueAsLong(null));
            eqConfig.setEqTyp(cbEqTyp.getSelectedItem().toString());
            eqConfig.setEqParam(tfEqParam.getText(null));
            eqConfig.setEqValue(tfEqValue.getText(null));
            eqConfig.setRangierungsPartDefault(chbRangDefault.isSelectedBoolean());
            eqConfig.setRangierungsPartAdditional(chbRangAdditional.isSelectedBoolean());

            RangierungAdminService ras = getCCService(RangierungAdminService.class);
            ras.saveProduktEQConfig(eqConfig);

            prepare4Close();
            setValue(eqConfig);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
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

}


