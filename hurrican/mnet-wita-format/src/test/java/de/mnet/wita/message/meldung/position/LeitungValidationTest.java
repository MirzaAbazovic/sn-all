package de.mnet.wita.message.meldung.position;

import static de.mnet.wita.TestGroups.*;
import static org.testng.Assert.*;

import java.lang.reflect.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.message.builder.common.LeitungsBezeichnungBuilder;
import de.mnet.wita.message.builder.meldung.position.LeitungBuilder;
import de.mnet.wita.tools.TestHelper;
import de.mnet.wita.validators.AbstractValidationTest;
import de.mnet.wita.validators.groups.V1;
import de.mnet.wita.validators.groups.WorkflowV1;

/**
 *
 */
@Test(groups = UNIT)
public class LeitungValidationTest extends AbstractValidationTest<Leitung> {

    @DataProvider
    public Object[][] dataProvider(Method method) {
        WitaCdmVersion witaCdmVersion = TestHelper.extractWitaVersion(method);
        return new Object[][] {
                { new LeitungBuilder(witaCdmVersion).buildValid(), true },
                { new LeitungBuilder(witaCdmVersion).build(), false },
                { new LeitungBuilder(witaCdmVersion).withLeitungsbezeichnung(new LeitungsBezeichnungBuilder(witaCdmVersion).buildValid()).build(), true },
        };
    }

    @Test(dataProvider = "dataProvider")
    public void testValidV1(Leitung leitung, boolean valid) {
        violations = validator.validate(leitung, V1.class, WorkflowV1.class);
        assertEquals(violations.isEmpty(), valid, "Found validations " + violations);
    }

}
