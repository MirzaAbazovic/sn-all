package de.augustakom.hurrican.model.cc;

import static org.testng.Assert.*;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

@Test(groups = BaseTest.UNIT)
public class EquipmentTest extends BaseTest{

    @DataProvider
    private Object[][] hwEqnDataProvider() {
        return new Object[][] {
                {"1-0", Equipment.HWEQNPART_FTTX_ONT_PORT_SS, "1"},
                {"2-0", Equipment.HWEQNPART_FTTX_ONT_PORT_SS, "2"},
        };
    }

    @Test(dataProvider = "hwEqnDataProvider")
    public void getHwEQNPart(String hwEqn, int requestHwEQNPart, String expectedHwEqnPart) {
        Equipment equipment = new Equipment();
        equipment.setHwEQN(hwEqn);
        assertEquals(equipment.getHwEQNPart(requestHwEQNPart), expectedHwEqnPart);
    }

}
