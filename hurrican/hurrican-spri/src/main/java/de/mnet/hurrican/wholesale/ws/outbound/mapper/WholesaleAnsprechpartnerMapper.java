/*

 * Copyright (c) 2017 - M-net Telekommunikations GmbH

 * All rights reserved.

 * -------------------------------------------------------

 * File created: 03.02.2017

 */

package de.mnet.hurrican.wholesale.ws.outbound.mapper;


import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.order.AnsprechpartnerType;

/**
 * Maps static Ansprechpartner 'MNET' to WholesaleProviderwechsel Ansprechpartner 'Auftragsmanagement'
 * <p>
 * Created by wieran on 03.02.2017.
 */
@Component("WholesaleAnsprechpartnerMapper")
public class WholesaleAnsprechpartnerMapper {

    private static final String KEINE_ANREDE = "9";
    private static final String NACHNAME = "MNET";
    private static final String TELEFONNUMMER = "089 452000";

    /**
     * Maps static Ansprechpartner 'MNET' to WholesaleProviderwechsel Ansprechpartner 'Auftragsmanagement'
     *
     * @return The mapped {@link AnsprechpartnerType}.
     */
    public AnsprechpartnerType createAnsprechpartner() {
        AnsprechpartnerType ansprechpartner = new AnsprechpartnerType();
        ansprechpartner.setAuftragsmanagement(getAuftragsmanagement());
        return ansprechpartner;
    }

    private de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.complex.AnsprechpartnerType getAuftragsmanagement() {
        de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.complex.AnsprechpartnerType ansprechpartner = new de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.complex.AnsprechpartnerType();

        ansprechpartner.setAnrede(KEINE_ANREDE);
        ansprechpartner.setNachname(NACHNAME);
        ansprechpartner.setTelefonnummer(TELEFONNUMMER);

        return ansprechpartner;
    }
}
