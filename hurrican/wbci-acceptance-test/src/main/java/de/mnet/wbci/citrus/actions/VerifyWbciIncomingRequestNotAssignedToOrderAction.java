/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.11.13
 */
package de.mnet.wbci.citrus.actions;

import com.consol.citrus.context.TestContext;
import org.testng.Assert;

import de.mnet.wbci.citrus.VariableNames;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.service.WbciCommonService;

/**
 * Citrus TestAction um zu ueberpruefen, dass ein eingehender WBCI Request (bzw. der zugehoerige Geschaeftsfall) keinem
 * Taifun Auftrag zugeordnet wurde.
 */
public class VerifyWbciIncomingRequestNotAssignedToOrderAction extends AbstractWbciTestAction {

    private WbciCommonService wbciCommonService;

    public VerifyWbciIncomingRequestNotAssignedToOrderAction(WbciCommonService wbciCommonService) {
        super("verifyWbciIncomingRequestNotAssignedToOrder");
        this.wbciCommonService = wbciCommonService;
    }

    @Override
    public void doExecute(TestContext context) {
        String vorabstimmungsId = context.getVariable(VariableNames.PRE_AGREEMENT_ID);
        Assert.assertNotNull(vorabstimmungsId);

        final WbciGeschaeftsfall wbciGeschaeftsfall = wbciCommonService.findWbciGeschaeftsfall(vorabstimmungsId);
        Assert.assertNull(wbciGeschaeftsfall.getBillingOrderNoOrig(),
                String.format("WbciGeschaeftsfall with VorabstimmungsId '%s' and billing order '%s' should NOT be assigned a billing order",
                        vorabstimmungsId, wbciGeschaeftsfall.getBillingOrderNoOrig())
        );
        Assert.assertNull(wbciGeschaeftsfall.getEndkunde().getKundenTyp(),
                String.format("WbciGeschaeftsfall with VorabstimmungsId '%s' sholud NOT have an asigned customer typ, but has '%s' as asigned customer typ",
                        vorabstimmungsId, wbciGeschaeftsfall.getEndkunde().getKundenTyp())
        );
    }

}
