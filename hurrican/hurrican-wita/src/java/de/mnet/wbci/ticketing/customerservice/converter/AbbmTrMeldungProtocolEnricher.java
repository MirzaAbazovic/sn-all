/* 
 * Copyright (c) 2013 - M-net Telekommunikations GmbH 
 * All rights reserved. 
 * ------------------------------------------------------- 
 * File created: 06.11.13 
 */
package de.mnet.wbci.ticketing.customerservice.converter;

import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.customer.customerservice.v1.AddCommunication;
import de.mnet.wbci.model.AbbruchmeldungTechnRessource;
import de.mnet.wbci.model.MeldungTyp;
import de.mnet.wbci.model.WbciMessage;

@Component
public class AbbmTrMeldungProtocolEnricher extends AbstractCustomerServiceProtocolEnricher<AbbruchmeldungTechnRessource> {

    @Override
    public boolean supports(WbciMessage wbciMessage) {
        return MeldungTyp.ABBM_TR.equals(wbciMessage.getTyp());
    }

    protected void enrichOutboundMessage(AbbruchmeldungTechnRessource abbmTr, AddCommunication csProtocol) {
        csProtocol.setNotes(outboundMessageCommentGenerator.getAbbmTrBemerkung(
                        getOtherPartyCarrierCode(abbmTr),
                        getMeldungsText(abbmTr),
                        getVaId(abbmTr))
        );
    }

    protected void enrichInboundMessage(AbbruchmeldungTechnRessource abbmTr, AddCommunication csProtocol) {
        csProtocol.setNotes(inboundMessageCommentGenerator.getAbbmTrBemerkung(
                        getOtherPartyCarrierCode(abbmTr),
                        getMeldungsText(abbmTr),
                        getVaId(abbmTr))
        );
    }

}
