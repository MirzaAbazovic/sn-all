/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.12.14
 */
package de.mnet.wbci.citrus.actions;

import org.apache.commons.lang.BooleanUtils;
import org.testng.Assert;

import com.consol.citrus.context.TestContext;

import de.mnet.wbci.citrus.VariableNames;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.service.WbciCommonService;
import de.mnet.wbci.service.WbciGeschaeftsfallService;

/**
 * Created by glinkjo on 01.12.14.
 */
public class ClearKlaerfallTestAction extends AbstractWbciTestAction {

    private WbciGeschaeftsfallService wbciGeschaeftsfallService;
    private WbciCommonService wbciCommonService;

    public ClearKlaerfallTestAction(WbciGeschaeftsfallService wbciGeschaeftsfallService, WbciCommonService wbciCommonService) {
        super("clearKlaerfallTestAction");
        this.wbciGeschaeftsfallService = wbciGeschaeftsfallService;
        this.wbciCommonService = wbciCommonService;
    }

    @Override
    public void doExecute(TestContext context) {
        String vorabstimmungsId = context.getVariable(VariableNames.PRE_AGREEMENT_ID);
        Assert.assertNotNull(vorabstimmungsId);

        final WbciGeschaeftsfall wbciGeschaeftsfall = wbciCommonService.findWbciGeschaeftsfall(vorabstimmungsId);
        Assert.assertNotNull(wbciGeschaeftsfall, String.format(
                "WbciGeschaeftsfall with VorabstimmungsId '%s' could not be found in the database", vorabstimmungsId));

        if (BooleanUtils.isTrue(wbciGeschaeftsfall.getKlaerfall())) {
            wbciGeschaeftsfallService.issueClarified(wbciGeschaeftsfall.getId(), null,
                    "cleanup by citrus ClearKlaerfallTestAction");
        }
    }

}
