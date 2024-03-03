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
import de.mnet.wbci.model.StornoAenderungAufAnfrage;
import de.mnet.wbci.model.WbciMessage;

@Component
public class StrAenAufRequestProtocolEnricher extends AbstractCustomerServiceProtocolEnricher<StornoAenderungAufAnfrage> {

    @Override
    public boolean supports(WbciMessage wbciMessage) {
        return RequestTyp.STR_AEN_AUF.equals(wbciMessage.getTyp());
    }

    protected void enrichOutboundMessage(StornoAenderungAufAnfrage storno, AddCommunication csProtocol) {
        csProtocol.setNotes(outboundMessageCommentGenerator.getStrAenAufBemerkung(
                        getOtherPartyCarrierCode(storno),
                        getStornoReqAenderungsId(storno),
                        getVaId(storno))
        );
    }

    protected void enrichInboundMessage(StornoAenderungAufAnfrage storno, AddCommunication csProtocol) {
        csProtocol.setNotes(inboundMessageCommentGenerator.getStrAenAufBemerkung(
                        getOtherPartyCarrierCode(storno),
                        getStornoReqAenderungsId(storno),
                        getVaId(storno))
        );
    }
}
