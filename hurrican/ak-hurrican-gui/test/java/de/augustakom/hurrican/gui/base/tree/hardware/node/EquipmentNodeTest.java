/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.12.2009 15:25:52
 */
package de.augustakom.hurrican.gui.base.tree.hardware.node;

import static org.testng.Assert.*;

import javax.swing.*;
import org.fest.swing.edt.FailOnThreadViolationRepaintManager;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.locator.ServiceLocatorRegistry;
import de.augustakom.hurrican.gui.base.tree.DynamicTree;
import de.augustakom.hurrican.gui.base.tree.DynamicTreeNode;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;


/**
 *
 */
@Test(groups = { BaseTest.SERVICE })
public class EquipmentNodeTest extends AbstractHurricanBaseServiceTest {

    @SuppressWarnings("deprecation")
    @Override
    protected void afterSetup() {
        ServiceLocatorRegistry.instance().setApplicationContext(getApplicationContext());
    }

    /**
     * Test method for {@link de.augustakom.hurrican.gui.base.tree.hardware.node.EquipmentNode#doLoadChildren()}.
     */
    public void testDoLoadChildren() {
        // Here we need to do GUI-Updates from a non-EDThread. This doesn't matter since we do
        // not show the GUI. Therefore we install a FailOnThreadViolationRepaintManager only
        // firing for Components which are shown.
        RepaintManager.setCurrentManager(new FailOnThreadViolationRepaintManager(false));
        try {
            EndstelleBuilder endstelleBuilder = getBuilder(EndstelleBuilder.class);
            EndstelleBuilder endstelleAltBuilder = getBuilder(EndstelleBuilder.class)
                    .withRangierungBuilder(endstelleBuilder.getRangierungBuilder());

            AuftragBuilder auftragBuilder = createAuftrag(endstelleBuilder);
            auftragBuilder.build();

            AuftragBuilder auftragAltBuilder = createAuftrag(endstelleAltBuilder);
            auftragAltBuilder.build();

            Equipment equipment = endstelleBuilder.getRangierungBuilder().getEqInBuilder().get();
            flushAndClear();

            DynamicTree dynamicTree = new DynamicTree();
            final DynamicTreeNode node = new EquipmentNode(dynamicTree, new EquipmentNodeInfo(equipment, null, false));

            node.loadChildren();
            assertEquals(node.getChildCount(), 2, "Node should have two children");
        }
        finally {
            RepaintManager.setCurrentManager(new FailOnThreadViolationRepaintManager(true));
        }
    }

    private AuftragBuilder createAuftrag(EndstelleBuilder endstelleBuilder) {
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class)
                .withAuftragDatenBuilder(getBuilder(AuftragDatenBuilder.class))
                .withAuftragTechnikBuilder(getBuilder(AuftragTechnikBuilder.class));
        auftragBuilder.getAuftragTechnikBuilder().withEndstelleBuilder(endstelleBuilder);
        return auftragBuilder;
    }

}
