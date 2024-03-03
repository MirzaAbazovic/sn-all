/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.13
 */
package de.mnet.wbci.acceptance.receiving.behavior;

import de.mnet.wbci.acceptance.AbstractTestBehavior;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.MeldungTyp;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequestStatus;

/**
 * Performs basic test steps for receiving of an ABBM for a VA, when M-Net is the receiving carrier.
 * <p/>
 * <pre>
 *      AtlasESB                        Hurrican (receiving carrier)
 *      ABBM               ->
 * </pre>
 *
 *
 */
public class ReceiveABBM_VA_TestBehavior extends AbstractTestBehavior {

    private final MeldungsCode[] meldungsCodes;
    private boolean expectedKlaerfall = false;

    public ReceiveABBM_VA_TestBehavior(MeldungsCode... expectedMeldungsCodes) {
        this.meldungsCodes = expectedMeldungsCodes;
    }

    public ReceiveABBM_VA_TestBehavior withExpectedKlaerfall(boolean expectedKlaerfall) {
        this.expectedKlaerfall = expectedKlaerfall;
        return this;
    }

    @Override
    public void apply() {
        atlas().sendCarrierChangeUpdate("ABBM");

        hurrican().assertIoArchiveEntryCreated(IOType.IN, getGeschaeftsfallTyp(), MeldungTyp.ABBM);
        hurrican().assertKlaerfallStatus(expectedKlaerfall, null);
        hurrican().assertVaMeldungsCodes(meldungsCodes);
        hurrican().assertVaRequestStatus(WbciRequestStatus.ABBM_EMPFANGEN);
        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);
    }

}
