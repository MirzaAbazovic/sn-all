package de.augustakom.hurrican.model.cc.ffm;

import static de.augustakom.common.BaseTest.*;
import static org.testng.Assert.*;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

@Test(groups = UNIT)
public class FfmProductMappingTest extends BaseTest {

    @DataProvider
    private Object[][] isEqualToOrPartOfDataProvider() {
        return new Object[][] {
                {createMapping(540L, 11000L), createMapping(540L, 11000L), true},
                {createMapping(540L, 11000L), createMapping(541L, 11000L), false},
                {createMapping(540L, 11000L), createMapping(null, 11000L), true},
                {createMapping(null, 11000L), createMapping(null, 11000L), true},
                {createMapping(540L, 11000L), createMapping(540L, null), true},
                {createMapping(540L, 11000L), createMapping(null, null), true},
                {createMapping(null, 11000L), createMapping(540L, 11001L), false},
                {createMapping(540L, null)  , createMapping(540L, 11001L), false},
                {createMapping(null, null)  , createMapping(540L, 11001L), false},
        };
    }

    @Test(dataProvider = "isEqualToOrPartOfDataProvider")
    public void isEqualToOrPartOf(FfmProductMapping mapping1, FfmProductMapping mapping2, boolean expected) {
        assertEquals(mapping1.isEqualToOrPartOf(mapping2), expected);
    }

    @DataProvider
    private Object[][] isSubsetOfDataProvider() {
        return new Object[][] {
                {createMapping(540L, 11000L), createMapping(540L, 11000L), false},
                {createMapping(540L, 11000L), createMapping(541L, 11000L), false},
                {createMapping(540L, 11000L), createMapping(null, 11000L), true},
                {createMapping(null, 11000L), createMapping(null, 11000L), false},
                {createMapping(540L, 11000L), createMapping(540L, null), true},
                {createMapping(540L, 11000L), createMapping(null, null), true},
                {createMapping(null, 11000L), createMapping(540L, 11001L), false},
                {createMapping(540L, null)  , createMapping(540L, 11001L), false},
                {createMapping(null, null)  , createMapping(540L, 11001L), false},
        };
    }

    @Test(dataProvider = "isSubsetOfDataProvider")
    public void isSubsetOf(FfmProductMapping mapping1, FfmProductMapping mapping2, boolean expected) {
        assertEquals(mapping1.isSubsetOf(mapping2), expected);
    }

    private FfmProductMapping createMapping(Long produktId, Long standortTypRefId) {
        return new FfmProductMappingBuilder()
                .withProduktId(produktId)
                .withStandortTypRefId(standortTypRefId)
                .setPersist(false)
                .build();
    }

}
