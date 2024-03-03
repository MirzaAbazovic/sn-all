/* 
 * Copyright (c) 2014 - M-net Telekommunikations GmbH 
 * All rights reserved. 
 * ------------------------------------------------------- 
 * File created: 17.01.14 
 */
package de.mnet.wbci.service.impl;

import java.time.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.mnet.wbci.dao.WbciDao;
import de.mnet.wbci.model.Antwortfrist;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wbci.service.WbciCommonService;
import de.mnet.wbci.service.WbciDeadlineService;

@CcTxRequired
public class WbciDeadlineServiceImpl implements WbciDeadlineService {
    private static final Logger LOGGER = Logger.getLogger(WbciDeadlineServiceImpl.class);

    @Autowired
    private WbciDao wbciDao;
    @Autowired
    private WbciCommonService wbciCommonService;

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateAnswerDeadline(WbciRequest wbciRequest) {
        final WbciRequestStatus requestStatus = wbciRequest.getRequestStatus();
        final RequestTyp typ = wbciRequest.getTyp();
        wbciRequest.setAnswerDeadline(calculateAnswerDeadline(wbciRequest, wbciDao.findAntwortfrist(typ, requestStatus)));
        wbciRequest.setIsMnetDeadline(isMnetDeadline(wbciRequest));
        wbciDao.store(wbciRequest);

        //on TV_ERLM update also the VA request to ensure that the deadline is updated also
        if (isTvErlm(wbciRequest)) {
            VorabstimmungsAnfrage va = wbciCommonService.findVorabstimmungsAnfrage(wbciRequest.getVorabstimmungsId());
            updateAnswerDeadline(va);
        }
    }

    /**
     * Calculates the latest deadline for answering a wbci request based on the updatedAt date of the request and the
     * antwortfrist. For the calculation only working days will be considered!
     *
     * @param wbciRequest
     * @param antwortfrist
     * @return
     */
    protected LocalDate calculateAnswerDeadline(WbciRequest wbciRequest, Antwortfrist antwortfrist) {
        if (antwortfrist != null && antwortfrist.getFristInStunden() != null && !isRuemVaAndRrnp(wbciRequest)) {
            if (isRuemVa(wbciRequest)) {
                //for RUEM-VAs calculate wechseltermin minus x days antwortfrist
                return DateCalculationHelper.addWorkingDays(wbciRequest.getWbciGeschaeftsfall().getWechseltermin(),
                        antwortfrist.getFristInTagen().intValue() * -1);
            }
            if (isStrAenErledigtMeldung(wbciRequest)) {
                // for STR-AEN Erledigt Meldung calculate the deadline based on wechseltermin minus x days antwortfrist
                return DateCalculationHelper.addWorkingDays(wbciRequest.getWbciGeschaeftsfall().getWechseltermin(),
                        antwortfrist.getFristInTagen().intValue() * -1);
            }
            return DateCalculationHelper.addWorkingDays(Instant.ofEpochMilli(wbciRequest.getUpdatedAt().getTime()).atZone(ZoneId.systemDefault()).toLocalDate(),
                    antwortfrist.getFristInTagen().intValue());

        }
        return null;
    }

    /**
     * @param wbciRequest
     * @return
     */
    protected Boolean isMnetDeadline(WbciRequest wbciRequest) {
        final WbciRequestStatus requestStatus = wbciRequest.getRequestStatus();
        if (wbciRequest.getAnswerDeadline() != null) {
            if (WbciRequestStatus.STORNO_ERLM_EMPFANGEN == requestStatus || WbciRequestStatus.STORNO_ERLM_VERSENDET == requestStatus) {
                return wbciRequest.getWbciGeschaeftsfall().getAufnehmenderEKP() == CarrierCode.MNET;
            }
            else {
                return requestStatus.isInbound();
            }
        }
        return Boolean.FALSE;
    }

    /**
     * Checks the current Request-Typ is StornoAenderung and the request status is ERLEDIGT.
     */
    private boolean isStrAenErledigtMeldung(WbciRequest wbciRequest) {
        final WbciRequestStatus requestStatus = wbciRequest.getRequestStatus();
        return wbciRequest.getTyp().isStornoAenderung() && requestStatus.isStornoErledigtRequestStatus();
    }

    /**
     * Checks whether the current GF-Typ is RRNP and the request status is RUEM_VA_EMPFANGEN or RUEM_VA_VERSENDET.
     */
    private boolean isRuemVaAndRrnp(WbciRequest wbciRequest) {
        final WbciRequestStatus requestStatus = wbciRequest.getRequestStatus();
        return GeschaeftsfallTyp.VA_RRNP == wbciRequest.getWbciGeschaeftsfall().getTyp()
                && (WbciRequestStatus.RUEM_VA_EMPFANGEN == requestStatus || WbciRequestStatus.RUEM_VA_VERSENDET == requestStatus);
    }

    /**
     * Checks the request status is RUEM_VA_EMPFANGEN or RUEM_VA_VERSENDET.
     */
    private boolean isRuemVa(WbciRequest wbciRequest) {
        return WbciRequestStatus.RUEM_VA_EMPFANGEN == wbciRequest.getRequestStatus() || WbciRequestStatus.RUEM_VA_VERSENDET == wbciRequest.getRequestStatus();
    }

    /**
     * Checks the request status is TV_ERLM_EMPFANGEN or TV_ERLM_VERSENDET.
     */
    private boolean isTvErlm(WbciRequest request) {
        return WbciRequestStatus.TV_ERLM_EMPFANGEN.equals(request.getRequestStatus()) ||
                WbciRequestStatus.TV_ERLM_VERSENDET.equals(request.getRequestStatus());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Antwortfrist findAntwortfrist(RequestTyp typ, WbciRequestStatus wbciRequestStatus) {
        return wbciDao.findAntwortfrist(typ, wbciRequestStatus);
    }


}
