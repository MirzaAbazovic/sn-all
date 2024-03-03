/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.12.14
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
 * TestAction, um eine WITA QEB zu erzeugen und dem WITA-Vorgang zuzuordnen, der widerum der
 * aktuellen VA zugeordnet ist..
 */
public class MarkMnetWitaRequestAsUnsent extends AbstractWbciTestAction {

    private WitaTalOrderService witaTalOrderService;
    private MwfEntityService mwfEntityService;

    public MarkMnetWitaRequestAsUnsent(WitaTalOrderService witaTalOrderService,
            MwfEntityService mwfEntityService) {
        
        super("fakeWitaQebTestAction");
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

            List<MnetWitaRequest> mwfRequests = mwfEntityService.findMwfEntitiesByProperty(
                    MnetWitaRequest.class, MnetWitaRequest.EXTERNE_AUFTRAGSNR_FIELD, result.get(0).getCarrierRefNr());
                    
            Assert.assertNotNull(mwfRequests);
            Assert.assertEquals(mwfRequests.size(), 1,
                    String.format("Anzahl gefundener MWF-Requests zu RefNr %s ungeultig!", 
                            result.get(0).getCarrierRefNr()));
            
            MnetWitaRequest requestToMarkAsUnsent = mwfRequests.get(0);
            requestToMarkAsUnsent.setSentAt(null);
            mwfEntityService.store(requestToMarkAsUnsent);
        }
        catch (Exception e) {
            Assert.fail(String.format(
                    "Error occured faking QEB for WITA: %s", e.getMessage()));
        }
    }

}
