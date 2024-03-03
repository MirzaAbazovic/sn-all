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
import de.mnet.wbci.model.StornoAenderungAbgAnfrage;
import de.mnet.wbci.model.WbciMessage;

@Component
public class StrAenAbgRequestProtocolEnricher extends AbstractCustomerServiceProtocolEnricher<StornoAenderungAbgAnfrage> {

    @Override
    public boolean supports(WbciMessage wbciMessage) {
        return RequestTyp.STR_AEN_ABG.equals(wbciMessage.getTyp());
    }

    protected void enrichOutboundMessage(StornoAenderungAbgAnfrage storno, AddCommunication csProtocol) {
        csProtocol.setNotes(outboundMessageCommentGenerator.getStrAenAbgBemerkung(
                        getOtherPartyCarrierCode(storno),
                        getStornoGrund(storno),
                        getStornoReqAenderungsId(storno),
                        getVaId(storno))
        );
    }

    protected void enrichInboundMessage(StornoAenderungAbgAnfrage storno, AddCommunication csProtocol) {
        csProtocol.setNotes(inboundMessageCommentGenerator.getStrAenAbgBemerkung(
                        getOtherPartyCarrierCode(storno),
                        getStornoGrund(storno),
                        getStornoReqAenderungsId(storno),
                        getVaId(storno))
        );
    }

    private String getStornoGrund(StornoAenderungAbgAnfrage str) {
        return str.getStornoGrund();
    }

}
