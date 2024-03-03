/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.08.2005 11:26:08
 */
package de.augustakom.common.concurrent;

import de.augustakom.common.service.iface.IServiceObject;


/**
 * Ein <code>ServiceProgress</code>-Event wird erstellt, wenn ein Service seine Listener (vom Typ
 * IServiceProgressListener) ueber einen best. Fortschritt informiert. <br> Ueber das Event-Objekt ist ersichtlich,
 * welcher Service das Event geschickt. Ausserdem ist eine Information ueber den aktuellen Service-Status (bzw. den
 * Status der aufgerufenen Methode) enthalten.
 */
public class MethodProgressEvent {

    private IServiceObject serviceObject = null;
    private String progressInfo = null;

    /**
     * Konstruktor mit Angabe aller Parameter fuer das Event-Objekt.
     *
     * @param serviceObject
     * @param info
     */
    public MethodProgressEvent(IServiceObject serviceObject, String info) {
        super();
        this.serviceObject = serviceObject;
        this.progressInfo = info;
    }

    /**
     * @return Returns the progressInfo.
     */
    public String getProgressInfo() {
        return progressInfo;
    }

    /**
     * @param progressInfo The progressInfo to set.
     */
    public void setProgressInfo(String progressInfo) {
        this.progressInfo = progressInfo;
    }

    /**
     * @return Returns the serviceObject.
     */
    public IServiceObject getServiceObject() {
        return serviceObject;
    }

    /**
     * @param serviceObject The serviceObject to set.
     */
    public void setServiceObject(IServiceObject serviceObject) {
        this.serviceObject = serviceObject;
    }

}
