/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.11.2005 11:22:30
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
import de.augustakom.hurrican.model.cc.dn.LeistungParameter;
import de.augustakom.hurrican.service.cc.CCRufnummernService;

/**
 * Dialog fuer die Erfassung neuer und Aenderung bestehender Parameter
 *
 *
 */
public class DnLeistungParameterDialog extends AbstractServiceOptionDialog {
    private static final Logger LOGGER = Logger.getLogger(DnLeistungParameterDialog.class);

    private LeistungParameter leistungParameter = null;

    private AKJTextField tfBeschreibung = null;
    private AKJFormattedTextField tfMehrfach = null;
    private AKJFormattedTextField tfMehrfachIms;

    /**
     * Konstruktor mit Angabe des zu aendernden Leistung-Parameters.
     *
     * @param leistungParameter
     */
    public DnLeistungParameterDialog(LeistungParameter leistungParameter) {
        super("de/augustakom/hurrican/gui/tools/dn/resources/DnLeistungParameterDialog.xml");
        this.leistungParameter = leistungParameter;
        if (this.leistungParameter == null) {
            throw new IllegalArgumentException("Parameter leistungParameter must not be null!");
        }
        createGUI();
        showValues();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    protected final void createGUI() {
        String title = ((leistungParameter.getId() == null) ? "Parameter neu anlegen" : "Parameter ändern");
        setTitle(title);

        AKJLabel lblBeschreibung = getSwingFactory().createLabel("beschreibung");
        AKJLabel lblMehrfach = getSwingFactory().createLabel("mehrfach");
        AKJLabel lblMehrfachIms = getSwingFactory().createLabel("mehrfachIms");

        tfBeschreibung = getSwingFactory().createTextField("beschreibung");
        tfMehrfach = getSwingFactory().createFormattedTextField("mehrfach");
        tfMehrfachIms = getSwingFactory().createFormattedTextField("mehrfachIms");

        AKJPanel top = new AKJPanel(new GridBagLayout());
        top.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        top.add(lblBeschreibung, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(tfBeschreibung, GBCFactory.createGBC(100, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 2, 1, 1, GridBagConstraints.NONE));
        top.add(lblMehrfach, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(tfMehrfach, GBCFactory.createGBC(100, 0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 4, 4, 1, 1, GridBagConstraints.BOTH));
        top.add(lblMehrfachIms, GBCFactory.createGBC(0, 0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(tfMehrfachIms, GBCFactory.createGBC(100, 0, 3, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 5, 4, 1, 1, GridBagConstraints.BOTH));

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(top, BorderLayout.CENTER);
    }

    /* Zeigt die Werte des aktuellen Modells an. */
    private void showValues() {
        if (leistungParameter != null) {
            tfBeschreibung.setText(leistungParameter.getLeistungParameterBeschreibung());
            tfMehrfach.setValue(leistungParameter.getLeistungParameterMehrfach());
            tfMehrfachIms.setValue(leistungParameter.getLeistungParameterMehrfachIms());
        }
    }

    /* Uebergibt die eingetragenen Parameter dem Modell. */
    private void setValues() {
        leistungParameter.setLeistungParameterBeschreibung(tfBeschreibung.getText());
        leistungParameter.setLeistungParameterMehrfach(tfMehrfach.getValueAsInt(null));
        leistungParameter.setLeistungParameterMehrfachIms(tfMehrfachIms.getValueAsInt(null));
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    protected void doSave() {
        try {
            CCRufnummernService rs = getCCService(CCRufnummernService.class);
            setValues();
            rs.saveLeistungParameter(leistungParameter);
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
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#execute(java.lang.String)
     */
    protected void execute(String command) {
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {
    }
}
