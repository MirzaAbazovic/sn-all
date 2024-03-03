/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.02.14
 */
package de.mnet.wita.citrus.actions;

import com.consol.citrus.context.TestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mnet.wbci.dao.WbciDao;
import de.mnet.wbci.model.WbciEntity;

/**
 *
 */
public abstract class CreateWbciEntityAction extends AbstractWitaWbciTestAction {

    /** Logger */
    private static final Logger LOG = LoggerFactory.getLogger(CreateWbciEntityAction.class);

    public CreateWbciEntityAction(WbciDao wbciDao) {
        super("createWbciEntity", wbciDao);
    }

    public abstract WbciEntity getWbciEntity(TestContext context);

    @Override
    public void doExecute(TestContext context) {
        WbciEntity entity = wbciDao.store(getWbciEntity(context));
        LOG.debug("Successfully stored entity to database: " + entity);
    }

}
