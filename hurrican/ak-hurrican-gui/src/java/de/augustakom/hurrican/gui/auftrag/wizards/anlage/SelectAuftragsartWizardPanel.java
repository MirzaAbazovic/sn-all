/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.08.2004 15:53:49
 */
package de.augustakom.hurrican.gui.auftrag.wizards.anlage;

import java.awt.*;
import javax.swing.*;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJRadioButton;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.wizard.AKJWizardComponents;
import de.augustakom.hurrican.gui.auftrag.wizards.AuftragWizardObjectNames;
import de.augustakom.hurrican.gui.base.AbstractServiceWizardPanel;


/**
 * Wizard-Panel, um die Auftragsart fuer einen neuen Auftrag auszuwaehlen.
 *
 *
 */
public class SelectAuftragsartWizardPanel extends AbstractServiceWizardPanel implements AuftragWizardObjectNames {

    private AKJRadioButton rbNeuschaltung = null;
    private AKJRadioButton rbUebernahme = null;

    /**
     * Konstruktor fuer das Wizard-Panel.
     *
     * @param wizardComponents
     */
    public SelectAuftragsartWizardPanel(AKJWizardComponents wizardComponents) {
        super("de/augustakom/hurrican/gui/auftrag/wizards/anlage/SelectAuftragsartWizardPanel.xml", wizardComponents);
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        ButtonGroup bgLeft = new ButtonGroup();
        rbNeuschaltung = getSwingFactory().createRadioButton("neuschaltung", bgLeft);
        rbUebernahme = getSwingFactory().createRadioButton("uebernahme", bgLeft);

        AKJPanel left = new AKJPanel(new GridBagLayout());
        left.setBorder(BorderFactory.createTitledBorder(getSwingFactory().getText("auftragsart")));
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        left.add(rbNeuschaltung, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(rbUebernahme, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 2, 3, 1, 1, GridBagConstraints.VERTICAL));

        getChildPanel().setLayout(new GridBagLayout());
        getChildPanel().add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        getChildPanel().add(left, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.VERTICAL));
        getChildPanel().add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.NONE));
    }

    /**
     * @see de.augustakom.common.gui.swing.wizard.AKJDefaultWizardPanel#next()
     */
    @Override
    public boolean next() {
        if (rbNeuschaltung.isSelected()) {
            addWizardObject(WIZARD_OBJECT_CC_AUFTRAGSART, AUFTRAGSART_NEUSCHALTUNG);
        }
        else if (rbUebernahme.isSelected()) {
            addWizardObject(WIZARD_OBJECT_CC_AUFTRAGSART, AUFTRAGSART_UEBERNAHME);
        }
        else {
            String msg = "Bitte wählen Sie eine Auftragsart für \nden Auftrag aus.";
            String title = "Warnung";
            MessageHelper.showMessageDialog(this, msg, title, JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return super.next();
    }

}
