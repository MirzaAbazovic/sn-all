/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 */
package de.mnet.wita.citrus.actions;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;

import de.mnet.wita.acceptance.common.AcceptanceTestWorkflow;

public class WaitForVzmPvTestAction extends AbstractWitaWorkflowTestAction {

    public WaitForVzmPvTestAction() {
        super("waitFor VZM-PV");
    }

    @Override
    public void doExecute(AcceptanceTestWorkflow workflow, TestContext testContext) {
        try {
            workflow.waitForVZMPV();
        }
        catch (Exception e) {
            throw new CitrusRuntimeException("Failed to wait for 'VZM-PV'", e);
        }
    }
}
