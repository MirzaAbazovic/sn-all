/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.AuftragstypType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungstypAbstractType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class MeldungTypeBuilder implements LineOrderNotificationTypeBuilder<MeldungType> {

    private AuftragstypType auftragstyp;
    private MeldungstypAbstractType meldungstyp;

    @Override
    public MeldungType build() {
        MeldungType meldungType = new MeldungType();
        meldungType.setAuftragstyp(auftragstyp);
        meldungType.setMeldungstyp(meldungstyp);
        return meldungType;
    }

    public MeldungTypeBuilder withAuftragstyp(AuftragstypType auftragstyp) {
        this.auftragstyp = auftragstyp;
        return this;
    }

    public MeldungTypeBuilder withMeldungstyp(MeldungstypAbstractType meldungstyp) {
        this.meldungstyp = meldungstyp;
        return this;
    }

}