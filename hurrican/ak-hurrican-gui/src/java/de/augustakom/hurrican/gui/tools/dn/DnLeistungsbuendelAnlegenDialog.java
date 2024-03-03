/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.03.2006 13:51:41
 */
package de.augustakom.hurrican.gui.tools.dn;

import java.awt.*;
import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.cc.dn.Leistungsbuendel;
import de.augustakom.hurrican.service.cc.CCRufnummernService;


/**
 * Erfassung der Leistngsbuendel.
 *
 *
 */
public class DnLeistungsbuendelAnlegenDialog extends AbstractServiceOptionDialog {

    private static final Logger LOGGER = Logger.getLogger(DnLeistungsbuendelAnlegenDialog.class);

    private AKJTextField tfName = null;
    private AKJTextField tfBeschreibung = null;

    /**
     * Default-Const.
     */
    public DnLeistungsbuendelAnlegenDialog() {
        super(null);
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    protected final void createGUI() {
        setTitle("Leistungsbündel anlegen");

        AKJLabel lblName = new AKJLabel("Name");
        AKJLabel lblBeschreibung = new AKJLabel("Beschreibung");
        tfName = new AKJTextField(null, 20);
        tfBeschreibung = new AKJTextField(null, 20);

        AKJPanel center = new AKJPanel(new GridBagLayout());
        center.add(lblName, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        center.add(tfName, GBCFactory.createGBC(0, 0, 1, 1, 2, 1, GridBagConstraints.HORIZONTAL));
        center.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        center.add(lblBeschreibung, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        center.add(tfBeschreibung, GBCFactory.createGBC(0, 0, 1, 2, 2, 1, GridBagConstraints.HORIZONTAL));

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(center, BorderLayout.CENTER);
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#validateSaveButton()
     */
    protected void validateSaveButton() {
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    protected void execute(String command) {
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#getModel()
     */
    public Object getModel() {
        return null;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#hasModelChanged()
     */
    public boolean hasModelChanged() {
        return false;
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable arg0, Object arg1) {
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    protected void doSave() {
        try {
            Leistungsbuendel lb = new Leistungsbuendel();
            lb.setName(tfName.getText());
            lb.setBeschreibung(tfBeschreibung.getText());
            CCRufnummernService service = getCCService(CCRufnummernService.class);
            service.saveLeistungsbuendel(lb);
            prepare4Close();
            setValue(Integer.valueOf(OK_OPTION));
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
    }

}


