/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.13
 */
package de.mnet.wbci.acceptance.receiving.behavior;

import de.mnet.wbci.acceptance.AbstractTestBehavior;
import de.mnet.wbci.citrus.VariableNames;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.MeldungTyp;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequestStatus;

/**
 * Performs basic test steps for receiving of an ERLM for a TV, when M-Net is the receiving carrier.
 * <p/>
 * <pre>
 *      AtlasESB                        Hurrican (receiving carrier)
 *      ERLM (for TV)          ->
 * </pre>
 *
 *
 */
public class ReceiveERLM_TV_TestBehavior extends AbstractTestBehavior {

    private boolean expectedKlaerfall = false;

    public ReceiveERLM_TV_TestBehavior withExpectedKlaerfall(boolean expectedKlaerfall) {
        this.expectedKlaerfall = expectedKlaerfall;
        return this;
    }

    @Override
    public void apply() {
        atlas().sendCarrierChangeUpdate("ERLM");

        hurrican().assertIoArchiveEntryCreated(IOType.IN, getGeschaeftsfallTyp(), MeldungTyp.ERLM);
        hurrican().assertKlaerfallStatus(expectedKlaerfall, null);
        hurrican().assertTvMeldungsCodes(MeldungsCode.TV_OK);
        hurrican().assertTvRequestStatus(WbciRequestStatus.TV_ERLM_EMPFANGEN);
        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);
        hurrican().assertWechseltermin(VariableNames.RESCHEDULED_CUSTOMER_DATE);
        hurrican().assertTvAnswerDeadlineIsNotSet();
        hurrican().assertVaAnswerDeadlineIsSet();
    }

}
