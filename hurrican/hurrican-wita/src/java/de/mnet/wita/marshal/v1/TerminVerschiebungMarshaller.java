/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.2014
 */
package de.mnet.wita.marshal.v1;

import javax.xml.datatype.*;
import org.springframework.stereotype.Component;

import de.mnet.common.tools.DateConverterUtils;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.GeschaeftsfallTerminverschiebungType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.TerminverschiebungType;
import de.mnet.wita.message.TerminVerschiebung;
import de.mnet.wita.message.auftrag.Kundenwunschtermin;

@Component
public class TerminVerschiebungMarshaller extends RequestMarshaller<TerminVerschiebung, TerminverschiebungType> {

    @Override
    TerminverschiebungType createAuftragType(TerminVerschiebung input) {
        TerminverschiebungType terminverschiebungType = new TerminverschiebungType();
        GeschaeftsfallTerminverschiebungType type = new GeschaeftsfallTerminverschiebungType();
        terminverschiebungType.setGeschaeftsfallAenderung(type);

        XMLGregorianCalendar termin = DateConverterUtils.toXmlGregorianCalendar(input.getTermin());
        type.setKundenwunschtermin(termin);

        Kundenwunschtermin.Zeitfenster zeitfenster = input.getGeschaeftsfall().getKundenwunschtermin().getZeitfenster();
        if (zeitfenster != null) {
            type.setZeitfenster(zeitfenster.witaZeitfenster);
        }
        return terminverschiebungType;
    }

}
