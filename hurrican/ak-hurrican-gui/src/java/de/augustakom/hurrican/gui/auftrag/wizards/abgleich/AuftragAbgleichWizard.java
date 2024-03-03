/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.08.2004 09:34:53
 */
package de.augustakom.hurrican.gui.auftrag.wizards.abgleich;

import de.augustakom.hurrican.gui.auftrag.wizards.AuftragWizardObjectNames;
import de.augustakom.hurrican.gui.base.AbstractServiceWizardOptionDialog;


/**
 * Wizard fuer den Abgleich zwischen CC- und Billing-Auftraegen.
 *
 *
 */
public class AuftragAbgleichWizard extends AbstractServiceWizardOptionDialog {

    private boolean withTaifunOrderSelection;

    /**
     * Konstruktor fuer den Wizard.
     *
     * @param kNoOrig
     */
    public AuftragAbgleichWizard(Long kNoOrig, boolean withTaifunOrderSelection) {
        super(null);
        init(kNoOrig, withTaifunOrderSelection);
    }

    /* Initialisiert den Wizard. */
    private void init(Long kNoOrig, boolean withTaifunOrderSelection) {
        if (kNoOrig == null) {
            throw new IllegalArgumentException("Keine Kundennummer angegeben!");
        }
        this.withTaifunOrderSelection = withTaifunOrderSelection;

        getWizardComponents().addWizardObject(
                AuftragWizardObjectNames.WIZARD_OBJECT_KUNDEN_NO, kNoOrig);
        getWizardComponents().addWizardObject(
                AuftragWizardObjectNames.WIZARD_OBJECT_TAIFUN_ORDER_SELECTION, withTaifunOrderSelection);

        createGUI();
    }

    @Override
    protected void createGUI() {
        setTitle("Auftragsabgleich (Taifun-Hurrican)");
        setIconURL("de/augustakom/hurrican/gui/images/auftraege.gif");

        getWizardComponents().getFinishButton().setText("Fertigstellen");

        if (withTaifunOrderSelection) {
            TaifunOrderSelectionWizardPanel taifunOrderSelectionPanel =
                    new TaifunOrderSelectionWizardPanel(getWizardComponents());
            getWizardComponents().addWizardPanel(taifunOrderSelectionPanel);
        }
        else {
            AuftragsMonitorWizardPanel amPanel = new AuftragsMonitorWizardPanel(getWizardComponents());
            getWizardComponents().addWizardPanel(amPanel);
        }
    }

}


