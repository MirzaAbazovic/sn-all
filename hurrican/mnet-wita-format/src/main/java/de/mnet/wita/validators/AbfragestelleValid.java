/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.07.2011 13:16:24
 */
package de.mnet.wita.validators;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;
import javax.validation.*;
import org.apache.commons.lang.StringUtils;

import de.mnet.wita.validators.AbfragestelleValid.AbfragestelleValidator;


@Retention(RUNTIME)
@Target({ METHOD, FIELD })
@Constraint(validatedBy = AbfragestelleValidator.class)
@Documented
public @interface AbfragestelleValid {

    String message() default "Abfragestelle muss aus 1-6 Ziffern bestehen.";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public static class AbfragestelleValidator implements
            ConstraintValidator<AbfragestelleValid, String> {

        @Override
        public boolean isValid(String abfragestelle, ConstraintValidatorContext context) {
            if (abfragestelle == null) {
                return true;
            }

            // @formatter:off
            return StringUtils.isNumeric(abfragestelle)
                && (StringUtils.length(abfragestelle) >= 1)
                && (StringUtils.length(abfragestelle) <= 6);
            // @formatter:on
        }

        @Override
        public void initialize(AbfragestelleValid constraintAnnotation) {
            // nothing to do
        }
    }

}
