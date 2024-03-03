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
import de.mnet.wbci.model.StornoAufhebungAufAnfrage;
import de.mnet.wbci.model.WbciMessage;

@Component
public class StrAufhAufRequestProtocolEnricher extends AbstractCustomerServiceProtocolEnricher<StornoAufhebungAufAnfrage> {

    @Override
    public boolean supports(WbciMessage wbciMessage) {
        return RequestTyp.STR_AUFH_AUF.equals(wbciMessage.getTyp());
    }

    protected void enrichOutboundMessage(StornoAufhebungAufAnfrage storno, AddCommunication csProtocol) {
        csProtocol.setNotes(outboundMessageCommentGenerator.getStrAufhAufBemerkung(
                        getOtherPartyCarrierCode(storno),
                        getStornoReqAenderungsId(storno),
                        getVaId(storno))
        );
    }

    protected void enrichInboundMessage(StornoAufhebungAufAnfrage storno, AddCommunication csProtocol) {
        csProtocol.setNotes(inboundMessageCommentGenerator.getStrAufhAufBemerkung(
                        getOtherPartyCarrierCode(storno),
                        getStornoReqAenderungsId(storno),
                        getVaId(storno))
        );
    }
}
