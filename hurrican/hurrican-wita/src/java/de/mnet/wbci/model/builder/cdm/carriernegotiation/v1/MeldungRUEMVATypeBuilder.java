/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.08.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import java.util.*;
import javax.xml.datatype.*;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungRUEMVAType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungsPositionRUEMVAType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.RufnummernportierungMeldungType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.TechnischeRessourceType;
import de.mnet.wbci.model.Technologie;

/**
 *
 */
public class MeldungRUEMVATypeBuilder extends AbstractMeldungTypeBuilder<MeldungRUEMVAType> {

    public MeldungRUEMVATypeBuilder() {
        objectType = OBJECT_FACTORY.createMeldungRUEMVAType();
    }

    public MeldungRUEMVATypeBuilder withPositionList(
            List<MeldungsPositionRUEMVAType> meldungsPositionRUEMVATypeList) {
        objectType.getPosition().addAll(meldungsPositionRUEMVATypeList);
        return this;
    }

    public MeldungRUEMVATypeBuilder withPosition(MeldungsPositionRUEMVAType meldungsPositionRUEMVAType) {
        objectType.getPosition().add(meldungsPositionRUEMVAType);
        return this;
    }

    public MeldungRUEMVATypeBuilder withRessourceList(List<TechnischeRessourceType> technischenRessourceList) {
        objectType.getRessource().addAll(technischenRessourceList);
        return this;
    }

    public MeldungRUEMVATypeBuilder withRessource(TechnischeRessourceType technischeRessourceType) {
        objectType.getRessource().add(technischeRessourceType);
        return this;
    }

    public MeldungRUEMVATypeBuilder withTechnologie(Technologie technologie) {
        objectType.setTechnologie(technologie.getWbciTechnologieCode());
        return this;
    }

    public MeldungRUEMVATypeBuilder withWechselTermin(XMLGregorianCalendar wechselTermin) {
        objectType.setWechseltermin(wechselTermin);
        return this;
    }

    public MeldungRUEMVATypeBuilder withRufnummernportierungMeldung(
            RufnummernportierungMeldungType rufnummernportierungMeldung) {
        objectType.setRufnummernPortierung(rufnummernportierungMeldung);
        return this;
    }
}
