/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.08.2015
 */
package de.mnet.wita.citrus.actions;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;

import de.mnet.wita.acceptance.common.AcceptanceTestWorkflow;
import de.mnet.wita.acceptance.common.CreatedData;
import de.mnet.wita.citrus.VariableNames;

/**
 *
 */
public class CreateCbVorgangTestAction extends AbstractWitaWorkflowTestAction {

    protected final CreatedData createdData;
    protected final Long cbVorgangTyp;

    /**
     * Default constructor using fields.
     * @param createdData
     * @param cbVorgangTyp
     */
    public CreateCbVorgangTestAction(CreatedData createdData, Long cbVorgangTyp) {
        super("createCbVorgang");
        this.createdData = createdData;
        this.cbVorgangTyp = cbVorgangTyp;
    }

    @Override
    public void doExecute(AcceptanceTestWorkflow workflow, TestContext testContext) {
        try {
            workflow.send(cbVorgangTyp);
            testContext.getVariables().put(VariableNames.TEST_DATA, createdData);
        }
        catch (Exception e) {
            throw new CitrusRuntimeException("Failed to send Wita Bereitstellung", e);
        }
    }
}
