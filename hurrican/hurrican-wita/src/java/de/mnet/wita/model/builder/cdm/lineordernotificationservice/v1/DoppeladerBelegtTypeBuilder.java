/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v1;

import java.util.*;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.DoppeladerBelegtType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.LeitungsbezeichnungType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class DoppeladerBelegtTypeBuilder implements LineOrderNotificationTypeBuilder<DoppeladerBelegtType> {

    private List<LeitungsbezeichnungType> leitungsbezeichnung;

    @Override
    public DoppeladerBelegtType build() {
        DoppeladerBelegtType doppeladerBelegtType = new DoppeladerBelegtType();
        if (leitungsbezeichnung != null) {
            doppeladerBelegtType.getLeitungsbezeichnung().addAll(leitungsbezeichnung);
        }
        return null;
    }

    public DoppeladerBelegtTypeBuilder withLeitungsbezeichnung(List<LeitungsbezeichnungType> leitungsbezeichnung) {
        this.leitungsbezeichnung = leitungsbezeichnung;
        return this;
    }

    public DoppeladerBelegtTypeBuilder addLeitungsbezeichnung(LeitungsbezeichnungType leitungsbezeichnung) {
        if (this.leitungsbezeichnung == null) {
            this.leitungsbezeichnung = new ArrayList<>();
        }
        this.leitungsbezeichnung.add(leitungsbezeichnung);
        return this;
    }

}
