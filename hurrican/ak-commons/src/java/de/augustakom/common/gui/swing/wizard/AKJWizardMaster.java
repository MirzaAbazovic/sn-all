/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.08.2004 11:48:09
 */
package de.augustakom.common.gui.swing.wizard;


/**
 * Interface fuer die 'Master'-Komponente des Wizards. Der 'Master' ist der Wizard-Dialog (oder ein Wizard-Frame).
 *
 *
 */
public interface AKJWizardMaster {

    /**
     * Bricht den Wizard ab.
     */
    public void cancelWizard();

    /**
     * Beendet den Wizard ueber 'finish'.
     */
    public void finishWizard();

}


