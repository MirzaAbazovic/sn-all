/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.07.2006 14:36:25
 */
package de.augustakom.common.service.base;

import java.io.*;


/**
 * Modell, um von einem Check-Command ein Result zu erhalten.
 *
 *
 */
public class ServiceCommandResult implements Serializable {

    /**
     * Wert fuer 'checkStatus', wenn der Status unbekannt ist.
     */
    public static final int CHECK_STATUS_UNKNOWN = -1;
    /**
     * Wert fuer 'checkStatus', wenn der Status o.k. ist.
     */
    public static final int CHECK_STATUS_OK = 0;
    /**
     * Wert fuer 'checkStatus', wenn der Status nicht o.k. ist.
     */
    public static final int CHECK_STATUS_INVALID = 1;

    private Class<?> commandClass = null;
    private int checkStatus = -1;
    private String message = null;
    private Object resultObject = null;

    /**
     * Generiert ein Objekt vom Typ <code>ServiceCommandResult</code> mit den angegebenen Parametern und gibt dieses
     * zurueck.
     *
     * @param status  Status des Commands
     * @param message generierte Message
     * @return Objekt vom Typ <code>ServiceCommandResult</code>
     *
     */
    public static <T> ServiceCommandResult createCmdResult(int status, String message, Class<T> commandClass) {
        ServiceCommandResult cmdResult = new ServiceCommandResult();
        cmdResult.setCheckStatus(status);
        cmdResult.setMessage(message);
        cmdResult.setCommandClass(commandClass);
        return cmdResult;
    }

    /**
     * Generiert ein Objekt vom Typ <code>ServiceCommandResult</code> mit den angegebenen Parametern und gibt dieses
     * zurueck.
     *
     * @param status    Status des Commands
     * @param message   generierte Message
     * @param resultObj spezifisches Result-Objekt des Service-Commands
     * @return Objekt vom Typ <code>ServiceCommandResult</code>
     *
     */
    public static <T> ServiceCommandResult createCmdResult(int status, String message, Class<T> commandClass, Object resultObj) {
        ServiceCommandResult cmdResult = new ServiceCommandResult();
        cmdResult.setCheckStatus(status);
        cmdResult.setMessage(message);
        cmdResult.setCommandClass(commandClass);
        cmdResult.setResultObject(resultObj);
        return cmdResult;
    }

    /**
     * Prueft, ob der Check-Status 'o.k.' ist.
     *
     * @return
     *
     */
    public boolean isOk() {
        if (getCheckStatus() != ServiceCommandResult.CHECK_STATUS_OK) {
            return false;
        }
        return true;
    }

    /**
     * @return Returns the checkStatus.
     */
    public int getCheckStatus() {
        return this.checkStatus;
    }

    /**
     * @param checkStatus The checkStatus to set.
     */
    public void setCheckStatus(int checkStatus) {
        this.checkStatus = checkStatus;
    }

    /**
     * @return Returns the commandClass.
     */
    public Class<?> getCommandClass() {
        return this.commandClass;
    }

    /**
     * @param commandClass The commandClass to set.
     */
    public void setCommandClass(Class<?> commandClass) {
        this.commandClass = commandClass;
    }

    /**
     * @return Returns the message.
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * @param message The message to set.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the resultObject
     */
    public Object getResultObject() {
        return resultObject;
    }

    /**
     * Ueber diese Methode kann ein Command dem ServiceCommandResult-Objekt ein spezifisches Result-Objekt uebergeben.
     * <br> Der Caller der Service-Chain kann dieses Objekt dann wieder auswerten/verwenden.
     *
     * @param resultObject the resultObject to set
     */
    public void setResultObject(Object resultObject) {
        this.resultObject = resultObject;
    }

}


