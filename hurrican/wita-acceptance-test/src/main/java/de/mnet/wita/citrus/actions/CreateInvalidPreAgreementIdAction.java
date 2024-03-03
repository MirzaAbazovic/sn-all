/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.02.14
 */
package de.mnet.wita.citrus.actions;

import com.consol.citrus.context.TestContext;

import de.mnet.wita.citrus.VariableNames;

public class CreateInvalidPreAgreementIdAction extends AbstractWitaTestAction {

    public CreateInvalidPreAgreementIdAction() {
        super("createInvalidPreAgreementIdAction");
    }

    @Override
    public void doExecute(TestContext testContext) {
        testContext.setVariable(VariableNames.PRE_AGREEMENT_ID, "DEU.ABCD.VH00000000");
    }

}
