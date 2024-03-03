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
 * AK-Implementierung eines JMenus.
 *
 *
 * @see javax.swing.JMenu
 */
public class AKJMenu extends JMenu implements AKManageableComponent, AKParentAware {

    private boolean executable = true;
    private boolean visible = true;
    private boolean managementCalled = false;
    private String parentClazz = null;

    /**
     * Erzeugt ein neues JMenu.
     */
    public AKJMenu() {
        super();
    }

    /**
     * Erzeugt ein neues JMenu mit Angabe des Namens.
     *
     * @param s
     */
    public AKJMenu(String s) {
        super(s);
    }

    /**
     * Erzeugt ein neues JMenu mit Angabe des Namens und ob es ein tear-off Menu ist. <br> (Hinweis: tear-off ist z.Z.
     * vom JDK noch nicht implementiert.)
     *
     * @param s
     * @param b
     */
    public AKJMenu(String s, boolean b) {
        super(s, b);
    }

    /**
     * Erzeugt ein neues JMenu, das sich die Eigenschaften aus der Action holt.
     *
     * @param a
     */
    public AKJMenu(Action a) {
        super(a);
    }

    /**
     * @see javax.swing.JMenu#add(javax.swing.Action)
     */
    public JMenuItem add(Action a) {
        if (a instanceof AKAbstractAction) {
            Object value = ((AKAbstractAction) a).getValue(AKAbstractAction.ADD_SEPARATOR);
            if (value instanceof Boolean && ((Boolean) value).booleanValue()) {
                addSeparator();
            }
        }
        return super.add(a);
    }

    /**
     * @see javax.swing.JMenu#add(javax.swing.JMenuItem)
     */
    public JMenuItem add(JMenuItem menuItem) {
        Action a = menuItem.getAction();
        if (a instanceof AKAbstractAction) {
            Object value = ((AKAbstractAction) a).getValue(AKAbstractAction.ADD_SEPARATOR);
            if (value instanceof Boolean && ((Boolean) value).booleanValue()) {
                addSeparator();
            }
        }
        return super.add(menuItem);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#getComponentName()
     */
    public String getComponentName() {
        return (getAccessibleContext() != null && getAccessibleContext().getAccessibleName() != null)
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
