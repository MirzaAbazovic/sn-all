/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.05.2004 08:28:00
 */
package de.augustakom.common.gui.swing;

import java.awt.*;
import java.beans.*;
import javax.swing.*;

import de.augustakom.common.gui.iface.AKManageableComponent;


/**
 * AK-Implementierung von <code>javax.swing.JPopupMenu</code>
 *
 *
 */
public class AKJPopupMenu extends JPopupMenu implements AKManageableComponent {

    private boolean executable = true;
    private boolean visible = true;
    private boolean managementCalled = false;

    /**
     * @see javax.swing.JPopupMenu()
     */
    public AKJPopupMenu() {
        super();
    }

    /**
     * @see javax.swing.JPopupMenu(String)
     */
    public AKJPopupMenu(String label) {
        super(label);
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
        return executable;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#isComponentVisible()
     */
    public boolean isComponentVisible() {
        return visible;
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
        if (!managementCalled) {
            for (int i = 0; i < getComponentCount(); i++) {
                Component comp = getComponent(i);
                if (comp instanceof AKJMenuItem && ((AKJMenuItem) comp).isArmed()) {
                    return ((AKJMenuItem) comp).isManagementCalled();
                }
            }
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
     * @see javax.swing.JPopupMenu#add(java.lang.String)
     */
    public JMenuItem add(String s) {
        return add(new AKJMenuItem(s));
    }

    /**
     * @see javax.swing.JPopupMenu#add(javax.swing.Action)
     */
    public JMenuItem add(Action a) {
        JMenuItem mi = createActionComponent(a);
        mi.setAction(a);
        add(mi);
        return mi;
    }

    /**
     * @see javax.swing.JPopupMenu#createActionComponent(javax.swing.Action)
     */
    protected JMenuItem createActionComponent(Action a) {
        AKJMenuItem mi = new AKJMenuItem((String) a.getValue(Action.NAME), (Icon) a.getValue(Action.SMALL_ICON)) {
            protected PropertyChangeListener createActionPropertyChangeListener(Action a) {
                PropertyChangeListener pcl = createActionChangeListener(this);
                return pcl;
            }
        };
        mi.setHorizontalTextPosition(JButton.TRAILING);
        mi.setVerticalTextPosition(JButton.CENTER);
        mi.setEnabled(a.isEnabled());
        if (a instanceof AKAbstractAction) {
            mi.getAccessibleContext().setAccessibleName(((AKAbstractAction) a).getActionCommand());
            mi.setParentClassName(((AKAbstractAction) a).getParentClassName());
        }
        return mi;
    }
}
