/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.Geschaeftsfall;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.LeistungsmerkmalAenderungType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class GeschaeftsfallLeistungsmerkmalAenderungBuilder implements LineOrderTypeBuilder<Geschaeftsfall> {

    private LeistungsmerkmalAenderungType aenlmae;

    @Override
    public Geschaeftsfall build() {
        Geschaeftsfall geschaeftsfall = new Geschaeftsfall();
        geschaeftsfall.setAENLMAE(aenlmae);
        return null;
    }

    public GeschaeftsfallLeistungsmerkmalAenderungBuilder withAenLmae(LeistungsmerkmalAenderungType aenlmae) {
        this.aenlmae = aenlmae;
        return this;
    }

}
