/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.03.2006 13:08:33
 */
package de.augustakom.hurrican.gui.tools.dn;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJAbstractOptionDialog;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJList;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.model.cc.dn.Leistung2DN;
import de.augustakom.hurrican.model.cc.dn.LeistungParameter;


/**
 * Dialog fuer die Erfassung der Parameterwerte fuer Rufnummernleistungen
 *
 *
 */
public class DnParameter2LeistungDialog extends AKJAbstractOptionDialog {

    /* tfWert wird als Default-Parameter ausgewertet und gespeichert*/
    private AKJTextField tfWert = null;
    /* tfWert2 Als zweiter Parameter möglich(z.B. Nebenstelle), muss dann aber noch eingebunden werden*/
    private AKJTextField tfWert2 = null;
    private DefaultListModel lsMdlWerte = null;
    private AKJLabel lblWert = new AKJLabel();
    private int paramCount = 0;
    private String values = "";
    private final Integer leistungParameterTyp;
    private final int leistungParameterMehrfach;

    /**
     * Default-Const.
     */
    public DnParameter2LeistungDialog(Integer leistungParameterTyp, Integer leistungParameterMehrfach) {
        super("de/augustakom/hurrican/gui/tools/dn/resources/DnParameter2LeistungDialog.xml");
        this.leistungParameterTyp = leistungParameterTyp;
        this.leistungParameterMehrfach = leistungParameterMehrfach != null ? leistungParameterMehrfach : 1;
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle("Parameterwerte eingeben");

        AKJPanel center = new AKJPanel(new GridBagLayout());

        if (leistungParameterTyp.equals(LeistungParameter.PARAMETER_TYP_RUFNUMMER)) {
            lblWert = getSwingFactory().createLabel("rufnummer");
            tfWert = getSwingFactory().createTextField("rufnummer");
        }
        else if (leistungParameterTyp.equals(LeistungParameter.PARAMETER_TYP_NS_RUFNUMMER)) {
            AKJLabel lblWert2 = getSwingFactory().createLabel("nebenstelle");
            tfWert2 = getSwingFactory().createTextField("nebenstelle");
            center.add(lblWert2, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
            center.add(tfWert2, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.NONE));
            lblWert = getSwingFactory().createLabel("zielrufnummer");
            tfWert = getSwingFactory().createTextField("zielrufnummer");
        }
        else {
            lblWert = getSwingFactory().createLabel("wert");
            tfWert = getSwingFactory().createTextField("wert");
        }

        AKJButton btnAdd = getSwingFactory().createButton("add", getActionListener());
        lsMdlWerte = new DefaultListModel();
        AKJList lsWerte = getSwingFactory().createList("ls.parameter", lsMdlWerte);
        lsWerte.setEnabled(false);
        AKJScrollPane spWerte = new AKJScrollPane(lsWerte);
        spWerte.setPreferredSize(new Dimension(300, 130));

        center.add(lblWert, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.NONE));
        center.add(tfWert, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.NONE));
        center.add(btnAdd, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.NONE));
        center.add(spWerte, GBCFactory.createGBC(0, 0, 0, 4, 2, 1, GridBagConstraints.NONE));

        AKJButton btnOk = getSwingFactory().createButton("ok", getActionListener());
        AKJButton btnCancel = getSwingFactory().createButton("cancel", getActionListener());

        AKJPanel button = new AKJPanel(new GridBagLayout());
        button.add(btnOk, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        button.add(btnCancel, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        button.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(center, BorderLayout.CENTER);
        getChildPanel().add(button, BorderLayout.SOUTH);
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if ("cancel".equals(command)) {
            prepare4Close();
            setValue(Integer.valueOf(CANCEL_OPTION));
        }
        else if ("add".equals(command)) {
            if (StringUtils.isNotBlank(tfWert.getText())) {
                if (paramCount >= leistungParameterMehrfach) {
                    String info = "Max. " + leistungParameterMehrfach + " " + lblWert.getText() + " (n) erlaubt";
                    MessageHelper.showInfoDialog(this, info);
                    return;
                }
                ++paramCount;
                if (paramCount != 1) {
                    values = values + Leistung2DN.PARAMETER_SEP;
                }

                if (leistungParameterTyp.equals(LeistungParameter.PARAMETER_TYP_NS_RUFNUMMER)) {
                    if (StringUtils.isNotBlank(tfWert2.getText())) {
                        values = values + tfWert2.getText() + "-" + tfWert.getText();
                        lsMdlWerte.addElement(tfWert2.getText() + "-" + tfWert.getText());
                        tfWert.setText("");
                        tfWert2.setText("");
                        tfWert2.requestFocus();
                    }
                    else {
                        MessageHelper.showMessageDialog(getParent(), "Die Parameter konnten nicht gespeichert werden. Die Nebenstelle ist nicht angegeben");
                        tfWert2.requestFocus();
                    }
                }
                else {
                    // ParameterNummer zweistellig (mit fuehrender 0) erstellen
                    String paramNumber = StringTools.fillToSize("" + paramCount, 2, '0', true);

                    // Parameter: ParameterNummer-ParameterValue
                    String newValue = paramNumber + "-" + tfWert.getText();

                    values = values + newValue;
                    lsMdlWerte.addElement(newValue);
                    tfWert.setText("");
                    tfWert.requestFocus();
                }
            }
        }
        else if ("ok".equals(command)) {
            prepare4Close();
            setValue(values);
        }
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable arg0, Object arg1) {
    }
}
