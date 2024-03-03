/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.11.2014
 */
package de.mnet.hurrican.acceptance.actions;

import javax.sql.*;
import com.consol.citrus.actions.AbstractTestAction;
import com.consol.citrus.context.TestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.augustakom.hurrican.dao.cc.CCAuftragDAO;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.mnet.hurrican.acceptance.builder.TvTestBuilder;
import de.mnet.hurrican.ffm.citrus.TaifunVariables;

public class CreateTvAuftragTestAction extends AbstractTestAction {

    /**
     * Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(CreateTvAuftragTestAction.class);

    private final CCAuftragDAO ccAuftragDAO;
    private final Long prodId;
    private final Long standortTypRefId;
    private DataSource taifunDataSource;

    /**
     * Constructor setting the action name field.
     */
    public CreateTvAuftragTestAction(DataSource taifunDataSource, CCAuftragDAO ccAuftragDAO, final Long prodId, final Long standortTypRefId) {
        setName("createTvAuftrag");
        this.taifunDataSource = taifunDataSource;
        this.ccAuftragDAO = ccAuftragDAO;
        this.prodId = prodId;
        this.standortTypRefId = standortTypRefId;
    }

    @Override
    public void doExecute(TestContext context) {
        LOG.info("Creating Tv Auftrag");
        AuftragDaten auftragDaten =
                new TvTestBuilder().configure(ccAuftragDAO)
                        .withProdId(prodId)
                        .withStandortTypRefId(standortTypRefId)
                        .withRangierung(true)
                        .buildTvAuftrag(context);

        context.setVariable(TaifunVariables.ORDER_NO, auftragDaten.getAuftragNoOrig());
    }
}