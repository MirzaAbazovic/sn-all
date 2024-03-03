package de.augustakom.hurrican.model.cc.fttx;

import static de.augustakom.common.BaseTest.*;

import junit.framework.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.HVTTechnik;
import de.augustakom.hurrican.model.cc.HVTTechnikBuilder;
import de.augustakom.hurrican.model.cc.ProduktBuilder;

@Test(groups = UNIT)
public class Produkt2CvlanTest extends BaseTest {
    ProduktBuilder prodBuilder = new ProduktBuilder().withId(Long.valueOf(501));
    HVTTechnikBuilder huaweiBuilder = new HVTTechnikBuilder().withId(HVTTechnik.HUAWEI);
    HVTTechnikBuilder alcatelBuilder = new HVTTechnikBuilder().withId(HVTTechnik.ALCATEL);
    Produkt2Cvlan hsiHuawei = new Produkt2CvlanBuilder().withProduktBuilder(prodBuilder)
            .withHvtTechnikBuilder(huaweiBuilder).setPersist(false).build();
    Produkt2Cvlan hsi = new Produkt2CvlanBuilder().withProduktBuilder(prodBuilder).setPersist(false).build();
    Produkt2Cvlan hsiAlcatel = new Produkt2CvlanBuilder().withProduktBuilder(prodBuilder)
            .withHvtTechnikBuilder(alcatelBuilder).setPersist(false).build();
    Produkt2Cvlan hsiFttb = new Produkt2CvlanBuilder().withProduktBuilder(prodBuilder)
            .withTechLocationTypeRefId(HVTStandort.HVT_STANDORT_TYP_FTTB).setPersist(false).build();
    Produkt2Cvlan hsiFttbAlcatel = new Produkt2CvlanBuilder().withProduktBuilder(prodBuilder)
            .withTechLocationTypeRefId(HVTStandort.HVT_STANDORT_TYP_FTTB).withHvtTechnikBuilder(alcatelBuilder)
            .setPersist(false).build();

    @DataProvider
    public Object[][] product2CvlanData() {
        return new Object[][] {
                { hsi, hsiHuawei, Boolean.TRUE },
                { hsiHuawei, hsi, Boolean.FALSE },
                { hsiHuawei, hsiAlcatel, Boolean.FALSE },
                { hsi, hsiFttb, Boolean.TRUE },
                { hsi, hsiFttbAlcatel, Boolean.TRUE },
                { hsiFttb, hsiFttbAlcatel, Boolean.TRUE },
        };
    }

    @Test(dataProvider = "product2CvlanData")
    public void isSubsetOf(Produkt2Cvlan referenz, Produkt2Cvlan prod2Cvlan, Boolean expectedResult) {
        Assert.assertEquals(expectedResult.booleanValue(), prod2Cvlan.isSubsetOf(referenz));
    }

    @Test(expectedExceptions = { IllegalStateException.class })
    public void isSubsetOfWrongConfig() {
        hsiHuawei.isSubsetOf(hsiFttb);
    }

    @Test(expectedExceptions = { IllegalStateException.class })
    public void isSubsetOfWrongConfig2() {
        hsiFttb.isSubsetOf(hsiHuawei);
    }
}
