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

import de.mnet.wita.validators.RufnummerValid.RufnummerValidator;


@Retention(RUNTIME)
@Target({ METHOD, FIELD })
@Constraint(validatedBy = RufnummerValidator.class)
@Documented
public @interface RufnummerValid {

    String message() default "Rufnummer muss aus 1-14 Ziffern bestehen.";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public static class RufnummerValidator implements
            ConstraintValidator<RufnummerValid, String> {

        @Override
        public boolean isValid(String rufnummer, ConstraintValidatorContext context) {
            if (rufnummer == null) {
                return true;
            }

            // @formatter:off
            return StringUtils.isNumeric(rufnummer)
                && (StringUtils.length(rufnummer) >= 1)
                && (StringUtils.length(rufnummer) <= 14);
            // @formatter:on
        }

        @Override
        public void initialize(RufnummerValid constraintAnnotation) {
            // nothing to do
        }
    }

}
