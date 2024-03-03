/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.03.2005 11:54:30
 */
package de.augustakom.common.service.iface;

import java.util.*;


/**
 * Callback-Interface fuer Services, um an einen Caller eine Aktion auszufuehren.
 *
 *
 */
public interface IServiceCallback {

    /**
     * Wird von einem Service aufgerufen, um eine Aktion ausfuehren zu lassen.
     *
     * @param source         Service, der die Aktion aufruft
     * @param callbackAction Art der auszufuehrenden Callback-Action (sollte als Konstante in dem jeweiligen
     *                       Service-Interface definiert sein.
     * @param parameters     Map mit Parametern fuer das Callback-Objekt
     * @return Ergebnis des Callback-Aufrufs.
     */
    public Object doServiceCallback(Object source, int callbackAction, Map<String, ?> parameters);

}


