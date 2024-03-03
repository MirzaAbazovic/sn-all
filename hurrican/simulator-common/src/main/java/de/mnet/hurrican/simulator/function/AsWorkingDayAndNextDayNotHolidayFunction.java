/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.01.14
 */
package de.mnet.hurrican.simulator.function;

import java.text.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.functions.core.AbstractDateFunction;
import org.springframework.util.CollectionUtils;

import de.augustakom.common.service.holiday.DateCalculationHelper;

/**
 * Function which checks that the provided date is a working day and the following day is not a holiday and returns the
 * provided date as a formatted string value. If the check fails then the next valid date is returned. The function also
 * supports additional date offset in order to manipulate result date value.
 *
 *
 */
public class AsWorkingDayAndNextDayNotHolidayFunction extends AbstractDateFunction {

    @Override
    public String execute(List<String> parameterList, TestContext context) {
        if (CollectionUtils.isEmpty(parameterList) || (parameterList.size() != 2 && parameterList.size() != 3)) {
            throw new IllegalArgumentException(String.format("Invalid number of parameters! Usage:" +
                    "asWorkingDayAndNextDayNotHoliday(datePattern, days/months/years, (offset))."));
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
        if (parameterList.size() > 2) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            applyDateOffset(calendar, parameterList.get(2));
            return DateCalculationHelper.asWorkingDayAndNextDayNotHoliday(calendar.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()).format(DateTimeFormatter.ofPattern(pattern));
        }
        else {
            return DateCalculationHelper.asWorkingDayAndNextDayNotHoliday(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()).format(DateTimeFormatter.ofPattern(pattern));
        }
    }

}
