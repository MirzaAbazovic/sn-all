/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.11.2011 15:34:51
 */
package de.mnet.wita.message.meldung;

import static de.mnet.wita.TestGroups.*;
import static de.mnet.wita.message.GeschaeftsfallTyp.*;
import static org.testng.Assert.*;

import org.testng.annotations.Test;

import de.mnet.wita.message.Auftrag;
import de.mnet.wita.message.auftrag.Kunde;
import de.mnet.wita.message.builder.AuftragBuilder;

@Test(groups = UNIT)
public class ErledigtMeldungKundeTest {

    public void createFromAuftragSetsKundenNummerBesteller() {
        Auftrag auftrag = new AuftragBuilder(BEREITSTELLUNG).buildValid();

        Kunde besteller = new Kunde();
        besteller.setKundennummer("123456");
        auftrag.setBesteller(besteller);

        ErledigtMeldungKunde erlmk = new ErledigtMeldungKunde("9876", auftrag);
        assertEquals(erlmk.getKundennummerBesteller(), "123456");
    }

}
