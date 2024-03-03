/* 
 * Copyright (c) 2013 - M-net Telekommunikations GmbH 
 * All rights reserved. 
 * ------------------------------------------------------- 
 * File created: 06.11.13 
 */
package de.mnet.wbci.ticketing.customerservice.converter;

import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.customer.customerservice.v1.AddCommunication;
import de.mnet.wbci.model.ErledigtmeldungTerminverschiebung;
import de.mnet.wbci.model.WbciMessage;

@Component
public class TvErlmMeldungProtocolEnricher extends AbstractCustomerServiceProtocolEnricher<ErledigtmeldungTerminverschiebung> {

    @Override
    public boolean supports(WbciMessage wbciMessage) {
        return wbciMessage instanceof ErledigtmeldungTerminverschiebung;
    }

    protected void enrichOutboundMessage(ErledigtmeldungTerminverschiebung erlmTv, AddCommunication csProtocol) {
        csProtocol.setNotes(outboundMessageCommentGenerator.getTvErlmBemerkung(
                        getOtherPartyCarrierCode(erlmTv),
                        getKundenwunschtermin(erlmTv),
                        getErledigtMeldungAenderungsId(erlmTv),
                        getVaId(erlmTv))
        );
    }

    protected void enrichInboundMessage(ErledigtmeldungTerminverschiebung erlmTv, AddCommunication csProtocol) {
        csProtocol.setNotes(inboundMessageCommentGenerator.getTvErlmBemerkung(
                        getOtherPartyCarrierCode(erlmTv),
                        getKundenwunschtermin(erlmTv),
                        getErledigtMeldungAenderungsId(erlmTv),
                        getVaId(erlmTv))
        );
    }
}
