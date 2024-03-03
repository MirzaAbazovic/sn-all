/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.05.2004
 */
package de.augustakom.common.gui.swing;

import java.awt.event.*;
import javax.swing.*;

import de.augustakom.common.gui.iface.AKManageableComponent;
import de.augustakom.common.gui.iface.AKParentAware;


/**
 * AK-Implementierung eines JButton.
 *
 *
 * @see javax.swing.JButton
 */
public class AKJButton extends JButton implements AKManageableComponent, AKParentAware {

    private boolean executable = true;
    private boolean visible = true;
    private boolean managementCalled = false;
    private String parentClazz = null;

    /**
     * Erzeugt einen 'leeren' Button.
     */
    public AKJButton() {
        super();
    }

    /**
     * Erzeugt einen Button mit Text.
     *
     * @param text
     */
    public AKJButton(String text) {
        super(text);
    }

    /**
     * Erzeugt einen Button ueber eine Action.
     *
     * @param a
     */
    public AKJButton(Action a) {
        super(a);
    }

    /**
     * Erzeugt einen Button mit Icon.
     *
     * @param icon
     */
    public AKJButton(Icon icon) {
        super(icon);
    }

    /**
     * Erzeugt einen Button mit Text und Icon.
     *
     * @param text
     * @param icon
     */
    public AKJButton(String text, Icon icon) {
        super(text, icon);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#getComponentName()
     */
    public String getComponentName() {
        return ((getAccessibleContext() != null) && (getAccessibleContext().getAccessibleName() != null))
                ? getAccessibleContext().getAccessibleName() : getName();
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#isComponentExecutable()
     */
    public boolean isComponentExecutable() {
        return this.executable;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#isComponentVisible()
     */
    public boolean isComponentVisible() {
        return this.visible;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#setComponentExecutable(boolean)
     */
    public void setComponentExecutable(boolean executable) {
        this.executable = executable;
        setEnabled(executable);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#setComponentVisible(boolean)
     */
    public void setComponentVisible(boolean visible) {
        this.visible = visible;
        setVisible(visible);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#isManagementCalled()
     */
    public boolean isManagementCalled() {
        return managementCalled;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#setManagementCalled(boolean)
     */
    public void setManagementCalled(boolean called) {
        this.managementCalled = called;
    }

    /**
     * @see java.awt.Component#setEnabled(boolean)
     */
    @Override
    public void setEnabled(boolean b) {
        boolean x = (!isComponentExecutable()) ? false : b;
        super.setEnabled(x);
    }

    /**
     * @see java.awt.Component#setVisible(boolean)
     */
    @Override
    public void setVisible(boolean b) {
        boolean x = (!isComponentVisible()) ? false : b;
        super.setVisible(x);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKParentAware#getParentClassName()
     */
    public String getParentClassName() {
        return parentClazz;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKParentAware#setParentClassName(java.lang.String)
     */
    public void setParentClassName(String className) {
        this.parentClazz = className;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKParentAware#setParentClass(java.lang.Class)
     */
    public void setParentClass(Class<?> parentClazz) {
        this.parentClazz = (parentClazz != null) ? parentClazz.getName() : null;
    }

    /**
     * Erstellt aus dem Button eine Action mit den gleichen Eigenschaften wie der aktuelle Button. <br> Die Action ruft
     * die Methode fireActionPerformed des Buttons auf.
     *
     * @return
     */
    public AKAbstractAction createAction() {
        AKAbstractAction action = new ButtonAction(this);
        action.setName(getText());
        action.setActionCommand(getActionCommand());
        action.putValue(AKAbstractAction.SHORT_DESCRIPTION, getToolTipText());
        return action;
    }

    /* Action, die den Button aufruft. */
    static class ButtonAction extends AKAbstractAction {
        private AKJButton button = null;

        ButtonAction(AKJButton button) {
            super();
            this.button = button;
        }

        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            button.fireActionPerformed(e);
        }
    }
}
