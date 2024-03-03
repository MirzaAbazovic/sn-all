/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.02.2008 13:10:37
 */
package de.augustakom.hurrican.model.cc;


/**
 * Modell zur Abbildung von Quality-of-Service Daten zu einem Auftrag.
 *
 *
 */
public class AuftragQoS extends AbstractCCHistoryUserModel {

    private Long auftragId = null;
    private Long qosClassRefId = null;
    private Integer percentage = null;

    /**
     * Gibt die QoS-Klasse an, der konfiguriert wird.
     *
     * @return Returns the qosClassRefId.
     */
    public Long getQosClassRefId() {
        return qosClassRefId;
    }

    /**
     * @param qosClassRefId The qosClassRefId to set.
     */
    public void setQosClassRefId(Long qosClassRefId) {
        this.qosClassRefId = qosClassRefId;
    }

    /**
     * Prozent-Wert gibt an, wie viel von der maximal verfuegbaren Bandbreite fuer die QoS-Klasse verwendet werden soll.
     * <br> Die Gesamt-Angabe der Prozentwerte zu einem Auftrag darf die 100% natuerlich nicht uebersteigen.
     *
     * @return Returns the percentage.
     */
    public Integer getPercentage() {
        return percentage;
    }

    /**
     * @param percentage The percentage to set.
     */
    public void setPercentage(Integer percentage) {
        this.percentage = percentage;
    }

    /**
     * @return Returns the auftragId.
     */
    public Long getAuftragId() {
        return auftragId;
    }

    /**
     * @param auftragId The auftragId to set.
     */
    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

}


