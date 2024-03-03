/*
 /*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 */
package de.augustakom.hurrican.model.cc.view;

import java.util.*;

import de.augustakom.hurrican.model.base.AbstractHurricanModel;

/**
 * Modell für den Export von Rangierungsdaten an Command
 *
 *
 */
public class FTTXRangierDatenView extends AbstractHurricanModel {
    // Standort
    private String standort = null;

    // FTTB (Technik MDU)
    private String mdu = null;
    private String mduSeriennummer = null;
    private String mduTyp = null;
    private String mduPort = null;
    private String leisteWohnung = null;
    private String stiftWohnung = null;
    private String leisteDTAG = null;
    private String stiftDTAG = null;

    // FTTH (Technik ONT)
    private String ont = null;
    private String ontSerienNummer = null;
    private String ontTyp = null;

    // Auftrag
    private Integer auftragId = null;
    private Long orderNo = null;
    private String verbindungsbezeichnung = null;
    private String status = null;

    public static final String STATUS_KUENDIGUNG = "kuendigung";
    public static final String STATUS_NEUSCHALTUNG = "neuschaltung";

    public static final List<String> POSSIBLE_STATES = Arrays.asList(STATUS_NEUSCHALTUNG, STATUS_KUENDIGUNG);


    /**
     * @return the standort
     */
    public String getStandort() {
        return standort;
    }

    /**
     * @param standort the standort to set
     */
    public void setStandort(String standort) {
        this.standort = standort;
    }

    /**
     * @return the mdu
     */
    public String getMdu() {
        return mdu;
    }

    /**
     * @param mdu the mdu to set
     */
    public void setMdu(String mdu) {
        this.mdu = mdu;
    }

    /**
     * @return the mduSeriennummer
     */
    public String getMduSeriennummer() {
        return mduSeriennummer;
    }

    /**
     * @param mduSeriennummer the mduSeriennummer to set
     */
    public void setMduSeriennummer(String mduSeriennummer) {
        this.mduSeriennummer = mduSeriennummer;
    }

    /**
     * @return the mduTyp
     */
    public String getMduTyp() {
        return mduTyp;
    }

    /**
     * @param mduTyp the mduTyp to set
     */
    public void setMduTyp(String mduTyp) {
        this.mduTyp = mduTyp;
    }

    /**
     * @return the mduPort
     */
    public String getMduPort() {
        return mduPort;
    }

    /**
     * @param mduPort the mduPort to set
     */
    public void setMduPort(String mduPort) {
        this.mduPort = mduPort;
    }

    /**
     * @return the leisteWohnung
     */
    public String getLeisteWohnung() {
        return leisteWohnung;
    }

    /**
     * @param leisteWohnung the leisteWohnung to set
     */
    public void setLeisteWohnung(String leisteWohnung) {
        this.leisteWohnung = leisteWohnung;
    }

    /**
     * @return the stiftWohnung
     */
    public String getStiftWohnung() {
        return stiftWohnung;
    }

    /**
     * @param stiftWohnung the stiftWohnung to set
     */
    public void setStiftWohnung(String stiftWohnung) {
        this.stiftWohnung = stiftWohnung;
    }

    /**
     * @return the leisteDTAG
     */
    public String getLeisteDTAG() {
        return leisteDTAG;
    }

    /**
     * @param leisteDTAG the leisteDTAG to set
     */
    public void setLeisteDTAG(String leisteDTAG) {
        this.leisteDTAG = leisteDTAG;
    }

    /**
     * @return the stiftDTAG
     */
    public String getStiftDTAG() {
        return stiftDTAG;
    }

    /**
     * @param stiftDTAG the stiftDTAG to set
     */
    public void setStiftDTAG(String stiftDTAG) {
        this.stiftDTAG = stiftDTAG;
    }

    /**
     * @return the ont
     */
    public String getOnt() {
        return ont;
    }

    /**
     * @param ont the ont to set
     */
    public void setOnt(String ont) {
        this.ont = ont;
    }

    /**
     * @return the ontSerienNummer
     */
    public String getOntSerienNummer() {
        return ontSerienNummer;
    }

    /**
     * @param ontSerienNummer the ontSerienNummer to set
     */
    public void setOntSerienNummer(String ontSerienNummer) {
        this.ontSerienNummer = ontSerienNummer;
    }

    /**
     * @return the ontTyp
     */
    public String getOntTyp() {
        return ontTyp;
    }

    /**
     * @param ontTyp the ontTyp to set
     */
    public void setOntTyp(String ontTyp) {
        this.ontTyp = ontTyp;
    }

    /**
     * @return the auftragId
     */
    public Integer getAuftragId() {
        return auftragId;
    }

    /**
     * @param auftragId the auftragId to set
     */
    public void setAuftragId(Integer auftragId) {
        this.auftragId = auftragId;
    }

    /**
     * @return the orderNo
     */
    public Long getOrderNo() {
        return orderNo;
    }

    /**
     * @param orderNo the orderNo to set
     */
    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }

    /**
     * @return the verbindungsbezeichnung
     */
    public String getVerbindungsbezeichnung() {
        return verbindungsbezeichnung;
    }

    /**
     * @param verbindungsbezeichnung the verbindungsbezeichnung to set
     */
    public void setVerbindungsbezeichnung(String verbindungsbezeichnung) {
        this.verbindungsbezeichnung = verbindungsbezeichnung;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

}
