package de.mnet.hurrican.atlas.simulator.wita;

import java.text.*;
import java.util.*;
import org.apache.log4j.Logger;

/**
 * Helper provides various service methods for building simulation logic, such as calculating date values or generating
 * matching values.
 */
public final class WitaLineOrderSimulatorHelper {

    private static final Logger LOGGER = Logger.getLogger(WitaLineOrderSimulatorHelper.class);

    /**
     * Prevent instantiation
     */
    private WitaLineOrderSimulatorHelper() {
    }

    /**
     * Calculates new delivery date based on given date value that was extracted from initial request message.
     * <p/>
     * New delivery date should not be on weekend so dynamically choose next monday in case of conflict.
     *
     * @param dateValue
     * @return
     */
    public static String calculateDeliveryDate(String dateValue) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar deliveryDate = Calendar.getInstance();

        try {
            deliveryDate.setTime(dateFormat.parse(dateValue));
        }
        catch (ParseException e) {
            LOGGER.error(e.getMessage(), e);
        }

        if (deliveryDate.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            // add two days so we have MONDAY instead of SATURDAY
            deliveryDate.add(Calendar.DAY_OF_WEEK, 2);
        }
        else if (deliveryDate.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            // add one day so we have MONDAY instead of SUNDAY
            deliveryDate.add(Calendar.DAY_OF_WEEK, 1);
        }

        return dateFormat.format(deliveryDate.getTime());
    }
}
