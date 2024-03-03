/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.10.13
 */
package de.mnet.wbci.validation.constraints;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;
import java.time.*;
import javax.validation.*;

import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.mnet.wbci.validation.helper.ValidationHelper;

@Target({ METHOD, FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = CheckKundenwunschterminNotInRange.KundenwunschterminValidator.class)
@Documented
public @interface CheckKundenwunschterminNotInRange {

    String message() default "liegt zwischen %s und %s Arbeitstage in der Zukunft";

    int from();

    int to();

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public static class KundenwunschterminValidator implements
            ConstraintValidator<CheckKundenwunschterminNotInRange, LocalDate> {

        protected int rangeFrom;
        protected int rangeTo;
        protected String defaultMessage;

        @Override
        public void initialize(CheckKundenwunschterminNotInRange constraintAnnotation) {
            this.rangeFrom = constraintAnnotation.from();
            this.rangeTo = constraintAnnotation.to();
            this.defaultMessage = constraintAnnotation.message();
        }

        @Override
        public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
            if (value == null || dateIsNotInRange(value)) {
                return true;
            }
            ValidationHelper.addConstraintViolation(context, String.format(defaultMessage, rangeFrom, rangeTo));
            return false;
        }

        private boolean dateIsNotInRange(LocalDate value) {
            int workingDaysBetween = DateCalculationHelper.getDaysBetween(
                    DateCalculationHelper.getDateInWorkingDaysFromNow(0).toLocalDate(),
                    value, DateCalculationHelper.DateCalculationMode.WORKINGDAYS);
            return (workingDaysBetween < rangeFrom || workingDaysBetween > rangeTo);
        }
    }

}
