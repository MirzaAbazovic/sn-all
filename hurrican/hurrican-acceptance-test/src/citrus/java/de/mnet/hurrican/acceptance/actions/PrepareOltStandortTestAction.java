/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.11.2014
 */
package de.mnet.hurrican.acceptance.actions;

import com.consol.citrus.actions.AbstractTestAction;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;

import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.mnet.hurrican.acceptance.builder.StandortDataBuilder;
import de.mnet.hurrican.ffm.citrus.VariableNames;

/**
 * Created by maherma on 26.11.2014.
 */
public class PrepareOltStandortTestAction extends AbstractTestAction {

    protected final StandortDataBuilder standortDataBuilder;
    protected final Long hvtStandortTyp;
    protected final String hwRackType;

    public PrepareOltStandortTestAction(StandortDataBuilder standortDataBuilder, Long hvtStandortTyp, String hwRackType) {
        setName("prepareOltStandort");
        this.standortDataBuilder = standortDataBuilder;
        this.hvtStandortTyp = hvtStandortTyp != null ? hvtStandortTyp : HVTStandort.HVT_STANDORT_TYP_FTTH;
        this.hwRackType = hwRackType != null ? hwRackType : HWRack.RACK_TYPE_ONT;
    }

    @Override
    public void doExecute(TestContext testContext) {
        try {
            standortDataBuilder.reset(); // TODO MM prevent caching of standortdata
            createStandortData(testContext);
        }
        catch (Exception e) {
            throw new CitrusRuntimeException(e.getMessage(), e);
        }
    }

    public StandortDataBuilder.StandortData createStandortData(TestContext testContext) throws Exception {
        StandortDataBuilder.StandortData standortData = standortDataBuilder
                .withStandortTypeRefId(hvtStandortTyp)
                .withOltChildRackType(hwRackType)
                .getStandortData();
        testContext.setVariable(VariableNames.OLT_BEZEICHNUNG, standortData.hwOlt.getGeraeteBez());
        testContext.setVariable(VariableNames.OLT_CHILD_ORTSTEIL, standortData.oltChildGruppe.getOrtsteil());
        testContext.setVariable(VariableNames.OLT_ORTSTEIL, standortData.oltChildGruppe.getOrtsteil());
        testContext.setVariable(VariableNames.GEO_ID, standortData.geoId.getId());
        testContext.setVariable(VariableNames.ORTSTEIL_BETREIBSRAUM, standortData.betriebsraumGruppe.getOrtsteil());
        testContext.setVariable(VariableNames.ORTSTEIL_FUNKTIONSRAUM, standortData.funktionsraumGruppe.getOrtsteil());
        testContext.setVariable(VariableNames.ASB, standortData.oltChildStandort.getAsb());
        testContext.setVariable(VariableNames.HVT_STANDORT_ID, standortData.oltChildStandort.getId());
        return standortData;
    }

}
