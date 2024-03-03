/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.13
 */
package de.mnet.wbci.acceptance.donating.behavior;

import de.mnet.wbci.acceptance.AbstractTestBehavior;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.MeldungTyp;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequestStatus;

/**
 * Performs basic test steps for sending of an AKMTR, when M-Net is the receiving carrier.
 * <p/>
 * <pre>
 *      AtlasESB                        Hurrican (donating carrier)
 *      AKMTR              ->
 * </pre>
 *
 *
 */
public class ReceiveAKMTR_TestBehavior extends AbstractTestBehavior {

    private final int expCountIoArchiveEntries;
    private boolean expectedKlaerfall = false;

    public ReceiveAKMTR_TestBehavior() {
        this(1);
    }

    public ReceiveAKMTR_TestBehavior(int expCountIoArchiveEntries) {
        this.expCountIoArchiveEntries = expCountIoArchiveEntries;
    }

    public ReceiveAKMTR_TestBehavior withExpectedKlaerfall(boolean expectedKlaerfall) {
        this.expectedKlaerfall = expectedKlaerfall;
        return this;
    }

    @Override
    public void apply() {
        atlas().sendCarrierChangeUpdate("AKMTR");

        hurrican().assertIoArchiveEntryCreated(IOType.IN, getGeschaeftsfallTyp(), MeldungTyp.AKM_TR, expCountIoArchiveEntries, false);
        hurrican().assertKlaerfallStatus(expectedKlaerfall, null);
        hurrican().assertVaMeldungsCodes(MeldungsCode.AKMTR_CODE);
        hurrican().assertVaRequestStatus(WbciRequestStatus.AKM_TR_EMPFANGEN);
        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);
        hurrican().assertVorabstimmungAbgebendSet(true, "positive WBCI-Vorabstimmung - RUEM-VA versendet.*");
    }

}
