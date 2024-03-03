/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.11.13
 */
package de.mnet.wbci.citrus.actions;

import com.consol.citrus.context.TestContext;
import org.testng.Assert;

import de.mnet.wbci.citrus.VariableNames;
import de.mnet.wbci.model.KundenTyp;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.service.WbciCommonService;

/**
 * Citrus TestAction um zu ueberpruefen, ob ein eingehender WBCI Request (bzw. der zugehoerige Geschaeftsfall) einem
 * bestimmten Taifun Auftrag zugeordnet wurde.
 */
public class VerifyWbciIncomingRequestAssignedToOrderAction extends AbstractWbciTestAction {

    private KundenTyp customerTyp;
    private WbciCommonService wbciCommonService;
    private Long taifunOrderNoOrig;

    public VerifyWbciIncomingRequestAssignedToOrderAction(WbciCommonService wbciCommonService, Long taifunOrderNoOrig) {
        super("verifyWbciIncomingRequestAssignedToOrder");
        this.wbciCommonService = wbciCommonService;
        this.taifunOrderNoOrig = taifunOrderNoOrig;
    }

    public VerifyWbciIncomingRequestAssignedToOrderAction(WbciCommonService wbciCommonService, Long taifunOrderNoOrig, KundenTyp customerTyp) {
        this(wbciCommonService, taifunOrderNoOrig);
        this.customerTyp = customerTyp;
    }

    @Override
    public void doExecute(TestContext context) {
        String vorabstimmungsId = context.getVariable(VariableNames.PRE_AGREEMENT_ID);
        Assert.assertNotNull(vorabstimmungsId);

        final WbciGeschaeftsfall wbciGeschaeftsfall = wbciCommonService.findWbciGeschaeftsfall(vorabstimmungsId);
        Assert.assertEquals(wbciGeschaeftsfall.getBillingOrderNoOrig(), taifunOrderNoOrig,
                String.format("WbciGeschaeftsfall with VorabstimmungsId '%s' not assigned to billing order %s",
                        vorabstimmungsId, taifunOrderNoOrig)
        );
        Assert.assertNotNull(wbciGeschaeftsfall.getEndkunde().getKundenTyp(),
                String.format("WbciGeschaeftsfall with VorabstimmungsId '%s' has no customer typ (PK or GK) assigned - dependent billing order: %s.",
                        vorabstimmungsId, taifunOrderNoOrig)
        );
        if (customerTyp != null) {
            Assert.assertEquals(wbciGeschaeftsfall.getEndkunde().getKundenTyp(), customerTyp,
                    String.format("WbciGeschaeftsfall with VorabstimmungsId '%s' expect the customer typ '%s'  but found '%s' as assigned customer typ - dependent billing order: %s.",
                            vorabstimmungsId, customerTyp, wbciGeschaeftsfall.getEndkunde().getKundenTyp(), taifunOrderNoOrig)
            );
        }
    }

}
