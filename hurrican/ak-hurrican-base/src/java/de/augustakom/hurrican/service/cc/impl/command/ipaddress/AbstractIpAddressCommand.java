/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.01.2013 11:21:14
 */
package de.augustakom.hurrican.service.cc.impl.command.ipaddress;

import de.augustakom.common.service.iface.IServiceCallback;
import de.augustakom.hurrican.service.cc.impl.command.AbstractServiceCommand;

/**
 *
 */
public abstract class AbstractIpAddressCommand extends AbstractServiceCommand {

    /**
     * Key fuer die prepare-Methode, um die Session-ID des Users zu uebergeben.
     */
    public static final String KEY_SESSION_ID = "session.id";
    /**
     * Key fuer die prepare-Methode, um die ID des Ursprungs-Auftrags fuer die Physik-Aktion zu uebergeben.
     */
    public static final String KEY_AUFTRAG_ID_SRC = "auftrag.id.src";
    /**
     * Key fuer die prepare-Methode, um die ID des neuen Auftrags fuer die Physik-Aktion zu uebergeben.
     */
    public static final String KEY_AUFTRAG_ID_DEST = "auftrag.id.dest";
    /**
     * Key fuer die prepare-Methode, um das ServiceCallback-Objekt zu uebergeben.
     */
    public static final String KEY_SERVICE_CALLBACK = "service.callback";

    /**
     * Gibt die ID des Ursprungs-Auftrags zurueck.
     */
    protected Long getAuftragIdSrc() {
        return (Long) getPreparedValue(KEY_AUFTRAG_ID_SRC);
    }

    /**
     * Gibt die ID des 'neuen' Auftrags zurueck.
     */
    protected Long getAuftragIdDest() {
        return (Long) getPreparedValue(KEY_AUFTRAG_ID_DEST);
    }

    /**
     * Gibt die Session-ID des Users zurueck, der das Command ausgeloest hat.
     */
    protected Long getSessionId() {
        return (Long) getPreparedValue(KEY_SESSION_ID);
    }

    /**
     * Gibt ein ServiceCallback zurueck.
     */
    protected IServiceCallback getServiceCallback() {
        Object tmp = getPreparedValue(KEY_SERVICE_CALLBACK);
        if (tmp instanceof IServiceCallback) {
            return (IServiceCallback) tmp;
        }
        else {
            addWarning(this, "Es konnte kein ein IServiceCallback-Objekt ermittelt werden, ueber "
                    + "das der Benutzer nach der IP Adressuebernahme gefragt werden kann.");
            return null;
        }
    }

}


