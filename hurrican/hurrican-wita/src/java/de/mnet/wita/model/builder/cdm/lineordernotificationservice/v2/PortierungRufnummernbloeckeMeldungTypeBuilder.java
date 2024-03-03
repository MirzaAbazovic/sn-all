/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v2;

import java.util.*;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.OnkzDurchwahlAbfragestelleType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.PortierungRufnummernbloeckeMeldungType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.RufnummernblockType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class PortierungRufnummernbloeckeMeldungTypeBuilder implements LineOrderNotificationTypeBuilder<PortierungRufnummernbloeckeMeldungType> {

    private OnkzDurchwahlAbfragestelleType onkzDurchwahlAbfragestelle;
    private List<RufnummernblockType> zuPortierenderRufnummernblock;

    @Override
    public PortierungRufnummernbloeckeMeldungType build() {
        PortierungRufnummernbloeckeMeldungType rufnummernbloeckeMeldungType = new PortierungRufnummernbloeckeMeldungType();
        rufnummernbloeckeMeldungType.setOnkzDurchwahlAbfragestelle(onkzDurchwahlAbfragestelle);
        if (zuPortierenderRufnummernblock != null) {
            rufnummernbloeckeMeldungType.getZuPortierenderRufnummernblock().addAll(zuPortierenderRufnummernblock);
        }
        return rufnummernbloeckeMeldungType;
    }

    public PortierungRufnummernbloeckeMeldungTypeBuilder withOnkzDurchwahlAbfragestelle(OnkzDurchwahlAbfragestelleType onkzDurchwahlAbfragestelle) {
        this.onkzDurchwahlAbfragestelle = onkzDurchwahlAbfragestelle;
        return this;
    }

    public PortierungRufnummernbloeckeMeldungTypeBuilder withZuPortierenderRufnummernblock(List<RufnummernblockType> zuPortierenderRufnummernblock) {
        this.zuPortierenderRufnummernblock = zuPortierenderRufnummernblock;
        return this;
    }

    public PortierungRufnummernbloeckeMeldungTypeBuilder addZuPortierenderRufnummernblock(RufnummernblockType zuPortierenderRufnummernblock) {
        if (this.zuPortierenderRufnummernblock == null) {
            this.zuPortierenderRufnummernblock = new ArrayList<>();
        }
        this.zuPortierenderRufnummernblock.add(zuPortierenderRufnummernblock);
        return this;
    }

}
