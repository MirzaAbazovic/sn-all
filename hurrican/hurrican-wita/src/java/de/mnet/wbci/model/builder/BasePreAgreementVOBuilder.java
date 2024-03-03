/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.04.2014
 */
package de.mnet.wbci.model.builder;

import java.util.*;

import de.mnet.wbci.model.BasePreAgreementVO;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequestStatus;

/**
 *
 */
public abstract class BasePreAgreementVOBuilder<T extends BasePreAgreementVO> {

    protected String vaid;
    protected RequestTyp aenderungskz;
    protected GeschaeftsfallTyp gfType;
    protected CarrierCode ekpAbg;
    protected CarrierCode ekpAuf;
    protected Date vorgabeDatum;
    protected Date wechseltermin;
    protected WbciRequestStatus requestStatus;
    protected WbciGeschaeftsfallStatus geschaeftsfallStatus;
    protected Date processedAt;
    protected Integer daysUntilDeadlinePartner;
    protected Integer daysUntilDeadlineMnet;
    protected Date rueckmeldeDatum;
    private Boolean automatable;

    public abstract T build();

    protected T enrich(T vo) {
        vo.setVaid(vaid);
        vo.setGfType(gfType);
        vo.setEkpAbg(ekpAbg);
        vo.setEkpAuf(ekpAuf);
        vo.setVorgabeDatum(vorgabeDatum);
        vo.setWechseltermin(wechseltermin);
        vo.setAenderungskz(aenderungskz);
        vo.setRequestStatus(requestStatus);
        vo.setGeschaeftsfallStatus(geschaeftsfallStatus);
        vo.setProcessedAt(processedAt);
        vo.setDaysUntilDeadlinePartner(daysUntilDeadlinePartner);
        vo.setDaysUntilDeadlineMnet(daysUntilDeadlineMnet);
        vo.setRueckmeldeDatum(rueckmeldeDatum);
        vo.setAutomatable(automatable);
        return vo;
    }

    public BasePreAgreementVOBuilder<T> withVaid(String vaid) {
        this.vaid = vaid;
        return this;
    }

    public BasePreAgreementVOBuilder<T> withGfType(GeschaeftsfallTyp gfType) {
        this.gfType = gfType;
        return this;
    }

    public BasePreAgreementVOBuilder<T> withEkpAbg(CarrierCode ekpAbg) {
        this.ekpAbg = ekpAbg;
        return this;
    }

    public BasePreAgreementVOBuilder<T> withEkpAuf(CarrierCode ekpAuf) {
        this.ekpAuf = ekpAuf;
        return this;
    }

    public BasePreAgreementVOBuilder<T> withVorgabeDatum(Date vorgabeDatum) {
        this.vorgabeDatum = vorgabeDatum;
        return this;
    }

    public BasePreAgreementVOBuilder<T> withWechseltermin(Date wechseltermin) {
        this.wechseltermin = wechseltermin;
        return this;
    }

    public BasePreAgreementVOBuilder<T> withRequestStatus(WbciRequestStatus requestStatus) {
        this.requestStatus = requestStatus;
        return this;
    }

    public BasePreAgreementVOBuilder<T> withGeschaeftsfallStatus(WbciGeschaeftsfallStatus geschaeftsfallStatus) {
        this.geschaeftsfallStatus = geschaeftsfallStatus;
        return this;
    }

    public BasePreAgreementVOBuilder<T> withProcessedAt(Date processedAt) {
        this.processedAt = processedAt;
        return this;
    }

    public BasePreAgreementVOBuilder<T> withDaysUntilDeadlinePartner(Integer daysUntilDeadlinePartner) {
        this.daysUntilDeadlinePartner = daysUntilDeadlinePartner;
        return this;
    }

    public BasePreAgreementVOBuilder<T> withRueckmeldungDatum(Date rueckmeldeDatum) {
        this.rueckmeldeDatum = rueckmeldeDatum;
        return this;
    }

    public BasePreAgreementVOBuilder<T> withAenderungskz(RequestTyp anAenderungskz) {
        this.aenderungskz = anAenderungskz;
        return this;
    }

    public BasePreAgreementVOBuilder<T> withAutomatable(Boolean automatable) {
        this.automatable = automatable;
        return this;
    }

    public BasePreAgreementVOBuilder<T> withDaysUntilDeadlineMnet(Integer daysUntilDeadlineMnet) {
        this.daysUntilDeadlineMnet = daysUntilDeadlineMnet;
        return this;
    }
}
