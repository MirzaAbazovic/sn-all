/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.01.2015
 */
package de.mnet.wbci.citrus.actions;

import com.consol.citrus.context.TestContext;
import org.testng.Assert;

import de.mnet.wbci.dao.WbciDao;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.service.WbciCommonService;

/**
 *
 */
public class AssignUserToWbciGeschaeftsfallAction extends AbstractWbciTestAction {

    private WbciCommonService wbciCommonService;
    private WbciDao wbciDao;
    private Long userId;

    public AssignUserToWbciGeschaeftsfallAction(WbciCommonService wbciCommonService, WbciDao wbciDao, Long userId) {
        super("AssignUserToWbciGeschaeftsfall");
        this.wbciCommonService = wbciCommonService;
        this.wbciDao = wbciDao;
        this.userId = userId;
    }

    @Override
    public void doExecute(TestContext context) {
        final String vorabstimmungsId = getVorabstimmungsId(context);
        WbciGeschaeftsfall wbciGeschaeftsfall = wbciCommonService.findWbciGeschaeftsfall(vorabstimmungsId);
        Assert.assertNotNull(wbciGeschaeftsfall, String.format(
                "WbciGeschaeftsfall with VorabstimmungsId '%s' could not be found in the database", vorabstimmungsId));
        wbciGeschaeftsfall.setUserId(userId);
        wbciDao.store(wbciGeschaeftsfall);
    }

}
