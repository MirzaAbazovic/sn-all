/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.02.2012 11:22:44
 */
package de.mnet.wita.ticketing.converter;

import java.time.*;

import de.mnet.common.tools.DateConverterUtils;
import de.mnet.esb.cdm.customer.customerservice.v1.AddCommunication;
import de.mnet.wita.message.MnetWitaRequest;

public abstract class BaseRequestBsiProtokollConverter<T extends MnetWitaRequest> extends
        AbstractMwfBsiProtokollConverter<T> {

    protected AddCommunication createRequestProtokollEintrag(T request) {
        AddCommunication protokollEintrag = createWitaProtokollEintrag();

        if (request.getSentAt() != null) {
            protokollEintrag.setTime(DateConverterUtils.toXmlGregorianCalendar(request.getSentAt()));
        } else {
            protokollEintrag.setTime(DateConverterUtils.toXmlGregorianCalendar(request.getMwfCreationDate()));
        }
        return protokollEintrag;
    }

    @Override
    public Long findHurricanAuftragId(T request) {
        return findHurricanAuftragIdViaCbVorgang(request.getExterneAuftragsnummer());
    }
}
