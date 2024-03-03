/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.KundenwunschterminType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.RnrExportMitKuendigungTermineType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class RnrExportMitKuendigungTermineTypeBuilder implements LineOrderTypeBuilder<RnrExportMitKuendigungTermineType> {

    private KundenwunschterminType kundenwunschtermin;

    @Override
    public RnrExportMitKuendigungTermineType build() {
        RnrExportMitKuendigungTermineType termineType = new RnrExportMitKuendigungTermineType();
        termineType.setKundenwunschtermin(kundenwunschtermin);
        return termineType;
    }

    public RnrExportMitKuendigungTermineTypeBuilder withKundenwunschtermin(KundenwunschterminType kundenwunschtermin) {
        this.kundenwunschtermin = kundenwunschtermin;
        return this;
    }

}
