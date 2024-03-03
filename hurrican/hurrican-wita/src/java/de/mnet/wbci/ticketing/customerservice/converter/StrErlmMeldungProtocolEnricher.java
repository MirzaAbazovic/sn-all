/* 
 * Copyright (c) 2013 - M-net Telekommunikations GmbH 
 * All rights reserved. 
 * ------------------------------------------------------- 
 * File created: 06.11.13 
 */
package de.mnet.wbci.ticketing.customerservice.converter;

import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.customer.customerservice.v1.AddCommunication;
import de.mnet.wbci.model.Erledigtmeldung;
import de.mnet.wbci.model.ErledigtmeldungStornoAen;
import de.mnet.wbci.model.ErledigtmeldungStornoAuf;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciMessage;


@Component
public class StrErlmMeldungProtocolEnricher extends AbstractCustomerServiceProtocolEnricher<Erledigtmeldung> {

    @Override
    public boolean supports(WbciMessage wbciMessage) {
        return (wbciMessage instanceof ErledigtmeldungStornoAen && !hasGeschaeftsfallStatus(wbciMessage, WbciGeschaeftsfallStatus.NEW_VA_EXPIRED))
                || wbciMessage instanceof ErledigtmeldungStornoAuf;
    }

    protected void enrichOutboundMessage(Erledigtmeldung erlmStr, AddCommunication csProtocol) {
        csProtocol.setNotes(outboundMessageCommentGenerator.getStornoErlmBemerkung(
                getOtherPartyCarrierCode(erlmStr),
                getErledigtMeldungStornoId(erlmStr),
                getVaId(erlmStr)));
    }

    protected void enrichInboundMessage(Erledigtmeldung erlmStr, AddCommunication csProtocol) {
        csProtocol.setNotes(inboundMessageCommentGenerator.getStornoErlmBemerkung(
                getOtherPartyCarrierCode(erlmStr),
                getErledigtMeldungStornoId(erlmStr),
                getVaId(erlmStr)));
    }
}
