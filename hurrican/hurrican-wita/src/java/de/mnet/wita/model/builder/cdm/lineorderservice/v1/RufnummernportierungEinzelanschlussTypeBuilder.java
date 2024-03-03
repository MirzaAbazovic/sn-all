/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.PortierungEinzelanschlussType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.RufnummernportierungEinzelanschlussType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class RufnummernportierungEinzelanschlussTypeBuilder implements LineOrderTypeBuilder<RufnummernportierungEinzelanschlussType> {

    private PortierungEinzelanschlussType einzelanschluss;
    private String portierungskenner;

    @Override
    public RufnummernportierungEinzelanschlussType build() {
        RufnummernportierungEinzelanschlussType einzelanschlussType = new RufnummernportierungEinzelanschlussType();
        einzelanschlussType.setEinzelanschluss(einzelanschluss);
        einzelanschlussType.setPortierungskenner(portierungskenner);
        return einzelanschlussType;
    }

    public RufnummernportierungEinzelanschlussTypeBuilder withEinzelanschluss(PortierungEinzelanschlussType einzelanschluss) {
        this.einzelanschluss = einzelanschluss;
        return this;
    }

    public RufnummernportierungEinzelanschlussTypeBuilder withPortierungskenner(String portierungskenner) {
        this.portierungskenner = portierungskenner;
        return this;
    }

}
