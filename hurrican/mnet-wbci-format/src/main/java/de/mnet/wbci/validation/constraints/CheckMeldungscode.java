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
import javax.validation.*;

import de.mnet.wbci.model.MeldungPosition;
import de.mnet.wbci.validation.helper.ValidationHelper;

/**
 * Checks if the added {@link de.mnet.wbci.model.MeldungsCode} is valid for the {@link MeldungPosition}.
 */
@Target(value = TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = CheckMeldungscode.MeldungsCodeValidator.class)
@Documented
public @interface CheckMeldungscode {

    String message() default "Der angegebene Meldungscode %s (%s), ist nicht zulässig für den Meldungstyp %s";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public class MeldungsCodeValidator implements ConstraintValidator<CheckMeldungscode, MeldungPosition> {
        protected String defaultMsg;

        @Override
        public void initialize(CheckMeldungscode constraintAnnotation) {
            defaultMsg = constraintAnnotation.message();
        }

        @Override
        public boolean isValid(MeldungPosition value, ConstraintValidatorContext context) {
            if (!value.getMeldungsCode().isValidForMeldungPosTyps(value.getPositionTyp())) {
                ValidationHelper.addConstraintViolation(context,
                        String.format(
                                defaultMsg,
                                value.getMeldungsCode().name(),
                                value.getMeldungsCode().getStandardText(),
                                value.getPositionTyp().getValue())
                );
                return false;
            }
            return true;
        }
    }

}
