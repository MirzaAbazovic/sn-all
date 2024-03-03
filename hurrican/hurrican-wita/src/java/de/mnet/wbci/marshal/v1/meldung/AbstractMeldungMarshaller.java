/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.09.13
 */
package de.mnet.wbci.marshal.v1.meldung;

import java.util.*;
import com.google.common.base.Function;
import org.springframework.beans.factory.annotation.Autowired;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.AbstractMeldungType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.AbstractMeldungsPositionType;
import de.mnet.wbci.marshal.v1.AbstractBaseMarshaller;
import de.mnet.wbci.marshal.v1.entities.CarrierIdentifikatorMarshaller;
import de.mnet.wbci.marshal.v1.entities.EKPMeldungMarshaller;
import de.mnet.wbci.marshal.v1.entities.GeschaeftsfallEnumMarshaller;
import de.mnet.wbci.model.Meldung;
import de.mnet.wbci.model.MeldungPosition;

/**
 *
 */
public abstract class AbstractMeldungMarshaller<IN extends Meldung<POS>, OUT extends AbstractMeldungType, POS extends MeldungPosition, POST extends AbstractMeldungsPositionType>
        extends AbstractBaseMarshaller implements Function<IN, OUT> {

    @Autowired
    private EKPMeldungMarshaller ekpMarshaller;
    @Autowired
    private CarrierIdentifikatorMarshaller ciMarshaller;
    @Autowired
    private GeschaeftsfallEnumMarshaller gfTypMarshaller;

    public OUT apply(IN meldung, OUT output) {
        output.setAbsender(ciMarshaller.apply(meldung.getAbsender()));
        output.setEmpfaenger(ciMarshaller.apply(meldung.getEKPPartner()));
        output.setEndkundenvertragspartner(ekpMarshaller.apply(meldung));
        output.setGeschaeftsfall(gfTypMarshaller.apply(meldung.getWbciGeschaeftsfall().getTyp()));
        output.setVorabstimmungsIdRef(meldung.getWbciGeschaeftsfall().getVorabstimmungsId());
        //WBCI version is only for incoming wbci messages relevant.
        //for outgoing messages the wbci version will be set from ATLAS.
        applyPositionen(meldung.getMeldungsPositionen(), output);

        return output;
    }

    protected abstract void applyPositionen(Set<POS> meldungsPositionen, OUT output);

}
