/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.10.2011 17:51:38
 */
package de.augustakom.hurrican.model.cc.tal;

import static de.augustakom.common.BaseTest.*;
import static org.testng.Assert.*;

import java.util.*;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wita.model.WitaCBVorgang;

@Test(groups = UNIT)
public class CBVorgangNiederlassungTest extends BaseTest {

    public void prioShouldBeSet() {
        WitaCBVorgang cbv = new WitaCBVorgang();
        cbv.setReturnRealDate(new Date());
        cbv.setReturnOk(false);
        CBVorgangNiederlassung cbVorgangNiederlassung = new CBVorgangNiederlassung(cbv, "TestNiederlassung");

        assertTrue(cbVorgangNiederlassung.getPrio());
    }

}


