/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.08.2004 09:24:04
 */
package de.augustakom.common.gui.swing.wizard;

import java.awt.*;

import de.augustakom.common.gui.swing.AKJPanel;

/**
 * AK-Implementierung von JWizardPanel. <br> Die Wizard-Komponenten sind nicht im Java-SDK enthalten sondern kommen aus
 * einem OpenSource-Projekt!!! <br> Website des Projekts: <a href="http://jwizardcmponent.sourceforge.net">JWizardComponent</a>
 * <br><br> Ableitungen koennen ihre Komponenten auf das Panel platzieren, das die Methode <code>getChildPanel()</code>
 * zurueck liefert.
 *
 *
 */
public abstract class AKJWizardPanel extends AKJDefaultWizardPanel {

    private AKJPanel childPanel = null;

    /**
     * Konstruktor mit Angabe der WizardComponents, zu denen das Panel hinzugefuegt werden soll.
     *
     * @param resource
     * @param wizardComponents
     */
    public AKJWizardPanel(String resource, AKJWizardComponents wizardComponents) {
        super(resource, wizardComponents);
        createDefaultGUI();
    }

    /**
     * Erstellt die Default-GUI fuer das Panel.
     */
    private void createDefaultGUI() {
        childPanel = new AKJPanel(new BorderLayout());

        this.setLayout(new BorderLayout());
        this.add(childPanel, BorderLayout.CENTER);
    }

    /**
     * Muss von den Ableitungen implementiert werden. <br>
     */
    @Override
    protected abstract void createGUI();

    /**
     * Gibt das ChildPanel zurueck, auf das die Ableitungen ihre GUI-Komponenten platzieren koennen.
     *
     * @return
     */
    public AKJPanel getChildPanel() {
        return childPanel;
    }

    /**
     * Entfernt alle Panels, die nach dem Panel <code>panel</code> vorhanden sind.
     *
     * @param panel
     *
     */
    protected void removePanelsAfter(AKJWizardPanel panel) {
        int count = getWizardComponents().getWizardPanelList().size();
        int index = getWizardComponents().getIndexOfPanel(panel);
        for (int i = count - 1; i > index; i--) {
            getWizardComponents().removeWizardPanel(i);
        }
    }

    /**
     * Gibt das Wizard-Objekt zurueck, das in den AKJWizardComponents gespeichert ist.
     *
     * @param name Name des gesuchten Wizard-Objekts
     * @return
     */
    protected Object getWizardObject(String name) {
        return getWizardComponents().getWizardObject(name);
    }

    /**
     * Speichert ein Wizard-Objekt unter dem angegebenen Namen.
     *
     * @param name
     * @param wizardObject
     */
    protected void addWizardObject(String name, Object wizardObject) {
        getWizardComponents().addWizardObject(name, wizardObject);
    }
}


