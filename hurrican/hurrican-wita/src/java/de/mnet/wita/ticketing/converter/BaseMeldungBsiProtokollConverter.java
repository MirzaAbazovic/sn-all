/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.02.2012 17:15:40
 */
package de.mnet.wita.ticketing.converter;

import java.time.*;

import de.mnet.common.tools.DateConverterUtils;
import de.mnet.esb.cdm.customer.customerservice.v1.AddCommunication;
import de.mnet.wita.message.meldung.Meldung;

public abstract class BaseMeldungBsiProtokollConverter<T extends Meldung<?>> extends
        AbstractMwfBsiProtokollConverter<T> {

    protected AddCommunication createMeldungProtokollEintrag(T meldung) {
        AddCommunication protokollEintrag = createWitaProtokollEintrag();
        protokollEintrag.setTime(DateConverterUtils.toXmlGregorianCalendar(meldung.getVersandZeitstempel()));
        return protokollEintrag;
    }

    @Override
    public Long findHurricanAuftragId(T meldung) {
        return findHurricanAuftragIdViaCbVorgang(meldung.getExterneAuftragsnummer());
    }

}
