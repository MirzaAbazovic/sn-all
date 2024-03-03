/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.08.13
 */
package de.mnet.wbci.model.builder;

import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.TechnischeRessource;
import de.mnet.wbci.model.WbciCdmVersion;

/**
 *
 */
public class TechnischeRessourceTestBuilder extends TechnischeRessourceBuilder implements WbciTestBuilder<TechnischeRessource> {

    @Override
    public TechnischeRessource buildValid(WbciCdmVersion wbciCdmVersion, GeschaeftsfallTyp gfTyp) {

        if (vertragsnummer == null && lineId == null) {
            vertragsnummer = "V123456";
        }

        if (tnbKennungAbg == null) {
            tnbKennungAbg = "D001";
        }

        return build();
    }

    @Override
    public TechnischeRessourceTestBuilder withVertragsnummer(String vertragsnummer) {
        return (TechnischeRessourceTestBuilder) super.withVertragsnummer(vertragsnummer);
    }

    @Override
    public TechnischeRessourceTestBuilder withLineId(String lineId) {
        return (TechnischeRessourceTestBuilder) super.withLineId(lineId);
    }
}
