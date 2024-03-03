/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.09.13
 */
package de.mnet.wbci.citrus.actions;

import com.consol.citrus.context.TestContext;
import org.testng.Assert;

import de.mnet.wbci.citrus.VariableNames;
import de.mnet.wbci.model.StornoAnfrage;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.service.WbciStornoService;

public class CreateWbciStornoTestAction<GF extends WbciGeschaeftsfall> extends AbstractWbciTestAction {

    private WbciStornoService<GF> wbciStornoService;
    private StornoAnfrage<?> stornoAnfrage;

    public CreateWbciStornoTestAction(WbciStornoService<GF> wbciStornoService, StornoAnfrage<?> stornoAnfrage) {
        super("createWbciStorno");
        this.wbciStornoService = wbciStornoService;
        this.stornoAnfrage = stornoAnfrage;
    }

    @Override
    public void doExecute(TestContext context) {
        String vorabstimmungsId = context.getVariable(VariableNames.PRE_AGREEMENT_ID);
        Assert.assertNotNull(vorabstimmungsId);
        StornoAnfrage<GF> newStornoAnfrage = wbciStornoService.createWbciStorno(stornoAnfrage, vorabstimmungsId);
        Assert.assertNotNull(newStornoAnfrage.getId());
        Assert.assertNotNull(newStornoAnfrage.getAenderungsId());
        context.setVariable(VariableNames.STORNO_ID, newStornoAnfrage.getAenderungsId());
    }

}
