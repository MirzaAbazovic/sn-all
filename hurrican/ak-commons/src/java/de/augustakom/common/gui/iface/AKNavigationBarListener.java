/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.06.2004 11:14:47
 */
package de.augustakom.common.gui.iface;

import java.beans.*;


/**
 * Listener-Interface fuer Objekte, die ueber Aenderungen in einer Navigation-Bar informiert werden wollen.
 *
 *
 */
public interface AKNavigationBarListener {

    /**
     * Wird von der NavigationBar aufgerufen. <br> Die Listener-Implementierung soll das Objekt <code>obj</code>
     * anzeigen.
     *
     * @param obj    Objekt, auf das navigiert wurde
     * @param number Nummer des Objekts in der Liste.
     * @throws PropertyVetoException wenn der Listener der Navigation 'widerspricht'. Dies kann z.B. sein, wenn der
     *                               Listener erkennt, dass der vorherige Datensatz noch nicht gespeichert ist und
     *                               deshalb nicht zu einem neuen Datensatz navigiert werden soll. Die Anzeige des
     *                               Counts (x von y) wird nach dem Auftreten dieser Exception wieder auf den vorherigen
     *                               Wert gesetzt. Die Listener muss jedoch selbst dafuer Sorge tragen, dass das
     *                               vorherige Objekt angezeigt bleibt!
     */
    public void showNavigationObject(Object obj, int number) throws PropertyVetoException;

}


