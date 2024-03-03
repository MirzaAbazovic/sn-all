/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.04.2012 13:10:20
 */
package de.augustakom.hurrican.service.cc.impl.command.leistung;

import javax.annotation.*;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.Reference;

@Test(groups = BaseTest.UNIT)
public class IpV6PrefixReservierenCommandTest extends IPAbstractCommandTest {

    @InjectMocks
    @Spy
    IpV6PrefixReservierenCommand cut = new IpV6PrefixReservierenCommand();

    @Override
    protected AbstractIpCommand cut() {
        return cut;
    }

    @Override
    @Nonnull
    protected String refType() {
        return Reference.REF_TYPE_IP_PURPOSE_TYPE_V6;
    }
}


