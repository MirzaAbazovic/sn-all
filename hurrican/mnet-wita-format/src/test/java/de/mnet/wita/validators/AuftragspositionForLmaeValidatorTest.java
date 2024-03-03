/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.10.2011 11:37:39
 */
package de.mnet.wita.validators;

import static de.mnet.wita.TestGroups.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import javax.validation.*;
import javax.validation.ConstraintValidatorContext.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.mnet.wita.message.auftrag.Auftragsposition;
import de.mnet.wita.message.auftrag.Auftragsposition.ProduktBezeichner;
import de.mnet.wita.message.auftrag.Schaltangaben;
import de.mnet.wita.message.auftrag.SchaltungKupfer;
import de.mnet.wita.message.builder.auftrag.AuftragspositionBuilder;
import de.mnet.wita.message.common.Uebertragungsverfahren;
import de.mnet.wita.validators.AuftragspositionValidForLmae.AuftragspositionForLmaeValidator;

@Test(groups = UNIT)
public class AuftragspositionForLmaeValidatorTest {

    @Mock
    private ConstraintValidatorContext context;
    @Mock
    private ConstraintViolationBuilder constraintViolationBuilder;

    private AuftragspositionForLmaeValidator validator;

    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(constraintViolationBuilder);

        validator = new AuftragspositionForLmaeValidator();
    }

    public void validateEmpty() {
        Auftragsposition auftragsposition = new AuftragspositionBuilder().build();
        assertNotValid(auftragsposition, AuftragspositionForLmaeValidator.ALTE_SCHALTANGABEN_MUESSEN_GESETZT_SEIN);
    }

    public void validateOldSchaltangabenWithoutSchaltungKupfer() {
        Auftragsposition auftragsposition = new AuftragspositionBuilder().build();
        auftragsposition.getGeschaeftsfallProdukt().setSchaltangaben(new Schaltangaben());
        assertNotValid(auftragsposition,
                AuftragspositionForLmaeValidator.FALSCHE_ANZAHL_ALTER_SCHALTANGABEN);
    }

    public void validateOldSchaltangabenWithoutUevt() {
        Auftragsposition auftragsposition = new AuftragspositionBuilder().build();
        Schaltangaben schaltangaben = new Schaltangaben();
        schaltangaben.setSchaltungKupfer(ImmutableList.of(new SchaltungKupfer()));
        auftragsposition.getGeschaeftsfallProdukt().setSchaltangaben(schaltangaben);

        assertNotValid(auftragsposition,
                AuftragspositionForLmaeValidator.ALTE_SCHALTANGABEN_MUESSEN_UEBERTRAGUNGSVERFAHREN_GESETZT_HABEN);
    }

    public void validateWithoutSchaltangaben() {
        Auftragsposition auftragsposition = createAuftragsposition(Uebertragungsverfahren.H03);
        auftragsposition.setPosition(new AuftragspositionBuilder().build());

        assertNotValid(auftragsposition,
                AuftragspositionForLmaeValidator.NEUE_SCHALTANGABEN_MUESSEN_GESETZT_SEIN);
    }

    public void validateSchaltangabenWithoutSchaltungKupfer() {
        Auftragsposition auftragsposition = createAuftragsposition(Uebertragungsverfahren.H03);

        Auftragsposition subPosition = new AuftragspositionBuilder().build();
        subPosition.getGeschaeftsfallProdukt().setSchaltangaben(new Schaltangaben());
        auftragsposition.setPosition(subPosition);

        assertNotValid(auftragsposition,
                AuftragspositionForLmaeValidator.FALSCHE_ANZAHL_SCHALTANGABEN);
    }

    public void validateSchaltangabenWithoutUevt() {
        Auftragsposition auftragsposition = createAuftragsposition(Uebertragungsverfahren.H03);

        Auftragsposition subPosition = new AuftragspositionBuilder().build();
        Schaltangaben schaltangaben = new Schaltangaben();
        schaltangaben.setSchaltungKupfer(ImmutableList.of(new SchaltungKupfer()));
        subPosition.getGeschaeftsfallProdukt().setSchaltangaben(schaltangaben);
        auftragsposition.setPosition(subPosition);

        assertNotValid(auftragsposition,
                AuftragspositionForLmaeValidator.NEUE_SCHALTANGABEN_MUESSEN_UEBERTRAGUNGSVERFAHREN_GESETZT_HABEN);
    }

    public void validateUnchangedUevt() {
        Auftragsposition auftragsposition = createAuftragsposition(Uebertragungsverfahren.H03);
        createSubPosition(auftragsposition, Uebertragungsverfahren.H03);

        assertNotValid(auftragsposition,
                AuftragspositionForLmaeValidator.AUFTRAG_ENTHAELT_KEINE_AENDERUNG_AM_UEBERTRAGUNGSVERFAHREN);
    }

    public void validateWrongNumberOfOldSchaltangaben() {
        Auftragsposition auftragsposition = createAuftragsposition(Uebertragungsverfahren.H03,
                Uebertragungsverfahren.H03);
        createSubPosition(auftragsposition, Uebertragungsverfahren.H16);

        assertNotValid(auftragsposition,
                AuftragspositionForLmaeValidator.FALSCHE_ANZAHL_ALTER_SCHALTANGABEN);
    }

    public void validateWrongNumberOfSchaltangaben() {
        Auftragsposition auftragsposition = createAuftragsposition(Uebertragungsverfahren.H03);
        createSubPosition(auftragsposition, Uebertragungsverfahren.H16, Uebertragungsverfahren.H16);

        assertNotValid(auftragsposition,
                AuftragspositionForLmaeValidator.FALSCHE_ANZAHL_SCHALTANGABEN);
    }

    public void validateValid() {
        Auftragsposition auftragsposition = createAuftragsposition(Uebertragungsverfahren.H03);
        createSubPosition(auftragsposition, Uebertragungsverfahren.H16);

        assertTrue(validator.isValid(auftragsposition, context));
    }

    public void validateValid96x() {
        Auftragsposition auftragsposition = create96xAuftragsposition(Uebertragungsverfahren.H03,
                Uebertragungsverfahren.H03);
        createSubPosition(auftragsposition, Uebertragungsverfahren.H16, Uebertragungsverfahren.H16);

        assertTrue(validator.isValid(auftragsposition, context));
    }

    public void validate96xWrongNumberOfOldSchaltangaben() {
        Auftragsposition auftragsposition = create96xAuftragsposition(Uebertragungsverfahren.H03);
        createSubPosition(auftragsposition, Uebertragungsverfahren.H16, Uebertragungsverfahren.H16);

        assertNotValid(auftragsposition,
                AuftragspositionForLmaeValidator.FALSCHE_ANZAHL_ALTER_SCHALTANGABEN);
    }

    public void validate96xWrongNumberOfSchaltangaben() {
        Auftragsposition auftragsposition = create96xAuftragsposition(Uebertragungsverfahren.H03,
                Uebertragungsverfahren.H03);
        createSubPosition(auftragsposition, Uebertragungsverfahren.H16);

        assertNotValid(auftragsposition,
                AuftragspositionForLmaeValidator.FALSCHE_ANZAHL_SCHALTANGABEN);
    }

    public void validate96xNotMatchingOldSchaltangaben() {
        Auftragsposition auftragsposition = create96xAuftragsposition(Uebertragungsverfahren.H03,
                Uebertragungsverfahren.H04);
        createSubPosition(auftragsposition, Uebertragungsverfahren.H16, Uebertragungsverfahren.H16);

        assertNotValid(auftragsposition,
                AuftragspositionForLmaeValidator.ALTE_UEBERTRAGUNGSVERFAHREN_BEI_96X_MUESSEN_EINDEUTIG_GESETZT_SEIN);
    }

    public void validate96xNotMatchingNewSchaltangaben() {
        Auftragsposition auftragsposition = create96xAuftragsposition(Uebertragungsverfahren.H03,
                Uebertragungsverfahren.H03);
        createSubPosition(auftragsposition, Uebertragungsverfahren.H15, Uebertragungsverfahren.H16);

        assertNotValid(auftragsposition,
                AuftragspositionForLmaeValidator.NEUE_UEBERTRAGUNGSVERFAHREN_BEI_96X_MUESSEN_EINDEUTIG_GESETZT_SEIN);
    }

    private void assertNotValid(Auftragsposition auftragsposition, String expectedMessage) {
        assertFalse(validator.isValid(auftragsposition, context));

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(context).buildConstraintViolationWithTemplate(captor.capture());
        assertEquals(captor.getValue(), expectedMessage);
    }

    private Auftragsposition create96xAuftragsposition(Uebertragungsverfahren... uebertragungsverfahren) {
        Auftragsposition auftragsposition = createAuftragsposition(uebertragungsverfahren);
        auftragsposition.setProduktBezeichner(ProduktBezeichner.HVT_4H);
        return auftragsposition;
    }

    private void createSubPosition(Auftragsposition auftragsposition, Uebertragungsverfahren... uebertragungsverfahren) {
        Auftragsposition subPosition = createAuftragsposition(uebertragungsverfahren);
        auftragsposition.setPosition(subPosition);
    }

    private Auftragsposition createAuftragsposition(Uebertragungsverfahren... uebertragungsverfahren) {
        Auftragsposition auftragsposition = new AuftragspositionBuilder().build();
        createValidSchaltangaben(auftragsposition, uebertragungsverfahren);
        return auftragsposition;
    }

    private void createValidSchaltangaben(Auftragsposition auftragsposition,
            Uebertragungsverfahren... uebertragungsverfahrenList) {

        List<SchaltungKupfer> schaltungKupferList = Lists.newArrayList();
        for (Uebertragungsverfahren uebertragungsverfahren : uebertragungsverfahrenList) {
            SchaltungKupfer schaltungKupfer = new SchaltungKupfer();
            schaltungKupfer.setUebertragungsverfahren(uebertragungsverfahren);
            schaltungKupferList.add(schaltungKupfer);
        }

        Schaltangaben schaltangaben = new Schaltangaben();
        schaltangaben.setSchaltungKupfer(schaltungKupferList);

        auftragsposition.getGeschaeftsfallProdukt().setSchaltangaben(schaltangaben);
    }

}
