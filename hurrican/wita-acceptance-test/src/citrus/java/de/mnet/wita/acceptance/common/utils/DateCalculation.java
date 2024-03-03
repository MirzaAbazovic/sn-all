/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.08.2016
 */
package de.mnet.wita.acceptance.common.utils;

import java.text.*;
import java.util.*;
import org.apache.log4j.Logger;

public class DateCalculation {
    private static final Logger LOGGER = Logger.getLogger(DateCalculation.class);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * Calculates new delivery date based on given date value that was extracted from initial request message.
     * <p/>
     * New delivery date should not be on weekend so dynamically choose next monday in case of conflict.
     */
    public static String calculateDeliveryDate(String dateValue) {
        Calendar deliveryDate = Calendar.getInstance();

        try {
            deliveryDate.setTime(DATE_FORMAT.parse(dateValue));
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

        return DATE_FORMAT.format(deliveryDate.getTime());
    }

}

