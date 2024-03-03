/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.PortierungRufnummernMeldungType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.PortierungRufnummernbloeckeMeldungType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.RufnummernportierungMeldungType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class RufnummernportierungMeldungTypeBuilder implements LineOrderNotificationTypeBuilder<RufnummernportierungMeldungType> {

    private PortierungRufnummernMeldungType portierungRufnummern;
    private PortierungRufnummernbloeckeMeldungType portierungRufnummernbloecke;

    @Override
    public RufnummernportierungMeldungType build() {
        return enrich(new RufnummernportierungMeldungType());
    }

    protected <RNPM extends RufnummernportierungMeldungType> RNPM enrich(RNPM rufnummernportierungMeldung) {
        rufnummernportierungMeldung.setPortierungRufnummern(portierungRufnummern);
        rufnummernportierungMeldung.setPortierungRufnummernbloecke(portierungRufnummernbloecke);
        return rufnummernportierungMeldung;
    }

    public RufnummernportierungMeldungTypeBuilder withPortierungRufnummern(PortierungRufnummernMeldungType portierungRufnummern) {
        this.portierungRufnummern = portierungRufnummern;
        return this;
    }

    public RufnummernportierungMeldungTypeBuilder withPortierungRufnummernbloecke(PortierungRufnummernbloeckeMeldungType portierungRufnummernbloecke) {
        this.portierungRufnummernbloecke = portierungRufnummernbloecke;
        return this;
    }

}
