/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.07.2011 09:36:34
 */
package de.mnet.wita.validators;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;
import javax.validation.*;
import org.apache.commons.lang.StringUtils;

import de.mnet.wita.validators.OnkzValid.OnkzValidator;

@Retention(RUNTIME)
@Target({ METHOD, FIELD })
@Constraint(validatedBy = OnkzValidator.class)
@Documented
public @interface OnkzValid {

    String message() default "Ortskennzahl muss aus 2-5 Ziffern bestehen (ohne fuehrende 0).";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public static class OnkzValidator implements
            ConstraintValidator<OnkzValid, String> {

        @Override
        public boolean isValid(String onkz, ConstraintValidatorContext context) {
            if (onkz == null) {
                return true;
            }

            // @formatter:off
            return StringUtils.isNumeric(onkz)
                && (StringUtils.length(onkz) >= 2)
                && (StringUtils.length(onkz) <= 5)
                && !StringUtils.startsWith(onkz, "0");
            // @formatter:on
        }

        @Override
        public void initialize(OnkzValid constraintAnnotation) {
            // nothing to do
        }
    }

}
