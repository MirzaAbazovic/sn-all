/* 
 * Copyright (c) 2014 - M-net Telekommunikations GmbH 
 * All rights reserved. 
 * ------------------------------------------------------- 
 * File created: 25.07.2014 
 */
package de.mnet.wbci.citrus.actions;

import java.util.*;
import com.consol.citrus.context.TestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.testng.Assert;

import de.mnet.wbci.dao.WbciDao;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.service.WbciGeschaeftsfallService;

/**
 * Prevents VAs that are capable of being processed automatically, from being automatically processed.
 */
public abstract class AbstractResetAutomatedVaProcessingTestAction extends AbstractWbciTestAction {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractResetAutomatedVaProcessingTestAction.class);

    protected WbciDao wbciDao;
    private WbciGeschaeftsfallService wbciGeschaeftsfallService;

    public AbstractResetAutomatedVaProcessingTestAction(String actionName, WbciDao wbciDao, WbciGeschaeftsfallService wbciGeschaeftsfallService) {
        super(actionName);
        this.wbciDao = wbciDao;
        this.wbciGeschaeftsfallService = wbciGeschaeftsfallService;
    }

    protected abstract Set<String> getVaIdsToPreventForAutomaticProcessing();

    @Override
    public void doExecute(TestContext context) {
        Set<String> vaIds = getVaIdsToPreventForAutomaticProcessing();
        if(!CollectionUtils.isEmpty(vaIds)) {
            for (String vaId :vaIds) {
                LOGGER.info(String.format("Setting klaerfall flag on geschaeftsfall '%s'", vaId));
                WbciGeschaeftsfall gf = wbciDao.findWbciGeschaeftsfall(vaId);
                wbciGeschaeftsfallService.markGfForClarification(gf.getId(), "set by citrus", null);
            }
        }
        Assert.assertTrue(CollectionUtils.isEmpty(getVaIdsToPreventForAutomaticProcessing()));
    }

}
