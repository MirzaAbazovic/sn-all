/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.01.2015
 */
package de.mnet.hurrican.atlas.simulator.wita.actions;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import org.apache.commons.lang.StringUtils;

import de.augustakom.hurrican.service.cc.CarrierElTALService;
import de.mnet.wita.model.WitaCBVorgang;

/**
 *
 */
public class WaitForCbVorgangCreationAction extends AbstractWitaTestAction {

    private String externalOrderId;
    private CarrierElTALService carrierElTALService;

    public WaitForCbVorgangCreationAction(String externalOrderId, CarrierElTALService carrierElTALService) {
        super("WaitForCbVorgangCreationAction");
        this.externalOrderId = externalOrderId;
        assert !StringUtils.isEmpty(externalOrderId);
        this.carrierElTALService = carrierElTALService;
    }

    @Override
    public void doExecute(TestContext context) {
        WitaCBVorgang cbVorgang = (WitaCBVorgang) carrierElTALService.findCBVorgangByCarrierRefNr(externalOrderId);
        if (cbVorgang == null) {
            throw new CitrusRuntimeException(String.format("search wita cb vorgang with externalOrderId '%s'", externalOrderId));
        }
    }

}
