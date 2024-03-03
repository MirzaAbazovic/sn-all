/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.01.2007 07:36:15
 */
package de.augustakom.common.model;


/**
 * Interface fuer Query-Objekte.
 *
 *
 */
public interface IQuery {

    /**
     * Ueberprueft, ob das Query-Objekt mit Daten gefuellt ist oder 'leer' ist. <br> Fuer diese Pruefung kann nicht die
     * equals-Methode verwendet werden, da z.B. ein String auf <code>null</code> und Leerstring ueberprueft werden
     * soll.
     *
     * @return <code>true</code> wenn das Query-Objekt 'leer' ist.
     */
    public abstract boolean isEmpty();

}


