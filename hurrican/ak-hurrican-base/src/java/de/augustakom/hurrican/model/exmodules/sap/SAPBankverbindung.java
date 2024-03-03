/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.08.2007 08:36:23
 */
package de.augustakom.hurrican.model.exmodules.sap;


/**
 * Modell fuer einen SAP-Buchungssatz
 *
 *
 */
public class SAPBankverbindung extends AbstractSAPModel {

    // SAP-Header Informationen
    // Bankkonto
    public static final String ACCOUNT = "BANK_ACCT";
    // Bankleitzahl
    public static final String BLZ = "BANK_KEY";
    // Lastschriftverfahren
    public static final String COLL_AUTH = "COLL_AUTH";
    // Bankland
    public static final String BANK_LAND = "BANK_CTRY";

    private Long account = null;
    private Long blz = null;
    private String collAuth = null;
    private String bankLand = null;


    /**
     * @return account
     */
    public Long getAccount() {
        return account;
    }

    /**
     * @param account Festzulegender account
     */
    public void setAccount(Long account) {
        this.account = account;
    }

    /**
     * @return bankLand
     */
    public String getBankLand() {
        return bankLand;
    }

    /**
     * @param bankLand Festzulegender bankLand
     */
    public void setBankLand(String bankLand) {
        this.bankLand = bankLand;
    }

    /**
     * @return blz
     */
    public Long getBlz() {
        return blz;
    }

    /**
     * @param blz Festzulegender blz
     */
    public void setBlz(Long blz) {
        this.blz = blz;
    }

    /**
     * @return collAuth
     */
    public String getCollAuth() {
        return collAuth;
    }

    /**
     * @param collAuth Festzulegender collAuth
     */
    public void setCollAuth(String collAuth) {
        this.collAuth = collAuth;
    }


}


