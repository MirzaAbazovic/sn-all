/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.06.2011 09:28:18
 */
package de.mnet.wita.bpm.impl;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

import java.util.regex.*;
import javax.validation.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wita.message.Auftrag;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.builder.AuftragBuilder;
import de.mnet.wita.message.builder.meldung.AuftragsBestaetigungsMeldungBuilder;
import de.mnet.wita.message.meldung.AuftragsBestaetigungsMeldung;

@Test(groups = BaseTest.UNIT)
public class WorkflowTaskValidationServiceImplTest extends BaseTest {

    private WorkflowTaskValidationServiceImpl cut;
    private AuftragBuilder auftragBuilder;

    @BeforeMethod
    public void setUp() {
        cut = new WorkflowTaskValidationServiceImpl();
        auftragBuilder = new AuftragBuilder(GeschaeftsfallTyp.BEREITSTELLUNG);
    }

    public void validateMwfObjectsExpectSuccess() {
        cut.validateMwfOutput(auftragBuilder.buildValid());
    }

    @Test(expectedExceptions = ValidationException.class, expectedExceptionsMessageRegExp = "^cbVorgangId.*")
    public void validateMwfObjectsExpectErrorBecauseOfMissingCbvId() {
        Auftrag auftrag = auftragBuilder.buildValid();
        auftrag.setCbVorgangId(null);
        cut.validateMwfOutput(auftrag);
    }

    @Test(expectedExceptions = ValidationException.class)
    public void validateMwfObjectsExpectErrorBecauseOfMultipleMissingValues() {
        Auftrag auftrag = auftragBuilder.buildValid();
        auftrag.setCbVorgangId(null);
        auftrag.setKunde(null);

        try {
            cut.validateMwfOutput(auftrag);
        }
        catch (ValidationException e) {
            // multi line error text validation not possible with expectedExceptionsMessageRegExp!
            Pattern pattern = Pattern.compile(".*cbVorgangId.*", Pattern.DOTALL);
            Matcher m = pattern.matcher(e.getMessage());
            assertTrue(m.matches());

            pattern = Pattern.compile(".*kunde.*", Pattern.DOTALL);
            m = pattern.matcher(e.getMessage());
            assertTrue(m.matches());

            throw e;
        }
    }

    public void validateMwfInputWithDefaultGroup() {
        AuftragsBestaetigungsMeldung abm = new AuftragsBestaetigungsMeldungBuilder().build();

        // externeAuftragsnummer is validated in default group
        abm.setExterneAuftragsnummer(null);
        String errorMsg = cut.validateMwfInput(abm);
        assertThat(errorMsg, notNullValue());
    }

    public void validateMwfInputWithWorkflowGroup() {
        AuftragsBestaetigungsMeldung abm = new AuftragsBestaetigungsMeldungBuilder().build();

        // kundennummer is validated in workflow group
        abm.setKundenNummer(null);
        String errorMsg = cut.validateMwfInput(abm);
        assertThat(errorMsg, notNullValue());
    }

}
