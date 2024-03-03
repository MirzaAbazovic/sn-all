/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.10.13
 */
package de.mnet.wbci.service.impl.validation;

import static de.mnet.wbci.TestGroups.*;

import java.util.*;
import javax.validation.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.mnet.wbci.model.Abbruchmeldung;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.MeldungPositionAbbruchmeldung;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.builder.AbbruchmeldungTestBuilder;
import de.mnet.wbci.model.builder.MeldungPositionAbbruchmeldungBuilder;
import de.mnet.wbci.model.builder.MeldungPositionAbbruchmeldungTestBuilder;
import de.mnet.wbci.validation.groups.V1MeldungVa;

@Test(groups = UNIT)
@SuppressWarnings("unchecked")
public class ValidateAbbmTest extends ValidateBase {

    @Override
    public Class<?> getErrorGroup() {
        return V1MeldungVa.class;
    }

    @Override
    public Class<?> getWarnGroup() {
        return null;
    }

    @Override
    public GeschaeftsfallTyp getGeschaeftsfallTyp() {
        return GeschaeftsfallTyp.VA_KUE_MRN;
    }

    @DataProvider(name = "abbmMeldungsCodes")
    public Object[][] abbmMeldungsCodes() {
        // @formatter:off
        // [MeldungsCodeArray, valid]
        return new Object[][] {
                {new MeldungsCode[]{MeldungsCode.ZWA, MeldungsCode.ADAHSNR},null, 2},
                {new MeldungsCode[]{MeldungsCode.ZWA, MeldungsCode.RNG}, null, 1}  ,
                {new MeldungsCode[]{MeldungsCode.ADFHSNR, MeldungsCode.RNG},null,0},
                {new MeldungsCode[]{MeldungsCode.ADFHSNR, MeldungsCode.RNG},"09984824",0},
                {new MeldungsCode[]{MeldungsCode.RNG},"0998/4824",1}, // no valid Rufnummer will be set
                {new MeldungsCode[]{MeldungsCode.ADFORT, MeldungsCode.ADFSTR},null, 0}
        };
        // @formatter:on
    }

    @Test(dataProvider = "abbmMeldungsCodes")
    public void testMeldunguscodesAndRNG(MeldungsCode[] codes, String rnr, int constraints) throws Exception {

        Abbruchmeldung meldung = new AbbruchmeldungTestBuilder()
                .buildValid(WbciCdmVersion.V1, getGeschaeftsfallTyp());

        for (MeldungsCode code : codes) {
            MeldungPositionAbbruchmeldungBuilder builder = new MeldungPositionAbbruchmeldungBuilder();
            if (rnr != null) {
                builder.withRufnummer(rnr);
            }
            meldung.getMeldungsPositionen().add(builder.withMeldungsCode(code)
                    .withMeldungsText(code.getStandardText()).build());
        }

        Set<ConstraintViolation<Abbruchmeldung>> constraintViolationSet = checkMessageForErrors(
                WbciCdmVersion.V1,
                meldung,
                getErrorGroup());
        assertConstraintViolationSet(constraintViolationSet, constraints);

    }

    @DataProvider(name = "abbmBegruendung")
    public Object[][] abbmBegruendung() {
        // @formatter:off
        // [MeldungsCodeArray, valid]
        return new Object[][] {
                {new MeldungsCode[]{MeldungsCode.SONST, MeldungsCode.ADAHSNR},null, 1},
                {new MeldungsCode[]{MeldungsCode.SONST, MeldungsCode.ADFORT},null, 0},
                {new MeldungsCode[]{MeldungsCode.SONST, MeldungsCode.ADFORT},"BAL",0},
                {new MeldungsCode[]{MeldungsCode.SONST},"BAL",0},
                {new MeldungsCode[]{MeldungsCode.ADFORT}, null, 0}  ,
                {new MeldungsCode[]{MeldungsCode.ADFORT}, "", 0}  ,
                {new MeldungsCode[]{MeldungsCode.ADFORT},"BAL",0},
        };
        // @formatter:on
    }

    @Test(dataProvider = "abbmBegruendung")
    public void testCheckAbbmBegruendung(MeldungsCode[] codes, String begruendung, int constraints) throws Exception {

        Abbruchmeldung meldung = new AbbruchmeldungTestBuilder()
                .buildValid(WbciCdmVersion.V1, getGeschaeftsfallTyp());
        meldung.setBegruendung(begruendung);

        Set<MeldungPositionAbbruchmeldung> pos = new HashSet<>();
        for (MeldungsCode code : codes) {
            MeldungPositionAbbruchmeldungBuilder builder = new MeldungPositionAbbruchmeldungTestBuilder();
            pos.add(builder.withMeldungsCode(code)
                    .withMeldungsText(code.getStandardText()).build());
        }
        meldung.setMeldungsPositionen(pos);

        Set<ConstraintViolation<Abbruchmeldung>> constraintViolationSet = checkMessageForErrors(
                WbciCdmVersion.V1,
                meldung,
                getErrorGroup());
        assertConstraintViolationSet(constraintViolationSet, constraints);
    }
}
