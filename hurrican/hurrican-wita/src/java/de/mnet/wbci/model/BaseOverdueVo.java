/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.06.2014
 */
package de.mnet.wbci.model;

import java.io.*;
import java.util.*;

import de.augustakom.common.tools.lang.DateTools;

/**
 * Base VO for  {@link OverdueAbmPvVO} and {@link OverdueAbmPvVO}.
 *
 *
 */
public abstract class BaseOverdueVo implements Serializable {
    private static final long serialVersionUID = 1962855947932493133L;
    protected CarrierCode ekpAuf;
    protected CarrierCode ekpAbg;
    protected String vaid;
    protected Date wechseltermin;
    protected Long auftragNoOrig;
    protected Long auftragId;

    public String getVaid() {
        return vaid;
    }

    public void setVaid(String vaid) {
        this.vaid = vaid;
    }

    public CarrierCode getEkpAuf() {
        return ekpAuf;
    }

    public void setEkpAuf(CarrierCode ekpAuf) {
        this.ekpAuf = ekpAuf;
    }

    public CarrierCode getEkpAbg() {
        return ekpAbg;
    }

    public void setEkpAbg(CarrierCode ekpAbg) {
        this.ekpAbg = ekpAbg;
    }

    public Date getWechseltermin() {
        return wechseltermin;
    }

    public String getFormattedWechseltermin() {
        if (wechseltermin != null) {
            return DateTools.formatDate(wechseltermin, DateTools.PATTERN_DAY_MONTH_YEAR);
        }
        return "";
    }

    public void setWechseltermin(Date wechseltermin) {
        this.wechseltermin = wechseltermin;
    }

    public Long getAuftragNoOrig() {
        return auftragNoOrig;
    }

    public void setAuftragNoOrig(Long auftragNoOrig) {
        this.auftragNoOrig = auftragNoOrig;
    }

    public Long getAuftragId() {
        return auftragId;
    }

    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }
}
