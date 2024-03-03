/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.11.2014
 */
package de.mnet.hurrican.acceptance.actions;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;

import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.mnet.hurrican.acceptance.builder.OltChildTestBuilder;
import de.mnet.hurrican.acceptance.builder.RangierungTestBuilder;
import de.mnet.hurrican.acceptance.builder.StandortDataBuilder;
import de.mnet.hurrican.ffm.citrus.VariableNames;

/**
 *
 */
public class PrepareCustomerDataTestAction extends PrepareOltStandortTestAction {

    private final OltChildTestBuilder oltChildTestBuilder;
    private final RangierungTestBuilder rangierungTestBuilder;

    public PrepareCustomerDataTestAction(String hwRackType, StandortDataBuilder standortDataBuilder,
            OltChildTestBuilder oltChildTestBuilder, RangierungTestBuilder rangierungTestBuilder) {
        super(standortDataBuilder, null, hwRackType);
        setName("prepareCustomerStandort");
        this.oltChildTestBuilder = oltChildTestBuilder;
        this.rangierungTestBuilder = rangierungTestBuilder;
    }

    @Override
    public void doExecute(TestContext testContext) {
        try {
            standortDataBuilder.reset();
            StandortDataBuilder.StandortData standortData = createStandortData(testContext);
            HVTStandort oltChildStandort = standortData.oltChildStandort;
            final HWRack rack;
            switch (hwRackType) {
                case HWRack.RACK_TYPE_MDU:
                    rack = oltChildTestBuilder.buildMdu(oltChildStandort);
                    break;
                case HWRack.RACK_TYPE_ONT:
                    rack = oltChildTestBuilder.buildOnt(oltChildStandort);
                    break;
                case HWRack.RACK_TYPE_DPO:
                    rack = oltChildTestBuilder.buildOnt(oltChildStandort);
                    break;
                default:
                    throw new RuntimeException(String.format("Unsupported HwRackType '%s'", hwRackType));
            }
            Equipment equipment = rangierungTestBuilder.buildRangierung(oltChildStandort, rack);
            testContext.setVariable(VariableNames.HW_EQN, equipment.getHwEQN());
            testContext.setVariable(VariableNames.OLT_CHILD_ID, rack.getGeraeteBez());
        }
        catch (Exception e) {
            throw new CitrusRuntimeException(e.getMessage(), e);
        }
    }

}
