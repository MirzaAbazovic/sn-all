/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.07.13
 */
package de.mnet.wbci.citrus.actions;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import org.testng.Assert;

import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.service.WbciCommonService;

/**
 * Citrus Test Action verifies whether the created (outgoing) Wbci-Geschaeftsfall is stored in the database.
 *
 *
 */
public class VerifyWbciOutgoingRequestCreatedAction extends AbstractWbciTestAction {

    private WbciCommonService wbciCommonService;

    public VerifyWbciOutgoingRequestCreatedAction(WbciCommonService wbciCommonService) {
        super("verifyWbciOutgoingRequestCreated");
        this.wbciCommonService = wbciCommonService;
    }

    @Override
    public void doExecute(TestContext context) {
        final String vorabstimmungsId = getVorabstimmungsId(context);
        if (vorabstimmungsId == null) {
            throw new CitrusRuntimeException("Variable 'PRE_AGREEMENT_ID_' is null, the test variable hast to be set in the TestContext");
        }

        final WbciGeschaeftsfall wbciGeschaeftsfallReloaded = wbciCommonService.findWbciGeschaeftsfall(vorabstimmungsId);
        Assert.assertNotNull(wbciGeschaeftsfallReloaded, String.format("WbciGeschaeftsfall with VorabstimmungsId '%s' could not be " +
                "found in the database", vorabstimmungsId));
    }

}
