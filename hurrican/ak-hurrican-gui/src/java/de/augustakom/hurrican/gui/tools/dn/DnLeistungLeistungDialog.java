/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.01.2006 15:43:34
 */
package de.augustakom.hurrican.gui.tools.dn;

import java.awt.*;
import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJOptionDialog;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.cc.dn.Leistung4Dn;
import de.augustakom.hurrican.service.cc.CCRufnummernService;

/**
 * Dialog fuer die Erfassung neuer und Aenderung bestehender Rufnummernleistungen
 *
 *
 */
public class DnLeistungLeistungDialog extends AbstractServiceOptionDialog {
    private static final Logger LOGGER = Logger.getLogger(DnLeistungLeistungDialog.class);

    private Leistung4Dn leistung4Dn = null;

    private AKJTextField tfBeschreibung = null;
    private AKJTextField tfLeistung = null;
    private AKJFormattedTextField tfExtLstNo = null;
    private AKJFormattedTextField tfExtSonstNo = null;

    /**
     * Konstruktor mit Angabe der Rufnummern-Leistung, die editiert werden soll.
     *
     * @param leistung4Dn
     */
    public DnLeistungLeistungDialog(Leistung4Dn leistung4Dn) {
        super("de/augustakom/hurrican/gui/tools/dn/resources/DnLeistungLeistungDialog.xml");
        this.leistung4Dn = leistung4Dn;
        if (this.leistung4Dn == null) {
            throw new IllegalArgumentException("Parameter leistung4Dn must not be null!");
        }
        createGUI();
        showValues();
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    protected void doSave() {
        try {
            CCRufnummernService rs = getCCService(CCRufnummernService.class);
            setValues();
            rs.saveDNLeistung(leistung4Dn);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
        finally {
            prepare4Close();
            setValue(Integer.valueOf(AKJOptionDialog.CANCEL_OPTION));
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    protected final void createGUI() {
        String title = ((leistung4Dn.getId() == null) ? "Leistung neu anlegen" : "Leistung ändern");
        setTitle(title);

        tfBeschreibung = getSwingFactory().createTextField("beschreibung");
        tfLeistung = getSwingFactory().createTextField("leistung");
        tfExtSonstNo = getSwingFactory().createFormattedTextField("ext.sonstiges.no");
        tfExtLstNo = getSwingFactory().createFormattedTextField("ext.leistung.no");

        AKJLabel lblLeistung = getSwingFactory().createLabel("leistung");
        AKJLabel lblBeschreibung = getSwingFactory().createLabel("beschreibung");
        AKJLabel lblExtSonstNo = getSwingFactory().createLabel("ext.sonstiges.no");
        AKJLabel lblExtLstNo = getSwingFactory().createLabel("ext.leistung.no");

        AKJPanel top = new AKJPanel(new GridBagLayout());
        top.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        top.add(lblLeistung, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(tfLeistung, GBCFactory.createGBC(0, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 2, 1, 1, GridBagConstraints.NONE));
        top.add(lblBeschreibung, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(tfBeschreibung, GBCFactory.createGBC(0, 0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(lblExtSonstNo, GBCFactory.createGBC(0, 0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(tfExtSonstNo, GBCFactory.createGBC(0, 0, 3, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(lblExtLstNo, GBCFactory.createGBC(0, 0, 1, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(tfExtLstNo, GBCFactory.createGBC(0, 0, 3, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 4, 6, 1, 1, GridBagConstraints.BOTH));

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(top, BorderLayout.CENTER);
    }

    /* Zeigt die Werte des aktuellen Objekts an */
    public void showValues() {
        if (leistung4Dn != null) {
            tfBeschreibung.setText(leistung4Dn.getBeschreibung());
            tfLeistung.setText(leistung4Dn.getLeistung());
            tfExtSonstNo.setValue(leistung4Dn.getExternSonstigesNo());
            tfExtLstNo.setValue(leistung4Dn.getExternLeistungNo());
        }
    }

    /* Uebergibt die eingetragenen Werte dem Modell. */
    public void setValues() {
        leistung4Dn.setBeschreibung(tfBeschreibung.getText());
        leistung4Dn.setLeistung(tfLeistung.getText());
        leistung4Dn.setExternSonstigesNo(tfExtSonstNo.getValueAsLong(null));
        leistung4Dn.setExternLeistungNo(tfExtLstNo.getValueAsLong(null));
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#execute(java.lang.String)
     */
    protected void execute(String command) {
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable arg0, Object arg1) {
    }
}


