/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.07.2007 14:36:14
 */
package de.augustakom.hurrican.model.billing;


/**
 * Modell zur Abbildung eines Finanz-Objektes aus Taifun.
 *
 *
 */
public class Finanz extends AbstractBillingModel {

    private Long finanzNo = null;
    private Long kundeNo = null;
    private String zahlungArt = null;
    private Long blz = null;
    private String kontoNo = null;
    private String kontoInhaber = null;
    private Long zahlungsFrist = null;

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
     * @return finanzNo
     */
    public Long getFinanzNo() {
        return finanzNo;
    }

    /**
     * @param finanzNo Festzulegender finanzNo
     */
    public void setFinanzNo(Long finanzNo) {
        this.finanzNo = finanzNo;
    }

    /**
     * @return kontoInhaber
     */
    public String getKontoInhaber() {
        return kontoInhaber;
    }

    /**
     * @param kontoInhaber Festzulegender kontoInhaber
     */
    public void setKontoInhaber(String kontoInhaber) {
        this.kontoInhaber = kontoInhaber;
    }

    /**
     * @return kontoNo
     */
    public String getKontoNo() {
        return kontoNo;
    }

    /**
     * @param kontoNo Festzulegender kontoNo
     */
    public void setKontoNo(String kontoNo) {
        this.kontoNo = kontoNo;
    }

    /**
     * @return kundeNo
     */
    public Long getKundeNo() {
        return kundeNo;
    }

    /**
     * @param kundeNo Festzulegender kundeNo
     */
    public void setKundeNo(Long kundeNo) {
        this.kundeNo = kundeNo;
    }

    /**
     * @return zahlungArt
     */
    public String getZahlungArt() {
        return zahlungArt;
    }

    /**
     * @param zahlungArt Festzulegender zahlungArt
     */
    public void setZahlungArt(String zahlungArt) {
        this.zahlungArt = zahlungArt;
    }

    /**
     * @return zahlungsFrist
     */
    public Long getZahlungsFrist() {
        return zahlungsFrist;
    }

    /**
     * @param zahlungsFrist Festzulegender zahlungsFrist
     */
    public void setZahlungsFrist(Long zahlungsFrist) {
        this.zahlungsFrist = zahlungsFrist;
    }


}


