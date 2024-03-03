/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.08.2015
 */
package de.mnet.wita.citrus.actions;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;

import de.mnet.wita.acceptance.common.AcceptanceTestDataBuilder;
import de.mnet.wita.acceptance.common.AcceptanceTestWorkflow;
import de.mnet.wita.citrus.VariableNames;

/**
 *
 */
public class SendWitaBereitstellungTestAction extends AbstractWitaWorkflowTestAction {

    private final AcceptanceTestDataBuilder acceptanceTestDataBuilder;

    public SendWitaBereitstellungTestAction(AcceptanceTestDataBuilder acceptanceTestDataBuilder) {
        super("sendBereitstellung");
        this.acceptanceTestDataBuilder = acceptanceTestDataBuilder;
    }

    @Override
    public void doExecute(AcceptanceTestWorkflow workflow, TestContext testContext) {
        try {
            workflow.sendBereitstellung(acceptanceTestDataBuilder);
            testContext.getVariables().put(VariableNames.TEST_DATA, workflow.getCreatedData());
        }
        catch (Exception e) {
            throw new CitrusRuntimeException("Failed to send Wita Bereitstellung", e);
        }
    }

    public AcceptanceTestDataBuilder testDataBuilder() {
        return acceptanceTestDataBuilder;
    }

}
