/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.09.2014
 */
package de.augustakom.hurrican.service.cc.impl;

import javax.annotation.*;

import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.hardware.HWDpo;
import de.augustakom.hurrican.service.cc.FTTXHardwareService;
import de.augustakom.hurrican.service.cc.HwDpoAdminService;
import de.augustakom.hurrican.service.cc.HwOltChildHelperService;

/**
 * STUBS for HUR-19355 TODO implement and maybe consolidate with {@code de.augustakom.hurrican.service.cc.impl.HwOntAdminServiceImpl}
 */
public class HwDpoAdminServiceImpl extends DefaultCCService implements HwDpoAdminService {

    @Resource(name = "de.augustakom.hurrican.service.cc.FTTXHardwareService")
    private FTTXHardwareService fttxHardwareService;

    @Resource(name = "de.augustakom.hurrican.service.cc.HwOltChildHelperService")
    private HwOltChildHelperService hwOltChildHelperService;

    @Override
    @SuppressWarnings("unchecked")
    public Long createAndSendCreateDeviceCpsTransaction(@Nonnull HWDpo dpo, Long sessionId) {
        checkPreconditions(dpo);
        return hwOltChildHelperService.createAndSendCpsTx(dpo, CPSTransaction.SERVICE_ORDER_TYPE_CREATE_DEVICE,
                sessionId).getId();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Long createAndSendModifyDeviceCpsTransaction(@Nonnull HWDpo dpo, Long sessionId) {
        checkPreconditions(dpo);
        return hwOltChildHelperService.createAndSendCpsTx(dpo, CPSTransaction.SERVICE_ORDER_TYPE_MODIFY_DEVICE,
                sessionId).getId();
    }

    @Override
    public Long createAndSendDeleteDeviceCpsTransaction(@Nonnull HWDpo dpo, Long sessionId) {
        try {
            return fttxHardwareService.checkHwOltChildForActiveAuftraegeAndDelete(dpo, false, sessionId);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private void checkPreconditions(@Nonnull final HWDpo dpo) {
        if (hwOltChildHelperService.isOltChildDeleted(dpo)) {
            throw new RuntimeException("Aktion kann nicht ausgeführt werden, da DPO bereits gelöscht ist.");
        }
        if (!fttxHardwareService.checkIfDpoFieldsComplete(dpo)) {
            throw new RuntimeException("Aktion kann nicht ausgeführt werden, da Pflichtfelder der DPO leer sind.");
        }
    }
}
