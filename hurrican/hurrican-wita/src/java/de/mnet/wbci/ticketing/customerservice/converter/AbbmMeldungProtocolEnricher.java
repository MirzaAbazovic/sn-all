/* 
 * Copyright (c) 2013 - M-net Telekommunikations GmbH 
 * All rights reserved. 
 * ------------------------------------------------------- 
 * File created: 13.11.13 
 */
package de.mnet.wbci.ticketing.customerservice.converter;

import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.customer.customerservice.v1.AddCommunication;
import de.mnet.wbci.model.Abbruchmeldung;
import de.mnet.wbci.model.MeldungTyp;
import de.mnet.wbci.model.WbciMessage;

@Component
public class AbbmMeldungProtocolEnricher extends AbstractCustomerServiceProtocolEnricher<Abbruchmeldung> {

    @Override
    public boolean supports(WbciMessage wbciMessage) {
        return MeldungTyp.ABBM.equals(wbciMessage.getTyp());
    }

    protected void enrichOutboundMessage(Abbruchmeldung abbm, AddCommunication csProtocol) {
        csProtocol.setNotes(outboundMessageCommentGenerator.getAbbmBemerkung(
                        getOtherPartyCarrierCode(abbm),
                        getMeldungsText(abbm),
                        getVaId(abbm))
        );
    }

    protected void enrichInboundMessage(Abbruchmeldung abbm, AddCommunication csProtocol) {
        csProtocol.setNotes(inboundMessageCommentGenerator.getAbbmBemerkung(
                        getOtherPartyCarrierCode(abbm),
                        getMeldungsText(abbm),
                        getVaId(abbm))
        );
    }

}
