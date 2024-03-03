/* 
 * Copyright (c) 2013 - M-net Telekommunikations GmbH 
 * All rights reserved. 
 * ------------------------------------------------------- 
 * File created: 06.11.13 
 */
package de.mnet.wbci.ticketing.customerservice.converter;

import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.customer.customerservice.v1.AddCommunication;
import de.mnet.wbci.model.MeldungTyp;
import de.mnet.wbci.model.UebernahmeRessourceMeldung;
import de.mnet.wbci.model.WbciMessage;

@Component
public class AkmTrMeldungProtocolEnricher extends AbstractCustomerServiceProtocolEnricher<UebernahmeRessourceMeldung> {

    @Override
    public boolean supports(WbciMessage wbciMessage) {
        return MeldungTyp.AKM_TR.equals(wbciMessage.getTyp());
    }

    protected void enrichOutboundMessage(UebernahmeRessourceMeldung akmTr, AddCommunication csProtocol) {
        csProtocol.setNotes(outboundMessageCommentGenerator.getAkmTrBemerkung(
                        getOtherPartyCarrierCode(akmTr),
                        isUebernahme(akmTr),
                        getVaId(akmTr))
        );
    }

    protected void enrichInboundMessage(UebernahmeRessourceMeldung akmTr, AddCommunication csProtocol) {
        csProtocol.setNotes(inboundMessageCommentGenerator.getAkmTrBemerkung(
                        getOtherPartyCarrierCode(akmTr),
                        isUebernahme(akmTr),
                        getVaId(akmTr))
        );
    }

    private Boolean isUebernahme(UebernahmeRessourceMeldung akmTr) {
        return akmTr.isUebernahme();
    }


}
