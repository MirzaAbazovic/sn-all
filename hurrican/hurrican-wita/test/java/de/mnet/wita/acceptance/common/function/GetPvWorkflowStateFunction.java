/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.08.2011 12:07:02
 */
package de.mnet.wita.acceptance.common.function;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import de.mnet.wita.activiti.CanOpenActivitiWorkflow;

public class GetPvWorkflowStateFunction extends AbstractPvWorkflowFunction<String> {
    private static final Logger LOGGER = Logger.getLogger(GetPvWorkflowStateFunction.class);

    public GetPvWorkflowStateFunction(CanOpenActivitiWorkflow akmPv, ApplicationContext applicationContext) {
        super(akmPv, applicationContext);
    }

    @Override
    public String apply(Void input) {
        String state = commonWorkflowService.getWorkflowState(akmPv.getBusinessKey());
        LOGGER.info("worklfow '" + akmPv.getBusinessKey() + "' is in state '" + state + "'");
        return state;
    }

}


