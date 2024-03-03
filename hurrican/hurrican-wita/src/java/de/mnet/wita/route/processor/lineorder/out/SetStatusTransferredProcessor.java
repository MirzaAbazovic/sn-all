/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.10.2014
 */
package de.mnet.wita.route.processor.lineorder.out;

import static de.augustakom.hurrican.model.cc.tal.CBVorgang.*;

import javax.annotation.*;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.service.cc.CarrierElTALService;
import de.mnet.common.route.HurricanOutProcessor;
import de.mnet.wbci.route.WbciCamelConstants;
import de.mnet.wita.OutgoingWitaMessage;

/**
 *
 */
@Component
public class SetStatusTransferredProcessor extends HurricanOutProcessor implements WbciCamelConstants {

    @Resource(name = "de.augustakom.hurrican.service.cc.CarrierElTALService")
    private CarrierElTALService carrierElTalService;

    @Override
    public void process(Exchange exchange) throws Exception {
        OutgoingWitaMessage message = getOriginalMessage(exchange);
        String externeAuftragsnummer = message.getExterneAuftragsnummer();
        CBVorgang cbVorgang = carrierElTalService.findCBVorgangByCarrierRefNr(externeAuftragsnummer);
        if ((cbVorgang != null) && (cbVorgang.getStatus() < STATUS_TRANSFERRED)) {
            cbVorgang.setStatus(STATUS_TRANSFERRED);
            carrierElTalService.saveCBVorgang(cbVorgang);
        }
    }

}
