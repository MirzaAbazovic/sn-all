/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.08.2005 11:23:58
 */
package de.augustakom.common.concurrent.iface;

import de.augustakom.common.concurrent.MethodProgressEvent;


/**
 * Listener-Interface fuer Objekte, die ueber einen Fortschritt innerhalb einer Methode informiert werden
 * wollen/sollen.
 */
public interface IMethodProgressListener {

    /**
     * Wird aufgerufen, wenn ein best. Fortschritt einer Methode signalisiert werden soll.
     */
    void methodProgress(MethodProgressEvent event);

    /**
     * Wird aufgerufen, wenn eine Methode ihre 'Arbeit' getan hat.
     */
    void methodFinished();

}


