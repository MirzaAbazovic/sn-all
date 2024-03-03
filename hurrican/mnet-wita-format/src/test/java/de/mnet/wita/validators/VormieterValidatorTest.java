/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.10.2011 11:37:39
 */
package de.mnet.wita.validators;

import static de.mnet.wita.TestGroups.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import javax.validation.groups.*;
import com.google.common.collect.Iterables;
import org.testng.annotations.Test;

import de.mnet.wita.message.auftrag.Vormieter;
import de.mnet.wita.validators.groups.V1;

@Test(groups = UNIT)
public class VormieterValidatorTest extends AbstractValidationTest<Vormieter> {

    public void validateEmptyVormieter() {
        Vormieter vormieter = new Vormieter();

        violations = validator.validate(vormieter);
        assertThat(violations, hasSize(1));

        // Check that message has been set
        assertThat(Iterables.getOnlyElement(violations).getMessage(), not(equalTo(VormieterValid.DEFAULT_MESSAGE)));
    }

    public void validateVormieterWithoutVorname() {
        Vormieter vormieter = new Vormieter();
        vormieter.setNachname("Meier");
        violations = validator.validate(vormieter);
        assertThat(violations, hasSize(0));
    }

    public void validateVormieterWithoutNachname() {
        Vormieter vormieter = new Vormieter();
        vormieter.setVorname("Hans");
        violations = validator.validate(vormieter);
        assertThat(violations, hasSize(1));

        // Check that message has been set
        assertThat(Iterables.getOnlyElement(violations).getMessage(), not(equalTo(VormieterValid.DEFAULT_MESSAGE)));

    }

    public void validateVormieterValidUfaNummerV4() {
        Vormieter vormieter = new Vormieter();
        vormieter.setUfaNummer("123456789012345");
        violations = validator.validate(vormieter, V1.class);
        assertThat(violations, hasSize(1));
        vormieter.setUfaNummer("1234567890123456");
        violations = validator.validate(vormieter, Default.class, V1.class);
        assertThat(violations, hasSize(1));
        vormieter.setUfaNummer("12345C1234567");
        violations = validator.validate(vormieter, Default.class, V1.class);
        assertThat(violations, hasSize(0));
    }

}
