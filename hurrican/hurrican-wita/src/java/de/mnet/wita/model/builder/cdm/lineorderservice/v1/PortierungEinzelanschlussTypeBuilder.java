/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.BoolDecisionType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.PortierungEinzelanschlussType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.RufnummernListeType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class PortierungEinzelanschlussTypeBuilder implements LineOrderTypeBuilder<PortierungEinzelanschlussType> {

    private BoolDecisionType alleRufnummern;
    private RufnummernListeType rufnummernliste;

    @Override
    public PortierungEinzelanschlussType build() {
        PortierungEinzelanschlussType portierungEinzelanschlussType = new PortierungEinzelanschlussType();
        portierungEinzelanschlussType.setAlleRufnummern(alleRufnummern);
        portierungEinzelanschlussType.setRufnummernliste(rufnummernliste);
        return portierungEinzelanschlussType;
    }

    public PortierungEinzelanschlussTypeBuilder withAlleRufnummern(BoolDecisionType alleRufnummern) {
        this.alleRufnummern = alleRufnummern;
        return this;
    }

    public PortierungEinzelanschlussTypeBuilder withRufnummernliste(RufnummernListeType rufnummernliste) {
        this.rufnummernliste = rufnummernliste;
        return this;
    }

}
