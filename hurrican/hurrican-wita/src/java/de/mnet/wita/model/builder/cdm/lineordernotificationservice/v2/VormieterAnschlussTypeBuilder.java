/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v2;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.AnschlussType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.UFAType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.VormieterAnschlussType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class VormieterAnschlussTypeBuilder implements LineOrderNotificationTypeBuilder<VormieterAnschlussType> {

    private VormieterAnschlussType.Person person;
    private AnschlussType anschluss;
    private UFAType ufa;

    @Override
    public VormieterAnschlussType build() {
        VormieterAnschlussType vormieterAnschlussType = new VormieterAnschlussType();
        vormieterAnschlussType.setAnschluss(anschluss);
        vormieterAnschlussType.setPerson(person);
        vormieterAnschlussType.setUFA(ufa);
        return vormieterAnschlussType;
    }

    public VormieterAnschlussTypeBuilder withPerson(VormieterAnschlussType.Person person) {
        this.person = person;
        return this;
    }

    public VormieterAnschlussTypeBuilder withAnschluss(AnschlussType anschluss) {
        this.anschluss = anschluss;
        return this;
    }

    public VormieterAnschlussTypeBuilder withUfa(UFAType ufa) {
        this.ufa = ufa;
        return this;
    }

}
