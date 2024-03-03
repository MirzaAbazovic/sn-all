/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.07.13
 */
package de.mnet.wbci.citrus.actions;

import com.consol.citrus.actions.AbstractTestAction;
import com.consol.citrus.context.TestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.testng.Assert;

import de.mnet.wbci.citrus.VariableNames;
import de.mnet.wbci.citrus.helper.WbciDateUtils;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.builder.ProjektBuilder;
import de.mnet.wbci.service.WbciVaService;

/**
 * Citrus test action calls a WBCI remote service on Hurrican web server application for creating a new
 * WbciGeschaeftsfall use case. This simulates the Hurrican GUI which interacts with Hurrican web server via remote
 * service calls.
 *
 *
 */
public class CreateWbciVorgangTestAction<GF extends WbciGeschaeftsfall> extends AbstractTestAction {

    /**
     * Hurrican remote service and geschaeftsfall
     */
    private WbciVaService<GF> wbciCreateVaService;
    private GF wbciGeschaeftsfall;

    /**
     * Use case name for wbci simulator in end-to-end acceptance tests
     */
    private String projektKenner;

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateWbciVorgangTestAction.class);

    /**
     * Default constructor requires proper WbciCreateVaService instance and WbciGeschaeftsfall fields.
     *
     * @param wbciCreateVaService
     * @param wbciGeschaeftsfall
     */
    public CreateWbciVorgangTestAction(WbciVaService<GF> wbciCreateVaService, GF wbciGeschaeftsfall) {
        this.wbciCreateVaService = wbciCreateVaService;
        this.wbciGeschaeftsfall = wbciGeschaeftsfall;

        setName("createWbciVorgang");
    }

    @Override
    public void doExecute(TestContext context) {
        addSimulatorUseCase(context);

        VorabstimmungsAnfrage<GF> anfrage = wbciCreateVaService.createWbciVorgang(wbciGeschaeftsfall);
        wbciGeschaeftsfall = anfrage.getWbciGeschaeftsfall();
        Assert.assertTrue(wbciGeschaeftsfall.getId() != null);
        Assert.assertTrue(wbciGeschaeftsfall.getVorabstimmungsId() != null);
        context.setVariable(VariableNames.PRE_AGREEMENT_ID, wbciGeschaeftsfall.getVorabstimmungsId());
        context.setVariable(VariableNames.WBCI_GESCHAEFTSFALL_TYPE, wbciGeschaeftsfall.getTyp().name());

        context.setVariable(VariableNames.CARRIER_CODE_ABGEBEND, wbciGeschaeftsfall.getAbgebenderEKP()
                .getITUCarrierCode());
        context.setVariable(VariableNames.CARRIER_CODE_AUFNEHMEND, wbciGeschaeftsfall.getAufnehmenderEKP()
                .getITUCarrierCode());

        context.setVariable(VariableNames.REQUESTED_CUSTOMER_DATE,
                WbciDateUtils.formatToWbciDate(wbciGeschaeftsfall.getKundenwunschtermin().atStartOfDay()));
    }

    /**
     * Sets the WBCI simulator use case as ProjektKenner. In a End-to-end acceptance test scenario the WBCI simulator is
     * then triggered by Atlas ESB with the respective use case. In local acceptance tests where Atlas is mocked by
     * Citrus this has no effect.
     *
     * @param context
     */
    private void addSimulatorUseCase(TestContext context) {
        if (wbciGeschaeftsfall.getProjekt() != null) {
            LOGGER.info(String.format("WbciGeschaeftsfall has already set some projekt instance (%s)" +
                    " - skip automatic projekt setting", wbciGeschaeftsfall.getProjekt().getProjektKenner()));
            return;
        }

        if (StringUtils.hasText(projektKenner)) {
            wbciGeschaeftsfall.setProjekt(new ProjektBuilder().withProjektKenner(projektKenner).build());
        }
        else if (context.getVariables().containsKey(VariableNames.SIMULATOR_USE_CASE)) {
            wbciGeschaeftsfall.setProjekt(new ProjektBuilder().withProjektKenner(
                    context.getVariable(VariableNames.SIMULATOR_USE_CASE)).build());
        }
        else {
            wbciGeschaeftsfall.setProjekt(new ProjektBuilder().withProjektKenner("DEFAULT_TEQ").build());
        }
    }

    /**
     * Sets the projekt kenner in wbciGeschaeftsfall object. This leads to request message containing the wbci simulator
     * use case name in end-to-end acceptance tests.
     *
     * @param projektKenner
     */
    public void setProjektKenner(String projektKenner) {
        this.projektKenner = projektKenner;
    }

}
