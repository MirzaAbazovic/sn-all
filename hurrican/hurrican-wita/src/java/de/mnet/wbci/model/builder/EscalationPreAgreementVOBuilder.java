/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.11.13
 */
package de.mnet.wbci.model.builder;

import de.mnet.wbci.model.BasePreAgreementVO;
import de.mnet.wbci.model.EscalationPreAgreementVO;

/**
 *
 */
public class EscalationPreAgreementVOBuilder extends BasePreAgreementVOBuilder<EscalationPreAgreementVO> {

    private EscalationPreAgreementVO.EscalationType escalationType;
    private EscalationPreAgreementVO.EscalationLevel escalationLevel;
    private Long deadlineDays;

    public EscalationPreAgreementVO build() {
        EscalationPreAgreementVO vo = new EscalationPreAgreementVO();
        vo = enrich(vo);
        vo.setEscalationType(escalationType);
        vo.setEscalationLevel(escalationLevel);
        vo.setDeadlineDays(deadlineDays);
        return vo;
    }

    public EscalationPreAgreementVOBuilder withBasePreAgreementVo(BasePreAgreementVO basePreAgreementVO) {
        if (basePreAgreementVO != null) {
            this.vaid = basePreAgreementVO.getVaid();
            this.gfType = basePreAgreementVO.getGfType();
            this.ekpAbg = basePreAgreementVO.getEkpAbg();
            this.ekpAuf = basePreAgreementVO.getEkpAuf();
            this.vorgabeDatum = basePreAgreementVO.getVorgabeDatum();
            this.wechseltermin = basePreAgreementVO.getWechseltermin();
            this.requestStatus = basePreAgreementVO.getRequestStatus();
            this.geschaeftsfallStatus = basePreAgreementVO.getGeschaeftsfallStatus();
            this.aenderungskz = basePreAgreementVO.getRequestTyp();
            this.processedAt = basePreAgreementVO.getProcessedAt();
            this.daysUntilDeadlinePartner = basePreAgreementVO.getDaysUntilDeadlinePartner();
            this.daysUntilDeadlineMnet = basePreAgreementVO.getDaysUntilDeadlineMnet();
            this.rueckmeldeDatum = basePreAgreementVO.getRueckmeldeDatum();
        }
        return this;
    }

    public EscalationPreAgreementVOBuilder withEscalationType(EscalationPreAgreementVO.EscalationType escalationType) {
        this.escalationType = escalationType;
        return this;
    }

    public EscalationPreAgreementVOBuilder withEscalationLevel(EscalationPreAgreementVO.EscalationLevel escalationLevel) {
        this.escalationLevel = escalationLevel;
        return this;
    }


    public EscalationPreAgreementVOBuilder withDeadlineDays(Long deadlineDays) {
        this.deadlineDays = deadlineDays;
        return this;
    }
}
