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
import de.mnet.wita.message.auftrag.Auftragsposition;

public class SendWitaLmaeTestAction extends AbstractWitaWorkflowTestAction {

    private final AcceptanceTestDataBuilder acceptanceTestDataBuilder;

    public SendWitaLmaeTestAction(AcceptanceTestDataBuilder acceptanceTestDataBuilder) {
        super("sendLmae");
        this.acceptanceTestDataBuilder = acceptanceTestDataBuilder;
    }

    @Override
    public void doExecute(AcceptanceTestWorkflow workflow, TestContext testContext) {
        try {
            workflow.sendLmae(acceptanceTestDataBuilder, Auftragsposition.ProduktBezeichner.HVT_2H);
            testContext.getVariables().put(VariableNames.TEST_DATA, workflow.getCreatedData());
        }
        catch (Exception e) {
            throw new CitrusRuntimeException("Failed to send Wita LMAE", e);
        }
    }

    public AcceptanceTestDataBuilder testDataBuilder() {
        return acceptanceTestDataBuilder;
    }

}
