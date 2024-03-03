/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.09.2011 14:49:40
 */
package de.mnet.wita.model;

import static de.augustakom.common.BaseTest.*;
import static de.mnet.wita.model.TamUserTask.TamBearbeitungsStatus.*;
import static de.mnet.wita.model.TamUserTask.*;
import static org.testng.Assert.*;

import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wita.model.TamUserTask.*;

@Test(groups = UNIT)
public class TamUserTaskTest extends BaseTest {

    public void appendStatusAenderungToEmptyBemerkungen() {
        TamBearbeitungsStatus tamBearbeitungsStatusAlt = IN_BEARBEITUNG;
        TamBearbeitungsStatus tamBearbeitungsStatusNeu = KUNDE_NICHT_ERREICHT;

        String bemerkungenNeu = appendStatusAenderungToBemerkungen(tamBearbeitungsStatusNeu, tamBearbeitungsStatusAlt, null);

        assertTrue(bemerkungenNeu.contains(tamBearbeitungsStatusAlt.getDisplay()));
        assertTrue(bemerkungenNeu.contains(tamBearbeitungsStatusNeu.getDisplay()));
    }

    public void doNotAppendStatusAenderungWhenStatusHasNotChanged() {
        TamBearbeitungsStatus tamBearbeitungsStatusAlt = IN_BEARBEITUNG;
        TamBearbeitungsStatus tamBearbeitungsStatusNeu = IN_BEARBEITUNG;
        String bemerkungenAlt = "foo";

        String bemerkungenNeu = appendStatusAenderungToBemerkungen(tamBearbeitungsStatusNeu, tamBearbeitungsStatusAlt, bemerkungenAlt);

        assertTrue(bemerkungenAlt.equals(bemerkungenNeu));
    }

}
