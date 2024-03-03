/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.04.2014
 */
package de.mnet.wbci.helper;

import static de.augustakom.common.service.holiday.DateCalculationHelper.*;

import java.time.*;
import java.util.*;

import de.mnet.common.tools.DateConverterUtils;

/**
 *
 */
public class AnswerDeadlineCalculationHelper {

    /**
     * Calculates the days until deadline for Mnet
     *
     * @param answerDeadline the deadline date
     * @param isMnetDeadline {@code true} if it is a deadline for mnet, {@code false} otherwise
     * @return
     */
    public static Integer calculateDaysUntilDeadlineMnet(Date answerDeadline, Boolean isMnetDeadline) {
        if ((isMnetDeadline == null || !isMnetDeadline) || answerDeadline == null) {
            return null;
        }
        final LocalDate deadline = DateConverterUtils.asLocalDate(answerDeadline);
        return calculateDaysUntilDeadline(deadline);
    }

    /**
     * Calculates the days until deadline for the partner carrier
     *
     * @param answerDeadline the deadline date
     * @param isMnetDeadline {@code true} if it is a deadline for mnet, {@code false} otherwise
     * @return
     */
    public static Integer calculateDaysUntilDeadlinePartner(Date answerDeadline, Boolean isMnetDeadline) {
        if ((isMnetDeadline != null && isMnetDeadline) || answerDeadline == null) {
            return null;
        }
        final LocalDate deadline = DateConverterUtils.asLocalDate(answerDeadline);
        return calculateDaysUntilDeadline(deadline);
    }

    private static Integer calculateDaysUntilDeadline(LocalDate answerDeadline) {
        return getDifferenceInDays(
                getDateInWorkingDaysFromNow(0).toLocalDate(), //ensures that the base calc day is also a working day
                answerDeadline, DateCalculationMode.WORKINGDAYS);
    }

}
