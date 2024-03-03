/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.08.2004 10:03:15
 */
package de.augustakom.common.gui.swing.wizard;

import java.awt.*;
import java.util.*;
import javax.swing.*;

import de.augustakom.common.gui.swing.AKJAbstractOptionDialog;
import de.augustakom.common.gui.swing.AKJPanel;

/**
 * Implementierung eines Wizard-Dialogs.
 *
 *
 */
public class AKJWizardOptionDialog extends AKJAbstractOptionDialog implements AKJWizardMaster {

    private transient AKJWizardComponents wizardComponents;

    /**
     * Konstruktor fuer den Dialog.
     *
     * @param resource
     */
    public AKJWizardOptionDialog(String resource) {
        super(resource);
        wizardComponents = new DefaultAKJWizardComponents(this);
        initWizard();
    }

    /**
     * Konstruktor mit Angabe des Titles und der Icon-URL.
     *
     * @param resource
     * @param title
     * @param iconURL
     */
    public AKJWizardOptionDialog(String resource, String title, String iconURL) {
        super(resource, true);
        setTitle(title);
        setIconURL(iconURL);
        wizardComponents = new DefaultAKJWizardComponents(this);
        initWizard();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    protected void createGUI() {
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

    /* Initialisiert den Wizard-Dialog. */
    private void initWizard() {
        getChildPanel().setLayout(new GridBagLayout());

        getChildPanel().add(wizardComponents.getWizardPanelsContainer(),
                new GridBagConstraints(1, 0, 1, 1, 1.0, 0.9, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0)
        );

        getChildPanel().add(new JSeparator(),
                new GridBagConstraints(0, 1, 2, 0, 1.0, 0.01, GridBagConstraints.WEST,
                        GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0)
        );

        AKJPanel buttonPanel = new AKJWizardButtonPanel(wizardComponents);
        getChildPanel().add(buttonPanel,
                new GridBagConstraints(0, 2, 2, 1, 1.0, 0.09, GridBagConstraints.WEST,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0)
        );

        wizardComponents.setFinishAction(new WizardDialogFinishAction());
        wizardComponents.setCancelAction(new WizardDialogCancelAction());

        wizardComponents.updateComponents();
    }

    /**
     * Gibt die Wizard-Komponenten des Dialogs zurueck.
     */
    public AKJWizardComponents getWizardComponents() {
        return wizardComponents;
    }

    /**
     * Uebergibt dem Dialog die Wizard-Komponenten
     */
    public void setWizardComponents(AKJWizardComponents aWizardComponents) {
        wizardComponents = aWizardComponents;
    }

    /**
     * Wird aufgerufen, wenn der Wizard ueber den Finish-Button beendet wird.
     */
    public void finishWizard() {
        prepare4Close();
        setValue(Integer.valueOf(OK_OPTION));
    }

    /**
     * Wird aufgerufen, wenn der Wizard ueber den Cancel-Button abgebrochen wird.
     */
    public void cancelWizard() {
        prepare4Close();
        setValue(Integer.valueOf(CANCEL_OPTION));
    }

    /**
     * Finish-Action fuer den Wizard-Dialog.
     */
    class WizardDialogFinishAction extends WizardFinishAction {

        /**
         */
        public WizardDialogFinishAction() {
            super();
        }

        /**
         * @see de.augustakom.common.gui.swing.wizard.WizardAction#performAction()
         */
        public void performAction() {
            finishWizard();
        }
    }

    /**
     * Finish-Action fuer den Wizard-Dialog.
     */
    class WizardDialogCancelAction extends WizardCancelAction {

        /**
         * Default-Const.
         */
        public WizardDialogCancelAction() {
            super();
        }

        /**
         * @see de.augustakom.common.gui.swing.wizard.WizardAction#performAction()
         */
        public void performAction() {
            cancelWizard();
        }
    }

}

