/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.04.2014
 */
package de.mnet.wbci.model;

import java.io.*;
import java.util.*;

/**
 * Representing the base values of a WBCI preagreement.
 *
 *
 */
public abstract class BasePreAgreementVO extends Observable implements Serializable {

    private static final long serialVersionUID = -8417427103495319833L;
    protected String vaid;
    protected GeschaeftsfallTyp gfType;
    protected CarrierCode ekpAbg;
    protected CarrierCode ekpAuf;
    protected Date vorgabeDatum;
    protected Date wechseltermin;
    protected WbciRequestStatus requestStatus;
    protected WbciGeschaeftsfallStatus geschaeftsfallStatus;
    protected Date processedAt;
    protected Integer daysUntilDeadlinePartner;
    protected Date rueckmeldeDatum;
    protected RequestTyp aenderungskz;
    protected Boolean automatable;
    protected Integer daysUntilDeadlineMnet;

    public String getVaid() {
        return vaid;
    }

    public void setVaid(String vaid) {
        this.vaid = vaid;
    }

    public String getGfTypeShortName() {
        if (gfType != null) {
            return gfType.getShortName();
        }
        return null;
    }

    public GeschaeftsfallTyp getGfType() {
        return gfType;
    }

    public void setGfType(GeschaeftsfallTyp gfType) {
        this.gfType = gfType;
    }

    public String getEkpAbgITU() {
        if (ekpAbg != null) {
            return ekpAbg.getITUCarrierCode();
        }
        return null;
    }

    public CarrierCode getEkpAbg() {
        return ekpAbg;
    }

    public void setEkpAbg(CarrierCode ekpAbg) {
        this.ekpAbg = ekpAbg;
    }

    public String getEkpAufITU() {
        if (ekpAuf != null) {
            return ekpAuf.getITUCarrierCode();
        }
        return null;
    }

    public CarrierCode getEkpAuf() {
        return ekpAuf;
    }

    public void setEkpAuf(CarrierCode ekpAuf) {
        this.ekpAuf = ekpAuf;
    }

    public Date getVorgabeDatum() {
        return cloneDate(vorgabeDatum);
    }

    public void setVorgabeDatum(Date vorgabeDatum) {
        this.vorgabeDatum = cloneDate(vorgabeDatum);
    }

    public Date getWechseltermin() {
        return cloneDate(wechseltermin);
    }

    public void setWechseltermin(Date wechseltermin) {
        this.wechseltermin = cloneDate(wechseltermin);
    }

    public WbciRequestStatus getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(WbciRequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }

    /**
     * @return the request status description or empty string if no request status is set
     */
    public String getRequestStatusDescription() {
        return requestStatus != null ? requestStatus.getDescription() : "";
    }

    public WbciGeschaeftsfallStatus getGeschaeftsfallStatus() {
        return geschaeftsfallStatus;
    }

    public void setGeschaeftsfallStatus(WbciGeschaeftsfallStatus geschaeftsfallStatus) {
        this.geschaeftsfallStatus = geschaeftsfallStatus;
    }

    /**
     * @return the gf status description or empty string if no gf status is set
     */
    public String getGeschaeftsfallStatusDescription() {
        return geschaeftsfallStatus != null ? geschaeftsfallStatus.getDescription() : "";
    }

    public Date getProcessedAt() {
        return cloneDate(processedAt);
    }

    public void setProcessedAt(Date processedAt) {
        this.processedAt = cloneDate(processedAt);
    }

    public Integer getDaysUntilDeadlinePartner() {
        return daysUntilDeadlinePartner;
    }

    public void setDaysUntilDeadlinePartner(Integer daysUntilDeadlinePartner) {
        this.daysUntilDeadlinePartner = daysUntilDeadlinePartner;
    }

    public Integer getDaysUntilDeadlineMnet() {
        return daysUntilDeadlineMnet;
    }

    public void setDaysUntilDeadlineMnet(Integer daysUntilDeadlineMnet) {
        this.daysUntilDeadlineMnet = daysUntilDeadlineMnet;
    }

    public Date getRueckmeldeDatum() {
        return cloneDate(rueckmeldeDatum);
    }

    public void setRueckmeldeDatum(Date rueckmeldeDatum) {
        this.rueckmeldeDatum = cloneDate(rueckmeldeDatum);
    }

    public RequestTyp getRequestTyp() {
        return aenderungskz;
    }

    public String getAenderungskz() {
        if (aenderungskz != null) {
            return aenderungskz.getShortName();
        }
        return null;
    }

    public void setAenderungskz(RequestTyp aenderungskz) {
        this.aenderungskz = aenderungskz;
    }

    public Boolean getAutomatable() {
        return automatable;
    }

    public void setAutomatable(Boolean automatable) {
        this.automatable = automatable;
    }

    @SuppressWarnings("unchecked")
    protected Date cloneDate(Date date) {
        if (date == null) {
            return null;
        }
        return (Date) date.clone();
    }

    @Override
    public String toString() {
        return "vaid='" + vaid + '\'' +
                ", gfType=" + gfType +
                ", ekpAbg=" + ekpAbg +
                ", ekpAuf=" + ekpAuf +
                ", vorgabeDatum=" + vorgabeDatum +
                ", wechseltermin=" + wechseltermin +
                ", requestStatus=" + requestStatus +
                ", geschaeftsfallStatus=" + geschaeftsfallStatus +
                ", aenderungskz=" + aenderungskz +
                ", processedAt=" + processedAt +
                ", daysUntilDeadlinePartner=" + daysUntilDeadlinePartner +
                ", daysUntilDeadlineMnet=" + daysUntilDeadlineMnet +
                ", automatable=" + automatable +
                ", rueckmeldeDatum=" + rueckmeldeDatum;
    }
}
