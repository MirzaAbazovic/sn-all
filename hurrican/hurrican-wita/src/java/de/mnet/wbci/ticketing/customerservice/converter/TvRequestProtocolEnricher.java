/* 
 * Copyright (c) 2013 - M-net Telekommunikations GmbH 
 * All rights reserved. 
 * ------------------------------------------------------- 
 * File created: 06.11.13 
 */
package de.mnet.wbci.ticketing.customerservice.converter;

import java.time.*;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.customer.customerservice.v1.AddCommunication;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.TerminverschiebungsAnfrage;
import de.mnet.wbci.model.WbciMessage;

@Component
public class TvRequestProtocolEnricher extends AbstractCustomerServiceProtocolEnricher<TerminverschiebungsAnfrage> {

    @Override
    public boolean supports(WbciMessage wbciMessage) {
        return RequestTyp.TV.equals(wbciMessage.getTyp());
    }

    protected void enrichOutboundMessage(TerminverschiebungsAnfrage tv, AddCommunication csProtocol) {
        csProtocol.setNotes(outboundMessageCommentGenerator.getTvBemerkung(
                        getOtherPartyCarrierCode(tv),
                        getNewKundenwunschtermin(tv),
                        getAenderungsId(tv),
                        getVaId(tv))
        );
    }

    protected void enrichInboundMessage(TerminverschiebungsAnfrage tv, AddCommunication csProtocol) {
        csProtocol.setNotes(inboundMessageCommentGenerator.getTvBemerkung(
                        getOtherPartyCarrierCode(tv),
                        getNewKundenwunschtermin(tv),
                        getAenderungsId(tv),
                        getVaId(tv))
        );
    }

    private LocalDateTime getNewKundenwunschtermin(TerminverschiebungsAnfrage tv) {
        return tv.getTvTermin().atStartOfDay();
    }

    private String getAenderungsId(TerminverschiebungsAnfrage tv) {
        return tv.getAenderungsId();
    }
}
