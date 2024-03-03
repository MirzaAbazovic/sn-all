/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.10.13
 */
package de.mnet.wbci.service.impl.validation;

import static de.augustakom.common.service.holiday.DateCalculationHelper.*;
import static de.mnet.wbci.TestGroups.*;
import static de.mnet.wbci.model.GeschaeftsfallTyp.*;
import static de.mnet.wbci.model.WbciCdmVersion.*;

import java.time.*;
import java.util.*;
import javax.validation.*;
import org.apache.commons.lang.ArrayUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.RueckmeldungVorabstimmung;
import de.mnet.wbci.model.Rufnummernportierung;
import de.mnet.wbci.model.builder.MeldungPositionRueckmeldungVaTestBuilder;
import de.mnet.wbci.model.builder.RueckmeldungVorabstimmungTestBuilder;
import de.mnet.wbci.model.builder.RufnummernportierungAnlageTestBuilder;
import de.mnet.wbci.model.builder.RufnummernportierungEinzelnTestBuilder;
import de.mnet.wbci.model.builder.TechnischeRessourceTestBuilder;
import de.mnet.wbci.validation.groups.V1MeldungVaKueMrn;
import de.mnet.wbci.validation.groups.V1MeldungVaKueMrnWarn;

@Test(groups = UNIT)
@SuppressWarnings("unchecked")
public class ValidateRuemVaWithGfVaKueMrnTest extends ValidateBase {

    @Override
    public Class<?> getErrorGroup() {
        return V1MeldungVaKueMrn.class;
    }

    @Override
    public Class<?> getWarnGroup() {
        return V1MeldungVaKueMrnWarn.class;
    }

    @Override
    public GeschaeftsfallTyp getGeschaeftsfallTyp() {
        return VA_KUE_MRN;
    }

    @DataProvider(name = "ruemVaDates")
    public Object[][] ruemVaDates() {
        // [withWbciGeschaeftsfall, kwt, ruemVaTermin, constraints]
        Object[][] values = new Object[][] {
                { true, getDateInWorkingDaysFromNowAndNextDayNotHoliday(0),
                        getDateInWorkingDaysFromNow(4), 1, 0 },
                { true, getDateInWorkingDaysFromNowAndNextDayNotHoliday(0),
                        getDateInWorkingDaysFromNowAndNextDayNotHoliday(5).atStartOfDay(), 0, 1 },
                { true, getDateInWorkingDaysFromNowAndNextDayNotHoliday(0),
                        getDateInWorkingDaysFromNowAndNextDayNotHoliday(7).atStartOfDay(), 0, 1 },
                { true, getDateInWorkingDaysFromNowAndNextDayNotHoliday(0),
                        getDateInWorkingDaysFromNowAndNextDayNotHoliday(8).atStartOfDay(), 0, 0 },
                { true, getDateInWorkingDaysFromNowAndNextDayNotHoliday(0),
                        getDateInWorkingDaysFromNow(-1), 1, 1 },
                { false, null, getDateInWorkingDaysFromNowAndNextDayNotHoliday(5).atStartOfDay(), 1, 1 },
                { true, null, getDateInWorkingDaysFromNowAndNextDayNotHoliday(5).atStartOfDay(), 1, 1 },
                { true, getDateInWorkingDaysFromNowAndNextDayNotHoliday(0), null, 1, 0 },
                { true, null, null, 2, 0 },
        };

        boolean checkOf5WorkingDaysInFutureRemoved = false;
        if (DateCalculationHelper.getDaysBetween(LocalDateTime.now().toLocalDate(),
                getDateInWorkingDaysFromNowAndNextDayNotHoliday(5), DateCalculationHelper.DateCalculationMode.WORKINGDAYS) > 5) {
            values = (Object[][]) ArrayUtils.remove(values, 1);
            checkOf5WorkingDaysInFutureRemoved = true;
        }

        if (DateCalculationHelper.getDaysBetween(LocalDateTime.now().toLocalDate(),
                getDateInWorkingDaysFromNowAndNextDayNotHoliday(7), DateCalculationHelper.DateCalculationMode.WORKINGDAYS) > 7) {
            int indexToRemove = (checkOf5WorkingDaysInFutureRemoved) ? 1 : 2;
            values = (Object[][]) ArrayUtils.remove(values, indexToRemove);
        }

        return values;
    }

    @Test(dataProvider = "ruemVaDates")
    public void testWeschseltermin(boolean withWbciGeschaeftsfall, LocalDate kwt, LocalDateTime ruemVaTermin,
            int expectedErrors, int expectedWarnings) throws Exception {

        RueckmeldungVorabstimmung ruemVa = new RueckmeldungVorabstimmungTestBuilder()
                .buildValid(V1, getGeschaeftsfallTyp());
        ruemVa.setWechseltermin(ruemVaTermin != null ? ruemVaTermin.toLocalDate() : null);
        if (withWbciGeschaeftsfall) {
            ruemVa.getWbciGeschaeftsfall().setKundenwunschtermin(kwt);
        }
        else {
            ruemVa.setWbciGeschaeftsfall(null);
        }
        Set<ConstraintViolation<RueckmeldungVorabstimmung>> errorsSet =
                checkMessageForErrors(V1, ruemVa, getErrorGroup());
        assertConstraintViolationSet(errorsSet, expectedErrors);

        Set<ConstraintViolation<RueckmeldungVorabstimmung>> warningsSet =
                checkMessageForWarnings(V1, ruemVa, getWarnGroup());
        assertConstraintViolationSet(warningsSet, expectedWarnings);
    }

    /**
     * a more detail test is not necessary, see Test of {@link de.mnet.wbci.validation.constraints.CheckRuemVaMandatoryFields}.
     */
    @Test
    public void testMandatoryFieldValidation() throws Exception {
        RueckmeldungVorabstimmung ruemVa = new RueckmeldungVorabstimmungTestBuilder().buildValid(V1, VA_KUE_MRN);
        ruemVa.setTechnischeRessourcen(null);
        ruemVa.setTechnologie(null);
        assertConstraintViolationSet(checkMessageForErrors(V1, ruemVa, getErrorGroup()), 1);
    }

    /**
     * a more detail test is not necessary, see Test of {@link de.mnet.wbci.validation.constraints.CheckRuemVaMeldungscodes}.
     */
    @Test
    public void testRuemVaMeldungscodesValidation() throws Exception {
        RueckmeldungVorabstimmung ruemVa = new RueckmeldungVorabstimmungTestBuilder()
                .addMeldungPosition(
                        new MeldungPositionRueckmeldungVaTestBuilder().withMeldungsCode(MeldungsCode.ZWA).build())
                .addMeldungPosition(
                        new MeldungPositionRueckmeldungVaTestBuilder().withMeldungsCode(MeldungsCode.NAT).build())
                .buildValid(V1, VA_KUE_MRN);
        assertConstraintViolationSet(checkMessageForErrors(V1, ruemVa, getErrorGroup()), 1);

        ruemVa = new RueckmeldungVorabstimmungTestBuilder()
                .addMeldungPosition(
                        new MeldungPositionRueckmeldungVaTestBuilder().withMeldungsCode(MeldungsCode.ZWA).build())
                .addMeldungPosition(
                        new MeldungPositionRueckmeldungVaTestBuilder().withMeldungsCode(MeldungsCode.ADAHSNR)
                                .buildValid(V1, VA_KUE_MRN)
                )
                .buildValid(V1, VA_KUE_MRN);
        assertConstraintViolationSet(checkMessageForErrors(V1, ruemVa, getErrorGroup()), 0);
    }

    /**
     * a more detail test is not necessary, see Test of {@link de.mnet.wbci.validation.constraints.CheckRuemVaMeldungscodeADA}.
     */
    @Test
    public void testRuemVaMeldungscodesADAValidation() throws Exception {
        RueckmeldungVorabstimmung ruemVa = new RueckmeldungVorabstimmungTestBuilder()
                .addMeldungPosition(
                        new MeldungPositionRueckmeldungVaTestBuilder().withMeldungsCode(MeldungsCode.ZWA).build())
                .addMeldungPosition(
                        new MeldungPositionRueckmeldungVaTestBuilder().withMeldungsCode(MeldungsCode.ADAHSNR)
                                .buildValid(V1, VA_KUE_MRN)
                )
                .buildValid(V1, VA_KUE_MRN);
        assertConstraintViolationSet(checkMessageForErrors(V1, ruemVa, getErrorGroup()), 0);
    }

    /**
     * a more detail test is not necessary, see Test of {@link de.mnet.wbci.validation.constraints.CheckTechnischeRessourceMandatoryFields}.
     */
    @Test
    public void testTechnischeRessourceValidation() throws Exception {
        //valid
        RueckmeldungVorabstimmung ruemVa = new RueckmeldungVorabstimmungTestBuilder()
                .addTechnischeRessource(
                        new TechnischeRessourceTestBuilder().withVertragsnummer("WITA-Vtr").withTnbKennungAbg("D102").build())
                .buildValid(V1, VA_KUE_MRN);
        assertConstraintViolationSet(checkMessageForErrors(V1, ruemVa, getErrorGroup()), 0);

        //valid => TNB abgebend has no longer to be set
        ruemVa = new RueckmeldungVorabstimmungTestBuilder()
                .addTechnischeRessource(
                        new TechnischeRessourceTestBuilder().withVertragsnummer("WITA-Vtr").build())
                .buildValid(V1, VA_KUE_MRN);
        assertConstraintViolationSet(checkMessageForErrors(V1, ruemVa, getErrorGroup()), 0);

        // not valid => Line-Id and Wita-VertNr is not valid
        ruemVa = new RueckmeldungVorabstimmungTestBuilder()
                .addTechnischeRessource(
                        new TechnischeRessourceTestBuilder()
                                .withLineId("LINE-ID")
                                .withVertragsnummer("WITA-Vtr")
                                .withTnbKennungAbg("D102").build()
                )
                .buildValid(V1, VA_KUE_MRN);
        assertConstraintViolationSet(checkMessageForErrors(V1, ruemVa, getErrorGroup()), 1);

        //valid
        ruemVa = new RueckmeldungVorabstimmungTestBuilder()
                .addTechnischeRessource(
                        new TechnischeRessourceTestBuilder().withIdentifizierer("LINE-ID").withTnbKennungAbg("D102").build())
                .buildValid(V1, VA_KUE_MRN);
        assertConstraintViolationSet(checkMessageForErrors(V1, ruemVa, getErrorGroup()), 0);

        //valid
        ruemVa = new RueckmeldungVorabstimmungTestBuilder()
                .addTechnischeRessource(
                        new TechnischeRessourceTestBuilder().withIdentifizierer("LINE-ID").build())
                .buildValid(V1, VA_KUE_MRN);
        assertConstraintViolationSet(checkMessageForErrors(V1, ruemVa, getErrorGroup()), 0);
    }

    @DataProvider
    public Object[][] ruemVaRnpDP() {
        return new Object[][] {
                { new RufnummernportierungEinzelnTestBuilder().buildValid(V1, getGeschaeftsfallTyp()), 1, 0 },
                { new RufnummernportierungAnlageTestBuilder().buildValid(V1, getGeschaeftsfallTyp()), 0, 0 },
        };
    }

    @Test(dataProvider = "ruemVaRnpDP")
    public void testRufnummern(Rufnummernportierung rnp, int expectedErrors, int expectedWarnings) throws Exception {
        RueckmeldungVorabstimmung ruemVa = new RueckmeldungVorabstimmungTestBuilder()
                .withRufnummernportierung(rnp)
                .buildValid(V1, getGeschaeftsfallTyp());

        Set<ConstraintViolation<RueckmeldungVorabstimmung>> errorsSet =
                checkMessageForErrors(V1, ruemVa, getErrorGroup());
        assertConstraintViolationSet(errorsSet, expectedErrors);

        Set<ConstraintViolation<RueckmeldungVorabstimmung>> warningsSet =
                checkMessageForWarnings(V1, ruemVa, getWarnGroup());
        assertConstraintViolationSet(warningsSet, expectedWarnings);
    }

}
