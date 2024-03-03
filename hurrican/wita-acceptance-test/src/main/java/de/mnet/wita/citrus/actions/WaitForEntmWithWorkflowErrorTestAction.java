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

public class WaitForEntmWithWorkflowErrorTestAction extends AbstractWitaWorkflowTestAction {

    public WaitForEntmWithWorkflowErrorTestAction() {
        super("waitFor ENTM with workflow error");
    }

    @Override
    public void doExecute(AcceptanceTestWorkflow workflow, TestContext testContext) {
        try {
            workflow.waitForENTMInWorkflowError();
        }
        catch (Exception e) {
            throw new CitrusRuntimeException("Failed to wait for 'ENTM' with workflow error!", e);
        }
    }
}
