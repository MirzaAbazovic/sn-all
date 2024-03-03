/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.08.2015
 */
package de.mnet.wita.citrus.actions;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;

import de.mnet.wita.AbbmPvMeldungsCode;
import de.mnet.wita.acceptance.common.AcceptanceTestWorkflow;

public class WaitForAbbmPvTestAction extends AbstractWitaWorkflowTestAction {

    private final AbbmPvMeldungsCode abbmPvMeldungsCode;

    public WaitForAbbmPvTestAction(AbbmPvMeldungsCode abbmPvMeldungsCode) {
        super("waitFor non-closing ABBM");
        this.abbmPvMeldungsCode = abbmPvMeldungsCode;
    }

    @Override
    public void doExecute(AcceptanceTestWorkflow workflow, TestContext testContext) {
        try {
            workflow.waitForAbbmPv(abbmPvMeldungsCode);
        }
        catch (Exception e) {
            throw new CitrusRuntimeException("Failed to wait for non-closing 'ABBM'", e);
        }
    }
}
