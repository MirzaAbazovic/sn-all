/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.10.13
 */
package de.mnet.wbci.validation.constraints;

import static de.augustakom.common.service.holiday.DateCalculationHelper.DateCalculationMode.*;
import static de.augustakom.common.service.holiday.DateCalculationHelper.*;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;
import java.time.*;
import javax.validation.*;

import de.mnet.wbci.validation.helper.ValidationHelper;

/**
 * Prueft den Kundenwunschtermin auf
 * <p/>
 * <ul> <li>der KWT ein Arbeitstag ist</li> <li>mindestens X Tage in der Zukunft</li> </ul> Ob der Tag nach dem KWT ein
 * Feiertag ist, spielt keine Rolle und wird nicht geprüft.
 */
@Target({ METHOD, FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = CheckKundenwunschterminIgnoringNextDay.KundenwunschterminValidator.class)
@Documented
public @interface CheckKundenwunschterminIgnoringNextDay {

    String message() default "Der angegebene Kundenwunschtermin ist nicht gültig. Bitte beachten Sie folgende Kriterien:%n"
            +
            "  + %s Tag(e) Vorlaufzeit%n" +
            "  + Vorgabedatum nicht am Wochenende";

    int minimumLeadTime();

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public class KundenwunschterminValidator implements ConstraintValidator<CheckKundenwunschterminIgnoringNextDay, LocalDate> {

        protected int minimumLeadTime;
        protected String defaultMessage;

        @Override
        public void initialize(CheckKundenwunschterminIgnoringNextDay constraintAnnotation) {
            this.minimumLeadTime = constraintAnnotation.minimumLeadTime();
            this.defaultMessage = constraintAnnotation.message();
        }

        @Override
        public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
            if (value == null) {
                return true;
            }

            final LocalDate valueAsLocalDate = value;
            if (isWorkingDay(valueAsLocalDate) && getDaysBetween(getDateInWorkingDaysFromNow(0).toLocalDate(), valueAsLocalDate, WORKINGDAYS) >= minimumLeadTime) {
                return true;
            }
            ValidationHelper.addConstraintViolation(context, String.format(defaultMessage, minimumLeadTime));
            return false;
        }

    }

}
