/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.11.13
 */
package de.mnet.wbci.service.impl;

import static de.mnet.wbci.model.WbciGeschaeftsfallStatus.*;

import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.hurrican.annotation.CcTxRequired;
import de.mnet.wbci.dao.WbciDao;
import de.mnet.wbci.exception.WbciServiceException;
import de.mnet.wbci.model.ErledigtmeldungStornoAuf;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciMessage;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wbci.service.WbciGeschaeftsfallStatusUpdateService;

@CcTxRequired
public class WbciGeschaeftsfallStatusUpdateServiceImpl implements WbciGeschaeftsfallStatusUpdateService {
    @Autowired
    protected WbciDao wbciDao;

    @Override
    public void updateGeschaeftsfallStatus(Long geschaeftsfallId, WbciGeschaeftsfallStatus newStatus) {
        WbciGeschaeftsfall gf = wbciDao.findById(geschaeftsfallId, WbciGeschaeftsfall.class);
        updateGeschaeftsfallStatus(gf, newStatus);
        wbciDao.store(gf);
    }

    @Override
    public void updateGeschaeftsfallStatusWithoutCheckAndWithoutStore(WbciGeschaeftsfall wbciGeschaeftsfall, WbciGeschaeftsfallStatus newStatus) {
        wbciGeschaeftsfall.setStatus(newStatus);
    }

    private void updateGeschaeftsfallStatus(WbciGeschaeftsfall wbciGeschaeftsfall, WbciGeschaeftsfallStatus newStatus) {
        WbciGeschaeftsfallStatus currentStatus = wbciGeschaeftsfall.getStatus();
        if (currentStatus == null) {
            wbciGeschaeftsfall.setStatus(newStatus);
        }
        else if (currentStatus.isLegalStatusChange(newStatus)) {
            wbciGeschaeftsfall.setStatus(newStatus);
        }
        else {
            handleInvalidStatusChange(currentStatus, newStatus, wbciGeschaeftsfall.getId());
        }
    }

    private void handleInvalidStatusChange(WbciGeschaeftsfallStatus currentStatus, WbciGeschaeftsfallStatus newStatus, Long geschaeftsfallId) {
        throw new WbciServiceException(String.format("Update of Geschaeftsfall (ID='%s') Status  failed. Cannot update from '%s' to '%s'", geschaeftsfallId, currentStatus, newStatus));
    }

    @Override
    public WbciGeschaeftsfallStatus lookupStatusBasedOnRequestStatusChange(WbciRequestStatus newRequestStatus,
            WbciRequestStatus vaRequestStatus, WbciMessage wbciMessage) {
        switch (newRequestStatus) {
            case ABBM_TR_VERSENDET:
                return ACTIVE;
            case AKM_TR_EMPFANGEN:
                return ACTIVE;
            case VA_EMPFANGEN:
                return ACTIVE;
            case AKM_TR_VERSENDET:
                return ACTIVE;
            case VA_VERSENDET:
                return ACTIVE;
            case VA_VORGEHALTEN:
                return ACTIVE;
            case ABBM_TR_EMPFANGEN:
                return ACTIVE;
            case ABBM_EMPFANGEN:
                return ACTIVE;
            case RUEM_VA_EMPFANGEN:
                return ACTIVE;
            case TV_EMPFANGEN:
                return ACTIVE;
            case TV_ERLM_EMPFANGEN:
                return ACTIVE;
            case TV_ABBM_EMPFANGEN:
                return ACTIVE;
            case TV_VERSENDET:
                return ACTIVE;
            case TV_VORGEHALTEN:
                return ACTIVE;
            case STORNO_VERSENDET:
                return ACTIVE;
            case STORNO_VORGEHALTEN:
                return ACTIVE;
            case STORNO_ABBM_EMPFANGEN:
                return ACTIVE;
            case STORNO_EMPFANGEN:
                return ACTIVE;
            case TV_ERLM_VERSENDET:
                return lookupStatusBasedOnRequestStatusChange(vaRequestStatus, null, wbciMessage);
            case TV_ABBM_VERSENDET:
                return lookupStatusBasedOnRequestStatusChange(vaRequestStatus, null, wbciMessage);
            case STORNO_ABBM_VERSENDET:
                return lookupStatusBasedOnRequestStatusChange(vaRequestStatus, null, wbciMessage);
            case RUEM_VA_VERSENDET:
                if (GeschaeftsfallTyp.VA_RRNP.equals(wbciMessage.getWbciGeschaeftsfall().getTyp())) {
                    return PASSIVE;
                }
                else {
                    return ACTIVE;
                }
            case STORNO_ERLM_EMPFANGEN:
                if (wbciMessage instanceof ErledigtmeldungStornoAuf) {
                    return ACTIVE;
                }
                else {
                    return NEW_VA;
                }
            case STORNO_ERLM_VERSENDET:
                if (wbciMessage instanceof ErledigtmeldungStornoAuf) {
                    return COMPLETE;
                }
                else {
                    return NEW_VA;
                }
            case ABBM_VERSENDET:
                return COMPLETE;
            default:
                throw new UnsupportedOperationException(String.format("The supplied RequestStatus and WbciMessage " +
                        "combination is not supported - RequestStatus:%s, WbciMessage:%s", newRequestStatus, wbciMessage));
        }
    }
}
