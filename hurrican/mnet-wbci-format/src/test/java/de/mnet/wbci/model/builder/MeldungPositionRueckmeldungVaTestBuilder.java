/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.08.13
 */
package de.mnet.wbci.model.builder;

import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.MeldungPositionRueckmeldungVa;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.WbciCdmVersion;

/**
 *
 */
public class MeldungPositionRueckmeldungVaTestBuilder extends MeldungPositionRueckmeldungVaBuilder
        implements WbciTestBuilder<MeldungPositionRueckmeldungVa> {
    @Override
    public MeldungPositionRueckmeldungVa buildValid(WbciCdmVersion wbciCdmVersion, GeschaeftsfallTyp gfTyp) {
        MeldungPositionTestBuilder.enrich(this, wbciCdmVersion, gfTyp);

        if (standortAbweichend == null) {
            standortAbweichend = new StandortTestBuilder()
                    .withConsiderationOfMeldungsCode(this.meldungsCode)
                    .buildValid(wbciCdmVersion, gfTyp);
        }

        return build();
    }

    @Override
    public MeldungPositionRueckmeldungVaTestBuilder withMeldungsCode(MeldungsCode meldungsCode) {
        return (MeldungPositionRueckmeldungVaTestBuilder) super.withMeldungsCode(meldungsCode);
    }
}
