/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.01.2007 15:16:36
 */
package de.augustakom.hurrican.model.auskunft;

import java.util.*;

import de.augustakom.hurrican.model.base.AbstractHurricanModel;


/**
 * Modell-Klasse fuer CDRs.
 *
 *
 */
public class CDR extends AbstractHurricanModel {

    private Long offset = null;
    private Integer rectyp = null;
    private Integer cpc = null;
    private String aRN = null;
    private String bRN = null;
    private String ddi = null;
    private Integer art = null;
    private Date datum = null;
    private String zeit = null;
    private Float dauer = null;
    private Integer zone = null;
    private Integer units = null;
    private String in = null;
    private String out = null;
    private String connId = null;
    private Boolean ctx = null;
    private Boolean answer = null;
    private Float ansDur = null;
    private Integer causeVal = null;
    private Integer lacLength = null;
    private String bRN2 = null;

    /**
     * @return Returns the ansDur.
     */
    public Float getAnsDur() {
        return this.ansDur;
    }

    /**
     * @param ansDur The ansDur to set.
     */
    public void setAnsDur(Float ansDur) {
        this.ansDur = ansDur;
    }

    /**
     * @return Returns the answer.
     */
    public Boolean getAnswer() {
        return this.answer;
    }

    /**
     * @param answer The answer to set.
     */
    public void setAnswer(Boolean answer) {
        this.answer = answer;
    }

    /**
     * @return Returns the aRN.
     */
    public String getARN() {
        return this.aRN;
    }

    /**
     * @param arn The aRN to set.
     */
    public void setARN(String arn) {
        this.aRN = arn;
    }

    /**
     * @return Returns the art.
     */
    public Integer getArt() {
        return this.art;
    }

    /**
     * @param art The art to set.
     */
    public void setArt(Integer art) {
        this.art = art;
    }

    /**
     * @return Returns the bRN.
     */
    public String getBRN() {
        return this.bRN;
    }

    /**
     * @param brn The bRN to set.
     */
    public void setBRN(String brn) {
        this.bRN = brn;
    }

    /**
     * @return Returns the bRN2.
     */
    public String getBRN2() {
        return this.bRN2;
    }

    /**
     * @param brn2 The bRN2 to set.
     */
    public void setBRN2(String brn2) {
        this.bRN2 = brn2;
    }

    /**
     * @return Returns the causeVal.
     */
    public Integer getCauseVal() {
        return this.causeVal;
    }

    /**
     * @param causeVal The causeVal to set.
     */
    public void setCauseVal(Integer causeVal) {
        this.causeVal = causeVal;
    }

    /**
     * @return Returns the connId.
     */
    public String getConnId() {
        return this.connId;
    }

    /**
     * @param connId The connId to set.
     */
    public void setConnId(String connId) {
        this.connId = connId;
    }

    /**
     * @return Returns the cpc.
     */
    public Integer getCpc() {
        return this.cpc;
    }

    /**
     * @param cpc The cpc to set.
     */
    public void setCpc(Integer cpc) {
        this.cpc = cpc;
    }

    /**
     * @return Returns the ctx.
     */
    public Boolean getCtx() {
        return this.ctx;
    }

    /**
     * @param ctx The ctx to set.
     */
    public void setCtx(Boolean ctx) {
        this.ctx = ctx;
    }

    /**
     * @return Returns the datum.
     */
    public Date getDatum() {
        return this.datum;
    }

    /**
     * @param datum The datum to set.
     */
    public void setDatum(Date datum) {
        this.datum = datum;
    }

    /**
     * @return Returns the dauer.
     */
    public Float getDauer() {
        return this.dauer;
    }

    /**
     * @param dauer The dauer to set.
     */
    public void setDauer(Float dauer) {
        this.dauer = dauer;
    }

    /**
     * @return Returns the ddi.
     */
    public String getDdi() {
        return this.ddi;
    }

    /**
     * @param ddi The ddi to set.
     */
    public void setDdi(String ddi) {
        this.ddi = ddi;
    }

    /**
     * @return Returns the in.
     */
    public String getIn() {
        return this.in;
    }

    /**
     * @param in The in to set.
     */
    public void setIn(String in) {
        this.in = in;
    }

    /**
     * @return Returns the lacLength.
     */
    public Integer getLacLength() {
        return this.lacLength;
    }

    /**
     * @param lacLength The lacLength to set.
     */
    public void setLacLength(Integer lacLength) {
        this.lacLength = lacLength;
    }

    /**
     * @return Returns the offset.
     */
    public Long getOffset() {
        return this.offset;
    }

    /**
     * @param offset The offset to set.
     */
    public void setOffset(Long offset) {
        this.offset = offset;
    }

    /**
     * @return Returns the out.
     */
    public String getOut() {
        return this.out;
    }

    /**
     * @param out The out to set.
     */
    public void setOut(String out) {
        this.out = out;
    }

    /**
     * @return Returns the rectyp.
     */
    public Integer getRectyp() {
        return this.rectyp;
    }

    /**
     * @param rectyp The rectyp to set.
     */
    public void setRectyp(Integer rectyp) {
        this.rectyp = rectyp;
    }

    /**
     * @return Returns the units.
     */
    public Integer getUnits() {
        return this.units;
    }

    /**
     * @param units The units to set.
     */
    public void setUnits(Integer units) {
        this.units = units;
    }

    /**
     * @return Returns the zeit.
     */
    public String getZeit() {
        return this.zeit;
    }

    /**
     * @param zeit The zeit to set.
     */
    public void setZeit(String zeit) {
        this.zeit = zeit;
    }

    /**
     * @return Returns the zone.
     */
    public Integer getZone() {
        return this.zone;
    }

    /**
     * @param zone The zone to set.
     */
    public void setZone(Integer zone) {
        this.zone = zone;
    }

}


