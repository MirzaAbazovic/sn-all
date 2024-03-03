/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v1;

import java.util.*;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.DurchwahlanlageBestandType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.OnkzDurchwahlAbfragestelleType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.RufnummernblockType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class DurchwahlanlageBestandTypeBuilder implements LineOrderNotificationTypeBuilder<DurchwahlanlageBestandType> {

    private OnkzDurchwahlAbfragestelleType onkzDurchwahlAbfragestelle;
    private List<RufnummernblockType> rufnummernbloecke;

    @Override
    public DurchwahlanlageBestandType build() {
        DurchwahlanlageBestandType durchwahlanlageBestandType = new DurchwahlanlageBestandType();
        durchwahlanlageBestandType.setOnkzDurchwahlAbfragestelle(onkzDurchwahlAbfragestelle);
        if (rufnummernbloecke != null) {
            durchwahlanlageBestandType.getRufnummernbloecke().addAll(rufnummernbloecke);
        }
        return durchwahlanlageBestandType;
    }

    public DurchwahlanlageBestandTypeBuilder withOnkzDurchwahlAbfragestelle(OnkzDurchwahlAbfragestelleType onkzDurchwahlAbfragestelle) {
        this.onkzDurchwahlAbfragestelle = onkzDurchwahlAbfragestelle;
        return this;
    }

    public DurchwahlanlageBestandTypeBuilder withRufnummernbloecke(List<RufnummernblockType> rufnummernbloecke) {
        this.rufnummernbloecke = rufnummernbloecke;
        return this;
    }

    public DurchwahlanlageBestandTypeBuilder addRufnummernbloecke(RufnummernblockType rufnummernblock) {
        if (this.rufnummernbloecke == null) {
            this.rufnummernbloecke = new ArrayList<>();
        }
        this.rufnummernbloecke.add(rufnummernblock);
        return this;
    }

}
