/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.09.13
 */
package de.mnet.wbci.marshal.v1.request.terminverschiebung;

import javax.annotation.*;
import com.google.common.base.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mnet.common.tools.DateConverterUtils;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.TerminverschiebungType;
import de.mnet.wbci.marshal.v1.AbstractBaseMarshaller;
import de.mnet.wbci.marshal.v1.entities.CarrierIdentifikatorMarshaller;
import de.mnet.wbci.marshal.v1.entities.EKPGeschaeftsfallMarshaller;
import de.mnet.wbci.marshal.v1.entities.PersonOderFirmaMarshaller;
import de.mnet.wbci.model.TerminverschiebungsAnfrage;

@Component
public class TerminverschiebungMarshaller extends
        AbstractBaseMarshaller implements Function<TerminverschiebungsAnfrage, TerminverschiebungType> {

    @Autowired
    private EKPGeschaeftsfallMarshaller ekpMarshaller;
    @Autowired
    private CarrierIdentifikatorMarshaller ciMarshaller;
    @Autowired
    private PersonOderFirmaMarshaller personOderFirmaMarshaller;

    @Nullable
    @Override
    public TerminverschiebungType apply(@Nullable TerminverschiebungsAnfrage input) {
        TerminverschiebungType tvType = V1_OBJECT_FACTORY.createTerminverschiebungType();
        if (input.getWbciGeschaeftsfall() != null) {
            tvType.setEndkundenvertragspartner(ekpMarshaller.apply(input.getWbciGeschaeftsfall()));
            tvType.setAbsender(ciMarshaller.apply(input.getWbciGeschaeftsfall().getAbsender()));
            tvType.setEmpfaenger(ciMarshaller.apply(input.getWbciGeschaeftsfall().getAbgebenderEKP()));
            tvType.setVorabstimmungsIdRef(input.getWbciGeschaeftsfall().getVorabstimmungsId());
        }
        tvType.setAenderungsId(input.getAenderungsId());
        if (input.getTvTermin() != null) {
            tvType.setNeuerKundenwunschtermin(DateConverterUtils.toXmlGregorianCalendar(input.getTvTermin()));
        }
        tvType.setName(personOderFirmaMarshaller.apply(input.getEndkunde()));

        return tvType;
    }

}
