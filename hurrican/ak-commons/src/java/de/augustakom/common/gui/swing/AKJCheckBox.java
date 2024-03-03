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
 * AK-Implementierung einer JCheckBox.
 *
 *
 * @see javax.swing.JCheckBox
 */
public class AKJCheckBox extends JCheckBox implements AKManageableComponent {

    private static final long serialVersionUID = 8486778479277150933L;

    private boolean executable = true;
    private boolean visible = true;
    private boolean managementCalled = false;

    private boolean selectionValueNull = false;

    /**
     * Erzeugt eine 'leere' CheckBox.
     */
    public AKJCheckBox() {
        super();
    }

    /**
     * Erzeugt eine CheckBox mit dem angegebenen Text.
     *
     * @param text
     */
    public AKJCheckBox(String text) {
        super(text);
    }

    /**
     * Erzeugt eine CheckBox mit Text. Zusaetzlich wird hier definiert, ob die CheckBox selektiert sein soll oder
     * nicht.
     *
     * @param text
     * @param selected
     */
    public AKJCheckBox(String text, boolean selected) {
        super(text, selected);
    }

    /**
     * Erzeugt eine CheckBox, die ihre Eigenschaften aus der Action holt.
     *
     * @param a
     */
    public AKJCheckBox(Action a) {
        super(a);
    }

    /**
     * Erzeugt eine CheckBox mit Icon.
     *
     * @param icon
     */
    public AKJCheckBox(Icon icon) {
        super(icon);
    }

    /**
     * Erzeugt eine CheckBox mit Icon. Zusaetzlich wird hier definiert, ob die CheckBox selektiert sein soll oder
     * nicht.
     *
     * @param icon
     * @param selected
     */
    public AKJCheckBox(Icon icon, boolean selected) {
        super(icon, selected);
    }

    /**
     * Erzeugt eine CheckBox mit Text und Icon.
     *
     * @param text
     * @param icon
     */
    public AKJCheckBox(String text, Icon icon) {
        super(text, icon);
    }

    /**
     * Erzeugt eine CheckBox mit Text und Icon. Zusaetzlich wird hier definiert, ob die CheckBox selektiert sein soll
     * oder nicht.
     *
     * @param text
     * @param icon
     * @param selected
     */
    public AKJCheckBox(String text, Icon icon, boolean selected) {
        super(text, icon, selected);
    }

    /**
     * @see javax.swing.AbstractButton#setSelected(boolean)
     */
    public void setSelected(Boolean b) {
        selectionValueNull = (b == null) ? true : false;
        setSelected((b != null) ? b.booleanValue() : false);
    }

    /**
     * @return Wert als Boolean-Objekt oder <code>null</code> wenn die Selection vorher entsprechend gesetzt wurde
     * @see javax.swing.AbstractButton#isSelected
     */
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "NP_BOOLEAN_RETURN_NULL", justification = "implementation intended")
    public Boolean isSelectedBoolean() {
        if (!isSelected() && selectionValueNull) {
            return null;
        }
        return (isSelected()) ? Boolean.TRUE : Boolean.FALSE;
    }

    /**
     * @return Wert als Boolean-Objekt
     * @see javax.swing.AbstractButton#isSelected
     */
    public Boolean isSelectedBoolean(Boolean defaultValue) {
        Boolean result = isSelectedBoolean();
        return (result == null) ? defaultValue : result;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#getComponentName()
     */
    @Override
    public String getComponentName() {
        return ((getAccessibleContext() != null) && (getAccessibleContext().getAccessibleName() != null))
                ? getAccessibleContext().getAccessibleName() : getName();
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#isComponentExecutable()
     */
    @Override
    public boolean isComponentExecutable() {
        return this.executable;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#isComponentVisible()
     */
    @Override
    public boolean isComponentVisible() {
        return this.visible;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#setComponentExecutable(boolean)
     */
    @Override
    public void setComponentExecutable(boolean executable) {
        this.executable = executable;
        setEnabled(executable);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#setComponentVisible(boolean)
     */
    @Override
    public void setComponentVisible(boolean visible) {
        this.visible = visible;
        setVisible(visible);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#isManagementCalled()
     */
    @Override
    public boolean isManagementCalled() {
        return managementCalled;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#setManagementCalled(boolean)
     */
    @Override
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
}
