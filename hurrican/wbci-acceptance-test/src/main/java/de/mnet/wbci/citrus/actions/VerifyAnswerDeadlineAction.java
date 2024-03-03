/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.01.14
 */
package de.mnet.wbci.citrus.actions;

import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import com.consol.citrus.context.TestContext;
import com.google.common.collect.ImmutableMap;

import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wbci.service.WbciCommonService;

/**
 *
 */
public class VerifyAnswerDeadlineAction extends AbstractRequestAction {

    private static final Map<WbciRequestStatus, Integer> ANSWER_DEADLINE_MAP = ImmutableMap.<WbciRequestStatus, Integer>builder()
            .put(WbciRequestStatus.RUEM_VA_EMPFANGEN, 8)
            .put(WbciRequestStatus.RUEM_VA_VERSENDET, 8)
            .put(WbciRequestStatus.VA_EMPFANGEN, 3)
            .put(WbciRequestStatus.VA_VERSENDET, 3)
            .put(WbciRequestStatus.TV_EMPFANGEN, 3)
            .put(WbciRequestStatus.TV_VERSENDET, 3)
            .put(WbciRequestStatus.STORNO_EMPFANGEN, 3)
            .put(WbciRequestStatus.STORNO_VERSENDET, 3)
            .put(WbciRequestStatus.STORNO_ERLM_EMPFANGEN, 1)
            .put(WbciRequestStatus.STORNO_ERLM_VERSENDET, 1)
            .build();

    private static final int STR_AEN_ERLM_DEADLINE = 7;

    private final boolean isRequestValid;

    public VerifyAnswerDeadlineAction(WbciCommonService wbciCommonService, RequestTyp requestTyp, boolean isRequestValid) {
        super("verifyAnswerDeadline", wbciCommonService, requestTyp);
        this.isRequestValid = isRequestValid;
    }

    @Override
    public void doExecute(TestContext context) {
        final WbciRequest request = retrieve(context);
        final Integer expectedDaysTillDeadline = getExpectedDaysTillDeadline(request);
        if (isRequestValid && !isRuemVaAndRrnp(request) && expectedDaysTillDeadline != null) {
            assertNotNull(request.getIsMnetDeadline());
            assertEquals(request.getIsMnetDeadline(), isMnetDeadline(request));
            assertNotNull(request.getAnswerDeadline());
            if (isRuemVa(request)) {
                assertEquals(request.getAnswerDeadline(),
                        DateCalculationHelper.addWorkingDays(request.getWbciGeschaeftsfall().getWechseltermin(),
                                expectedDaysTillDeadline * -1),
                        String.format("Die gesetze Frist bei der %s ('%s') wurde nicht korrekt gesetzt", request.getTyp().getShortName(), request.getVorabstimmungsId())
                );
            }
            else if (isStrAenErledigt(request)) {
                assertEquals(request.getAnswerDeadline(),
                        DateCalculationHelper.addWorkingDays(request.getWbciGeschaeftsfall().getWechseltermin(),
                                expectedDaysTillDeadline * -1)
                );
            }
            else {
                assertEquals(request.getAnswerDeadline(),
                        DateCalculationHelper.addWorkingDays(Instant.ofEpochMilli(request.getUpdatedAt().getTime()).atZone(ZoneId.systemDefault()).toLocalDate(),
                                expectedDaysTillDeadline),
                        String.format("Die gesetze Frist bei der %s ('%s') wurde nicht korrekt gesetzt", request.getTyp().getShortName(), request.getVorabstimmungsId())
                );
            }
        }
        else {
            assertFalse(request.getIsMnetDeadline());
            assertNull(request.getAnswerDeadline());
        }
    }

    private Integer getExpectedDaysTillDeadline(WbciRequest request) {
        // The deadline for a Storno Erledigt Meldungen depends on the request type (STR-AEN or STR-AUF)
        if (isStrAenErledigt(request)) {
            return STR_AEN_ERLM_DEADLINE;
        }
        return ANSWER_DEADLINE_MAP.get(request.getRequestStatus());
    }

    private boolean isRuemVaAndRrnp(WbciRequest wbciRequest) {
        final WbciRequestStatus requestStatus = wbciRequest.getRequestStatus();
        return GeschaeftsfallTyp.VA_RRNP == wbciRequest.getWbciGeschaeftsfall().getTyp()
                && (WbciRequestStatus.RUEM_VA_EMPFANGEN == requestStatus || WbciRequestStatus.RUEM_VA_VERSENDET == requestStatus);
    }

    private boolean isRuemVa(WbciRequest wbciRequest) {
        return WbciRequestStatus.RUEM_VA_EMPFANGEN == wbciRequest.getRequestStatus() || WbciRequestStatus.RUEM_VA_VERSENDET == wbciRequest.getRequestStatus();
    }

    private boolean isStrAenErledigt(WbciRequest wbciRequest) {
        return wbciRequest.getRequestStatus().isStornoErledigtRequestStatus() && wbciRequest.getTyp().isStornoAenderung();
    }

    private Boolean isMnetDeadline(WbciRequest wbciRequest) {
        final WbciRequestStatus requestStatus = wbciRequest.getRequestStatus();
        if (WbciRequestStatus.STORNO_ERLM_EMPFANGEN == requestStatus || WbciRequestStatus.STORNO_ERLM_VERSENDET == requestStatus) {
            return wbciRequest.getWbciGeschaeftsfall().getAufnehmenderEKP() == CarrierCode.MNET;
        }
        else {
            return requestStatus.isInbound();
        }
    }

}
