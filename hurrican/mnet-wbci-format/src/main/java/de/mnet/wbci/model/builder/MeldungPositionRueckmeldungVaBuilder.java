/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.08.13
 */
package de.mnet.wbci.model.builder;

import de.mnet.wbci.model.MeldungPositionRueckmeldungVa;
import de.mnet.wbci.model.Standort;

/**
 *
 */
public class MeldungPositionRueckmeldungVaBuilder extends MeldungPositionBuilder<MeldungPositionRueckmeldungVa> {

    protected Standort standortAbweichend;

    @Override
    public MeldungPositionRueckmeldungVa build() {
        MeldungPositionRueckmeldungVa meldungPosition = new MeldungPositionRueckmeldungVa();

        meldungPosition.setStandortAbweichend(standortAbweichend);

        super.enrich(meldungPosition);
        return meldungPosition;
    }

    public MeldungPositionRueckmeldungVaBuilder withStandortAbweichend(Standort standortAbweichend) {
        this.standortAbweichend = standortAbweichend;
        return this;
    }
}
