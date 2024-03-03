/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.13
 */
package de.mnet.wbci.acceptance.common.behavior;

import com.consol.citrus.exceptions.CitrusRuntimeException;

import de.mnet.wbci.acceptance.AbstractTestBehavior;
import de.mnet.wbci.acceptance.common.builder.WbciStornoAenderungAbgAnfrageKftBuilder;
import de.mnet.wbci.acceptance.common.builder.WbciStornoAenderungAufAnfrageKftBuilder;
import de.mnet.wbci.acceptance.common.builder.WbciStornoAufhebungAbgAnfrageKftBuilder;
import de.mnet.wbci.acceptance.common.builder.WbciStornoAufhebungAufAnfrageKftBuilder;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.StornoAnfrage;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequestStatus;

/**
 * Performs basic test steps for sending a StornoAufhebung from the donating carrier (M-Net).
 * <p/>
 * <pre>
 *      AtlasESB                        Hurrican (donating carrier)
 *                          <-          STRAUF
 * </pre>
 */
public class SendSTR_TestBehavior extends AbstractTestBehavior {

    private final RequestTyp stornoType;
    private final boolean validStornoRequest;
    private String stornoId;
    private String payloadTemplate = "STR";

    public SendSTR_TestBehavior(RequestTyp stornoType) {
        this(stornoType, true);
    }

    public SendSTR_TestBehavior(RequestTyp stornoType, boolean validStornoRequest) {
        if (!stornoType.isStorno()) {
            throw new CitrusRuntimeException("Invalid behavior usage - request type must be storno but was: " + stornoType);
        }

        this.stornoType = stornoType;
        this.validStornoRequest = validStornoRequest;
    }

    public SendSTR_TestBehavior withPayloadTemplate(String payloadTemplate) {
        this.payloadTemplate = payloadTemplate;
        return this;
    }

    @Override
    public void apply() {
        StornoAnfrage stornoAnfrage = null;
        switch (stornoType) {
            case STR_AEN_AUF:
                stornoAnfrage = new WbciStornoAenderungAufAnfrageKftBuilder<>(getCdmVersion())
                        .withAenderungsId(stornoId)
                        .build();
                break;

            case STR_AEN_ABG:
                stornoAnfrage = new WbciStornoAenderungAbgAnfrageKftBuilder<>(getCdmVersion())
                        .withAenderungsId(stornoId)
                        .build();
                break;

            case STR_AUFH_AUF:
                stornoAnfrage = new WbciStornoAufhebungAufAnfrageKftBuilder<>(getCdmVersion())
                        .withAenderungsId(stornoId)
                        .build();
                break;

            case STR_AUFH_ABG:
                stornoAnfrage = new WbciStornoAufhebungAbgAnfrageKftBuilder<>(getCdmVersion())
                        .withAenderungsId(stornoId)
                        .build();
                break;
        }

        if (stornoAnfrage == null) {
            throw new CitrusRuntimeException("Unable to create storno anfrage for type: " + stornoType);
        }

        hurrican().createWbciStorno(stornoAnfrage);

        atlas().receiveCarrierChangeCancel(payloadTemplate);

        hurrican().assertIoArchiveEntryCreated(IOType.OUT, getGeschaeftsfallTyp(), stornoType);
        hurrican().assertKlaerfallStatus(false, null);
        hurrican().assertStornoRequestStatus(WbciRequestStatus.STORNO_VERSENDET);
        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);
        if (validStornoRequest) {
            hurrican().assertStornoAnswerDeadlineIsSet(stornoType);
        }
        else {
            hurrican().assertStornoAnswerDeadlineIsNotSet(stornoType);
        }
    }

    public AbstractTestBehavior withStornoId(String stornoId) {
        this.stornoId = stornoId;
        return this;
    }

}
