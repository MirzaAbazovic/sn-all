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
import de.mnet.wbci.model.MeldungTyp;
import de.mnet.wbci.model.RueckmeldungVorabstimmung;
import de.mnet.wbci.model.WbciMessage;

@Component
public class RuemVaMeldungProtocolEnricher extends AbstractCustomerServiceProtocolEnricher<RueckmeldungVorabstimmung> {

    @Override
    public boolean supports(WbciMessage wbciMessage) {
        return MeldungTyp.RUEM_VA.equals(wbciMessage.getTyp());
    }

    protected void enrichOutboundMessage(RueckmeldungVorabstimmung rumeVa, AddCommunication csProtocol) {
        csProtocol.setNotes(outboundMessageCommentGenerator.getRuemVaBemerkung(
                        getOtherPartyCarrierCode(rumeVa),
                        getWechseltermin(rumeVa),
                        getVaId(rumeVa))
        );
    }

    protected void enrichInboundMessage(RueckmeldungVorabstimmung rumeVa, AddCommunication csProtocol) {
        csProtocol.setNotes(inboundMessageCommentGenerator.getRuemVaBemerkung(
                        getOtherPartyCarrierCode(rumeVa),
                        getWechseltermin(rumeVa),
                        getVaId(rumeVa))
        );
    }

    protected LocalDateTime getWechseltermin(RueckmeldungVorabstimmung rumeVa) {
        return rumeVa.getWechseltermin().atStartOfDay();
    }

}
