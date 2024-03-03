/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.PortierungDurchwahlanlageType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.PortierungEinzelanschlussType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.RufnummernportierungType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class RufnummernportierungTypeBuilder implements LineOrderTypeBuilder<RufnummernportierungType> {

    private PortierungDurchwahlanlageType anlagenanschluss;
    private PortierungEinzelanschlussType einzelanschluss;
    private String portierungskenner;

    @Override
    public RufnummernportierungType build() {
        RufnummernportierungType rufnummernportierungType = new RufnummernportierungType();
        rufnummernportierungType.setPortierungskenner(portierungskenner);
        rufnummernportierungType.setEinzelanschluss(einzelanschluss);
        rufnummernportierungType.setAnlagenanschluss(anlagenanschluss);
        return rufnummernportierungType;
    }

    public RufnummernportierungTypeBuilder withAnlagenanschluss(PortierungDurchwahlanlageType anlagenanschluss) {
        this.anlagenanschluss = anlagenanschluss;
        return this;
    }

    public RufnummernportierungTypeBuilder withEinzelanschluss(PortierungEinzelanschlussType einzelanschluss) {
        this.einzelanschluss = einzelanschluss;
        return this;
    }

    public RufnummernportierungTypeBuilder withPortierungskenner(String portierungskenner) {
        this.portierungskenner = portierungskenner;
        return this;
    }

}
