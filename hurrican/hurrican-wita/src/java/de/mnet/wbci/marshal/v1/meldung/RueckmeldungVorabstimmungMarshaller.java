/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.09.13
 */
package de.mnet.wbci.marshal.v1.meldung;

import java.util.*;
import javax.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mnet.common.tools.DateConverterUtils;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungRUEMVAType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungsPositionRUEMVAType;
import de.mnet.wbci.marshal.v1.entities.MeldungPositionRueckmeldungVaMarshaller;
import de.mnet.wbci.marshal.v1.entities.RufnummernportierungMeldungMarshaller;
import de.mnet.wbci.marshal.v1.entities.TechnischeRessourceMarshaller;
import de.mnet.wbci.model.MeldungPositionRueckmeldungVa;
import de.mnet.wbci.model.RueckmeldungVorabstimmung;
import de.mnet.wbci.model.TechnischeRessource;

/**
 *
 */
@Component
public class RueckmeldungVorabstimmungMarshaller extends
        AbstractMeldungMarshaller<RueckmeldungVorabstimmung, MeldungRUEMVAType, MeldungPositionRueckmeldungVa, MeldungsPositionRUEMVAType> {

    @Autowired
    private RufnummernportierungMeldungMarshaller rufnummernportierungMarshaller;
    @Autowired
    private TechnischeRessourceMarshaller technischeRessourceMarshaller;
    @Autowired
    private MeldungPositionRueckmeldungVaMarshaller meldungsPositionMarshaller;

    @Nullable
    @Override
    public MeldungRUEMVAType apply(@Nullable RueckmeldungVorabstimmung meldung) {
        if (meldung != null) {
            MeldungRUEMVAType meldungRUEMVAType = V1_OBJECT_FACTORY.createMeldungRUEMVAType();

            meldungRUEMVAType.setTechnologie((meldung.getTechnologie() != null) ? meldung.getTechnologie().getWbciTechnologieCode() : null);
            meldungRUEMVAType.setWechseltermin(DateConverterUtils.toXmlGregorianCalendar(meldung.getWechseltermin()));
            meldungRUEMVAType.setRufnummernPortierung(rufnummernportierungMarshaller.apply(meldung.getRufnummernportierung()));

            if (meldung.getTechnischeRessourcen() != null) {
                for (TechnischeRessource technischeRessource : meldung.getTechnischeRessourcen()) {
                    meldungRUEMVAType.getRessource().add(technischeRessourceMarshaller.apply(technischeRessource));
                }
            }

            super.apply(meldung, meldungRUEMVAType);
            return meldungRUEMVAType;
        }
        return null;
    }

    @Override
    protected void applyPositionen(Set<MeldungPositionRueckmeldungVa> meldungsPositionen, MeldungRUEMVAType output) {
        for (MeldungPositionRueckmeldungVa meldungPosition : meldungsPositionen) {
            output.getPosition().add(meldungsPositionMarshaller.apply(meldungPosition));
        }
    }
}
