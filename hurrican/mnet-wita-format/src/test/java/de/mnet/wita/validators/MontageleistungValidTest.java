/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.11.2011 15:11:46
 */
package de.mnet.wita.validators;

import static de.mnet.wita.TestGroups.*;

import java.lang.reflect.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.message.auftrag.Montageleistung;
import de.mnet.wita.message.builder.auftrag.MontageleistungBuilder;
import de.mnet.wita.message.builder.common.PersonennameBuilder;
import de.mnet.wita.tools.TestHelper;
import de.mnet.wita.validators.groups.V1;

@Test(groups = UNIT)
public class MontageleistungValidTest extends AbstractValidationTest<Montageleistung> {

    @DataProvider
    public Object[][] testCases(Method method) {
        WitaCdmVersion witaCdmVersion = TestHelper.extractWitaVersion(method);
        return new Object[][] {
                { new MontageleistungBuilder().buildValid(), true },
                { new MontageleistungBuilder().withPersonenname(new PersonennameBuilder()
                        .withNachname("Meier").buildValid()).buildValid(), true },
                { new MontageleistungBuilder().withPersonenname(new PersonennameBuilder()
                        .withNachname("ToLongToLongToLongToLongToLongToLongToLong").buildValid()).buildValid(), false },
                { new MontageleistungBuilder().withMontagehinweis("1").buildValid(), true },
                { new MontageleistungBuilder().withEmailadresse("").buildValid(), true },
                { new MontageleistungBuilder().withEmailadresse("foo@m-met.de").buildValid(), true },
                { new MontageleistungBuilder().withTelefonnummer("").buildValid(), false },
                { new MontageleistungBuilder().withTelefonnummer("0821 1815678").buildValid(), true },
        };
    }

    @Test(dataProvider = "testCases")
    public void testValidationV1(Montageleistung montageleistung, boolean valid) {
        checkValidation(montageleistung, valid, V1.class);
    }

}
