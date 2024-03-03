/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.08.2011 12:07:02
 */
package de.mnet.wita.acceptance.common.function;

import org.springframework.context.ApplicationContext;

import de.mnet.wita.model.WitaCBVorgang;

public class GetTalOrderWorkflowStateFunction extends AbstractTalOrderWorkflowFunction<String> {

    public GetTalOrderWorkflowStateFunction(WitaCBVorgang cbVorgang, ApplicationContext applicationContext) {
        super(cbVorgang, applicationContext);
    }

    @Override
    public String apply(Void input) {
        return commonWorkflowService.getWorkflowState(cbVorgang.getBusinessKey());
    }

}


