/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.11.13
 */
package de.mnet.wbci.service.impl.validation;

import static de.mnet.wbci.TestGroups.*;
import static de.mnet.wbci.model.WbciCdmVersion.*;

import java.util.*;
import javax.validation.*;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wbci.model.builder.PersonTestBuilder;
import de.mnet.wbci.model.builder.StandortTestBuilder;
import de.mnet.wbci.model.builder.StornoAenderungAbgAnfrageTestBuilder;
import de.mnet.wbci.model.builder.StornoAufhebungAbgAnfrageTestBuilder;
import de.mnet.wbci.validation.groups.V1Request;
import de.mnet.wbci.validation.groups.V1RequestTvWarn;

@Test(groups = UNIT)
@SuppressWarnings("unchecked")
public class ValidateRequestStronoTest extends ValidateBase {

    public Class<?> getErrorGroup() {
        return V1Request.class;
    }

    public Class<?> getWarnGroup() {
        return V1RequestTvWarn.class;
    }

    @Override
    public GeschaeftsfallTyp getGeschaeftsfallTyp() {
        return GeschaeftsfallTyp.VA_KUE_MRN;
    }

    @DataProvider(name = "stornos")
    public Object[][] stornos() {
        // @formatter:off
        // [request, constraints]
        return new Object[][] {
                { new StornoAenderungAbgAnfrageTestBuilder<>().buildValid(V1, getGeschaeftsfallTyp()), 0},
                { new StornoAufhebungAbgAnfrageTestBuilder<>().buildValid(V1, getGeschaeftsfallTyp()), 0},
                { new StornoAenderungAbgAnfrageTestBuilder<>().withEndkunde(new PersonTestBuilder().buildValid(V1, getGeschaeftsfallTyp())).buildValid(V1, getGeschaeftsfallTyp()), 1},
                { new StornoAufhebungAbgAnfrageTestBuilder<>().withEndkunde(new PersonTestBuilder().buildValid(V1, getGeschaeftsfallTyp())).buildValid(V1, getGeschaeftsfallTyp()), 1},
                { new StornoAenderungAbgAnfrageTestBuilder<>().withStandort(new StandortTestBuilder().buildValid(V1, getGeschaeftsfallTyp())).buildValid(V1, getGeschaeftsfallTyp()), 1},
                { new StornoAufhebungAbgAnfrageTestBuilder<>().withStandort(new StandortTestBuilder().buildValid(V1,getGeschaeftsfallTyp())).buildValid(V1, getGeschaeftsfallTyp()), 1},
                { new StornoAenderungAbgAnfrageTestBuilder<>().withEndkunde(new PersonTestBuilder().buildValid(V1, getGeschaeftsfallTyp())).withStandort(new StandortTestBuilder().buildValid(V1, getGeschaeftsfallTyp())).buildValid(V1, getGeschaeftsfallTyp()), 2},
                { new StornoAufhebungAbgAnfrageTestBuilder<>().withEndkunde(new PersonTestBuilder().buildValid(V1, getGeschaeftsfallTyp())).withStandort(new StandortTestBuilder().buildValid(V1, getGeschaeftsfallTyp())).buildValid(V1, getGeschaeftsfallTyp()), 2},

        };
        // @formatter:on
    }

    @Test(dataProvider = "stornos")
    public void testCheckRequestForErrorsWithValidRequest(WbciRequest wbciRequest, int expectedValidations)
            throws Exception {
        Set<ConstraintViolation<WbciRequest>> violations = checkMessageForErrors(V1, wbciRequest, getErrorGroup());
        Assert.assertEquals(violations.size(), expectedValidations);
    }

}
