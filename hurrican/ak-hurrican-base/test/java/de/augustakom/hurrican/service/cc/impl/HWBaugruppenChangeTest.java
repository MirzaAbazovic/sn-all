/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.04.2010 12:19:15
 */
package de.augustakom.hurrican.service.cc.impl;

import static org.testng.Assert.*;

import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChange;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;


/**
 * TestNG Klasse, um Funktionen von {@link HWBaugruppenChange} zu testen.
 */
@Test(groups = BaseTest.UNIT)
public class HWBaugruppenChangeTest extends AbstractHurricanBaseServiceTest {

    public void testHWBaugruppenChangeModel() {
        Reference stateReference = new Reference();
        stateReference.setId(HWBaugruppenChange.ChangeState.CHANGE_STATE_PLANNING.refId());

        HWBaugruppenChange hwBgChange = new HWBaugruppenChange();
        hwBgChange.setChangeState(stateReference);
        assertTrue(hwBgChange.isPreparingAllowed());
        assertTrue(hwBgChange.isCancelAllowed());
        assertFalse(hwBgChange.isExecuteAllowed());
        assertFalse(hwBgChange.isCloseAllowed());

        stateReference.setId(HWBaugruppenChange.ChangeState.CHANGE_STATE_PREPARED.refId());
        assertFalse(hwBgChange.isPreparingAllowed());
        assertTrue(hwBgChange.isCancelAllowed());
        assertTrue(hwBgChange.isExecuteAllowed());
        assertFalse(hwBgChange.isCloseAllowed());

        stateReference.setId(HWBaugruppenChange.ChangeState.CHANGE_STATE_EXECUTED.refId());
        assertFalse(hwBgChange.isPreparingAllowed());
        assertFalse(hwBgChange.isCancelAllowed());
        assertFalse(hwBgChange.isExecuteAllowed());
        assertTrue(hwBgChange.isCloseAllowed());
    }
}


