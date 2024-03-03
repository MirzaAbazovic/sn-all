/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.11.2014
 */
package de.mnet.hurrican.acceptance.actions;

import com.consol.citrus.actions.AbstractTestAction;
import com.consol.citrus.context.TestContext;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.augustakom.hurrican.model.cc.HVTStandort;
import de.mnet.hurrican.acceptance.builder.HvtStandortTestBuilder;
import de.mnet.hurrican.ffm.citrus.VariableNames;

/**
 *
 */
public class CreateHvtStandortTestAction extends AbstractTestAction {

    /**
     * Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(CreateTvAuftragTestAction.class);

    private final HvtStandortTestBuilder hvtStandortTestBuilder;
    private final String ortsteil;
    private final Long standortTypRefId;

    /**
     * Constructor setting the action name field.
     */
    public CreateHvtStandortTestAction(HvtStandortTestBuilder hvtStandortTestBuilder) {
        this(hvtStandortTestBuilder, RandomStringUtils.randomAlphanumeric(20), HVTStandort.HVT_STANDORT_TYP_FTTB);
    }

    /**
     * Constructor setting the action name field.
     */
    public CreateHvtStandortTestAction(HvtStandortTestBuilder hvtStandortTestBuilder, final String ortsteil, final Long standortTypRefId) {
        setName("createHvtStandort");
        this.hvtStandortTestBuilder = hvtStandortTestBuilder;
        this.ortsteil = ortsteil;
        this.standortTypRefId = standortTypRefId;
    }

    @Override
    public void doExecute(TestContext context) {
        LOG.info("Creating HvtGruppe and HvtStandort");
        HVTStandort hvtStandort =
                hvtStandortTestBuilder
                        .withOrtsteil(ortsteil)
                        .withStandortTypRefId(standortTypRefId)
                        .build();
        context.setVariable(VariableNames.ORTSTEIL, ortsteil);
        context.setVariable(VariableNames.HVT_STANDORT_ID, hvtStandort.getId());
    }

}
