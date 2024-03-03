/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.10.2011 11:35:51
 */
package de.mnet.wita.validators;

import static de.mnet.wita.TestGroups.*;
import static org.testng.Assert.*;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.mnet.wita.message.auftrag.Anrede;
import de.mnet.wita.message.builder.common.PersonennameBuilder;
import de.mnet.wita.message.common.Personenname;

@Test(groups = UNIT)
public class PersonennameValidationTest extends AbstractValidationTest<Personenname> {

    @DataProvider
    public Object[][] testCases() {
        // @formatter:off
        return new Object[][] {
                { new PersonennameBuilder().withAnrede(Anrede.HERR).buildValid()         , true },
                { new PersonennameBuilder().withAnrede(Anrede.FRAU).buildValid()         , true },
                { new PersonennameBuilder().withAnrede(Anrede.UNBEKANNT).buildValid()    , true },
                { new PersonennameBuilder().withAnrede(Anrede.FIRMA).buildValid()        , false },
                { new PersonennameBuilder().withVorname("SeppDerDepp")
                        .withNachname("HuberHuberHuberHuberHuberHuberHuber").buildValid(), false },
                { new PersonennameBuilder().withVorname("SeppDerDepp")
                        .withNachname("Huber\nHuber").buildValid()                       , false },
        };
    }

    @Test(dataProvider = "testCases")
    public void testValidation(Personenname personenname, boolean valid) {
        violations = validator.validate(personenname);
        assertEquals(violations.isEmpty(), valid);
    }

}
