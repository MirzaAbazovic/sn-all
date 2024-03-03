/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.01.2013 09:34:43
 */
package de.augustakom.hurrican.service.cc.impl.command.leistung;

import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.Reference;

@Test(groups = BaseTest.UNIT)
public class IpV4ReservierenCommandTest extends IPAbstractCommandTest {

    @InjectMocks
    @Spy
    IpV4ReservierenCommand cut = new IpV4ReservierenCommand();

    @Override
    protected AbstractIpCommand cut() {
        return cut;
    }

    @Override
    protected String refType() {
        return Reference.REF_TYPE_IP_PURPOSE_TYPE_V4;
    }

}


