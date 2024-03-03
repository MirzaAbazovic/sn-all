/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.10.13
 */
package de.mnet.wbci.citrus.actions;

import com.consol.citrus.context.TestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mnet.wbci.dao.WbciDao;
import de.mnet.wbci.model.WbciEntity;

/**
 *
 */
public class StoreWbciEntityTestAction extends AbstractWbciTestAction {

    /**
     * Dao and entity injected as constructor fields
     */
    private WbciDao wbciDao;
    private WbciEntity wbciEntity;

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(StoreWbciEntityTestAction.class);

    public StoreWbciEntityTestAction(WbciDao wbciDao, WbciEntity wbciEntity) {
        super("storeWbciEntity");
        this.wbciDao = wbciDao;
        this.wbciEntity = wbciEntity;
    }

    @Override
    public void doExecute(TestContext context) {
        WbciEntity entity = wbciDao.store(wbciEntity);
        LOGGER.debug("Successfully stored entity to database: " + entity);
    }
}
