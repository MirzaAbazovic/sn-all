/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.01.14
 */
package de.mnet.wbci.model.builder;

import de.mnet.wbci.model.MeldungPositionAbbmRufnummer;

/**
 * Special builder class for ABBM rufnummern.
 *
 *
 */
public class MeldungPositionAbbmRufnummerBuilder implements WbciBuilder<MeldungPositionAbbmRufnummer> {

    private String rufnummer;

    @Override
    public MeldungPositionAbbmRufnummer build() {
        MeldungPositionAbbmRufnummer abbmRufnummer = new MeldungPositionAbbmRufnummer();
        abbmRufnummer.setRufnummer(rufnummer);
        return abbmRufnummer;
    }

    public MeldungPositionAbbmRufnummerBuilder withRufnummer(String rufnummer) {
        this.rufnummer = rufnummer;
        return this;
    }

}

