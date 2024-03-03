/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.07.13
 */
package de.mnet.wbci.model;

import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;

/**
 * Represents the matching between the GUI table which lists all pre-agreements and a {@link WbciRequest}
 */
public class PreAgreementVO extends BasePreAgreementVO implements CCAuftragModel {

    private static final long serialVersionUID = -8943029749170332860L;

    private Long wbciRequestId;
    private Long auftragId;
    private Long auftragNoOrig;
    private PreAgreementType preAgreementType;
    private MeldungTyp rueckmeldung;
    private String meldungCodes;
    private Long userId;
    private String userName;
    private String niederlassung;
    private Technologie mnetTechnologie;
    private Boolean klaerfall;
    private Long teamId;
    private String teamName;
    private Long currentUserId;
    private String currentUserName;
    private String internalStatus;
    private boolean automationErrors;

    public Long getWbciRequestId() {
        return wbciRequestId;
    }

    public void setWbciRequestId(Long wbciRequestId) {
        this.wbciRequestId = wbciRequestId;
    }

    @Override
    public Long getAuftragId() {
        return auftragId;
    }

    @Override
    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    public Long getAuftragNoOrig() {
        return auftragNoOrig;
    }

    public void setAuftragNoOrig(Long auftragsNoOrig) {
        this.auftragNoOrig = auftragsNoOrig;
    }

    public PreAgreementType getPreAgreementType() {
        return preAgreementType;
    }

    public void setPreAgreementType(PreAgreementType preAgreementType) {
        this.preAgreementType = preAgreementType;
    }

    public String getRueckmeldung() {
        if (rueckmeldung != null) {
            return rueckmeldung.toString();
        }
        return null;
    }

    public void setRueckmeldung(MeldungTyp rueckmeldung) {
        this.rueckmeldung = rueckmeldung;
    }

    public String getMeldungCodes() {
        return meldungCodes;
    }

    public void setMeldungCodes(String meldungCodes) {
        this.meldungCodes = meldungCodes;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getNiederlassung() {
        return niederlassung;
    }

    public void setNiederlassung(String niederlassung) {
        this.niederlassung = niederlassung;
    }

    public String getMnetTechnologie() {
        return mnetTechnologie != null ? mnetTechnologie.getWbciTechnologieCode() : "";
    }

    public void setMnetTechnologie(Technologie mnetTechnologie) {
        this.mnetTechnologie = mnetTechnologie;
    }

    public Boolean getKlaerfall() {
        return klaerfall;
    }

    public void setKlaerfall(Boolean klaerfall) {
        this.klaerfall = klaerfall;
    }

    public String getCurrentUserName() {
        return currentUserName;
    }

    public void setCurrentUserName(String currentUserName) {
        this.currentUserName = currentUserName;
    }

    public Long getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(Long currentUserId) {
        this.currentUserId = currentUserId;
    }

    public void setInternalStatus(String internalStatus) {
        this.internalStatus = internalStatus;
    }

    public String getInternalStatus() {
        return internalStatus;
    }

    public boolean isAutomationErrors() {
        return automationErrors;
    }

    public void setAutomationErrors(boolean automationErrors) {
        this.automationErrors = automationErrors;
    }

    @Override
    public String toString() {
        return "PreAgreementVO{" +
                super.toString() +
                ", wbciRequestId=" + wbciRequestId +
                ", auftragId=" + auftragId +
                ", auftragNoOrig=" + auftragNoOrig +
                ", preAgreementType=" + preAgreementType +
                ", rueckmeldung=" + rueckmeldung +
                ", meldungCodes='" + meldungCodes + '\'' +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                ", niederlassung='" + niederlassung + '\'' +
                ", mnetTechnologie=" + mnetTechnologie +
                ", klaerfall=" + klaerfall +
                ", teamId=" + teamId +
                ", teamName='" + teamName + '\'' +
                ", currentUserId=" + currentUserId +
                ", currentUserName='" + currentUserName + '\'' +
                ", internalStatus='" + internalStatus + '\'' +
                ", automationErrors='" + automationErrors + '\'' +
                '}';
    }

}
