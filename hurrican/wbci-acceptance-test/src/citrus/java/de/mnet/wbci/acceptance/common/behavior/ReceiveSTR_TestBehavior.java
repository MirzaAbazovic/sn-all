/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.13
 */
package de.mnet.wbci.acceptance.common.behavior;

import com.consol.citrus.exceptions.CitrusRuntimeException;
import org.springframework.util.StringUtils;

import de.mnet.wbci.acceptance.AbstractTestBehavior;
import de.mnet.wbci.citrus.VariableNames;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequestStatus;

/**
 * Performs basic test steps for sending a StornoAufhebung to the donating carrier (M-Net).
 * <p/>
 * <pre>
 *      AtlasESB                                  Hurrican (donating carrier)
 *      STR with stornoType STR_XXX_XXX    ->
 * </pre>
 */
public class ReceiveSTR_TestBehavior extends AbstractTestBehavior {

    private final RequestTyp stornoType;
    private String stornoId;
    private WbciRequestStatus expectedRequestStatus = WbciRequestStatus.STORNO_EMPFANGEN;
    private boolean expectedKlaerfall = false;
    private boolean skipStornoRequestChecks = false;
    private boolean skipGeschaeftsfallChecks = false;
    private String payloadTemplate = "STR";

    public ReceiveSTR_TestBehavior(RequestTyp stornoType) {
        if (!stornoType.isStorno()) {
            throw new CitrusRuntimeException("Invalid behavior usage - request type must be storno but was: " + stornoType);
        }
        this.stornoType = stornoType;
    }

    public ReceiveSTR_TestBehavior withExpectedKlaerfall(boolean expectedKlaerfall) {
        this.expectedKlaerfall = expectedKlaerfall;
        return this;
    }

    public ReceiveSTR_TestBehavior withExplicitGeschaeftsfallTyp(GeschaeftsfallTyp explicitGeschaeftsfallTyp) {
        setExplicitGeschaeftsfallTyp(explicitGeschaeftsfallTyp);
        return this;
    }

    /**
     * The Storno request checks (deadline, request status, kundenwunschtermin) should be skipped. This is typically
     * required when an invalid TV request is sent to hurrican, which does not get persisted in the hurrican database.
     *
     * @return
     */
    public ReceiveSTR_TestBehavior withSkipStornoRequestChecks() {
        this.skipStornoRequestChecks = true;
        return this;
    }

    /**
     * The geschaeftsfall checks (gf status, klaerfall) should be skipped. This is typically required when a storno
     * request is sent to hurrican containing an invalid VaRefId.
     *
     * @return
     */
    public ReceiveSTR_TestBehavior withSkipGeschaeftsfallChecks() {
        this.skipGeschaeftsfallChecks = true;
        return this;
    }


    public ReceiveSTR_TestBehavior withStornoId(String stornoId) {
        this.stornoId = stornoId;
        return this;
    }

    public ReceiveSTR_TestBehavior withPayloadTemplate(String payloadTemplate) {
        this.payloadTemplate = payloadTemplate;
        return this;
    }

    public String getStornoId() {
        return stornoId;
    }

    @Override
    public void apply() {
        // only generate stornoId when not explicitly set before
        if (!StringUtils.hasText(getStornoId())) {
            variable(VariableNames.STORNO_ID, "wbci:createVorabstimmungsId('DEU.DTAG', 'S')");
        }
        else {
            variable(VariableNames.STORNO_ID, getStornoId());
        }

        atlas().sendCarrierChangeCancel(payloadTemplate);
        hurrican().assertIoArchiveEntryCreated(IOType.IN, getGeschaeftsfallTyp(), stornoType);

        if (!skipGeschaeftsfallChecks) {
            hurrican().assertKlaerfallStatus(expectedKlaerfall, null);
            hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);
        }

        // storno request checks
        if (!skipStornoRequestChecks) {
            hurrican().assertStornoRequestStatus(expectedRequestStatus);
            hurrican().assertStornoAnswerDeadlineIsSet(stornoType);
        }
    }

}
