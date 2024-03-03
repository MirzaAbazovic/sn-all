/*

 * Copyright (c) 2017 - M-net Telekommunikations GmbH

 * All rights reserved.

 * -------------------------------------------------------

 * File created: 01.03.2017

 */

package de.mnet.hurrican.wholesale.ws.outbound.testdata;


import de.mnet.wbci.model.Anrede;
import de.mnet.wbci.model.Firma;
import de.mnet.wbci.model.KundenTyp;

/**
 * Created by wieran on 01.03.2017.
 */

public class FirmaTestdata {

    /**
     * @return test firma with static data
     */
    public static Firma createFirma() {
        Firma firma = new Firma();
        firma.setFirmenname("BäckereiBrösel");
        firma.setFirmennamenZusatz("OhG");
        firma.setAnrede(Anrede.FIRMA);
        firma.setKundenTyp(KundenTyp.GK);
        return firma;
    }
}
