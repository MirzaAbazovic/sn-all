/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.01.2010 07:43:46
 */
package de.augustakom.hurrican.model.cc.tal;

import de.augustakom.hurrican.model.cc.AbstractCCIDModel;


/**
 * Klasse fuer die Protokollierung der Sub-Orders fuer eine TAL-Klammerung.
 *
 *
 */
public class CBVorgangSubOrder extends AbstractCCIDModel implements CBVorgangReturnModel {

    private static final long serialVersionUID = 481390553811856022L;

    private Long auftragId;
    private String dtagPort;
    private String returnLBZ;
    private String returnVTRNR;
    private String returnAQS;
    private String returnLL;

    // nachfolgende Felder sind nicht persistent!
    private Boolean selected;
    private String vbz;
    private Integer anzahlSelectedAnlagen;

    public CBVorgangSubOrder() {
        // required by Hibernate
    }

    public CBVorgangSubOrder(Long auftragId, String dtagPort, Boolean selected, String vbz) {
        this.auftragId = auftragId;
        this.dtagPort = dtagPort;
        this.selected = selected;
        this.vbz = vbz;
    }

    public Long getAuftragId() {
        return auftragId;
    }

    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    public String getDtagPort() {
        return dtagPort;
    }

    public void setDtagPort(String dtagPort) {
        this.dtagPort = dtagPort;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public String getVbz() {
        return vbz;
    }

    public void setVbz(String vbz) {
        this.vbz = vbz;
    }

    public Integer getAnzahlSelectedAnlagen() {
        return anzahlSelectedAnlagen;
    }

    public void setAnzahlSelectedAnlagen(Integer anzahlSelectedAnlagen) {
        this.anzahlSelectedAnlagen = anzahlSelectedAnlagen;
    }

    @Override
    public String getReturnLBZ() {
        return returnLBZ;
    }

    @Override
    public void setReturnLBZ(String returnLBZ) {
        this.returnLBZ = returnLBZ;
    }

    @Override
    public String getReturnVTRNR() {
        return returnVTRNR;
    }

    @Override
    public void setReturnVTRNR(String returnVTRNR) {
        this.returnVTRNR = returnVTRNR;
    }

    @Override
    public String getReturnAQS() {
        return returnAQS;
    }

    @Override
    public void setReturnAQS(String returnAQS) {
        this.returnAQS = returnAQS;
    }

    @Override
    public String getReturnLL() {
        return returnLL;
    }

    @Override
    public void setReturnLL(String returnLL) {
        this.returnLL = returnLL;
    }

}


