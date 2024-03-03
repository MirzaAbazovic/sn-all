/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.06.2005 18:51:06
 */
package de.augustakom.hurrican.model.cc.view;

import java.util.*;

import de.augustakom.hurrican.model.cc.AbstractCCModel;


/**
 * Modell enthaelt Daten fuer die Anzahl der durchgefuehrten CuDA-Bestellungen zu einem best. Schaltungstermin.
 *
 *
 */
public class CuDAVorschau extends AbstractCCModel {

    private Date vorgabeSCV = null;
    private Integer anzahlCuDA = null;

    /**
     * @return Returns the anzahlCuDA.
     */
    public Integer getAnzahlCuDA() {
        return anzahlCuDA;
    }

    /**
     * @param anzahlCuDA The anzahlCuDA to set.
     */
    public void setAnzahlCuDA(Integer anzahlCuDA) {
        this.anzahlCuDA = anzahlCuDA;
    }

    /**
     * @return Returns the vorgabeSCV.
     */
    public Date getVorgabeSCV() {
        return vorgabeSCV;
    }

    /**
     * @param vorgabeSCV The vorgabeSCV to set.
     */
    public void setVorgabeSCV(Date vorgabeSCV) {
        this.vorgabeSCV = vorgabeSCV;
    }

}


