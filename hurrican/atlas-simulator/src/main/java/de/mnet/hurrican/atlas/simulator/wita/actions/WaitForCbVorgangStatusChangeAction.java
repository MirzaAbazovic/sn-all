/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.12.2014
 */
package de.mnet.hurrican.atlas.simulator.wita.actions;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;

import de.augustakom.hurrican.service.cc.CarrierElTALService;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.hurrican.atlas.simulator.wita.WitaLineOrderVariableNames;

/**
 *
 */
public class WaitForCbVorgangStatusChangeAction extends AbstractWitaTalTestAction {

    private final Long expectedStatus;

    public WaitForCbVorgangStatusChangeAction(CarrierElTALService carrierElTalService, Long expectedStatus) {
        super("waitForCbVorgangStatusChange", carrierElTalService);
        this.expectedStatus = expectedStatus;
    }

    @Override
    public void doExecute(TestContext context) {
        String externalOrderId = context.getVariable(WitaLineOrderVariableNames.EXTERNAL_ORDER_ID);

        WitaCBVorgang witaCbVorgang = findWitaCbVorgang(externalOrderId);
        if (witaCbVorgang == null) {
            throw new CitrusRuntimeException(
                    String.format("No WitaCbVorgang found for externalOrderId '%s'", externalOrderId));
        }
        if (!expectedStatus.equals(witaCbVorgang.getStatus())) {
            throw new CitrusRuntimeException(
                    String.format("WitaCbVorgang with externalOrderId '%s' has status '%s' instead of '%s' as expected",
                            externalOrderId, witaCbVorgang.getStatus(), expectedStatus));
        }
    }

}
