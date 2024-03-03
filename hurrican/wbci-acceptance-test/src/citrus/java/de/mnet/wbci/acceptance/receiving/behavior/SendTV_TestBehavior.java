/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.13
 */
package de.mnet.wbci.acceptance.receiving.behavior;

import java.time.*;

import de.mnet.wbci.acceptance.AbstractTestBehavior;
import de.mnet.wbci.citrus.VariableNames;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequestStatus;

/**
 * Performs basic test steps for sending a TV, when M-Net is the receiving carrier.
 * <p/>
 * <pre>
 *      AtlasESB                        Hurrican (receiving carrier)
 *                          <-          TV
 * </pre>
 *
 *
 */
public class SendTV_TestBehavior extends AbstractTestBehavior {

    private final LocalDate tvTermin;
    private final String aenderungsId;

    public SendTV_TestBehavior(LocalDate tvTermin) {
        this(tvTermin, null);
    }

    public SendTV_TestBehavior(LocalDate tvTermin, String aenderungsId) {
        this.tvTermin = tvTermin;
        this.aenderungsId = aenderungsId;
    }

    @Override
    public void apply() {
        hurrican().createWbciTv(tvTermin, aenderungsId);

        atlas().receiveCarrierChangeReschedule("TV");

        hurrican().assertIoArchiveEntryCreated(IOType.OUT, getGeschaeftsfallTyp(), RequestTyp.TV);
        hurrican().assertKlaerfallStatus(false, null);
        hurrican().assertTvRequestStatus(WbciRequestStatus.TV_VERSENDET);
        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);
        hurrican().assertWechseltermin(VariableNames.REQUESTED_CUSTOMER_DATE);
        hurrican().assertKundenwunschtermin(tvTermin, RequestTyp.TV);
        hurrican().assertTvAnswerDeadlineIsSet();
    }

}
