/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.10.13
 */
package de.mnet.wbci.acceptance.common.behavior;

import de.mnet.wbci.acceptance.AbstractTestBehavior;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.MeldungTyp;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequestStatus;

/**
 * Performs basic test steps for receiving a Storno ABBM for a VA.
 * <p/>
 * <pre>
 *      AtlasESB                        Hurrican
 *      ABBM             ->
 * </pre>
 */
public class ReceiveABBM_STR_TestBehavior extends AbstractTestBehavior {

    private MeldungsCode[] meldungsCodes;

    public ReceiveABBM_STR_TestBehavior(MeldungsCode... expectedMeldungsCodes) {
        super();
        this.meldungsCodes = expectedMeldungsCodes;
    }

    @Override
    public void apply() {
        atlas().sendCarrierChangeUpdate("ABBM");
        hurrican().assertIoArchiveEntryCreated(IOType.IN, getGeschaeftsfallTyp(), MeldungTyp.ABBM);
        hurrican().assertKlaerfallStatus(false, null);
        hurrican().assertStornoMeldungsCodes(meldungsCodes);
        hurrican().assertStornoRequestStatus(WbciRequestStatus.STORNO_ABBM_EMPFANGEN);
        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);
    }

}
