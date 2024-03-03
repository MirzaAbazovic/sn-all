/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.11.13
 */
package de.mnet.wbci.model.builder;

import de.mnet.wbci.model.MeldungTyp;
import de.mnet.wbci.model.PreAgreementType;
import de.mnet.wbci.model.PreAgreementVO;
import de.mnet.wbci.model.Technologie;

/**
 *
 */
public class PreAgreementVOBuilder extends BasePreAgreementVOBuilder<PreAgreementVO> {

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
    private boolean automationErrors;

    public PreAgreementVO build() {
        PreAgreementVO vo = new PreAgreementVO();
        vo = enrich(vo);
        vo.setWbciRequestId(wbciRequestId);
        vo.setAuftragId(auftragId);
        vo.setAuftragNoOrig(auftragNoOrig);
        vo.setPreAgreementType(preAgreementType);
        vo.setRueckmeldung(rueckmeldung);
        vo.setMeldungCodes(meldungCodes);
        vo.setUserId(userId);
        vo.setUserName(userName);
        vo.setNiederlassung(niederlassung);
        vo.setMnetTechnologie(mnetTechnologie);
        vo.setKlaerfall(klaerfall);
        vo.setTeamId(teamId);
        vo.setTeamName(teamName);
        vo.setCurrentUserId(currentUserId);
        vo.setCurrentUserName(currentUserName);
        vo.setAutomationErrors(automationErrors);
        return vo;
    }

    public PreAgreementVOBuilder withWbciRequestId(Long wbciRequestId) {
        this.wbciRequestId = wbciRequestId;
        return this;
    }

    public PreAgreementVOBuilder withAuftragId(Long auftragId) {
        this.auftragId = auftragId;
        return this;
    }

    public PreAgreementVOBuilder withAuftragNoOrig(Long auftragNoOrig) {
        this.auftragNoOrig = auftragNoOrig;
        return this;
    }

    public PreAgreementVOBuilder withKundenTyp(PreAgreementType preAgreementType) {
        this.preAgreementType = preAgreementType;
        return this;
    }

    public PreAgreementVOBuilder withRueckmeldung(MeldungTyp rueckmeldung) {
        this.rueckmeldung = rueckmeldung;
        return this;
    }

    public PreAgreementVOBuilder withMeldungsCodes(String meldungsCodes) {
        this.meldungCodes = meldungsCodes;
        return this;
    }

    public PreAgreementVOBuilder withUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public PreAgreementVOBuilder withUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public PreAgreementVOBuilder withNiederlassung(String niederlassung) {
        this.niederlassung = niederlassung;
        return this;
    }

    public PreAgreementVOBuilder withMnetTechnologie(Technologie mnetTechnologie) {
        this.mnetTechnologie = mnetTechnologie;
        return this;
    }

    public PreAgreementVOBuilder withKlaerfall(Boolean klaerfall) {
        this.klaerfall = klaerfall;
        return this;
    }

    public PreAgreementVOBuilder withTeamId(Long teamId) {
        this.teamId = teamId;
        return this;
    }

    public PreAgreementVOBuilder withTeamName(String teamName) {
        this.teamName = teamName;
        return this;
    }

    public PreAgreementVOBuilder withCurrentUserId(Long currentUserId) {
        this.currentUserId = currentUserId;
        return this;
    }

    public PreAgreementVOBuilder withCurrentUserName(String currentUserName) {
        this.currentUserName = currentUserName;
        return this;
    }

    public PreAgreementVOBuilder withAutomationErrors(boolean automationErrors) {
        this.automationErrors = automationErrors;
        return this;
    }
}
