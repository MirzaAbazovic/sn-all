/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.02.2005 09:14:50
 */
package de.augustakom.common.gui.swing.wizard;


/**
 * Exception, um einen Wizard daran zu hindern, sich zu beenden.
 *
 *
 */
public class AKJWizardFinishVetoException extends Exception {

    /**
     * Default-Konstruktor.
     */
    public AKJWizardFinishVetoException() {
        super();
    }

    /**
     * @param message
     */
    public AKJWizardFinishVetoException(String message) {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public AKJWizardFinishVetoException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause
     */
    public AKJWizardFinishVetoException(Throwable cause) {
        super(cause);
    }

}


