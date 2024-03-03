/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.08.2011 13:21:21
 */
package de.mnet.wita.acceptance.common.function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import de.mnet.wita.activiti.CanOpenActivitiWorkflow;
import de.mnet.wita.bpm.CommonWorkflowService;

public abstract class AbstractPvWorkflowFunction<T> extends AbstractAutowiringAcceptanceFunction<T> {

    @Autowired
    protected CommonWorkflowService commonWorkflowService;

    protected final CanOpenActivitiWorkflow akmPv;

    public AbstractPvWorkflowFunction(CanOpenActivitiWorkflow akmPv, ApplicationContext applicationContext) {
        super(applicationContext);
        this.akmPv = akmPv;
    }

}


