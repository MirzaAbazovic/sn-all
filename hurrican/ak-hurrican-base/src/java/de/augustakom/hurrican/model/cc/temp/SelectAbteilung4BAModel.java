/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.03.2009 16:02:54
 */
package de.augustakom.hurrican.model.cc.temp;

import java.util.*;

import de.augustakom.hurrican.model.cc.AbstractCCModel;

public class SelectAbteilung4BAModel extends AbstractCCModel {

    private Long abteilungId = null;
    private Long niederlassungId = null;
    private Long extServiceProviderId = null;
    private Date realDate = null;

    public SelectAbteilung4BAModel() {
    }

    public SelectAbteilung4BAModel(Long abteilungId, Long niederlassungId, Long extServiceProviderId, Date realDate) {
        this.abteilungId = abteilungId;
        this.niederlassungId = niederlassungId;
        this.extServiceProviderId = extServiceProviderId;
        this.realDate = realDate;
    }

    /**
     * @return abteilungId
     */
    public Long getAbteilungId() {
        return abteilungId;
    }

    /**
     * @param abteilungId Festzulegender abteilungId
     */
    public void setAbteilungId(Long abteilungId) {
        this.abteilungId = abteilungId;
    }

    /**
     * @return extServiceProviderId
     */
    public Long getExtServiceProviderId() {
        return extServiceProviderId;
    }

    /**
     * @param extServiceProviderId Festzulegender extServiceProviderId
     */
    public void setExtServiceProviderId(Long extServiceProviderId) {
        this.extServiceProviderId = extServiceProviderId;
    }

    /**
     * @return realDate
     */
    public Date getRealDate() {
        return realDate;
    }

    /**
     * @param realDate Festzulegender realDate
     */
    public void setRealDate(Date realDate) {
        this.realDate = realDate;
    }

    /**
     * @return the niederlassungId
     */
    public Long getNiederlassungId() {
        return niederlassungId;
    }

    /**
     * @param niederlassungId the niederlassungId to set
     */
    public void setNiederlassungId(Long niederlassungId) {
        this.niederlassungId = niederlassungId;
    }

}
