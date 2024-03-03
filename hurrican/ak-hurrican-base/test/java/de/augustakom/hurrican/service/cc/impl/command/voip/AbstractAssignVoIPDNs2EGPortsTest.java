/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.11.2011 11:45:36
 */
package de.augustakom.hurrican.service.cc.impl.command.voip;

import static de.augustakom.hurrican.service.cc.impl.command.voip.AbstractAssignVoIPDNs2EGPorts.*;

import java.util.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.cc.AuftragVoIPDN;
import de.augustakom.hurrican.model.shared.view.voip.AuftragVoipDNView;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;

/**
 * {@link AbstractAssignVoIPDNs2EGPorts} Unit Test
 */
@Test(groups = BaseTest.UNIT)
public abstract class AbstractAssignVoIPDNs2EGPortsTest extends BaseTest {

    private AbstractAssignVoIPDNs2EGPorts cmd;

    protected int portcount;
    protected Rufnummer hauptRufNr;
    protected AuftragVoIPDN auftragVoIPDN;
    protected Collection<AuftragVoipDNView> auftragVoipDNViews;

    protected abstract AbstractAssignVoIPDNs2EGPorts getInstanceOfImplToTest();

    @BeforeMethod
    public void setUpCmd() {
        cmd = getInstanceOfImplToTest();

        auftragVoipDNViews = new ArrayList<AuftragVoipDNView>();
        auftragVoipDNViews.add(new AuftragVoipDNView());
        portcount = (int) (Math.random() * 10);
    }

    @Test(expectedExceptions = { HurricanServiceCommandException.class })
    public void checkValues_noAuftragVoIPDNViews() throws Exception {
        cmd.prepare(PORT_COUNT, portcount);
        cmd.execute();
    }

    @Test(expectedExceptions = { HurricanServiceCommandException.class })
    public void checkValues_noPortCount() throws Exception {
        cmd.prepare(AUFTRAG_VOIP_DN_VIEWS, auftragVoipDNViews);
        cmd.execute();
    }
}
