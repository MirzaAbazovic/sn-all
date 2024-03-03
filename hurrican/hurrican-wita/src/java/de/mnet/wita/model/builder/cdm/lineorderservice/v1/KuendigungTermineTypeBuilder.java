/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.KuendigungTermineType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.KundenwunschterminType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class KuendigungTermineTypeBuilder implements LineOrderTypeBuilder<KuendigungTermineType> {

    private KundenwunschterminType kundenwunschtermin;

    @Override
    public KuendigungTermineType build() {
        KuendigungTermineType kuendigungTermineType = new KuendigungTermineType();
        kuendigungTermineType.setKundenwunschtermin(kundenwunschtermin);
        return kuendigungTermineType;
    }

    public KuendigungTermineTypeBuilder withKundenwunschtermin(KundenwunschterminType kundenwunschtermin) {
        this.kundenwunschtermin = kundenwunschtermin;
        return this;
    }

}
