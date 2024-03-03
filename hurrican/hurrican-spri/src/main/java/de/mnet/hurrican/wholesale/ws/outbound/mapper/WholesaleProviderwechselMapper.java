/*

 * Copyright (c) 2017 - M-net Telekommunikations GmbH

 * All rights reserved.

 * -------------------------------------------------------

 * File created: 03.02.2017

 */

package de.mnet.hurrican.wholesale.ws.outbound.mapper;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.order.ProviderwechselType;
import de.mnet.wbci.model.PersonOderFirma;
import de.mnet.wbci.model.Standort;
import de.mnet.wbci.model.WbciGeschaeftsfall;

/**
 * Maps WBCI PreAgreement data to WholesaleProviderwechsel
 * <p>
 * Created by wieran on 03.02.2017.
 */
@Component("WholesaleProviderwechselMapper")
public class WholesaleProviderwechselMapper {

    @Autowired
    private WholesaleAnsprechpartnerMapper ansprechpartnerMapper;

    @Autowired
    private WholesaleProviderwechselTermineMapper pvTermineMapper;

    @Autowired
    private WholesaleStandortAMapper standortAMapper;

    /**
     * Maps WBCI PreAgreement data to WholesaleProviderwechsel
     * <p>
     * uses {@link WholesaleAnsprechpartnerMapper}, {@link WholesaleProviderwechselMapper} and {@link
     * WholesaleStandortAMapper}
     *
     * @param wbciGeschaeftsfall The {@link WbciGeschaeftsfall} to map. Mandatory.
     * @param lineId             The lineId to map. Mandatory.
     * @param standort   The {@link Standort} to map. Mandatory.
     * @param endkundePersonOderFirma
     * @return The mapped {@link ProviderwechselType}.
     */
    public ProviderwechselType createProviderwechsel(WbciGeschaeftsfall wbciGeschaeftsfall, String lineId, Standort standort, PersonOderFirma endkundePersonOderFirma) {
        ProviderwechselType pv = new ProviderwechselType();
        pv.setLineId(lineId);
        pv.setVorabstimmungId(wbciGeschaeftsfall.getVorabstimmungsId());
        pv.setAnsprechpartner(ansprechpartnerMapper.createAnsprechpartner());
        pv.setTermine(pvTermineMapper.createPvTermine(wbciGeschaeftsfall));
        pv.setStandortA(standortAMapper.createStandortA(standort, endkundePersonOderFirma));
        pv.setProdukt("FttB BSA 100/40");
        //        wbciGeschaeftsfall.getMnetTechnologie().getProduktGruppe().name()

        return pv;
    }
}
