/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.09.13
 */
package de.mnet.wbci.model.builder;

import de.mnet.wbci.model.Leitung;

/**
 *
 */
public class LeitungBuilder implements WbciBuilder<Leitung> {

    protected String vertragsnummer;
    protected String lineId;

    @Override
    public Leitung build() {
        Leitung leitung = new Leitung();

        leitung.setVertragsnummer(vertragsnummer);
        leitung.setLineId(lineId);

        return leitung;
    }

    public LeitungBuilder withVertragsnummer(String vertragsnummer) {
        this.vertragsnummer = vertragsnummer;
        return this;
    }

    public LeitungBuilder withLineId(String lineId) {
        this.lineId = lineId;
        return this;
    }
}
