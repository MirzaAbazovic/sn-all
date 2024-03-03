/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.12.13
 */
package de.mnet.wbci.citrus.actions;

import com.consol.citrus.context.TestContext;
import org.testng.Assert;

import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.service.WbciCommonService;
import de.mnet.wbci.service.WbciGeschaeftsfallService;

/**
 * Citrus Test-Action to close the GF issue.
 */
public class CloseGeschaefsfallIssueAction extends AbstractWbciTestAction {

    private final WbciGeschaeftsfallService wbciGeschaeftsfallService;
    private WbciCommonService wbciCommonService;
    private String comment;

    public CloseGeschaefsfallIssueAction(WbciCommonService wbciCommonService,
            WbciGeschaeftsfallService wbciGeschaeftsfallService,
            String comment) {
        super("closeGeschaefsfallIssue");
        this.wbciCommonService = wbciCommonService;
        this.wbciGeschaeftsfallService = wbciGeschaeftsfallService;
        this.comment = comment;
        assert comment != null;
    }

    @Override
    public void doExecute(TestContext context) {
        final String vorabstimmungsId = getVorabstimmungsId(context);

        WbciGeschaeftsfall wbciGeschaeftsfall = wbciCommonService.findWbciGeschaeftsfall(vorabstimmungsId);
        Assert.assertNotNull(wbciGeschaeftsfall);
        Assert.assertTrue(wbciGeschaeftsfall.getKlaerfall());
        wbciGeschaeftsfallService.issueClarified(wbciGeschaeftsfall.getId(), null, comment);
    }

}
