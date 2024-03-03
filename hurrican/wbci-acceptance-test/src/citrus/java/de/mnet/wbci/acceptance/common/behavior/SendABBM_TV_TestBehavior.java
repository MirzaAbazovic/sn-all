/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.10.13
 */
package de.mnet.wbci.acceptance.common.behavior;

import de.mnet.wbci.acceptance.AbstractTestBehavior;
import de.mnet.wbci.acceptance.common.builder.AbbruchmeldungKftBuilder;
import de.mnet.wbci.citrus.VariableNames;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.MeldungTyp;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wbci.model.builder.AbbruchmeldungBuilder;

/**
 * Performs basic test steps for receiving of an ABBM for a VA, when M-Net is the receiving carrier.
 * <p/>
 * <pre>
 *      AtlasESB                        Hurrican (receiving carrier)
 *                         <-           ABBM-TV
 * </pre>
 *
 *
 */
public class SendABBM_TV_TestBehavior extends AbstractTestBehavior {

    private AbbruchmeldungBuilder abbruchmeldungBuilder;
    private MeldungsCode[] meldungsCodes;


    public SendABBM_TV_TestBehavior withBuilder(AbbruchmeldungBuilder abbruchmeldungBuilder) {
        this.abbruchmeldungBuilder = abbruchmeldungBuilder;
        return this;
    }

    public SendABBM_TV_TestBehavior withExpectedMeldungsCodes(MeldungsCode... expectedMeldungsCodes) {
        this.meldungsCodes = expectedMeldungsCodes;
        return this;
    }

    @Override
    public void apply() {
        if (abbruchmeldungBuilder == null) {
            new AbbruchmeldungKftBuilder(getCdmVersion(), getGeschaeftsfallTyp(), IOType.OUT);
        }
        hurrican().createWbciMeldung(abbruchmeldungBuilder.buildForTv(), RequestTyp.TV);

        hurrican().assertIoArchiveEntryCreated(IOType.OUT, getGeschaeftsfallTyp(), MeldungTyp.ABBM);
        hurrican().assertKlaerfallStatus(false, null);
        hurrican().assertTvMeldungsCodes(meldungsCodes);
        hurrican().assertTvRequestStatus(WbciRequestStatus.TV_ABBM_VERSENDET);
        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);

        hurrican().assertWechseltermin(VariableNames.REQUESTED_CUSTOMER_DATE);

        atlas().receiveCarrierChangeUpdate("ABBM");
    }

}
