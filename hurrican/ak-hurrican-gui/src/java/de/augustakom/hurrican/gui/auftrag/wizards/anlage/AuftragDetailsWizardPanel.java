/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.08.2004 16:31:40
 */
package de.augustakom.hurrican.gui.auftrag.wizards.anlage;

import java.awt.*;

import de.augustakom.common.gui.swing.wizard.AKJWizardComponents;
import de.augustakom.hurrican.gui.auftrag.wizards.AuftragWizardObjectNames;
import de.augustakom.hurrican.gui.auftrag.wizards.shared.AuftragDetailsPanel;
import de.augustakom.hurrican.gui.base.AbstractServiceWizardPanel;
import de.augustakom.hurrican.model.cc.AuftragDaten;


/**
 * Wizard-Panel, um einige Details fuer den neuen Hurrican-Auftrag anzugeben.
 *
 *
 */
public class AuftragDetailsWizardPanel extends AbstractServiceWizardPanel {

    private AuftragDetailsPanel detailsPanel = null;

    /**
     * Konstruktor
     *
     * @param resource
     * @param wizardComponents
     */
    public AuftragDetailsWizardPanel(AKJWizardComponents wizardComponents) {
        super(null, wizardComponents);
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    protected final void createGUI() {
        detailsPanel = new AuftragDetailsPanel(getWizardComponents());
        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(detailsPanel, BorderLayout.CENTER);
    }

    /**
     * @see de.augustakom.common.gui.swing.wizard.AKJDefaultWizardPanel#update()
     */
    public void update() {
        if (getWizardComponents().getDirection() == AKJWizardComponents.DIRECTION_FORWARD) {
            AuftragDaten ad = (AuftragDaten) getWizardObject(AuftragWizardObjectNames.WIZARD_OBJECT_CC_AUFTRAG_DATEN);
            detailsPanel.setModel(ad);
            setFinishButtonEnabled(true);
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.wizard.AKJDefaultWizardPanel#finish()
     */
    public void finish() {
        detailsPanel.saveModel();
    }
}


