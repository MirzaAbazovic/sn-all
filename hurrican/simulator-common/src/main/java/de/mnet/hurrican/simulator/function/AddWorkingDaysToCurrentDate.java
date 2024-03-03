/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.12.13
 */
package de.mnet.hurrican.simulator.function;

import java.text.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.functions.Function;
import org.springframework.util.CollectionUtils;

import de.augustakom.common.service.holiday.DateCalculationHelper;

/**
 * Citrus-Funktion zum Hinzufügen von Werktagen zum heutigen Datum. <br><br> Moegliche Parameter: <ul> <li>Index 0:
 * DatePattern (z.B. yyyy-MM-dd)</li> <li>Index 1: Anzahl der Werktage, die zu dem heutigen Datum hinzugefügt werden
 * sollen.</li> </ul>
 *
 *
 */
public class AddWorkingDaysToCurrentDate implements Function {

    @Override
    public String execute(List<String> parameterList, TestContext context) {
        if (CollectionUtils.isEmpty(parameterList) || parameterList.size() != 2) {
            throw new IllegalArgumentException(String.format("Invalid number of parameters! Usage:" +
                    "currentDatePlusWorkingDays(datePattern, numberOfWorkingDays)."));
        }
        String pattern = parameterList.get(0);
        int workingDays = Integer.parseInt(parameterList.get(1));
        final LocalDate dateInWorkingDaysFromNow = DateCalculationHelper.getDateInWorkingDaysFromNow(workingDays).toLocalDate();
        return DateTimeFormatter.ofPattern(pattern).format(dateInWorkingDaysFromNow);
    }

}
