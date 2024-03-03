/* 
 * Copyright (c) 2013 - M-net Telekommunikations GmbH 
 * All rights reserved. 
 * ------------------------------------------------------- 
 * File created: 18.09.13 
 */
package de.mnet.wbci.model.builder;

import de.mnet.wbci.model.MeldungPositionErledigtmeldung;

public class MeldungPositionErledigtmeldungBuilder extends MeldungPositionBuilder<MeldungPositionErledigtmeldung> {

    @Override
    public MeldungPositionErledigtmeldung build() {
        MeldungPositionErledigtmeldung meldungPosition = new MeldungPositionErledigtmeldung();
        super.enrich(meldungPosition);
        return meldungPosition;
    }

}
