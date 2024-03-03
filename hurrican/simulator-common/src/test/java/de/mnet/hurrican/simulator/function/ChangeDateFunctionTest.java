package de.mnet.hurrican.simulator.function;

import java.util.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

@Test(groups = BaseTest.UNIT)
public class ChangeDateFunctionTest {

    private ChangeDateFunction testling = new ChangeDateFunction();

    @Test
    public void testExecute() throws Exception {
        List<String> parameterList = new ArrayList<>();
        parameterList.add("yyyy-MM-dd");
        parameterList.add("2013-01-01");
        parameterList.add("+5d");

        String date = testling.execute(parameterList, null);
        Assert.assertEquals(date, "2013-01-06");
    }
}
