/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.07.2014
 */
package de.mnet.wbci.ticketing.escalation;

import de.mnet.wbci.model.BasePreAgreementVO;
import de.mnet.wbci.model.EscalationPreAgreementVO;
import de.mnet.wbci.model.PreAgreementVO;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequestStatus;

/**
 *
 */
public class EscalationHelper {
    /**
     * Determines the current DeadlineDays in consideration of the internal flag: <ul> <li>internal == true: {@link
     * PreAgreementVO#daysUntilDeadlineMnet}</li> <li>internal == false: {@link PreAgreementVO#daysUntilDeadlinePartner}</li>
     * </ul>
     */
    static Integer determineDeadlineDays(EscalationReportGenerator.EscalationListType escalationListType, BasePreAgreementVO vo) {
        if (EscalationReportGenerator.EscalationListType.INTERNAL.equals(escalationListType)) {
            return vo.getDaysUntilDeadlineMnet();
        }
        return vo.getDaysUntilDeadlinePartner();
    }

    static EscalationPreAgreementVO.EscalationType getResponsibleEscalationType(RequestTyp typ, WbciRequestStatus requestStatus, WbciGeschaeftsfallStatus geschaeftsfallStatus) {
        if (WbciGeschaeftsfallStatus.NEW_VA_EXPIRED.equals(geschaeftsfallStatus)) {
            return EscalationPreAgreementVO.EscalationType.NEW_VA_EXPIRED;
        }
        for (EscalationPreAgreementVO.EscalationType esc : EscalationPreAgreementVO.EscalationType.values()) {
            if (typ != null && requestStatus != null
                    && typ.equals(esc.getTyp()) && requestStatus.equals(esc.getRequestStatus())) {
                return esc;
            }
        }
        return null;
    }
}
