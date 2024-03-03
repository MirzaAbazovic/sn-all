/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.07.2007 10:46:15
 */
package de.augustakom.hurrican.model.exmodules.tal;

import org.apache.commons.lang.StringUtils;


/**
 * View-Modell, das die Rueckmeldedaten der TAL-Bestellungen enthaelt.
 *
 *
 */
public abstract class AbstractTALReturnView extends AbstractTALBestellungModel {

    private String b005_2 = null;
    private String b005_3 = null;
    private Integer b005_4 = null;
    private Long tbvId = null;
    private Long srcId = null;
    private Long tbsTbsId = null;
    private Long tbsFirstId = null;
    private Long tbsStatus = null;
    private String tbsDatei = null;
    private String tbsSender = null;
    private String tbsRecipient = null;
    private String b017_2 = null;
    private Long tfeId = null;  // ID aus TTALBESTELLUNG_FEHLERTYP
    private String tfeKlasse = null;
    private String tfeBeschreibung = null;
    private String b002_2 = null;
    private Integer b002_4 = null;
    private String b001_2 = null;
    private Long b001_4 = null;
    private String b001_5 = null;
    private String b001_6 = null;
    private String b015_2 = null;
    private String b015_3 = null;
    private String b015_4 = null;
    private String b015_5 = null;
    private String b015_6 = null;
    private String b015_2_Snd = null;
    private String b015_3_Snd = null;
    private String b015_4_Snd = null;
    private String b015_5_Snd = null;
    private String b015_6_Snd = null;
    private String b009_2 = null;
    private String b009_3 = null;
    private Integer b009_4 = null;
    private String b009_5 = null;
    private String b009_6 = null;
    private String b009_7 = null;
    private String b009_8 = null;
    private String b009_9 = null;
    private String b009_10 = null;
    private String b009_11 = null;
    private String b009_12 = null;
    private String b009_13 = null;

    /**
     * Gibt an, ob es sich bei der Rueckmeldung um eine positive (false) oder negative (true) Rueckmeldung handelt.
     *
     * @return
     *
     */
    public abstract boolean isError();

    /**
     * Prueft, ob die Rueckmeldung mit Code 'AEA' (Auftrag wie erteilt ausgefuehrt) zurueck geliefert wurde.
     *
     * @return
     */
    public boolean isAEA() {
        return StringUtils.equalsIgnoreCase(getB005_3(), "AEA");
    }

    /**
     * Gibt den Namen des Carrier-Bearbeiters zurueck. Hierbei werden Name, Vorname und Telefonnummer zusammengesetzt.
     *
     * @return
     *
     */
    public String getCarrierBearbeiter() {
        StringBuilder sb = new StringBuilder();
        sb.append(getB015_2());
        sb.append(" ");
        sb.append(getB015_3());
        sb.append(" (");
        sb.append(getB015_4());
        sb.append(")");
        return sb.toString();
    }

    /**
     * @return Returns the b005_2.
     */
    public String getB005_2() {
        return b005_2;
    }

    /**
     * @param b005_2 The b005_2 to set.
     */
    public void setB005_2(String b005_2) {
        this.b005_2 = b005_2;
    }

    /**
     * @return Returns the b005_3.
     */
    public String getB005_3() {
        return b005_3;
    }

    /**
     * @param b005_3 The b005_3 to set.
     */
    public void setB005_3(String b005_3) {
        this.b005_3 = b005_3;
    }

    /**
     * @return Returns the b005_4.
     */
    public Integer getB005_4() {
        return b005_4;
    }

    /**
     * @param b005_4 The b005_4 to set.
     */
    public void setB005_4(Integer b005_4) {
        this.b005_4 = b005_4;
    }

    /**
     * @return Returns the tbvId.
     */
    public Long getTbvId() {
        return tbvId;
    }

    /**
     * @param tbvId The tbvId to set.
     */
    public void setTbvId(Long tbvId) {
        this.tbvId = tbvId;
    }

    /**
     * @return Returns the srcId.
     */
    public Long getSrcId() {
        return srcId;
    }

    /**
     * @param srcId The srcId to set.
     */
    public void setSrcId(Long srcId) {
        this.srcId = srcId;
    }

    /**
     * @return Returns the tbsTbsId.
     */
    public Long getTbsTbsId() {
        return tbsTbsId;
    }

    /**
     * @param tbsTbsId The tbsTbsId to set.
     */
    public void setTbsTbsId(Long tbsTbsId) {
        this.tbsTbsId = tbsTbsId;
    }

    /**
     * @return Returns the tbsFirstId.
     */
    public Long getTbsFirstId() {
        return tbsFirstId;
    }

    /**
     * @param tbsFirstId The tbsFirstId to set.
     */
    public void setTbsFirstId(Long tbsFirstId) {
        this.tbsFirstId = tbsFirstId;
    }

    /**
     * @return Returns the tbsStatus.
     */
    public Long getTbsStatus() {
        return tbsStatus;
    }

    /**
     * @param tbsStatus The tbsStatus to set.
     */
    public void setTbsStatus(Long tbsStatus) {
        this.tbsStatus = tbsStatus;
    }

    /**
     * @return Returns the tbsDatei.
     */
    public String getTbsDatei() {
        return tbsDatei;
    }

    /**
     * @param tbsDatei The tbsDatei to set.
     */
    public void setTbsDatei(String tbsDatei) {
        this.tbsDatei = tbsDatei;
    }

    /**
     * @return Returns the tbsSender.
     */
    public String getTbsSender() {
        return tbsSender;
    }

    /**
     * @param tbsSender The tbsSender to set.
     */
    public void setTbsSender(String tbsSender) {
        this.tbsSender = tbsSender;
    }

    /**
     * @return Returns the tbsRecipient.
     */
    public String getTbsRecipient() {
        return tbsRecipient;
    }

    /**
     * @param tbsRecipient The tbsRecipient to set.
     */
    public void setTbsRecipient(String tbsRecipient) {
        this.tbsRecipient = tbsRecipient;
    }

    /**
     * @return Returns the b017_2.
     */
    public String getB017_2() {
        return b017_2;
    }

    /**
     * @param b017_2 The b017_2 to set.
     */
    public void setB017_2(String b017_2) {
        this.b017_2 = b017_2;
    }

    /**
     * @return Returns the tfeId.
     */
    public Long getTfeId() {
        return tfeId;
    }

    /**
     * @param tfeId The tfeId to set.
     */
    public void setTfeId(Long tfeId) {
        this.tfeId = tfeId;
    }

    /**
     * @return Returns the tfeKlasse.
     */
    public String getTfeKlasse() {
        return tfeKlasse;
    }

    /**
     * @param tfeKlasse The tfeKlasse to set.
     */
    public void setTfeKlasse(String tfeKlasse) {
        this.tfeKlasse = tfeKlasse;
    }

    /**
     * @return Returns the tfeBeschreibung.
     */
    public String getTfeBeschreibung() {
        return tfeBeschreibung;
    }

    /**
     * @param tfeBeschreibung The tfeBeschreibung to set.
     */
    public void setTfeBeschreibung(String tfeBeschreibung) {
        this.tfeBeschreibung = tfeBeschreibung;
    }

    /**
     * @return Returns the b002_2.
     */
    public String getB002_2() {
        return b002_2;
    }

    /**
     * @param b002_2 The b002_2 to set.
     */
    public void setB002_2(String b002_2) {
        this.b002_2 = b002_2;
    }

    /**
     * @return Returns the b002_4.
     */
    public Integer getB002_4() {
        return b002_4;
    }

    /**
     * @param b002_4 The b002_4 to set.
     */
    public void setB002_4(Integer b002_4) {
        this.b002_4 = b002_4;
    }

    /**
     * @return Returns the b001_2.
     */
    public String getB001_2() {
        return b001_2;
    }

    /**
     * @param b001_2 The b001_2 to set.
     */
    public void setB001_2(String b001_2) {
        this.b001_2 = b001_2;
    }

    /**
     * @return Returns the b001_4.
     */
    public Long getB001_4() {
        return b001_4;
    }

    /**
     * @param b001_4 The b001_4 to set.
     */
    public void setB001_4(Long b001_4) {
        this.b001_4 = b001_4;
    }

    /**
     * @return Returns the b001_5.
     */
    public String getB001_5() {
        return b001_5;
    }

    /**
     * @param b001_5 The b001_5 to set.
     */
    public void setB001_5(String b001_5) {
        this.b001_5 = b001_5;
    }

    /**
     * @return Returns the b001_6.
     */
    public String getB001_6() {
        return b001_6;
    }

    /**
     * @param b001_6 The b001_6 to set.
     */
    public void setB001_6(String b001_6) {
        this.b001_6 = b001_6;
    }

    /**
     * @return Returns the b015_2.
     */
    public String getB015_2() {
        return b015_2;
    }

    /**
     * @param b015_2 The b015_2 to set.
     */
    public void setB015_2(String b015_2) {
        this.b015_2 = b015_2;
    }

    /**
     * @return Returns the b015_3.
     */
    public String getB015_3() {
        return b015_3;
    }

    /**
     * @param b015_3 The b015_3 to set.
     */
    public void setB015_3(String b015_3) {
        this.b015_3 = b015_3;
    }

    /**
     * @return Returns the b015_4.
     */
    public String getB015_4() {
        return b015_4;
    }

    /**
     * @param b015_4 The b015_4 to set.
     */
    public void setB015_4(String b015_4) {
        this.b015_4 = b015_4;
    }

    /**
     * @return Returns the b015_5.
     */
    public String getB015_5() {
        return b015_5;
    }

    /**
     * @param b015_5 The b015_5 to set.
     */
    public void setB015_5(String b015_5) {
        this.b015_5 = b015_5;
    }

    /**
     * @return Returns the b015_6.
     */
    public String getB015_6() {
        return b015_6;
    }

    /**
     * @param b015_6 The b015_6 to set.
     */
    public void setB015_6(String b015_6) {
        this.b015_6 = b015_6;
    }

    /**
     * @return Returns the b015_2_Snd.
     */
    public String getB015_2_Snd() {
        return b015_2_Snd;
    }

    /**
     * @param snd The b015_2_Snd to set.
     */
    public void setB015_2_Snd(String snd) {
        b015_2_Snd = snd;
    }

    /**
     * @return Returns the b015_3_Snd.
     */
    public String getB015_3_Snd() {
        return b015_3_Snd;
    }

    /**
     * @param snd The b015_3_Snd to set.
     */
    public void setB015_3_Snd(String snd) {
        b015_3_Snd = snd;
    }

    /**
     * @return Returns the b015_4_Snd.
     */
    public String getB015_4_Snd() {
        return b015_4_Snd;
    }

    /**
     * @param snd The b015_4_Snd to set.
     */
    public void setB015_4_Snd(String snd) {
        b015_4_Snd = snd;
    }

    /**
     * @return Returns the b015_5_Snd.
     */
    public String getB015_5_Snd() {
        return b015_5_Snd;
    }

    /**
     * @param snd The b015_5_Snd to set.
     */
    public void setB015_5_Snd(String snd) {
        b015_5_Snd = snd;
    }

    /**
     * @return Returns the b015_6_Snd.
     */
    public String getB015_6_Snd() {
        return b015_6_Snd;
    }

    /**
     * @param snd The b015_6_Snd to set.
     */
    public void setB015_6_Snd(String snd) {
        b015_6_Snd = snd;
    }

    /**
     * @return Returns the b009_2.
     */
    public String getB009_2() {
        return b009_2;
    }

    /**
     * @param b009_2 The b009_2 to set.
     */
    public void setB009_2(String b009_2) {
        this.b009_2 = b009_2;
    }

    /**
     * @return Returns the b009_3.
     */
    public String getB009_3() {
        return b009_3;
    }

    /**
     * @param b009_3 The b009_3 to set.
     */
    public void setB009_3(String b009_3) {
        this.b009_3 = b009_3;
    }

    /**
     * @return Returns the b009_4.
     */
    public Integer getB009_4() {
        return b009_4;
    }

    /**
     * @param b009_4 The b009_4 to set.
     */
    public void setB009_4(Integer b009_4) {
        this.b009_4 = b009_4;
    }

    /**
     * @return Returns the b009_5.
     */
    public String getB009_5() {
        return b009_5;
    }

    /**
     * @param b009_5 The b009_5 to set.
     */
    public void setB009_5(String b009_5) {
        this.b009_5 = b009_5;
    }

    /**
     * @return Returns the b009_6.
     */
    public String getB009_6() {
        return b009_6;
    }

    /**
     * @param b009_6 The b009_6 to set.
     */
    public void setB009_6(String b009_6) {
        this.b009_6 = b009_6;
    }

    /**
     * @return Returns the b009_7.
     */
    public String getB009_7() {
        return b009_7;
    }

    /**
     * @param b009_7 The b009_7 to set.
     */
    public void setB009_7(String b009_7) {
        this.b009_7 = b009_7;
    }

    /**
     * @return Returns the b009_8.
     */
    public String getB009_8() {
        return b009_8;
    }

    /**
     * @param b009_8 The b009_8 to set.
     */
    public void setB009_8(String b009_8) {
        this.b009_8 = b009_8;
    }

    /**
     * @return Returns the b009_9.
     */
    public String getB009_9() {
        return b009_9;
    }

    /**
     * @param b009_9 The b009_9 to set.
     */
    public void setB009_9(String b009_9) {
        this.b009_9 = b009_9;
    }

    /**
     * @return Returns the b009_10.
     */
    public String getB009_10() {
        return b009_10;
    }

    /**
     * @param b009_10 The b009_10 to set.
     */
    public void setB009_10(String b009_10) {
        this.b009_10 = b009_10;
    }

    /**
     * @return Returns the b009_11.
     */
    public String getB009_11() {
        return b009_11;
    }

    /**
     * @param b009_11 The b009_11 to set.
     */
    public void setB009_11(String b009_11) {
        this.b009_11 = b009_11;
    }

    /**
     * @return Returns the b009_12.
     */
    public String getB009_12() {
        return b009_12;
    }

    /**
     * @param b009_12 The b009_12 to set.
     */
    public void setB009_12(String b009_12) {
        this.b009_12 = b009_12;
    }

    /**
     * @return Returns the b009_13.
     */
    public String getB009_13() {
        return b009_13;
    }

    /**
     * @param b009_13 The b009_13 to set.
     */
    public void setB009_13(String b009_13) {
        this.b009_13 = b009_13;
    }

}

