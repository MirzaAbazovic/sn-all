/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.01.14
 */
package de.mnet.wbci.validation.constraints;

import static de.mnet.wbci.model.GeschaeftsfallTyp.*;
import static de.mnet.wbci.model.WbciCdmVersion.*;

import java.util.*;
import com.google.common.collect.Sets;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.RueckmeldungVorabstimmung;
import de.mnet.wbci.model.Rufnummernportierung;
import de.mnet.wbci.model.TechnischeRessource;
import de.mnet.wbci.model.Technologie;
import de.mnet.wbci.model.builder.RueckmeldungVorabstimmungTestBuilder;
import de.mnet.wbci.model.builder.RufnummernportierungEinzelnTestBuilder;
import de.mnet.wbci.model.builder.TechnischeRessourceTestBuilder;

/**
 *
 */
public class CheckRuemVaMandatoryFieldsTest extends AbstractValidatorTest<CheckRuemVaMandatoryFields.MandatoryFieldsValidator> {


    @Override
    protected CheckRuemVaMandatoryFields.MandatoryFieldsValidator createTestling() {
        return new CheckRuemVaMandatoryFields.MandatoryFieldsValidator();
    }

    @BeforeMethod
    @Override
    public void setUp() throws Exception {
        super.setUp();
        testling.fieldsToBeNotSet = new ArrayList<>();
        testling.fieldsToBeSet = new ArrayList<>();
        testling.defaultMessage = "%s, %s";
    }

    @Test
    public void testGetViolatedFieldMessage() throws Exception {
        testling.fieldsToBeNotSet.add("TO_BE_NOT_SET_FIELD");
        testling.fieldsToBeSet.add("TO_BE_SET_FIELD");
        Assert.assertTrue(testling.getViolatedFieldMessage().contains("> Die Attribute [TO_BE_NOT_SET_FIELD] dürfen nicht gesetzt sein."));
        assertErrorMessageSet(true);
        Assert.assertTrue(testling.getViolatedFieldMessage().contains("> Die Attribute [TO_BE_SET_FIELD] sind Pflichtfelder und müssen gesetzt sein."));
        assertErrorMessageSet(true);
    }

    @DataProvider(name = "validateTechnologie")
    public Object[][] validateTechnologie() {
        // Technologie, GF, valid
        return new Object[][] {
                // @formatter:off
                { Technologie.TAL_ISDN, VA_KUE_MRN, true},
                { null,                 VA_KUE_MRN, false},
                { Technologie.TAL_ISDN, VA_KUE_ORN, true},
                { null,                 VA_KUE_ORN, false},
                { Technologie.TAL_ISDN, VA_RRNP,    false},
                { null,                 VA_RRNP,    true},
        };
            // @formatter:on
    }

    @Test(dataProvider = "validateTechnologie")
    public void testValidationTechnologie(Technologie technologie, GeschaeftsfallTyp gfTyp, boolean valid) throws Exception {
        RueckmeldungVorabstimmung ruemVa = new RueckmeldungVorabstimmungTestBuilder().buildValid(V1, gfTyp);
        ruemVa.setTechnologie(technologie);

        Assert.assertEquals(testling.isValid(ruemVa, contextMock), valid);
        assertErrorMessageSet(valid);
        assertContainsField("Technologie", !valid);
    }

    @DataProvider(name = "validateTechRessource")
    public Object[][] validateTechRessource() {
        // Technologie, GF, valid
        return new Object[][] {
                // @formatter:off
                { Sets.newHashSet(new TechnischeRessourceTestBuilder().buildValid(V1, VA_KUE_MRN)), VA_KUE_MRN, true},
                { Sets.newHashSet(), VA_KUE_MRN, true},
                { null, VA_KUE_MRN, true},
                { Sets.newHashSet(new TechnischeRessourceTestBuilder().buildValid(V1, VA_KUE_ORN)), VA_KUE_ORN, true},
                { Sets.newHashSet(), VA_KUE_ORN, true},
                { null, VA_KUE_ORN, true},
                { Sets.newHashSet(new TechnischeRessourceTestBuilder().buildValid(V1, VA_RRNP)), VA_RRNP, false},
                { Sets.newHashSet(), VA_RRNP, true},
                { null, VA_RRNP, true},
        };
            // @formatter:on
    }

    @Test(dataProvider = "validateTechRessource")
    public void testValidationTechnischeRessource(Set<TechnischeRessource> technischeRessourcen, GeschaeftsfallTyp gfTyp, boolean valid) throws Exception {
        RueckmeldungVorabstimmung ruemVa = new RueckmeldungVorabstimmungTestBuilder().buildValid(V1, gfTyp);
        ruemVa.setTechnischeRessourcen(technischeRessourcen);

        Assert.assertEquals(testling.isValid(ruemVa, contextMock), valid);
        assertErrorMessageSet(valid);
        assertContainsField("technische Ressourcen", !valid);
    }


    @DataProvider(name = "validateRufnummernportierung")
    public Object[][] validateRufnummernportierung() {
        // Technologie, GF, valid
        return new Object[][] {
                // @formatter:off
                { new RufnummernportierungEinzelnTestBuilder().buildValid(V1, VA_KUE_MRN), VA_KUE_MRN, true},
                { null, VA_KUE_MRN, false},
                { new RufnummernportierungEinzelnTestBuilder().buildValid(V1, VA_KUE_ORN), VA_KUE_ORN, false},
                { null, VA_KUE_ORN, true},
                { new RufnummernportierungEinzelnTestBuilder().buildValid(V1, VA_RRNP), VA_RRNP, true},
                { null, VA_RRNP, false},
        };
        // @formatter:on
    }

    @Test(dataProvider = "validateRufnummernportierung")
    public void testValidationRufnummernportierung(Rufnummernportierung rufnummernportierung, GeschaeftsfallTyp gfTyp, boolean valid) throws Exception {
        RueckmeldungVorabstimmung ruemVa = new RueckmeldungVorabstimmungTestBuilder()
                .buildValid(V1, gfTyp);
        ruemVa.setRufnummernportierung(rufnummernportierung);
        Assert.assertEquals(testling.isValid(ruemVa, contextMock), valid);
        assertErrorMessageSet(valid);
        assertContainsField("Rufnummernportierung", !valid);
    }

    private void assertContainsField(String fieldName, boolean contain) {
        Assert.assertEquals(testling.fieldsToBeNotSet.contains(fieldName) || testling.fieldsToBeSet.contains(fieldName), contain);
    }

}
