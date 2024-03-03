package de.mnet.wita.validators;

import static de.mnet.wita.TestGroups.*;
import static org.testng.Assert.*;

import org.testng.annotations.Test;

import de.mnet.wita.message.common.LeitungsBezeichnung;
import de.mnet.wita.message.meldung.position.Leitung;
import de.mnet.wita.validators.groups.Workflow;

@Test(groups = UNIT)
public class LeitungValidationTest extends AbstractValidationTest<Leitung> {

    public void testEmptyLeitungForDefaultGroup() {
        Leitung leitung = new Leitung(null);
        violations = validator.validate(leitung);
        // default group only checks necessary fields to enter workflow
        assertEquals(violations.size(), 0);
    }

    public void testEmptyLeitung() {
        Leitung leitung = new Leitung(null);
        violations = validator.validate(leitung, Workflow.class);
        // should contain 1 violation due to 1 missing required argument
        assertEquals(violations.size(), 1);
    }

    public void testLeitungWithEmptyLeitungsbezeichnung() {
        Leitung leitung = new Leitung(new LeitungsBezeichnung(null, null, null, null));
        violations = validator.validate(leitung, Workflow.class);
        // should contain 4 violations due to 4 missing required arguments
        assertEquals(violations.size(), 4);
    }

    public void testLeitungWithInvalidOnkzKunde() {
        Leitung leitung = new Leitung(new LeitungsBezeichnung("1234", "123456", "3456", "1234567890"));
        violations = validator.validate(leitung, Workflow.class);
        assertEquals(violations.size(), 1);
    }

    public void testLeitungWithInvalidOnkzKollokation() {
        Leitung leitung = new Leitung(new LeitungsBezeichnung("1234", "3456", "123456", "1234567890"));
        violations = validator.validate(leitung, Workflow.class);
        assertEquals(violations.size(), 1);
    }

    public void testLeitungWithValidLeitungsbezeichnung() {
        Leitung leitung = new Leitung(new LeitungsBezeichnung("1234", "2345", "3456", "1234567890"));
        violations = validator.validate(leitung, Workflow.class);
        assertTrue(violations.isEmpty());
    }

    public void testLeitungWithInvalidOrdnungsnummer() {
        Leitung leitung = new Leitung(new LeitungsBezeichnung("1234", "2345", "3456", "123"));
        violations = validator.validate(leitung, Workflow.class);
        assertEquals(violations.size(), 1);
    }

}
