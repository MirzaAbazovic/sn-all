/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.10.13
 */
package de.mnet.wbci.citrus.actions;

import com.consol.citrus.context.TestContext;

import de.mnet.wbci.service.WbciEscalationService;

/**
 * Test action which triggers all scheduled escalation reports.
 *
 *
 */
public class SendInternalverviewReportAction extends AbstractWbciTestAction {

    private WbciEscalationService wbciEscalationService;

    public SendInternalverviewReportAction(WbciEscalationService wbciEscalationService) {
        super("sendInternalOverviewReportAction");
        this.wbciEscalationService = wbciEscalationService;
    }

    @Override
    public void doExecute(TestContext context) {
        wbciEscalationService.sendInternalOverviewReport();
    }

}
