/* 
 * Copyright (c) 2013 - M-net Telekommunikations GmbH 
 * All rights reserved. 
 * ------------------------------------------------------- 
 * File created: 02.10.13 
 */
package de.mnet.wbci.service.impl;

import java.time.*;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import de.mnet.wbci.dao.WbciDao;
import de.mnet.wbci.exception.InvalidRequestStatusChangeException;
import de.mnet.wbci.exception.WbciRequestNotFoundException;
import de.mnet.wbci.exception.WbciServiceException;
import de.mnet.wbci.model.MeldungTyp;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wbci.service.WbciDeadlineService;
import de.mnet.wbci.service.WbciGeschaeftsfallService;
import de.mnet.wbci.service.WbciRequestStatusUpdateService;
import de.mnet.wbci.service.WbciWitaServiceFacade;

@Transactional(
        value = "cc.hibernateTxManager",
        noRollbackFor = InvalidRequestStatusChangeException.class
)
public class WbciRequestStatusUpdateServiceImpl implements WbciRequestStatusUpdateService {

    @Autowired
    protected WbciDao wbciDao;

    @Autowired
    protected WbciGeschaeftsfallService wbciGeschaeftsfallService;

    @Autowired
    protected WbciDeadlineService wbciDeadlineService;

    @Autowired
    protected WbciWitaServiceFacade wbciWitaServiceFacade;


    /**
     * {@inheritDoc}
     */
    @Override
    public void updateVorabstimmungsAnfrageStatus(String vorabstimmungsId, WbciRequestStatus newStatus, String meldungCodes, MeldungTyp meldungType, LocalDateTime meldungDate) {
        List<VorabstimmungsAnfrage> requests = wbciDao.findWbciRequestByType(vorabstimmungsId, VorabstimmungsAnfrage.class);
        if (CollectionUtils.isEmpty(requests)) {
            handleWbciRequestNotFound(vorabstimmungsId, null);
        }

        updateRequest(requests.get(0), newStatus, meldungCodes, meldungType, meldungDate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateTerminverschiebungAnfrageStatus(String vorabstimmungsId, String changeId, WbciRequestStatus newStatus, String meldungCodes, MeldungTyp meldungType, LocalDateTime meldungDate) {
        List<WbciRequest> requests = wbciDao.findWbciRequestByChangeId(vorabstimmungsId, changeId);
        if (CollectionUtils.isEmpty(requests)) {
            handleWbciRequestNotFound(vorabstimmungsId, changeId);
        }

        updateRequest(requests.get(0), newStatus, meldungCodes, meldungType, meldungDate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateStornoAnfrageStatus(String vorabstimmungsId, String stornoId, WbciRequestStatus newStatus, String meldungCodes, MeldungTyp meldungType, LocalDateTime meldungDate) {
        List<WbciRequest> requests = wbciDao.findWbciRequestByChangeId(vorabstimmungsId, stornoId);
        if (CollectionUtils.isEmpty(requests)) {
            handleWbciRequestNotFound(vorabstimmungsId, stornoId);
        }

        updateRequest(requests.get(0), newStatus, meldungCodes, meldungType, meldungDate);
    }

    private void updateRequest(WbciRequest request, WbciRequestStatus newStatus, String meldungCodes, MeldungTyp meldungType, LocalDateTime meldungDate) {
        WbciRequestStatus currentStatus = request.getRequestStatus();
        if (currentStatus != null && currentStatus.isLegalStatusChange(newStatus)) {
            request.setRequestStatus(newStatus);
            request.setUpdatedAt(new Date());
            request.setLastMeldungCodes(meldungCodes);
            request.setLastMeldungType(meldungType);
            request.setLastMeldungDate(Date.from(meldungDate.atZone(ZoneId.systemDefault()).toInstant()));
            wbciDao.store(request);

            try {
                wbciDeadlineService.updateAnswerDeadline(request);
                wbciWitaServiceFacade.updateOrCreateWitaVorabstimmungAbgebend(request);
            }
            catch (WbciServiceException e) {
                handleInvalidStatusChange(currentStatus, newStatus, request.getId(), e);
            }
        }
        else {
            handleInvalidStatusChange(currentStatus, newStatus, request.getId());
        }
    }

    private void handleInvalidStatusChange(WbciRequestStatus currentStatus, WbciRequestStatus newStatus, Long id, WbciServiceException nestedException) {
        throw new InvalidRequestStatusChangeException(id, currentStatus, newStatus, nestedException);
    }

    private void handleWbciRequestNotFound(String vorabstimmungsId, String changeId) {
        throw new WbciRequestNotFoundException(vorabstimmungsId, changeId);
    }

    private void handleInvalidStatusChange(WbciRequestStatus currentStatus, WbciRequestStatus newStatus, Long wbciRequestId) {
        throw new InvalidRequestStatusChangeException(wbciRequestId, currentStatus, newStatus);
    }

}
