package de.mnet.hurrican.atlas.simulator.wita;

import java.text.*;
import java.util.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class WitaLineOrderSimulatorHelperTest {

    @Test
    public void testCalculateDeliveryDate() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Calendar calendar = Calendar.getInstance();
        calendar.setWeekDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.WEEK_OF_YEAR), Calendar.SUNDAY);

        String deliveryDate = WitaLineOrderSimulatorHelper.calculateDeliveryDate(dateFormat.format(calendar.getTime()));

        Calendar delivery = Calendar.getInstance();
        delivery.setTime(dateFormat.parse(deliveryDate));
        Assert.assertEquals(delivery.get(Calendar.DAY_OF_WEEK), Calendar.MONDAY);

        calendar.setWeekDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.WEEK_OF_YEAR), Calendar.SATURDAY);
        deliveryDate = WitaLineOrderSimulatorHelper.calculateDeliveryDate(dateFormat.format(calendar.getTime()));

        delivery.setTime(dateFormat.parse(deliveryDate));
        Assert.assertEquals(delivery.get(Calendar.DAY_OF_WEEK), Calendar.MONDAY);

        calendar.setWeekDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.WEEK_OF_YEAR), Calendar.TUESDAY);
        deliveryDate = WitaLineOrderSimulatorHelper.calculateDeliveryDate(dateFormat.format(calendar.getTime()));

        delivery.setTime(dateFormat.parse(deliveryDate));
        Assert.assertEquals(delivery.get(Calendar.DAY_OF_WEEK), Calendar.TUESDAY);
    }

}
