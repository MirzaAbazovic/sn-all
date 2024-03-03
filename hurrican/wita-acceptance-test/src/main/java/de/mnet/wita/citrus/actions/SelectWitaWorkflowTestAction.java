/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.08.2015
 */
package de.mnet.wita.citrus.actions;

import com.consol.citrus.context.TestContext;

import de.mnet.wita.acceptance.common.AcceptanceTestWorkflow;
import de.mnet.wita.citrus.VariableNames;

/**
 * Saves workflow instance to test variables. This has the effect that next test action will operate workflow actions
 * on this particular workflow instance. This way test case can work with multiple workflow instances at the same time.
 *
 *
 */
public class SelectWitaWorkflowTestAction extends AbstractWitaTestAction {

    /** The workflow instance to set */
    private final AcceptanceTestWorkflow workflow;

    /**
     * Constructor setting the action name.
     */
    public SelectWitaWorkflowTestAction(AcceptanceTestWorkflow workflow) {
        super("selectWitaWorkflow");
        this.workflow = workflow;
    }

    @Override
    public void doExecute(TestContext testContext) {
        testContext.getVariables().put(VariableNames.TEST_WORKFLOW, workflow);
    }
}
