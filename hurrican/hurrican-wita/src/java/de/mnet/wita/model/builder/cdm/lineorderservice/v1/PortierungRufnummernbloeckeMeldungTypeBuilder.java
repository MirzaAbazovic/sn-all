/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import java.util.*;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.OnkzDurchwahlAbfragestelleType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.PortierungRufnummernbloeckeMeldungType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.RufnummernblockType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class PortierungRufnummernbloeckeMeldungTypeBuilder implements LineOrderTypeBuilder<PortierungRufnummernbloeckeMeldungType> {

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
