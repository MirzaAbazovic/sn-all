/*
 * Copyright (c) 2009 - M-Net Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.03.2009 11:00:21
 */
package de.augustakom.hurrican.model.cc.view;

import java.util.*;

/**
 * View-Klasse fuer den el. Verlauf der Abteilung Extern.
 *
 *
 */
public class VerlaufEXTView extends AbstractBauauftragView implements TimeSlotAware {

    private String endstelleOrtB = null;
    private String endstelleStrasseB = null;
    private Date carrierBereitstellung = null;
    private String carrier = null;
    private Long extServiceProviderId = null;
    private String extServiceProviderName = null;
    private Boolean kreuzung = null;
    private Long statusId = null;
    private TimeSlotHolder timeSlot = new TimeSlotHolder();
    private String geraeteBez;

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public Date getCarrierBereitstellung() {
        return carrierBereitstellung;
    }

    public void setCarrierBereitstellung(Date carrierBereitstellung) {
        this.carrierBereitstellung = carrierBereitstellung;
    }

    @Override
    public String getEndstelleOrtB() {
        return endstelleOrtB;
    }

    @Override
    public void setEndstelleOrtB(String endstelleOrtB) {
        this.endstelleOrtB = endstelleOrtB;
    }

    public String getEndstelleStrasseB() {
        return endstelleStrasseB;
    }

    public void setEndstelleStrasseB(String endstelleStrasseB) {
        this.endstelleStrasseB = endstelleStrasseB;
    }

    public Long getExtServiceProviderId() {
        return extServiceProviderId;
    }

    public void setExtServiceProviderId(Long extServiceProviderId) {
        this.extServiceProviderId = extServiceProviderId;
    }

    public String getExtServiceProviderName() {
        return extServiceProviderName;
    }

    public void setExtServiceProviderName(String extServiceProviderName) {
        this.extServiceProviderName = extServiceProviderName;
    }

    public Boolean getKreuzung() {
        return kreuzung;
    }

    public void setKreuzung(Boolean kreuzung) {
        this.kreuzung = kreuzung;
    }

    public Long getStatusId() {
        return statusId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    public String getGeraeteBez() {
        return geraeteBez;
    }

    public void setGeraeteBez(String geraeteBez) {
        this.geraeteBez = geraeteBez;
    }

    public TimeSlotHolder getTimeSlot() {
        return timeSlot;
    }
}
