/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.06.2009 11:43:39
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

import de.augustakom.hurrican.model.cc.cps.serviceorder.converter.CPSGetServiceOrderStatusResponseLongDataConverter;


/**
 * Modell-Klasse fuer die SO-Data, die bei einer Status-Abfrage der CPS-Transaktion geliefert wird.
 *
 *
 */
@XStreamAlias("GETSOSTATUS_RESPONSE")
public class CPSGetServiceOrderStatusResponseData extends AbstractCPSServiceOrderDataModel {

    @XStreamAlias("SO_STATUS")
    @XStreamConverter(CPSGetServiceOrderStatusResponseLongDataConverter.class)
    private Long soStatus = null;
    @XStreamAlias("SO_RESULT")
    @XStreamConverter(CPSGetServiceOrderStatusResponseLongDataConverter.class)
    private Long soResult = null;
    @XStreamAlias("SO_RESPONSE")
    private String soResponse = null;

    /**
     * @return the soStatus
     */
    public Long getSoStatus() {
        return soStatus;
    }

    /**
     * @param soStatus the soStatus to set
     */
    public void setSoStatus(Long soStatus) {
        this.soStatus = soStatus;
    }

    /**
     * @return the soResult
     */
    public Long getSoResult() {
        return soResult;
    }

    /**
     * @param soResult the soResult to set
     */
    public void setSoResult(Long soResult) {
        this.soResult = soResult;
    }

    /**
     * @return the soResponse
     */
    public String getSoResponse() {
        return soResponse;
    }

    /**
     * @param soResponse the soResponse to set
     */
    public void setSoResponse(String soResponse) {
        this.soResponse = soResponse;
    }
}
