/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.05.14
 */
package de.mnet.wbci.citrus.actions;

import com.consol.citrus.context.TestContext;
import org.testng.Assert;

import de.mnet.wbci.citrus.VariableNames;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.service.WbciCommonService;

/**
 *
 */
public class VerifyNonBillingRelevantOrdersAssignedAction extends AbstractWbciTestAction {

    private WbciCommonService wbciCommonService;
    private Long[] taifunOrderNoOrigs;

    public VerifyNonBillingRelevantOrdersAssignedAction(WbciCommonService wbciCommonService, Long ... taifunOrderNoOrigs) {
        super("verifyNonBillingRelevantOrdersAssigned");
        this.wbciCommonService = wbciCommonService;
        this.taifunOrderNoOrigs = taifunOrderNoOrigs;
    }

    @Override
    public void doExecute(TestContext context) {
        String vorabstimmungsId = context.getVariable(VariableNames.PRE_AGREEMENT_ID);
        Assert.assertNotNull(vorabstimmungsId);

        final WbciGeschaeftsfall wbciGeschaeftsfall = wbciCommonService.findWbciGeschaeftsfall(vorabstimmungsId);

        Assert.assertEquals(wbciGeschaeftsfall.getNonBillingRelevantOrderNoOrigs().size(), taifunOrderNoOrigs.length,
                "Non billing relevant order assignments not as expected");

        for (Long taifunOrderNoOrig : taifunOrderNoOrigs) {
            Assert.assertTrue(wbciGeschaeftsfall.getNonBillingRelevantOrderNoOrigs().contains(taifunOrderNoOrig),
                    String.format("WbciGeschaeftsfall with VorabstimmungsId '%s' missing non billing relevant order assignment '%s'",
                    vorabstimmungsId, taifunOrderNoOrig));
        }
    }
}
