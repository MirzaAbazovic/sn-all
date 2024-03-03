/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.04.2005 07:51:02
 */
package de.augustakom.common.gui.iface;


/**
 * Interface fuer Klassen (GUI-Komponenten), die 'wissen' sollen, ob sie den Inhalt speichern duerfen.
 *
 *
 */
public interface AKSaveableAware {

    /**
     * Benachrichtigt die Klasse darueber, ob gespeichert werden darf oder nicht.
     *
     * @param saveable
     */
    public void setSaveable(boolean saveable);

}


