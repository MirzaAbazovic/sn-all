package de.mnet.wita.validators;

import static de.mnet.wita.TestGroups.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.testng.annotations.Test;

import de.mnet.wita.message.auftrag.GeschaeftsfallProdukt;
import de.mnet.wita.message.builder.auftrag.GeschaeftsfallProduktBuilder;
import de.mnet.wita.validators.groups.V1;

/**
 *
 */
@Test(groups = UNIT)
public class GeschaeftsfallProduktValidationTest extends AbstractValidationTest<GeschaeftsfallProdukt> {

    public void validateVorabstimmungsId() {
        GeschaeftsfallProdukt geschaeftsfallProdukt = new GeschaeftsfallProduktBuilder().build();
        violations = validator.validate(geschaeftsfallProdukt, V1.class);
        assertThat(violations, hasSize(0));
        geschaeftsfallProdukt = new GeschaeftsfallProduktBuilder().withVorabstimmungsId("12345").build();
        violations = validator.validate(geschaeftsfallProdukt, V1.class);
        assertThat(violations, hasSize(1));
        geschaeftsfallProdukt = new GeschaeftsfallProduktBuilder().withVorabstimmungsId("DEU.MNET.V123456789").build();
        violations = validator.validate(geschaeftsfallProdukt, V1.class);
        assertThat(violations, hasSize(0));
        geschaeftsfallProdukt = new GeschaeftsfallProduktBuilder().withVorabstimmungsId("DEU.MNET.V123456789").build();
        violations = validator.validate(geschaeftsfallProdukt, V1.class);
        assertThat(violations, hasSize(0));
        geschaeftsfallProdukt = new GeschaeftsfallProduktBuilder().withVorabstimmungsId("DEU.MNET.V1234567890123123").build();
        violations = validator.validate(geschaeftsfallProdukt, V1.class);
        assertThat(violations, hasSize(1));
    }

}
