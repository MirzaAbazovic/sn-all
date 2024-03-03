/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.10.13
 */
package de.mnet.wbci.service.impl;

import java.time.*;
import java.util.*;
import org.apache.log4j.Logger;
import org.hibernate.LockMode;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.annotation.CcTxRequiresNew;
import de.mnet.wbci.dao.WbciDao;
import de.mnet.wbci.exception.WbciBaseException;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wbci.service.WbciSchedulerService;
import de.mnet.wbci.service.WbciSchemaValidationService;
import de.mnet.wbci.service.WbciSendMessageService;
import de.mnet.wita.model.WitaSendLimit;
import de.mnet.wita.service.WitaConfigService;

/**
 *
 */
@CcTxRequired
public class WbciSchedulerServiceImpl implements WbciSchedulerService {

    private static final Logger LOGGER = Logger.getLogger(WbciSchedulerServiceImpl.class);
    @Autowired
    protected WbciDao wbciDao;
    @Autowired
    private WitaConfigService witaConfigService;
    @Autowired
    private WbciSendMessageService wbciSendMessageService;
    @Autowired
    private WbciSchemaValidationService wbciSchemaValidationService;

    @Override
    public <T extends WbciRequest> void scheduleRequest(T request) {
        final int minutesRequestOnHold = witaConfigService.getWbciMinutesWhileRequestIsOnHold();
        request.setSendAfter(Date.from(LocalDateTime.now().plusMinutes(minutesRequestOnHold).atZone(ZoneId.systemDefault()).toInstant()));
        wbciDao.store(request);

        if (minutesRequestOnHold < 0) {
            // immediately send the message
            sendAndProcessMessage(request);
        }
        else {
            /**
             * Check if the request is valid, to inform the user immediately about schema errors and prevent entries in
             * the T_EXCEPTION_LOG table at the scheduled sending.
             */
            final WbciCdmVersion wbciCdmVersion = witaConfigService.getWbciCdmVersion(request.getEKPPartner());
            wbciSchemaValidationService.validateWbciMessage(request, wbciCdmVersion);
        }
    }

    @Override
    public List<Long> findScheduledWbciRequestIds() {
        return wbciDao.findScheduledWbciRequestIds();
    }

    @Override
    @CcTxRequiresNew
    public void sendScheduledRequest(Long requestId) {
        WbciRequest request = wbciDao.byIdWithLockMode(requestId, LockMode.PESSIMISTIC_WRITE, WbciRequest.class);
        if (request.getProcessedAt() != null) {
            throw new WbciBaseException(String.format("Wbci request can not be sent out, because it is already processed. " +
                    "Request: '%s'", request.toString()));
        }
        if (WbciGeschaeftsfallStatus.COMPLETE.equals(request.getWbciGeschaeftsfall().getStatus())) {
            throw new WbciBaseException(String.format("Wbci request can not be sent out, because the Geschaeftsfall is already completed. " +
                    "Request: '%s'", request.toString()));
        }
        sendAndProcessMessage(request);
    }

    private <T extends WbciRequest> void sendAndProcessMessage(T request) {
        final WbciGeschaeftsfall wbciGeschaeftsfall = request.getWbciGeschaeftsfall();
        final WitaSendLimit witaSendLimit = witaConfigService.findWitaSendLimit(wbciGeschaeftsfall.getTyp().name(),
                null, wbciGeschaeftsfall.getAbgebenderEKP().getITUCarrierCode());
        if (witaSendLimit != null && !witaSendLimit.isLimitInfinite()) {
            final Long sendLimit = witaSendLimit.getWitaSendLimit();
            Long existingCount = witaConfigService.getWitaSentCount(witaSendLimit.getGeschaeftsfallTyp(),
                    witaSendLimit.getKollokationsTyp(), witaSendLimit.getItuCarrierCode());
            if (existingCount == null || NumberTools.isLess(existingCount, sendLimit)) {
                wbciSendMessageService.sendAndProcessMessage(request);
                witaConfigService.createSendLog(request);
            }
            else {
                LOGGER.warn(String.format("Das Senden von Requests '%s' an Carrier '%s' ist auf '%s' limitiert. " +
                                "Das Limit wurde bereits erreicht und somit ist kein Versenden mehr möglich. Um weitere " +
                                "Requests für diesen Carrier versenden zu können, muss das Sendelimit erhöht werden.",
                        wbciGeschaeftsfall.getTyp(), wbciGeschaeftsfall.getAbgebenderEKP().getITUCarrierCode(), existingCount
                ));
            }
        }
        else {
            wbciSendMessageService.sendAndProcessMessage(request);
        }
    }

}
