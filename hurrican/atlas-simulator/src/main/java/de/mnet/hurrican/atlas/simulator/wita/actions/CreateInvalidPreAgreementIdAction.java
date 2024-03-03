/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.02.14
 */
package de.mnet.hurrican.atlas.simulator.wita.actions;

import static de.mnet.hurrican.atlas.simulator.wita.WitaLineOrderVariableNames.*;

import com.consol.citrus.context.TestContext;

public class CreateInvalidPreAgreementIdAction extends AbstractWitaTestAction {

    public CreateInvalidPreAgreementIdAction() {
        super("createInvalidPreAgreementIdAction");
    }

    @Override
    public void doExecute(TestContext testContext) {
        testContext.setVariable(PRE_AGREEMENT_ID, "DEU.ABCD.VH00000000");
    }

}
