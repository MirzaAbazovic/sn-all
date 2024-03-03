/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.13
 */
package de.mnet.wbci.acceptance.receiving.behavior;

import java.time.*;

import de.mnet.wbci.acceptance.AbstractTestBehavior;
import de.mnet.wbci.citrus.VariableNames;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.MeldungTyp;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequestStatus;

/**
 * Performs basic test steps for receiving of a RUEMVA, when M-Net is the receiving carrier.
 * <p/>
 * <pre>
 *      AtlasESB                        Hurrican (receiving carrier)
 *      RUEM_VA             ->
 * </pre>
 *
 *
 */
public class ReceiveRUEMVA_TestBehavior extends AbstractTestBehavior {

    private final MeldungsCode[] meldungsCodes;
    private final LocalDateTime bestaetigterWechseltermin;
    private boolean expectedKlaerfall = false;
    private String klaerfallGrund = null;

    public ReceiveRUEMVA_TestBehavior() {
        this(MeldungsCode.ZWA);
    }

    public ReceiveRUEMVA_TestBehavior(MeldungsCode... expectedMeldungsCodes) {
        this(null, expectedMeldungsCodes);
    }

    public ReceiveRUEMVA_TestBehavior(LocalDateTime bestaetigterWechseltermin, MeldungsCode... expectedMeldungsCodes) {
        this.meldungsCodes = expectedMeldungsCodes;
        this.bestaetigterWechseltermin = bestaetigterWechseltermin;
    }

    public ReceiveRUEMVA_TestBehavior isKlaerfall(boolean expectedKlaerfall) {
        this.expectedKlaerfall = expectedKlaerfall;
        return this;
    }

    public ReceiveRUEMVA_TestBehavior withKlaerfallGrund(String klaerfallGrund) {
        this.klaerfallGrund = klaerfallGrund;
        return this;
    }

    @Override
    public void apply() {
        atlas().sendCarrierChangeUpdate("RUEMVA");
        hurrican().assertIoArchiveEntryCreated(IOType.IN, getGeschaeftsfallTyp(), MeldungTyp.RUEM_VA);
        hurrican().assertKlaerfallStatus(expectedKlaerfall, klaerfallGrund);
        hurrican().assertWbciBaseRequestMetaDataSet(IOType.OUT, WbciGeschaeftsfallStatus.ACTIVE);
        hurrican().assertVaMeldungsCodes(meldungsCodes);
        hurrican().assertVaRequestStatus(WbciRequestStatus.RUEM_VA_EMPFANGEN);
        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);

        // if no date is sepcified use the variable
        if (bestaetigterWechseltermin == null) {
            hurrican().assertWechseltermin(VariableNames.REQUESTED_CUSTOMER_DATE);
        }
        else {
            hurrican().assertWechseltermin(bestaetigterWechseltermin);
        }
        hurrican().assertVaAnswerDeadlineIsSet();
    }

}
