/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.09.2014
 */
package de.augustakom.hurrican.service.cc;

import javax.annotation.*;

import de.augustakom.hurrican.model.cc.hardware.HWOltChild;

public interface HwOltChildAdminService<T extends HWOltChild> extends ICCService {

    /**
     * Erzeugt und sendet eine createDevice CPS-Transaktion für das gegebene OLT-Child.
     *
     * @return ID der erzeugten und gesendeten CPS-Transaktion
     */
    public Long createAndSendCreateDeviceCpsTransaction(@Nonnull T hwOltChild, Long sessionId);

    /**
     * Erzeugt und sendet eine modifyDevice CPS-Transaktion für die gegebene OLT-Child.
     *
     * @return ID der erzeugten und gesendeten CPS-Transaktion
     */
    public Long createAndSendModifyDeviceCpsTransaction(@Nonnull T hwOltChild, Long sessionId);

    /**
     * Erzeugt und sendet eine deleteDevice CPS-Transaktion für die gegebene OLT-Child.
     *
     * @return ID der erzeugten und gesendeten CPS-Transaktion
     */
    public Long createAndSendDeleteDeviceCpsTransaction(@Nonnull T hwOltChild, Long sessionId);
}
