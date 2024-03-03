package de.mnet.wita.validators;

import static de.mnet.wita.TestGroups.*;
import static org.testng.Assert.*;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.mnet.wita.message.builder.meldung.AuftragsBestaetigungsMeldungBuilder;
import de.mnet.wita.message.common.LeitungsBezeichnung;
import de.mnet.wita.message.meldung.AuftragsBestaetigungsMeldung;
import de.mnet.wita.message.meldung.position.Leitung;
import de.mnet.wita.validators.groups.Workflow;

@Test(groups = UNIT)
public class AuftragsBestaetigungsMeldungValidationTest extends AbstractValidationTest<AuftragsBestaetigungsMeldung> {

    private AuftragsBestaetigungsMeldung abm;

    @Override
    @BeforeMethod
    public void setupMethod() {
        super.setupMethod();
        abm = new AuftragsBestaetigungsMeldungBuilder().build();
    }

    public void testValidABM() {
        violations = validator.validate(abm, Workflow.class);
        assertTrue(violations.isEmpty(), "Es gab Validierungsfehler: " + violations.toString());
    }

    public void testABMWithoutExtAuftragsnummer() {
        abm.setExterneAuftragsnummer(null);
        violations = validator.validate(abm);
        assertEquals(violations.size(), 1);
    }

    public void testABMWithoutGeschaeftsfallTyp() {
        abm.setGeschaeftsfallTyp(null);
        violations = validator.validate(abm);
        assertEquals(violations.size(), 1);
    }

    public void testABMWithoutAenderungsKennzeichen() {
        abm.setAenderungsKennzeichen(null);
        violations = validator.validate(abm);
        assertEquals(violations.size(), 1);
    }

    public void testABMWithoutLeitung() {
        abm.setLeitung(null);
        violations = validator.validate(abm, Workflow.class);

        // ABM without Leitung is ok
        assertEquals(violations.size(), 0);
    }

    public void testABMWithoutVertragsnummer() {
        abm.setVertragsNummer(null);
        violations = validator.validate(abm, Workflow.class);

        // ABM without vertragsnummer is ok
        assertEquals(violations.size(), 0);
    }

    public void testABMWithInvalidLeitung() {
        Leitung leitung = new Leitung(new LeitungsBezeichnung(null, null, null, null));
        abm.setLeitung(leitung);
        violations = validator.validate(abm, Workflow.class);

        // One violation per leitung parameter
        assertEquals(violations.size(), 4);
    }
}
