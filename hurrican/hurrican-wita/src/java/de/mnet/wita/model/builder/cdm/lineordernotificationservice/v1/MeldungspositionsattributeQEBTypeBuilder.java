/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.AnsprechpartnerBaseType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungspositionsattributeQEBType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class MeldungspositionsattributeQEBTypeBuilder implements LineOrderNotificationTypeBuilder<MeldungspositionsattributeQEBType> {

    private AnsprechpartnerBaseType ansprechpartner;

    @Override
    public MeldungspositionsattributeQEBType build() {
        return enrich(new MeldungspositionsattributeQEBType());
    }

    protected <MPA extends MeldungspositionsattributeQEBType> MPA enrich(MPA meldungspositionsattribute) {
        meldungspositionsattribute.setAnsprechpartnerTelekom(ansprechpartner);
        return meldungspositionsattribute;
    }

    public MeldungspositionsattributeQEBTypeBuilder withAnsprechpartner(AnsprechpartnerBaseType ansprechpartner) {
        this.ansprechpartner = ansprechpartner;
        return this;
    }

}
