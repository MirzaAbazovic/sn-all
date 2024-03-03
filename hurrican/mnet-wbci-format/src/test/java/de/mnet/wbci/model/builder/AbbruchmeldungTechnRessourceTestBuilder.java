/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.09.13
 */
package de.mnet.wbci.model.builder;

import java.util.*;

import de.mnet.wbci.model.AbbruchmeldungTechnRessource;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.MeldungPositionAbbruchmeldungTechnRessource;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfall;

/**
 *
 */
public class AbbruchmeldungTechnRessourceTestBuilder extends AbbruchmeldungTechnRessourceBuilder implements
        WbciTestBuilder<AbbruchmeldungTechnRessource> {

    @Override
    public AbbruchmeldungTechnRessource buildValid(WbciCdmVersion wbciCdmVersion, GeschaeftsfallTyp gfTyp) {
        if (meldungsPositionen.isEmpty()) {
            addMeldungPosition(new MeldungPositionAbbruchmeldungTechnRessourceTestBuilder().buildValid(wbciCdmVersion,
                    gfTyp));
        }
        MeldungTestBuilder.enrich(this, wbciCdmVersion, gfTyp);
        return build();
    }

    @Override
    public AbbruchmeldungTechnRessourceTestBuilder withWbciGeschaeftsfall(WbciGeschaeftsfall wbciGeschaeftsfall) {
        return (AbbruchmeldungTechnRessourceTestBuilder) super.withWbciGeschaeftsfall(wbciGeschaeftsfall);
    }

    @Override
    public AbbruchmeldungTechnRessourceTestBuilder addMeldungPosition(MeldungPositionAbbruchmeldungTechnRessource position) {
        return (AbbruchmeldungTechnRessourceTestBuilder) super.addMeldungPosition(position);
    }

    @Override
    public AbbruchmeldungTechnRessourceTestBuilder withMeldungsPositionen(
            Set<MeldungPositionAbbruchmeldungTechnRessource> positionen) {
        return (AbbruchmeldungTechnRessourceTestBuilder) super.withMeldungsPositionen(positionen);
    }

}
