/* 
 * Copyright (c) 2013 - M-net Telekommunikations GmbH 
 * All rights reserved. 
 * ------------------------------------------------------- 
 * File created: 06.11.13 
 */
package de.mnet.wbci.ticketing.customerservice.converter;

import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.customer.customerservice.v1.AddCommunication;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciMessage;


@Component
public class VaRequestProtocolEnricher extends AbstractCustomerServiceProtocolEnricher<VorabstimmungsAnfrage> {

    @Override
    public boolean supports(WbciMessage wbciMessage) {
        return RequestTyp.VA.equals(wbciMessage.getTyp());
    }

    protected void enrichOutboundMessage(VorabstimmungsAnfrage va, AddCommunication csProtocol) {
        csProtocol.setNotes(outboundMessageCommentGenerator.getVaBemerkung(
                        getGfTyp(va),
                        getOtherPartyCarrierCode(va),
                        getKundenwunschtermin(va),
                        getVaId(va))
        );
    }

    protected void enrichInboundMessage(VorabstimmungsAnfrage va, AddCommunication csProtocol) {
        csProtocol.setNotes(inboundMessageCommentGenerator.getVaBemerkung(
                        getGfTyp(va),
                        getOtherPartyCarrierCode(va),
                        getKundenwunschtermin(va),
                        getVaId(va))
        );
    }
}
