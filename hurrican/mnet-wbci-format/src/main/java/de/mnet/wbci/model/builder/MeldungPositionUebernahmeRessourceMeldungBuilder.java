/* 
 * Copyright (c) 2013 - M-net Telekommunikations GmbH 
 * All rights reserved. 
 * ------------------------------------------------------- 
 * File created: 18.09.13 
 */
package de.mnet.wbci.model.builder;

import de.mnet.wbci.model.MeldungPositionUebernahmeRessourceMeldung;

public class MeldungPositionUebernahmeRessourceMeldungBuilder extends
        MeldungPositionBuilder<MeldungPositionUebernahmeRessourceMeldung> {

    @Override
    public MeldungPositionUebernahmeRessourceMeldung build() {
        MeldungPositionUebernahmeRessourceMeldung meldungPosition = new MeldungPositionUebernahmeRessourceMeldung();
        super.enrich(meldungPosition);
        return meldungPosition;
    }

}
