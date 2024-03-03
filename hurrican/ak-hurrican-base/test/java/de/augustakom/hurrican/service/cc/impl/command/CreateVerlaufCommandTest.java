package de.augustakom.hurrican.service.cc.impl.command;

import static org.mockito.MockitoAnnotations.*;
import static org.testng.Assert.*;

import java.util.*;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlass;
import de.augustakom.hurrican.service.base.exceptions.FindException;

/**
 * Testklasse fuer {@link CreateVerlaufCommand}
 */
@Test(groups = { BaseTest.UNIT })
public class CreateVerlaufCommandTest extends BaseTest {

    @Spy
    private CreateVerlaufCommand cut;

    @BeforeMethod
    void setup() {
        cut = new CreateVerlaufCommand();
        initMocks(this);
    }

    @DataProvider
    private Object[][] checkValuesDataProvider() {
        return new Object[][] {
                { BAVerlaufAnlass.NEUSCHALTUNG, false },
                { BAVerlaufAnlass.KUENDIGUNG, true },
        };
    }

    @Test(dataProvider = "checkValuesDataProvider")
    public void testIsKuendigungInCheckValuesSet(Long anlassId, boolean expectedIsKuendigung) throws FindException {
        cut.prepare(CreateVerlaufCommand.AUFTRAG_ID, 1L);
        cut.prepare(CreateVerlaufCommand.REALISIERUNGSTERMIN, new Date());
        cut.prepare(CreateVerlaufCommand.ANLASS_ID, anlassId);
        cut.prepare(CreateVerlaufCommand.INSTALL_TYPE_ID, null);
        cut.prepare(CreateVerlaufCommand.SESSION_ID, -1L);
        cut.prepare(CreateVerlaufCommand.ZENTRALE_DISPO, false);
        cut.prepare(CreateVerlaufCommand.AUFTRAG_SUBORDERS, null);
        cut.checkValues();
        assertEquals(expectedIsKuendigung, cut.getIsKuendigung());
    }
}
