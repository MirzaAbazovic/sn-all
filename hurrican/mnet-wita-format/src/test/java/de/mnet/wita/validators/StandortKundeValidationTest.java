package de.mnet.wita.validators;

import static de.mnet.wita.TestGroups.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.testng.annotations.Test;

import de.mnet.wita.message.auftrag.StandortKunde;
import de.mnet.wita.message.builder.auftrag.StandortKundeBuilder;
import de.mnet.wita.validators.groups.V1;

@Test(groups = UNIT)
public class StandortKundeValidationTest extends AbstractValidationTest<StandortKunde> {

    public void validateLageTAEDose() {
        StandortKunde standortKunde = new StandortKundeBuilder().build();
        violations = validator.validate(standortKunde, V1.class);
        assertThat(violations, hasSize(0));
        standortKunde.setLageTAEDose("");
        violations = validator.validate(standortKunde, V1.class);
        assertThat(violations, hasSize(1));
        standortKunde.setLageTAEDose("12345");
        violations = validator.validate(standortKunde, V1.class);
        assertThat(violations, hasSize(0));

        standortKunde.setGebaeudeteilName("");
        violations = validator.validate(standortKunde, V1.class);
        assertThat(violations, hasSize(1));
    }

}
