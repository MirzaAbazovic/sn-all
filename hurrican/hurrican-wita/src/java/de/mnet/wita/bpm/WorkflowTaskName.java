/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.09.2011 14:04:14
 */
package de.mnet.wita.bpm;

/**
 * Haelt die ids (=taskDefinitionKey) der Tasks in den Workflows.
 */
public enum WorkflowTaskName {

    PROCESS_AKM_PV("processAkmPv"),
    PROCESS_TAM("processTAM"),

    WAIT_FOR_ENTM("waitForENTMMessage"),
    WAIT_FOR_ENTM_PV("waitForEntmPvMessage"),
    WAIT_FOR_MESSAGE("waitForMessage"),
    WAIT_FOR_RUEMPV("waitForRuemPv"),

    WORKFLOW_ERROR("workflowError");

    public final String id;

    private WorkflowTaskName(String id) {
        this.id = id;
    }
}
