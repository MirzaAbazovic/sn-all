/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.08.2004 09:24:04
 */
package de.augustakom.common.gui.swing.wizard;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;
import java.util.List;

import javax.swing.*;

import org.apache.log4j.Logger;

import de.augustakom.common.tools.lang.ResourceReader;

/**
 * Default-Implementierung von AKJWizardComponent.
 */

public class DefaultAKJWizardComponents implements AKJWizardComponents {

    private static final Logger LOGGER = Logger.getLogger(DefaultAKJWizardComponents.class);

    private static ResourceReader resourceReader =
            new ResourceReader("de.augustakom.common.gui.swing.wizard.i18n");

    private JButton backButton;
    private JButton nextButton;
    private JButton finishButton;
    private JButton cancelButton;

    private WizardFinishAction finishAction;
    private WizardCancelAction cancelAction;

    private List<?> panelList;
    private int currentIndex;
    private int direction = DIRECTION_UNKNOWN;
    private JPanel wizardPanelsContainer;
    private PropertyChangeSupport propertyChangeListeners;

    private Map<String, Object> wizardObjects = null;
    private AKJWizardMaster wizardMaster = null;

    /**
     * This class is the "bread and butter" of this framework.  All of these components can be used visually however you
     * want, as shown in the frame and example packages, but all a developer really needs is this, and they can even
     * instead implement JWizard and choose to do this portion any way they wish.
     */
    public DefaultAKJWizardComponents(AKJWizardMaster wizardMaster) {
        try {
            this.wizardMaster = wizardMaster;
            init();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.wizard.AKJWizardComponents#getWizardMaster()
     */
    @Override
    public AKJWizardMaster getWizardMaster() {
        return wizardMaster;
    }

    /**
     * @see de.augustakom.common.gui.swing.wizard.AKJWizardComponents#addWizardObject(java.lang.String,
     * java.lang.Object)
     */
    @Override
    public void addWizardObject(String name, Object wizardObject) {
        wizardObjects.put(name, wizardObject);
    }

    /**
     * @see de.augustakom.common.gui.swing.wizard.AKJWizardComponents#getWizardObject(java.lang.String)
     */
    @Override
    public Object getWizardObject(String name) {
        return wizardObjects.get(name);
    }

    @Override
    public Object removeWizardObject(String name) {
        return wizardObjects.remove(name);
    }

    /**
     * @see de.augustakom.common.gui.swing.wizard.AKJWizardComponents#getDirection()
     */
    @Override
    public int getDirection() {
        return direction;
    }

    /**
     * @see de.augustakom.common.gui.swing.wizard.AKJWizardComponents#setDirection(int)
     */
    @Override
    public void setDirection(int direction) {
        this.direction = direction;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void addWizardPanel(AKJDefaultWizardPanel panel) {
        getWizardPanelList().add(panel);
        wizardPanelsContainer.add(panel, Integer.toString(getWizardPanelList().size() - 1));
    }

    @Override
    @SuppressWarnings("unchecked")
    public void addWizardPanel(int index, AKJDefaultWizardPanel panel) {
        getWizardPanelList().add(index, panel);
        wizardPanelsContainer.add(panel, Integer.toString(index), index);
        if (index < (getWizardPanelList().size() - 1)) {
            for (int i = index + 1; i < getWizardPanelList().size(); i++) {
                wizardPanelsContainer.add((AKJDefaultWizardPanel) getWizardPanelList().get(i), Integer.toString(i));
            }
        }
    }

    @Override
    public void addWizardPanelAfter(
            AKJDefaultWizardPanel panelToBePlacedAfter,
            AKJDefaultWizardPanel panel) {
        addWizardPanel(
                getWizardPanelList().indexOf(panelToBePlacedAfter) + 1,
                panel);
    }

    @Override
    public void addWizardPanelBefore(
            AKJDefaultWizardPanel panelToBePlacedBefore,
            AKJDefaultWizardPanel panel) {
        addWizardPanel(
                getWizardPanelList().indexOf(panelToBePlacedBefore) - 1,
                panel);
    }

    @Override
    public void addWizardPanelAfterCurrent(AKJDefaultWizardPanel panel) {
        addWizardPanel(getCurrentIndex() + 1, panel);
    }

    @Override
    public AKJDefaultWizardPanel removeWizardPanel(AKJDefaultWizardPanel panel) {
        int index = getWizardPanelList().indexOf(panel);
        return removeWizardPanel(index);
    }

    @Override
    public AKJDefaultWizardPanel removeWizardPanel(int index) {
        wizardPanelsContainer.remove(index);
        AKJDefaultWizardPanel panel = (AKJDefaultWizardPanel) getWizardPanelList().remove(index);
        for (int i = index; i < getWizardPanelList().size(); i++) {
            wizardPanelsContainer.add((AKJDefaultWizardPanel) getWizardPanelList().get(i), Integer.toString(i));
        }
        return panel;
    }

    @Override
    public AKJDefaultWizardPanel removeWizardPanelAfter(AKJDefaultWizardPanel panel) {
        return removeWizardPanel(getWizardPanelList().indexOf(panel) + 1);
    }

    @Override
    public AKJDefaultWizardPanel removeWizardPanelBefore(AKJDefaultWizardPanel panel) {
        return removeWizardPanel(getWizardPanelList().indexOf(panel) - 1);
    }

    @Override
    public AKJDefaultWizardPanel getWizardPanel(int index) {
        return (AKJDefaultWizardPanel) getWizardPanelList().get(index);
    }

    @Override
    public int getIndexOfPanel(AKJDefaultWizardPanel panel) {
        return getWizardPanelList().indexOf(panel);
    }

    @Override
    public boolean onLastPanel() {
        return (getCurrentIndex() == (getWizardPanelList().size() - 1));
    }

    private void init() throws Exception {
        this.propertyChangeListeners = new PropertyChangeSupport(this);

        wizardObjects = new HashMap<String, Object>();

        backButton = new JButton();
        nextButton = new JButton();
        finishButton = new JButton();
        cancelButton = new JButton();

        panelList = new ArrayList<Object>();
        currentIndex = 0;
        wizardPanelsContainer = new JPanel();

        backButton.setText(resourceReader.getValue("L_BackButton"));
        backButton.setMnemonic(resourceReader.getValue("L_BackButtonMnem").charAt(0));
        backButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                backButton_actionPerformed(e);
            }
        });

        nextButton.setText(resourceReader.getValue("L_NextButton"));
        nextButton.setMnemonic(resourceReader.getValue("L_NextButtonMnem").charAt(0));
        nextButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nextButton_actionPerformed(e);
            }
        });

        cancelButton.setText(resourceReader.getValue("L_CancelButton"));
        cancelButton.setMnemonic(resourceReader.getValue("L_CancelButtonMnem").charAt(0));
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelButton_actionPerformed(e);
            }
        });

        finishButton.setText(resourceReader.getValue("L_FinishButton"));
        finishButton.setMnemonic(resourceReader.getValue("L_FinishButtonMnem").charAt(0));
        finishButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                finishButton_actionPerformed(e);
            }
        });

        wizardPanelsContainer.setLayout(new CardLayout());
    }

    void cancelButton_actionPerformed(ActionEvent e) {
        try {
            getCurrentPanel().cancel();
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        getCancelAction().performAction();
    }

    void finishButton_actionPerformed(ActionEvent e) {
        boolean veto = false;
        try {
            getCurrentPanel().finish();
        }
        catch (AKJWizardFinishVetoException ex) {
            veto = true;
            LOGGER.error(ex.getMessage(), ex);
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }

        if (!veto) {
            getFinishAction().performAction();
        }
    }

    void nextButton_actionPerformed(ActionEvent e) {
        try {
            getCurrentPanel().next();
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    void backButton_actionPerformed(ActionEvent e) {
        try {
            getCurrentPanel().back();
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    @Override
    public AKJDefaultWizardPanel getCurrentPanel() throws Exception {
        if ((getWizardPanelList() != null) && ((getWizardPanelList().size() - 1) >= currentIndex)) {
            if (getWizardPanelList().get(currentIndex) != null) {
                return (AKJDefaultWizardPanel) getWizardPanelList().get(currentIndex);
            }
            else {
                throw new Exception("No panels in panelList");
            }
        }
        throw new Exception("No panels in panelList");
    }

    @Override
    public void updateComponents() {
        try {
            CardLayout cl = (CardLayout) (wizardPanelsContainer.getLayout());
            cl.show(wizardPanelsContainer, Integer.toString(currentIndex));
            Frame frameForComponent = JOptionPane.getFrameForComponent(getWizardPanelsContainer());
            frameForComponent.pack();

            if (currentIndex == 0) {
                backButton.setEnabled(false);
            }
            else {
                backButton.setEnabled(true);
            }

            if (onLastPanel()) {
                nextButton.setEnabled(false);
                finishButton.setEnabled(true);
            }
            else {
                finishButton.setEnabled(false);
                nextButton.setEnabled(true);
            }

            // let panel to update itself
            getCurrentPanel().update();

            // inform PropertyChangeListeners
            PropertyChangeEvent event = new PropertyChangeEvent(this, CURRENT_PANEL_PROPERTY, null, getCurrentPanel());
            propertyChangeListeners.firePropertyChange(event);
        }
        catch (Exception e) {
            LOGGER.warn(e.getMessage());
        }
    }

    // Getters and Setters from here on ...

    @Override
    @SuppressWarnings("unchecked")
    public List getWizardPanelList() {
        return this.panelList;
    }

    @SuppressWarnings("unchecked")
    public void setWizardPanelList(ArrayList panelList) {
        this.panelList = panelList;
    }

    @Override
    public WizardFinishAction getFinishAction() {
        return finishAction;
    }

    @Override
    public void setFinishAction(WizardFinishAction aFinishAction) {
        finishAction = aFinishAction;
    }

    @Override
    public WizardCancelAction getCancelAction() {
        return cancelAction;
    }

    @Override
    public void setCancelAction(WizardCancelAction aCancelAction) {
        cancelAction = aCancelAction;
    }

    @Override
    public int getCurrentIndex() {
        return currentIndex;
    }

    @Override
    public void setCurrentIndex(int aCurrentIndex) {
        currentIndex = aCurrentIndex;
    }

    @Override
    public JPanel getWizardPanelsContainer() {
        return wizardPanelsContainer;
    }

    @Override
    public void setWizardPanelsContainer(JPanel aWizardPanelsContainer) {
        wizardPanelsContainer = aWizardPanelsContainer;
    }

    @Override
    public JButton getBackButton() {
        return backButton;
    }

    @Override
    public void setBackButton(JButton aBackButton) {
        backButton = aBackButton;
    }

    @Override
    public JButton getNextButton() {
        return nextButton;
    }

    @Override
    public void setNextButton(JButton aNextButton) {
        nextButton = aNextButton;
    }

    @Override
    public JButton getCancelButton() {
        return cancelButton;
    }

    @Override
    public void setCancelButton(JButton aCancelButton) {
        cancelButton = aCancelButton;
    }

    @Override
    public JButton getFinishButton() {
        return finishButton;
    }

    @Override
    public void setFinishButton(JButton button) {
        finishButton = button;
    }

    /**
     * @see de.augustakom.common.gui.swing.wizard.AKJWizardComponents#setBackButtonEnabled(boolean)
     */
    @Override
    public void setBackButtonEnabled(boolean set) {
        getBackButton().setEnabled(set);
    }

    /**
     * @see de.augustakom.common.gui.swing.wizard.AKJWizardComponents#setFinishButtonEnabled(boolean)
     */
    @Override
    public void setFinishButtonEnabled(boolean set) {
        getFinishButton().setEnabled(set);
    }

    /**
     * @see de.augustakom.common.gui.swing.wizard.AKJWizardComponents#setNextButtonEnabled(boolean)
     */
    @Override
    public void setNextButtonEnabled(boolean set) {
        getNextButton().setEnabled(set);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setWizardPanelList(List panelList) {
        this.panelList = panelList;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeListeners.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeListeners.removePropertyChangeListener(listener);
    }

}
