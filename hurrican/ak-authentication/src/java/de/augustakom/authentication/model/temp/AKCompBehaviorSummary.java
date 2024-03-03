/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.02.2008 11:45:07
 */
package de.augustakom.authentication.model.temp;

import de.augustakom.authentication.model.AbstractAuthenticationModel;
import de.augustakom.common.gui.iface.AKManageableComponent;


/**
 * Temporaeres Modell, in dem das GUI-Verhalten einer Komponente fuer einen bestimmten User hinterlegt ist.
 *
 *
 */
public class AKCompBehaviorSummary extends AbstractAuthenticationModel implements AKManageableComponent {

    private String componentName = null;
    private String parentClass = null;
    private boolean visible = false;
    private boolean executable = false;
    private boolean managementCalled = false;

    /**
     * @return Returns the parentClass.
     */
    public String getParentClass() {
        return parentClass;
    }

    /**
     * @param parentClass The parentClass to set.
     */
    public void setParentClass(String parentClass) {
        this.parentClass = parentClass;
    }

    /**
     * @param componentName The componentName to set.
     */
    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#getComponentName()
     */
    public String getComponentName() {
        return componentName;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#setComponentExecutable(boolean)
     */
    public void setComponentExecutable(boolean executable) {
        this.executable = executable;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#isComponentExecutable()
     */
    public boolean isComponentExecutable() {
        return (managementCalled) ? executable : false;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#setComponentVisible(boolean)
     */
    public void setComponentVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKManageableComponent#isComponentVisible()
     */
    public boolean isComponentVisible() {
        return (managementCalled) ? visible : false;
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

}


