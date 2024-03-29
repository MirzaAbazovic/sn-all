/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.RufnummernportierungEinzelanschlussMeldungType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.RufnummernportierungMeldungType;

/**
 *
 */
public class RufnummernportierungEinzelanschlussMeldungTypeBuilder extends RufnummernportierungMeldungTypeBuilder {

    @Override
    public RufnummernportierungMeldungType build() {
        return enrich(new RufnummernportierungEinzelanschlussMeldungType());
    }

}
