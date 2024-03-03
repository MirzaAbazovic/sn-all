/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.11.14
 */
package de.mnet.wbci.citrus.actions;

import com.consol.citrus.context.TestContext;
import org.testng.Assert;

import de.mnet.wbci.citrus.VariableNames;
import de.mnet.wbci.dao.WbciDao;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.service.WbciCommonService;

/**
 * Action, um einen {@link WbciGeschaeftsfall} als 'automatisierbar' markieren zu koennen.
 */
public class MarkGfAsAutomatableAction extends AbstractWbciTestAction {

    private WbciCommonService wbciCommonService;
    private WbciDao wbciDao;

    public MarkGfAsAutomatableAction(WbciCommonService wbciCommonService, WbciDao wbciDao) {
        super("markGfAsAutomatable");
        this.wbciCommonService = wbciCommonService;
        this.wbciDao = wbciDao;
    }

    @Override
    public void doExecute(TestContext context) {
        String vorabstimmungsId = context.getVariable(VariableNames.PRE_AGREEMENT_ID);
        Assert.assertNotNull(vorabstimmungsId);

        final WbciGeschaeftsfall wbciGeschaeftsfall = wbciCommonService.findWbciGeschaeftsfall(vorabstimmungsId);
        Assert.assertNotNull(wbciGeschaeftsfall, String.format(
                "WbciGeschaeftsfall with VorabstimmungsId '%s' could not be found in the database", vorabstimmungsId));

        wbciGeschaeftsfall.setAutomatable(Boolean.TRUE);
        wbciDao.store(wbciGeschaeftsfall);
    }

}
