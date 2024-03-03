/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.08.2004 09:24:04
 */

package de.augustakom.common.gui.swing.wizard;

import java.awt.*;

import de.augustakom.common.gui.swing.AKJAbstractPanel;

/**
 * Default-Implementierung fuer ein Wizard-Panel
 */
abstract class AKJDefaultWizardPanel extends AKJAbstractPanel {

    private static final long serialVersionUID = 2597214113750454922L;

    private AKJWizardComponents wizardComponents;
    private String panelTitle;

    /**
     * Konstruktor mit Angabe der Wizard-Komponenten, zu denen das panel hinzugefuegt werden soll.
     *
     * @param resource
     * @param wizardComponents
     */
    public AKJDefaultWizardPanel(String resource, AKJWizardComponents wizardComponents) {
        this(resource, wizardComponents, null);
    }

    /**
     * Konstruktor mit Angabe der Wizard-Komponenten, zu denen das panel hinzugefuegt werden soll. <br> Ueber den
     * Parameter <code>title</code> kann ein Text definiert werden, der als Panel-Title verwendet wird.
     *
     * @param resource
     * @param wizardComponents
     * @param title
     */
    public AKJDefaultWizardPanel(String resource, AKJWizardComponents wizardComponents, String title) {
        super(resource);
        this.wizardComponents = wizardComponents;
        this.panelTitle = title;
    }

    public void update() {
        // to be overridden
    }

    /**
     * Wird vom Wizard-Master aufgerufen, wenn der 'Next'-Button betaetigt wird.
     */
    public boolean next() {
        return goNext();
    }

    /**
     * Wird vom Wizard-Master aufgerufen, wenn der 'Back'-Button betaetigt wird.
     */
    public void back() {
        goBack();
    }

    /**
     * Wird vom Wizard-Master aufgerufen, wenn der Wizard ueber den 'Cancel'-Button beendet wird. <br> Die Ableitungen
     * koennen in dieser Methode z.B. Aufraeumarbeiten durchfuehren. Der Wizard wird aber auf jeden Fall beendet.
     */
    public void cancel() {
        // to be overridden
    }

    /**
     * Wird vom Wizard-Master aufgerufen, wenn der Wizard ueber den 'Finish'-Button beendet wird. <br> Die Ableitungen
     * koennen in dieser Methode z.B. Aufraeumarbeiten durchfuehren oder Daten speichern. <br><br> Der Wizard wird aber
     * auf jeden Fall beendet! <br> Die einzige Moeglichkeit, das Beenden des Wizards zu verhindern, ist es, eine
     * <code>AKJWizardFinishVetoException</code> zu werfen.
     *
     * @throws AKJWizardFinishVetoException wenn verhindert werden soll, dass der Wizard beendet wird.
     */
    public void finish() throws AKJWizardFinishVetoException {
        // to be overridden
    }

    public AKJWizardComponents getWizardComponents() {
        return wizardComponents;
    }

    public void setWizardComponents(AKJWizardComponents awizardComponents) {
        wizardComponents = awizardComponents;
    }

    public String getPanelTitle() {
        return panelTitle;
    }

    public void setPanelTitle(String title) {
        panelTitle = title;
    }

    /**
     * Wird aufgerufen, wenn zum naechsten Wizard-Panel navigiert werden soll.
     *
     * @return true wenn das naechste Panel angezeigt werden kann/darf.
     */
    protected boolean goNext() {
        wizardComponents.setDirection(AKJWizardComponents.DIRECTION_FORWARD);
        if (wizardComponents.getWizardPanelList().size() > (wizardComponents.getCurrentIndex() + 1)) {
            wizardComponents.setCurrentIndex(wizardComponents.getCurrentIndex() + 1);
            wizardComponents.updateComponents();
            packWizard();
            return true;
        }
        return false;
    }

    protected boolean goBack() {
        wizardComponents.setDirection(AKJWizardComponents.DIRECTION_BACKWARD);
        if ((wizardComponents.getCurrentIndex() - 1) >= 0) {
            wizardComponents.setCurrentIndex(wizardComponents.getCurrentIndex() - 1);
            wizardComponents.updateComponents();
            return true;
        }
        return false;
    }

    protected void switchPanel(int panelIndex) {
        getWizardComponents().setCurrentIndex(panelIndex);
        getWizardComponents().updateComponents();
    }

    public void setBackButtonEnabled(boolean set) {
        wizardComponents.getBackButton().setEnabled(set);
    }

    public void setNextButtonEnabled(boolean set) {
        wizardComponents.getNextButton().setEnabled(set);
    }

    public void setFinishButtonEnabled(boolean set) {
        wizardComponents.getFinishButton().setEnabled(set);
    }


    /**
     * Ermittelt das {@link Window} des Wizards und ruft auf diesem die Methode {@code pack()} auf, damit sich der
     * Wizard an die PreferredSize und das Layout des dargestellten Wizard-Panels anpasst.
     */
    protected void packWizard() {
        Container parent = null;
        do {
            parent = (parent != null) ? parent.getParent() : getParent();
        }
        while (!(parent instanceof Window) && (parent != null));

        if (parent != null) {
            ((Window) parent).pack();
        }
    }

}
