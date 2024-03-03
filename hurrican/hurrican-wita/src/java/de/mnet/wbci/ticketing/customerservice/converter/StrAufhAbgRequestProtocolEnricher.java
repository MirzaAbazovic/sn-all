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
import de.mnet.wbci.model.StornoAufhebungAbgAnfrage;
import de.mnet.wbci.model.WbciMessage;

@Component
public class StrAufhAbgRequestProtocolEnricher extends AbstractCustomerServiceProtocolEnricher<StornoAufhebungAbgAnfrage> {

    @Override
    public boolean supports(WbciMessage wbciMessage) {
        return RequestTyp.STR_AUFH_ABG.equals(wbciMessage.getTyp());
    }

    protected void enrichOutboundMessage(StornoAufhebungAbgAnfrage storno, AddCommunication csProtocol) {
        csProtocol.setNotes(outboundMessageCommentGenerator.getStrAufhAbgBemerkung(
                        getOtherPartyCarrierCode(storno),
                        getStornoGrund(storno),
                        getStornoReqAenderungsId(storno),
                        getVaId(storno))
        );
    }

    protected void enrichInboundMessage(StornoAufhebungAbgAnfrage storno, AddCommunication csProtocol) {
        csProtocol.setNotes(inboundMessageCommentGenerator.getStrAufhAbgBemerkung(
                        getOtherPartyCarrierCode(storno),
                        getStornoGrund(storno),
                        getStornoReqAenderungsId(storno),
                        getVaId(storno))
        );
    }

    private String getStornoGrund(StornoAufhebungAbgAnfrage str) {
        return str.getStornoGrund();
    }


}
