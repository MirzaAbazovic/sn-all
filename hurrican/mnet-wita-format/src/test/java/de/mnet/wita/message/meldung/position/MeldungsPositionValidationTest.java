package de.mnet.wita.message.meldung.position;

import static de.mnet.wita.TestGroups.*;
import static org.testng.Assert.*;

import java.lang.reflect.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.message.builder.meldung.position.MeldungsPositionBuilder;
import de.mnet.wita.tools.TestHelper;
import de.mnet.wita.validators.AbstractValidationTest;
import de.mnet.wita.validators.groups.V1;

/**
 *
 */
@Test(groups = UNIT)
public class MeldungsPositionValidationTest extends AbstractValidationTest<MeldungsPosition> {

    @DataProvider
    public Object[][] dataProvider(Method method) {
        WitaCdmVersion witaCdmVersion = TestHelper.extractWitaVersion(method);
        return new Object[][] {
                { new MeldungsPositionBuilder(witaCdmVersion).buildValid(), true },
                { new MeldungsPositionBuilder(witaCdmVersion).build(), false },
                { new MeldungsPositionBuilder(witaCdmVersion).withMeldungsCode("").buildValid(), false },
                { new MeldungsPositionBuilder(witaCdmVersion).withMeldungsCode("1").buildValid(), true },
                { new MeldungsPositionBuilder(witaCdmVersion).withMeldungsCode("123").buildValid(), true },
                { new MeldungsPositionBuilder(witaCdmVersion).withMeldungsText("").buildValid(), false },
                { new MeldungsPositionBuilder(witaCdmVersion).withMeldungsText("1").buildValid(), true },
        };
    }

    @Test(dataProvider = "dataProvider")
    public void testValidV1(MeldungsPosition meldungsPosition, boolean valid) {
        violations = validator.validate(meldungsPosition, V1.class);
        assertEquals(violations.isEmpty(), valid, "Found validations " + violations);
    }

}
