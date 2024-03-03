/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.07.2004 11:06:02
 */
package de.augustakom.common.gui.swing;

import java.awt.*;
import javax.swing.*;

import de.augustakom.common.gui.iface.AKColorChangeableComponent;
import de.augustakom.common.gui.iface.AKManageableComponent;
import de.augustakom.common.gui.iface.AKSwingConstants;


/**
 * AK-Implementierung von <code>JSpinner</code>.
 *
 *
 */
public class AKJSpinner extends JSpinner implements AKColorChangeableComponent,
        AKManageableComponent, AKSwingConstants {

    private Color activeColor = null;
    private Color inactiveColor = null;
    private boolean focusListenerBound = false;
    private boolean executable = true;
    private boolean visible = true;
    private boolean managementCalled = false;

    /**
     * @see javax.swing.JSpinner()
     */
    public AKJSpinner() {
        super();
    }

    /**
     * @param model
     * @see javax.swing.JSpinner(SpinnerModel)
     */
    public AKJSpinner(SpinnerModel model) {
        super(model);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKColorChangeableComponent
     */
    public void setActiveColor(Color activeColor) {
        this.activeColor = activeColor;
        boundColorChangeFocusListener();
    }

    /**
     * @see de.augustakom.common.gui.iface.AKColorChangeableComponent
     */
    public Color getActiveColor() {
        return activeColor;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKColorChangeableComponent
     */
    public void setInactiveColor(Color inactiveColor) {
        this.inactiveColor = inactiveColor;
        boundColorChangeFocusListener();
        if (this.inactiveColor != null) {
            setBackground(this.inactiveColor);
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKColorChangeableComponent
     */
    public Color getInactiveColor() {
        return inactiveColor;
    }

    /**
     * Fuegt dem TextField einmalig einen FocusListener vom Typ ChangeColorFocusListener hinzu.
     */
    private void boundColorChangeFocusListener() {
        if (!focusListenerBound) {
            addFocusListener(new AKChangeColorFocusListener());
            focusListenerBound = true;
        }
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
    public void setEnabled(boolean enabled) {
        boolean x = (!isComponentExecutable()) ? false : enabled;
        super.setEnabled(x);

        if (!x) {
            setBackground(PANEL_BACKGROUND_COLOR);
        }
        else {
            if (hasFocus()) {
                setBackground(activeColor);
            }
            else {
                setBackground(inactiveColor);
            }
        }
    }

    /**
     * @see java.awt.Component#setVisible(boolean)
     */
    public void setVisible(boolean b) {
        boolean x = (!isComponentVisible()) ? false : b;
        super.setVisible(x);
    }
}


