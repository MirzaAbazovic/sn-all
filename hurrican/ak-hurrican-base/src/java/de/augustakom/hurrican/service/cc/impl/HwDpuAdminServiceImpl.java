/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.09.2016
 */
package de.augustakom.hurrican.service.cc.impl;

import javax.annotation.*;

import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.hardware.HWDpu;
import de.augustakom.hurrican.service.cc.FTTXHardwareService;
import de.augustakom.hurrican.service.cc.HwDpuAdminService;
import de.augustakom.hurrican.service.cc.HwOltChildHelperService;

/**
 * Created by solomonda on 05.09.2016.
 */
public class HwDpuAdminServiceImpl extends DefaultCCService implements HwDpuAdminService {
    @Resource(name = "de.augustakom.hurrican.service.cc.FTTXHardwareService")
    private FTTXHardwareService fttxHardwareService;

    @Resource(name = "de.augustakom.hurrican.service.cc.HwOltChildHelperService")
    private HwOltChildHelperService hwOltChildHelperService;

    @Override
    @SuppressWarnings("unchecked")
    public Long createAndSendCreateDeviceCpsTransaction(@Nonnull HWDpu dpu, Long sessionId) {
        checkPreconditions(dpu);
        return hwOltChildHelperService.createAndSendCpsTx(dpu, CPSTransaction.SERVICE_ORDER_TYPE_CREATE_DEVICE,
                sessionId).getId();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Long createAndSendModifyDeviceCpsTransaction(@Nonnull HWDpu dpu, Long sessionId) {
        checkPreconditions(dpu);
        return hwOltChildHelperService.createAndSendCpsTx(dpu, CPSTransaction.SERVICE_ORDER_TYPE_MODIFY_DEVICE,
                sessionId).getId();
    }

    @Override
    public Long createAndSendDeleteDeviceCpsTransaction(@Nonnull HWDpu dpu, Long sessionId) {
        try {
            return fttxHardwareService.checkHwOltChildForActiveAuftraegeAndDelete(dpu, false, sessionId);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private void checkPreconditions(@Nonnull final HWDpu dpu) {
        if (hwOltChildHelperService.isOltChildDeleted(dpu)) {
            throw new RuntimeException("Aktion kann nicht ausgeführt werden, da DPu bereits gelöscht ist.");
        }

        if (!fttxHardwareService.checkIfDpuFieldsComplete(dpu)) {
            throw new RuntimeException("Aktion kann nicht ausgeführt werden, da Pflichtfelder der DPU leer sind.");
        }
    }
}
