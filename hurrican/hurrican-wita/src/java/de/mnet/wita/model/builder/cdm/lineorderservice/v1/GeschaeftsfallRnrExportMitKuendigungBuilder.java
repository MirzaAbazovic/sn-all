/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.Geschaeftsfall;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.RnrExportMitKuendigungType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class GeschaeftsfallRnrExportMitKuendigungBuilder implements LineOrderTypeBuilder<Geschaeftsfall> {

    private RnrExportMitKuendigungType rexmk;

    @Override
    public Geschaeftsfall build() {
        Geschaeftsfall geschaeftsfall = new Geschaeftsfall();
        geschaeftsfall.setREXMK(rexmk);
        return null;
    }

    public GeschaeftsfallRnrExportMitKuendigungBuilder withRexMk(RnrExportMitKuendigungType rexmk) {
        this.rexmk = rexmk;
        return this;
    }

}
