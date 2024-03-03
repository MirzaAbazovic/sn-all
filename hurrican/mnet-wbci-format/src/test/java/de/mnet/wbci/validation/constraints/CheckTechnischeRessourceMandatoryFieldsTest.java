/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.10.13
 */
package de.mnet.wbci.validation.constraints;

import static de.mnet.wbci.TestGroups.*;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.mnet.wbci.model.TechnischeRessource;
import de.mnet.wbci.model.builder.TechnischeRessourceTestBuilder;

@Test(groups = UNIT)
public class CheckTechnischeRessourceMandatoryFieldsTest extends AbstractValidatorTest<CheckTechnischeRessourceMandatoryFields.MandatoryFieldsValidator> {


    @Override
    protected CheckTechnischeRessourceMandatoryFields.MandatoryFieldsValidator createTestling() {
        return new CheckTechnischeRessourceMandatoryFields.MandatoryFieldsValidator();
    }

    @BeforeMethod
    @Override
    public void setUp() throws Exception {
        super.setUp();
        testling.defaultMessage = "%s";
    }

    @DataProvider(name = "validation")
    public Object[][] validationErrors() {
        // @formatter:off
        return new Object[][] {
                {null   ,null       , null  , null      , false},
                {""     ,""         , null  , null      , false},
                {""     ,""         , null  , "TnbAbg"  , false},
                {"WITA" ,""         , null  , "TnbAbg"  , true},
                {"WITA" ,""         , null  , ""        , true},
                {"WITA" ,""         , null  , null      , true},
                {"WITA" ,null       , null  , "TnbAbg"  , true},
                {""     ,"LINE-ID"  , null  , "TnbAbg"  , true},
                {null   ,"LINE-ID"  , null  , "TnbAbg"  , true},
                {"WITA" ,"LINE-ID"  , null  , ""        , false},
                {"WITA" ,"LINE-ID"  , null  , "TnbAbg"  , false},
                {"WITA" ,null       , "Id"  , "TnbAbg"  , true},
                {null   ,null       , "Id"  , null      , true},
        };
        // @formatter:on
    }

    @Test(dataProvider = "validation")
    public void testCheckKundenwunschtermin(String witaVertNr, String wbciLineId, String identifier, String tnbAbg,
            boolean valid) throws Exception {
        final TechnischeRessource technischeRessource =
                new TechnischeRessourceTestBuilder()
                        .withVertragsnummer(witaVertNr)
                        .withLineId(wbciLineId)
                        .withIdentifizierer(identifier)
                        .withTnbKennungAbg(tnbAbg)
                        .build();
        Assert.assertEquals(testling.isValid(technischeRessource, contextMock), valid);
        assertErrorMessageSet(valid);
    }

}
