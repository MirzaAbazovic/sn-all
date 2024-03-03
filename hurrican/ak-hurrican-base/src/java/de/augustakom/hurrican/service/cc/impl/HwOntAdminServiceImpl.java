/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.09.2014
 */
package de.augustakom.hurrican.service.cc.impl;

import javax.annotation.*;

import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.hardware.HWOnt;
import de.augustakom.hurrican.service.cc.FTTXHardwareService;
import de.augustakom.hurrican.service.cc.HwOltChildHelperService;
import de.augustakom.hurrican.service.cc.HwOntAdminService;

public class HwOntAdminServiceImpl extends DefaultCCService implements HwOntAdminService {

    @Resource(name = "de.augustakom.hurrican.service.cc.FTTXHardwareService")
    private FTTXHardwareService fttxHardwareService;

    @Resource(name = "de.augustakom.hurrican.service.cc.HwOltChildHelperService")
    private HwOltChildHelperService hwOltChildHelperService;

    @Override
    @SuppressWarnings("unchecked")
    public Long createAndSendCreateDeviceCpsTransaction(@Nonnull HWOnt ont, Long sessionId) {
        checkPreconditions(ont);
        return hwOltChildHelperService.createAndSendCpsTx(ont, CPSTransaction.SERVICE_ORDER_TYPE_CREATE_DEVICE,
                sessionId).getId();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Long createAndSendModifyDeviceCpsTransaction(@Nonnull HWOnt ont, Long sessionId) {
        checkPreconditions(ont);
        return hwOltChildHelperService.createAndSendCpsTx(ont, CPSTransaction.SERVICE_ORDER_TYPE_MODIFY_DEVICE,
                sessionId).getId();
    }

    @Override
    public Long createAndSendDeleteDeviceCpsTransaction(@Nonnull HWOnt ont, Long sessionId) {
        try {
            return fttxHardwareService.checkHwOltChildForActiveAuftraegeAndDelete(ont, false, sessionId);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private void checkPreconditions(@Nonnull final HWOnt ont) {
        if (hwOltChildHelperService.isOltChildDeleted(ont)) {
            throw new RuntimeException("Aktion kann nicht ausgeführt werden, da ONT bereits gelöscht ist.");
        }
        if (!fttxHardwareService.checkIfOntFieldsComplete(ont)) {
            throw new RuntimeException("Aktion kann nicht ausgeführt werden, da Pflichtfelder der ONT leer sind.");
        }
    }
}
