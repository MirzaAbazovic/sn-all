/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.08.2004 09:24:04
 */
package de.augustakom.common.gui.swing.wizard;

import java.beans.*;
import java.util.*;
import javax.swing.*;

/**
 * Interface fuer eine Wizard-Komponente.
 */
public interface AKJWizardComponents extends AKJWizard {

    /**
     * Die Richtung des Wizards ist unbekannt.
     */
    public static final int DIRECTION_UNKNOWN = -1;
    /**
     * Der Wizard wurde 'forwaerts bewegt'.
     */
    public static final int DIRECTION_FORWARD = 0;
    /**
     * Der Wizard wurde 'rueckwaerts bewegt'.
     */
    public static final int DIRECTION_BACKWARD = 1;

    /**
     * Setzt die Richtung, in die der Dialog 'bewegt' wird.
     */
    public void setDirection(int direction);

    /**
     * Gibt die Richtung zurueck, in die der Dialog 'bewegt' wird.
     */
    public int getDirection();

    /**
     * Gibt den Wizard-Master zurueck.
     *
     * @return
     */
    public AKJWizardMaster getWizardMaster();

    /**
     * Uebergibt der Wizard-Komponente ein Modell, mit dem die Panels 'arbeiten' koennen.
     *
     * @param wizardObject
     * @name Name fuer das Wizard-Objekt.
     */
    public void addWizardObject(String name, Object wizardObject);

    /**
     * Gibt das Wizard-Objekt mit dem Angegebenen Namen zurueck.
     *
     * @param name Name des gesuchten Objekts
     * @return
     */
    public Object getWizardObject(String name);

    /**
     * Löscht das Wizard-Objekt mit dem angegebenen Namen.
     *
     * @param name Name des gesuchten Objekts
     * @return WizardObject, falls dieses unter dem angegebenen Namen registriert war, sonst null
     */
    public Object removeWizardObject(String name);

    @Override
    public void addWizardPanel(AKJDefaultWizardPanel panel);

    @Override
    public void addWizardPanel(int index, AKJDefaultWizardPanel panel);

    public void addWizardPanelAfter(
            AKJDefaultWizardPanel panelToBePlacedAfter,
            AKJDefaultWizardPanel panel);

    public void addWizardPanelBefore(
            AKJDefaultWizardPanel panelToBePlacedBefore,
            AKJDefaultWizardPanel panel);

    public void addWizardPanelAfterCurrent(AKJDefaultWizardPanel panel);

    @Override
    public AKJDefaultWizardPanel removeWizardPanel(AKJDefaultWizardPanel panel);

    @Override
    public AKJDefaultWizardPanel removeWizardPanel(int index);

    public AKJDefaultWizardPanel removeWizardPanelAfter(AKJDefaultWizardPanel panel);

    public AKJDefaultWizardPanel removeWizardPanelBefore(AKJDefaultWizardPanel panel);

    @Override
    public AKJDefaultWizardPanel getWizardPanel(int index);

    public int getIndexOfPanel(AKJDefaultWizardPanel panel);

    public void updateComponents();

    public AKJDefaultWizardPanel getCurrentPanel() throws Exception;

    public WizardFinishAction getFinishAction();

    public void setFinishAction(WizardFinishAction aFinishAction);

    public WizardCancelAction getCancelAction();

    public void setCancelAction(WizardCancelAction aCancelAction);

    public int getCurrentIndex();

    public void setCurrentIndex(int aCurrentIndex);

    public JPanel getWizardPanelsContainer();

    public void setWizardPanelsContainer(JPanel aWizardPanelsContainer);

    public JButton getBackButton();

    public void setBackButton(JButton aBackButton);

    public JButton getNextButton();

    public void setNextButton(JButton aNextButton);

    public JButton getCancelButton();

    public void setCancelButton(JButton aCancelButton);

    public JButton getFinishButton();

    public void setFinishButton(JButton button);

    public void setBackButtonEnabled(boolean set);

    public void setNextButtonEnabled(boolean set);

    public void setFinishButtonEnabled(boolean set);

    @Override
    public List<?> getWizardPanelList();

    @Override
    public void setWizardPanelList(List<?> panelList);

    public boolean onLastPanel();

    public final static String CURRENT_PANEL_PROPERTY = "currentPanel";

    public void addPropertyChangeListener(PropertyChangeListener listener);

    public void removePropertyChangeListener(PropertyChangeListener listener);

}
