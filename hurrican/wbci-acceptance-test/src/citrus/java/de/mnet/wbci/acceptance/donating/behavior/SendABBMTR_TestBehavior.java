/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.10.13
 */
package de.mnet.wbci.acceptance.donating.behavior;

import java.util.*;

import de.mnet.wbci.acceptance.AbstractTestBehavior;
import de.mnet.wbci.acceptance.common.builder.AbbruchmeldungTechnRessourceKftBuilder;
import de.mnet.wbci.citrus.actions.CreateWbciMeldungTestAction;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.Meldung;
import de.mnet.wbci.model.MeldungPositionAbbruchmeldungTechnRessource;
import de.mnet.wbci.model.MeldungTyp;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wbci.model.builder.MeldungPositionAbbruchmeldungTechnRessourceBuilder;

/**
 * Performs basic test steps for sending of an ABBM-TR, when M-Net is the donating carrier.
 * <p/>
 * <pre>
 *      AtlasESB                        Hurrican (donating carrier)
 *                        <-            ABBM-TR
 * </pre>
 *
 *
 */
public class SendABBMTR_TestBehavior extends AbstractTestBehavior {

    private MeldungsCode[] meldungsCodes;

    public SendABBMTR_TestBehavior(MeldungsCode... codes) {
        this.meldungsCodes = codes;
    }

    @Override
    public void apply() {
        final CreateWbciMeldungTestAction wbciMeldung = hurrican().createWbciMeldung(buildAbbruchmeldung());
        if (isExceptionExpected()) {
            assertException(wbciMeldung)
                    .exception(expectedExceptionClass)
                    .message(expectedExceptionMessage);

        }
        else {
            hurrican().assertIoArchiveEntryCreated(IOType.OUT, getGeschaeftsfallTyp(), MeldungTyp.ABBM_TR);
            hurrican().assertKlaerfallStatus(false, null);
            hurrican().assertVaMeldungsCodes(meldungsCodes);
            hurrican().assertVaRequestStatus(WbciRequestStatus.ABBM_TR_VERSENDET);
            hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);

            atlas().receiveCarrierChangeUpdate("ABBMTR");
        }
    }

    private Meldung<MeldungPositionAbbruchmeldungTechnRessource> buildAbbruchmeldung() {
        AbbruchmeldungTechnRessourceKftBuilder abbruchmeldungKftBuilder = new AbbruchmeldungTechnRessourceKftBuilder(
                getCdmVersion(), getGeschaeftsfallTyp(), IOType.OUT);

        if (meldungsCodes.length > 0) {
            abbruchmeldungKftBuilder.withMeldungsPositionen(new HashSet<MeldungPositionAbbruchmeldungTechnRessource>());

            for (MeldungsCode meldungsCode : meldungsCodes) {
                abbruchmeldungKftBuilder.addMeldungPosition(new MeldungPositionAbbruchmeldungTechnRessourceBuilder()
                        .withMeldungsCode(meldungsCode)
                        .withMeldungsText(meldungsCode.getStandardText())
                        .build());
            }
        }

        return abbruchmeldungKftBuilder.build();
    }

    @Override
    public SendABBMTR_TestBehavior withExpectedException(Class expectedExceptionClass, String expectedExceptionMessage) {
        return (SendABBMTR_TestBehavior) super.withExpectedException(expectedExceptionClass, expectedExceptionMessage);
    }

}
