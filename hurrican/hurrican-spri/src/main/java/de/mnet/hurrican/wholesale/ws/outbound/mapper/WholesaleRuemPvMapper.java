/*

 * Copyright (c) 2017 - M-net Telekommunikations GmbH

 * All rights reserved.

 * -------------------------------------------------------

 * File created: 14.03.2017

 */

package de.mnet.hurrican.wholesale.ws.outbound.mapper;


import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.complex.MeldungspositionOhneAttributeType;
import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.complex.ProviderwechselRueckmeldungType;
import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.enm.JaNeinType;
import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.message.MeldungsattributeAKMPVType;
import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.message.MeldungsattributeRUEMPVType;
import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.message.MeldungstypAKMPVType;
import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.message.MeldungstypRUEMPVType;
import de.mnet.hurrican.wholesale.ws.outbound.impl.WholesaleOrderOutboundServiceImpl;

/**
 * Maps RUEM PV Type
 * <p>
 * Created by wieran on 14.03.2017.
 */
@Component("WholesaleRuemPvMapper")
public class WholesaleRuemPvMapper {

    public static final String MELDUNGSCODE_RUEM_PV = "0021";
    public static final String MELDUNGSTEXT_RUEM_PV = "Antwort des abgebenden Provider";

    /**
     * This method creates a meldungstypRUEMPVType
     *
     * @param akmpv
     * @return
     */
    public MeldungstypRUEMPVType createRUEMPV(MeldungstypAKMPVType akmpv) {

        MeldungstypRUEMPVType ruempvType = new MeldungstypRUEMPVType();

        MeldungsattributeAKMPVType akmpvMeldungsattribute = akmpv.getMeldungsattribute();
        MeldungsattributeRUEMPVType attribute = createRuempvMeldungsattribute(akmpvMeldungsattribute);
        ruempvType.setMeldungsattribute(attribute);

        MeldungstypRUEMPVType.Meldungspositionen meldungspositionen = createMeldungspositionen();
        ruempvType.setMeldungspositionen(meldungspositionen);

        return ruempvType;

    }

    private MeldungstypRUEMPVType.Meldungspositionen createMeldungspositionen() {
        MeldungstypRUEMPVType.Meldungspositionen meldungspositionen = new MeldungstypRUEMPVType.Meldungspositionen();
        MeldungspositionOhneAttributeType meldungsposition = new MeldungspositionOhneAttributeType();
        meldungsposition.setMeldungscode(MELDUNGSCODE_RUEM_PV);
        meldungsposition.setMeldungstext(MELDUNGSTEXT_RUEM_PV);
        meldungspositionen.getPosition().add(meldungsposition);
        return meldungspositionen;
    }

    private MeldungsattributeRUEMPVType createRuempvMeldungsattribute(MeldungsattributeAKMPVType akmpvMeldungsattribute) {
        MeldungsattributeRUEMPVType attribute = new MeldungsattributeRUEMPVType();
        attribute.setAbgebenderProvider(createProviderwechselRueckmeldungType());
        attribute.setAnschluss(akmpvMeldungsattribute.getAnschluss());
        attribute.setAuftraggebernummer(akmpvMeldungsattribute.getAuftraggebernummer());
        attribute.setExterneAuftragsnummer(akmpvMeldungsattribute.getExterneAuftragsnummer());
        attribute.setVertragsnummer(akmpvMeldungsattribute.getVertragsnummer());
        attribute.setVorabstimmungId(akmpvMeldungsattribute.getVorabstimmungId());
        return attribute;
    }

    private ProviderwechselRueckmeldungType createProviderwechselRueckmeldungType() {
        ProviderwechselRueckmeldungType providerwechselRueckmeldungType = new ProviderwechselRueckmeldungType();

        providerwechselRueckmeldungType.setProvidername(WholesaleOrderOutboundServiceImpl.SENDING_CARRIER);
        providerwechselRueckmeldungType.setZustimmungProviderwechsel(JaNeinType.J);

        return providerwechselRueckmeldungType;
    }

}
