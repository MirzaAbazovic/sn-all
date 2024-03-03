/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.08.2015
 */
package de.mnet.wita.citrus.actions;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;

import de.mnet.wita.AbbmMeldungsCode;
import de.mnet.wita.acceptance.common.AcceptanceTestWorkflow;

public class WaitForNonClosingAbbmTestAction extends AbstractWitaWorkflowTestAction {

    private final AbbmMeldungsCode abbmMeldungsCode;

    public WaitForNonClosingAbbmTestAction(AbbmMeldungsCode abbmMeldungsCode) {
        super("waitFor non-closing ABBM");
        this.abbmMeldungsCode = abbmMeldungsCode;
    }

    @Override
    public void doExecute(AcceptanceTestWorkflow workflow, TestContext testContext) {
        try {
            workflow.waitForNonClosingABBM(abbmMeldungsCode);
        }
        catch (Exception e) {
            throw new CitrusRuntimeException("Failed to wait for non-closing 'ABBM'", e);
        }
    }
}
