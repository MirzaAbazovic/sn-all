/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.09.13
 */
package de.mnet.wbci.model.builder;

import de.mnet.wbci.model.MeldungPositionAbbruchmeldungTechnRessource;

/**
 *
 */
public class MeldungPositionAbbruchmeldungTechnRessourceBuilder extends
        MeldungPositionBuilder<MeldungPositionAbbruchmeldungTechnRessource> {

    @Override
    public MeldungPositionAbbruchmeldungTechnRessource build() {
        MeldungPositionAbbruchmeldungTechnRessource meldungPosition = new MeldungPositionAbbruchmeldungTechnRessource();
        super.enrich(meldungPosition);
        return meldungPosition;
    }

}
