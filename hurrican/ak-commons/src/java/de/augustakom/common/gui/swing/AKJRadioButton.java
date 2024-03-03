/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.05.2004
 */
package de.augustakom.common.gui.swing;

import javax.swing.*;

import de.augustakom.common.gui.iface.AKManageableComponent;


/**
 * AK-Implementierung eines JRadioButtons.
 *
 *
 * @see javax.swing.JRadioButton
 */
public class AKJRadioButton extends JRadioButton implements AKManageableComponent {

    private boolean executable = true;
    private boolean visible = true;
    private boolean managementCalled = false;

    private Object valueObject = null;

    /**
     * Erzeugt einen neuen RadioButton.
     */
    public AKJRadioButton() {
        super();
    }

    /**
     * Erzeugt einen neuen RadioButton.
     *
     * @param bg Angabe der Button-Group
     */
    public AKJRadioButton(ButtonGroup bg) {
        super();
        if (bg != null) {
            bg.add(this);
        }
    }

    /**
     * Erzeugt einen neuen RadioButton mit Angabe des anzuzeigenden Texts.
     *
     * @param text
     */
    public AKJRadioButton(String text) {
        super(text);
    }

    /**
     * Erzeugt einen neuen RadioButton mit Text. Es kann definiert werden, ob der RadioButton selektiert sein soll oder
     * nicht.
     *
     * @param text
     * @param selected
     */
    public AKJRadioButton(String text, boolean selected) {
        super(text, selected);
    }

    /**
     * Erzeugt einen RadioButton, der seine Eigenschaften aus der Action bezieht.
     *
     * @param a
     */
    public AKJRadioButton(Action a) {
        super(a);
    }

    /**
     * Erzeugt einen RadioButton mit Icon.
     *
     * @param icon
     */
    public AKJRadioButton(Icon icon) {
        super(icon);
    }

    /**
     * Erzeugt einen RadioButton mit Icon. Es kann definiert werden, ob der RadioButton selektiert sein soll oder
     * nicht.
     *
     * @param icon
     * @param selected
     */
    public AKJRadioButton(Icon icon, boolean selected) {
        super(icon, selected);
    }

    /**
     * Erzeugt einen RadioButton mit Text und Icon.
     *
     * @param text
     * @param icon
     */
    public AKJRadioButton(String text, Icon icon) {
        super(text, icon);
    }

    /**
     * Erzeugt einen RadioButton mit Text und Icon. Es kann definiert werden, ob der RadioButton selektiert sein soll
     * oder nicht.
     *
     * @param text
     * @param icon
     * @param selected
     */
    public AKJRadioButton(String text, Icon icon, boolean selected) {
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
     * @return Returns the valueObject.
     */
    public Object getValueObject() {
        return valueObject;
    }

    /**
     * @param valueObject The valueObject to set.
     */
    public void setValueObject(Object valueObject) {
        this.valueObject = valueObject;
    }

}
