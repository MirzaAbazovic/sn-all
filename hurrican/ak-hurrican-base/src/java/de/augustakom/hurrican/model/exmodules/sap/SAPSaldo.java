/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.08.2007 08:36:23
 */
package de.augustakom.hurrican.model.exmodules.sap;


/**
 * Modell fuer einen SAP-Saldo
 *
 *
 */
public class SAPSaldo extends AbstractSAPModel {

    // SAP-Header Informationen
    // Aktueller Saldo
    public static final String SALDO = "BALANCE";
    // Währung
    public static final String WAEHRUNG = "CURRENCY";

    private Float saldo = null;
    private String waehrung = null;

    /**
     * @return saldo
     */
    public Float getSaldo() {
        return saldo;
    }

    /**
     * @param saldo Festzulegender saldo
     */
    public void setSaldo(Float saldo) {
        this.saldo = saldo;
    }

    /**
     * @return waehrung
     */
    public String getWaehrung() {
        return waehrung;
    }

    /**
     * @param waehrung Festzulegender waehrung
     */
    public void setWaehrung(String waehrung) {
        this.waehrung = waehrung;
    }


}


