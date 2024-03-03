/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.06.2011 12:10:40
 */
package de.mnet.wita.validators;

import static de.mnet.wita.TestGroups.*;
import static de.mnet.wita.message.GeschaeftsfallTyp.*;
import static de.mnet.wita.message.common.Uebertragungsverfahren.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import javax.validation.*;
import javax.validation.ConstraintValidatorContext.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.mnet.wita.message.Auftrag;
import de.mnet.wita.message.auftrag.Auftragsposition;
import de.mnet.wita.message.auftrag.Auftragsposition.ProduktBezeichner;
import de.mnet.wita.message.auftrag.GeschaeftsfallProdukt;
import de.mnet.wita.message.auftrag.Produkt;
import de.mnet.wita.message.auftrag.Schaltangaben;
import de.mnet.wita.message.auftrag.SchaltungKupfer;
import de.mnet.wita.message.builder.AuftragBuilder;
import de.mnet.wita.message.common.Uebertragungsverfahren;
import de.mnet.wita.validators.SchaltangabenValid.SchaltangabenValidator;

@Test(groups = UNIT)
public class SchaltangabenValidatorTest extends AbstractValidationTest<Auftragsposition> {

    public void validateEmptySchaltangabenExpectViolation() {
        Auftragsposition ap = new Auftragsposition();
        ap.setProdukt(Produkt.TAL);
        ap.setGeschaeftsfallProdukt(new GeschaeftsfallProdukt());
        ap.setProduktBezeichner(ProduktBezeichner.HVT_2N);
        ap.getGeschaeftsfallProdukt().setSchaltangaben(new Schaltangaben());

        violations = validator.validate(ap);
        assertThat(violations, hasSize(1));
    }

    public void validateEmptySchaltangabenExpectOk() {
        Auftrag auftrag = new AuftragBuilder(BEREITSTELLUNG).buildValid();
        Auftragsposition auftragsPosition = auftrag.getGeschaeftsfall().getAuftragsPosition();

        auftragsPosition.setProduktBezeichner(ProduktBezeichner.HVT_2N);
        auftragsPosition.getGeschaeftsfallProdukt().getSchaltangaben()
                .setSchaltungKupfer(Arrays.asList(schaltungKupfer(H04)));

        violations = validator.validate(auftragsPosition);
        assertThat(violations, hasSize(0));
    }

    @DataProvider
    public Object[][] dataProviderCheckIfSameUetv() {
        // @formatter:off
        return new Object[][] {
                { Arrays.asList(schaltungKupfer(H04)), true },
                { Arrays.asList(schaltungKupfer(H04), schaltungKupfer(H04)), true },
                { Arrays.asList(schaltungKupfer(H04), schaltungKupfer(H13)), false },
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderCheckIfSameUetv")
    public void testCheckIfSameUetv(List<SchaltungKupfer> schaltungKupfer, boolean expected) {
        SchaltangabenValidator cut = new SchaltangabenValidator();

        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
        ConstraintViolationBuilder builder = mock(ConstraintViolationBuilder.class);
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);

        Schaltangaben schaltangaben = new Schaltangaben();
        schaltangaben.setSchaltungKupfer(schaltungKupfer);

        if (cut.checkIfSameUetv(context, schaltangaben)) {
            assertTrue(expected);
        }
        else {
            assertFalse(expected);
            verify(context).buildConstraintViolationWithTemplate(anyString());
            verify(builder).addConstraintViolation();
        }
    }

    @DataProvider
    public Object[][] dataProviderIsExactlyOneUnequalToZero() {
        // @formatter:off
        return new Object[][] {
                { 0, 0, 0, false },
                { 0, 1, 1, false },
                { 1, 0, 1, false },
                { 1, 1, 0, false },
                { 1, 1, 1, false },
                { 2, 0, 1, false },
                { 1, 2, 0, false },
                { 1, 1, 2, false },

                { 0, 0, 1, true },
                { 0, 1, 0, true },
                { 1, 0, 0, true },

                { 0, 0, 2, true },
                { 0, 2, 0, true },
                { 2, 0, 0, true },
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderIsExactlyOneUnequalToZero")
    public void testIsExactlyOneUnequalToZero(int v1, int v2, int v3, boolean expectedResult) {
        SchaltangabenValidator cut = new SchaltangabenValidator();
        boolean result = cut.isExactlyOneUnequalToZero(v1, v2, v3);
        assertEquals(result, expectedResult);
    }

    private SchaltungKupfer schaltungKupfer(Uebertragungsverfahren uetv) {
        SchaltungKupfer schaltungKupfer = new SchaltungKupfer();
        schaltungKupfer.setUEVT("0101");
        schaltungKupfer.setDoppelader("01");
        schaltungKupfer.setEVS("01");
        schaltungKupfer.setUebertragungsverfahren(uetv);
        return schaltungKupfer;
    }
}
