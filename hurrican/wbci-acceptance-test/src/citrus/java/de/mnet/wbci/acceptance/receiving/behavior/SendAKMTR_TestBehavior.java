/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.13
 */
package de.mnet.wbci.acceptance.receiving.behavior;

import de.mnet.wbci.acceptance.AbstractTestBehavior;
import de.mnet.wbci.acceptance.common.builder.UebernahmeRessourceMeldungKftBuilder;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.MeldungTyp;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wbci.model.builder.UebernahmeRessourceMeldungBuilder;

/**
 * Performs basic test steps for sending of an AKMTR, when M-Net is the receiving carrier.
 * <p/>
 * <pre>
 *      AtlasESB                        Hurrican (receiving carrier)
 *                         <-           AKMTR
 * </pre>
 *
 *
 */
public class SendAKMTR_TestBehavior extends AbstractTestBehavior {

    private UebernahmeRessourceMeldungBuilder builder;
    private final int expCountIoArchiveEntries;

    public SendAKMTR_TestBehavior() {
        this(null, 1);
    }

    public SendAKMTR_TestBehavior(UebernahmeRessourceMeldungBuilder builder) {
        this(builder, 1);
    }

    public SendAKMTR_TestBehavior(int expCountIoArchiveEntries) {
        this(null, expCountIoArchiveEntries);
    }

    public SendAKMTR_TestBehavior(UebernahmeRessourceMeldungBuilder builder, int expCountIoArchiveEntries) {
        this.builder = builder;
        this.expCountIoArchiveEntries = expCountIoArchiveEntries;
    }

    @Override
    public void apply() {
        if (builder == null) {
            this.builder = new UebernahmeRessourceMeldungKftBuilder(getCdmVersion(), getGeschaeftsfallTyp(), IOType.OUT);
        }
        hurrican().createWbciMeldung(builder.build());
        hurrican().assertIoArchiveEntryCreated(IOType.OUT, getGeschaeftsfallTyp(), MeldungTyp.AKM_TR, expCountIoArchiveEntries, false);

        hurrican().assertKlaerfallStatus(false, null);

        atlas().receiveCarrierChangeUpdate("AKMTR");

        hurrican().assertVaRequestStatus(WbciRequestStatus.AKM_TR_VERSENDET);
        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);
    }

}
