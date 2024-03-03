/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.13
 */
package de.mnet.wbci.acceptance.common.behavior;

import com.consol.citrus.exceptions.CitrusRuntimeException;

import de.mnet.wbci.acceptance.AbstractTestBehavior;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.MeldungTyp;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequestStatus;

/**
 * Performs basic test steps for receiving a Storno ERLM.
 * <p/>
 * <pre>
 *      AtlasESB                        Hurrican
 *      ERLM             ->
 * </pre>
 *
 *
 */
public class ReceiveERLM_STR_TestBehavior extends AbstractTestBehavior {

    private final RequestTyp stornoType;

    public ReceiveERLM_STR_TestBehavior(RequestTyp stornoType) {
        if (!stornoType.isStorno()) {
            throw new CitrusRuntimeException("Invalid behavior usage - request type must be storno but was: " + stornoType);
        }

        this.stornoType = stornoType;
    }

    @Override
    public void apply() {
        atlas().sendCarrierChangeUpdate("ERLM");

        hurrican().assertIoArchiveEntryCreated(IOType.IN, getGeschaeftsfallTyp(), MeldungTyp.ERLM);
        hurrican().assertKlaerfallStatus(false, null);
        hurrican().assertStornoMeldungsCodes(MeldungsCode.STORNO_OK);
        hurrican().assertStornoRequestStatus(WbciRequestStatus.STORNO_ERLM_EMPFANGEN);
        if (stornoType == RequestTyp.STR_AUFH_ABG || stornoType == RequestTyp.STR_AUFH_AUF) {
            hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);
        }
        else {
            hurrican().assertGfStatus(WbciGeschaeftsfallStatus.NEW_VA);
        }

        hurrican().assertStornoAnswerDeadlineIsSet(stornoType);
        hurrican().assertVorabstimmungAbgebendSet(false, "stornierte WBCI-Vorabstimmung - Storno ERLM empfangen.*");
    }

}
