/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.05.2004 09:27:12
 */
package de.augustakom.authentication.gui.basics;

import javax.swing.*;

import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.service.locator.ServiceLocator;


/**
 * Ableitung von AKAbstractAction. Die Ableitung stellt zusaetzlich Methoden bereit, um einen Service zu erhalten.
 *
 *
 */
public abstract class AbstractAuthenticationServiceAction extends AKAbstractAction {

    /**
     * @see de.augustakom.common.gui.swing.AKAbstractAction()
     */
    public AbstractAuthenticationServiceAction() {
        super();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKAbstractAction(String)
     */
    public AbstractAuthenticationServiceAction(String name) {
        super(name);
    }

    /**
     * @see de.augustakom.common.gui.swing.AKAbstractAction(String, Icon)
     */
    public AbstractAuthenticationServiceAction(String name, Icon icon) {
        super(name, icon);
    }

    /**
     * Laedt den Service <code>serviceName</code> aus dem <code>AKAuthenticationServiceLocator</code> und gibt ihn
     * zurueck. <br> Ueber die Services, die diese Methode zurueck gibt, koennen die Children des Trees geladen werden.
     *
     * @param serviceName
     * @param type
     * @return
     */
    protected <T> T getAuthenticationService(String serviceName, Class<T> type) throws ServiceNotFoundException {
        return ServiceLocator.instance().getService(serviceName, type);
    }
}
