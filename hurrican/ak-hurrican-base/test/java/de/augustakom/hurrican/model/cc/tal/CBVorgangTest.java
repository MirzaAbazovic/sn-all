/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.11.2011 13:27:42
 */
package de.augustakom.hurrican.model.cc.tal;

import static de.augustakom.common.BaseTest.*;
import static org.testng.Assert.*;

import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

@Test(groups = UNIT)
public class CBVorgangTest extends BaseTest {

    private static final String RETURN_BEMERKUNG = "vodoo 123456789";

    public void setLongReturnBemerkung() {
        CBVorgang cbVorgang = new CBVorgang();
        String retBemerkungen = "";
        while (retBemerkungen.length() < CBVorgang.RETURN_BEMERKUNGEN_SIZE) {
            retBemerkungen += RETURN_BEMERKUNG;
        }

        cbVorgang.setReturnBemerkung(retBemerkungen);

        assertTrue(cbVorgang.getReturnBemerkung().length() <= CBVorgang.RETURN_BEMERKUNGEN_SIZE);
        assertTrue(cbVorgang.getReturnBemerkung().contains(RETURN_BEMERKUNG));
    }

}
