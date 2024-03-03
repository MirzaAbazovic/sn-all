/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.06.2011 10:52:24
 */
package de.mnet.wita.model;

import static de.augustakom.common.BaseTest.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.*;
import javax.validation.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wita.IOArchiveProperties.IOType;
import de.mnet.wita.message.MeldungsType;

@Test(groups = UNIT)
public class IoArchiveValidatorTest extends BaseTest {

    private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @DataProvider
    public Object[][] meldungProvider() {
        return new Object[][] {
                { MeldungsType.ABBM, false },
                { MeldungsType.ABM, false },
                { MeldungsType.ABBM_PV, true },
                { MeldungsType.ABM_PV, true },
                { MeldungsType.AKM_PV, true },
                { MeldungsType.ENTM, false },
                { MeldungsType.ENTM_PV, true },
                { MeldungsType.ERLM, false },
                { MeldungsType.ERLM_PV, true },
                { MeldungsType.ERLM_K, false },
                { MeldungsType.QEB, false },
                { MeldungsType.RUEM_PV, true },
                { MeldungsType.TAM, false },
                { MeldungsType.VZM, false },
        };
    }

    @Test(dataProvider = "meldungProvider")
    public void testWithoutExtAuftragsnummer(MeldungsType meldungstyp, boolean isVertragsnummerExpected) {
        IoArchive ioArchiveEntry = getBaseBuilder(meldungstyp)
                .withWitaExtOrderNo(null)
                .withWitaVertragsnummer("12345")
                .build();
        Set<ConstraintViolation<IoArchive>> violations = validator.validate(ioArchiveEntry);
        assertThat("Unexpected violations: " + violations, violations.isEmpty(), equalTo(isVertragsnummerExpected));
    }


    @Test(dataProvider = "meldungProvider")
    public void testWithoutVertragsnummer(MeldungsType meldungstyp, boolean isVertragsnummerExpected) {
        IoArchive ioArchiveEntry = getBaseBuilder(meldungstyp)
                .withWitaExtOrderNo("12345")
                .withWitaVertragsnummer(null)
                .build();
        Set<ConstraintViolation<IoArchive>> violations = validator.validate(ioArchiveEntry);
        assertThat("Unexpected violations: " + violations, violations.isEmpty(), equalTo(!isVertragsnummerExpected));
    }

    private IoArchiveBuilder getBaseBuilder(MeldungsType meldungstyp) {
        return new IoArchiveBuilder()
                .withIoType(IOType.IN)
                .withRequestMeldungstyp(meldungstyp.toString());
    }

}
