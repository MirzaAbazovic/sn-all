/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.11.14
 */
package de.mnet.wbci.citrus.actions;

import java.util.*;
import com.consol.citrus.context.TestContext;
import org.testng.Assert;

import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.service.WitaTalOrderService;

/**
 * TestAction, um einen WITA-Vorgang zu einer VA-Id zu ermitteln und den Typ zu pruefen.
 */
public class VerifyWitaCbVorgangExistTestAction extends AbstractWbciTestAction {

    private WitaTalOrderService witaTalOrderService;
    private Long cbVorgangTyp;

    public VerifyWitaCbVorgangExistTestAction(final Long cbVorgangTyp, WitaTalOrderService witaTalOrderService) {
        super("verifyWitaCbVorgangExist");
        this.witaTalOrderService = witaTalOrderService;
        this.cbVorgangTyp = cbVorgangTyp;
    }

    @Override
    public void doExecute(TestContext testContext) {
        try {
            String vorabstimmungsId = getVorabstimmungsId(testContext);
            List<WitaCBVorgang> result = witaTalOrderService.findCBVorgaengeByVorabstimmungsId(vorabstimmungsId);
            Assert.assertNotNull(result, String.format("keinen WITA-Vorgang zu %s gefunden", vorabstimmungsId));
            Assert.assertEquals(result.size(), 1,
                    String.format("Anzahl gefundener WITA-Vorgaenge zu VA %s ungueltig!", vorabstimmungsId));

            Assert.assertEquals(result.get(0).getTyp(), cbVorgangTyp,
                    String.format(
                            "WITA Vorgang %s zu %s nicht vom erwarteten Typ!",
                            result.get(0).getCarrierRefNr(),
                            vorabstimmungsId));
        }
        catch (Exception e) {
            Assert.fail(String.format(
                    "Error occured at witaTalOrderService.findCBVorgaengeByVorabstimmungsId: %s", e.getMessage()));
        }
    }

}
