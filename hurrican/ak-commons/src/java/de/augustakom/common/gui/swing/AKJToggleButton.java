/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.05.2004
 */
package de.augustakom.common.gui.swing;

import javax.swing.*;
import org.apache.commons.lang.StringUtils;

import de.augustakom.common.gui.iface.AKManageableComponent;


/**
 * AK-Implementierung eines JToggleButtons.
 *
 *
 * @see javax.swing.JToggleButton
 */
public class AKJToggleButton extends JToggleButton implements AKManageableComponent {

    private boolean executable = true;
    private boolean visible = true;
    private boolean managementCalled = false;

    private String tooltipSelected = null;

    /**
     * Erzeugt einen neuen Toggle-Button.
     */
    public AKJToggleButton() {
        super();
    }

    /**
     * Erzeugt einen neuen Toggle-Button mit Text.
     *
     * @param text
     */
    public AKJToggleButton(String text) {
        super(text);
    }

    /**
     * Erzeugt einen neuen Toggle-Button mit Text und dem definierten Selektions-Status.
     *
     * @param text
     * @param selected
     */
    public AKJToggleButton(String text, boolean selected) {
        super(text, selected);
    }

    /**
     * Erzeugt einen neuen Toggle-Button, der seine Eigenschaften aus der Action bezieht.
     *
     * @param a
     */
    public AKJToggleButton(Action a) {
        super(a);
    }

    /**
     * Erzeugt einen neuen Toggle-Button mit Icon.
     *
     * @param icon
     */
    public AKJToggleButton(Icon icon) {
        super(icon);
    }

    /**
     * Erzeugt einen neuen Toggle-Button mit Icon und dem definierten Selektions-Status.
     *
     * @param icon
     * @param selected
     */
    public AKJToggleButton(Icon icon, boolean selected) {
        super(icon, selected);
    }

    /**
     * Erzeugt einen neuen Toggle-Button mit Text und Icon.
     *
     * @param text
     * @param icon
     */
    public AKJToggleButton(String text, Icon icon) {
        super(text, icon);
    }

    /**
     * Erzeugt einen neuen Toggle-Button mit Text und Icon und dem definierten Selektions-Status.
     *
     * @param text
     * @param icon
     * @param selected
     */
    public AKJToggleButton(String text, Icon icon, boolean selected) {
        super(text, icon, selected);
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
     * @see javax.swing.JComponent#getToolTipText()
     */
    public String getToolTipText() {
        if (isSelected() && StringUtils.isNotBlank(getToolTipTextSelected())) {
            return getToolTipTextSelected();
        }

        return super.getToolTipText();
    }

    /**
     * @return Returns the tooltipSelected.
     */
    public String getToolTipTextSelected() {
        return tooltipSelected;
    }

    /**
     * @param tooltipSelected The tooltipSelected to set.
     */
    public void setToolTipTextSelected(String tooltipSelected) {
        this.tooltipSelected = tooltipSelected;
    }
}
