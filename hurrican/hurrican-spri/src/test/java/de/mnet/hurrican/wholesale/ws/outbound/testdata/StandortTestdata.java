/*

 * Copyright (c) 2017 - M-net Telekommunikations GmbH

 * All rights reserved.

 * -------------------------------------------------------

 * File created: 07.02.2017

 */

package de.mnet.hurrican.wholesale.ws.outbound.testdata;


import de.mnet.wbci.model.Standort;
import de.mnet.wbci.model.Strasse;

/**
 * Created by wieran on 07.02.2017.
 */
public class StandortTestdata {

    public static Standort createStandort() {
        Standort adresse = new Standort();
        Strasse strasse = new Strasse();
        strasse.setHausnummer("42");
        strasse.setStrassenname("hinterhof");
        strasse.setHausnummernZusatz("a");
        adresse.setOrt("Ort");
        adresse.setStrasse(strasse);
        adresse.setPostleitzahl("123456");
        return adresse;
    }
}
