/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.08.2011 12:07:25
 */
package de.mnet.wita.acceptance.common.function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import de.mnet.wita.bpm.CommonWorkflowService;
import de.mnet.wita.model.WitaCBVorgang;

public abstract class AbstractTalOrderWorkflowFunction<T> extends AbstractAutowiringAcceptanceFunction<T> {

    @Autowired
    protected CommonWorkflowService commonWorkflowService;

    protected final WitaCBVorgang cbVorgang;

    public AbstractTalOrderWorkflowFunction(WitaCBVorgang cbVorgang, ApplicationContext applicationContext) {
        super(applicationContext);

        this.cbVorgang = cbVorgang;
    }

}


