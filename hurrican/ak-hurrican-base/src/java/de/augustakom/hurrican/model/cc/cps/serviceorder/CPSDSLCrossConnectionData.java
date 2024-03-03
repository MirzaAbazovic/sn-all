/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.09.2009 09:40:21
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder;

import com.thoughtworks.xstream.annotations.XStreamAlias;


/**
 * CPS CrossConnection Definition
 *
 *
 */
@XStreamAlias("CC")
public class CPSDSLCrossConnectionData extends AbstractCPSServiceOrderDataModel {

    @XStreamAlias("NT_TYPE")
    private String ccType = null;
    @XStreamAlias("NT_OUTER")
    private Integer ntOuter = null;
    @XStreamAlias("NT_INNER")
    private Integer ntInner = null;
    @XStreamAlias("LT_OUTER")
    private Integer ltOuter = null;
    @XStreamAlias("LT_INNER")
    private Integer ltInner = null;
    @XStreamAlias("BRAS_OUTER")
    private Integer brasOuter = null;
    @XStreamAlias("BRAS_INNER")
    private Integer brasInner = null;

    public String getCcType() {
        return ccType;
    }

    public void setCcType(String ccType) {
        this.ccType = ccType;
    }

    public Integer getNtInner() {
        return ntInner;
    }

    public void setNtInner(Integer ntInner) {
        this.ntInner = ntInner;
    }

    public Integer getNtOuter() {
        return ntOuter;
    }

    public void setNtOuter(Integer ntOuter) {
        this.ntOuter = ntOuter;
    }

    public Integer getLtOuter() {
        return ltOuter;
    }

    public void setLtOuter(Integer ltOuter) {
        this.ltOuter = ltOuter;
    }

    public Integer getLtInner() {
        return ltInner;
    }

    public void setLtInner(Integer ltInner) {
        this.ltInner = ltInner;
    }

    public Integer getBrasOuter() {
        return brasOuter;
    }

    public void setBrasOuter(Integer brasOuter) {
        this.brasOuter = brasOuter;
    }

    public Integer getBrasInner() {
        return brasInner;
    }

    public void setBrasInner(Integer brasInner) {
        this.brasInner = brasInner;
    }

}


