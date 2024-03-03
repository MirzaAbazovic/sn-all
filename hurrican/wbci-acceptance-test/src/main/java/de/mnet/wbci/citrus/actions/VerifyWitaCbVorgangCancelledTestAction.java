/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.11.14
 */
package de.mnet.wbci.citrus.actions;

import java.util.*;

import org.testng.Assert;

import com.consol.citrus.context.TestContext;

import de.mnet.wita.message.MnetWitaRequest;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.service.MwfEntityService;
import de.mnet.wita.service.WitaTalOrderService;

/**
 * TestAction prueft, dass zu der WITA Vorgang zur aktuellen VA-Id storniert ist.
 */
public class VerifyWitaCbVorgangCancelledTestAction extends AbstractWbciTestAction {

    private WitaTalOrderService witaTalOrderService;
    private MwfEntityService mwfEntityService;

    public VerifyWitaCbVorgangCancelledTestAction(WitaTalOrderService witaTalOrderService, MwfEntityService mwfEntityService) {
        super("verifyWitaCbVorgangCancelled");
        this.witaTalOrderService = witaTalOrderService;
        this.mwfEntityService = mwfEntityService;
    }

    @Override
    public void doExecute(TestContext testContext) {
        try {
            String vorabstimmungsId = getVorabstimmungsId(testContext);
            List<WitaCBVorgang> result = witaTalOrderService.findCBVorgaengeByVorabstimmungsId(vorabstimmungsId);
            Assert.assertNotNull(result, String.format("keinen WITA-Vorgang zu %s gefunden", vorabstimmungsId));
            Assert.assertEquals(result.size(), 1,
                    String.format("Anzahl gefundener WITA-Vorgaenge zu VA %s ungueltig!", vorabstimmungsId));

            String carrierRefNr = result.get(0).getCarrierRefNr();
            
            MnetWitaRequest example = MnetWitaRequest.createEmptyRequestForExample();
            example.setExterneAuftragsnummer(carrierRefNr);
            
            List<MnetWitaRequest> mnetWitaRequests = mwfEntityService.findMwfEntitiesByExample(example);
            Assert.assertNotNull(mnetWitaRequests, String.format("keine MnetWitaRequest Instanz zu %s gefunden", carrierRefNr));
            Assert.assertEquals(mnetWitaRequests.size(), 1,
                    String.format("Anzahl gefundener MnetWitaRequests zu %s ungueltig!", carrierRefNr));
            
            MnetWitaRequest witaRequest = mnetWitaRequests.get(0);
            Assert.assertTrue(witaRequest.isStorno() || witaRequest.getRequestWurdeStorniert());
        }
        catch (Exception e) {
            Assert.fail(String.format(
                    "Error occured at verify WITA request cancelled: %s", e.getMessage()));
        }
    }

}
