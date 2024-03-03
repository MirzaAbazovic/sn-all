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
 * Performs basic test steps for receiving of an ABBMTR for an AKM-TR, when M-Net is the receiving carrier.
 * <p/>
 * <pre>
 *      AtlasESB                        Hurrican (receiving carrier)
 *      ABBMTR             ->
 * </pre>
 *
 *
 */
public class ReceiveABBMTR_TestBehavior extends AbstractTestBehavior {

    private final MeldungsCode[] meldungsCodes;
    private boolean expectedKlaerfall = true;

    public ReceiveABBMTR_TestBehavior(MeldungsCode... expectedMeldungsCodes) {
        assert expectedMeldungsCodes != null && expectedMeldungsCodes.length > 0;
        this.meldungsCodes = expectedMeldungsCodes;
    }

    public ReceiveABBMTR_TestBehavior(boolean expectedKlaerfall, MeldungsCode... expectedMeldungsCodes) {
        assert expectedMeldungsCodes != null && expectedMeldungsCodes.length > 0;
        this.expectedKlaerfall = expectedKlaerfall;
        this.meldungsCodes = expectedMeldungsCodes;
    }

    public ReceiveABBMTR_TestBehavior withExpectedKlaerfall(boolean expectedKlaerfall) {
        this.expectedKlaerfall = expectedKlaerfall;
        return this;
    }

    @Override
    public void apply() {
        atlas().sendCarrierChangeUpdate("ABBMTR");

        hurrican().assertIoArchiveEntryCreated(IOType.IN, getGeschaeftsfallTyp(), MeldungTyp.ABBM_TR);
        hurrican().assertKlaerfallStatus(expectedKlaerfall, null);
        hurrican().assertVaMeldungsCodes(meldungsCodes);
        hurrican().assertVaRequestStatus(WbciRequestStatus.ABBM_TR_EMPFANGEN);
        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);
    }

}
