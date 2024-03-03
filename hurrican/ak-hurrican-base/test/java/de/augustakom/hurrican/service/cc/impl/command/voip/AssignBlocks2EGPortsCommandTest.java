/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.11.2011 14:21:56
 */
package de.augustakom.hurrican.service.cc.impl.command.voip;

import static org.testng.Assert.*;

import java.util.*;
import com.google.common.collect.Iterables;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.shared.view.voip.AuftragVoipDNView;
import de.augustakom.hurrican.model.shared.view.voip.SelectedPortsView;

/**
 * {@link AssignBlocks2EGPortsCommand} Unit Test
 */
@Test(groups = BaseTest.UNIT)
public class AssignBlocks2EGPortsCommandTest extends AbstractAssignVoIPDNs2EGPortsTest {

    AssignBlocks2EGPortsCommand cmd;

    @BeforeMethod
    public void setUp() {
        cmd = new AssignBlocks2EGPortsCommand();
    }

    @Test
    public void execute_success() throws Exception {
        final Integer portcount = Integer.valueOf(3);
        final AuftragVoipDNView voipDNView = new AuftragVoipDNView();
        final Collection<AuftragVoipDNView> auftragVoipDNViews = new ArrayList<>();
        auftragVoipDNViews.add(voipDNView);
        cmd.prepare(AbstractAssignVoIPDNs2EGPorts.PORT_COUNT, portcount);
        cmd.prepare(AbstractAssignVoIPDNs2EGPorts.AUFTRAG_VOIP_DN_VIEWS, auftragVoipDNViews);
        assertNull(cmd.execute());
        SelectedPortsView selectedPortsView = Iterables.getOnlyElement(voipDNView.getSelectedPorts());
        assertTrue(selectedPortsView.isPortSelected(0));
        assertTrue(selectedPortsView.isPortSelected(1));
        assertTrue(selectedPortsView.isPortSelected(2));
    }

    @Override
    protected AbstractAssignVoIPDNs2EGPorts getInstanceOfImplToTest() {
        return new AssignBlocks2EGPortsCommand();
    }

}
