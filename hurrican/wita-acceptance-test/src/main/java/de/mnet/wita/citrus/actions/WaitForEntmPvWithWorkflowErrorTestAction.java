/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.08.2015
 */
package de.mnet.wita.citrus.actions;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;

import de.mnet.wita.acceptance.common.AcceptanceTestWorkflow;

public class WaitForEntmPvWithWorkflowErrorTestAction extends AbstractWitaWorkflowTestAction {

    public WaitForEntmPvWithWorkflowErrorTestAction() {
        super("waitFor ENTM PV with workflow error");
    }

    @Override
    public void doExecute(AcceptanceTestWorkflow workflow, TestContext testContext) {
        try {
            workflow.waitForEntmPvInWorkflowError();
        }
        catch (Exception e) {
            throw new CitrusRuntimeException("Failed to wait for 'ERLM' with workflow error!", e);
        }
    }
}
