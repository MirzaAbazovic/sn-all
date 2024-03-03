/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.04.2015
 */
package de.mnet.hurrican.simulator.function;

import java.text.*;
import java.time.*;
import java.util.*;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.functions.core.AbstractDateFunction;
import org.springframework.util.CollectionUtils;

import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.mnet.common.tools.DateConverterUtils;

/**
 * Citrus function to add working days to the supplied date. If the date calculated does not lie <b>directly</b> before
 * a holiday, then this date is returned. Otherwise the next valid working day, where the following day is not
 * a holiday, is calculated and returned.
 */
public class AddWorkingDaysAndNextDayIsWorkingDayToDate extends AbstractDateFunction {

    @Override
    public String execute(List<String> parameterList, TestContext context) {
        if (CollectionUtils.isEmpty(parameterList) || parameterList.size() != 3) {
            throw new IllegalArgumentException(String.format("Invalid number of parameters! Usage:" +
                    "datePlusWorkingDaysAndNextDayIsWorkingDay(datePattern, days/months/years, workingDays)."));
        }
        final String pattern = parameterList.get(0);
        final SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        final Date date;
        try {
            date = sdf.parse(parameterList.get(1));
        }
        catch (ParseException e) {
            throw new IllegalArgumentException(String.format("Invalid date parameter! The provided date should have " +
                    "following format: '%s'", pattern));
        }

        int workingDays = Integer.parseInt(parameterList.get(2));
        LocalDate dateInWorkingDaysAndNextDayNotHoliday = DateCalculationHelper.getDateInWorkingDaysAndNextDayNotHoliday(DateConverterUtils.asLocalDate(date), workingDays);
        return sdf.format(DateConverterUtils.asDate(dateInWorkingDaysAndNextDayNotHoliday));
    }
}
