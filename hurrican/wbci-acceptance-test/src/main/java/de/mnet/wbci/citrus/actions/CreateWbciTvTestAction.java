/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.09.13
 */
package de.mnet.wbci.citrus.actions;

import java.time.*;
import com.consol.citrus.context.TestContext;
import org.testng.Assert;

import de.mnet.wbci.citrus.VariableNames;
import de.mnet.wbci.citrus.helper.WbciDateUtils;
import de.mnet.wbci.model.TerminverschiebungsAnfrage;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.builder.TerminverschiebungsAnfrageBuilder;
import de.mnet.wbci.service.WbciCommonService;
import de.mnet.wbci.service.WbciTvService;

public class CreateWbciTvTestAction<GF extends WbciGeschaeftsfall> extends AbstractWbciTestAction {

    private WbciTvService<GF> wbciTvService;
    private WbciCommonService wbciCommonService;

    private LocalDate tvTermin;
    private String aenderungsId;

    public CreateWbciTvTestAction(WbciTvService<GF> wbciTvService, WbciCommonService wbciCommonService,
            LocalDate tvTermin, String aenderungsId) {
        super("createWbciTv");
        this.wbciTvService = wbciTvService;
        this.wbciCommonService = wbciCommonService;
        this.tvTermin = tvTermin;
        this.aenderungsId = aenderungsId;
    }


    @Override
    public void doExecute(TestContext context) {
        GF originalWbciGf = (GF) getWbciGeschaeftsfall(context, wbciCommonService);

        final TerminverschiebungsAnfrage terminverschiebung = new TerminverschiebungsAnfrageBuilder()
                .withAenderungsId(aenderungsId).withTvTermin(tvTermin != null ? tvTermin : null).build();
        TerminverschiebungsAnfrage<GF> tv = wbciTvService.createWbciTv(terminverschiebung, originalWbciGf.getVorabstimmungsId());
        Assert.assertTrue(tv.getId() != null);
        Assert.assertTrue(tv.getAenderungsId() != null);
        context.setVariable(VariableNames.CHANGE_ID, tv.getAenderungsId());
        context.setVariable(VariableNames.RESCHEDULED_CUSTOMER_DATE, WbciDateUtils.formatToWbciDate(tv.getTvTermin().atStartOfDay()));
    }

}
