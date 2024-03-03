/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.10.13
 */
package de.mnet.wbci.acceptance.donating.behavior;

import java.util.*;

import de.mnet.wbci.acceptance.AbstractTestBehavior;
import de.mnet.wbci.acceptance.common.builder.AbbruchmeldungKftBuilder;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.Meldung;
import de.mnet.wbci.model.MeldungPositionAbbruchmeldung;
import de.mnet.wbci.model.MeldungTyp;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wbci.model.builder.MeldungPositionAbbruchmeldungBuilder;
import de.mnet.wbci.model.builder.MeldungPositionAbbruchmeldungTestBuilder;

/**
 * Performs basic test steps for sending of an ABBM, when M-Net is the donating carrier.
 * <pre>
 *      AtlasESB                        Hurrican (donating carrier)
 *                        <-            ABBM
 * </pre>
 *
 *
 */
public class SendABBM_TestBehavior extends AbstractTestBehavior {

    private MeldungsCode[] meldungsCodes;

    public SendABBM_TestBehavior(MeldungsCode... codes) {
        this.meldungsCodes = codes;
    }

    @Override
    public void apply() {
        hurrican().createWbciMeldung(buildAbbruchmeldung());

        hurrican().assertIoArchiveEntryCreated(IOType.OUT, getGeschaeftsfallTyp(), MeldungTyp.ABBM);
        hurrican().assertKlaerfallStatus(false, null);
        hurrican().assertVaMeldungsCodes(meldungsCodes);
        hurrican().assertVaRequestStatus(WbciRequestStatus.ABBM_VERSENDET);
        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.COMPLETE);
        hurrican().assertVorabstimmungAbgebendSet(false, "negative WBCI-Vorabstimmung - ABBM versendet.*");
        atlas().receiveCarrierChangeUpdate("ABBM");
    }

    private Meldung<MeldungPositionAbbruchmeldung> buildAbbruchmeldung() {
        AbbruchmeldungKftBuilder abbruchmeldungKftBuilder = new AbbruchmeldungKftBuilder(getCdmVersion(), getGeschaeftsfallTyp(), IOType.OUT);

        if (meldungsCodes.length > 0) {
            abbruchmeldungKftBuilder.withMeldungsPositionen(new HashSet<MeldungPositionAbbruchmeldung>());

            for (MeldungsCode meldungsCode : meldungsCodes) {
                if (meldungsCode.equals(MeldungsCode.RNG)) {
                    abbruchmeldungKftBuilder.addMeldungPosition(new MeldungPositionAbbruchmeldungTestBuilder().buildValid(getCdmVersion(), getGeschaeftsfallTyp()));
                }
                else {
                    abbruchmeldungKftBuilder.addMeldungPosition(new MeldungPositionAbbruchmeldungBuilder()
                            .withMeldungsCode(meldungsCode)
                            .withMeldungsText(meldungsCode.getStandardText())
                            .build());
                }
            }
        }

        return abbruchmeldungKftBuilder.build();
    }
}
