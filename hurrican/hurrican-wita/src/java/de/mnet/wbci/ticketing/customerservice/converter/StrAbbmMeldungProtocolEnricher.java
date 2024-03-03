/* 
 * Copyright (c) 2013 - M-net Telekommunikations GmbH 
 * All rights reserved. 
 * ------------------------------------------------------- 
 * File created: 06.11.13 
 */
package de.mnet.wbci.ticketing.customerservice.converter;

import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.customer.customerservice.v1.AddCommunication;
import de.mnet.wbci.model.Abbruchmeldung;
import de.mnet.wbci.model.AbbruchmeldungStornoAen;
import de.mnet.wbci.model.AbbruchmeldungStornoAuf;
import de.mnet.wbci.model.WbciMessage;

@Component
public class StrAbbmMeldungProtocolEnricher extends AbstractCustomerServiceProtocolEnricher<Abbruchmeldung> {

    @Override
    public boolean supports(WbciMessage wbciMessage) {
        return wbciMessage instanceof AbbruchmeldungStornoAen || wbciMessage instanceof AbbruchmeldungStornoAuf;
    }

    protected void enrichOutboundMessage(Abbruchmeldung abbmStr, AddCommunication csProtocol) {
        csProtocol.setNotes(outboundMessageCommentGenerator.getStornoAbbmBemerkung(
                getOtherPartyCarrierCode(abbmStr),
                getMeldungsText(abbmStr),
                getAbbruchmeldungStornoId(abbmStr),
                getVaId(abbmStr)));
    }

    protected void enrichInboundMessage(Abbruchmeldung abbmStr, AddCommunication csProtocol) {
        csProtocol.setNotes(inboundMessageCommentGenerator.getStornoAbbmBemerkung(
                getOtherPartyCarrierCode(abbmStr),
                getMeldungsText(abbmStr),
                getAbbruchmeldungStornoId(abbmStr),
                getVaId(abbmStr)));
    }
}
