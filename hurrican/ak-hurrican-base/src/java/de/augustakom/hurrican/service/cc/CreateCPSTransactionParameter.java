/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.01.2012 15:49:07
 */
package de.augustakom.hurrican.service.cc;

import java.util.*;

import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSServiceOrderData.LazyInitMode;

/**
 * Parameter-Klasse für @link{CPSService#createCPSTransaction}
 *
 * @param auftragId           ID des zu provisionierenden Auftrags
 * @param verlaufId           (optional) ID des Bauauftrags
 * @param serviceOrderType    Typ der Provisionierung
 * @param txSource            die Quelle, aus der die CPS-Transaction erstellt wurde (z.B. TX_SOURCE_HURRICAN_VERLAUF,
 *                            TX_SOURCE_HURRICAN_LOCK)
 * @param serviceOrderPrio    Prioritaet fuer die CPS-Transaction
 * @param estimatedExecTime   gewuenschter Ausfuehrungszeitpunkt
 * @param serviceOrderStackId (optional) Stack-ID fuer Buendelungen
 * @param serviceOrderStackId (optional) Stack-Sequenz fuer Buendelungen
 * @param lockMode            (optional) Angabe des auszufuehrenden Lock-Modes
 * @param lazyInitMode        (optional) Angabe, falls es sich um eine Initialisierung (LazyInit) des Auftrags handelt
 * @param autoCreation        (optional) gibt an, ob die CPS-Tx ueber einen automatischen Job (z.B. durch Bauauftraege)
 *                            erstellt werden soll
 * @param forceExecType       gibt an, ob der angegebene {@code serviceOrderType} auf jeden Fall verwendet werden soll
 *                            ({@code true}), oder ob er ggf. geaendert werden darf ({@code false}).
 * @param sessionId           aktuelle SessionID des Users
 */
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EI_EXPOSE_REP2", justification = "Dateobjekt wird nicht veraendert!")
public class CreateCPSTransactionParameter {

    private Long auftragId;
    private Long verlaufId;
    private Long serviceOrderType;
    private Long txSource;
    private Long serviceOrderPrio;
    private Date estimatedExecTime;
    private Long serviceOrderStackId;
    private Long serviceOrderStackSeq;
    private String lockMode;
    private LazyInitMode lazyInitMode;
    private Boolean autoCreation;
    private Boolean forceExecType;
    private Long sessionId;

    public CreateCPSTransactionParameter() {
    }

    public CreateCPSTransactionParameter(Long auftragId, Long verlaufId, Long serviceOrderType,
            Long txSource, Long serviceOrderPrio, Date estimatedExecTime, Long serviceOrderStackId,
            Long serviceOrderStackSeq, String lockMode, LazyInitMode lazyInitMode, Boolean autoCreation,
            Boolean forceExecType, Long sessionId) {
        this.auftragId = auftragId;
        this.verlaufId = verlaufId;
        this.serviceOrderType = serviceOrderType;
        this.txSource = txSource;
        this.serviceOrderPrio = serviceOrderPrio;
        this.estimatedExecTime = estimatedExecTime;
        this.serviceOrderStackId = serviceOrderStackId;
        this.serviceOrderStackSeq = serviceOrderStackSeq;
        this.lockMode = lockMode;
        this.lazyInitMode = lazyInitMode;
        this.autoCreation = autoCreation;
        this.forceExecType = forceExecType;
        this.sessionId = sessionId;
    }

    public Long getAuftragId() {
        return auftragId;
    }

    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    public Long getVerlaufId() {
        return verlaufId;
    }

    public void setVerlaufId(Long verlaufId) {
        this.verlaufId = verlaufId;
    }

    public Long getServiceOrderType() {
        return serviceOrderType;
    }

    public void setServiceOrderType(Long serviceOrderType) {
        this.serviceOrderType = serviceOrderType;
    }

    public Long getTxSource() {
        return txSource;
    }

    public void setTxSource(Long txSource) {
        this.txSource = txSource;
    }

    public Long getServiceOrderPrio() {
        return serviceOrderPrio;
    }

    public void setServiceOrderPrio(Long serviceOrderPrio) {
        this.serviceOrderPrio = serviceOrderPrio;
    }

    public Date getEstimatedExecTime() {
        return estimatedExecTime;
    }

    public void setEstimatedExecTime(Date estimatedExecTime) {
        this.estimatedExecTime = estimatedExecTime;
    }

    public Long getServiceOrderStackId() {
        return serviceOrderStackId;
    }

    public void setServiceOrderStackId(Long serviceOrderStackId) {
        this.serviceOrderStackId = serviceOrderStackId;
    }

    public Long getServiceOrderStackSeq() {
        return serviceOrderStackSeq;
    }

    public void setServiceOrderStackSeq(Long serviceOrderStackSeq) {
        this.serviceOrderStackSeq = serviceOrderStackSeq;
    }

    public String getLockMode() {
        return lockMode;
    }

    public void setLockMode(String lockMode) {
        this.lockMode = lockMode;
    }

    public LazyInitMode getLazyInitMode() {
        return lazyInitMode;
    }

    public void setLazyInitMode(LazyInitMode lazyInitMode) {
        this.lazyInitMode = lazyInitMode;
    }

    public Boolean isAutoCreation() {
        return autoCreation;
    }

    public void setAutoCreation(Boolean autoCreation) {
        this.autoCreation = autoCreation;
    }

    public Boolean isForceExecType() {
        return forceExecType;
    }

    public void setForceExecType(Boolean forceExecType) {
        this.forceExecType = forceExecType;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String toString() {
        // @formatter:off
        return new StringBuilder("CreateCPSTransactionParameter [auftragId=").append(auftragId)
                .append(", verlaufId=").append(verlaufId)
                .append(", serviceOrderType=").append(serviceOrderType)
                .append(", txSource=").append(txSource)
                .append(", serviceOrderPrio=").append(serviceOrderPrio)
                .append(", estimatedExecTime=").append(estimatedExecTime)
                .append(", serviceOrderStackId=").append(serviceOrderStackId)
                .append(", serviceOrderStackSeq=").append(serviceOrderStackSeq)
                .append(", lockMode=").append(lockMode)
                .append(", lazyInitMode=").append(lazyInitMode)
                .append(", autoCreation=").append(autoCreation)
                .append(", forceExecType=").append(forceExecType)
                .append(", sessionId=").append(sessionId + "]")
                .toString();
        // @formatter:on
    }
}

