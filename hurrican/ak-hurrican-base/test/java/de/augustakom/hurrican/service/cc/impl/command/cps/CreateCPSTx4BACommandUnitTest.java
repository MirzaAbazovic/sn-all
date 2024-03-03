/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.10.2012 09:01:25
 */
package de.augustakom.hurrican.service.cc.impl.command.cps;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlass;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.VerlaufBuilder;
import de.augustakom.hurrican.model.cc.VerlaufStatus;
import de.augustakom.hurrican.service.base.exceptions.FindException;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class CreateCPSTx4BACommandUnitTest extends BaseTest {

    private CreateCPSTx4BACommand cut;

    @BeforeMethod
    public void setUp() throws FindException {
        cut = new CreateCPSTx4BACommand();
    }

    @Test
    public void testSortProvisioningOrders() {
        Verlauf verlaeufe[] = new Verlauf[32];
        // Test prueft, ob Komparator korrekt implementiert ist. Wenn der
        // Komparator transitive Beziehungen (A > B, B > C, somit muss A > C
        // sein) nicht korrekt behandelt, dann fliegt eine
        // IllegalArgumentException("comparison method violates its general contract")
        // ab Java 7. Damit diese Exception fliegt muessen mindestens 32
        // Elemente sortiert werden, da sonst kein Merge im Sortieralgorithmus
        // und somit keine Prüfung erfolgt.
        for (int i = 0; i < 4; i++) {
            verlaeufe[(i * 8) + 0] = new VerlaufBuilder().withRandomId().withAnlass(BAVerlaufAnlass.NEUSCHALTUNG)
                    .withVerlaufStatusId(VerlaufStatus.BEI_TECHNIK).setPersist(false).build();
            verlaeufe[(i * 8) + 1] = new VerlaufBuilder().withRandomId().withAnlass(BAVerlaufAnlass.KUENDIGUNG)
                    .withVerlaufStatusId(VerlaufStatus.KUENDIGUNG_BEI_TECHNIK).setPersist(false).build();
            verlaeufe[(i * 8) + 2] = new VerlaufBuilder().withRandomId().withAnlass(BAVerlaufAnlass.DSL_KREUZUNG)
                    .withVerlaufStatusId(VerlaufStatus.BEI_DISPO).setPersist(false).build();
            verlaeufe[(i * 8) + 3] = new VerlaufBuilder().withRandomId().withAnlass(BAVerlaufAnlass.KUENDIGUNG)
                    .withVerlaufStatusId(VerlaufStatus.KUENDIGUNG_BEI_DISPO).setPersist(false).build();
            verlaeufe[(i * 8) + 4] = new VerlaufBuilder().withRandomId().withAnlass(BAVerlaufAnlass.NEUSCHALTUNG)
                    .withVerlaufStatusId(VerlaufStatus.BEI_ZENTRALER_DISPO).setPersist(false).build();
            verlaeufe[(i * 8) + 5] = new VerlaufBuilder().withRandomId().withAnlass(BAVerlaufAnlass.KUENDIGUNG)
                    .withVerlaufStatusId(VerlaufStatus.KUENDIGUNG_BEI_TECHNIK).setPersist(false).build();
            verlaeufe[(i * 8) + 6] = new VerlaufBuilder().withRandomId().withAnlass(BAVerlaufAnlass.KUENDIGUNG)
                    .withVerlaufStatusId(VerlaufStatus.KUENDIGUNG_BEI_TECHNIK).setPersist(false).build();
            verlaeufe[(i * 8) + 7] = new VerlaufBuilder().withRandomId().withAnlass(BAVerlaufAnlass.KUENDIGUNG)
                    .withVerlaufStatusId(VerlaufStatus.KUENDIGUNG_BEI_TECHNIK).setPersist(false).build();
        }

        List<Verlauf> provisioningOrders = Arrays.asList(verlaeufe);

        cut.sortProvisioningOrders(provisioningOrders);
        for (int i = 0; i < 20; i++) {
            assertThat(provisioningOrders.get(i).getAnlass(), equalTo(BAVerlaufAnlass.KUENDIGUNG));
        }
        for (int i = 20; i < 32; i++) {
            assertThat(provisioningOrders.get(i).getAnlass(), not(equalTo(BAVerlaufAnlass.KUENDIGUNG)));
        }
    }

}


