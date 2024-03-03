/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.07.13
 */
package de.mnet.wbci.citrus.actions;

import com.consol.citrus.context.TestContext;
import org.testng.Assert;

import de.mnet.wbci.citrus.VariableNames;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.service.WbciCommonService;

/**
 * Citrus Test Action verifies whether an incoming Wbci-Geschaeftsfall is stored in the database.
 *
 *
 */
public class VerifyWbciIncomingRequestCreatedAction extends AbstractWbciTestAction {

    private WbciCommonService wbciCommonService;

    public VerifyWbciIncomingRequestCreatedAction(WbciCommonService wbciCommonService) {
        super("verifyWbciIncomingRequestCreated");
        this.wbciCommonService = wbciCommonService;
    }

    @Override
    public void doExecute(TestContext context) {
        String vorabstimmungsId = context.getVariable(VariableNames.PRE_AGREEMENT_ID);
        Assert.assertNotNull(vorabstimmungsId);

        final WbciGeschaeftsfall wbciGeschaeftsfall = wbciCommonService.findWbciGeschaeftsfall(vorabstimmungsId);
        Assert.assertNotNull(wbciGeschaeftsfall, String.format(
                "WbciGeschaeftsfall with VorabstimmungsId '%s' could not be found in the database", vorabstimmungsId));
    }

}
