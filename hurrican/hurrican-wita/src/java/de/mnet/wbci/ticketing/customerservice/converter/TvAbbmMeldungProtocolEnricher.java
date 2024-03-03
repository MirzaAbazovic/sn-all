/* 
 * Copyright (c) 2013 - M-net Telekommunikations GmbH 
 * All rights reserved. 
 * ------------------------------------------------------- 
 * File created: 06.11.13 
 */
package de.mnet.wbci.ticketing.customerservice.converter;

import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.customer.customerservice.v1.AddCommunication;
import de.mnet.wbci.model.AbbruchmeldungTerminverschiebung;
import de.mnet.wbci.model.WbciMessage;


@Component
public class TvAbbmMeldungProtocolEnricher extends AbstractCustomerServiceProtocolEnricher<AbbruchmeldungTerminverschiebung> {

    @Override
    public boolean supports(WbciMessage wbciMessage) {
        return wbciMessage instanceof AbbruchmeldungTerminverschiebung;
    }

    protected void enrichOutboundMessage(AbbruchmeldungTerminverschiebung abbmTv, AddCommunication csProtocol) {
        csProtocol.setNotes(outboundMessageCommentGenerator.getTvAbbmBemerkung(
                getOtherPartyCarrierCode(abbmTv),
                getMeldungsText(abbmTv),
                getAbbruchmeldungAenderungsId(abbmTv),
                getVaId(abbmTv)));
    }

    protected void enrichInboundMessage(AbbruchmeldungTerminverschiebung abbmTv, AddCommunication csProtocol) {
        csProtocol.setNotes(inboundMessageCommentGenerator.getTvAbbmBemerkung(
                getOtherPartyCarrierCode(abbmTv),
                getMeldungsText(abbmTv),
                getAbbruchmeldungAenderungsId(abbmTv),
                getVaId(abbmTv)));
    }
}
