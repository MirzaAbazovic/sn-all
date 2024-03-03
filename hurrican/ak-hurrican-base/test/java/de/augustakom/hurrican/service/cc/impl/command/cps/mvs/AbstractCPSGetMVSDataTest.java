/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.01.2012 16:13:20
 */
package de.augustakom.hurrican.service.cc.impl.command.cps.mvs;

import org.mockito.Mock;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.RandomTools;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.billing.KundeBuilder;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSServiceOrderData;
import de.augustakom.hurrican.service.billing.KundenService;
import de.augustakom.hurrican.service.cc.CCAuftragService;

/**
 * Basis-Testklasse fuer CPSGetMVS*DataTest-Klassen
 */
@Test(groups = { BaseTest.UNIT })
public abstract class AbstractCPSGetMVSDataTest extends BaseTest {

    @Mock
    CCAuftragService asMock;

    @Mock
    KundenService ksMock;

    final CPSServiceOrderData soData = new CPSServiceOrderData();
    final Auftrag auftrag;
    final Kunde kunde;

    public AbstractCPSGetMVSDataTest() {
        //@formatter:off
        auftrag = new AuftragBuilder().setPersist(false)
                    .withRandomId()
                    .withKundeNo(RandomTools.createLong())
                    .build();
        kunde = new KundeBuilder().setPersist(false)
                .withResellerKundeNo(RandomTools.createLong())
                .build();
        //@formatter:on
    }
}
