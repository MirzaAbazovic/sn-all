/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.08.13
 */
package de.mnet.wbci.unmarshal.v1.meldung;

import javax.annotation.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mnet.common.tools.DateConverterUtils;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungRUEMVAType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungsPositionRUEMVAType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.TechnischeRessourceType;
import de.mnet.wbci.model.RueckmeldungVorabstimmung;
import de.mnet.wbci.model.Technologie;
import de.mnet.wbci.model.builder.RueckmeldungVorabstimmungBuilder;
import de.mnet.wbci.unmarshal.v1.enities.MeldungsPositionRUEMVAUnmarshaller;
import de.mnet.wbci.unmarshal.v1.enities.RufnummerportierungMeldungUnmarshaller;
import de.mnet.wbci.unmarshal.v1.enities.TechnischeRessourceUnmarshaller;

/**
 *
 */
@Component
public class RueckmeldungVorabstimmungUnmarshaller extends
        AbstractMeldungUnmarshaller<MeldungRUEMVAType, RueckmeldungVorabstimmung> {

    @Autowired
    private MeldungsPositionRUEMVAUnmarshaller meldungsPositionRUEMVAUnmarshaller;
    @Autowired
    private RufnummerportierungMeldungUnmarshaller rufnummerportierungUnmarshaller;
    @Autowired
    private TechnischeRessourceUnmarshaller technischeRessourceUnmarshaller;

    @Nullable
    @Override
    public RueckmeldungVorabstimmung apply(@Nullable MeldungRUEMVAType input) {
        if (input == null) {
            return null;
        }

        RueckmeldungVorabstimmung meldung = new RueckmeldungVorabstimmungBuilder().build();

        if (!CollectionUtils.isEmpty(input.getPosition())) {
            for (MeldungsPositionRUEMVAType positionRUEMVAType : input.getPosition()) {
                meldung.getMeldungsPositionen().add(meldungsPositionRUEMVAUnmarshaller.apply(positionRUEMVAType));
            }
        }

        if (!CollectionUtils.isEmpty(input.getRessource())) {
            for (TechnischeRessourceType technischeRessourceType : input.getRessource()) {
                meldung.getTechnischeRessourcen().add(
                        technischeRessourceUnmarshaller.apply(technischeRessourceType));
            }
        }

        meldung.setRufnummernportierung(rufnummerportierungUnmarshaller.apply(input.getRufnummernPortierung()));
        meldung.setWechseltermin(DateConverterUtils.toLocalDate(input.getWechseltermin()));
        if (StringUtils.isNotEmpty(input.getTechnologie())) {
            meldung.setTechnologie(Technologie.lookUpWbciTechnologieCode(input.getTechnologie()));
        }

        super.apply(meldung, input);

        return meldung;
    }
}
