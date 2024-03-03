/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.06.2004 10:58:42
 */
package de.augustakom.common.gui.iface;


/**
 * Interface fuer GUI-Elemente, deren Verhalten ueber ein Berechtigungssystem veraendert werden kann. <br>
 */
public interface AKManageableComponent {

    /**
     * Gibt den Namen der Komponente zurueck.
     *
     * @return Name der Komponente
     */
    String getComponentName();

    /**
     * Bestimmt, ob die Komponente sichtbar sein soll.
     *
     * @param visible true Komponente soll/darf sichtbar sein.
     */
    void setComponentVisible(boolean visible);

    /**
     * Gibt an, ob die Komponente sichtbar sein soll/darf. <br> Der Wert gibt nicht an, ob die Komponente gerade
     * sichtbar ist, sondern ob sie sichtbar sein kann!
     *
     * @return true Komponente soll/darf sichtbar sein.
     */
    boolean isComponentVisible();

    /**
     * Bestimmt, ob die Komponente ausgefuehrt werden darf.
     *
     * @param executable true Komponente darf ausgefuehrt werden.
     */
    void setComponentExecutable(boolean executable);

    /**
     * Gibt an, ob die Komponente ausgefuehrt werden kann/darf. <br> Der Wert gibt nicht an, ob die Komponente gerade
     * ausfuehrbar ist, sondern ob sie ausfuehrbar sein kann!
     *
     * @return true Komponente kann/darf ausgefuehrt werden.
     */
    boolean isComponentExecutable();

    /**
     * Wird auf <code>true</code> gesetzt, wenn die Komponente auf ihr Rollen-Verhalten ueberprueft wird.
     */
    void setManagementCalled(boolean called);

    /**
     * Gibt <code>true</code> zurueck, wenn das Rollen-Verhalten der Komponente bereits einmal geprueft wurde.
     */
    boolean isManagementCalled();

}


