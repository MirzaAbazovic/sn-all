/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.10.2016
 */
package de.augustakom.hurrican.gui.hvt.switchmigration.util;

import static org.testng.Assert.*;

import java.util.*;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.gui.hvt.switchmigration.SwitchMigrationPanel;
import de.augustakom.hurrican.model.shared.view.SwitchMigrationView;

@Test(groups = { BaseTest.UNIT })
public class DialNumberUtilTest extends BaseTest {

    @Test
    public void calculateDialNumberSum_DuplicateElements_ReturnNumbersOnlyOnce() {
        SwitchMigrationView view = new SwitchMigrationView();
        view.setBillingAuftragId(1L);
        SwitchMigrationPanel.SwitchMigrationViewObject o1 = new SwitchMigrationPanel.SwitchMigrationViewObject(view);
        o1.setDialNumberCount(10);
        SwitchMigrationPanel.SwitchMigrationViewObject o2 = new SwitchMigrationPanel.SwitchMigrationViewObject(view);
        o2.setDialNumberCount(10);
        Collection<SwitchMigrationPanel.SwitchMigrationViewObject> data =
                Arrays.asList(o1, o2);

        int result = DialNumberUtil.calculateDialNumberSum(data);

        assertEquals(result, 10);
    }

    @Test
    public void calculateDialNumberSum_NoDuplicateElements_ReturnSum() {
        SwitchMigrationView view1 = new SwitchMigrationView();
        view1.setBillingAuftragId(1L);
        SwitchMigrationPanel.SwitchMigrationViewObject o1 = new SwitchMigrationPanel.SwitchMigrationViewObject(view1);
        o1.setDialNumberCount(10);

        SwitchMigrationView view2 = new SwitchMigrationView();
        view2.setBillingAuftragId(2L);
        SwitchMigrationPanel.SwitchMigrationViewObject o2 = new SwitchMigrationPanel.SwitchMigrationViewObject(view2);
        o2.setDialNumberCount(10);
        Collection<SwitchMigrationPanel.SwitchMigrationViewObject> data =
                Arrays.asList(o1, o2);

        int result = DialNumberUtil.calculateDialNumberSum(data);

        assertEquals(result, 20);
    }

}