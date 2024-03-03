/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.12.2014
 */
package de.mnet.hurrican.atlas.simulator.wita.actions;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CarrierElTALService;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.service.WitaTalOrderService;
import de.mnet.hurrican.atlas.simulator.wita.WitaLineOrderVariableNames;

/**
 *
 */
public class CloseCbVorgangAction extends AbstractWitaTalTestAction {

    /** Logger */
    private static final Logger LOG = LoggerFactory.getLogger(CloseCbVorgangAction.class);
    private final WitaTalOrderService witaTalOrderService;

    public CloseCbVorgangAction(CarrierElTALService carrierElTalService, WitaTalOrderService witaTalOrderService) {
        super("closeCbVorgangAction", carrierElTalService);
        this.witaTalOrderService = witaTalOrderService;
    }

    @Override
    public void doExecute(TestContext context) {
        String externalOrderId = context.getVariable(WitaLineOrderVariableNames.EXTERNAL_ORDER_ID);

        WitaCBVorgang witaCbVorgang = findWitaCbVorgang(externalOrderId);
        if (witaCbVorgang == null) {
            throw new CitrusRuntimeException(
                    String.format("No WitaCB-Vorgang found for externalOrderId '%s'", externalOrderId));
        }
        try {
            witaTalOrderService.closeCBVorgang(witaCbVorgang.getId(), getSessionId());
            LOG.info("WitaCB-Vorgang with externalOrderId '{}' closed successful", externalOrderId);
        }
        catch (StoreException | ValidationException e) {
            throw new CitrusRuntimeException(String.format("Can't close WitaCB-Vorgang with externalOrderId '%s'!" + externalOrderId, e));
        }
    }

}
