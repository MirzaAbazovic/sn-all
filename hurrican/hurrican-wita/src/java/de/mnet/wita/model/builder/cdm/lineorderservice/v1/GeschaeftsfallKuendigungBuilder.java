/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.Geschaeftsfall;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.KuendigungType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class GeschaeftsfallKuendigungBuilder implements LineOrderTypeBuilder<Geschaeftsfall> {

    private KuendigungType kuekd;

    @Override
    public Geschaeftsfall build() {
        Geschaeftsfall geschaeftsfall = new Geschaeftsfall();
        geschaeftsfall.setKUEKD(kuekd);
        return null;
    }

    public GeschaeftsfallKuendigungBuilder withKueKd(KuendigungType kuekd) {
        this.kuekd = kuekd;
        return this;
    }

}
