/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.11.13
 */
package de.augustakom.hurrican.gui.tools.wbci.tables;

import static de.augustakom.hurrican.gui.tools.wbci.tables.DecisionTableModel.*;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wbci.model.DecisionAttribute;
import de.mnet.wbci.model.DecisionResult;
import de.mnet.wbci.model.DecisionVO;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.builder.DecisionVOBuilder;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class DecisionTableModelTest {

    @DataProvider(name = "enumValues")
    public Object[][] enumValues() {
        return new Object[][] {
                { 0, DecisionTableMetaData.ATTRIBUTE },
                { 1, DecisionTableMetaData.CONTROL_VALUE },
                { 2, DecisionTableMetaData.PROPERTY_VALUE },
                { 3, DecisionTableMetaData.SUGGESTED_RESULT },
                { 4, DecisionTableMetaData.FINAL_RESULT },
                { 5, DecisionTableMetaData.SUGGESTED_CODE },
                { 6, DecisionTableMetaData.FINAL_CODE },
                { 7, DecisionTableMetaData.COUNT_COL }
        };
    }

    @Test(dataProvider = "enumValues")
    public void testEnumForColumn(int columnNo, DecisionTableMetaData expected) throws Exception {
        Assert.assertEquals(DecisionTableMetaData.forColumn(columnNo), expected);

    }

    @DataProvider(name = "decisionVOs")
    public Object[][] decisionVOs() {
        return new Object[][] {
                { DecisionAttribute.VORNAME, "Max", "Moritz", DecisionResult.OK, DecisionResult.NICHT_OK,
                        MeldungsCode.ZWA, MeldungsCode.AIFVN, false, true },
                { DecisionAttribute.VORNAME, "Max", "Moritz", DecisionResult.OK, DecisionResult.NICHT_OK,
                        MeldungsCode.ZWA, MeldungsCode.ZWA, false, false },
                { DecisionAttribute.VORNAME_WAI, "Max", "Moritz", DecisionResult.MANUELL, DecisionResult.MANUELL,
                        null, null, false, false },
                { DecisionAttribute.VORNAME_WAI, "Max", "Moritz", DecisionResult.MANUELL, DecisionResult.OK,
                        MeldungsCode.WAI, MeldungsCode.ZWA, true, false },
                { DecisionAttribute.KUNDENWUNSCHTERMIN, "27.12.1980", "01.12.1980", DecisionResult.OK, DecisionResult.NICHT_OK,
                        MeldungsCode.ZWA, MeldungsCode.NAT, true, true },
                { DecisionAttribute.KUNDENWUNSCHTERMIN, "27.12.1980", "01.12.1980", DecisionResult.NICHT_OK, DecisionResult.OK,
                        MeldungsCode.NAT, MeldungsCode.ZWA, true, true },
                { DecisionAttribute.KUNDENWUNSCHTERMIN, "27.12.1980", "27.12.1980", DecisionResult.OK, DecisionResult.OK,
                        MeldungsCode.ZWA, MeldungsCode.ZWA, true, false }
        };
    }

    @Test(dataProvider = "decisionVOs")
    public void testDecisionTableModelHelper(DecisionAttribute attribute, String properyValue, String controlValue,
            DecisionResult suggestedResult, DecisionResult finalResult, MeldungsCode suggestedMC, MeldungsCode finalMC,
            boolean showSuggestedCode, boolean showFinalCode) throws Exception {
        DecisionTableModel testling = new DecisionTableModel(GeschaeftsfallTyp.VA_KUE_MRN);

        DecisionVO vo = new DecisionVOBuilder(attribute)
                .withPropertyValue(properyValue)
                .withControlValue(controlValue)
                .withSuggestedResult(suggestedResult)
                .withFinalResult(finalResult)
                .withSuggestedMeldungsCode(suggestedMC)
                .withFinalMeldungsCode(finalMC)
                .build();

        Assert.assertEquals(testling.getValueForColumn(0, vo), attribute.getDisplayName());
        Assert.assertEquals(testling.getValueForColumn(1, vo), controlValue);
        Assert.assertEquals(testling.getValueForColumn(2, vo), properyValue);
        Assert.assertEquals(testling.getValueForColumn(3, vo), suggestedResult.name());
        Assert.assertEquals(testling.getValueForColumn(4, vo), finalResult.name());

        if (showSuggestedCode) {
            Assert.assertEquals(testling.getValueForColumn(5, vo), suggestedMC.name());
        }
        else {
            Assert.assertEquals(testling.getValueForColumn(5, vo), "");
        }

        if (showFinalCode) {
            Assert.assertEquals(testling.getValueForColumn(6, vo), finalMC.name());
        }
        else {
            Assert.assertEquals(testling.getValueForColumn(6, vo), "");
        }

        Assert.assertEquals(testling.getValueForColumn(7, vo), null);

    }
}
