/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v2;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.AnsprechpartnerBaseType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungspositionsattributeABMType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class MeldungspositionsattributeABMTypeBuilder implements LineOrderNotificationTypeBuilder<MeldungspositionsattributeABMType> {

    private AnsprechpartnerBaseType ansprechpartner;

    @Override
    public MeldungspositionsattributeABMType build() {
        return enrich(new MeldungspositionsattributeABMType());
    }

    protected <MPA extends MeldungspositionsattributeABMType> MPA enrich(MPA meldungspositionsattribute) {
        meldungspositionsattribute.setAnsprechpartnerTelekom(ansprechpartner);
        return meldungspositionsattribute;
    }

    public MeldungspositionsattributeABMTypeBuilder withAnsprechpartner(AnsprechpartnerBaseType ansprechpartner) {
        this.ansprechpartner = ansprechpartner;
        return this;
    }

}
