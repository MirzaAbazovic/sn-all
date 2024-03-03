/*

 * Copyright (c) 2017 - M-net Telekommunikations GmbH

 * All rights reserved.

 * -------------------------------------------------------

 * File created: 03.02.2017

 */

package de.mnet.hurrican.wholesale.ws.outbound.mapper;


import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.complex.WunschterminType;
import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.order.ProviderwechselTermineType;
import de.mnet.wbci.model.WbciGeschaeftsfall;

/**
 * Maps WBCI PreAgreement data to WholesaleProviderwechsel Wunschtermin
 * <p>
 * Created by wieran on 03.02.2017.
 */
@Component("WholesaleProviderwechselTermineMapper")
public class WholesaleProviderwechselTermineMapper {

    /**
     * Maps WBCI PreAgreement data to WholesaleProviderwechsel Wunschtermin
     *
     * @param wbciGeschaeftsfall The {@link WbciGeschaeftsfall} to map. Mandatory.
     * @return The mapped {@link ProviderwechselTermineType}.
     */
    public ProviderwechselTermineType createPvTermine(WbciGeschaeftsfall wbciGeschaeftsfall) {
        ProviderwechselTermineType termine = new ProviderwechselTermineType();
        termine.setAuftraggeberWunschtermin(createWunschterminType(wbciGeschaeftsfall));
        // Zeitfenster nicht notwendig
        return termine;
    }

    private WunschterminType createWunschterminType(WbciGeschaeftsfall wbciGeschaeftsfall) {
        WunschterminType wunschtermin = new WunschterminType();
        wunschtermin.setDatum(wbciGeschaeftsfall.getKundenwunschtermin());
        return wunschtermin;
    }
}
