/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v1;

import java.util.*;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.OnkzDurchwahlAbfragestelleType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.PortierungDurchwahlanlageType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.RufnummernblockType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class PortierungDurchwahlanlageTypeBuilder implements LineOrderNotificationTypeBuilder<PortierungDurchwahlanlageType> {

    private OnkzDurchwahlAbfragestelleType onkzDurchwahlAbfragestelle;
    private List<RufnummernblockType> zuPortierenderRufnummernblock;

    @Override
    public PortierungDurchwahlanlageType build() {
        PortierungDurchwahlanlageType portierungDurchwahlanlageType = new PortierungDurchwahlanlageType();
        portierungDurchwahlanlageType.setOnkzDurchwahlAbfragestelle(onkzDurchwahlAbfragestelle);
        if (zuPortierenderRufnummernblock != null) {
            portierungDurchwahlanlageType.getZuPortierenderRufnummernblock().addAll(zuPortierenderRufnummernblock);
        }
        return portierungDurchwahlanlageType;
    }

    public PortierungDurchwahlanlageTypeBuilder withOnkzDurchwahlAbfragestelle(OnkzDurchwahlAbfragestelleType onkzDurchwahlAbfragestelle) {
        this.onkzDurchwahlAbfragestelle = onkzDurchwahlAbfragestelle;
        return this;
    }

    public PortierungDurchwahlanlageTypeBuilder withZuPortierenderRufnummernblock(List<RufnummernblockType> zuPortierenderRufnummernblock) {
        this.zuPortierenderRufnummernblock = zuPortierenderRufnummernblock;
        return this;
    }

    public PortierungDurchwahlanlageTypeBuilder addZuPortierenderRufnummernblock(RufnummernblockType zuPortierenderRufnummernblock) {
        if (this.zuPortierenderRufnummernblock == null) {
            this.zuPortierenderRufnummernblock = new ArrayList<>();
        }
        this.zuPortierenderRufnummernblock .add(zuPortierenderRufnummernblock);
        return this;
    }

}
