/*

 * Copyright (c) 2017 - M-net Telekommunikations GmbH

 * All rights reserved.

 * -------------------------------------------------------

 * File created: 03.02.2017

 */

package de.mnet.hurrican.wholesale.ws.outbound.mapper;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.complex.AuftraggeberType;
import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.enm.GeschaeftsfallArtType;
import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.order.AuftragType;
import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.order.Geschaeftsfall;
import de.mnet.wbci.model.PersonOderFirma;
import de.mnet.wbci.model.Standort;
import de.mnet.wbci.model.WbciGeschaeftsfall;

/**
 * Maps WBCI PreAgreement data to WholesaleProviderwechsel Auftrag
 * <p>
 * Created by wieran on 03.02.2017.
 */
@Component("WholesaleAuftragMapper")
public class WholesaleAuftragMapper {

    public static final String AUFTRAGGEBERNUMMER = "MNETH";
    public static final String LEISTUNGSNUMMER = "MNETH-001";

    @Autowired
    private WholesaleProviderwechselMapper pvMapper;

    /**
     * Maps WBCI PreAgreement data to WholesaleProviderwechsel Auftrag <br/>
     * <p>
     * uses {@link WholesaleProviderwechselMapper}
     *
     * @param wbciGeschaeftsfall    The {@link WbciGeschaeftsfall} to map. Mandatory.
     * @param externeAuftragsnummer The externeAuftragsnummer to map. Mandatory.
     * @param lineId                The lineId to map. Mandatory.
     * @param standort      The {@link Standort} to map. Mandatory.
     * @param endkundePersonOderFirma
     * @return The mapped {@link AuftraggeberType}.
     */
    public AuftragType createPVAuftrag(WbciGeschaeftsfall wbciGeschaeftsfall, String externeAuftragsnummer, String lineId, Standort standort, PersonOderFirma endkundePersonOderFirma) {
        AuftragType auftrag = new AuftragType();
        auftrag.setExterneAuftragsnummer(externeAuftragsnummer);
        auftrag.setAuftraggeber(createAuftraggeberType());
        auftrag.setGeschaeftsfall(createGeschaeftsfall(wbciGeschaeftsfall, lineId, standort, endkundePersonOderFirma));
        auftrag.setGeschaeftsfallArt(GeschaeftsfallArtType.ENDKUNDENANBIETERWECHSEL);
        return auftrag;
    }

    private AuftraggeberType createAuftraggeberType() {
        AuftraggeberType auftraggeber = new AuftraggeberType();
        auftraggeber.setAuftraggebernummer(AUFTRAGGEBERNUMMER);
        auftraggeber.setLeistungsnummer(LEISTUNGSNUMMER);
        return auftraggeber;
    }

    private Geschaeftsfall createGeschaeftsfall(WbciGeschaeftsfall wbciGeschaeftsfall, String lineId, Standort standort, PersonOderFirma endkundePersonOderFirma) {
        Geschaeftsfall geschaeftsfall = new Geschaeftsfall();
        geschaeftsfall.setPV(pvMapper.createProviderwechsel(wbciGeschaeftsfall, lineId, standort, endkundePersonOderFirma));
        return geschaeftsfall;
    }
}
