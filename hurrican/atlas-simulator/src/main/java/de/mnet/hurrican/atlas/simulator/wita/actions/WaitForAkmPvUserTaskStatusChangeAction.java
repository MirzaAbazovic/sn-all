/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.12.2014
 */
package de.mnet.hurrican.atlas.simulator.wita.actions;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;

import de.mnet.wita.model.AkmPvUserTask;
import de.mnet.wita.model.UserTask;
import de.mnet.wita.service.WitaUsertaskService;
import de.mnet.hurrican.atlas.simulator.wita.WitaLineOrderVariableNames;

/**
 *
 */
public class WaitForAkmPvUserTaskStatusChangeAction extends AbstractWitaTestAction {

    private final WitaUsertaskService witaUsertaskService;
    private final UserTask.UserTaskStatus expectedStatus;

    public WaitForAkmPvUserTaskStatusChangeAction(WitaUsertaskService witaUsertaskService, UserTask.UserTaskStatus expectedStatus) {
        super("waitForAkmPvUserTaskStatusChange");
        this.witaUsertaskService = witaUsertaskService;
        this.expectedStatus = expectedStatus;
    }

    @Override
    public void doExecute(TestContext context) {
        String contractId = context.getVariable(WitaLineOrderVariableNames.CONTRACT_ID);
        AkmPvUserTask userTask = witaUsertaskService.findAkmPvUserTaskByContractId(contractId);

        if (userTask == null) {
            throw new CitrusRuntimeException(
                    String.format("No AkmPvUserTask found for contractId '%s'", contractId));
        }
        if (!expectedStatus.equals(userTask.getStatus())) {
            throw new CitrusRuntimeException(
                    String.format("WitaCbVorgang with contractId '%s' has status '%s' instead of '%s' as expected",
                            contractId, userTask.getStatus(), expectedStatus));
        }
    }

}
