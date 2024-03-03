/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.08.2011 17:13:53
 */
package de.mnet.wita.bpm.impl;

import java.util.*;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;


/**
 * Konstanten fuer die WITA-Workflows
 */
public enum Workflow {
    ABGEBEND_PV("AbgebendPv"),
    KUE_DT("KueDt"),
    TAL_ORDER("TalOrder");

    public final String processDefinitionKey;

    private Workflow(String processDefinitionKey) {
        this.processDefinitionKey = processDefinitionKey;
    }

    /**
     * @param processDefinitionKey
     * @return Die Workflow oder null, wenn keiner mit dem ProcessDefinitionKey gefunden wurde.
     */
    public static Workflow of(String processDefinitionKey) {
        for (Workflow workflow : Workflow.values()) {
            if (workflow.processDefinitionKey.equals(processDefinitionKey)) {
                return workflow;
            }
        }
        return null;
    }

    public static Workflow of(ProcessInstance processInstance,
            Map<String, ProcessDefinition> processDefinitions) {
        Workflow workflow = null;
        if (processDefinitions.containsKey(processInstance.getProcessDefinitionId())) {
            workflow = Workflow.of(processDefinitions.get(processInstance.getProcessDefinitionId()).getKey());
        }
        return workflow;
    }

}
