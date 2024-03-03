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
import de.mnet.hurrican.ffm.citrus.VariableNames;

public class CreateBundledTvAuftraegeTestAction extends AbstractTestAction {

    /**
     * Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(CreateBundledTvAuftraegeTestAction.class);

    private final CCAuftragDAO ccAuftragDAO;
    private final Long prodId;
    private final Long standortTypRefId;

    private DataSource taifunDataSource;

    /**
     * Constructor setting the action name field.
     */
    public CreateBundledTvAuftraegeTestAction(DataSource taifunDataSource, CCAuftragDAO ccAuftragDAO, final Long prodId, final Long standortTypRefId) {
        setName("createBundledTvAuftraege");
        this.taifunDataSource = taifunDataSource;
        this.ccAuftragDAO = ccAuftragDAO;
        this.prodId = prodId;
        this.standortTypRefId = standortTypRefId;
    }

    @Override
    public void doExecute(TestContext context) {

        LOG.info("Creating Mitversorgte-Tv-Auftrag");
        AuftragDaten mitVersorgteAuftrag =
                new TvTestBuilder().configure(ccAuftragDAO)
                        .withProdId(prodId)
                        .withStandortTypRefId(standortTypRefId)
                        .buildTvAuftrag(context);
        context.setVariable(VariableNames.MITVERSORGTE_GEO_ID, context.getVariable(VariableNames.GEO_ID));
        context.setVariable(VariableNames.MITVERSORGTE_ORTSTEIL, context.getVariable(VariableNames.ORTSTEIL));

        LOG.info("Creating Versorgende-Tv-Auftrag");
        AuftragDaten versorgendeAuftrag =
                new TvTestBuilder().configure(ccAuftragDAO)
                        .withProdId(prodId)
                        .withStandortTypRefId(standortTypRefId)
                        .withRangierung(true)
                        .buildTvAuftrag(context);

        context.setVariable(VariableNames.VERSORGENE_GEO_ID, context.getVariable(VariableNames.GEO_ID));
        context.setVariable(VariableNames.VERSORGENE_ORTSTEIL, context.getVariable(VariableNames.ORTSTEIL));
        context.setVariable(TaifunVariables.ORDER_NO, versorgendeAuftrag.getAuftragNoOrig());

        new TvTestBuilder().configure(ccAuftragDAO)
                .bundleAuftraege(context, versorgendeAuftrag, mitVersorgteAuftrag);
    }

}
