/* 
 * Copyright (c) 2014 - M-net Telekommunikations GmbH 
 * All rights reserved. 
 * ------------------------------------------------------- 
 * File created: 01.07.2014 
 */
package de.mnet.wbci.ticketing.customerservice.converter;

import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.customer.customerservice.v1.AddCommunication;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.Erledigtmeldung;
import de.mnet.wbci.model.ErledigtmeldungStornoAen;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciMessage;

@Component
public class NewVaExpiredProtocolEnricher extends AbstractCustomerServiceProtocolEnricher<Erledigtmeldung> {

    @Override
    public boolean supports(WbciMessage wbciMessage) {
        return wbciMessage instanceof ErledigtmeldungStornoAen && hasGeschaeftsfallStatus(wbciMessage, WbciGeschaeftsfallStatus.NEW_VA_EXPIRED);
    }

    protected void enrichOutboundMessage(Erledigtmeldung erlmStr, AddCommunication csProtocol) {
        csProtocol.setNotes(getNotes(erlmStr));
    }

    protected void enrichInboundMessage(Erledigtmeldung erlmStr, AddCommunication csProtocol) {
        csProtocol.setNotes(getNotes(erlmStr));
    }

    private String getNotes(Erledigtmeldung erlmStr) {
        CarrierCode carrierCode = getOtherPartyCarrierCode(erlmStr);
        String vaId = getVaId(erlmStr);
        if (erlmStr.getWbciGeschaeftsfall().isMNetReceivingCarrier()) {
            return newVaExpiredCommentGenerator.getNewVaExpiredReceivingBemerkung(carrierCode, vaId);
        }
        else {
            return newVaExpiredCommentGenerator.getNewVaExpiredDonatingBemerkung(carrierCode, vaId);
        }
    }
}
