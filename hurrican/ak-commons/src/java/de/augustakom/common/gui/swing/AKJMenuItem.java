/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.05.2004
 */
package de.augustakom.common.gui.swing;

import javax.swing.*;

import de.augustakom.common.gui.iface.AKManageableComponent;
import de.augustakom.common.gui.iface.AKParentAware;


/**
 * AK-Implementierung eines JMenuItems.
 *
 *
 * @see javax.swing.JMenuItem
 */
public class AKJMenuItem extends JMenuItem implements AKManageableComponent, AKParentAware {

    private boolean executable = true;
    private boolean visible = true;
    private boolean managementCalled = false;
    private String parentClazz = null;

    /**
     * Erzeugt ein neues MenuItem.
     */
    public AKJMenuItem() {
        super();
    }

    /**
     * Erzeugt ein neues MenuItem mit Text.
     *
     * @param text
     */
    public AKJMenuItem(String text) {
        super(text);
    }

    /**
     * Erzeugt ein neues MenuItem mit Text und Keyboard-Mnemonic.
     *
     * @param text
     * @param mnemonic
     */
    public AKJMenuItem(String text, int mnemonic) {
        super(text, mnemonic);
    }

    /**
     * Erzeugt ein neues MenuItem, das seine Eigenschaften aus einer Action bezieht.
     *
     * @param a
     */
    public AKJMenuItem(Action a) {
        super();
        setAction(a);
    }

    /**
     * Erzeugt ein neues MenuItem mit Icon.
     *
     * @param icon
     */
    public AKJMenuItem(Icon icon) {
        super(icon);
    }

    /**
     * Erzeugt ein neues MenuItem mit Text und Icon.
     *
     * @param text
     * @param icon
     */
    public AKJMenuItem(String text, Icon icon) {
        super(text, icon);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#getComponentName()
     */
    public String getComponentName() {
        return getActionCommand();
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
        if (!managementCalled && getAction() instanceof AKAbstractAction) {
            return ((AKAbstractAction) getAction()).isManagementCalled();
        }
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
    public void setEnabled(boolean b) {
        boolean x = (!isComponentExecutable()) ? false : b;
        super.setEnabled(x);
    }

    /**
     * @see java.awt.Component#setVisible(boolean)
     */
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
}
