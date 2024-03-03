/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.12.2004 13:54:19
 */
package de.augustakom.hurrican.gui.auftrag;

import java.awt.*;
import java.util.*;
import javax.swing.*;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJRadioButton;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.cc.PhysikaenderungsTyp;


/**
 * Dialog zur Auswahl der Physik-Uebernahme-Strategie. <br> Der Dialog gibt eine Konstante (definiert in
 * <code>RangierungsService</code> zurueck, die die Auswahl wider spiegelt.
 *
 *
 */
public class PhysikUebernahmeDialog extends AbstractServiceOptionDialog {

    private AKJRadioButton rbUebernahme = null;
    private AKJRadioButton rbAnalogISDN = null;
    private AKJRadioButton rbIsdnAnalog = null;
    private AKJRadioButton rbBandbreite = null;
    private AKJRadioButton rbDSLKreuzung = null;

    /**
     * Konstruktor.
     */
    public PhysikUebernahmeDialog() {
        super("de/augustakom/hurrican/gui/auftrag/resources/PhysikUebernahmeDialog.xml");
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle("Physik-Übernahme");
        configureButton(CMD_SAVE, "OK", "Führt die Physik-Übernahme durch", true, true);

        ButtonGroup group = new ButtonGroup();
        rbUebernahme = getSwingFactory().createRadioButton("anschlussuebernahme", group);
        rbAnalogISDN = getSwingFactory().createRadioButton("wandel.analog.isdn", group);
        rbIsdnAnalog = getSwingFactory().createRadioButton("wandel.isdn.analog", group);
        rbBandbreite = getSwingFactory().createRadioButton("bandbreitenaenderung", group);
        rbDSLKreuzung = getSwingFactory().createRadioButton("dsl.kreuzung", group);

        AKJPanel btnPanel = new AKJPanel(new GridBagLayout());
        btnPanel.setBorder(BorderFactory.createTitledBorder("Auswahl"));
        btnPanel.add(rbUebernahme, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        btnPanel.add(rbAnalogISDN, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        btnPanel.add(rbIsdnAnalog, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        btnPanel.add(rbBandbreite, GBCFactory.createGBC(0, 0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        btnPanel.add(rbDSLKreuzung, GBCFactory.createGBC(0, 0, 1, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        btnPanel.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 2, 6, 1, 1, GridBagConstraints.HORIZONTAL));

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(btnPanel, BorderLayout.CENTER);
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#validateSaveButton()
     */
    @Override
    protected void validateSaveButton() {
        // KEINE Ueberpruefung!
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    @Override
    protected void doSave() {
        long selection = -1;
        if (rbUebernahme.isSelected()) {
            selection = PhysikaenderungsTyp.STRATEGY_ANSCHLUSSUEBERNAHME;
        }
        else if (rbAnalogISDN.isSelected()) {
            selection = PhysikaenderungsTyp.STRATEGY_WANDEL_ANALOG_ISDN;
        }
        else if (rbIsdnAnalog.isSelected()) {
            selection = PhysikaenderungsTyp.STRATEGY_WANDEL_ISDN_ANALOG;
        }
        else if (rbBandbreite.isSelected()) {
            selection = PhysikaenderungsTyp.STRATEGY_BANDBREITENAENDERUNG;
        }
        else if (rbDSLKreuzung.isSelected()) {
            selection = PhysikaenderungsTyp.STRATEGY_DSL_KREUZUNG;
        }

        if ((selection < PhysikaenderungsTyp.STRATEGY_ANSCHLUSSUEBERNAHME) ||
                (selection > PhysikaenderungsTyp.STRATEGY_DSL_KREUZUNG)) {
            MessageHelper.showMessageDialog(getMainFrame(),
                    "Bitte wählen Sie eine Strategie für die Physik-Übernahme aus.", "Ungültige Auswahl",
                    JOptionPane.INFORMATION_MESSAGE);
        }
        else {
            prepare4Close();
            setValue(Long.valueOf(selection));
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


