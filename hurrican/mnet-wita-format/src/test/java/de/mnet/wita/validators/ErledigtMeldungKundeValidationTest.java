/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.08.2011 14:59:37
 */
package de.mnet.wita.validators;

import static de.mnet.wita.TestGroups.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.testng.annotations.Test;

import de.mnet.wita.message.builder.meldung.ErledigtMeldungKundeBuilder;
import de.mnet.wita.message.meldung.ErledigtMeldungKunde;
import de.mnet.wita.validators.groups.Workflow;

@Test(groups = UNIT)
public class ErledigtMeldungKundeValidationTest extends AbstractValidationTest<ErledigtMeldungKunde> {

    public void vertragsNummerShouldBeValidated() {
        ErledigtMeldungKunde erlmK = new ErledigtMeldungKundeBuilder().build();
        erlmK.setVertragsNummer(null);
        violations = validator.validate(erlmK, Workflow.class);
        assertThat(violations, hasSize(1));
    }

    public void builtErlmKShouldBeValid() {
        ErledigtMeldungKunde erlmK = new ErledigtMeldungKundeBuilder().build();
        violations = validator.validate(erlmK);
        assertThat(violations, hasSize(0));

        violations = validator.validate(erlmK, Workflow.class);
        assertThat(violations, hasSize(0));
    }

}


