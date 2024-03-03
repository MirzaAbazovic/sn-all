/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.10.2011 11:55:55
 */
package de.mnet.wita.validators;

import static de.mnet.wita.TestGroups.*;
import static org.testng.Assert.*;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.mnet.wita.message.auftrag.Anrede;
import de.mnet.wita.message.common.Firmenname;

@Test(groups = UNIT)
public class FirmennameAnredeValidTest extends AbstractValidationTest<Firmenname> {

    @DataProvider
    public Object[][] testCases() {
        // @formatter:off
        return new Object[][] {
                { createFirmenname(Anrede.UNBEKANNT)            , true },
                { createFirmenname(Anrede.FIRMA)                , true },
                { createFirmenname(Anrede.HERR)                 , false },
                { createFirmenname(Anrede.FRAU)                 , false },
                { createFirmennameMitZeilenumbruch(Anrede.FIRMA), false },
        };
    }

    @Test(dataProvider = "testCases")
    public void testValidation(Firmenname personenname, boolean valid) {
        violations = validator.validate(personenname);
        assertEquals(violations.isEmpty(), valid);
    }

    private Firmenname createFirmenname(Anrede anrede) {
        Firmenname firmenname = new Firmenname();
        firmenname.setErsterTeil("Test Firma ");
        firmenname.setZweiterTeil("GmbH");
        firmenname.setAnrede(anrede);
        return firmenname;
    }

    private Firmenname createFirmennameMitZeilenumbruch(Anrede anrede) {
        Firmenname firmenname = new Firmenname();
        firmenname.setErsterTeil("Test\nFirma");
        firmenname.setZweiterTeil("GmbH");
        firmenname.setAnrede(anrede);
        return firmenname;
    }

}


