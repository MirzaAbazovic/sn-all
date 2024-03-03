/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.01.14
 */
package de.mnet.wbci.citrus.actions;

import com.consol.citrus.context.TestContext;
import org.testng.Assert;

import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.service.WbciCommonService;

/**
 * Retrieves the linked StrAen WbciGeschaeftsfall and verifies that the status of the linked WbciGeschaeftsfall matches
 * the expected status.
 */
public class VerifyLinkedStrAenGfStatusAction extends AbstractWbciTestAction {

    private WbciCommonService wbciCommonService;
    private WbciGeschaeftsfallStatus expectedStatus;

    public VerifyLinkedStrAenGfStatusAction(WbciCommonService wbciCommonService, WbciGeschaeftsfallStatus expectedStatus) {
        super("verifyLinkedStrAenGfStatus");
        this.wbciCommonService = wbciCommonService;
        this.expectedStatus = expectedStatus;
    }

    @Override
    public void doExecute(TestContext context) {
        final WbciGeschaeftsfall originalStrAenGf = getLinkedStrAenGeschaeftsfall(context, wbciCommonService);
        Assert.assertEquals(originalStrAenGf.getStatus(), expectedStatus);
    }
}
