/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.06.2011 15:50:59
 */
package de.mnet.wita.message;

import static de.mnet.wita.TestGroups.*;
import static de.mnet.wita.message.GeschaeftsfallTyp.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import javax.validation.*;
import org.apache.log4j.Logger;
import org.hamcrest.Matchers;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.message.builder.AuftragBuilder;
import de.mnet.wita.message.builder.MnetWitaRequestBuilder;
import de.mnet.wita.message.builder.StornoBuilder;
import de.mnet.wita.message.builder.TerminVerschiebungBuilder;
import de.mnet.wita.validators.groups.V1;

@Test(groups = UNIT)
public class MnetWitaRequestValidationTest {

    private static final Logger LOGGER = Logger.getLogger(MnetWitaRequestValidationTest.class);
    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private static final GeschaeftsfallTyp[] GESCHAEFTSFAELLE = { BEREITSTELLUNG,
            KUENDIGUNG_KUNDE,
            LEISTUNGS_AENDERUNG,
            LEISTUNGSMERKMAL_AENDERUNG,
            PORTWECHSEL,
            PROVIDERWECHSEL,
            RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG,
            VERBUNDLEISTUNG };

    @DataProvider
    public Object[][] dataProvider() {
        List<Object[]> data = new ArrayList<>();
        for (GeschaeftsfallTyp typ : GESCHAEFTSFAELLE) {
            data.add(new Object[] { new AuftragBuilder(typ) });
            data.add(new Object[] { new StornoBuilder(typ) });
            data.add(new Object[] { new TerminVerschiebungBuilder(typ) });
        }
        return data.toArray(new Object[data.size()][]);
    }

    @Test(dataProvider = "dataProvider")
    public void testValid(MnetWitaRequestBuilder<?> builder) {
        assertNoViolations(builder.buildValid());
    }

    @DataProvider
    public Object[][] dataProviderTerminVerschiebungV7() {
        List<Object[]> data = new ArrayList<>();
        for (GeschaeftsfallTyp typ : GESCHAEFTSFAELLE) {
            data.add(new Object[] { new TerminVerschiebungBuilder(typ, WitaCdmVersion.V1).withTermin(LocalDate.now()).build(), true });
            data.add(new Object[] { new TerminVerschiebungBuilder(typ, WitaCdmVersion.V1).build(), false });
        }
        return data.toArray(new Object[data.size()][]);
    }

    @Test(dataProvider = "dataProviderTerminVerschiebungV7")
    public void testValidTerminVerschiebungV7(MnetWitaRequest request, boolean valid) {
        Set<ConstraintViolation<MnetWitaRequest>> violations = validator.validate(request, V1.class);
        assertEquals(violations.isEmpty(), valid, "Found validations " + violations);
    }

    @Test(dataProvider = "dataProvider")
    public void testWithoutKunde(MnetWitaRequestBuilder<?> builder) {
        assertHasViolations(builder.buildWithoutKunde());
    }

    @Test(dataProvider = "dataProvider")
    public void testWithoutGeschaeftsfall(MnetWitaRequestBuilder<?> builder) {
        assertHasViolations(builder.buildWithoutGeschaeftsfall());
    }

    @Test(dataProvider = "dataProvider")
    public void testMissingDate(MnetWitaRequestBuilder<?> builder) {
        assertHasViolations(builder.buildWithoutDate());
    }

    @Test(dataProvider = "dataProvider")
    public void testWithoutVertragsnummer(MnetWitaRequestBuilder<?> builder) throws Exception {
        MnetWitaRequest request = builder.buildValid();
        request.getGeschaeftsfall().setVertragsNummer(null);

        switch (request.getGeschaeftsfall().getGeschaeftsfallTyp()) {
            case BEREITSTELLUNG:
            case VERBUNDLEISTUNG:
            case RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG:
                assertNoViolations(request);
                break;
            default:
                assertHasViolations(request);
        }
    }

    @Test(dataProvider = "dataProvider")
    public void testWithToLongVertragsnummer(MnetWitaRequestBuilder<?> builder) throws Exception {
        MnetWitaRequest request = builder.buildValid();
        request.getGeschaeftsfall().setVertragsNummer("12345678901");
        assertHasViolations(request);
    }

    @Test(dataProvider = "dataProvider")
    public void testWithToShortVertragsnummer(MnetWitaRequestBuilder<?> builder) throws Exception {
        MnetWitaRequest request = builder.buildValid();
        request.getGeschaeftsfall().setVertragsNummer("");
        assertHasViolations(request);
    }

    @Test(dataProvider = "dataProvider")
    public void testInvalidWitaVersion(MnetWitaRequestBuilder<?> builder) throws Exception {
        MnetWitaRequest request = builder.buildValid();
        // default wita version is set to 4 (see MnetWitaRequestBuilder)
        assertNoViolations(request);
        request.setCdmVersion(WitaCdmVersion.V1);
        assertNoViolations(request);
        request.setCdmVersion(WitaCdmVersion.UNKNOWN);
        assertHasViolations(request);
    }

    public void testWithoutTermin() {
        TerminVerschiebung terminVerschiebung = new TerminVerschiebungBuilder(BEREITSTELLUNG).build();
        assertHasViolations(terminVerschiebung);
    }

    private void assertNoViolations(MnetWitaRequest request) {
        Set<ConstraintViolation<MnetWitaRequest>> validations = validator.validate(request);
        assertThat(validations, Matchers.<ConstraintViolation<MnetWitaRequest>>empty());
        LOGGER.info(validations);
    }

    private void assertHasViolations(MnetWitaRequest request) {
        Set<ConstraintViolation<MnetWitaRequest>> validations = validator.validate(request);
        assertThat(validations, not(Matchers.<ConstraintViolation<MnetWitaRequest>>empty()));
        LOGGER.info(validations);
    }
}
