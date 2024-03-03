/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.13
 */
package de.mnet.wbci.acceptance.donating.behavior;

import de.mnet.wbci.acceptance.AbstractTestBehavior;
import de.mnet.wbci.acceptance.common.builder.ErledigtmeldungKftBuilder;
import de.mnet.wbci.citrus.VariableNames;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.MeldungTyp;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequestStatus;

/**
 * Performs basic test steps for sending an ERLM for a TV, when M-Net is the donating carrier.
 * <p/>
 * <pre>
 *      AtlasESB                        Hurrican (donating carrier)
 *                         <--          ERLM (for TV)
 * </pre>
 *
 *
 */
public class SendERLM_TV_TestBehavior extends AbstractTestBehavior {

    private WbciGeschaeftsfallStatus gfStatus = WbciGeschaeftsfallStatus.ACTIVE;

    @Override
    public void apply() {
        hurrican().createWbciMeldung(
                new ErledigtmeldungKftBuilder(IOType.OUT)
                        .withMeldungsCodes(MeldungsCode.TV_OK)
                        .buildForTv(), RequestTyp.TV
        );
        hurrican().assertIoArchiveEntryCreated(IOType.OUT, getGeschaeftsfallTyp(), MeldungTyp.ERLM);
        hurrican().assertKlaerfallStatus(false, null);
        hurrican().assertTvMeldungsCodes(MeldungsCode.TV_OK);
        hurrican().assertTvRequestStatus(WbciRequestStatus.TV_ERLM_VERSENDET);
        hurrican().assertGfStatus(gfStatus);
        hurrican().assertWechseltermin(VariableNames.RESCHEDULED_CUSTOMER_DATE);
        hurrican().assertVorabstimmungAbgebendSet(true, "positive WBCI-Terminverschiebung - TV ERLM versendet.*");
        hurrican().assertTvAnswerDeadlineIsNotSet();
        hurrican().assertVaAnswerDeadlineIsSet();
        atlas().receiveCarrierChangeUpdate("ERLM");
    }

    public SendERLM_TV_TestBehavior withExpectedGfStatus(WbciGeschaeftsfallStatus gfStatus) {
        this.gfStatus = gfStatus;
        return this;
    }

}
