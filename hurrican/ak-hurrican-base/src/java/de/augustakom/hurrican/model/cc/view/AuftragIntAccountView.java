/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.08.2004 10:03:33
 */
package de.augustakom.hurrican.model.cc.view;

import de.augustakom.hurrican.model.cc.AbstractCCModel;


/**
 * View-Modell, um Auftrags- und IntAccount-Daten darzustellen.
 *
 *
 */
public class AuftragIntAccountView extends AbstractCCModel {

    private Long auftragId = null;
    private Long auftragNoOrig = null;
    private Long accountId = null;
    private Integer accountLiNr = null;
    private String account = null;
    private String accountPasswort = null;
    private String accountRufnummer = null;
    private String anschlussart = null;
    private String vbz = null;
    private Long vpnId = null;
    private Long prodId = null;

    /**
     * @return Returns the account.
     */
    public String getAccount() {
        return account;
    }

    /**
     * @param account The account to set.
     */
    public void setAccount(String account) {
        this.account = account;
    }

    /**
     * @return Returns the accountId.
     */
    public Long getAccountId() {
        return accountId;
    }

    /**
     * @param accountId The accountId to set.
     */
    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    /**
     * @return Returns the accountRufnummer.
     */
    public String getAccountRufnummer() {
        return accountRufnummer;
    }

    /**
     * @param accountRufnummer The accountRufnummer to set.
     */
    public void setAccountRufnummer(String accountRufnummer) {
        this.accountRufnummer = accountRufnummer;
    }

    /**
     * @return Returns the anschlussart.
     */
    public String getAnschlussart() {
        return anschlussart;
    }

    /**
     * @param anschlussart The anschlussart to set.
     */
    public void setAnschlussart(String anschlussart) {
        this.anschlussart = anschlussart;
    }

    /**
     * @return Returns the auftragId.
     */
    public Long getAuftragId() {
        return auftragId;
    }

    /**
     * @param auftragId The auftragId to set.
     */
    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    /**
     * @return Returns the auftragNoOrig.
     */
    public Long getAuftragNoOrig() {
        return auftragNoOrig;
    }

    /**
     * @param auftragNoOrig The auftragNoOrig to set.
     */
    public void setAuftragNoOrig(Long auftragNoOrig) {
        this.auftragNoOrig = auftragNoOrig;
    }

    /**
     * @return Returns the verbindungsBezeichnung.
     */
    public String getVbz() {
        return vbz;
    }

    /**
     * @param verbindungsBezeichnung The verbindungsBezeichnung to set.
     */
    public void setVbz(String vbz) {
        this.vbz = vbz;
    }

    /**
     * @return Returns the vpnId.
     */
    public Long getVpnId() {
        return vpnId;
    }

    /**
     * @param vpnId The vpnId to set.
     */
    public void setVpnId(Long vpnId) {
        this.vpnId = vpnId;
    }

    /**
     * @return Returns the accountPasswort.
     */
    public String getAccountPasswort() {
        return accountPasswort;
    }

    /**
     * @param accountPasswort The accountPasswort to set.
     */
    public void setAccountPasswort(String accountPasswort) {
        this.accountPasswort = accountPasswort;
    }

    /**
     * @return Returns the accountLiNr.
     */
    public Integer getAccountLiNr() {
        return accountLiNr;
    }

    /**
     * @param accountLiNr The accountLiNr to set.
     */
    public void setAccountLiNr(Integer accountLiNr) {
        this.accountLiNr = accountLiNr;
    }

    /**
     * @return Returns the prodId.
     */
    public Long getProdId() {
        return this.prodId;
    }

    /**
     * @param prodId The prodId to set.
     */
    public void setProdId(Long prodId) {
        this.prodId = prodId;
    }
}


