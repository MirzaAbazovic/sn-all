/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.ErweiterteBestandssucheType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.OnkzDurchwahlAbfragestelleType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.OnkzRufNrType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class ErweiterteBestandssucheTypeBuilder implements LineOrderTypeBuilder<ErweiterteBestandssucheType> {

    private OnkzDurchwahlAbfragestelleType anlagenanschluss;
    private OnkzRufNrType einzelanschluss;

    @Override
    public ErweiterteBestandssucheType build() {
        ErweiterteBestandssucheType erweiterteBestandssucheType = new ErweiterteBestandssucheType();
        erweiterteBestandssucheType.setAnlagenanschluss(anlagenanschluss);
        erweiterteBestandssucheType.setEinzelanschluss(einzelanschluss);
        return erweiterteBestandssucheType;
    }

    public ErweiterteBestandssucheTypeBuilder withAnlagenanschluss(OnkzDurchwahlAbfragestelleType anlagenanschluss) {
        this.anlagenanschluss = anlagenanschluss;
        return this;
    }

    public ErweiterteBestandssucheTypeBuilder withEinzelanschluss(OnkzRufNrType einzelanschluss) {
        this.einzelanschluss = einzelanschluss;
        return this;
    }

}
