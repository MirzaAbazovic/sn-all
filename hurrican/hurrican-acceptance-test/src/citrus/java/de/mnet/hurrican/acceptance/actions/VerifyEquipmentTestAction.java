/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.11.2014
 */
package de.mnet.hurrican.acceptance.actions;

import static org.testng.Assert.*;

import com.consol.citrus.actions.AbstractTestAction;
import com.consol.citrus.context.TestContext;
import org.testng.Assert;

import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.mnet.hurrican.ffm.citrus.VariableNames;

/**
 *
 */
public class VerifyEquipmentTestAction extends AbstractTestAction {

    private RangierungsService rangierungsService;
    private final String leiste;
    private final String stift;
    private final String hwEqn;

    /**
     * Constructor setting the action name field.
     */
    public VerifyEquipmentTestAction(RangierungsService rangierungsService, String hwEqn, String leiste, String stift) {
        setName("verifyEquipment");
        this.rangierungsService = rangierungsService;
        this.leiste = leiste;
        this.stift = stift;
        this.hwEqn = hwEqn;
    }

    @Override
    public void doExecute(TestContext context) {
        Long hvtIdStandort = Long.parseLong(context.getVariable(VariableNames.HVT_STANDORT_ID));
        try {
            Equipment equipment = rangierungsService.findEQByLeisteStift(hvtIdStandort, leiste, stift);
            assertEquals(equipment.getHwEQN(), hwEqn);
            Assert.assertNotNull(equipment, String.format("Kein Equipment für HvtStandort '%s', Leiste '%s' " +
                    "und Stift '%s' gefunden", hvtIdStandort, leiste, stift));
        }
        catch (FindException e) {
            e.printStackTrace();
                Assert.fail(String.format("Ein unerwarteter Fehler ist bei der Suche nach einem Equipment " +
                        "aufgetretten. Folgende Suchparameter wurden benutzt hvtIdStabdort '%s', leiste '%s' " +
                        "und stift '%s'.", hvtIdStandort, leiste, stift));
        }
    }

}
