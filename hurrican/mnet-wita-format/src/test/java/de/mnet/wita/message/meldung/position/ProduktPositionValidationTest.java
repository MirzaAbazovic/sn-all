package de.mnet.wita.message.meldung.position;

import static de.mnet.wita.TestGroups.*;
import static org.testng.Assert.*;

import java.lang.reflect.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.message.auftrag.AktionsCode;
import de.mnet.wita.message.auftrag.Auftragsposition;
import de.mnet.wita.message.builder.meldung.position.ProduktPositionBuilder;
import de.mnet.wita.message.common.Uebertragungsverfahren;
import de.mnet.wita.tools.TestHelper;
import de.mnet.wita.validators.AbstractValidationTest;
import de.mnet.wita.validators.groups.V1;
import de.mnet.wita.validators.groups.WorkflowV1;

/**
 *
 */
@Test(groups = UNIT)
public class ProduktPositionValidationTest extends AbstractValidationTest<ProduktPosition> {

    @DataProvider
    public Object[][] dataProvider(Method method) {
        WitaCdmVersion witaCdmVersion = TestHelper.extractWitaVersion(method);
        return new Object[][] {
                { new ProduktPositionBuilder(witaCdmVersion).buildValid(), true },
                { new ProduktPositionBuilder(witaCdmVersion).build(), false },
                { new ProduktPositionBuilder(witaCdmVersion).withAktionsCode(AktionsCode.AENDERUNG).build(), false },
                { new ProduktPositionBuilder(witaCdmVersion).withProduktBezeichner(Auftragsposition.ProduktBezeichner.HVT_2H).build(), false },
                { new ProduktPositionBuilder(witaCdmVersion).withAktionsCode(AktionsCode.AENDERUNG)
                        .withProduktBezeichner(Auftragsposition.ProduktBezeichner.HVT_2H).build(), true },
                { new ProduktPositionBuilder(witaCdmVersion).withUebertragungsVerfahren(Uebertragungsverfahren.H01).buildValid(), true },
        };
    }

    @Test(dataProvider = "dataProvider")
    public void testValidV1(ProduktPosition produktPosition, boolean valid) {
        violations = validator.validate(produktPosition, V1.class, WorkflowV1.class);
        assertEquals(violations.isEmpty(), valid, "Found validations " + violations);
    }

}
