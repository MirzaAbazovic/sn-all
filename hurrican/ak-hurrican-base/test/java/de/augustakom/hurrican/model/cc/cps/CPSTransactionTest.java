package de.augustakom.hurrican.model.cc.cps;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class CPSTransactionTest {

    @DataProvider
    private Object[][] getReadableServiceOrderTypeDP() {
        Long invalidServiceOrderType = -1L;
        return new Object[][] {
                {CPSTransaction.SERVICE_ORDER_TYPE_CREATE_SUB,"Create-Subscriber"},
                {invalidServiceOrderType,""}
        };
    }

    @Test(dataProvider = "getReadableServiceOrderTypeDP")
    public void testGetReadableServiceOrderType(Long serviceOrderType, String expectedResult) throws Exception {
        Assert.assertEquals(CPSTransaction.getReadableServiceOrderType(serviceOrderType), expectedResult);
    }
}