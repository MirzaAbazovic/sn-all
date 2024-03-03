/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.02.14
 */
package de.mnet.wbci.citrus.actions;

import com.consol.citrus.context.TestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mnet.wbci.citrus.VariableNames;
import de.mnet.wita.dao.WitaCBVorgangDao;
import de.mnet.wita.model.WitaCBVorgang;

/**
 *
 */
public class StoreWitaCbVorgangTestAction extends AbstractWbciTestAction {

    /**
     * Dao and entity injected as constructor fields
     */
    private WitaCBVorgangDao witaCBVorgangDao;
    private WitaCBVorgang witaCBVorgang;

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(StoreWbciEntityTestAction.class);
    private boolean withVorabstimmungsId;

    public StoreWitaCbVorgangTestAction(WitaCBVorgangDao witaCBVorgangDao, WitaCBVorgang witaCBVorgang) {
        super("StoreWitaCbVorgang");
        this.witaCBVorgangDao = witaCBVorgangDao;
        this.witaCBVorgang = witaCBVorgang;
    }

    @Override
    public void doExecute(TestContext context) {
        String vorabstimmungsId = null;
        if (withVorabstimmungsId) {
            vorabstimmungsId = context.getVariable(VariableNames.PRE_AGREEMENT_ID);
        }
        witaCBVorgang.setVorabstimmungsId(vorabstimmungsId);
        WitaCBVorgang storedCbVorgang = witaCBVorgangDao.store(witaCBVorgang);
        LOGGER.debug("Successfully stored cb vorgang to database: " + storedCbVorgang);
    }

    public StoreWitaCbVorgangTestAction withVorabstimmungsId(boolean withVorabstimmungsId) {
        this.withVorabstimmungsId = withVorabstimmungsId;
        return this;
    }

}
