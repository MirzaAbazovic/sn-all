/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.13
 */
package de.mnet.wbci.acceptance.common.behavior;

import com.consol.citrus.exceptions.CitrusRuntimeException;

import de.mnet.wbci.acceptance.AbstractTestBehavior;
import de.mnet.wbci.acceptance.common.builder.ErledigtmeldungKftBuilder;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.MeldungTyp;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequestStatus;

/**
 * Performs basic test steps for sending of an ERLM for a Storno when M-Net is the receiving carrier.
 * <p/>
 * <pre>
 *      AtlasESB                        Hurrican (receiving carrier)
 *                    <--                 ERLM (for STR_XXX_XXX)
 * </pre>
 *
 *
 */
public class SendERLM_STR_TestBehavior extends AbstractTestBehavior {

    private final RequestTyp stornoType;

    public SendERLM_STR_TestBehavior(RequestTyp stornoType) {
        if (!stornoType.isStorno()) {
            throw new CitrusRuntimeException("Invalid behavior usage - request type must be storno but was: " + stornoType);
        }

        this.stornoType = stornoType;
    }

    public SendERLM_STR_TestBehavior withExplicitGeschaeftsfallTyp(GeschaeftsfallTyp explicitGeschaeftsfallTyp) {
        setExplicitGeschaeftsfallTyp(explicitGeschaeftsfallTyp);
        return this;
    }

    @Override
    public void apply() {
        hurrican().createWbciMeldung(new ErledigtmeldungKftBuilder(IOType.OUT)
                .withMeldungsCodes(MeldungsCode.STORNO_OK)
                .withoutWechseltermin()
                .buildForStorno(stornoType), stornoType);
        hurrican().assertIoArchiveEntryCreated(IOType.OUT, getGeschaeftsfallTyp(), MeldungTyp.ERLM);
        hurrican().assertKlaerfallStatus(false, null);
        hurrican().assertStornoMeldungsCodes(MeldungsCode.STORNO_OK);
        hurrican().assertStornoRequestStatus(WbciRequestStatus.STORNO_ERLM_VERSENDET);
        if (stornoType == RequestTyp.STR_AUFH_ABG || stornoType == RequestTyp.STR_AUFH_AUF) {
            hurrican().assertGfStatus(WbciGeschaeftsfallStatus.COMPLETE);
        }
        else {
            hurrican().assertGfStatus(WbciGeschaeftsfallStatus.NEW_VA);
        }

        hurrican().assertStornoAnswerDeadlineIsSet(stornoType);
        hurrican().assertVorabstimmungAbgebendSet(false, "stornierte WBCI-Vorabstimmung - Storno ERLM versendet.*");
        atlas().receiveCarrierChangeUpdate("ERLM");
    }

}
