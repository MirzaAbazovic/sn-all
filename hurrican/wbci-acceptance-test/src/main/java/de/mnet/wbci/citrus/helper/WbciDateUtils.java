/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.09.13
 */
package de.mnet.wbci.citrus.helper;

import java.time.*;
import java.time.format.*;

import de.augustakom.common.tools.lang.DateTools;

public class WbciDateUtils {

    public static String formatToWbciDate(LocalDateTime toFormat) {
        return toFormat.format(DateTimeFormatter.ofPattern(DateTools.PATTERN_YEAR_MONTH_DAY));
    }

}
