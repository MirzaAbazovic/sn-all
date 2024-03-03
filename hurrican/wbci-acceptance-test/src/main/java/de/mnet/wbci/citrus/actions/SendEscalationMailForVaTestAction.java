/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.04.2014
 */
package de.mnet.wbci.citrus.actions;

import com.consol.citrus.context.TestContext;

import de.augustakom.authentication.model.AKUser;
import de.mnet.wbci.service.WbciEscalationService;

/**
 * Test action which sends an escalation mail for the current VA.
 *
 *
 */
public class SendEscalationMailForVaTestAction extends AbstractWbciTestAction {

    private WbciEscalationService wbciEscalationService;
    private AKUser user;

    public SendEscalationMailForVaTestAction(WbciEscalationService wbciEscalationService, AKUser user) {
        super("sendEscalationMailForVa");
        this.wbciEscalationService = wbciEscalationService;
        this.user = user;
    }

    @Override
    public void doExecute(TestContext context) {
        wbciEscalationService.sendVaEscalationMailToCarrier(getVorabstimmungsId(context), user);
    }

}
