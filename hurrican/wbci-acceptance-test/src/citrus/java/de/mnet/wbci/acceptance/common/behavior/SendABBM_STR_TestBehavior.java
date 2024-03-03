/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.10.13
 */
package de.mnet.wbci.acceptance.common.behavior;

import de.mnet.wbci.acceptance.AbstractTestBehavior;
import de.mnet.wbci.acceptance.common.builder.AbbruchmeldungKftBuilder;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.MeldungTyp;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequestStatus;

/**
 * Performs basic test steps for sending of an ABBM for a STORNO. Applies when M-Net is the receiving or donating
 * carrier.
 * <pre>
 *      AtlasESB                        Hurrican (donating / receiving carrier)
 *                         <-           ABBM-STR
 * </pre>
 *
 *
 */
public class SendABBM_STR_TestBehavior extends AbstractTestBehavior {

    private final RequestTyp stornoType;

    private final MeldungsCode[] meldungsCodes;

    public SendABBM_STR_TestBehavior(RequestTyp stornoType, MeldungsCode... codes) {
        this.stornoType = stornoType;
        this.meldungsCodes = codes;
    }

    @Override
    public void apply() {
        hurrican().createWbciMeldung(new AbbruchmeldungKftBuilder(getCdmVersion(), getGeschaeftsfallTyp(), IOType.OUT)
                .withoutWechseltermin()
                .withMeldungsCodes(meldungsCodes)
                .withBegruendung("Storno")
                .buildForStorno(stornoType), stornoType);
        hurrican().assertIoArchiveEntryCreated(IOType.OUT, getGeschaeftsfallTyp(), MeldungTyp.ABBM);
        hurrican().assertKlaerfallStatus(false, null);
        hurrican().assertStornoMeldungsCodes(meldungsCodes);
        hurrican().assertStornoRequestStatus(WbciRequestStatus.STORNO_ABBM_VERSENDET);
        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);

        atlas().receiveCarrierChangeUpdate("ABBM");
    }
}
