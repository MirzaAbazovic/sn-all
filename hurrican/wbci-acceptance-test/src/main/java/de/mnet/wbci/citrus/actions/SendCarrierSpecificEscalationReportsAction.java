/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.10.13
 */
package de.mnet.wbci.citrus.actions;

import java.util.*;
import com.consol.citrus.context.TestContext;

import de.mnet.wbci.citrus.VariableNames;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.service.WbciEscalationService;

/**
 * Test action which triggers all escalation carrier reports.
 *
 *
 */
public class SendCarrierSpecificEscalationReportsAction extends AbstractWbciTestAction {

    private WbciEscalationService wbciEscalationService;

    public SendCarrierSpecificEscalationReportsAction(WbciEscalationService wbciEscalationService) {
        super("sendCarrierSpecificEscalationReports");
        this.wbciEscalationService = wbciEscalationService;
    }

    @Override
    public void doExecute(TestContext context) {
        final List<CarrierCode> carrierCodes = wbciEscalationService.sendCarrierSpecificEscalationReports();
        context.setVariable(VariableNames.ESCALATION_REPORT_CARRIER_LIST, carrierCodes);
    }

}
