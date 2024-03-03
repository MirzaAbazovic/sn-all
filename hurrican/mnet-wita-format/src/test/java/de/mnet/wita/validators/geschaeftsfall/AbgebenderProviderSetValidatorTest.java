/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.07.2011
 */
package de.mnet.wita.validators.geschaeftsfall;

import static de.mnet.wita.TestGroups.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import javax.validation.*;
import javax.validation.ConstraintValidatorContext.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.auftrag.geschaeftsfall.Geschaeftsfall;
import de.mnet.wita.message.auftrag.geschaeftsfall.GeschaeftsfallNeu;
import de.mnet.wita.message.builder.auftrag.geschaeftsfall.GeschaeftsfallBuilder;
import de.mnet.wita.message.common.Carrier;
import de.mnet.wita.validators.geschaeftsfall.AbgebenderProviderSetForGeschaeftsfall.AbgebenderProviderSetValidator;

@Test(groups = UNIT)
public class AbgebenderProviderSetValidatorTest {

    @DataProvider
    public Object[][] dataProviderIsValid() {
        Geschaeftsfall validPv = (new GeschaeftsfallBuilder(GeschaeftsfallTyp.PROVIDERWECHSEL)).buildValid();
        Geschaeftsfall validRexMk = (new GeschaeftsfallBuilder(GeschaeftsfallTyp.RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG))
                .buildValid();

        // @formatter:off
        return new Object[][] {
                { new GeschaeftsfallNeu(), false, false, null, true },

                { validPv, false, false, null, false },
                { validRexMk, true, true, Carrier.OTHER, false },

                { validPv, true, false, null, true },
                { validPv, true, false, Carrier.DTAG, true },
                { validPv, true, false, Carrier.OTHER, true },

                { validPv, true, true, Carrier.OTHER, true },
                { validRexMk, true, true, Carrier.DTAG, true },
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderIsValid")
    public void testIsValid(Geschaeftsfall geschaeftsfall, boolean present, boolean checkCarrier,
            Carrier expectedCarrier, boolean expectedResult) {
        ConstraintViolationBuilder builder = mock(ConstraintViolationBuilder.class);
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);

        AbgebenderProviderSetForGeschaeftsfall constraintAnnotation = mock(AbgebenderProviderSetForGeschaeftsfall.class);
        when(constraintAnnotation.permitted()).thenReturn(present);
        when(constraintAnnotation.checkCarrier()).thenReturn(checkCarrier);
        when(constraintAnnotation.expectedCarrier()).thenReturn(expectedCarrier);

        AbgebenderProviderSetValidator cut = new AbgebenderProviderSetValidator();
        cut.initialize(constraintAnnotation);
        assertEquals(cut.isValid(geschaeftsfall, context), expectedResult);
    }
}
