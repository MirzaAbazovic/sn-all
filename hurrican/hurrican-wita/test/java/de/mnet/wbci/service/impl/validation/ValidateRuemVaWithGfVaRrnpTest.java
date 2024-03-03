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
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.RueckmeldungVorabstimmung;
import de.mnet.wbci.model.builder.MeldungPositionRueckmeldungVaTestBuilder;
import de.mnet.wbci.model.builder.RueckmeldungVorabstimmungTestBuilder;
import de.mnet.wbci.validation.groups.V1MeldungVaRrnp;
import de.mnet.wbci.validation.groups.V1MeldungVaRrnpWarn;

@Test(groups = UNIT)
@SuppressWarnings("unchecked")
public class ValidateRuemVaWithGfVaRrnpTest extends ValidateBase {

    @Override
    public Class<?> getErrorGroup() {
        return V1MeldungVaRrnp.class;
    }

    @Override
    public Class<?> getWarnGroup() {
        return V1MeldungVaRrnpWarn.class;
    }

    @Override
    public GeschaeftsfallTyp getGeschaeftsfallTyp() {
        return VA_RRNP;
    }

    @DataProvider(name = "ruemVaDates")
    public Object[][] ruemVaDates() {
        return new Object[][] {
                // {kwt, ruemVaTermin, expectedErrors]
                { getDateInWorkingDaysFromNow(0).toLocalDate(), getDateInWorkingDaysFromNow(0), 1 },
                { getDateInWorkingDaysFromNow(0).toLocalDate(), getDateInWorkingDaysFromNow(1), 0 },
                { null, getDateInWorkingDaysFromNow(0), 2 },
                { null, getDateInWorkingDaysFromNow(1), 1 },
                { getDateInWorkingDaysFromNow(0).toLocalDate(), null, 1 },
                { null, null, 2 },
        };
    }

    @Test(dataProvider = "ruemVaDates")
    public void testWeschseltermin(LocalDate kwt, LocalDateTime ruemVaTermin,
            int expectedErrors) throws Exception {

        RueckmeldungVorabstimmung ruemVa = new RueckmeldungVorabstimmungTestBuilder()
                .buildValid(V1, getGeschaeftsfallTyp());
        ruemVa.setWechseltermin(ruemVaTermin != null ? ruemVaTermin.toLocalDate() : null);
        ruemVa.getWbciGeschaeftsfall().setKundenwunschtermin(kwt);
        Set<ConstraintViolation<RueckmeldungVorabstimmung>> errorsSet =
                checkMessageForErrors(V1, ruemVa, getErrorGroup());
        assertConstraintViolationSet(errorsSet, expectedErrors);

        Set<ConstraintViolation<RueckmeldungVorabstimmung>> warningsSet =
                checkMessageForWarnings(V1, ruemVa, getWarnGroup());
        assertConstraintViolationSet(warningsSet, 0);
    }

    /**
     * a more detail test is not necessary, see Test of {@link de.mnet.wbci.validation.constraints.CheckRuemVaMandatoryFields}.
     */
    @Test
    public void testMandatoryFieldValidation() throws Exception {
        RueckmeldungVorabstimmung ruemVa = new RueckmeldungVorabstimmungTestBuilder().buildValid(V1, GeschaeftsfallTyp.VA_RRNP);
        ruemVa.setTechnischeRessourcen(null);
        ruemVa.setTechnologie(null);
        assertConstraintViolationSet(checkMessageForErrors(V1, ruemVa, getErrorGroup()), 0);
    }

    /**
     * a more detail test is not necessary, see Test of {@link de.mnet.wbci.validation.constraints.CheckRuemVaMeldungscodeADA}.
     */
    @Test
    public void testRuemVaMeldungscodesADAValidation() throws Exception {
        // not valid => Hausnummer is empty
        RueckmeldungVorabstimmung ruemVa = new RueckmeldungVorabstimmungTestBuilder()
                .addMeldungPosition(
                        new MeldungPositionRueckmeldungVaTestBuilder().withMeldungsCode(MeldungsCode.ZWA).build())
                .addMeldungPosition(
                        new MeldungPositionRueckmeldungVaTestBuilder().withMeldungsCode(MeldungsCode.ADAHSNR).build())
                .buildValid(V1, GeschaeftsfallTyp.VA_RRNP);
        assertConstraintViolationSet(checkMessageForErrors(V1, ruemVa, getErrorGroup()), 1);

    }
}
